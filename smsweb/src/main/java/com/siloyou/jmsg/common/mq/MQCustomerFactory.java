package com.siloyou.jmsg.common.mq;

import com.aliyun.openservices.ons.api.Consumer;
import com.aliyun.openservices.ons.api.ONSFactory;
import com.aliyun.openservices.ons.api.PropertyKeyConst;
import com.sanerzone.common.support.utils.CommonConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
import com.alibaba.rocketmq.common.consumer.ConsumeFromWhere;
import com.siloyou.core.common.config.Global;

import java.util.Properties;

public class MQCustomerFactory {
	Logger logger = LoggerFactory.getLogger(MQCustomerFactory.class);
	
	public static Consumer getPushConsumer(String consumerGroup) {
		Properties consumerProperties = new Properties();
		consumerProperties.setProperty(PropertyKeyConst.ConsumerId, consumerGroup);
		consumerProperties.put(PropertyKeyConst.GROUP_ID, "GID_"+consumerGroup);
		System.out.println("SMSWEB:  GROUP_ID:_______GID_"+consumerGroup);
		consumerProperties.setProperty(PropertyKeyConst.AccessKey, CommonConstants.MQ_AccessKey);
		consumerProperties.setProperty(PropertyKeyConst.SecretKey, CommonConstants.MQ_SecretKey);
		consumerProperties.setProperty(PropertyKeyConst.NAMESRV_ADDR, CommonConstants.MQ_ONSAddr);
		consumerProperties.put(PropertyKeyConst.ConsumeThreadNums,60);
		Consumer consumer = ONSFactory.createConsumer(consumerProperties);

//		DefaultMQPushConsumer pushConsumer = new DefaultMQPushConsumer(consumerGroup);
//		pushConsumer.setNamesrvAddr(Global.getConfig("sms.center.namesvr"));
//		pushConsumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
		return consumer;
	}
}
