package com.siloyou.jmsg.modules.sms.task.handler.listener;

import java.util.List;

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
import com.siloyou.jmsg.common.message.SmsSrMessage;
import com.siloyou.jmsg.modules.sms.entity.JmsgSmsSubmit;

@Service
public class SmsSubmitRespMsgListener implements MessageListenerConcurrently
{
    public static Logger logger = LoggerFactory.getLogger(SmsSubmitRespMsgListener.class);
    
    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context)
    {
        SqlSession sqlSession = SpringContextHolder.getBean(SqlSessionFactory.class).openSession(ExecutorType.BATCH, false);
        try
        {
            int num = 0;
            for (MessageExt message : msgs)
            {
                logger.info("msgid:{}, key:{}", message.getMsgId(), message.getKeys());
                SmsSrMessage smsSrMessage = (SmsSrMessage)FstObjectSerializeUtil.read(message.getBody());
                if (smsSrMessage != null)
                {
                    JmsgSmsSubmit jmsgSmsSubmit = new JmsgSmsSubmit();
                    jmsgSmsSubmit.setMsgid(smsSrMessage.getMsgid());
                    jmsgSmsSubmit.setResult(smsSrMessage.getResult());
                    jmsgSmsSubmit.setBizid(smsSrMessage.getMessage().getId());
                    jmsgSmsSubmit.setTaskid(smsSrMessage.getMessage().getTaskid());
                    jmsgSmsSubmit.setUserid(smsSrMessage.getMessage().getUserid());
                    jmsgSmsSubmit.setGatewayid(smsSrMessage.getMessage().getGateWayID());
                    jmsgSmsSubmit.setReserve(smsSrMessage.getReserve());
                    jmsgSmsSubmit.setPhone(smsSrMessage.getMessage().getPhone());
                    
                    sqlSession.insert("com.siloyou.jmsg.modules.sms.dao.JmsgSmsSubmitDao.insert", jmsgSmsSubmit);
                    
                    num++;
                    
                    if (num % 200 == 0)
                    {
                        sqlSession.commit();
                    }
                }
                else
                {
                    logger.error("msgid:{}, key:{}, 解析异常", message.getMsgId(), message.getKeys());
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
