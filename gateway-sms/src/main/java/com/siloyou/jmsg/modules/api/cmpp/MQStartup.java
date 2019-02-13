package com.siloyou.jmsg.modules.api.cmpp;

import com.aliyun.openservices.ons.api.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.Application;
import com.siloyou.jmsg.common.config.Global;
import com.siloyou.jmsg.common.mq.MQCustomerFactory;
import com.siloyou.jmsg.modules.api.cmpp.listener.GateWayCMPPMOListener;
import com.siloyou.jmsg.modules.api.cmpp.listener.GateWayCMPPRTListener;

public class MQStartup {
	Logger logger = LoggerFactory.getLogger(MQStartup.class);
	
	private GateWayCMPPRTListener gateWayCMPPRTListener;
	
	public void setGateWayCMPPRTListener(GateWayCMPPRTListener gateWayCMPPRTListener) {
		this.gateWayCMPPRTListener = gateWayCMPPRTListener;
	}
	
	private GateWayCMPPMOListener gateWayCMPPMOListener;
	
	public void setGateWayCMPPMOListener(GateWayCMPPMOListener gateWayCMPPMOListener) {
		this.gateWayCMPPMOListener = gateWayCMPPMOListener;
	}

	private String appCode = Application.appCode;
	
	public void init() {
		cmppRT();
		cmppMO();
	}
	
	private void cmppRT(){
		Consumer cmppRTConsumer = MQCustomerFactory.getPushConsumer(appCode+"-SMSURV1CmppSrvGroup");
		try {
//			cmppRTConsumer.setInstanceName("SMSURV1CmppSrvConsumer");
			cmppRTConsumer.subscribe(Global.getConfig("gateway.smsur.topic"), "CMPP" + appCode,gateWayCMPPRTListener);
//			cmppRTConsumer.setMessageListener(gateWayCMPPRTListener);
			cmppRTConsumer.start();
			logger.info("{},topic{},tag{}, 状态报告推送程序已启动", appCode,Global.getConfig("gateway.smsur.topic"), "CMPP" + appCode);
		} catch (Exception e) {
			logger.error(appCode+"状态报告推送程序异常", e);
		}
	}
	
	private void cmppMO(){
		Consumer cmppRTConsumer = MQCustomerFactory.getPushConsumer(appCode+"-SMSMOV1CmppSrvGroup");
		try {
//			cmppRTConsumer.setInstanceName("SMSMOV1CmppSrvConsumer");
			cmppRTConsumer.subscribe(Global.getConfig("gateway.smsmo.topic"), "*",gateWayCMPPMOListener);
//			cmppRTConsumer.setMessageListener(gateWayCMPPMOListener);
			cmppRTConsumer.start();
			logger.info("{}, 上行推送程序已启动", appCode);
		} catch (Exception e) {
			logger.error(appCode+"上行推送程序异常", e);
		}
	}
}
