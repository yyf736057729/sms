package com.siloyou.jmsg.modules.sms.task.handler.listener;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.marre.sms.SmsAlphabet;
import org.marre.sms.SmsMsgClass;
import org.marre.sms.SmsTextMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.google.common.collect.Maps;
import com.siloyou.core.common.utils.FstObjectSerializeUtil;
import com.siloyou.core.common.utils.SpringContextHolder;
import com.siloyou.core.common.utils.StringUtils;
import com.siloyou.core.modules.sys.entity.User;
import com.siloyou.core.modules.sys.utils.UserUtils;
import com.siloyou.jmsg.common.message.SmsMtMessage;
import com.siloyou.jmsg.common.storedMap.BDBStoredMapFactoryImpl;
import com.siloyou.jmsg.common.utils.BlacklistUtils;
import com.siloyou.jmsg.common.utils.CacheKeys;
import com.siloyou.jmsg.common.utils.GatewayUtils;
import com.siloyou.jmsg.common.utils.KeywordsUtils;
import com.siloyou.jmsg.common.utils.PhoneUtils;
import com.siloyou.jmsg.common.utils.SignUtils;
import com.siloyou.jmsg.modules.account.service.JmsgAccountService;
import com.siloyou.jmsg.modules.sms.dao.JmsgSmsSendDao;
import com.siloyou.jmsg.modules.sms.entity.GatewayResult;
import com.siloyou.jmsg.modules.sms.entity.JmsgSmsSend;
import com.siloyou.jmsg.modules.sms.service.SmsSendService;
import com.siloyou.jmsg.modules.sms.task.impl.SmsPushExecutor;

@Service
public class SmsUMTListener implements MessageListenerConcurrently {

	public static Logger logger = LoggerFactory.getLogger(SmsUMTListener.class);

	@Autowired
	private SqlSessionFactory sqlSessionFactory;

	@Autowired
	private JmsgAccountService jmsgAccountService;
	
	@Autowired
	private JmsgSmsSendDao jmsgSmsSendDao ;
	
	@Autowired
	private SmsSendService smsSendService;
	
	private static Map<String, Serializable> umtMap = BDBStoredMapFactoryImpl.INS.buildMap("SMS", "UMTTASK");

	public int findPayCount(String smsContent) {
		SmsTextMessage sms = null;
		if (com.siloyou.core.common.utils.StringUtils.haswidthChar(smsContent)) {
			sms = new SmsTextMessage(smsContent, SmsAlphabet.UCS2, SmsMsgClass.CLASS_UNKNOWN);
		} else {
			sms = new SmsTextMessage(smsContent, SmsAlphabet.LATIN1, SmsMsgClass.CLASS_UNKNOWN);
		}
		return sms.getPdus().length;
	}

	private GatewayResult gatewayMap(int signFlag, String groupId, String phoneType, String provinceId, String sign,
			String userId) {
		GatewayResult entity = new GatewayResult();
		if (1 == signFlag) {
			entity = GatewayUtils.getGateway(userId, groupId, phoneType, provinceId, sign);
		} else {
			entity = GatewayUtils.getGateway(groupId, phoneType, provinceId);
		}
		return entity;
	}
	

	@Override
	public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
		for (MessageExt msg : msgs) {
			logger.info("umt listener recv message: topic:{}, tags:{}, msgid:{}, key:{}", msg.getTopic(), msg.getTags(),
					msg.getMsgId(), msg.getKeys());
			
			long startTime = System.currentTimeMillis();
			try {
				SmsMtMessage smsRtMessage = (SmsMtMessage) FstObjectSerializeUtil.read(msg.getBody());
				smsRtMessage.setMsgContent(SignUtils.formatContent(smsRtMessage.getMsgContent()));
				
				logger.info("CMPP模拟网关,开始处理放库发送: 任务ID:{}, 用户:{}, 手机号码：{}, 接收时间:{} ", smsRtMessage.getTaskid(), smsRtMessage.getUserid(), smsRtMessage.getPhone(), startTime);
			
				String dataId = "99";
				String taskId = smsRtMessage.getTaskid();
				String userId = smsRtMessage.getUserid();
				String phones = smsRtMessage.getPhone();
				String smsContent = smsRtMessage.getMsgContent();
				String reportGaetewayId = smsRtMessage.getUserReportGateWayID();
				String spNumber = smsRtMessage.getSpNumber();
				String pushFlag = smsRtMessage.getUserReportNotify();
				String topic = CacheKeys.getSmsBatchTopic();

				User user = UserUtils.get(userId);
				String sign = SignUtils.get(smsContent);
				int payCount = findPayCount(smsContent);
				String smsType = payCount > 1 ? "2" : "1";
				String payType = UserUtils.getPayMode(userId, "sms");
				String payStatus = "1";
				int money = 0;
				int index = 0;
				String[] phoneList = phones.split(",");
				SqlSession sqlSession = this.sqlSessionFactory.openSession(ExecutorType.BATCH, false);
				try {
					for (String phone : phoneList) {
						JmsgSmsSend jmsgSmsSend = new JmsgSmsSend();
						jmsgSmsSend.setDataId(dataId);
						jmsgSmsSend.setTaskId(taskId);
						jmsgSmsSend.setPhone(phone);
						jmsgSmsSend.setSmsContent(smsContent);
						jmsgSmsSend.setSmsType(smsType);
						jmsgSmsSend.setPayCount(payCount);
						jmsgSmsSend.setUser(user);
						String sendStatus = "P000";
						
						//  1：校验 0： 不校验
			            if (user.getSysBlacklistFlag() == 1 && BlacklistUtils.isExistSysBlackList(phone))
			            {
			                // 判断是否系统黑名单
			                sendStatus = "F002";
			            }
			            else if (user.getUserBlacklistFlag() == 1 && BlacklistUtils.isExistUserBlackList(phone))
			            {
			                // 判断是否营销黑名单
			                sendStatus = "F008";
			            }
			            
			            if("1".equals(user.getFilterWordFlag())){//过滤敏感词
							String keywords = KeywordsUtils.keywords(smsContent.trim());
							if(StringUtils.isNotBlank(keywords)) {
								 sendStatus = "F020";
							}
						}
						Map<String, String> phoneMap = PhoneUtils.get(phone);
						String phoneType = "";
						String cityCode = "";
						if ((phoneMap == null) || (phoneMap.size() < 2)) {
							sendStatus = "F0170";
							jmsgSmsSend.setSendDatetime(new Date());
						} else {
							phoneType = PhoneUtils.getPhoneType(phoneMap);
							cityCode = PhoneUtils.getCityCode(phoneMap);
							
							if ((StringUtils.isBlank(cityCode)) || (StringUtils.isBlank(phoneType))) {
								sendStatus = "F0170";
								jmsgSmsSend.setSendDatetime(new Date());
							} else {
								GatewayResult gatewayResult = gatewayMap(user.getSignFlag(), user.getGroupId(),
										phoneType, cityCode.substring(0, 2), sign, userId);
								if (gatewayResult.isExists()) {
									jmsgSmsSend.setChannelCode(gatewayResult.getGatewayId());
									String tdSpNumber = gatewayResult.getSpNumber();
									if ((StringUtils.isNotBlank(spNumber)) && (spNumber.startsWith(tdSpNumber))) {
										tdSpNumber = spNumber;
									}
									if(StringUtils.isNotBlank(tdSpNumber) && tdSpNumber.length() > 20) {
										tdSpNumber = tdSpNumber.substring(0, 20);
									}
									jmsgSmsSend.setSpNumber(tdSpNumber);
								} else {
									sendStatus = gatewayResult.getErrorCode();
									jmsgSmsSend.setSendDatetime(new Date());
								}
							}
						}
						jmsgSmsSend.setPhoneType(phoneType);
						jmsgSmsSend.setAreaCode(cityCode);
						jmsgSmsSend.setPayType(payType);
						jmsgSmsSend.setPayStatus(payStatus);
						jmsgSmsSend.setPushFlag(pushFlag);

						jmsgSmsSend.setSendStatus(sendStatus);
						jmsgSmsSend.setSubmitMode(reportGaetewayId);
						jmsgSmsSend.setTopic(topic);
						jmsgSmsSend.setReportGatewayId(reportGaetewayId);
						money += payCount;
						
						//入库
						jmsgSmsSendDao.insert(jmsgSmsSend);
						
						Map<String,String> map = Maps.newHashMap();
						map.put("id", jmsgSmsSend.getId());
						
						if(StringUtils.startsWith(sendStatus, "F")) { //更新结果
							map.put("sendStatus", sendStatus);
							map.put("msgid", "0000");
						} else { //发送队列
							map = smsSendService.sendHandler(jmsgSmsSend);//发送短信处理
						}
						
						index++;
						sqlSession.update("com.siloyou.jmsg.modules.sms.dao.JmsgSmsSendDao.batchUpdate",map);
//						sqlSession.insert("com.siloyou.jmsg.modules.sms.dao.JmsgSmsSendDao.insert", jmsgSmsSend);
						if (index % 100 == 0) {
							sqlSession.commit();
						}
					}
					sqlSession.commit();
					
					logger.info("CMPP模拟网关,发送结束: 任务ID:{}, 耗时:{}s",smsRtMessage.getTaskid(), (System.currentTimeMillis() - startTime)/1000);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (sqlSession != null) {
						sqlSession.close();
					}
				}
				if (money > 0) {
//					this.jmsgAccountService.consumeMoney(userId, "02", money, "sms", "" + taskId + ")", userId, taskId);
					
					//推送 
//					SmsPushExecutor smsPushExecutor = new SmsPushExecutor(taskId);
//					smsPushExecutor.run();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
	}

}
