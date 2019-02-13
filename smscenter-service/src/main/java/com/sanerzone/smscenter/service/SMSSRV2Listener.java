package com.sanerzone.smscenter.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;
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
import com.sanerzone.common.support.utils.StringUtils;
import com.sanerzone.smscenter.entity.SMSSRMessage;
import com.sanerzone.smscenter.task.SmsSRQueueFactory;

@Service
public class SMSSRV2Listener implements /*MessageListenerConcurrently*/ MessageListener {

	public static Logger logger = LoggerFactory.getLogger(SMSSRV2Listener.class);

    @Override
    public Action consume(Message message, ConsumeContext consumeContext) {
        SqlSession sqlSession = SpringContextHolder.getBean(SqlSessionFactory.class).openSession(ExecutorType.BATCH, false);
        try
        {
            int num = 0;
            logger.info("msgid:{}, key:{}", message.getMsgID(), message.getKey());
            SMSSRMessage smsSrMessage = (SMSSRMessage)FstObjectSerializeUtil.read(message.getBody());
            if (smsSrMessage != null)
            {
                if( StringUtils.equals(smsSrMessage.getResult(), "0") )     return Action.CommitMessage;;

                Map<String,Object> map = Maps.newHashMap();
                map.put("id", smsSrMessage.getMessage().getId());
                map.put("gatewayStatus", smsSrMessage.getResult());
                map.put("submitTime", new Date(smsSrMessage.getMessage().getSubmitTime()));
                map.put("reportTime", new Date(smsSrMessage.getMessage().getReportTime()));
                sqlSession.update("com.sanerzone.smscenter.modules.smsv2.dao.SmsSendDao.batchUpdateGatewayStatus", map);

                int count = sqlSession.update("com.sanerzone.smscenter.modules.smsv2.dao.SmsSendDao.batchUpdateGatewayStatus", map);

                if (count == 0)
                {
                    SmsSRQueueFactory.getSmsSendQueue().put(smsSrMessage);
                }

                num++;

                if (num % 200 == 0)
                {
                    sqlSession.commit();
                }
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
//	@Override
//	public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
//		SqlSession sqlSession = SpringContextHolder.getBean(SqlSessionFactory.class).openSession(ExecutorType.BATCH, false);
//        try
//        {
//            int num = 0;
//
//            for (MessageExt message : msgs)
//            {
//                logger.info("msgid:{}, key:{}", message.getMsgId(), message.getKeys());
//                SMSSRMessage smsSrMessage = (SMSSRMessage)FstObjectSerializeUtil.read(message.getBody());
//                if (smsSrMessage != null)
//                {
//                	if( StringUtils.equals(smsSrMessage.getResult(), "0") ) continue;
//
//                    Map<String,Object> map = Maps.newHashMap();
//                    map.put("id", smsSrMessage.getMessage().getId());
//                    map.put("gatewayStatus", smsSrMessage.getResult());
//                    map.put("submitTime", new Date(smsSrMessage.getMessage().getSubmitTime()));
//                    map.put("reportTime", new Date(smsSrMessage.getMessage().getReportTime()));
//                    sqlSession.update("com.sanerzone.smscenter.modules.smsv2.dao.SmsSendDao.batchUpdateGatewayStatus", map);
//
//                    int count = sqlSession.update("com.sanerzone.smscenter.modules.smsv2.dao.SmsSendDao.batchUpdateGatewayStatus", map);
//
//					if (count == 0)
//					{
//						SmsSRQueueFactory.getSmsSendQueue().put(smsSrMessage);
//					}
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


}
