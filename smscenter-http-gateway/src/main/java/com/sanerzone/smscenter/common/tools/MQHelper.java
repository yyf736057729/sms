package com.sanerzone.smscenter.common.tools;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.client.producer.SendStatus;
import com.alibaba.rocketmq.common.message.Message;
import com.sanerzone.common.support.utils.FstObjectSerializeUtil;
import com.siloyou.jmsg.common.message.SmsMtMessage;
import com.xiaoleilu.hutool.util.BeanUtil;

@Service
public class MQHelper {
	
	public Logger logger = LoggerFactory.getLogger(MQHelper.class);

	@Autowired(required=true)
	private DefaultMQProducer defaultMQProducer;
	
	public String sendSMSUMT(String tag, String key, SmsMtMessage message) {
		return sendSyncMsgId("SMSUMTV1", tag, key, message);
	}
	
	public String sendSyncMsgId(String topic, String tag, String key, Serializable body) {
		SendResult sendResult = null;
		try {
			sendResult = send(topic, tag, key, body);
		} catch (Exception e) {
			logger.error("序列化消息异常, {}", BeanUtil.beanToMap(body) ,e);
		}
		
		if (sendResult == null || sendResult.getSendStatus() != SendStatus.SEND_OK) {
			return null;
		}
		return sendResult.getMsgId();
	}
	
	public SendResult send(String topic, String tag, String key, Serializable body) throws Exception {
		return send(topic, tag, key, FstObjectSerializeUtil.write(body));
	}
	
	public SendResult send(String topic, String tag, String key, byte[] body) {
		Message msg = new Message(topic, tag, key, body);
		try {
			return defaultMQProducer.send(msg);
		} catch (Exception e) {
			logger.error("消息队列异常,topic:{}, tag:{}, key:{}", topic, tag, key, e);
		}
		return null;
	}
	
	
}
