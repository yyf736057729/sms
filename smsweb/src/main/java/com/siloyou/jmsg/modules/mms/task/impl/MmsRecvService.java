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
import com.siloyou.core.common.utils.JedisClusterUtils;
import com.siloyou.core.common.utils.SpringContextHolder;
import com.siloyou.core.common.utils.StringUtils;

//@Service
//@Lazy(false)
public class MmsRecvService {
	public static Logger logger = LoggerFactory.getLogger(MmsRecvService.class);

	@PostConstruct
	public void init() {
		try {
			DefaultMQPushConsumer smsSubmitStatusConsumer = new DefaultMQPushConsumer("SmsSubmitStatusGroup");
			smsSubmitStatusConsumer.setNamesrvAddr(Global.getConfig("mms.send.url"));
			smsSubmitStatusConsumer.setInstanceName("smsSubmitStatusConsumer");
			smsSubmitStatusConsumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
			smsSubmitStatusConsumer.subscribe("SmsSubmitStatusTopic", "*");
			smsSubmitStatusConsumer.setConsumeMessageBatchMaxSize(500);
			smsSubmitStatusConsumer.registerMessageListener(new MessageListenerConcurrently() {
				@Override
				public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs,
						ConsumeConcurrentlyContext context) {
					SqlSession sqlSession = SpringContextHolder.getBean(SqlSessionFactory.class).openSession(ExecutorType.BATCH, false);
					try{
						for (MessageExt msg : msgs) {
							@SuppressWarnings("unchecked")
							Map<String,String> mmsMsg = (Map<String,String>)JsonMapper.fromJsonString(new String(msg.getBody()), Map.class);
							JedisClusterUtils.hset("mms_submit", msg.getKeys(), String.format("%s_%s", mmsMsg.get("taskid"), mmsMsg.get("bizid")));
							sqlSession.insert("com.siloyou.jmsg.modules.mms.dao.JmsgMmsSubmitDao.batchInsert", mmsMsg);//批量提交
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
			smsSubmitStatusConsumer.start();
			
			DefaultMQPushConsumer smsReportStatusConsumer = new DefaultMQPushConsumer("SmsReportStatusGroup");
			smsReportStatusConsumer.setNamesrvAddr(Global.getConfig("mms.send.url"));
			smsReportStatusConsumer.setInstanceName("smsReportStatusConsumer");
			smsReportStatusConsumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
			smsReportStatusConsumer.subscribe("SmsReportStatusTopic", "*");
			smsReportStatusConsumer.setConsumeMessageBatchMaxSize(500);
			smsReportStatusConsumer.registerMessageListener(new MessageListenerConcurrently() {
				@Override
				public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs,
						ConsumeConcurrentlyContext context) {
					SqlSession sqlSession = SpringContextHolder.getBean(SqlSessionFactory.class).openSession(ExecutorType.BATCH, false);
					try{
						for (MessageExt msg : msgs) {
							@SuppressWarnings("unchecked")
							Map<String,String> mmsMsg = (Map<String,String>)JsonMapper.fromJsonString(new String(msg.getBody()), Map.class);
							String mmsSubmit = JedisClusterUtils.hget("mms_submit", msg.getKeys());
							if(StringUtils.isNotBlank(mmsSubmit)) {
								String[] mmsSubmitFields = mmsSubmit.split("_");
								mmsMsg.put("taskid", mmsSubmitFields[0]);
								mmsMsg.put("bizid", mmsSubmitFields[1]);
								sqlSession.insert("com.siloyou.jmsg.modules.mms.dao.JmsgMmsReportDao.batchInsert", mmsMsg);
							} else {
								sqlSession.insert("com.siloyou.jmsg.modules.mms.dao.JmsgMmsReportDao.insertRetrySyncMmsReport", mmsMsg);
							}
							
						}
						sqlSession.commit();
					}catch(Exception e){
						logger.error("{}",e);
					}finally{
						sqlSession.close();
					}
					return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
				}
			});
			smsReportStatusConsumer.start();
		} catch (Exception e) {

		}
	}
}
