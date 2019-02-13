package com.siloyou.jmsg.modules.api.web;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.siloyou.core.common.utils.FstObjectSerializeUtil;
import com.siloyou.core.modules.sys.utils.UserUtils;
import com.siloyou.jmsg.common.message.SmsRtMessage;
import com.siloyou.jmsg.common.storedMap.BDBStoredMapFactoryImpl;

@Service
public class SmsRTCacheListener implements /*MessageListenerConcurrently*/MessageListener {

	public static Logger logger = LoggerFactory.getLogger(SmsRTCacheListener.class);

	@Override
	public Action consume(Message message, ConsumeContext consumeContext) {
		try{
			SmsRtMessage smsRtMessage = (SmsRtMessage)FstObjectSerializeUtil.read(message.getBody());

			if(smsRtMessage.getSmsMt() != null){
				BDBStoredMapFactoryImpl.INS.getQueue("SMS", "SMSQUERY_"+smsRtMessage.getSmsMt().getUserid()).put(message.getBody());
				String callbackUrl = UserUtils.get(smsRtMessage.getSmsMt().getUserid()).getCallbackUrl();
				System.out.println("callbackurl:"+callbackUrl);
//					if(StringUtils.equals(, "QUERY")) {
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
//				SmsRtMessage smsRtMessage = (SmsRtMessage)FstObjectSerializeUtil.read(msg.getBody());
//
//				if(smsRtMessage.getSmsMt() != null){
//					BDBStoredMapFactoryImpl.INS.getQueue("SMS", "SMSQUERY_"+smsRtMessage.getSmsMt().getUserid()).put(msg.getBody());
//                    String callbackUrl = UserUtils.get(smsRtMessage.getSmsMt().getUserid()).getCallbackUrl();
//                    System.out.println("callbackurl:"+callbackUrl);
////					if(StringUtils.equals(, "QUERY")) {
////					}
//				}
//			}
//
//		}catch(Exception e){
//			logger.error("{}", e);
//		}
//
//		return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
//	}


}
