package com.siloyou.jmsg.modules.sms.task.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Map;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.RateLimiter;
import com.sanerzone.common.modules.TableNameUtil;
import com.sanerzone.common.support.utils.DateUtils;
import com.siloyou.core.common.config.Global;
import com.siloyou.core.common.utils.HttpRequest;
import com.siloyou.core.common.utils.SpringContextHolder;
import com.siloyou.core.common.utils.StringUtils;
import com.siloyou.core.modules.sys.entity.Dict;
import com.siloyou.core.modules.sys.entity.User;
import com.siloyou.core.modules.sys.utils.DictUtils;
import com.siloyou.core.modules.sys.utils.UserUtils;
import com.siloyou.jmsg.common.utils.PhoneUtils;
import com.siloyou.jmsg.modules.Result;
import com.siloyou.jmsg.modules.sms.dao.JmsgSmsTaskDao;
import com.siloyou.jmsg.modules.sms.entity.JmsgGatewayInfo;
import com.siloyou.jmsg.modules.sms.entity.JmsgSmsSend;
import com.siloyou.jmsg.modules.sms.entity.JmsgSmsTask;
import com.siloyou.jmsg.modules.sms.service.JmsgGatewayInfoService;
import com.siloyou.jmsg.modules.sms.task.SmsSendBatchTask;

public class SmsTaskSendBatchExecutor implements Runnable {

	public static Logger logger = LoggerFactory.getLogger(SmsTaskSendBatchExecutor.class);
	private static JmsgSmsTaskDao taskDao = SpringContextHolder.getBean(JmsgSmsTaskDao.class);
	private static JmsgGatewayInfoService gatewayInfoService = SpringContextHolder.getBean(JmsgGatewayInfoService.class);
	private static SqlSessionFactory sqlSessionFactory = SpringContextHolder.getBean(SqlSessionFactory.class);
	protected RateLimiter limiter = RateLimiter.create(20);
	
	
	private String taskid;
	private int version;
	private int rowNumber;//记录暂停时发送条数
	private String sendDatetime;
	
	private int index = 0;

	public SmsTaskSendBatchExecutor(String taskid,int version,int rowNumber,String sendDatetime) {
		this.taskid = taskid;
		this.version = version;
		this.rowNumber = rowNumber;
		this.sendDatetime = sendDatetime;
	}

	
	
	@Override
	public void run() {

		long startTime = System.currentTimeMillis();
		logger.info("任务开始执行,TASKID{},时间{}" , taskid, DateUtils.getDateTime());
		StringBuffer phoneSB = new StringBuffer("");
		
		try {
			//更新状态为运行中
			Map<String, String> sMap = Maps.newHashMap();
			sMap.put("taskid", taskid);
			
			Map<String,String> result = taskRunResult(sMap, index);
			if("0".equals(result.get("runFlag")))return;
			String userId = result.get("userId");
			String smsContent = result.get("smsContent");
			
			//获取用户
			User user = getUser(userId);
			if(user == null || StringUtils.isBlank(user.getId())){
				logger.error("batch-send批量发送短信定时任务失败，用户[{}]不存在，TASKID:{}", userId, taskid);
				doFinish(sMap);
				return;
			}
			
			//获取通道ID
			JmsgGatewayInfo gateway  = getGateway(userId);
			if(gateway == null || StringUtils.isBlank(gateway.getId())){
				logger.error("batch-send批量发送短信定时任务失败，用户[{}]没有匹配到通道，TASKID:{}", userId, taskid);
				doFinish(sMap);
				return;
			}
			
			//获取待发送数据
			String pathanme = Global.getConfig("smsTask.phoneList.dir")+File.separator+sendDatetime+File.separator+taskid+".txt";
			File file = new File(pathanme);
			if(!(file.exists() && file.length() > 0)){
				logger.error("batch-send批量发送短信定时任务失败，文件路径：{}，获取文件内容失败，TASKID:{}", userId, taskid);
				doFinish(sMap);
				return;
			}
			
			String payType = findPayType(userId);//扣费方式
			
			int payCount = StringUtils.findPayCount(smsContent);//扣费条数
			String smsType = payCount > 1 ? "2" : "1";//短信类型 1:短短信 2:长短信
			String pushFlag = StringUtils.isNotBlank(user.getCallbackUrl())?"9":"0";
			
			//处理发送实体
			JmsgSmsSend jmsgSmsSend = handlerEntity(taskid, smsContent, smsType, payCount, user, 
						payType, pushFlag, gateway.getId());
			
			BufferedReader br = null;
			InputStreamReader reader = null;
			SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH, false);
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
						Map<String,String> resultMap = taskRunResult(sMap, index);
						if("0".equals(resultMap.get("runFlag")))return;
					}
					
					Map<String,String> phoneMap = PhoneUtils.get(line);//获取手机号码信息
					
					jmsgSmsSend.setId(getBizid(line));
					jmsgSmsSend.setPhone(line);
					jmsgSmsSend.setPhoneType(PhoneUtils.getPhoneType(phoneMap));
					jmsgSmsSend.setAreaCode(PhoneUtils.getCityCode(phoneMap));
					
					phoneSB.append(line).append(",");
					
					sqlSession.insert("com.siloyou.jmsg.modules.sms.dao.JmsgSmsSendDao.insert", jmsgSmsSend);
					if (index % 100 == 0) {
						sqlSession.commit();
					}
				}
				sqlSession.commit();
			}catch(Exception e){
				logger.error("batch-send批量任务发送失败{},TASKID:{}", e, taskid);
			}finally{
				if(br != null)br.close();
				if(reader != null)reader.close();
				if (sqlSession != null) {
					sqlSession.close();
				}
			}
			
			doFinish(sMap);
			
			String phoneList = phoneSB.toString();
			if(StringUtils.isNotBlank(phoneList)){
				phoneList = phoneList.substring(0, phoneList.length() - 1);
				//提交网关
	            StringBuffer strBuf = new StringBuffer();
	            strBuf.append("http://")
	                .append(gateway.getAppHost())
	                .append(":")
	                .append(gateway.getAppCode())
	                .append("/api/sms/gateway/send");
		         
		        StringBuffer paramBuf = new StringBuffer();
		        paramBuf.append("id=")
		        	.append("&gateWayID="+gateway.getId())//通道ID
		            .append("&userid="+user.getId())
		            .append("&taskid="+taskid)
		            .append("&payType="+jmsgSmsSend.getPayType())
		            .append("&cstmOrderID=")
		            .append("&userReportNotify="+jmsgSmsSend.getPushFlag())
		            .append("&userReportGateWayID=HTTP")//HTTP
		            .append("&msgContent="+smsContent)
		            .append("&phone="+phoneList)
		            .append("&spNumber=")//null
		            .append("&smsType=sms")//sms
		            .append("&wapUrl=");//null
		        Result code = JSON.parseObject(HttpRequest.sendPost(strBuf.toString(), paramBuf.toString(), null), Result.class);
		        if (!code.isSuccess()){
		        	logger.error("batch-send批量任务发送提交网关失败,错误码{}",code.getErrorCode());//日志记录
		        }
			}
		} catch (Exception e) {
			logger.error("batch-send批量任务发送失败",e);
		}

		long endTime = System.currentTimeMillis();
		logger.info("batch-send任务执行完成,TASKID{},结束时间{},耗时：{}}",taskid, DateUtils.getDateTime(), (endTime - startTime)/1000);
	}
	
	private String getBizid(String phone){
		return taskid+phone.substring(1);
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
				SmsSendBatchTask.batchSendExecMap.remove(taskid);// 剔除线程
				runFlag = "0";
			}
	
			if ("1".equals(sendStatus) || "8".equals(sendStatus)) {// 发送状态为待发送、继续发送
				map.put("status", "2");// 运行中
				map.put("version",String.valueOf(version));
				if(0 == taskDao.updateSendStatus(map)){
					SmsSendBatchTask.batchSendExecMap.remove(taskid);// 剔除线程
					runFlag = "0";
				}
			}
		}else{
			SmsSendBatchTask.batchSendExecMap.remove(taskid);// 剔除线程
			runFlag="0";
		}
		
		resultMap.put("runFlag", runFlag);
		resultMap.put("userId", userId);
		resultMap.put("smsContent", smsContent);
		
		return resultMap;
	}
	
	/**
	 * 处理实体类
	 * @param taskId
	 * @param phone
	 * @param smsContent
	 * @param smsType
	 * @param payCount
	 * @param user
	 * @param phoneType
	 * @param areaCode
	 * @param payType
	 * @param pushFlag
	 * @param channelCode
	 * @return
	 */
	private JmsgSmsSend handlerEntity(String taskId, String smsContent, String smsType, int payCount, 
			User user, String payType, String pushFlag, String channelCode){
		JmsgSmsSend entity = new JmsgSmsSend();
		entity.setTableName("jmsg_sms_send_"+TableNameUtil.getTableIndex(0));
		entity.setTaskId(taskId);
		entity.setDataId("99");
		entity.setSmsContent(smsContent);
		entity.setSmsType(smsType);
		entity.setPayCount(payCount);
		entity.setUser(user);
		entity.setPayType(payType);
		entity.setPayStatus("1");
		entity.setPushFlag(pushFlag);
		entity.setSendStatus("T000");
		entity.setChannelCode(channelCode);
		entity.setSubmitMode("API");
		entity.setTopic(channelCode);
		entity.setSpNumber("");
		entity.setReportGatewayId("HTTP");
		entity.setReportStatus("P100");
		return entity;
	}
	
	//扣费方式
	private String findPayType(String userId){
		return UserUtils.getPayMode(userId, "sms");
	}

	//获取用户信息
	private User getUser(String userId){
		return UserUtils.getByNow(userId);
	}
	
	//获取通道信息
	private JmsgGatewayInfo getGateway(String userId){
		Dict param = new Dict();
		param.setType("batch_gateway_conf");
		param.setValue(userId);
		String gatewayId = DictUtils.getDictLabel(param);
		if(StringUtils.isBlank(gatewayId)){
			return null;
		}
		return gatewayInfoService.get(gatewayId);
	}
	
	//完成任务发送
	private void doFinish(Map<String,String> sMap){
		sMap.put("status", "3");// 发送状态为发送完成
		sMap.put("version", "");// 发送状态为发送完成
		taskDao.updateSendStatus(sMap);
		SmsSendBatchTask.batchSendExecMap.remove(taskid);// 任务执行完成
	}
	
}
