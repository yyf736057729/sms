package com.sanerzone.smscenter.utils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import com.aliyun.openservices.ons.api.Producer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.aliyun.openservices.ons.api.SendResult;
import com.aliyun.openservices.ons.api.Message;
import com.sanerzone.common.support.config.Global;
import com.sanerzone.common.support.rocketmq.MQProducerFactory;

@Service
public class MQUtils {
	public Logger logger = LoggerFactory.getLogger(MQUtils.class);

	private String appCode = Global.getConfig("appcode");

//	private static DefaultMQProducer smsCenterProducer;
//	private DefaultMQProducer smsMTProducer;
	private Producer smsMTProducer;

	@PostConstruct
	public void initMQSender() {
//		geSmsCenterProducer();
		getSmsMTProducer();
	}

	private Producer getSmsMTProducer() {
		if (smsMTProducer == null) {
			smsMTProducer = MQProducerFactory.getMQProducer("GID_"+appCode + "SMSMTGroup", appCode + "SMSMTInstance");
			try {
				smsMTProducer.start();
				logger.info("{}，短信MT程序启动成功", appCode);
			} catch (Exception e) {
				logger.error(appCode + "，短信MT程序启动失败", e);
			}
		}
		return smsMTProducer;
	}

//	public DefaultMQProducer geSmsCenterProducer() {
//		if (smsCenterProducer == null) {
//			smsCenterProducer = MQProducerFactory.getMQProducer(appCode + "SMSCENTERGroup",
//					appCode + "SMSCENTERInstance");
//			try {
//				smsCenterProducer.start();
//				logger.info("{}，短信Center程序启动成功", appCode);
//			} catch (MQClientException e) {
//				logger.error(appCode + "，短信Center程序启动失败", e);
//			}
//		}
//		return smsCenterProducer;
//	}

	@PreDestroy
	public void destroy() {
//		if (smsCenterProducer != null) {
//			smsCenterProducer.shutdown();
//		}
		logger.info("{}，短信MT程序shutdown", appCode);
		if (smsMTProducer != null) {
			smsMTProducer.shutdown();
		}
	}

//	public void sendCreateMmsMQ(String mmsid, String jsonBody) {
//		sendMQ(smsCenterProducer, "JmsgAppTopic", "create_mms", mmsid, jsonBody);
//	}
//
//	public void sendCheckMmsMQ(String mmsid, String jsonBody) {
//		sendMQ(smsCenterProducer, "JmsgAppTopic", "check_mms", mmsid, jsonBody);
//	}
//	
//	public void sendMmsDownloadMQ(String mmsid, byte[] jsonBody) {
//        sendMQ(smsCenterProducer, "JmsgMmsTopic", "mms_download", mmsid, jsonBody);
//    }
//
//	public String sendSmsMQ(String id, String topic, String tag, byte[] jsonBody) {
//		return sendMQ(smsCenterProducer, topic, tag, id, jsonBody);
//	}
//
//	public String pushSmsMQ(String id, String tag, byte[] jsonBody) {
//		return sendMQ(smsCenterProducer, CacheKeys.getPushTopic(), tag, id, jsonBody);
//	}
//
//	public String smsSendStatus(String id, byte[] jsonBody) {
//		return sendMQ(smsCenterProducer, CacheKeys.getSmsSendStatus(), "sms_sendstatus", id, jsonBody);
//	}
	
	public String sendSmsMT(String topic, String tag, String key, byte[] body) {
		return sendMQ(smsMTProducer, topic, tag, key, body);
	}
	
//	public String sendSmsRT(String gateWayID, String msgid, byte[] body) {
//		return sendMQ(smsCenterProducer, CacheKeys.getReportTopic(), gateWayID, msgid, body);
//	}
	
	public String sendMQ(Producer producer, String topic, String tag, String key, String body) {
		if (producer == null) {
			return null;
		}
		
		Message msg = new Message(topic, tag, key, body.getBytes());
		try {
            SendResult sendResult = producer.send(msg);
            return sendResult.getMessageId();
		} catch (Exception e) {
			logger.error("消息队列异常", e);
		}
		return null;
	}
	
	public String sendMQ(Producer producer, String topic, String tag, String key, byte[] body) {
		if (producer == null) {
			return "-1";
		}
		Message msg = new Message(topic, tag, key, body);
		try {
			SendResult sendResult = producer.send(msg);
			return sendResult.getMessageId();
		} catch (Exception e) {
			logger.error("消息队列异常", e);
			return "-1";
		}
	}
}
