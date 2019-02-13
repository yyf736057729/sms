//package com.sanerzone.smscenter.service;
//
//import javax.annotation.PostConstruct;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Lazy;
//import org.springframework.stereotype.Service;
//
//import com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
//import com.alibaba.rocketmq.client.exception.MQClientException;
//import com.sanerzone.common.support.rocketmq.MQCustomerFactory;
//import com.sanerzone.smscenter.utils.CacheKeys;
//
//@Service
//@Lazy(false)
//public class MQStartup {
//	Logger logger = LoggerFactory.getLogger(MQStartup.class);
//	
//	@Autowired
//	private SMSREQListener smsREQListener;
//	
//	@Autowired
//	private SMSMTV2Listener smsMTV2Listener;
//	
//	@Autowired
//	private SMSSRV2Listener smsSRV2Listener;
//	
//	@Autowired
//	private SMSSRV2SubmitListener smsSRV2SubmitListener;
//	
//	@Autowired
//	private SMSRTV2Listener smsRTV2Listener;
//	
//	@Autowired
//	private SMSRTStatusListener smsRTStatusListener;
//	
//
//	@PostConstruct
//	public void init(){
//		smsREQConsumer();
//		smsMTConsumer();
//		smsSRConsumer();
//		smsSRSubmitConsumer();
//		smsRTConsumer();
//	}
//	
//	/**
//     * SMSREQ 集群消费
//     */
//    private void smsREQConsumer() {
//        DefaultMQPushConsumer smsStatusConsumer = MQCustomerFactory.getPushConsumer("smsREQConsumerGroup");
//        try {
//            smsStatusConsumer.setInstanceName("smsREQConsumer");
//            smsStatusConsumer.subscribe(CacheKeys.getSmsREQTopic(), "*");
//            smsStatusConsumer.setMessageListener(smsREQListener);
//            smsStatusConsumer.start();
//            logger.info("短信SMSREQ 集群消费程序已启动");
//        } catch (MQClientException e) {
//            logger.error("短信SMSREQ 集群消费程序异常", e);
//        }
//    }
//    
//    /**
//     * SMSMTV2混合消费
//     */
//    private void smsMTConsumer() {
//        DefaultMQPushConsumer smsStatusConsumer = MQCustomerFactory.getPushConsumer("smsMTConsumerGroup");
//        try {
//            smsStatusConsumer.setInstanceName("smsMTConsumer");
//            smsStatusConsumer.subscribe(CacheKeys.getSmsSingleTopic(), "*");
//            smsStatusConsumer.setMessageListener(smsMTV2Listener);
//            smsStatusConsumer.start();
//            logger.info("短信混合消费处理程序已启动");
//        } catch (MQClientException e) {
//            logger.error("短信混合消费处理程序异常", e);
//        }
//    }
//    
//    /**
//	 * SMSSRV2更新状态
//	 */
//	private void smsSRConsumer() {
//		DefaultMQPushConsumer smsStatusConsumer = MQCustomerFactory.getPushConsumer("smsSRConsumerGroup");
//		try {
//			smsStatusConsumer.setInstanceName("smsSRConsumer");
//			smsStatusConsumer.subscribe(CacheKeys.getSmsSubmit(), "*");
//			smsStatusConsumer.setMessageListener(smsSRV2Listener);
//			smsStatusConsumer.start();
//			logger.info("短信SMSSRV2更新状态处理程序已启动");
//		} catch (MQClientException e) {
//			logger.error("短信SMSSRV2更新状态处理程序异常", e);
//		}
//	}
//	
//	/**
//	 * SMSSRV2 入库
//	 */
//	private void smsSRSubmitConsumer() {
//		DefaultMQPushConsumer smsStatusConsumer = MQCustomerFactory.getPushConsumer("smsSRSubmitConsumerGroup");
//		try {
//			smsStatusConsumer.setInstanceName("smsSRSubmitConsumer");
//			smsStatusConsumer.subscribe(CacheKeys.getSmsSubmit(), "*");
//			smsStatusConsumer.setMessageListener(smsSRV2SubmitListener);
//			smsStatusConsumer.start();
//			logger.info("短信SMSSRV2 入库处理程序已启动");
//		} catch (MQClientException e) {
//			logger.error("短信SMSSRV2 入库处理程序异常", e);
//		}
//	}
//	
//	/**
//	 * SMSRTV2 入库
//	 */
//	private void smsRTConsumer() {
//		DefaultMQPushConsumer smsStatusConsumer = MQCustomerFactory.getPushConsumer("smsRTConsumerGroup");
//		try {
//			smsStatusConsumer.setInstanceName("smsRTConsumer");
//			smsStatusConsumer.subscribe(CacheKeys.getReportTopic(), "*");
//			smsStatusConsumer.setMessageListener(smsRTV2Listener);
//			smsStatusConsumer.start();
//			logger.info("短信SMSRTV2 入库处理程序已启动");
//		} catch (MQClientException e) {
//			logger.error("短信SMSRTV2 入库处理程序异常", e);
//		}
//	}
//	
//	/**
//	 * SMSRTV2更新状态
//	 */
//	private void smsRTStatusConsumer() {
//		DefaultMQPushConsumer smsStatusConsumer = MQCustomerFactory.getPushConsumer("smsRTStatusConsumerGroup");
//		try {
//			smsStatusConsumer.setInstanceName("smsRTStatusConsumer");
//			smsStatusConsumer.subscribe(CacheKeys.getReportTopic(), "*");
//			smsStatusConsumer.setMessageListener(smsRTStatusListener);
//			smsStatusConsumer.start();
//			logger.info("短信SMSRTV2更新状态处理程序已启动");
//		} catch (MQClientException e) {
//			logger.error("短信SMSRTV2更新状态处理程序异常", e);
//		}
//	}
//	
//}
