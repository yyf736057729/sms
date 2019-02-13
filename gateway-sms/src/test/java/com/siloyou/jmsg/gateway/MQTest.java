package com.siloyou.jmsg.gateway;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.alibaba.rocketmq.client.consumer.DefaultMQPullConsumer;
import com.alibaba.rocketmq.client.consumer.MQPullConsumer;
import com.alibaba.rocketmq.client.consumer.MQPullConsumerScheduleService;
import com.alibaba.rocketmq.client.consumer.PullResult;
import com.alibaba.rocketmq.client.consumer.PullTaskCallback;
import com.alibaba.rocketmq.client.consumer.PullTaskContext;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.common.message.MessageQueue;
import com.alibaba.rocketmq.common.protocol.heartbeat.MessageModel;
import com.google.common.collect.Maps;

public class MQTest {
	private static final Map<MessageQueue, Long> OFFSE_TABLE = new HashMap<MessageQueue, Long>();

	public static void main(String[] args) throws MQClientException {
		DefaultMQPullConsumer pullConsumer = new DefaultMQPullConsumer("CustomPullGroup");
		pullConsumer.setMessageModel(MessageModel.BROADCASTING); //广播消费
		pullConsumer.setNamesrvAddr("101.37.117.13:9876");
		//1. 初始化全部网关 2. 启动
		ConcurrentHashMap<String, PullTaskCallback> callbackTable = new ConcurrentHashMap<String, PullTaskCallback>();
		
		MQTest mqTest = new MQTest();
		MQPullConsumerScheduleService pullConsumerScheduleService = mqTest.getMqPullConsumerService(pullConsumer, callbackTable);
		
	}
	
	class PullTaskCallbackImpl implements PullTaskCallback {
		@Override
		public void doPullTask(MessageQueue mq, PullTaskContext context) {
			MQPullConsumer consumer = context.getPullConsumer();
			try {
				
				//查询通道列表，循环获取
				
				long offset = consumer.fetchConsumeOffset(mq, false);
				if (offset < 0)
					offset = 0;

				PullResult pullResult = consumer.pull(mq, "*", offset, 32);
				System.out.printf("%s%n", offset + "\t" + mq + "\t" + pullResult);
				switch (pullResult.getPullStatus()) {
				case FOUND:
					break;
				case NO_MATCHED_MSG:
					break;
				case NO_NEW_MSG:
				case OFFSET_ILLEGAL:
					break;
				default:
					break;
				}
				consumer.updateConsumeOffset(mq, pullResult.getNextBeginOffset());

				context.setPullNextDelayTimeMillis(100);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public MQPullConsumerScheduleService getMqPullConsumerService(DefaultMQPullConsumer pullConsumer, ConcurrentHashMap<String, PullTaskCallback> callbackTable) throws MQClientException {
		final MQPullConsumerScheduleService scheduleService = new MQPullConsumerScheduleService("CustomPullGroup");
		scheduleService.setPullThreadNums(10);	//线程数
		scheduleService.setDefaultMQPullConsumer(pullConsumer);	//Pull实现
		scheduleService.setCallbackTable(callbackTable);
		scheduleService.start();
		
		return scheduleService;
	}

	public void testPull() throws MQClientException {
		DefaultMQPullConsumer consumer = new DefaultMQPullConsumer("PullTestGroup");
		consumer.setNamesrvAddr("");
		consumer.setClientCallbackExecutorThreads(10); //线程数
		consumer.start();

		//多线程执行，共想一个consoumer，每个一个线程，先获取 验证码、行业、群发 
		
		// 验证码 单独循环 -- 
		Set<MessageQueue> mqs = consumer.fetchSubscribeMessageQueues("SMSMT");
		for (MessageQueue mq : mqs) {
			System.out.printf("Consume from the queue: " + mq + "%n");
			SINGLE_MQ: while (true) {
				try {
					PullResult pullResult = consumer.pullBlockIfNotFound(mq, "YD5001", getMessageQueueOffset(mq), 32);
					System.out.printf("%s%n", pullResult);
					putMessageQueueOffset(mq, pullResult.getNextBeginOffset());
					switch (pullResult.getPullStatus()) {
					case FOUND:
						break;
					case NO_MATCHED_MSG:
						break;
					case NO_NEW_MSG:
						break SINGLE_MQ;
					case OFFSET_ILLEGAL:
						break;
					default:
						break;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		consumer.shutdown();
	}

	private static long getMessageQueueOffset(MessageQueue mq) {
		Long offset = OFFSE_TABLE.get(mq);
		if (offset != null)
			return offset;

		return 0;
	}

	private static void putMessageQueueOffset(MessageQueue mq, long offset) {
		OFFSE_TABLE.put(mq, offset);
	}
}
