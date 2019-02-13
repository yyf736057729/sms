package com.siloyou.jmsg.common.mq;

import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.aliyun.openservices.ons.api.ONSFactory;
import com.aliyun.openservices.ons.api.Producer;
import com.aliyun.openservices.ons.api.PropertyKeyConst;
import com.sanerzone.common.support.utils.CommonConstants;
import com.siloyou.core.common.config.Global;

import java.util.Properties;

public class MQProducerFactory {

	public static Producer getMQProducer(String producerGroup, String instanceName){
		Properties producerProperties = new Properties();
//		producerProperties.setProperty(PropertyKeyConst.ProducerId, CommonConstants.MQ_PID);
//		producerProperties.setProperty("GROUP_ID", CommonConstants.MQ_GID);
		producerProperties.setProperty(PropertyKeyConst.AccessKey, CommonConstants.MQ_AccessKey);
		producerProperties.setProperty(PropertyKeyConst.SecretKey, CommonConstants.MQ_SecretKey);
		producerProperties.setProperty(PropertyKeyConst.NAMESRV_ADDR, CommonConstants.MQ_ONSAddr);
		Producer producer = ONSFactory.createProducer(producerProperties);

//		DefaultMQProducer producer = new DefaultMQProducer(producerGroup);
//		producer.setNamesrvAddr(Global.getConfig("sms.center.namesvr"));
//		producer.setInstanceName(instanceName);
		return producer;
	}
}
