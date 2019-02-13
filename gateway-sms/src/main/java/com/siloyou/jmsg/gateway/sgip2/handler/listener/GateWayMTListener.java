package com.siloyou.jmsg.gateway.sgip2.handler.listener;

import java.util.List;

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

public class GateWayMTListener implements MessageListenerConcurrently {
	Logger logger = LoggerFactory.getLogger(GateWayMTListener.class);
	
    private GatewayFactory     gatewayFactory;
    
    private GateWayMessageAbstract gateWayMessage;
	
	@Override
	public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs,
			ConsumeConcurrentlyContext context) {
		
		Result result = null;
        for (MessageExt message : msgs)
        {
            logger.info("sigp listener recv mt, topic:{}, tag:{}, msgid:{}, key:{}", message.getTopic(), message.getTags(), message.getMsgId(), message.getKeys());
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
                    smsMtMessage.setId(message.getKeys());
                }
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
                result = new Result("F10107", "消息解析异常");
                smsMtMessage = new SmsMtMessage();
                smsMtMessage.setId(message.getKeys());
            }
            
          //失败，放入队列
            if (!result.isSuccess())
            {
                SmsSrMessage smsSrMessage =
                    new SmsSrMessage(message.getMsgId(), result.getErrorCode(), smsMtMessage);
                smsSrMessage.setReserve(result.getErrorMsg());
                try
                {
                    // GateWayQueueFactory.getSubmitRespQueue().put(smsSrMessage);
                    gateWayMessage.sendSmsSRMessage(smsSrMessage, smsMtMessage.getGateWayID());
                }
                catch (Exception e)
                {
                    logger.error("msgid:{}, key:{}, 消息解析异常:{}", message.getMsgId(), message.getKeys(), e.getMessage());
                }
            }
        }
		
		return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
	}

	public void setGatewayFactory(GatewayFactory gatewayFactory) {
		this.gatewayFactory = gatewayFactory;
	}

    public void setGateWayMessage(GateWayMessageAbstract gateWayMessage)
    {
        this.gateWayMessage = gateWayMessage;
    }
	
}
