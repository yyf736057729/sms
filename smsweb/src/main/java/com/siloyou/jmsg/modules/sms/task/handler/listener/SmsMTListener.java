package com.siloyou.jmsg.modules.sms.task.handler.listener;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.marre.sms.SmsAlphabet;
import org.marre.sms.SmsMsgClass;
import org.marre.sms.SmsTextMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.siloyou.core.common.utils.FstObjectSerializeUtil;
import com.siloyou.core.common.utils.SpringContextHolder;
import com.siloyou.core.common.utils.StringUtils;
import com.siloyou.core.modules.sys.entity.User;
import com.siloyou.core.modules.sys.utils.UserUtils;
import com.siloyou.jmsg.common.message.SmsMtMessage;
import com.siloyou.jmsg.common.utils.CacheKeys;
import com.siloyou.jmsg.modules.sms.entity.JmsgSmsSend;


/**
 * 短信单发入库
 * 
 * @author  zhangjie
 * @version  [版本号, 2016年11月10日]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
@Service
public class SmsMTListener implements MessageListenerConcurrently
{
    public static Logger logger = LoggerFactory.getLogger(SmsMTListener.class);
    
    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context)
    {
        //SqlSession sqlSession = SpringContextHolder.getBean(SqlSessionFactory.class).openSession(ExecutorType.BATCH, false);
        SqlSession sqlSession = SpringContextHolder.getBean(SqlSessionFactory.class).openSession();
        SmsMtMessage smsMtMessage = null;
        int index =0;
        JmsgSmsSend jmsgSmsSend;
        User user = null;
        int payCount;
        String smsType;
        String payType;
        String payStatus;
        String sendStatus;
        
        try
        {
            for (MessageExt msg : msgs)
            {
                logger.info("mt listener recv message: topic:{}, tags:{}, msgid:{}, key:{}",
                    msg.getTopic(), msg.getTags(), msg.getMsgId(), msg.getKeys());
                
                smsMtMessage = (SmsMtMessage)FstObjectSerializeUtil.read(msg.getBody());
                
                if (null != smsMtMessage)
                {
                    // 只有通过接口提交的数据才执行入库；
                    if (!StringUtils.equals(smsMtMessage.getSourceFlag(), "API"))
                    {
                        continue;
                    }
                    
                    user = UserUtils.get(smsMtMessage.getUserid());//获取用户信息
                    payCount = findPayCount(smsMtMessage.getMsgContent());//扣费条数
                    smsType = payCount >1 ?"2":"1";//1:短短信 2:长短信
                    payType = user.getPayMode();//findPayType(userId);//扣费方式
                    payStatus = "1";//TODO 扣费状态
                    sendStatus = smsMtMessage.getSendStatus();
                    
                    jmsgSmsSend = new JmsgSmsSend();
                    jmsgSmsSend.setId(smsMtMessage.getId());
                    jmsgSmsSend.setDataId("99");
                    jmsgSmsSend.setTaskId(smsMtMessage.getTaskid());//任务我ID
                    jmsgSmsSend.setPhone(smsMtMessage.getPhone());//手机号码
                    jmsgSmsSend.setSmsContent(smsMtMessage.getMsgContent());//短信内容
                    jmsgSmsSend.setSmsType(smsType);//短信类型
                    jmsgSmsSend.setPayCount(payCount);//扣费条数
                    jmsgSmsSend.setUser(user);//用户ID,公司ID
                    jmsgSmsSend.setChannelCode(smsMtMessage.getGateWayID());//通道代码
                    jmsgSmsSend.setSpNumber(smsMtMessage.getSpNumber());//接入号
                    jmsgSmsSend.setPhoneType(smsMtMessage.getPhoneType());//运营商
                    jmsgSmsSend.setAreaCode(smsMtMessage.getCityCode());//省市代码
                    jmsgSmsSend.setPayType(payType);//扣费方式
                    jmsgSmsSend.setPayStatus(payStatus);//扣费状态
                    jmsgSmsSend.setPushFlag(smsMtMessage.getUserReportNotify());//推送标识
                    
                    if (StringUtils.equals(smsMtMessage.getSendStatus(), "P000"))
                    {
                        sendStatus = "T000";
                    }
                    jmsgSmsSend.setSendStatus(sendStatus);//发送状态
                    jmsgSmsSend.setSubmitMode(smsMtMessage.getUserReportGateWayID());//提交方式 WEB,API
                    jmsgSmsSend.setTopic(CacheKeys.getSmsSingleTopic());//发送队列
                    jmsgSmsSend.setReportGatewayId(smsMtMessage.getUserReportGateWayID());
                    jmsgSmsSend.setMsgid(msg.getMsgId());
                    jmsgSmsSend.setSendDatetime(new Date());
                    
                    index++;
                    sqlSession.insert("com.siloyou.jmsg.modules.sms.dao.JmsgSmsSendDao.insert", jmsgSmsSend);
                    /*if(index % 500 == 0) {
                        sqlSession.commit();
                    }*/
                }
                else
                {
                    logger.info("mt listener 解析异常: topic:{}, tags:{}, msgid:{}, key:{}",
                        msg.getTopic(), msg.getTags(), msg.getMsgId(), msg.getKeys());
                }
            }
//            sqlSession.commit();
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
    
    /**
     * 获取扣费条数
     * @param smsContent
     * @return
     * @see [类、类#方法、类#成员]
     */
    public int findPayCount(String smsContent){
        SmsTextMessage sms = null;
        if(StringUtils.haswidthChar(smsContent)) {
            sms = new SmsTextMessage(smsContent,SmsAlphabet.UCS2, SmsMsgClass.CLASS_UNKNOWN);
        } else {
            sms = new SmsTextMessage(smsContent,SmsAlphabet.LATIN1, SmsMsgClass.CLASS_UNKNOWN);
        }
        return sms.getPdus().length;
    }
    
}
