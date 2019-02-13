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
public class MmsDownloadRecvService {
	public static Logger logger = LoggerFactory.getLogger(MmsDownloadRecvService.class);
	
	@PostConstruct
	public void init() {
		try {
			DefaultMQPushConsumer smsDownloadConsumer = new DefaultMQPushConsumer("MmsDownloadStatusGroup");
			smsDownloadConsumer.setNamesrvAddr(Global.getConfig("mms.send.url"));
			smsDownloadConsumer.setInstanceName("mmsDownloadConsumer");
			smsDownloadConsumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
			smsDownloadConsumer.subscribe("MmsDownloadTopic", "*");
			smsDownloadConsumer.setConsumeMessageBatchMaxSize(500);
			smsDownloadConsumer.registerMessageListener(new MessageListenerConcurrently() {
				@Override
				public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs,
						ConsumeConcurrentlyContext context) {
					SqlSession sqlSession = SpringContextHolder.getBean(SqlSessionFactory.class).openSession(ExecutorType.BATCH, false);
					try {
						for (MessageExt msg : msgs) {
							@SuppressWarnings("unchecked")
							Map<String,String> mmsMsg = (Map<String,String>)JsonMapper.fromJsonString(new String(msg.getBody()), Map.class);
							sqlSession.insert("com.siloyou.jmsg.modules.mms.dao.JmsgMmsTaskDetailDao.updateReceive", mmsMsg);//批量提交
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
			smsDownloadConsumer.start();
		} catch (Exception e) {

		}
	}
}
