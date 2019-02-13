package com.siloyou.jmsg.modules.api.cmpp;

import java.io.Serializable;
import java.util.Map;

import com.aliyun.openservices.ons.api.*;
import com.zx.sms.codec.cmpp.msg.CmppDeliverResponseMessage;
import com.zx.sms.codec.cmpp.msg.CmppSubmitRequestMessage;
import org.apache.commons.lang3.StringUtils;
import org.marre.sms.SmsMessage;
import org.marre.wap.push.SmsMmsNotificationMessage;
import org.marre.wap.push.SmsWapPushMessage;
import org.marre.wap.push.WapSIPush;
import org.marre.wap.push.WapSLPush;
import org.marre.wap.wbxml.WbxmlDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.producer.DefaultMQProducer;

import com.Application;
import com.siloyou.jmsg.common.config.Global;
import com.siloyou.jmsg.common.message.SmsMtMessage;
import com.siloyou.jmsg.common.message.SmsPrMessage;
import com.siloyou.jmsg.common.mq.MQProducerFactory;
import com.siloyou.jmsg.common.util.FstObjectSerializeUtil;
import com.siloyou.jmsg.common.util.SystemClock;
import com.zx.sms.common.util.MsgId;
import com.siloyou.jmsg.gateway.Result;
/**
 * 调用RocketMq,将接收的网关消息推送到业务系统 状态报告、短信上行
 * 
 * @author mac
 */
public class GateWayMessage {

	private Logger logger = LoggerFactory.getLogger(GateWayMessage.class);
	private String appCode = Application.appCode;
	private String smsUmtTPC = Global.getConfig("gateway.smsumt.topic");
	private String smsPrTPC = Global.getConfig("gateway.smspr.topic");
	private static Producer smsUMTProducer = null;

	private Producer getSmsUMTProducer() {
		if (smsUMTProducer == null) {
			smsUMTProducer = MQProducerFactory.getMQProducer(appCode + "SMSSRGroup", appCode + "SMSUMTV1CmppSrvInstance");
			try {
				smsUMTProducer.start();
				logger.info("{}，模拟CMPP网关UMT发送程序启动成功", appCode);
			} catch (Exception e) {
				logger.error(appCode + "，模拟CMPP网关UMT发送程序启动失败", e);
			}
		}
		return smsUMTProducer;
	}

	private void initGateWayMessage() {
		getSmsUMTProducer();
	}

//    private SendResult sendMQ(DefaultMQProducer producer, Message message)
//        throws MQClientException, RemotingException, MQBrokerException, InterruptedException
//    {
//        SendResult result = producer.send(message);
//        logger.info("GatewayMessage srv send: Topic:{}, Tags:{}, Keys:{}, Msgid:{}, Status:{}",
//            message.getTopic(),
//            message.getTags(),
//            message.getKeys(),
//            result.getMsgId(),
//            result.getSendStatus());
//        return result;
//    }

	private void sendMQ(Producer producer, final Message message) {
		try {
		    producer.send(message);
			final long startTime = SystemClock.now();
//			producer.sendAsync(message,new SendCallback(){
//				@Override
//				public void onSuccess(SendResult sendResult) {
//					logger.info("GatewayMessage send: Topic:{}, Tags:{}, Keys:{}, Msgid:{}, Status:{}, time: {}ms",
//							message.getTopic(), message.getTag(), message.getKey(), sendResult.getMessageId(),
//							SystemClock.now() - startTime);
//				}
//
//				@Override
//				public void onException(OnExceptionContext e) {
//					logger.error("GatewayMessage send: Topic:"+message.getTopic()+", Tags:"+message.getTag()+", Keys:"+message.getKey(),  e);
//				}
//			});
		    
//			return null;
		} catch (Exception e) {
//			logger.error(appCode + ", 提交消息队列异常", e);
			logger.error("GatewayMessage send: Topic:"+message.getTopic()+", Tags:"+message.getTag()+", Keys:"+message.getKey(),  e);
		}
//		return null;
	}
	
	public Result sendSmsPushResultMessage(Serializable message, String userid) {
		long startTime = SystemClock.now();
		CmppDeliverResponseMessage cmppDeliverResponse = (CmppDeliverResponseMessage) message;
		SmsPrMessage smsPrMessage = new SmsPrMessage(cmppDeliverResponse.getMsgId().toString(), 
				String.valueOf(cmppDeliverResponse.getResult()), String.valueOf(cmppDeliverResponse.getAttachment()));
		smsPrMessage.setRecvTime(SystemClock.now());
		smsPrMessage.setUserid(userid);
		try {
			Message msg = new Message(smsPrTPC, appCode, smsPrMessage.getBizid(),
					FstObjectSerializeUtil.write(smsPrMessage));
			sendMQ(smsUMTProducer, msg);
			
			logger.info("send: {}ms", (SystemClock.now() - startTime));
			return new Result();
		} catch (Exception e) {
			logger.error("网关响应异常", e);
			return new Result("F1", "网关响应异常");
		}
		
	}

	public Result sendSmsUMTMessage(Serializable message, String gateWayID, String msgSrc) {
		long startTime = SystemClock.now();
		SmsMtMessage smsMtMessage = convertUMTMessage( message, gateWayID, msgSrc);
//		logger.info("convertMsg: {}ms", (SystemClock.now() - startTime));
		smsMtMessage.setId(new MsgId().toString());
		
//		startTime = SystemClock.now();
		try {
			System.out.println("http  运行一次！！------------------------");
			Message msg = new Message(smsUmtTPC, appCode, smsMtMessage.getTaskid(),
					FstObjectSerializeUtil.write(smsMtMessage));
			sendMQ(smsUMTProducer, msg);
			
//			logger.info("send: {}ms", (SystemClock.now() - startTime));
			return new Result();
		} catch (Exception e) {
			logger.error("网关响应异常", e);
			return new Result("F1", "网关响应异常");
		}
		
	}

	public SmsMtMessage convertUMTMessage(Serializable cmppSubmitRequestMessage, String gateWayID, String msgSrc) {
		
		CmppSubmitRequestMessage e = (CmppSubmitRequestMessage)cmppSubmitRequestMessage;
		SmsMtMessage smsMtMessage = new SmsMtMessage();
		
		//短信内容
		SmsMessage smsMessage = e.getSmsMessage();
		if(smsMessage instanceof SmsWapPushMessage) {//wappush
			SmsWapPushMessage smsWapPushMessage = (SmsWapPushMessage)smsMessage ;
			WbxmlDocument wapPush = smsWapPushMessage.getWbxml();
			if(wapPush instanceof WapSIPush) { //wap si
				WapSIPush wapSIPush = (WapSIPush) wapPush;
				smsMtMessage.setSmsType("wapsi");
				smsMtMessage.setWapUrl(wapSIPush.getUri());
				smsMtMessage.setMsgContent(wapSIPush.getMessage());
				
			} else if(wapPush instanceof WapSLPush) { //wap sl
				WapSLPush wapSLPush = (WapSLPush) wapPush;
				smsMtMessage.setSmsType("wapsl");
				smsMtMessage.setWapUrl(wapSLPush.getUri());
			}
			
		} else if(smsMessage instanceof SmsMmsNotificationMessage){  // sms mms
			SmsMmsNotificationMessage smsMmsNotificationMessage = (SmsMmsNotificationMessage) smsMessage;
			smsMtMessage.setSmsType("mms");
			smsMtMessage.setMsgContent(smsMmsNotificationMessage.getSubject_());
			smsMtMessage.setContentSize(smsMmsNotificationMessage.getSize_());
			smsMtMessage.setWapUrl(smsMmsNotificationMessage.getContentLocation_());
			
		} else { //sms
			smsMtMessage.setSmsType("sms");
			smsMtMessage.setMsgContent(e.getMsgContent());
		}
		
		String taskid = e.getMsgid().toString();
		String cstmOrderID = taskid;
		Map<Integer, String> msgIds = (Map<Integer, String>) e.getAttachment();
		if(msgIds != null && !msgIds.isEmpty()) {
			taskid = msgIds.get(1);
			
			StringBuilder tempIds = new StringBuilder();
			for(String id : msgIds.values()) {
				tempIds.append("-").append(id);
			}
			cstmOrderID = tempIds.substring(1);
			
		} 
		
		String srcId = e.getSrcId();
		String spNumber = GateWayFactory.userGateways.get(msgSrc).getSpnumber();
		if(srcId.startsWith(spNumber)) {
			srcId = srcId.substring(spNumber.length());
		}
		
		//客户端的msgid
		smsMtMessage.setCstmOrderID( cstmOrderID );
		smsMtMessage.setPhone(splicePhone(e.getDestterminalId()));
		smsMtMessage.setUserid( msgSrc ); //用户ID
		smsMtMessage.setTaskid( taskid ); //T 任务 C cmpp协议
		smsMtMessage.setSpNumber(srcId);
		smsMtMessage.setUserReportGateWayID("CMPP" + appCode);
		smsMtMessage.setUserReportNotify((e.getRegisteredDelivery() == (short)0x1)?"9":"0");
		return smsMtMessage;
	}
	
	private String splicePhone(String[] phones){
		int len = phones.length;
		if(null == phones || len < 1) {
			return "";
		}
		
		StringBuilder splicePhone = new StringBuilder();
		
		for( int i=0; i< len ;i ++ ) {
			String phone = phones[i];
			if(phone.startsWith("86")) {
				phone = phone.substring(2);
			}
			splicePhone.append(phone);
			if (i < len - 1) {
				splicePhone.append(",");
			}
		}
		return StringUtils.rightPad(splicePhone.toString(), 1);
	}
}
