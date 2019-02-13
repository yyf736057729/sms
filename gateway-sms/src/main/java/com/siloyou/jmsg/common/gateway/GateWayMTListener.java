package com.siloyou.jmsg.common.gateway;

import java.util.List;

import com.Application;
import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;
import com.siloyou.jmsg.gateway.cmpp.CmppGatewayFactory;
import com.siloyou.jmsg.gateway.cmpp.message.CmppGateWayMessage;
import com.zx.sms.codec.cmpp.msg.CmppDeliverRequestMessage;
import com.zx.sms.codec.cmpp.msg.CmppSubmitRequestMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.siloyou.jmsg.common.message.SmsMtMessage;
import com.siloyou.jmsg.common.message.SmsSrMessage;
import com.siloyou.jmsg.common.util.FstObjectSerializeUtil;
import com.siloyou.jmsg.gateway.Result;
import com.siloyou.jmsg.gateway.api.GateWayMessageAbstract;
import com.siloyou.jmsg.gateway.api.GatewayFactory;

public class GateWayMTListener implements /*MessageListenerConcurrently*/MessageListener {
    Logger logger = LoggerFactory.getLogger(GateWayMTListener.class);

    private SmsGateWay smsGateWay;

    public GateWayMTListener (SmsGateWay smsGateWay) {
        this.smsGateWay = smsGateWay;
    }
    private static CmppGatewayFactory cmppGatewayFactory = (CmppGatewayFactory)Application.applicationContext.getBean("cmppGatewayFactory");
    @Override
    public Action consume(Message message, ConsumeContext consumeContext) {
        Result result = null;
        logger.info("jianting--------------:"/*+message.size()*/);
        logger.info("短信发送执行,cmpp listener recv mt, topic:{}, tag:{}, msgid:{}, key:{}", message.getTopic(), message.getTag(), message.getMsgID(), message.getKey());
        SmsMtMessage smsMtMessage = null;
        try
        {
            smsMtMessage = (SmsMtMessage)FstObjectSerializeUtil.read(message.getBody());
            if (smsMtMessage != null)
            {
                try
                {
//                		result = smsGateWay.send(smsMtMessage); //①
                    result = cmppGatewayFactory.sendMsg(smsMtMessage);


//                        result = new Result("T001", "OK!");
                }
                catch (Exception ex)
                {
                    logger.error("提交网关异常", ex);
                    return Action.ReconsumeLater;
//                    	result = new Result("F10108", "提交网关异常");
                }
            }
            else
            {
                result = new Result("F10106", "消息解析无数据");
                smsMtMessage = new SmsMtMessage();
                smsMtMessage.setId(message.getKey());
            }
        }
        catch (Exception ex)
        {
            logger.error("消息解析异常", ex);
            result = new Result("F10107", "消息解析异常");
            smsMtMessage = new SmsMtMessage();
            smsMtMessage.setId(message.getKey());
        }

        //失败，放入队列
        if (!result.isSuccess())
        {
            SmsSrMessage smsSrMessage =
                    new SmsSrMessage(message.getMsgID(), result.getErrorCode(), smsMtMessage);
            smsSrMessage.setReserve(result.getErrorMsg());
            try
            {
                smsGateWay.getGatewayFactory().getGateWayMessage().sendSmsSRMessage(smsSrMessage, smsMtMessage.getGateWayID());
            }
            catch (Exception e)
            {
                logger.error("msgid:{}, key:{}, 消息解析异常:{}", message.getMsgID(), message.getKey(), e.getMessage());
            }
        }

        return Action.CommitMessage;
    }
//    @Override
//    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs,
//                                                    ConsumeConcurrentlyContext context) {
//
//        Result result = null;
//        for (MessageExt message : msgs)
//        {
//            logger.info("jianting--------------:"+msgs.size());
//            logger.info("短信发送执行,cmpp listener recv mt, topic:{}, tag:{}, msgid:{}, key:{}", message.getTopic(), message.getTags(), message.getMsgId(), message.getKeys());
//            SmsMtMessage smsMtMessage = null;
//            try
//            {
//                smsMtMessage = (SmsMtMessage)FstObjectSerializeUtil.read(message.getBody());
//                if (smsMtMessage != null)
//                {
//                    try
//                    {
////                		result = smsGateWay.send(smsMtMessage); //①
//                        result = cmppGatewayFactory.sendMsg(smsMtMessage);
//
//
////                        result = new Result("T001", "OK!");
//                    }
//                    catch (Exception ex)
//                    {
//                        logger.error("提交网关异常", ex);
//                        return ConsumeConcurrentlyStatus.RECONSUME_LATER;
////                    	result = new Result("F10108", "提交网关异常");
//                    }
//                }
//                else
//                {
//                    result = new Result("F10106", "消息解析无数据");
//                    smsMtMessage = new SmsMtMessage();
//                    smsMtMessage.setId(message.getKeys());
//                }
//            }
//            catch (Exception ex)
//            {
//                logger.error("消息解析异常", ex);
//                result = new Result("F10107", "消息解析异常");
//                smsMtMessage = new SmsMtMessage();
//                smsMtMessage.setId(message.getKeys());
//            }
//
//            //失败，放入队列
//            if (!result.isSuccess())
//            {
//                SmsSrMessage smsSrMessage =
//                        new SmsSrMessage(message.getMsgId(), result.getErrorCode(), smsMtMessage);
//                smsSrMessage.setReserve(result.getErrorMsg());
//                try
//                {
//                    smsGateWay.getGatewayFactory().getGateWayMessage().sendSmsSRMessage(smsSrMessage, smsMtMessage.getGateWayID());
//                }
//                catch (Exception e)
//                {
//                    logger.error("msgid:{}, key:{}, 消息解析异常:{}", message.getMsgId(), message.getKeys(), e.getMessage());
//                }
//            }
//        }
//
//        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
//    }


}
