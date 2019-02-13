package com.sanerzone.common.support.rocketmq;

import com.aliyun.openservices.ons.api.ONSFactory;
import com.aliyun.openservices.ons.api.PropertyKeyConst;
import com.sanerzone.common.support.utils.CommonConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
import com.alibaba.rocketmq.client.consumer.MQPullConsumerScheduleService;
import com.alibaba.rocketmq.common.consumer.ConsumeFromWhere;
import com.alibaba.rocketmq.common.protocol.heartbeat.MessageModel;
import com.sanerzone.common.support.config.Global;
import com.aliyun.openservices.ons.api.Consumer;

import java.util.Properties;

public class MQCustomerFactory {
	Logger logger = LoggerFactory.getLogger(MQCustomerFactory.class);

	public static Consumer getPushConsumer(String consumerGroup) {
		Properties consumerProperties = new Properties();
		consumerProperties.setProperty(PropertyKeyConst.ConsumerId, consumerGroup);
		consumerProperties.put(PropertyKeyConst.GROUP_ID, "GID_"+consumerGroup);
		System.out.println("SUPPORT:  GROUP_ID:_______GID_"+consumerGroup);
		consumerProperties.setProperty(PropertyKeyConst.AccessKey, CommonConstants.MQ_AccessKey);
		consumerProperties.setProperty(PropertyKeyConst.SecretKey, CommonConstants.MQ_SecretKey);
		consumerProperties.setProperty(PropertyKeyConst.NAMESRV_ADDR, CommonConstants.MQ_ONSAddr);
		consumerProperties.put(PropertyKeyConst.ConsumeThreadNums,60);
		Consumer consumer = ONSFactory.createConsumer(consumerProperties);
		return consumer;
	}
	
	public static MQPullConsumerScheduleService getPullScheduleConsumer(String consumerGroup) {
		MQPullConsumerScheduleService scheduleService  = new MQPullConsumerScheduleService(consumerGroup);
		scheduleService.getDefaultMQPullConsumer().setNamesrvAddr(Global.getConfig("sms.center.namesvr"));
        return scheduleService;
	}
}
