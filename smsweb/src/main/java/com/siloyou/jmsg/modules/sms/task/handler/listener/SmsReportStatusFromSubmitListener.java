package com.siloyou.jmsg.modules.sms.task.handler.listener;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
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
import com.google.common.collect.Maps;
import com.siloyou.core.common.utils.FstObjectSerializeUtil;
import com.siloyou.core.common.utils.SpringContextHolder;
import com.siloyou.jmsg.common.message.SmsRtMessage;
import com.siloyou.jmsg.common.message.SmsSrMessage;
import com.siloyou.jmsg.common.utils.MQUtils;

@Service
public class SmsReportStatusFromSubmitListener implements MessageListenerConcurrently
{
    public static Logger logger = LoggerFactory.getLogger(SmsReportStatusFromSubmitListener.class);
    
    @Autowired
    private MQUtils mQUtils;
    
    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context)
    {
        SqlSession sqlSession = SpringContextHolder.getBean(SqlSessionFactory.class).openSession(ExecutorType.BATCH, false);
        try
        {
            int num = 0;
            SmsRtMessage smsRtMessage = null;
            for (MessageExt message : msgs)
            {
                logger.info("msgid:{}, key:{}", message.getMsgId(), message.getKeys());
                SmsSrMessage smsSrMessage = (SmsSrMessage)FstObjectSerializeUtil.read(message.getBody());
                if (smsSrMessage != null)
                {
                	if( StringUtils.equals(smsSrMessage.getResult(), "0") ) continue;
                	
                    Map<String,String> map = Maps.newHashMap();
                    map.put("id", smsSrMessage.getMessage().getId());
                    map.put("reportStatus", smsSrMessage.getResult());
                    sqlSession.update("com.siloyou.jmsg.modules.sms.dao.JmsgSmsSendDao.batchUpdateReprotStatus", map);
                    
                    smsRtMessage = new SmsRtMessage();
                    smsRtMessage.setMsgid(smsSrMessage.getMsgid());
                    smsRtMessage.setDestTermID(smsSrMessage.getMessage().getPhone());
                    smsRtMessage.setDoneTime(formatDate(smsSrMessage.getMessage().getSubmitTime()));
                    smsRtMessage.setStat(smsSrMessage.getResult());
                    smsRtMessage.setSmsMt(smsSrMessage.getMessage());
                    
                    if("9".equals(smsSrMessage.getMessage().getUserReportNotify())){
                        mQUtils.pushSmsMQ(smsSrMessage.getMessage().getId(),smsSrMessage.getMessage().getUserReportGateWayID() ,FstObjectSerializeUtil.write(smsRtMessage));//推送短信状态报告
                    }
                    
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
    
    private String formatDate(Long longTime)
    {
        String time = DateFormatUtils.format(new Date(), "yyMMddHHmm");
        
        if (null != longTime)
        {
            time = DateFormatUtils.format(new Date(longTime), "yyMMddHHmm");
        }
        
        return time;
    }
}
