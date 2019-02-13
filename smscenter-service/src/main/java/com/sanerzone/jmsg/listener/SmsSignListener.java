package com.sanerzone.jmsg.listener;

import java.util.List;

import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;
import com.sanerzone.common.support.utils.FstObjectSerializeUtil;
import org.marre.sms.SmsAlphabet;
import org.marre.sms.SmsMsgClass;
import org.marre.sms.SmsTextMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.sanerzone.common.modules.smscenter.utils.SignUtils;
import com.sanerzone.common.support.utils.DateUtils;
import com.sanerzone.common.support.utils.StringUtils;
import com.sanerzone.common.support.utils.SystemClock;
import com.sanerzone.jmsg.util.MessageExtUtil;
import com.sanerzone.jmsg.util.UserSignUtils;
import com.siloyou.jmsg.common.message.SmsMtMessage;

@Service
public class SmsSignListener implements /*MessageListenerConcurrently*/ MessageListener {

	private Logger logger = LoggerFactory.getLogger(SmsSignListener.class);

	@Override
	public Action consume(Message message, ConsumeContext consumeContext) {
		SmsMtMessage smsRtMessage;
		logger.info("umt listener recv message: topic:{}, tags:{}, msgid:{}, key:{}", message.getTopic(), message.getTag(),
				message.getMsgID(), message.getKey());
		long startTime = SystemClock.now();
		try {
			smsRtMessage = (SmsMtMessage)FstObjectSerializeUtil.read(message.getBody());
//			smsRtMessage = MessageExtUtil.convertMessageExt(SmsMtMessage.class, message);
			if (smsRtMessage == null) {
				return Action.CommitMessage;
			}
			String smsContent = smsRtMessage.getMsgContent();
			if(StringUtils.isBlank(smsContent)){
				return Action.CommitMessage;
			}
			String userId = smsRtMessage.getUserid();
			String sign = SignUtils.get(smsContent);
			int payCount = findPayCount(smsContent);
			String day = DateUtils.getDate("yyyyMMdd");
			logger.info("获取用户签名: 任务ID:{}, 用户:{}, 手机号码：{}, 扣费条数：{}, 接收时间:{} ,签名:{}", smsRtMessage.getTaskid(), smsRtMessage.getUserid(), smsRtMessage.getPhone(), payCount ,startTime, sign);

			UserSignUtils.put(day, userId, sign, payCount);
		}catch(Exception e){
			logger.error("获取用户签名", e);
		}
		return Action.CommitMessage;
	}
//	@Override
//	public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
//		SmsMtMessage smsRtMessage;
//
//		for (MessageExt msg : msgs) {
//			logger.info("umt listener recv message: topic:{}, tags:{}, msgid:{}, key:{}", msg.getTopic(), msg.getTags(),
//					msg.getMsgId(), msg.getKeys());
//			long startTime = SystemClock.now();
//			try {
//				smsRtMessage = MessageExtUtil.convertMessageExt(SmsMtMessage.class, msg);
//				if (smsRtMessage == null) {
//					continue;
//				}
//				String smsContent = smsRtMessage.getMsgContent();
//				if(StringUtils.isBlank(smsContent)){
//					continue;
//				}
//				String userId = smsRtMessage.getUserid();
//				String sign = SignUtils.get(smsContent);
//				int payCount = findPayCount(smsContent);
//				String day = DateUtils.getDate("yyyyMMdd");
//				logger.info("获取用户签名: 任务ID:{}, 用户:{}, 手机号码：{}, 扣费条数：{}, 接收时间:{} ,签名:{}", smsRtMessage.getTaskid(), smsRtMessage.getUserid(), smsRtMessage.getPhone(), payCount ,startTime, sign);
//
//				UserSignUtils.put(day, userId, sign, payCount);
//			}catch(Exception e){
//				logger.error("获取用户签名", e);
//			}
//		}
//		return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
//	}
//
	//获取扣费条数
	public int findPayCount(String smsContent){
		SmsTextMessage sms = null;
		if(StringUtils.haswidthChar(smsContent)) {
			sms = new SmsTextMessage(smsContent,SmsAlphabet.UCS2, SmsMsgClass.CLASS_UNKNOWN);
		} else {
			sms = new SmsTextMessage(smsContent,SmsAlphabet.LATIN1, SmsMsgClass.CLASS_UNKNOWN);
		}
		return sms.getPdus().length;
	}


}
