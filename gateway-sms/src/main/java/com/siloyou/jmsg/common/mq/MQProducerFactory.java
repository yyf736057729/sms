package com.siloyou.jmsg.common.mq;

import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.Application;
import com.aliyun.openservices.ons.api.ONSFactory;
import com.aliyun.openservices.ons.api.Producer;
import com.aliyun.openservices.ons.api.PropertyKeyConst;
import com.siloyou.jmsg.common.util.CommonConstants;

import java.util.Properties;

public class MQProducerFactory {

	public static Producer getMQProducer(String producerGroup, String instanceName){
        Properties producerProperties = new Properties();
        producerProperties.setProperty(PropertyKeyConst.AccessKey, com.siloyou.jmsg.common.util.CommonConstants.MQ_AccessKey);
        producerProperties.setProperty(PropertyKeyConst.SecretKey, com.siloyou.jmsg.common.util.CommonConstants.MQ_SecretKey);
        producerProperties.setProperty(PropertyKeyConst.NAMESRV_ADDR, CommonConstants.MQ_ONSAddr);
        Producer producer = ONSFactory.createProducer(producerProperties);
        return producer;
	}
}
