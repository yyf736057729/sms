package com.siloyou.jmsg.modules.sms.task.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;
import com.google.common.util.concurrent.RateLimiter;
import com.sanerzone.common.modules.account.utils.AccountCacheUtils;
import com.sanerzone.common.support.sequence.MsgId;
import com.sanerzone.common.support.utils.DateUtils;
import com.sanerzone.common.support.utils.JedisClusterUtils;
import com.sanerzone.common.support.utils.SystemClock;
import com.siloyou.core.common.config.Global;
import com.siloyou.core.common.utils.FstObjectSerializeUtil;
import com.siloyou.core.common.utils.SpringContextHolder;
import com.siloyou.core.common.utils.StringUtils;
import com.siloyou.core.modules.sys.utils.UserUtils;
import com.siloyou.jmsg.common.message.SmsMtMessage;
import com.siloyou.jmsg.common.utils.MQUtils;
import com.siloyou.jmsg.common.utils.PhoneUtils;
import com.siloyou.jmsg.modules.sms.dao.JmsgSmsTaskDao;
import com.siloyou.jmsg.modules.sms.entity.JmsgSmsTask;
import com.siloyou.jmsg.modules.sms.task.SmsDot2DotSendTask;

public class SmsTaskSendDotExecutorV2 implements Runnable {

	public static Logger logger = LoggerFactory.getLogger(SmsTaskSendDotExecutorV2.class);
	private static JmsgSmsTaskDao taskDao = SpringContextHolder.getBean(JmsgSmsTaskDao.class);
    private MQUtils mQUtils = SpringContextHolder.getBean(MQUtils.class);
	protected RateLimiter limiter = RateLimiter.create(20);
	
	private String taskid;
	private int version;
	private int rowNumber;//记录暂停时发送条数
	
	private int index = 0;

	public SmsTaskSendDotExecutorV2(String taskid,int version,int rowNumber) {
		this.taskid = taskid;
		this.version = version;
		this.rowNumber = rowNumber;
	}

	@Override
	public void run() {

		long startTime = System.currentTimeMillis();
		logger.info("点对点短信发送任务开始执行,TASKID{},时间{}" , taskid, DateUtils.getDateTime());
		try {
			while(true) {
				
				//更新状态为运行中
				Map<String, String> sMap = Maps.newHashMap();
				sMap.put("taskid", taskid);
				
				Map<String,String> result = taskRunResult(sMap, index);
				if("0".equals(result.get("runFlag")))break;
				String userId = result.get("userId");
				String smsContent = "";
				int payCount = 1;
				String key = AccountCacheUtils.getAmountKey("sms", userId);
				//获取待发送数据
				File file = new File(Global.getConfig("smsTask.phoneList.dir")+File.separator+taskid+".txt");
				BufferedReader br = null;
				InputStreamReader reader = null;
				try{
					if(!file.exists()){
						doFinish(sMap);
						logger.error("点对点短信批量任务发送失败,文件不存在");
						break;
					}
					reader = new InputStreamReader(new FileInputStream(file),"UTF-8");
					br = new BufferedReader(reader);
					for(String line = br.readLine();line !=null;line=br.readLine()){
						limiter.acquire();
						index++;
						if(rowNumber > 0){
							if(rowNumber > index) {
								continue;
							}
							rowNumber = 0;
						}
						
						if(index % 1000 == 0){//执行1000条
							Map<String,String> resultMap = taskRunResult(sMap, index);
							if("0".equals(resultMap.get("runFlag")))return;
						}
						
						String[] array = line.split("	");
						
						smsContent = array[1];
						payCount = StringUtils.findPayCount(smsContent);
						if(JedisClusterUtils.decrBy(key, payCount) < 0){//余额不足
							JedisClusterUtils.incrBy(key, payCount);
							sMap.put("status", "4");// 发送状态为发送完成，余额不足
							sMap.put("version", "");// 发送状态为发送完成
							taskDao.updateSendStatus(sMap);
							logger.info("DOT2DOT-点对点短信批量任务发送结束，余额不足");
							SmsDot2DotSendTask.smsDotSendExecMap.remove(taskid);// 任务执行完成
							return;
						}
						//发送 
	                	SmsMtMessage message = new SmsMtMessage();
	                    message.setSmsType("sms");
	                    message.setMsgContent(smsContent);
	                    message.setPhone(array[0]);
	                    message.setUserid(userId); //用户ID
	                    message.setTaskid(taskid);
	                    message.setSpNumber("188");
	                    message.setUserReportGateWayID("HTTP");
	                    message.setUserReportNotify(StringUtils.isNotBlank(UserUtils.get(userId).getCallbackUrl())?"9":"0");
	                    message.setSendTime(SystemClock.now());
	                    message.setCstmOrderID("");
	                    
	                    // 提交到UMT
	                    int idx = 0;
	                    String msgid = mQUtils.sendSmsMT("SMSUMTV1", "HTTP", message.getTaskid(), FstObjectSerializeUtil.write(message));
						if(StringUtils.equals(msgid, "-1")){//失败发送SR消息
							String id = new MsgId().toString();
							message.setId(id);
							message.setSendStatus("F0074");
							message.setPayType(findPayType(userId));
							Map<String,String> phoneMap = PhoneUtils.get(array[0]);
							message.setPhoneType(PhoneUtils.getPhoneType(phoneMap));//运营商
							message.setCityCode(PhoneUtils.getCityCode(phoneMap));//省市代码
							message.setContentSize(StringUtils.findPayCount(smsContent));
							msgid = mQUtils.sendSmsMT("SMSMT", "HTTP", id, FstObjectSerializeUtil.write(message));
							idx = 1;
						}
						
						logger.info("TASKID:{},点对点短信发送提交队列msgid:{}, idx:{}",  taskid, msgid, idx);
					}
				}catch(Exception e){
					logger.error("点对点短信批量任务发送失败,导入数据错误",e);
				}finally{
					if(br != null)br.close();
					if(reader != null)reader.close();
				}

				doFinish(sMap);//完成
				break;
			}
		} catch (Exception e) {
			logger.error("点对点批量任务发送失败",e);
		}

		long endTime = System.currentTimeMillis();
		logger.info("点对点任务执行完成,TASKID{},结束时间{},耗时：{}}",taskid, DateUtils.getDateTime(), (endTime - startTime)/1000);
	}

	private void doFinish(Map<String, String> sMap) {
		sMap.put("status", "3");// 发送状态为发送完成
		sMap.put("version", "");// 发送状态为发送完成
		taskDao.updateSendStatus(sMap);
		SmsDot2DotSendTask.smsDotSendExecMap.remove(taskid);// 任务执行完成
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
				SmsDot2DotSendTask.smsDotSendExecMap.remove(taskid);// 剔除线程
				runFlag = "0";
			}
	
			if ("1".equals(sendStatus) || "8".equals(sendStatus)) {// 发送状态为待发送、继续发送
				map.put("status", "2");// 运行中
				map.put("version",String.valueOf(version));
				if(0 == taskDao.updateSendStatus(map)){
					SmsDot2DotSendTask.smsDotSendExecMap.remove(taskid);// 剔除线程
					runFlag = "0";
				}
			}
		}else{
			SmsDot2DotSendTask.smsDotSendExecMap.remove(taskid);// 剔除线程
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

}
