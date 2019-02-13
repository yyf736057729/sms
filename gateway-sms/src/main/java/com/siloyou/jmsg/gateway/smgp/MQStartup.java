package com.siloyou.jmsg.gateway.smgp;

import com.aliyun.openservices.ons.api.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.Application;
import com.siloyou.jmsg.common.config.Global;
import com.siloyou.jmsg.common.mq.MQCustomerFactory;
import com.siloyou.jmsg.gateway.smgp.handler.listener.GateWayMTListener;

public class MQStartup {
	Logger logger = LoggerFactory.getLogger(MQStartup.class);
	
	private GateWayMTListener gateWayMTListener;
	
	private String appCode = Application.appCode;
	
	private void init(){
		 single();
		 batch();
	}
	
	private void single() {
		Consumer singlePushConsumer = MQCustomerFactory.getPushConsumer(appCode+"-SingleGroup");
		try {
//			singlePushConsumer.setInstanceName("SmsSingleTaskServiceConsumer");
			singlePushConsumer.subscribe(Global.getConfig("gateway.single.topic"), appCode,gateWayMTListener);
//			singlePushConsumer.setMessageListener(gateWayMTListener);
			singlePushConsumer.start();
			logger.info("{}, 单发消费程序已启动", appCode);
		} catch (Exception e) {
			logger.error("启动单发消费程序异常", e);
		}
	}
	
	private void batch() {
		Consumer batchPushConsumer = MQCustomerFactory.getPushConsumer(appCode+"-BatchGroup");
		try {
//			batchPushConsumer.setInstanceName("SmsSingleTaskServiceConsumer");
			batchPushConsumer.subscribe(Global.getConfig("gateway.batch.topic"), appCode,gateWayMTListener);
//			batchPushConsumer.setMessageListener(gateWayMTListener);
			batchPushConsumer.start();
			logger.info("{}, 群发消费程序已启动", appCode);
		} catch (Exception e) {
			logger.error("启动群发消费程序异常", e);
		}
	}

	public void setGateWayMTListener(GateWayMTListener gateWayMTListener) {
		this.gateWayMTListener = gateWayMTListener;
	}
}
