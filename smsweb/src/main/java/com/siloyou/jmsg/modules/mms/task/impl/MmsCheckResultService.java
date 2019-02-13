package com.siloyou.jmsg.modules.mms.task.impl;

import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

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
import com.siloyou.core.common.utils.HttpRequest;
import com.siloyou.core.common.utils.SpringContextHolder;
import com.siloyou.core.common.utils.StringUtils;
import com.siloyou.core.modules.sys.utils.DictUtils;
import com.siloyou.jmsg.modules.mms.dao.JmsgMmsDataDao;
import com.siloyou.jmsg.modules.mms.entity.JmsgMmsData;

//@Service
//@Lazy(false)
public class MmsCheckResultService {
	public static Logger logger = LoggerFactory.getLogger(MmsCheckResultService.class);
	
	private static JmsgMmsDataDao jmsgMmsDataDao = SpringContextHolder.getBean(JmsgMmsDataDao.class);
	
	@PostConstruct
	public void init() {
		try {
			DefaultMQPushConsumer checkMmsConsumer = new DefaultMQPushConsumer("JmsgAppCheckMmsGroup");
			checkMmsConsumer.setNamesrvAddr(Global.getConfig("mms.send.url"));
			checkMmsConsumer.setInstanceName("checkMmsConsumer");
			checkMmsConsumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
			checkMmsConsumer.subscribe("JmsgAppTopic", "check_mms");
			checkMmsConsumer.setConsumeMessageBatchMaxSize(1);
			checkMmsConsumer.registerMessageListener(new MessageListenerConcurrently() {
				@Override
				public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs,
						ConsumeConcurrentlyContext context) {
					for (MessageExt msg : msgs) {
						@SuppressWarnings("unchecked")
						Map<String,String> mmsMsg = (Map<String,String>)JsonMapper.fromJsonString(new String(msg.getBody()), Map.class);
						
						try{
							String mmsIds = mmsMsg.get("mmsid");
							String[] mmsIdArray = mmsIds.split(";");
							for (String mmsId : mmsIdArray) {
								JmsgMmsData jmsgMmsData = jmsgMmsDataDao.get(mmsId);

								String callbackUrl = jmsgMmsData.getUser().getCallbackUrl();
								if(StringUtils.isNotBlank(callbackUrl)){//有回调地址
									callbackUrl += StringUtils.contains(callbackUrl, "\\?") ? "&" : "?";
									String userId = jmsgMmsData.getCreateBy().getId();
									String apikey = jmsgMmsData.getUser().getApikey();
									long timestamp = System.currentTimeMillis();
									String status = jmsgMmsData.getCheckStatus();
									String sign = HttpRequest.md5(apikey+userId+timestamp+apikey);
									String data = String.format("method=check_mms&mms_id=%s&timestamp=%s&sign=%s&status=%s&content=%s&appid=%s" , mmsId, timestamp, sign,status, URLEncoder.encode(DictUtils.getDictLabel(status, "check_status", "未知"),"UTF-8"),userId);
									HttpRequest.sendFormPost(callbackUrl+data, null, null, "UTF-8",3000);
								}
							}
							System.out.println("send mmscheck success");
				        }catch(Exception ex){
				            ex.printStackTrace();
				            System.out.println("send mmscheck fail");
				        }
					}
					return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
				}
			});
			checkMmsConsumer.start();
		} catch (Exception e) {

		}
	}
}
