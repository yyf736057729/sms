package com.siloyou.jmsg.modules.mms.service;

import com.aliyun.openservices.ons.api.Producer;
import com.aliyun.openservices.ons.api.SendResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.aliyun.openservices.ons.api.Message;
import com.siloyou.core.common.config.Global;
import com.siloyou.jmsg.common.mq.MQProducerFactory;

public class MmsSendService {

	public static Logger logger = LoggerFactory.getLogger(MmsSendService.class);
	
	private String appCode = Global.getConfig("appcode");
//	private static DefaultMQProducer mmsMTProducer;
	private static Producer mmsMTProducer;

//	@PostConstruct
	public void initMQSender() {
		getMmsMTProducer();
	}

	private Producer getMmsMTProducer() {
		if (mmsMTProducer == null) {
			mmsMTProducer = MQProducerFactory.getMQProducer("GID_"+appCode + "MMSMTGroup", appCode + "MMSMTInstance");
			try {
				mmsMTProducer.start();
				logger.info("{}，彩信MT程序启动成功", appCode);
			} catch (Exception e) {
				logger.error(appCode + "，彩信MT程序启动失败", e);
			}
		}
		return mmsMTProducer;
	}

	public static String sendMQ(Producer producer, String topic, String tag, String key, String body) {
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
	
	public static String sendMms(String key, String jsonBody){
		return sendMQ(mmsMTProducer, "SmsSendTopic", "mms_submit", key, jsonBody);
	}
	
	public static String sendMmsStatus(String key, String jsonBody){
		return sendMQ(mmsMTProducer, "SmsStatusTopic", "mms_submit_status", key, jsonBody);
	}
	
	public static String sendMmsDownload(String key, String jsonBody){
		return sendMQ(mmsMTProducer, "MmsDownloadTopic", "mms_download", key, jsonBody);
	}
	
}
