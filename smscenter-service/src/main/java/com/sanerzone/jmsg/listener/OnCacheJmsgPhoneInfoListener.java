package com.sanerzone.jmsg.listener;

import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;
import com.sanerzone.common.modules.phone.service.InitPhoneSegment;
import com.sanerzone.common.modules.phone.utils.PhoneUtils;
import com.sanerzone.common.modules.smscenter.service.InitGatewayGroup;
import com.sanerzone.common.modules.smscenter.service.InitGatewayInfo;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
//public class OnCacheJmsgPhoneInfoListener implements MessageListenerConcurrently {
public class OnCacheJmsgPhoneInfoListener implements MessageListener {
	private static Logger logger = LoggerFactory.getLogger(OnCacheJmsgPhoneInfoListener.class);
	@Override
	public Action consume(Message message, ConsumeContext consumeContext) {
		InitPhoneSegment phoneSegment = new InitPhoneSegment();
		phoneSegment.initPhoneType();
		logger.info("号段缓存加载完成");
		return Action.CommitMessage;
	}
//	@Override
//	public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs,
//			ConsumeConcurrentlyContext context) {
//		InitPhoneSegment phoneSegment = new InitPhoneSegment();
//		phoneSegment.initPhoneType();
//		logger.info("号段缓存加载完成");
//		return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
//	}
}
