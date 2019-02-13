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
import com.siloyou.jmsg.modules.mms.entity.JmsgMmsDownload;

@Service
public class MmsDownloadListener implements MessageListenerConcurrently
{
    public static Logger logger = LoggerFactory.getLogger(MmsDownloadListener.class);
    
    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context)
    {
        SqlSession sqlSession = SpringContextHolder.getBean(SqlSessionFactory.class).openSession(ExecutorType.BATCH, false);
        try
        {
            int num = 0;
            for (MessageExt message : msgs)
            {
                logger.info("MmsDownloadListener msgid:{}, key:{}", message.getMsgId(), message.getKeys());
                JmsgMmsDownload jmsgMmsDownload = (JmsgMmsDownload)FstObjectSerializeUtil.read(message.getBody());
                if (jmsgMmsDownload != null)
                {
                    sqlSession.insert("com.siloyou.jmsg.modules.mms.dao.JmsgMmsDownloadDao.insert", jmsgMmsDownload);
                    
                    num++;
                    
                    if (num % 200 == 0)
                    {
                        sqlSession.commit();
                    }
                }
                else
                {
                    logger.error("MmsDownloadListener msgid:{}, key:{}, 解析异常", message.getMsgId(), message.getKeys());
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
