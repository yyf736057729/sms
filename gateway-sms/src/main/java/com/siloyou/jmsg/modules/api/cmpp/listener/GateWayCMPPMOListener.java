package com.siloyou.jmsg.modules.api.cmpp.listener;

import java.util.List;

import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.siloyou.jmsg.common.message.SmsMoMessage;
import com.siloyou.jmsg.common.util.FstObjectSerializeUtil;
import com.siloyou.jmsg.gateway.Result;
import com.siloyou.jmsg.modules.api.cmpp.GateWayFactory;
import com.siloyou.jmsg.modules.api.cmpp.GateWayMessage;

//public class GateWayCMPPMOListener implements MessageListenerConcurrently {
public class GateWayCMPPMOListener implements MessageListener {
    Logger logger = LoggerFactory.getLogger(GateWayCMPPMOListener.class);

    private GateWayMessage gateWayMessage;
    private GateWayFactory gateWayFactory;
    @Override
    public Action consume(Message message, ConsumeContext consumeContext) {
        Result result = null;
        logger.info("msgid:{}, key:{}", message.getMsgID(), message.getKey());
        SmsMoMessage smsMoMessage = null;
        try
        {
            smsMoMessage = (SmsMoMessage)FstObjectSerializeUtil.read(message.getBody());
            if (smsMoMessage != null)
            {
                logger.info("目标号码:{}, 发送号码:{}, 消费次数:{}", smsMoMessage.getDestTermID(), smsMoMessage.getSrcTermID(),
                        message.getReconsumeTimes());
                gateWayFactory.sendDeliver(smsMoMessage);

                return Action.CommitMessage;
            }
            else
            {
                logger.error("消息解析为空, topic:{}, tags:{}, msgid:{}, key:{}", message.getTopic(), message.getTag(), message.getMsgID(), message.getKey());
            }
        }
        catch (Exception ex)
        {
            logger.error("消息处理异常, tags:"+message.getTag()+", msgid:"+message.getMsgID()+", key:"+ message.getKey(), ex);
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
//            SmsMoMessage smsMoMessage = null;
//            try
//            {
//            	smsMoMessage = (SmsMoMessage)FstObjectSerializeUtil.read(message.getBody());
//                if (smsMoMessage != null)
//                {
//                	logger.info("目标号码:{}, 发送号码:{}, 消费次数:{}", smsMoMessage.getDestTermID(), smsMoMessage.getSrcTermID(),
//                			message.getReconsumeTimes());
//                	gateWayFactory.sendDeliver(smsMoMessage);
//
//                	return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
//                }
//                else
//                {
//                	logger.error("消息解析为空, topic:{}, tags:{}, msgid:{}, key:{}", message.getTopic(), message.getTags(), message.getMsgId(), message.getKeys());
//                }
//            }
//            catch (Exception ex)
//            {
//            	logger.error("消息处理异常, tags:"+message.getTags()+", msgid:"+message.getMsgId()+", key:"+ message.getKeys(), ex);
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
