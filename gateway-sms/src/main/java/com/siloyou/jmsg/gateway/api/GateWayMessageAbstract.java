package com.siloyou.jmsg.gateway.api;

import java.io.Serializable;

import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.Producer;
import com.aliyun.openservices.ons.api.SendResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.producer.DefaultMQProducer;


import com.Application;
import com.siloyou.jmsg.common.config.Global;
import com.siloyou.jmsg.common.message.SmsMoMessage;
import com.siloyou.jmsg.common.message.SmsMtMessage;
import com.siloyou.jmsg.common.message.SmsRtMessage;
import com.siloyou.jmsg.common.message.SmsSrMessage;
import com.siloyou.jmsg.common.mq.MQProducerFactory;
import com.siloyou.jmsg.common.util.CachedMillisecondClock;
import com.siloyou.jmsg.common.util.FstObjectSerializeUtil;

/**
 * 调用RocketMq,将接收的网关消息推送到业务系统 状态报告、短信上行
 * 
 * @author mac
 */
public abstract class GateWayMessageAbstract {

	private Logger logger = LoggerFactory.getLogger(GateWayMessageAbstract.class);
	private String appCode = Application.appCode;
	private String smsRtTPC = Global.getConfig("gateway.smsrt.topic");
	private String smsMoTPC = Global.getConfig("gateway.smsmo.topic");
	private String smsSrTPC = Global.getConfig("gateway.smssr.topic");

//	private static DefaultMQProducer smsRTProducer = null;
//	private static DefaultMQProducer smsMOProducer = null;
//	private static DefaultMQProducer smsSRProducer = null;
	private static Producer smsRTProducer = null;
	private static Producer smsMOProducer = null;
	private static Producer smsSRProducer = null;
	
	private Producer getSmsSRProducer() {
        if (smsSRProducer == null) {
            smsSRProducer = MQProducerFactory.getMQProducer(appCode + "SMSSRV1CliGroup", appCode + "SMSSRInstance");
            try {
                smsSRProducer.start();
                logger.info("{}，网关状态推送程序启动成功", appCode);
            } catch (Exception e) {
                logger.error(appCode + "，网关状态推送程序启动失败", e);
            }
        }
        return smsSRProducer;
    }
	
	private Producer getSmsMOProducer() {
		if (smsMOProducer == null) {
			smsMOProducer = MQProducerFactory.getMQProducer(appCode + "SMSMOV1CliGroup", appCode + "SMSMOInstance");
			try {
				smsMOProducer.start();
				logger.info("{}，用户上行推送程序启动成功", appCode);
			} catch (Exception e) {
				logger.error(appCode + "，用户上行推送程序启动失败", e);
			}
		}
		return smsMOProducer;
	}

	private Producer getSmsRTProducer() {
		if(smsRTProducer == null) {
			smsRTProducer = MQProducerFactory.getMQProducer(appCode + "SMSRTV1CliGroup", appCode + "SMSRTInstance");
			try {
				smsRTProducer.start();
				logger.info("{}，状态报告推送程序启动成功", appCode);
			} catch (Exception e) {
				logger.error(appCode + "，状态报告推送程序启动失败", e);
			}
		}
		return smsRTProducer;
	}
	private void initGateWayMessage() {
		getSmsMOProducer();
		getSmsRTProducer();
		getSmsSRProducer();
	}

	private void sendMQ(Producer producer, final Message message) {
		final long startTime = CachedMillisecondClock.INS.now();
		try {
		    SendResult sendResult = producer.send(message);
		    logger.info("GatewayMessage send: Topic:{}, Tags:{}, Keys:{}, Msgid:{}, Status:{}, time:{}ms",
					message.getTopic(), message.getTag(), message.getKey(), sendResult.getMessageId() ,"-", ( CachedMillisecondClock.INS.now() - startTime ));
//		    producer.send(message, new SendCallback() {
//				
//				@Override
//				public void onSuccess(SendResult sendResult) {
//					logger.info("GatewayMessage send: Topic:{}, Tags:{}, Keys:{}, Msgid:{}, Status:{}, time:{}ms", 
//							message.getTopic(), message.getTags(), message.getKeys(), sendResult.getMsgId(), sendResult.getSendStatus(), ( CachedMillisecondClock.INS.now() - startTime ));
//				}
//				
//				@Override
//				public void onException(Throwable e) {
//					logger.error("GatewayMessage send: Topic:"+message.getTopic()+", Tags:"+message.getTags()+", Keys:"+message.getKeys(),  e);
//				}
//			});
		    
//			return null;
		} catch (Exception e) {
//			logger.error(appCode + ", 提交消息队列异常", e);
			logger.error("GatewayMessage send: Topic:"+message.getTopic()+", Tags:"+message.getTag()+", Keys:"+message.getKey(),  e);
		}
//		return null;
	}

	public void sendSmsSRMessage(Serializable message, String gateWayID) {
		SmsSrMessage smsSrMessage = convertSRMessage(message);
		try
        {
//		    GateWayQueueFactory.getSubmitRespQueue().put(smsSrMessage);
		    Message msg = new Message(smsSrTPC, appCode, smsSrMessage.getMsgid(),
                FstObjectSerializeUtil.write(smsSrMessage));
		//	getSmsSRProducer();
		    sendMQ(getSmsSRProducer(), msg);
        }
        catch (Exception e)
        {
            logger.error("网关响应异常", e);
        }
	}

	public void sendSmsRTMessage(Serializable message, String gateWayID) {
		SmsRtMessage smsRtMessage = convertRTMessage(message);
		smsRtMessage.setGateWayID(gateWayID);
		try {
			Message msg = new Message(smsRtTPC, appCode, smsRtMessage.getMsgid(),
					FstObjectSerializeUtil.write(smsRtMessage));
			sendMQ(getSmsRTProducer(), msg);
		} catch (Exception e) {
			logger.info(appCode + "状态报告消息序列化异常", e);
		}

	}

	public void sendSmsMOMessage(Serializable message, String gateWayID) {
		SmsMoMessage smsMoMessage = convertMOMessage(message);
		smsMoMessage.setGateWayID(gateWayID);
		try {
			Message msg = new Message(smsMoTPC, appCode, smsMoMessage.getMsgid(),
					FstObjectSerializeUtil.write(smsMoMessage));
			sendMQ(smsRTProducer, msg);
		} catch (Exception e) {
			logger.info(appCode + "用户上行消息序列化异常", e);
		}
	}

	/**
	 * 提交网关结果
	 * 
	 * @param smsSrMessage
	 * @return
	 */
	protected abstract SmsSrMessage convertSRMessage(Serializable smsSrMessage);

	protected abstract SmsRtMessage convertRTMessage(Serializable smsRtMessage);

	protected abstract SmsMoMessage convertMOMessage(Serializable smsRoMessage);

	public abstract Serializable convertMTMessage(SmsMtMessage smsMtMessage, boolean gatewaySign);
}
