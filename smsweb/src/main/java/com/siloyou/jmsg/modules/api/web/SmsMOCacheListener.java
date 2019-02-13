package com.siloyou.jmsg.modules.api.web;

import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;
import com.sanerzone.common.support.storedMap.BDBStoredMapFactoryImpl;
import com.sanerzone.common.support.utils.FstObjectSerializeUtil;
import com.sanerzone.common.support.utils.JedisClusterUtils;
import com.sanerzone.common.support.utils.StringUtils;
import com.siloyou.core.modules.sys.utils.UserUtils;
import com.siloyou.jmsg.common.message.SmsMoMessage;
import com.siloyou.jmsg.common.message.SmsMtMessage;
import com.siloyou.jmsg.common.message.SmsRtMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class SmsMOCacheListener implements /*MessageListenerConcurrently*/MessageListener {

	public static Logger logger = LoggerFactory.getLogger(SmsMOCacheListener.class);

	@Override
	public Action consume(Message message, ConsumeContext consumeContext) {
		try{
			SmsMoMessage smsmoMessage = (SmsMoMessage) FstObjectSerializeUtil.read(message.getBody());
			if(smsmoMessage != null){
				String spNumber = "", userId = "";
				Map<String,String> moNumMap = JedisClusterUtils.getJedisInstance().hgetAll("MONUM");
				if(moNumMap != null) {
					userId = moNumMap.get(smsmoMessage.getDestTermID());
					if(StringUtils.isBlank(userId)) {
						for(Map.Entry<String, String> entry : moNumMap.entrySet()) {
							String key = entry.getKey();
							if(smsmoMessage.getSrcTermID().startsWith(key)){
								if (key.length() > spNumber.length()) {
									spNumber = key;
									userId = entry.getValue();
								}
							}
						}
					}
				}

				BDBStoredMapFactoryImpl.INS.getQueue("SMS", "SMSMOUERY_"+userId).put(message.getBody());

//
//
//					BDBStoredMapFactoryImpl.INS.getQueue("SMS", "SMSMOUERY_"+smsMtMessage.getMsgid()).put(msg.getBody());
//					if(StringUtils.equals(UserUtils.get(smsRtMessage.getSmsMt().getUserid()).getCallbackUrl(), "")) {

//					}
			}
		}catch(Exception e){
			logger.error("{}", e);
		}
		return Action.CommitMessage;
	}

//	@Override
//	public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
//		try{
//			for (MessageExt msg : msgs) {
//				SmsMoMessage smsmoMessage = (SmsMoMessage) FstObjectSerializeUtil.read(msg.getBody());
//				if(smsmoMessage != null){
//					String spNumber = "", userId = "";
//					Map<String,String> moNumMap = JedisClusterUtils.getJedisInstance().hgetAll("MONUM");
//					if(moNumMap != null) {
//						userId = moNumMap.get(smsmoMessage.getDestTermID());
//						if(StringUtils.isBlank(userId)) {
//							for(Map.Entry<String, String> entry : moNumMap.entrySet()) {
//								String key = entry.getKey();
//								if(smsmoMessage.getSrcTermID().startsWith(key)){
//									if (key.length() > spNumber.length()) {
//										spNumber = key;
//										userId = entry.getValue();
//									}
//								}
//							}
//						}
//					}
//
//					BDBStoredMapFactoryImpl.INS.getQueue("SMS", "SMSMOUERY_"+userId).put(msg.getBody());
//
////
////
////					BDBStoredMapFactoryImpl.INS.getQueue("SMS", "SMSMOUERY_"+smsMtMessage.getMsgid()).put(msg.getBody());
////					if(StringUtils.equals(UserUtils.get(smsRtMessage.getSmsMt().getUserid()).getCallbackUrl(), "")) {
//
////					}
//				}
//			}
//		}catch(Exception e){
//			logger.error("{}", e);
//		}
//		return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
//	}


}
