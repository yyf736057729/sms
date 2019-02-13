package com.siloyou.jmsg.modules.mms.task.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import com.alibaba.rocketmq.common.consumer.ConsumeFromWhere;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.siloyou.core.common.config.Global;
import com.siloyou.core.common.mapper.JsonMapper;
import com.siloyou.core.common.utils.EmailUtil;
import com.siloyou.core.common.utils.SendMailUtil;
import com.siloyou.core.common.utils.SpringContextHolder;

//@Service
//@Lazy(false)
public class MmsCheckMailService {
	public static Logger logger = LoggerFactory.getLogger(MmsCheckMailService.class);
	private EmailUtil email = new EmailUtil("smtp.exmail.qq.com", 465, 0, true, "xurui@xxx.com","2380915Xu",true);
	@PostConstruct
	public void init() {
		try {
			DefaultMQPushConsumer smsStatusConsumer = new DefaultMQPushConsumer("JmsgAppGroup");
			smsStatusConsumer.setNamesrvAddr(Global.getConfig("mms.send.url"));
			smsStatusConsumer.setInstanceName("smsStatusConsumer");
			smsStatusConsumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
			smsStatusConsumer.subscribe("JmsgAppTopic", "create_mms");
			smsStatusConsumer.setConsumeMessageBatchMaxSize(500);
			smsStatusConsumer.registerMessageListener(new MessageListenerConcurrently() {
				@Override
				public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs,
						ConsumeConcurrentlyContext context) {
					for (MessageExt msg : msgs) {
						@SuppressWarnings("unchecked")
						Map<String,String> mmsMsg = (Map<String,String>)JsonMapper.fromJsonString(new String(msg.getBody()), Map.class);
						
						try{
				            email.sendEmail(
				                    "xurui@xxx.com",
				                    "企业彩秀",
				                    mmsMsg.get("toaddress"),
				                    "彩信审核通知：您有待审彩信," + mmsMsg.get("subject"),
				                    "<html><body>您有待审彩信，主题: "+mmsMsg.get("subject")+" ,请登录<a href=\"http://114.55.90.98:8800/\">企业彩秀平台</a>进行审核。</body></html>",
				                    null);
				            System.out.println("send out successfully");
				        }catch(Exception ex){
				            ex.printStackTrace();
				            System.out.println("send fail");
				        }
					}
					return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
				}
			});
			smsStatusConsumer.start();
		} catch (Exception e) {

		}
	}
}
