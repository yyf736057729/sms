package com.siloyou.jmsg.modules.sms.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.siloyou.core.common.utils.DateUtils;
import com.siloyou.core.common.utils.SpringContextHolder;
import com.siloyou.core.common.utils.StringUtils;
import com.siloyou.core.modules.sys.entity.User;
import com.siloyou.core.modules.sys.utils.UserUtils;
import com.siloyou.jmsg.common.utils.BlacklistUtils;
import com.siloyou.jmsg.common.utils.CacheKeys;
import com.siloyou.jmsg.common.utils.GatewayUtils;
import com.siloyou.jmsg.common.utils.MsgId;
import com.siloyou.jmsg.common.utils.PhoneUtils;
import com.siloyou.jmsg.modules.sms.dao.JmsgSmsTaskDao;
import com.siloyou.jmsg.modules.sms.entity.GatewayResult;
import com.siloyou.jmsg.modules.sms.entity.JmsgSmsSend;
import com.siloyou.jmsg.modules.sms.entity.JmsgSmsTask;

public class JmsgSmsSendExecutor implements Runnable {
	
	private static Logger logger = LoggerFactory.getLogger(JmsgSmsSendExecutor.class);
	
	private static final int BATCH_COMMIT_MAX_COUNT = 500;//批量提交默认值
	
	private SqlSessionFactory sqlSessionFactory = SpringContextHolder.getBean(SqlSessionFactory.class);
	
	private JmsgSmsTaskDao jmsgSmsTaskDao = SpringContextHolder.getBean(JmsgSmsTaskDao.class);
	
	private JmsgSmsTask jmsgSmsTask;
	private List<String> phoneList;
	private int payCount;

	public JmsgSmsSendExecutor(JmsgSmsTask jmsgSmsTask,List<String> phoneList,int payCount){
		this.jmsgSmsTask = jmsgSmsTask;
		this.phoneList = phoneList;
		this.payCount = payCount;
	}
	
	@Override
	public void run() {
		try {
			createSendDetail(jmsgSmsTask.getId(), jmsgSmsTask.getCreateBy().getId(), phoneList, jmsgSmsTask.getSmsContent(), jmsgSmsTask.getSign(), payCount, jmsgSmsTask.getSendDatetime());
		} catch (Exception e) {
			logger.error("异步短信发送明细入库失败", e);
		}
	}
	
	
	//任务发送明细
	@Transactional(readOnly = false)
	public void createSendDetail(String taskId,String userId,List<String> phoneList,String smsContent,
								 String sign,int payCount,Date sendDatetime) throws Exception{
		if(StringUtils.isBlank(taskId)){
			return;
		}
		boolean timeFlag = false;//不是跨天任务
		int tableIndex = DateUtils.getDayOfMonth(sendDatetime);//获取天
		String tableName = "jmsg_sms_send_"+tableIndex;
		
		int todayIndex = DateUtils.getDayOfMonth(0);
		if(tableIndex != todayIndex){
			timeFlag = true;
		}
		
		User user = UserUtils.get(userId);
		int index=0;
		String smsType = payCount > 1 ?"2":"1";//1:短短信 2:长短信
		String payType = findPayType(userId);
		String payStatus = "1";//扣费状态
		String pushFlag = "9";//待推送
		if(StringUtils.isBlank(user.getCallbackUrl())){
			pushFlag = "0";//无需推送
		}
		SqlSession sqlSession = this.sqlSessionFactory.openSession(ExecutorType.BATCH,false);
		try{
			JmsgSmsSend jmsgSmsSend;
			MsgId msgid;
			for (String phone : phoneList) {
				jmsgSmsSend = new JmsgSmsSend();
				if(timeFlag){//跨天
					msgid= new MsgId(1000000);
					msgid.setDay(DateUtils.getDayOfMonth(tableIndex));
				}else{
					msgid = new MsgId();
				}
				jmsgSmsSend.setId(msgid.toString());
				jmsgSmsSend.setDataId("188");
				jmsgSmsSend.setTaskId(taskId);//任务我ID
				jmsgSmsSend.setPhone(phone);//手机号码
				jmsgSmsSend.setSmsContent(smsContent);//短信内容
				jmsgSmsSend.setSmsType(smsType);//短信类型
				jmsgSmsSend.setPayCount(payCount);//扣费条数
				jmsgSmsSend.setUser(user);//用户ID,公司ID
				String sendStatus = "T000";//待发
				String phoneType = "";//运营商
				String cityCode = "";//省市代码
				
				Map<String,String> phoneMap = PhoneUtils.get(phone);
				if(phoneMap == null||phoneMap.size() <2){
					sendStatus = "F0170";//号段匹配异常
					jmsgSmsSend.setSendDatetime(sendDatetime);
				}else{
					phoneType = PhoneUtils.getPhoneType(phoneMap);//运营商
					cityCode = PhoneUtils.getCityCode(phoneMap);//省市代码
					
					if(StringUtils.isBlank(cityCode) || StringUtils.isBlank(phoneType)){
						sendStatus = "F0170";//号段匹配异常
						jmsgSmsSend.setSendDatetime(sendDatetime);
					}else{
						//1：校验 0： 不校验
			            if (user.getSysBlacklistFlag() == 1 && BlacklistUtils.isExistSysBlackList(phone))
			            {
			                // 判断是否系统黑名单
			                sendStatus = "F002";
			                jmsgSmsSend.setSendDatetime(sendDatetime);
			            }
			            else if (user.getUserBlacklistFlag() == 1 && BlacklistUtils.isExistUserBlackList(phone))
			            {
			                // 判断是否营销黑名单
			                sendStatus = "F008";
			                jmsgSmsSend.setSendDatetime(sendDatetime);
			            }
			            else
			            {
			            	GatewayResult gatewayResult = gatewayMap(user.getSignFlag(),user.getGroupId(), phoneType, cityCode.substring(0, 2), sign, userId);
							if(gatewayResult.isExists()){
								jmsgSmsSend.setChannelCode(gatewayResult.getGatewayId());//通道代码
								String spNumber = gatewayResult.getSpNumber();
								if( 0 == user.getSignFlag() ){
									spNumber = spNumber + user.getId();
								}
								spNumber = spNumber + "188";
								if(spNumber.length() > 20) {
									spNumber = spNumber.substring(0, 20);
								}
								jmsgSmsSend.setSpNumber(spNumber);//接入号
							}else{
								sendStatus = gatewayResult.getErrorCode();//匹配通道失败
								jmsgSmsSend.setSendDatetime(sendDatetime);
							}
			            }
					}
				}
				
				jmsgSmsSend.setPhoneType(phoneType);//运营商
				jmsgSmsSend.setAreaCode(cityCode);//省市代码
				jmsgSmsSend.setPayType(payType);//扣费方式
				jmsgSmsSend.setPayStatus(payStatus);//扣费状态
				jmsgSmsSend.setPushFlag(pushFlag);//推送标识
				
				jmsgSmsSend.setSendStatus(sendStatus);//发送状态
				jmsgSmsSend.setSubmitMode("WEB");//提交方式 WEB,API
				jmsgSmsSend.setTopic(CacheKeys.getSmsBatchTopic());//发送队列
				jmsgSmsSend.setReportGatewayId("HTTP");
				jmsgSmsSend.setTableName(tableName);
				sqlSession.insert("com.siloyou.jmsg.modules.sms.dao.JmsgSmsSendDao.insert", jmsgSmsSend);
				index++;
				if(index % BATCH_COMMIT_MAX_COUNT == 0) {
					sqlSession.commit();
				}
			}
			sqlSession.commit();
			
			Map<String,String> map = Maps.newHashMap();
			map.put("taskid", taskId);
			map.put("status", "1");
			jmsgSmsTaskDao.updateSendStatus(map);
		}catch(Exception e){
			throw e;
		}finally{
			if(sqlSession != null){
				sqlSession.close();
			}
		}
	}
	
	//获取通道代码  signFlag 1:验证签名 0:自定义签名 
	private GatewayResult gatewayMap(int signFlag,String groupId,String phoneType,String provinceId,String sign,String userId){
		
		GatewayResult entity = new GatewayResult();
		if(1 == signFlag){
			entity = GatewayUtils.getGateway(userId, groupId, phoneType, provinceId, sign);
		}else{
			entity = GatewayUtils.getGateway(groupId, phoneType, provinceId);
		}
		
		return entity;
	}
	
	//扣费方式
	private String findPayType(String userId){
		return UserUtils.getPayMode(userId, "sms");
	}


}
