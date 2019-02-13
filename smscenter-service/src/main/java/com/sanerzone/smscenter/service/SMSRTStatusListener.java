package com.sanerzone.smscenter.service;

import java.util.Date;
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
import com.google.common.collect.Maps;
import com.sanerzone.common.support.utils.FstObjectSerializeUtil;
import com.sanerzone.common.support.utils.SpringContextHolder;
import com.sanerzone.smscenter.entity.SMSRTMessage;
import com.sanerzone.smscenter.task.SmsRTQueueFactory;

@Service
public class SMSRTStatusListener implements MessageListenerConcurrently {

	public static Logger logger = LoggerFactory.getLogger(SMSRTStatusListener.class);
	
	@Override
	public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs,ConsumeConcurrentlyContext context) {
		SqlSession sqlSession = SpringContextHolder.getBean(SqlSessionFactory.class).openSession(ExecutorType.BATCH, false);
		try {
			int index = 0;
			SMSRTMessage smsRTMessage = null;
			
			for (MessageExt msg : msgs) {
				smsRTMessage = (SMSRTMessage) FstObjectSerializeUtil.read(msg.getBody());
				
				if(smsRTMessage.getSmsMt() != null){
					Map<String,Object> map = Maps.newHashMap();
					map.put("id", smsRTMessage.getSmsMt().getId());
					if("DELIVRD".equals(smsRTMessage.getStat())){//成功
						map.put("reportStatus", "T100");
					}else{
						map.put("reportStatus", "F1100" + smsRTMessage.getStat());//失败
					}
					map.put("submitTime", new Date(smsRTMessage.getSmsMt().getSubmitTime()));
                    map.put("reportTime", new Date(smsRTMessage.getSmsMt().getReportTime()));
                    
					int num = sqlSession.update("com.sanerzone.smscenter.modules.smsv2.dao.SmsSendDao.batchUpdateGatewayStatus", map);
					
					if (num == 0)
					{
						SmsRTQueueFactory.getSmsSendQueue().put(smsRTMessage);
					}
					
					index++;
				}

				index++;
				if (index % 200 == 0) {
					sqlSession.commit();
				}
			}
			sqlSession.commit();

		} catch (Exception e) {
			logger.error("{}", e);
		} finally {
			if (sqlSession != null) {
				sqlSession.close();
			}
		}
		return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
	}

}
