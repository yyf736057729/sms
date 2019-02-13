package com.siloyou.jmsg.modules.mms.task.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import com.alibaba.rocketmq.common.consumer.ConsumeFromWhere;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.siloyou.core.common.config.Global;
import com.siloyou.core.common.mapper.JsonMapper;
import com.siloyou.core.common.utils.SpringContextHolder;

//@Service
//@Lazy(false)
public class MmsStatusRecvService {
	public static Logger logger = LoggerFactory.getLogger(MmsStatusRecvService.class);
	
	@PostConstruct
	public void init() {
		try {
			DefaultMQPushConsumer smsStatusConsumer = new DefaultMQPushConsumer("SmsSendStatusGroup");
			smsStatusConsumer.setNamesrvAddr(Global.getConfig("mms.send.url"));
			smsStatusConsumer.setInstanceName("smsStatusConsumer");
			smsStatusConsumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
			smsStatusConsumer.subscribe("SmsStatusTopic", "*");
			smsStatusConsumer.setConsumeMessageBatchMaxSize(500);
			smsStatusConsumer.registerMessageListener(new MessageListenerConcurrently() {
				@Override
				public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs,
						ConsumeConcurrentlyContext context) {
					logger.info("mqsize:{}", msgs.size());
					SqlSession sqlSession = SpringContextHolder.getBean(SqlSessionFactory.class).openSession(ExecutorType.BATCH, false);
					try {
						for (MessageExt msg : msgs) {
							@SuppressWarnings("unchecked")
							Map<String,String> mmsMsg = (Map<String,String>)JsonMapper.fromJsonString(new String(msg.getBody()), Map.class);
							sqlSession.insert("com.siloyou.jmsg.modules.mms.dao.JmsgMmsTaskDetailDao.updateSendStatus", mmsMsg);//批量提交
						}
						sqlSession.commit();
					}catch(Exception e){
						logger.error("{}", e);
					}finally{
						sqlSession.close();
					}
					return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
				}
			});
			smsStatusConsumer.start();
		} catch (Exception e) {

		}
	}
}
