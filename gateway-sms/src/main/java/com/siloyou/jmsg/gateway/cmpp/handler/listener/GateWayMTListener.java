package com.siloyou.jmsg.gateway.cmpp.handler.listener;

import java.util.List;

import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.siloyou.jmsg.common.message.SmsMtMessage;
import com.siloyou.jmsg.common.message.SmsSrMessage;
import com.siloyou.jmsg.common.util.FstObjectSerializeUtil;
import com.siloyou.jmsg.gateway.Result;
import com.siloyou.jmsg.gateway.api.GateWayMessageAbstract;
import com.siloyou.jmsg.gateway.api.GatewayFactory;

//public class GateWayMTListener implements MessageListenerConcurrently {
public class GateWayMTListener implements MessageListener {

    Logger logger = LoggerFactory.getLogger(GateWayMTListener.class);

    private GatewayFactory     gatewayFactory;

    private GateWayMessageAbstract gateWayMessage;

    @Override
    public Action consume(Message message, ConsumeContext context) {
        Result result = null;
        logger.info("cmpp listener recv mt, topic:{}, tag:{}, msgid:{}, key:{}", message.getTopic(), message.getTag(), message.getMsgID(), message.getKey());
        SmsMtMessage smsMtMessage = null;
        try
        {
            smsMtMessage = (SmsMtMessage)FstObjectSerializeUtil.read(message.getBody());
            if (smsMtMessage != null)
            {
                result = gatewayFactory.sendMsg(smsMtMessage);
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
                // GateWayQueueFactory.getSubmitRespQueue().put(smsSrMessage);
                gateWayMessage.sendSmsSRMessage(smsSrMessage, smsMtMessage.getGateWayID());
            }
            catch (Exception e)
            {
                logger.error("msgid:{}, key:{}, 消息解析异常:{}", message.getMsgID(), message.getKey(), e.getMessage());
            }
        }

//        for (MessageExt message : msgs)
//        {
//
//        }

        return Action.CommitMessage;
    }
//	@Override
//	public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs,
//			ConsumeConcurrentlyContext context) {
//
//		Result result = null;
//        for (MessageExt message : msgs)
//        {
//            logger.info("cmpp listener recv mt, topic:{}, tag:{}, msgid:{}, key:{}", message.getTopic(), message.getTags(), message.getMsgId(), message.getKeys());
//            SmsMtMessage smsMtMessage = null;
//            try
//            {
//                smsMtMessage = (SmsMtMessage)FstObjectSerializeUtil.read(message.getBody());
//                if (smsMtMessage != null)
//                {
//                    result = gatewayFactory.sendMsg(smsMtMessage);
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
//                result = new Result("F10107", "消息解析异常");
//                smsMtMessage = new SmsMtMessage();
//                smsMtMessage.setId(message.getKeys());
//            }
//
//            //失败，放入队列
//            if (!result.isSuccess())
//            {
//                SmsSrMessage smsSrMessage =
//                    new SmsSrMessage(message.getMsgId(), result.getErrorCode(), smsMtMessage);
//                smsSrMessage.setReserve(result.getErrorMsg());
//                try
//                {
//                    // GateWayQueueFactory.getSubmitRespQueue().put(smsSrMessage);
//                    gateWayMessage.sendSmsSRMessage(smsSrMessage, smsMtMessage.getGateWayID());
//                }
//                catch (Exception e)
//                {
//                    logger.error("msgid:{}, key:{}, 消息解析异常:{}", message.getMsgId(), message.getKeys(), e.getMessage());
//                }
//            }
//        }
//
////		for (MessageExt message : msgs) {
////			logger.info("msgid:{}, key:{}", message.getMsgId(), message.getKeys());
////			try {
////				SmsMtMessage smsMtMessage = (SmsMtMessage) FstObjectSerializeUtil.read(message.getBody());
////				if (smsMtMessage != null) {
////					CMPPEndpointEntity gateWay = (CMPPEndpointEntity)gatewayFactory.getGateway(smsMtMessage.getGateWayID());
////					if( gateWay == null) {
////						logger.error("{}通道, 未启动", smsMtMessage.getGateWayID());
////					} else {
////						CmppSubmitRequestMessage e = (CmppSubmitRequestMessage)gateWayMessage.convertMTMessage(smsMtMessage, gateWay.isGatewaySign());
////			        	ChannelUtil.asyncWriteToEntity(gateWay.getId(), e);
////					}
////				} else {
////					logger.info("{}: 无数据", message.getMsgId());
////				}
////			} catch (Exception ex) {
////				logger.error("消费异常", ex);
////				// 再次放进队列
//////				return ConsumeConcurrentlyStatus.RECONSUME_LATER;
////			}
////		}
//		return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
//	}

    public void setGatewayFactory(GatewayFactory gatewayFactory) {
        this.gatewayFactory = gatewayFactory;
    }

    public void setGateWayMessage(GateWayMessageAbstract gateWayMessage)
    {
        this.gateWayMessage = gateWayMessage;
    }


}
