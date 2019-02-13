package com.sanerzone.jmsg.listener;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
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
import com.sanerzone.common.modules.TableNameUtil;
import com.sanerzone.common.modules.smscenter.utils.JmsgCacheKeys;
import com.sanerzone.common.support.utils.FstObjectSerializeUtil;
import com.sanerzone.jmsg.entity.JmsgSmsSubmit;
import com.sanerzone.jmsg.util.MessageExtUtil;
import com.sanerzone.smscenter.utils.MQUtils;
import com.siloyou.jmsg.common.message.SmsRtMessage;
import com.siloyou.jmsg.common.message.SmsSrMessage;

@Service
public class SmsReportStatusFromSubmitListener implements /*MessageListenerConcurrently*/ MessageListener {
    public static Logger logger = LoggerFactory.getLogger(SmsReportStatusFromSubmitListener.class);

    @Autowired
    private MQUtils mQUtils;

    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    @Override
    public Action consume(Message message, ConsumeContext consumeContext) {
        //同步更新，通过更新影响行数来决定是否进入重试表
        SqlSession sqlSession = sqlSessionFactory.openSession();
        SmsSrMessage smsSrMessage;
        try {
            SmsRtMessage smsRtMessage = null;
            logger.info("msgid:{}, key:{}", message.getMsgID(), message.getKey());
            smsSrMessage = (SmsSrMessage)FstObjectSerializeUtil.read(message.getBody());
//            smsSrMessage = MessageExtUtil.convertMessageExt(SmsSrMessage.class, message);

            if (smsSrMessage != null && smsSrMessage.getMessage() != null) {
                if (StringUtils.equals(smsSrMessage.getResult(), "0"))
                    return Action.CommitMessage;

                Map<String, String> map = Maps.newHashMap();
                map.put("id", smsSrMessage.getMessage().getId());
                // taskid + phone
                map.put("reportStatus", smsSrMessage.getResult());
                map.put("tableName", "jmsg_sms_send_" + TableNameUtil.getTableIndex(smsSrMessage.getMessage().getId()));
                int update = sqlSession.update("com.sanerzone.jmsg.dao.JmsgSmsSendDao.batchUpdateReprotStatus", map);

                // update=0,需要进入重试库，通过定时任务扫表后继续更新，量比较小（也可以用批量提交后的）
                if (update == 0) {
                    JmsgSmsSubmit jmsgSmsSubmit = new JmsgSmsSubmit();
                    jmsgSmsSubmit.setMsgid(smsSrMessage.getMsgid());
                    jmsgSmsSubmit.setResult(smsSrMessage.getResult());
                    jmsgSmsSubmit.setBizid(smsSrMessage.getMessage().getId());
                    jmsgSmsSubmit.setTaskid(smsSrMessage.getMessage().getTaskid());
                    jmsgSmsSubmit.setUserid(smsSrMessage.getMessage().getUserid());
                    jmsgSmsSubmit.setGatewayid(smsSrMessage.getMessage().getGateWayID());
                    jmsgSmsSubmit.setReserve(smsSrMessage.getReserve());
                    jmsgSmsSubmit.setPhone(smsSrMessage.getMessage().getPhone());
                    jmsgSmsSubmit.setTableName("jmsg_sms_submit_sync");

                    sqlSession.insert("com.sanerzone.jmsg.dao.JmsgSmsSubmitDao.insert", jmsgSmsSubmit);
                }

                smsRtMessage = new SmsRtMessage();
                smsRtMessage.setMsgid(smsSrMessage.getMsgid());
                smsRtMessage.setDestTermID(smsSrMessage.getMessage().getPhone());
                smsRtMessage.setDoneTime(formatDate(smsSrMessage.getMessage().getSubmitTime()));
                smsRtMessage.setStat(smsSrMessage.getResult());
                smsRtMessage.setSmsMt(smsSrMessage.getMessage());

                if (StringUtils.isBlank(smsRtMessage.getSrcTermID())) {
                    smsRtMessage.setSrcTermID(smsSrMessage.getMessage().getPhone());
                }

                if ("9".equals(smsSrMessage.getMessage().getUserReportNotify())) {
//                    mQUtils.sendSmsMT(JmsgCacheKeys.getPushTopic(), smsRtMessage.getSmsMt().getUserReportGateWayID(), smsRtMessage.getSmsMt().getId(),FstObjectSerializeUtil.write(smsRtMessage));
                    mQUtils.sendSmsMT(JmsgCacheKeys.getPushTopic(), smsRtMessage.getSmsMt().getUserReportGateWayID(),
                            smsRtMessage.getSmsMt().getId(), FstObjectSerializeUtil.write(smsRtMessage));
                }

            } else {
                logger.error("msgid:{}, key:{}, 解析异常", message.getMsgID(), message.getKey());
            }
            sqlSession.commit();
//            List<BatchResult> batchResult = sqlSession.flushStatements();
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
//    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context)
//    {
//    	//同步更新，通过更新影响行数来决定是否进入重试表
//        SqlSession sqlSession = sqlSessionFactory.openSession();
//        SmsSrMessage smsSrMessage;
//        try
//        {
//            SmsRtMessage smsRtMessage = null;
//            for (MessageExt message : msgs)
//            {
//                logger.info("msgid:{}, key:{}", message.getMsgId(), message.getKeys());
//                //smsSrMessage = (SmsSrMessage)FstObjectSerializeUtil.read(message.getBody());
//                smsSrMessage = MessageExtUtil.convertMessageExt(SmsSrMessage.class, message);
//
//                if (smsSrMessage != null && smsSrMessage.getMessage() != null)
//                {
//                	if( StringUtils.equals(smsSrMessage.getResult(), "0") ) continue;
//
//                    Map<String,String> map = Maps.newHashMap();
//                    map.put("id", smsSrMessage.getMessage().getId());
//                    // taskid + phone
//                    map.put("reportStatus", smsSrMessage.getResult());
//                    map.put("tableName", "jmsg_sms_send_" + TableNameUtil.getTableIndex(smsSrMessage.getMessage().getId()));
//                    int update = sqlSession.update("com.sanerzone.jmsg.dao.JmsgSmsSendDao.batchUpdateReprotStatus", map);
//
//                    // update=0,需要进入重试库，通过定时任务扫表后继续更新，量比较小（也可以用批量提交后的）
//                    if (update == 0)
//                    {
//                    	JmsgSmsSubmit jmsgSmsSubmit = new JmsgSmsSubmit();
//                        jmsgSmsSubmit.setMsgid(smsSrMessage.getMsgid());
//                        jmsgSmsSubmit.setResult(smsSrMessage.getResult());
//                        jmsgSmsSubmit.setBizid(smsSrMessage.getMessage().getId());
//                        jmsgSmsSubmit.setTaskid(smsSrMessage.getMessage().getTaskid());
//                        jmsgSmsSubmit.setUserid(smsSrMessage.getMessage().getUserid());
//                        jmsgSmsSubmit.setGatewayid(smsSrMessage.getMessage().getGateWayID());
//                        jmsgSmsSubmit.setReserve(smsSrMessage.getReserve());
//                        jmsgSmsSubmit.setPhone(smsSrMessage.getMessage().getPhone());
//                        jmsgSmsSubmit.setTableName("jmsg_sms_submit_sync");
//
//                        sqlSession.insert("com.sanerzone.jmsg.dao.JmsgSmsSubmitDao.insert", jmsgSmsSubmit);
//                    }
//
//                    smsRtMessage = new SmsRtMessage();
//                    smsRtMessage.setMsgid(smsSrMessage.getMsgid());
//                    smsRtMessage.setDestTermID(smsSrMessage.getMessage().getPhone());
//                    smsRtMessage.setDoneTime(formatDate(smsSrMessage.getMessage().getSubmitTime()));
//                    smsRtMessage.setStat(smsSrMessage.getResult());
//                    smsRtMessage.setSmsMt(smsSrMessage.getMessage());
//
//                    if(StringUtils.isBlank(smsRtMessage.getSrcTermID())) {
//                    	smsRtMessage.setSrcTermID(smsSrMessage.getMessage().getPhone());
//                    }
//
//                    if("9".equals(smsSrMessage.getMessage().getUserReportNotify())){
////                    mQUtils.sendSmsMT(JmsgCacheKeys.getPushTopic(), smsRtMessage.getSmsMt().getUserReportGateWayID(), smsRtMessage.getSmsMt().getId(),FstObjectSerializeUtil.write(smsRtMessage));
//                        mQUtils.sendSmsMT(JmsgCacheKeys.getPushTopic(), smsRtMessage.getSmsMt().getUserReportGateWayID(),
//                                smsRtMessage.getSmsMt().getId(),FstObjectSerializeUtil.write(smsRtMessage));
//                    }
//
//                }
//                else
//                {
//                    logger.error("msgid:{}, key:{}, 解析异常", message.getMsgId(), message.getKeys());
//                }
//            }
//            sqlSession.commit();
////            List<BatchResult> batchResult = sqlSession.flushStatements();
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

    private String formatDate(Long longTime) {
        String time = DateFormatUtils.format(new Date(), "yyMMddHHmm");

        if (null != longTime) {
            time = DateFormatUtils.format(new Date(longTime), "yyMMddHHmm");
        }

        return time;
    }


}
