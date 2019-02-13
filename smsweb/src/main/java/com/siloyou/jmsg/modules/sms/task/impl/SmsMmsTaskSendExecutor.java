package com.siloyou.jmsg.modules.sms.task.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.Map;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;
import com.google.common.util.concurrent.RateLimiter;
import com.sanerzone.common.support.utils.DateUtils;
import com.siloyou.core.common.config.Global;
import com.siloyou.core.common.utils.FstObjectSerializeUtil;
import com.siloyou.core.common.utils.JedisClusterUtils;
import com.siloyou.core.common.utils.SpringContextHolder;
import com.siloyou.core.common.utils.StringUtils;
import com.siloyou.core.modules.sys.entity.User;
import com.siloyou.core.modules.sys.utils.UserUtils;
import com.siloyou.jmsg.common.message.SmsMtMessage;
import com.siloyou.jmsg.common.message.SmsSrMessage;
import com.siloyou.jmsg.common.utils.CacheKeys;
import com.siloyou.jmsg.common.utils.MQUtils;
import com.siloyou.jmsg.common.utils.SignUtils;
import com.siloyou.jmsg.common.utils.WhitelistUtils;
import com.siloyou.jmsg.modules.sms.dao.JmsgSmsTaskDao;
import com.siloyou.jmsg.modules.sms.entity.JmsgSmsSend;
import com.siloyou.jmsg.modules.sms.entity.JmsgSmsTask;
import com.siloyou.jmsg.modules.sms.service.JmsgSmsTaskService;
import com.siloyou.jmsg.modules.sms.service.SmsSendService;
import com.siloyou.jmsg.modules.sms.task.SmsMmsSendTask;

public class SmsMmsTaskSendExecutor implements Runnable {

	public static Logger logger = LoggerFactory.getLogger(SmsTaskSendExecutor.class);
	private static JmsgSmsTaskDao taskDao = SpringContextHolder.getBean(JmsgSmsTaskDao.class);
	private static JmsgSmsTaskService jmsgSmsTaskService = SpringContextHolder.getBean(JmsgSmsTaskService.class);
    private MQUtils mQUtils = SpringContextHolder.getBean(MQUtils.class);
	protected RateLimiter limiter = RateLimiter.create(20);
	
	private SqlSessionFactory sqlSessionFactory = SpringContextHolder.getBean(SqlSessionFactory.class);
	
	SmsSendService smsSendService = SpringContextHolder.getBean(SmsSendService.class);
	
	private String taskid;
	private int version;
	private int rowNumber;//记录暂停时发送条数
	
	private int index = 0;

	public SmsMmsTaskSendExecutor(String taskid,int version,int rowNumber) {
		this.taskid = taskid;
		this.version = version;
		this.rowNumber = rowNumber;
	}

	@SuppressWarnings("unused")
	@Override
	public void run() {

		long startTime = System.currentTimeMillis();
		logger.info("短信彩信任务开始执行,TASKID{},时间{}" , taskid, DateUtils.getDateTime());
		
		try {
			while(true) {
				
				//更新状态为运行中
				Map<String, String> sMap = Maps.newHashMap();
				sMap.put("taskid", taskid);
				
				Map<String,String> result = taskRunResult(sMap, index);
				if("0".equals(result.get("runFlag")))return;
				
				String userId = result.get("userId");
				String smsContent = result.get("smsContent");
				String sign = SignUtils.get(smsContent);
				User user = UserUtils.get(userId);
				int payCount = jmsgSmsTaskService.findPayCount(smsContent);
				String payType = findPayType(userId);
				String tableName = "jmsg_sms_send_"+DateUtils.getDayOfMonth(0);
				String pushFlag = "9";//待推送
				if(StringUtils.isBlank(user.getCallbackUrl())){
					pushFlag = "0";//无需推送
				}
				String smsType = payCount > 1 ?"2":"1";
				
				

				//获取待发送数据
				File file = new File(Global.getConfig("smsTask.phoneList.dir")+File.separator+taskid+".txt");
				if(file == null)break;
				BufferedReader br = null;
				InputStreamReader reader = null;
				SqlSession sqlSession = this.sqlSessionFactory.openSession(ExecutorType.BATCH,false);
				try {
					reader = new InputStreamReader(new FileInputStream(file));
					br = new BufferedReader(reader);
					for(String line = br.readLine();line !=null;line=br.readLine()){
						limiter.acquire();
						index++;
						if(rowNumber > 0){
							if(rowNumber > index) continue;
							rowNumber = 0;
						}
						
						if(index % 1000 == 0){//执行1000条
							sqlSession.commit();
							Map<String,String> resultMap = taskRunResult(sMap, index);
							if("0".equals(resultMap.get("runFlag")))return;
						}
						
						//发送 TODO
						JmsgSmsSend jmsgSmsSend = jmsgSmsTaskService.createSendDetailNew(user, tableName, taskid, line, smsContent, sign, payCount, payType, pushFlag, smsType, false);
						sqlSession.insert("com.siloyou.jmsg.modules.sms.dao.JmsgSmsSendDao.insert", jmsgSmsSend);
						if(index % 100 == 0) {
							sqlSession.commit();
						}
						
						Map<String, String> map = null;
						String sendStatus = "T000";
						if(sendStatus.equals(jmsgSmsSend.getSendStatus())){
							map = sendHandler(jmsgSmsSend);// 发送短信处理
							sendStatus = map.get("sendStatus");
						}
						
						if(StringUtils.startsWith(sendStatus, "F")){//失败发送SR消息
							sendSmsSR(jmsgSmsSend.getId(),sendStatus);
						}
						logger.info("TASKID:{},提交队列情况:{}",  taskid, map);
						
					}
					sqlSession.commit();
				}catch(Exception e){
					logger.error("短信彩信批量任务发送失败",e);
				}finally{
					if(br != null)br.close();
					if(reader != null)reader.close();
					if(sqlSession != null)sqlSession.close();
				}


				sMap.put("status", "3");// 发送状态为发送完成
				sMap.put("version", "");// 发送状态为发送完成
				taskDao.updateSendStatus(sMap);
				SmsMmsSendTask.smsMmsSendExecMap.remove(taskid);// 任务执行完成
				break;
			}
		} catch (Exception e) {
			logger.error("短信彩信批量任务发送失败",e);
		}

		long endTime = System.currentTimeMillis();
		logger.info("短信彩信任务执行完成,TASKID{},结束时间{},耗时：{}}",taskid, DateUtils.getDateTime(), (endTime - startTime)/1000);
	}
	
	/**
	 * 
	 * @param map
	 * @param index
	 * @return
	 */
	private Map<String,String> taskRunResult(Map<String,String> map,int index){
		
		Map<String,String> resultMap = Maps.newHashMap();
		String runFlag = "1";
		String userId = "";
		String smsContent="";
		JmsgSmsTask task = taskDao.get(taskid);// 获取任务信息
		if(task != null){
		
			userId = task.getCreateBy().getId();
			smsContent = task.getSmsContent();
			
			String sendStatus = task.getStatus();// 任务状态
			
			if ("5".equals(sendStatus)||"9".equals(sendStatus)) {// 任务暂停 或任务停止
				if(index > rowNumber)rowNumber=index;
				map.put("rowNumber", String.valueOf(rowNumber));
				taskDao.updateRowNumber(map);//修改暂停前发送条数
				SmsMmsSendTask.smsMmsSendExecMap.remove(taskid);// 剔除线程
				runFlag = "0";
			}
	
			if ("1".equals(sendStatus) || "8".equals(sendStatus)) {// 发送状态为待发送、继续发送
				map.put("status", "2");// 运行中
				map.put("version",String.valueOf(version));
				if(0 == taskDao.updateSendStatus(map)){
					SmsMmsSendTask.smsMmsSendExecMap.remove(taskid);// 剔除线程
					runFlag = "0";
				}
			}
		}else{
			SmsMmsSendTask.smsMmsSendExecMap.remove(taskid);// 剔除线程
			runFlag="0";
		}
		
		resultMap.put("runFlag", runFlag);
		resultMap.put("userId", userId);
		resultMap.put("smsContent", smsContent);
		
		return resultMap;
	}
	
	//扣费方式
	private String findPayType(String userId){
		return UserUtils.getPayMode(userId, "sms");
	}
	
	//发送SR，修改发送状态
	private void sendSmsSR(String bizid, String sendStatus) throws Exception {
		SmsSrMessage sr = new SmsSrMessage();
		SmsMtMessage message = new SmsMtMessage();
		message.setId(bizid);
		sr.setResult(sendStatus);
		sr.setMessage(message);
		mQUtils.sendSmsSR(bizid, FstObjectSerializeUtil.write((Serializable)sr));
	}

	private Map<String, String> sendHandler(JmsgSmsSend entity) {

		String id = entity.getId();
		String phone = entity.getPhone();

		Map<String, String> map = Maps.newHashMap();
		String sendStatus = "F000";
		String msgid="";
		map.put("id", id);
		
		try {
			if (WhitelistUtils.isExist(phone)) {// 判断是否是白名单号码
				Map<String,String> sendMap=smsSendService.sendSms(entity);
				sendStatus = sendMap.get("sendStatus");
				msgid = sendMap.get("msgid");
			} else {
				String dayKey = CacheKeys.getCacheDaySmsSendKey(phone);
				String monthKey = CacheKeys.getCacheMonthSmsSendKey(phone);
				long dayKeyValue = JedisClusterUtils.incr(dayKey, DateUtils.getEndDayTime());// 一天内发送次数+1
				long monthKeyValue = JedisClusterUtils.incr(monthKey, DateUtils.getEndMonthTime());// 一月内发送次数+1
				if (dayKeyValue > 50) {// 判断号码是否一天内发送5次以上 F3
					sendStatus = "F003";
				} else {
					if (monthKeyValue > 300) {// 判断号码是否一个月内发送30次以上 F4
						sendStatus = "F004";
					} else {
						Map<String,String> sendMap=smsSendService.sendSms(entity);
						sendStatus = sendMap.get("sendStatus");
						msgid = sendMap.get("msgid");
					}
				}
			}
		} catch (Exception e) {
			logger.error("{}", e);
		}
		map.put("sendStatus", sendStatus);
		map.put("msgid", msgid);
		return map;
	}


}
