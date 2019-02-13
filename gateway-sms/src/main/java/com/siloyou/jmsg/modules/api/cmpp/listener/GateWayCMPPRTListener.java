package com.siloyou.jmsg.modules.api.cmpp.listener;

import java.util.List;

import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.siloyou.jmsg.common.message.SmsRtMessage;
import com.siloyou.jmsg.common.util.FstObjectSerializeUtil;
import com.siloyou.jmsg.gateway.Result;
import com.siloyou.jmsg.modules.api.cmpp.GateWayFactory;
import com.siloyou.jmsg.modules.api.cmpp.GateWayMessage;

//public class GateWayCMPPRTListener implements MessageListenerConcurrently {
public class GateWayCMPPRTListener implements MessageListener {
    Logger logger = LoggerFactory.getLogger(GateWayCMPPRTListener.class);

    private GateWayMessage gateWayMessage;
    private GateWayFactory gateWayFactory;

    @Override
    public Action consume(Message message, ConsumeContext consumeContext) {
        Result result = null;
        logger.info("msgid:{}, key:{}", message.getMsgID(), message.getKey());
        SmsRtMessage smsRtMessage = null;
        try
        {
            smsRtMessage = (SmsRtMessage)FstObjectSerializeUtil.read(message.getBody());
            if (smsRtMessage != null)
            {
                logger.info("状态报告推送消息，用户ID:{}, 目标号码:{}, 发送号码:{}, 批次号:{}, 消费次数:{}", smsRtMessage.getSmsMt().getUserid(), smsRtMessage.getDestTermID(), smsRtMessage.getSrcTermID(),
                        smsRtMessage.getSmsMt().getTaskid(), message.getReconsumeTimes());
                gateWayFactory.sendReport(smsRtMessage);

                return Action.CommitMessage;
            }
            else
            {
                logger.error("状态报告消息解析为空， topic:{}, tags:{}, msgid:{}, key:{}", message.getTopic(), message.getTag(), message.getMsgID(), message.getKey());
            }
        }
        catch (Exception ex)
        {
            logger.error("状态报告消息处理异常, tags:"+message.getTag()+", msgid:"+message.getMsgID()+", key:"+ message.getKey(), ex);
        }
        return Action.CommitMessage;
    }
//	@Override
//	public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs,
//			ConsumeConcurrentlyContext context) {
//
//		Result result = null;
//        for (MessageExt message : msgs)
//        {
//            logger.info("msgid:{}, key:{}", message.getMsgId(), message.getKeys());
//            SmsRtMessage smsRtMessage = null;
//            try
//            {
//            	smsRtMessage = (SmsRtMessage)FstObjectSerializeUtil.read(message.getBody());
//                if (smsRtMessage != null)
//                {
//                	logger.info("状态报告推送消息，用户ID:{}, 目标号码:{}, 发送号码:{}, 批次号:{}, 消费次数:{}", smsRtMessage.getSmsMt().getUserid(), smsRtMessage.getDestTermID(), smsRtMessage.getSrcTermID(),
//                			smsRtMessage.getSmsMt().getTaskid(), message.getReconsumeTimes());
//                	gateWayFactory.sendReport(smsRtMessage);
//
//                	return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
//                }
//                else
//                {
//                	logger.error("状态报告消息解析为空， topic:{}, tags:{}, msgid:{}, key:{}", message.getTopic(), message.getTags(), message.getMsgId(), message.getKeys());
//                }
//            }
//            catch (Exception ex)
//            {
//            	logger.error("状态报告消息处理异常, tags:"+message.getTags()+", msgid:"+message.getMsgId()+", key:"+ message.getKeys(), ex);
//            }
//        }
//
//		return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
//	}


    public void setGateWayMessage(GateWayMessage gateWayMessage)
    {
        this.gateWayMessage = gateWayMessage;
    }

    public void setGateWayFactory(GateWayFactory gateWayFactory){
        this.gateWayFactory = gateWayFactory;
    }


}
