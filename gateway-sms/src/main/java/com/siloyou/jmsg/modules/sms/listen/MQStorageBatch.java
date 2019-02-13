package com.siloyou.jmsg.modules.sms.listen;

import java.util.Map.Entry;
import java.util.concurrent.ConcurrentMap;

import javax.annotation.PreDestroy;

import com.aliyun.openservices.ons.api.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.google.common.collect.Maps;
import com.Application;
import com.siloyou.jmsg.common.mq.MQCustomerFactory;

@Service
public class MQStorageBatch {
	Logger logger = LoggerFactory.getLogger(MQStorageBatch.class);
	
	@Autowired
	private SmsMTBATCHListener smsMTListener;
	
	ConcurrentMap<String, Consumer> consumerMap = Maps.newConcurrentMap();
	
	public void initStorage(String appCode){
		String key = "SMSMT" + appCode + "B";
		Consumer storageConsumer = consumerMap.get(key);
		if(storageConsumer == null) {
			storageConsumer = MQCustomerFactory.getPushConsumer(key+ "StorageGrp");
			try {
//				storageConsumer.setConsumeMessageBatchMaxSize(32);
//				storageConsumer.setInstanceName(key + "StorageIns");
				storageConsumer.subscribe(key, "*",smsMTListener);
//				storageConsumer.setMessageListener(smsMTListener);
				storageConsumer.start();
				consumerMap.put(key, storageConsumer);
				logger.info("{}, 状态报告推送程序已启动", appCode);
			} catch (Exception e) {
				logger.error(Application.appCode+"状态报告推送程序异常", e);
			}
		}
	}
	
	@PreDestroy
	void destroy() {
		for(Entry<String, Consumer> entry : consumerMap.entrySet()){
			if ( null != entry.getValue() ) {
				entry.getValue().shutdown();
			}
		}
	}
}
