package com.siloyou.jmsg.modules.sms.listen;

import java.util.Date;
import java.util.List;

import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;
import com.siloyou.jmsg.common.util.FstObjectSerializeUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.marre.sms.SmsAlphabet;
import org.marre.sms.SmsMsgClass;
import org.marre.sms.SmsTextMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.siloyou.jmsg.common.message.SmsMtMessage;
import com.siloyou.jmsg.common.util.TableNameUtil;
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
//public class SmsMTBATCHListener implements MessageListenerConcurrently
public class SmsMTBATCHListener implements MessageListener
{
    private Logger logger = LoggerFactory.getLogger(SmsMTBATCHListener.class);

    @Autowired
    SqlSessionFactory sqlSessionFactory;

    @Override
    public Action consume(Message message, ConsumeContext consumeContext) {
        SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH, false);
        SmsMtMessage smsMtMessage = null;
        JmsgSmsSend jmsgSmsSend;
        int payCount;
        try
        {
            System.out.println("入库6666666666666666666666666666666666666");
            long startTime = System.currentTimeMillis();
            logger.info("storage=> topic:{}, tags:{}, msgid:{}, key:{}",
                    message.getTopic(), message.getTag(), message.getMsgID(), message.getKey());
            byte[] body = message.getBody();
            smsMtMessage = (SmsMtMessage) FstObjectSerializeUtil.read(message.getBody());
//            smsMtMessage = MessageExtUtil.convertMessageExt(SmsMtMessage.class, message);
            if (null != smsMtMessage && StringUtils.equals(smsMtMessage.getWapUrl(),"LOW"))
            {
                payCount = findPayCount(smsMtMessage.getMsgContent());//扣费条数
                jmsgSmsSend = new JmsgSmsSend();
                jmsgSmsSend.setId(smsMtMessage.getId());
                jmsgSmsSend.setDataId("99");
                jmsgSmsSend.setTaskId(smsMtMessage.getTaskid());//任务我ID
                jmsgSmsSend.setPhone(smsMtMessage.getPhone());//手机号码
                jmsgSmsSend.setSmsContent(smsMtMessage.getMsgContent());//短信内容
                jmsgSmsSend.setSmsType(payCount >1 ?"2":"1");//短信类型
                jmsgSmsSend.setPayCount(payCount);//扣费条数
                jmsgSmsSend.setUserId(smsMtMessage.getUserid());
                jmsgSmsSend.setChannelCode(smsMtMessage.getGateWayID());//通道代码
                jmsgSmsSend.setSpNumber(smsMtMessage.getSpNumber());//接入号
                jmsgSmsSend.setPhoneType(smsMtMessage.getPhoneType());//运营商
                jmsgSmsSend.setAreaCode(smsMtMessage.getCityCode());//省市代码
                jmsgSmsSend.setPayType(smsMtMessage.getPayType());//扣费方式
                jmsgSmsSend.setPayStatus("1");//扣费状态
                jmsgSmsSend.setPushFlag(smsMtMessage.getUserReportNotify());//推送标识
                jmsgSmsSend.setCompanyId("11");
                jmsgSmsSend.setSendStatus(smsMtMessage.getSendStatus());//发送状态
                jmsgSmsSend.setSubmitMode(smsMtMessage.getUserReportGateWayID());//提交方式 WEB,API
                jmsgSmsSend.setTopic(message.getTag());//发送队列
                jmsgSmsSend.setReportGatewayId(smsMtMessage.getUserReportGateWayID());
                jmsgSmsSend.setMsgid(message.getMsgID());
                jmsgSmsSend.setSendDatetime(new Date());
                jmsgSmsSend.setCustomerOrderId(smsMtMessage.getCstmOrderID());
                jmsgSmsSend.setTableName("jmsg_sms_send_" + TableNameUtil.getTableIndex(smsMtMessage.getId()));

                sqlSession.insert("com.sanerzone.jmsg.dao.JmsgSmsSendDao.insert", jmsgSmsSend);
            }
            else
            {
                logger.info("storage=> 解析异常: topic:{}, tags:{}, msgid:{}, key:{}",
                        message.getTopic(), message.getTag(), message.getMsgID(), message.getKey());
            }
            sqlSession.commit();

            logger.info("storage=>入库结束,条数:{}, 耗时:{}ms", 1, System.currentTimeMillis() - startTime);
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
//        SmsMtMessage smsMtMessage = null;
//        JmsgSmsSend jmsgSmsSend;
//        int payCount;
//        try
//        {
//            System.out.println("入库6666666666666666666666666666666666666");
//        	long startTime = System.currentTimeMillis();
//            for (MessageExt msg : msgs)
//            {
//                logger.info("storage=> topic:{}, tags:{}, msgid:{}, key:{}",
//                    msg.getTopic(), msg.getTags(), msg.getMsgId(), msg.getKeys());
//
//                smsMtMessage = MessageExtUtil.convertMessageExt(SmsMtMessage.class, msg);
//                if (null != smsMtMessage && StringUtils.equals(smsMtMessage.getWapUrl(),"LOW"))
//                {
//                    payCount = findPayCount(smsMtMessage.getMsgContent());//扣费条数
//                    jmsgSmsSend = new JmsgSmsSend();
//                    jmsgSmsSend.setId(smsMtMessage.getId());
//                    jmsgSmsSend.setDataId("99");
//                    jmsgSmsSend.setTaskId(smsMtMessage.getTaskid());//任务我ID
//                    jmsgSmsSend.setPhone(smsMtMessage.getPhone());//手机号码
//                    jmsgSmsSend.setSmsContent(smsMtMessage.getMsgContent());//短信内容
//                    jmsgSmsSend.setSmsType(payCount >1 ?"2":"1");//短信类型
//                    jmsgSmsSend.setPayCount(payCount);//扣费条数
//                    jmsgSmsSend.setUserId(smsMtMessage.getUserid());
//                    jmsgSmsSend.setChannelCode(smsMtMessage.getGateWayID());//通道代码
//                    jmsgSmsSend.setSpNumber(smsMtMessage.getSpNumber());//接入号
//                    jmsgSmsSend.setPhoneType(smsMtMessage.getPhoneType());//运营商
//                    jmsgSmsSend.setAreaCode(smsMtMessage.getCityCode());//省市代码
//                    jmsgSmsSend.setPayType(smsMtMessage.getPayType());//扣费方式
//                    jmsgSmsSend.setPayStatus("1");//扣费状态
//                    jmsgSmsSend.setPushFlag(smsMtMessage.getUserReportNotify());//推送标识
//                    jmsgSmsSend.setCompanyId("11");
//                    jmsgSmsSend.setSendStatus(smsMtMessage.getSendStatus());//发送状态
//                    jmsgSmsSend.setSubmitMode(smsMtMessage.getUserReportGateWayID());//提交方式 WEB,API
//                    jmsgSmsSend.setTopic(msg.getTags());//发送队列
//                    jmsgSmsSend.setReportGatewayId(smsMtMessage.getUserReportGateWayID());
//                    jmsgSmsSend.setMsgid(msg.getMsgId());
//                    jmsgSmsSend.setSendDatetime(new Date());
//                    jmsgSmsSend.setCustomerOrderId(smsMtMessage.getCstmOrderID());
//                    jmsgSmsSend.setTableName("jmsg_sms_send_" + TableNameUtil.getTableIndex(smsMtMessage.getId()));
//
//                    sqlSession.insert("com.sanerzone.jmsg.dao.JmsgSmsSendDao.insert", jmsgSmsSend);
//                }
//                else
//                {
//                    logger.info("storage=> 解析异常: topic:{}, tags:{}, msgid:{}, key:{}",
//                        msg.getTopic(), msg.getTags(), msg.getMsgId(), msg.getKeys());
//                }
//            }
//            sqlSession.commit();
//
//            logger.info("storage=>入库结束,条数:{}, 耗时:{}ms", msgs.size(), System.currentTimeMillis() - startTime);
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
//    }
//
    /**
     * 获取扣费条数
     * @param smsContent
     * @return
     * @see [类、类#方法、类#成员]
     */
    public int findPayCount(String smsContent){
        SmsTextMessage sms = null;
        if(haswidthChar(smsContent)) {
            sms = new SmsTextMessage(smsContent,SmsAlphabet.UCS2, SmsMsgClass.CLASS_UNKNOWN);
        } else {
            sms = new SmsTextMessage(smsContent,SmsAlphabet.LATIN1, SmsMsgClass.CLASS_UNKNOWN);
        }
        return sms.getPdus().length;
    }

    public static boolean haswidthChar(String content) {
        if (StringUtils.isEmpty(content))
            return false;

        byte[] bytes = content.getBytes();
        for (int i = 0; i < bytes.length; i++) {
            // 判断最高位是否为1
            if ((bytes[i] & (byte) 0x80) == (byte) 0x80) {
                return true;
            }
        }
        return false;
    }


}
