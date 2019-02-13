package com.siloyou.jmsg.modules.sms.task.handler.listener;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.siloyou.core.common.utils.FstObjectSerializeUtil;
import com.siloyou.core.common.utils.SpringContextHolder;
@Service
public class SmsSendStatusMsgListener implements MessageListenerConcurrently{

	public static Logger logger = LoggerFactory.getLogger(SmsSendStatusMsgListener.class);
	
	@Override
	public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
		SqlSession sqlSession = SpringContextHolder.getBean(SqlSessionFactory.class).openSession(ExecutorType.BATCH,false);
		long l = System.currentTimeMillis();
		logger.info("入库修改短信发送状态开始==============");
		try{
			int index=0;
			for (MessageExt msg : msgs) {
				@SuppressWarnings("unchecked")
				Map<String,String> param = (Map<String,String>)FstObjectSerializeUtil.read(msg.getBody());
				index++;
				sqlSession.update("com.siloyou.jmsg.modules.sms.dao.JmsgSmsSendDao.batchUpdate", param);
				if(index % 200 ==0){
					sqlSession.commit();
				}
			}
			sqlSession.commit();
			logger.info("入库修改短信发送状态结束==============耗时:"+(System.currentTimeMillis()-l)/1000);
		}catch(Exception e){
			logger.error("{}", e);
		}finally{
			if(sqlSession!=null){
				sqlSession.close();
			}
		}
		
		return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
	}

}
