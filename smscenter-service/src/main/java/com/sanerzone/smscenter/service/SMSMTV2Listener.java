package com.sanerzone.smscenter.service;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.sanerzone.common.support.utils.FstObjectSerializeUtil;
import com.sanerzone.common.support.utils.SpringContextHolder;
import com.sanerzone.smscenter.entity.SMSMTMessage;

@Service
public class SMSMTV2Listener implements MessageListenerConcurrently {

	public static Logger logger = LoggerFactory.getLogger(SMSMTV2Listener.class);

	@Override
	public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
		SqlSession sqlSession = SpringContextHolder.getBean(SqlSessionFactory.class).openSession();
		
		int index =0;
		SMSMTMessage smsMTMessage = null;
			
		try {
			for (MessageExt msg : msgs) 
			{
				logger.info("umt listener recv message: topic:{}, tags:{}, msgid:{}, key:{}", msg.getTopic(), msg.getTags(),
						msg.getMsgId(), msg.getKeys());
				
				smsMTMessage = (SMSMTMessage)FstObjectSerializeUtil.read(msg.getBody());
			
				if (null != smsMTMessage)
                {
					index++;
                    sqlSession.insert("com.sanerzone.smscenter.modules.smsv2.dao.SmsSendDao.insert", smsMTMessage);
                    if(index % 500 == 0) {
                        sqlSession.commit();
                    }
                }
				else
				{
					logger.info("mt listener 解析异常: topic:{}, tags:{}, msgid:{}, key:{}",
	                        msg.getTopic(), msg.getTags(), msg.getMsgId(), msg.getKeys());
				}
			}
			
			sqlSession.commit();
		} 
		catch (Exception e) 
		{
			logger.error("{}", e);
		}
		finally
		{
			if (sqlSession != null)
            {
                sqlSession.close();
            }
		}
			
		return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
	}

}
