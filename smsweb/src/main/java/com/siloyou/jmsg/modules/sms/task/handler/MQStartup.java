//package com.siloyou.jmsg.modules.sms.task.handler;
//
//import java.util.List;
//
//import javax.annotation.PostConstruct;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Lazy;
//import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
//import org.springframework.stereotype.Service;
//
//import com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
//import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
//import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
//import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
//import com.alibaba.rocketmq.client.exception.MQClientException;
//import com.alibaba.rocketmq.common.message.MessageExt;
//import com.alibaba.rocketmq.common.protocol.heartbeat.MessageModel;
//import com.siloyou.core.common.utils.SpringContextHolder;
//import com.siloyou.jmsg.common.mq.MQCustomerFactory;
//import com.siloyou.jmsg.common.utils.CacheKeys;
//import com.siloyou.jmsg.modules.sms.task.handler.listener.SmsDeliverMsgListener;
//import com.siloyou.jmsg.modules.sms.task.handler.listener.SmsMTListener;
//import com.siloyou.jmsg.modules.sms.task.handler.listener.SmsPushMsgListener;
//import com.siloyou.jmsg.modules.sms.task.handler.listener.SmsReportMsgListener;
//import com.siloyou.jmsg.modules.sms.task.handler.listener.SmsReportStatusFromSubmitListener;
//import com.siloyou.jmsg.modules.sms.task.handler.listener.SmsReportStatusMsgListener;
//import com.siloyou.jmsg.modules.sms.task.handler.listener.SmsSendStatusMsgListener;
//import com.siloyou.jmsg.modules.sms.task.handler.listener.SmsSubmitRespMsgListener;
//import com.siloyou.jmsg.modules.sms.task.handler.listener.SmsUMTListener;
//import com.siloyou.jmsg.modules.sms.task.impl.SmsSendExecutor;
//
//@Service
//@Lazy(false)
//public class MQStartup {
//	Logger logger = LoggerFactory.getLogger(MQStartup.class);
//	
//	@Autowired
//	private SmsReportStatusMsgListener smsReportStatusMsgListener;
//	
//	@Autowired
//	private SmsReportMsgListener smsReportMsgListener;
//	
//	@Autowired
//	private SmsPushMsgListener smsPushMsgListener;
//	
//	@Autowired
//	private SmsSendStatusMsgListener smsSendStatusMsgListener;
//	
//	@Autowired
//	private SmsSubmitRespMsgListener smsSubmitRespMsgListener;
//	
//	@Autowired
//	private SmsUMTListener smsUMTListener;
//	
//	@Autowired
//	private SmsDeliverMsgListener smsDeliverMsgListener;
//	
//	@Autowired
//	private SmsReportStatusFromSubmitListener smsReportStatusFromSubmitListener;
//	
//	@Autowired
//	private SmsMTListener smsMTListener;
//
//	@PostConstruct
//	public void init(){
//		smsStatusConsumer();
//	    smsSubmitConsumer();
//	    smsRtConsumer();
//	    smsUtConsumer();
//	    smsUMTConsumer();
//	    singleTaskConsumer();
//	    smsMOConsumer();
//	    smsSendRtConsumer();
//	    smsSendRtFromSubmitConsumer();
//	}
//	
//	/**
//     * 短信单发入库
//     */
//    private void smsMTConsumer() {
//        DefaultMQPushConsumer smsStatusConsumer = MQCustomerFactory.getPushConsumer("smsMTConsumerGroup");
//        try {
//            smsStatusConsumer.setInstanceName("smsMTConsumer");
//            smsStatusConsumer.subscribe(CacheKeys.getSmsSingleTopic(), "*");
//            smsStatusConsumer.setMessageListener(smsMTListener);
//            smsStatusConsumer.start();
//            logger.info("短信单发入库处理程序已启动");
//        } catch (MQClientException e) {
//            logger.error("短信单发入库处理程序异常", e);
//        }
//    }
//	
//	/**
//	 * 短信发送状态
//	 */
//	private void smsStatusConsumer() {
//		DefaultMQPushConsumer smsStatusConsumer = MQCustomerFactory.getPushConsumer("SMSSTATUSConsumerGroup");
//		try {
//			smsStatusConsumer.setInstanceName("SMSSTATUSConsumer");
//			smsStatusConsumer.subscribe(CacheKeys.getSmsSendStatus(), "*");
//			smsStatusConsumer.setMessageListener(smsSendStatusMsgListener);
//			smsStatusConsumer.start();
//			logger.info("短信发送状态处理程序已启动");
//		} catch (MQClientException e) {
//			logger.error("短信发送状态处理程序异常", e);
//		}
//	}
//	
//	/**
//	 * 短信提交网关
//	 */
//	private void smsSubmitConsumer() {
//		DefaultMQPushConsumer smsStatusConsumer = MQCustomerFactory.getPushConsumer("SMSSUBMITConsumerGroup");
//		try {
//			smsStatusConsumer.setInstanceName("SMSSUBMITConsumer");
//			smsStatusConsumer.subscribe(CacheKeys.getSmsSubmit(), "*");
//			smsStatusConsumer.setMessageListener(smsSubmitRespMsgListener);
//			smsStatusConsumer.start();
//			logger.info("短信提交网关处理程序已启动");
//		} catch (MQClientException e) {
//			logger.error("短信提交网关处理程序异常", e);
//		}
//	}
//
//	/**
//     * 短信提交网关
//     */
//    private void smsSendRtFromSubmitConsumer() {
//        DefaultMQPushConsumer smsStatusConsumer = MQCustomerFactory.getPushConsumer("SMSSRUConsumerGroup");
//        try {
//            smsStatusConsumer.setInstanceName("SMSSRUConsumer");
//            smsStatusConsumer.subscribe(CacheKeys.getSmsSubmit(), "*");
//            smsStatusConsumer.setMessageListener(smsReportStatusFromSubmitListener);
//            smsStatusConsumer.start();
//            logger.info("网关状态处理程序已启动");
//        } catch (MQClientException e) {
//            logger.error("网关状态处理程序异常", e);
//        }
//    }
//	
//	/**
//	 * 状态报告处理
//	 */
//	private void smsRtConsumer() {
//		DefaultMQPushConsumer smsRtConsumer = MQCustomerFactory.getPushConsumer("SMSRTConsumerGroup");
//		try {
//			smsRtConsumer.setInstanceName("SMSRTConsumer");
//			smsRtConsumer.subscribe(CacheKeys.getReportTopic(), "*");
//			smsRtConsumer.setMessageListener(smsReportMsgListener);
//			smsRtConsumer.start();
//			logger.info("状态报告处理程序已启动");
//		} catch (MQClientException e) {
//			logger.error("启动状态报告处理程序异常", e);
//		}
//	}
//	
//	/**
//	 * 发送表状态报告回执
//	 */
//	private void smsSendRtConsumer() {
//		DefaultMQPushConsumer smsSentRtConsumer = MQCustomerFactory.getPushConsumer("SMSSendRTConsumerGroup");
//		try {
//			smsSentRtConsumer.setInstanceName("SMSSendRTConsumer");
//			smsSentRtConsumer.subscribe(CacheKeys.getReportTopic(), "*");
//			smsSentRtConsumer.setMessageListener(smsReportStatusMsgListener);
//			smsSentRtConsumer.start();
//			logger.info("发送表状态报告回执程序已启动");
//		} catch (MQClientException e) {
//			logger.error("启动发送表状态报告回执程序异常", e);
//		}
//	}	
//	
//	/**
//	 * 用户状态推送处理
//	 */
//	private void smsUtConsumer() {
//		DefaultMQPushConsumer smsUtConsumer = MQCustomerFactory.getPushConsumer("SMSUTConsumerGroup");
//		try {
//			smsUtConsumer.setInstanceName("SMSUTConsumer");
//			smsUtConsumer.subscribe(CacheKeys.getPushTopic(), "HTTP");
//			smsUtConsumer.setMessageListener(smsPushMsgListener);
//			smsUtConsumer.start();
//			logger.info("用户报告处理程序已启动");
//		} catch (MQClientException e) {
//			logger.error("用户状态报告处理程序异常", e);
//		}
//	}
//	
//	
//	ThreadPoolTaskExecutor poolTaskExecutor = (ThreadPoolTaskExecutor)SpringContextHolder.getBean("taskExecutor");
//	/**
//	 * 单发任务处理程序
//	 */
//	public void singleTaskConsumer() {
//		DefaultMQPushConsumer singleTaskConsumer = MQCustomerFactory.getPushConsumer("SmsSingleTaskServiceGroup");
//		try {
//			singleTaskConsumer.setInstanceName("SmsSingleTaskServiceConsumer");
//			singleTaskConsumer.subscribe("SMS_SINGLE_TASK_TOPIC", "*");
//			singleTaskConsumer.registerMessageListener(new MessageListenerConcurrently() {
//				@Override
//				public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs,
//						ConsumeConcurrentlyContext context) {
//					for (MessageExt msg : msgs) {
//						String taskId =  new String(msg.getBody());
//						SmsSendExecutor send = new SmsSendExecutor(taskId);
//						poolTaskExecutor.execute(send);
//					}
//					return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
//				}
//			});
//			singleTaskConsumer.start();
//			logger.info("单发消费程序启动成功");
//		} catch (Exception e) {
//			logger.error("单发消费程序启动异常", e);
//		}
//	}
//	
//	/**
//	 * 用户下行处理程序
//	 */
//	public void smsUMTConsumer() {
//		DefaultMQPushConsumer smsUMTConsumer = MQCustomerFactory.getPushConsumer("SmsUMTServiceGroup");
//		try {
//			smsUMTConsumer.setInstanceName("SmsUMTServiceConsumerslave");
//			smsUMTConsumer.subscribe(CacheKeys.getSmsUMTTopic(), "*");
//			smsUMTConsumer.setMessageModel(MessageModel.CLUSTERING);
//			smsUMTConsumer.setMessageListener(smsUMTListener);
//			smsUMTConsumer.start();
//			logger.info("短信UMT消费启动成功");
//		} catch (Exception e) {
//			logger.error("短信UMT消费启动异常",e);
//		}
//	}
//	
//	/**
//	 * 用户上行处理程序
//	 */
//	public void smsMOConsumer() {
//		DefaultMQPushConsumer smsMOConsumer = MQCustomerFactory.getPushConsumer("SmsMOServiceGroup");
//		try {
//			smsMOConsumer.setInstanceName("SmsMOServiceConsumer");
//			smsMOConsumer.subscribe(CacheKeys.getMOTopic(), "*");
//			smsMOConsumer.setMessageListener(smsDeliverMsgListener);
//			smsMOConsumer.start();
//			logger.info("短信MO消费启动成功");
//		} catch (Exception e) {
//			logger.error("短信MO消费启动异常",e);
//		}
//	}
//
//}
