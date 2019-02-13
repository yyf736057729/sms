package com.siloyou.jmsg.modules.sms.listen;

import javax.annotation.PreDestroy;

import com.aliyun.openservices.ons.api.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.Application;
import com.siloyou.jmsg.common.gateway.SmsGateWayConfig;
import com.siloyou.jmsg.common.mq.MQCustomerFactory;

@Service
public class MQStartup {
	Logger logger = LoggerFactory.getLogger(MQStartup.class);
	
	@Autowired
	private SmsMTListener smsMTListener;
	
	Consumer storageConsumer;
	
	public void initStorage(){
		storageConsumer = MQCustomerFactory.getPushConsumer(SmsGateWayConfig.getAppTopic("HIGH")+ "StorageGrp");
		try {
//			storageConsumer.setConsumeMessageBatchMaxSize(32);
//			storageConsumer.setInstanceName(SmsGateWayConfig.getAppTopic("HIGH") + "StorageIns");
			storageConsumer.subscribe(SmsGateWayConfig.getAppTopic("HIGH"), "*",smsMTListener);
//			storageConsumer.setMessageListener(smsMTListener);
			storageConsumer.start();
			logger.info("{}, 状态报告推送程序已启动", Application.appCode);
		} catch (Exception e) {
			logger.error(Application.appCode+"状态报告推送程序异常", e);
		}
	}
	
	@PreDestroy
	void destroy() {
		if ( null != storageConsumer ) {
			storageConsumer.shutdown();
		}
	}
}
