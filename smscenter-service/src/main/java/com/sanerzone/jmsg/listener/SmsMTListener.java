package com.sanerzone.jmsg.listener;

import java.util.Date;
import java.util.List;

import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;
import org.apache.commons.lang3.time.DateFormatUtils;
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

import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.sanerzone.common.modules.TableNameUtil;
import com.sanerzone.common.modules.smscenter.utils.JmsgCacheKeys;
import com.sanerzone.common.support.utils.DateUtils;
import com.sanerzone.common.support.utils.FstObjectSerializeUtil;
import com.sanerzone.common.support.utils.StringUtils;
import com.sanerzone.jmsg.entity.JmsgSmsSend;
import com.sanerzone.jmsg.util.MessageExtUtil;
import com.sanerzone.smscenter.utils.MQUtils;
import com.siloyou.jmsg.common.message.SmsMtMessage;
import com.siloyou.jmsg.common.message.SmsRtMessage;


/**
 * 短信单发入库
 *
 * @author zhangjie
 * @version [版本号, 2016年11月10日]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
@Service
//public class SmsMTListener implements MessageListenerConcurrently
public class SmsMTListener implements MessageListener {
    public static Logger logger = LoggerFactory.getLogger(SmsMTListener.class);

    @Autowired
    SqlSessionFactory sqlSessionFactory;

    @Autowired
    public MQUtils mQUtils;

    @Override
    public Action consume(Message message, ConsumeContext consumeContext) {
        SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH, false);
        SmsMtMessage smsMtMessage = null;
        JmsgSmsSend jmsgSmsSend;
        try {
            long startTime = System.currentTimeMillis();
            SmsRtMessage smsRtMessage;
            logger.info("mt listener recv message: topic:{}, tags:{}, msgid:{}, key:{}",
                    message.getTopic(), message.getTag(), message.getMsgID(), message.getKey());

            smsMtMessage = (SmsMtMessage)FstObjectSerializeUtil.read(message.getBody());
//            smsMtMessage = MessageExtUtil.convertMessageExt(SmsMtMessage.class, message);

            if (null != smsMtMessage) {
//                	payCount = findPayCount(smsMtMessage.getMsgContent());//扣费条数
                jmsgSmsSend = new JmsgSmsSend();
                jmsgSmsSend.setId(smsMtMessage.getId());
                jmsgSmsSend.setDataId("99");
                jmsgSmsSend.setTaskId(smsMtMessage.getTaskid());//任务我ID
                jmsgSmsSend.setPhone(smsMtMessage.getPhone());//手机号码
                jmsgSmsSend.setSmsContent(smsMtMessage.getMsgContent());//短信内容
                jmsgSmsSend.setSmsType(smsMtMessage.getContentSize() > 1 ? "2" : "1");//短信类型
                jmsgSmsSend.setPayCount((int) smsMtMessage.getContentSize());//扣费条数
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

                smsRtMessage = new SmsRtMessage();
                smsRtMessage.setMsgid(message.getMsgID());
                smsRtMessage.setSrcTermID(smsMtMessage.getPhone());//业务处理失败的情况下没有发送号码
                smsRtMessage.setDestTermID(smsMtMessage.getPhone());
                smsRtMessage.setSubmitTime(DateUtils.getDate("yyMMddHHmm"));
                smsRtMessage.setDoneTime(DateUtils.getDate("yyMMddHHmm"));
                smsRtMessage.setStat(smsMtMessage.getSendStatus());
                smsRtMessage.setSmsMt(smsMtMessage);
                smsRtMessage.setSmscSequence("0");
                mQUtils.sendSmsMT(JmsgCacheKeys.getPushTopic(), smsMtMessage.getUserReportGateWayID(), smsMtMessage.getId(), FstObjectSerializeUtil.write(smsRtMessage));
            } else {
                logger.info("mt listener 解析异常: topic:{}, tags:{}, msgid:{}, key:{}",
                        message.getTopic(), message.getTag(), message.getMsgID(), message.getKey());
            }

            sqlSession.commit();

            logger.info("MT 发送结束,条数:{}, 耗时:{}ms", 1, System.currentTimeMillis() - startTime);
        } catch (Exception e) {
            logger.error("{}", e);
        } finally {
            if (sqlSession != null) {
                sqlSession.close();
            }
        }
        return Action.CommitMessage;
    }

//    @Override
//    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
//        SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH, false);
//        SmsMtMessage smsMtMessage = null;
//        JmsgSmsSend jmsgSmsSend;
////        int payCount;
//        try {
//            long startTime = System.currentTimeMillis();
//            SmsRtMessage smsRtMessage;
//            for (MessageExt msg : msgs) {
//                logger.info("mt listener recv message: topic:{}, tags:{}, msgid:{}, key:{}",
//                        msg.getTopic(), msg.getTags(), msg.getMsgId(), msg.getKeys());
//
//                //smsMtMessage = (SmsMtMessage)FstObjectSerializeUtil.read(msg.getBody());
//                smsMtMessage = MessageExtUtil.convertMessageExt(SmsMtMessage.class, msg);
//
//                if (null != smsMtMessage) {
////                	payCount = findPayCount(smsMtMessage.getMsgContent());//扣费条数
//                    jmsgSmsSend = new JmsgSmsSend();
//                    jmsgSmsSend.setId(smsMtMessage.getId());
//                    jmsgSmsSend.setDataId("99");
//                    jmsgSmsSend.setTaskId(smsMtMessage.getTaskid());//任务我ID
//                    jmsgSmsSend.setPhone(smsMtMessage.getPhone());//手机号码
//                    jmsgSmsSend.setSmsContent(smsMtMessage.getMsgContent());//短信内容
//                    jmsgSmsSend.setSmsType(smsMtMessage.getContentSize() > 1 ? "2" : "1");//短信类型
//                    jmsgSmsSend.setPayCount((int) smsMtMessage.getContentSize());//扣费条数
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
//
//                    jmsgSmsSend.setTableName("jmsg_sms_send_" + TableNameUtil.getTableIndex(smsMtMessage.getId()));
//                    sqlSession.insert("com.sanerzone.jmsg.dao.JmsgSmsSendDao.insert", jmsgSmsSend);
//
//                    smsRtMessage = new SmsRtMessage();
//                    smsRtMessage.setMsgid(msg.getMsgId());
//                    smsRtMessage.setSrcTermID(smsMtMessage.getPhone());//业务处理失败的情况下没有发送号码
//                    smsRtMessage.setDestTermID(smsMtMessage.getPhone());
//                    smsRtMessage.setSubmitTime(DateUtils.getDate("yyMMddHHmm"));
//                    smsRtMessage.setDoneTime(DateUtils.getDate("yyMMddHHmm"));
//                    smsRtMessage.setStat(smsMtMessage.getSendStatus());
//                    smsRtMessage.setSmsMt(smsMtMessage);
//                    smsRtMessage.setSmscSequence("0");
//                    mQUtils.sendSmsMT(JmsgCacheKeys.getPushTopic(), smsMtMessage.getUserReportGateWayID(), smsMtMessage.getId(), FstObjectSerializeUtil.write(smsRtMessage));
//                } else {
//                    logger.info("mt listener 解析异常: topic:{}, tags:{}, msgid:{}, key:{}",
//                            msg.getTopic(), msg.getTags(), msg.getMsgId(), msg.getKeys());
//                }
//            }
//            sqlSession.commit();
//
//            logger.info("MT 发送结束,条数:{}, 耗时:{}ms", msgs.size(), System.currentTimeMillis() - startTime);
//        } catch (Exception e) {
//            logger.error("{}", e);
//        } finally {
//            if (sqlSession != null) {
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
//    public int findPayCount(String smsContent){
//        SmsTextMessage sms = null;
//        if(StringUtils.haswidthChar(smsContent)) {
//            sms = new SmsTextMessage(smsContent,SmsAlphabet.UCS2, SmsMsgClass.CLASS_UNKNOWN);
//        } else {
//            sms = new SmsTextMessage(smsContent,SmsAlphabet.LATIN1, SmsMsgClass.CLASS_UNKNOWN);
//        }
//        return sms.getPdus().length;
//    }

}
