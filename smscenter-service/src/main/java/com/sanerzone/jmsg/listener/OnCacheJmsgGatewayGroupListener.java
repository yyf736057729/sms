package com.sanerzone.jmsg.listener;

import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;
import com.sanerzone.common.modules.smscenter.service.InitGatewayGroup;
import com.sanerzone.common.modules.smscenter.service.InitGatewayInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
//public class OnCacheJmsgGatewayGroupListener implements MessageListenerConcurrently {
public class OnCacheJmsgGatewayGroupListener implements MessageListener {
	private static Logger logger = LoggerFactory.getLogger(OnCacheJmsgGatewayGroupListener.class);

	@Override
	public Action consume(Message message, ConsumeContext consumeContext) {
		InitGatewayGroup gatewayGroup = new InitGatewayGroup();
		gatewayGroup.initGatewayGroup();
		logger.info("网关分组缓存加载完成");
		InitGatewayInfo gatewayInfo = new InitGatewayInfo();

		gatewayInfo.initGatewayInfo();
		logger.info("网关缓存加载完成");
		return Action.CommitMessage;
	}

//	@Override
//	public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs,
//			ConsumeConcurrentlyContext context) {
//		InitGatewayGroup gatewayGroup = new InitGatewayGroup();
//		gatewayGroup.initGatewayGroup();
//		logger.info("网关分组缓存加载完成");
//		InitGatewayInfo gatewayInfo = new InitGatewayInfo();
//
//		gatewayInfo.initGatewayInfo();
//		logger.info("网关缓存加载完成");
//		return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
//	}


}
