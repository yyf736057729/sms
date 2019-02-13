package com.siloyou.jmsg.gateway.task;


import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.siloyou.jmsg.common.message.SmsSrMessage;
import com.siloyou.jmsg.common.util.DateUtils;
import com.siloyou.jmsg.gateway.api.GateWayQueueFactory;
import com.siloyou.jmsg.modules.sms.dao.JmsgSmsSubmitDao;
import com.siloyou.jmsg.modules.sms.entity.JmsgSmsSubmit;

/**
 * 
 * <一句话功能简述>
 * <功能详细描述>
 * 
 * @author  zhangjie
 * @version  [版本号, 2016年8月28日]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
@Component
public class SmsSubmitTask
{
    public static Logger logger = LoggerFactory.getLogger(SmsSubmitTask.class);
    
    @Autowired
    private JmsgSmsSubmitDao JmsgSmsSubmitDao;
    
    @Autowired
    private SqlSessionFactory sqlSessionFactory;
    
    //@Scheduled(fixedDelay = 1000)
    public void runSmsSubmit()
    {
        if (!GateWayQueueFactory.getSubmitRespQueue().isEmpty())
        {
            SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH, false);
            
            int num = 0;
            
            long beginTime = System.currentTimeMillis();
            
            try
            {
                while (!GateWayQueueFactory.getSubmitRespQueue().isEmpty())
                {
                    SmsSrMessage smsSrMessage = (SmsSrMessage)GateWayQueueFactory.getSubmitRespQueue().take();
                    if (null != smsSrMessage)
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
                        
                        num ++;
                        
                        if (num % 200 == 0)
                        {
                            sqlSession.commit();
                        }
                    }
                }
                sqlSession.commit();
            }
            catch (InterruptedException e)
            {
                logger.error("发送结果入库异常：{}", e.getMessage());
            }
            finally
            {
                sqlSession.close();
            }
            
            long endTime = System.currentTimeMillis();
            
            logger.info("发送结果入库用时：{}", DateUtils.formatDateTime(endTime - beginTime));
        }
    }
}
