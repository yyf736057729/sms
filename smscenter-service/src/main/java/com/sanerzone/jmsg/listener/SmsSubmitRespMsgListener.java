package com.sanerzone.jmsg.listener;

import java.util.List;

import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.sanerzone.common.modules.TableNameUtil;
import com.sanerzone.common.modules.smscenter.utils.JmsgCacheKeys;
import com.sanerzone.common.support.utils.FstObjectSerializeUtil;
import com.sanerzone.jmsg.entity.JmsgSmsSubmit;
import com.sanerzone.jmsg.util.MessageExtUtil;
import com.sanerzone.smscenter.utils.MQUtils;
import com.siloyou.jmsg.common.message.SmsSrMessage;

@Service
//public class SmsSubmitRespMsgListener implements MessageListenerConcurrently MessageListener
public class SmsSubmitRespMsgListener implements MessageListener
{
    public static Logger logger = LoggerFactory.getLogger(SmsSubmitRespMsgListener.class);
    
    @Autowired
    private MQUtils mQUtils;
    
    @Autowired
    SqlSessionFactory sqlSessionFactory ;

    @Override
    public Action consume(Message message, ConsumeContext consumeContext) {
        SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH, false);
        SmsSrMessage smsSrMessage;
        try
        {
            logger.info("msgid:{}, key:{}", message.getMsgID(), message.getKey());
            smsSrMessage = (SmsSrMessage)FstObjectSerializeUtil.read(message.getBody());
//            smsSrMessage = MessageExtUtil.convertMessageExt(SmsSrMessage.class, message);

            if (smsSrMessage != null && smsSrMessage.getMessage() != null)
            {
                // smsSrMessage.getMessage()为空需要输出日志
                JmsgSmsSubmit jmsgSmsSubmit = new JmsgSmsSubmit();
                jmsgSmsSubmit.setMsgid(smsSrMessage.getMsgid());
                jmsgSmsSubmit.setResult(smsSrMessage.getResult());
                jmsgSmsSubmit.setBizid(smsSrMessage.getMessage().getId());
                jmsgSmsSubmit.setTaskid(smsSrMessage.getMessage().getTaskid());
                jmsgSmsSubmit.setUserid(smsSrMessage.getMessage().getUserid());
                jmsgSmsSubmit.setGatewayid(smsSrMessage.getMessage().getGateWayID());
                jmsgSmsSubmit.setReserve(smsSrMessage.getReserve());
                jmsgSmsSubmit.setPhone(smsSrMessage.getMessage().getPhone());
//                    jmsgSmsSubmit.setTableName("jmsg_sms_submit_" +  TableNameUtil.getTableIndex(smsSrMessage.getMessage().getId()));
                jmsgSmsSubmit.setTableName("jmsg_sms_submit_" +  TableNameUtil.getTableIndex(smsSrMessage.getMsgid()));
                sqlSession.insert("com.sanerzone.jmsg.dao.JmsgSmsSubmitDao.insert", jmsgSmsSubmit);
            }
            else
            {
                logger.error("msgid:{}, key:{}, 解析异常", message.getMsgID(), message.getKey());
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
        return Action.CommitMessage;
    }
//    @Override
//    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context)
//    {
//        SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH, false);
//        SmsSrMessage smsSrMessage;
//        try
//        {
//            for (MessageExt message : msgs)
//            {
//                logger.info("msgid:{}, key:{}", message.getMsgId(), message.getKeys());
//                //smsSrMessage = (SmsSrMessage)FstObjectSerializeUtil.read(message.getBody());
//                smsSrMessage = MessageExtUtil.convertMessageExt(SmsSrMessage.class, message);
//
//                if (smsSrMessage != null && smsSrMessage.getMessage() != null)
//                {
//                	// smsSrMessage.getMessage()为空需要输出日志
//                    JmsgSmsSubmit jmsgSmsSubmit = new JmsgSmsSubmit();
//                    jmsgSmsSubmit.setMsgid(smsSrMessage.getMsgid());
//                    jmsgSmsSubmit.setResult(smsSrMessage.getResult());
//                    jmsgSmsSubmit.setBizid(smsSrMessage.getMessage().getId());
//                    jmsgSmsSubmit.setTaskid(smsSrMessage.getMessage().getTaskid());
//                    jmsgSmsSubmit.setUserid(smsSrMessage.getMessage().getUserid());
//                    jmsgSmsSubmit.setGatewayid(smsSrMessage.getMessage().getGateWayID());
//                    jmsgSmsSubmit.setReserve(smsSrMessage.getReserve());
//                    jmsgSmsSubmit.setPhone(smsSrMessage.getMessage().getPhone());
////                    jmsgSmsSubmit.setTableName("jmsg_sms_submit_" +  TableNameUtil.getTableIndex(smsSrMessage.getMessage().getId()));
//                    jmsgSmsSubmit.setTableName("jmsg_sms_submit_" +  TableNameUtil.getTableIndex(smsSrMessage.getMsgid()));
//                    sqlSession.insert("com.sanerzone.jmsg.dao.JmsgSmsSubmitDao.insert", jmsgSmsSubmit);
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
//        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
//    }


}
