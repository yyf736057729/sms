//package com.sanerzone.smscenter.service;
//
//import java.util.Date;
//import java.util.List;
//
//import org.apache.ibatis.session.ExecutorType;
//import org.apache.ibatis.session.SqlSession;
//import org.apache.ibatis.session.SqlSessionFactory;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Service;
//
//import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
//import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
//import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
//import com.alibaba.rocketmq.common.message.MessageExt;
//import com.sanerzone.common.support.utils.FstObjectSerializeUtil;
//import com.sanerzone.common.support.utils.SpringContextHolder;
//import com.sanerzone.smscenter.entity.SMSSRMessage;
//import com.sanerzone.smscenter.modules.smsv2.entity.SmsSubmit;
//
//@Service
//public class SMSSRV2SubmitListener implements MessageListenerConcurrently {
//
//	public static Logger logger = LoggerFactory.getLogger(SMSSRV2SubmitListener.class);
//
//	@Override
//	public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
//		SqlSession sqlSession = SpringContextHolder.getBean(SqlSessionFactory.class).openSession(ExecutorType.BATCH, false);
//        try
//        {
//            int num = 0;
//            SmsSubmit smsSubmit = null;
//            
//            for (MessageExt message : msgs)
//            {
//                logger.info("msgid:{}, key:{}", message.getMsgId(), message.getKeys());
//                SMSSRMessage smsSrMessage = (SMSSRMessage)FstObjectSerializeUtil.read(message.getBody());
//                if (smsSrMessage != null)
//                {
//                	smsSubmit = new SmsSubmit();
//                    smsSubmit.setMsgid(smsSrMessage.getMsgid());
//                    smsSubmit.setBizid(smsSrMessage.getMessage().getId());
//                    smsSubmit.setTaskid(smsSrMessage.getMessage().getTaskid());
//                    smsSubmit.setResult(smsSrMessage.getResult());
//                    smsSubmit.setUserid(smsSrMessage.getMessage().getUserid());
//                    smsSubmit.setAccId(smsSrMessage.getMessage().getAccId());
//                    smsSubmit.setGatewayId(smsSrMessage.getMessage().getGatewayId());
//                    smsSubmit.setReserve(smsSrMessage.getReserve());
//                    smsSubmit.setPhone(smsSrMessage.getMessage().getPhone());
//                    smsSubmit.setCreatetime(new Date());
//                    
//                    sqlSession.insert("com.sanerzone.smscenter.modules.smsv2.dao.SmsSubmitDao.insert", smsSubmit);
//                    
//                    num++;
//                    
//                    if (num % 200 == 0)
//                    {
//                        sqlSession.commit();
//                    }
//                }
//                else
//                {
//                    logger.error("msgid:{}, key:{}, 解析异常", message.getMsgId(), message.getKeys());
//                }
//            }
//            sqlSession.commit();
//        }
//        catch (Exception e)
//        {
//            logger.error("{}", e);
//        }
//        finally
//        {
//            if (sqlSession != null)
//            {
//                sqlSession.close();
//            }
//        }
//        
//        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
//	}
//
//}
