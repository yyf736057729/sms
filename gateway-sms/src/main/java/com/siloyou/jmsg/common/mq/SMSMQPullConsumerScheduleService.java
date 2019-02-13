package com.siloyou.jmsg.common.mq;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;

import com.alibaba.rocketmq.client.consumer.DefaultMQPullConsumer;
import com.alibaba.rocketmq.client.consumer.MessageQueueListener;
import com.alibaba.rocketmq.client.consumer.PullTaskContext;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.log.ClientLogger;
import com.alibaba.rocketmq.common.ThreadFactoryImpl;
import com.alibaba.rocketmq.common.message.MessageQueue;
import com.alibaba.rocketmq.common.protocol.heartbeat.MessageModel;

/**
 * Schedule service for pull consumer
 *
 * @author shijia.wxr
 */
public class SMSMQPullConsumerScheduleService {
	private final Logger log = ClientLogger.getLog();
	private DefaultMQPullConsumer defaultMQPullConsumer;
	private int pullThreadNums = 20;
	private ConcurrentHashMap<String /* tag */, SMSPullTaskCallback> callbackTable = new ConcurrentHashMap<String, SMSPullTaskCallback>();
	private ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;
	private final MessageQueueListener messageQueueListener = new MessageQueueListenerImpl();

	private final ConcurrentHashMap<SMSMessageQueue, PullTaskImpl> taskTable = new ConcurrentHashMap<SMSMessageQueue, PullTaskImpl>();

	class MessageQueueListenerImpl implements MessageQueueListener {
		@Override
		public void messageQueueChanged(String topic, Set<MessageQueue> mqAll, Set<MessageQueue> mqDivided) {
			MessageModel messageModel = SMSMQPullConsumerScheduleService.this.defaultMQPullConsumer.getMessageModel();
			switch (messageModel) {
			case BROADCASTING:
				SMSMQPullConsumerScheduleService.this.putTask(topic, mqAll);
				break;
			case CLUSTERING:
				SMSMQPullConsumerScheduleService.this.putTask(topic, mqDivided);
				break;
			default:
				break;
			}
		}
	}

	class PullTaskImpl implements Runnable {
		private final SMSMessageQueue messageQueue;
		private volatile boolean cancelled = false;

		public PullTaskImpl(final SMSMessageQueue messageQueue) {
			this.messageQueue = messageQueue;
		}

		@Override
		public void run() {
			String tag = this.messageQueue.getTag();
			if (!this.isCancelled()) {
				SMSPullTaskCallback pullTaskCallback = SMSMQPullConsumerScheduleService.this.callbackTable.get(tag);
				if (pullTaskCallback != null) {
					final PullTaskContext context = new PullTaskContext();
					context.setPullConsumer(SMSMQPullConsumerScheduleService.this.defaultMQPullConsumer);
					try {
						pullTaskCallback.doPullTask(this.messageQueue, context);
					} catch (Throwable e) {
						context.setPullNextDelayTimeMillis(1000);
						log.error("doPullTask Exception", e);
					}

					if (!this.isCancelled()) {
						SMSMQPullConsumerScheduleService.this.scheduledThreadPoolExecutor.schedule(this,
								context.getPullNextDelayTimeMillis(), TimeUnit.MILLISECONDS);
					} else {
						log.warn("The Pull Task is cancelled after doPullTask, {}", messageQueue);
					}
				} else {
					log.warn("Pull Task Callback not exist , {}", tag);
				}
			} else {
				log.warn("The Pull Task is cancelled, {}", messageQueue);
			}
		}

		public boolean isCancelled() {
			return cancelled;
		}

		public void setCancelled(boolean cancelled) {
			this.cancelled = cancelled;
		}

		public SMSMessageQueue getMessageQueue() {
			return messageQueue;
		}
	}

	public SMSMQPullConsumerScheduleService(final String consumerGroup) {
	        this.defaultMQPullConsumer = new DefaultMQPullConsumer(consumerGroup);
	        this.defaultMQPullConsumer.setMessageModel(MessageModel.BROADCASTING);
	}

	public void putTask(String topic, Set<MessageQueue> mqNewSet) {
		Iterator<Entry<SMSMessageQueue, PullTaskImpl>> it = this.taskTable.entrySet().iterator();
		while (it.hasNext()) {
			Entry<SMSMessageQueue, PullTaskImpl> next = it.next();
			if (next.getKey().getTopic().equals(topic)) {
				
				//转换
				
				
				if (!mqNewSet.contains(next.getKey())) {
					next.getValue().setCancelled(true);
					it.remove();
				}
			}
		}

//		for (SMSMessageQueue mq : mqNewSet) {
//			if (!this.taskTable.containsKey(mq)) {
//				PullTaskImpl command = new PullTaskImpl(mq);
//				this.taskTable.put(mq, command);
//				this.scheduledThreadPoolExecutor.schedule(command, 0, TimeUnit.MILLISECONDS);
//			}
//		}
	}

	public void start() throws MQClientException {
		final String group = this.defaultMQPullConsumer.getConsumerGroup();
		this.scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(//
				this.pullThreadNums, //
				new ThreadFactoryImpl("PullMsgThread-" + group)//
		);

		this.defaultMQPullConsumer.setMessageQueueListener(this.messageQueueListener);

		this.defaultMQPullConsumer.start();

		log.info("MQPullConsumerScheduleService start OK, {} {}", this.defaultMQPullConsumer.getConsumerGroup(),
				this.callbackTable);
	}

//	public void registerPullTaskCallback(final String topic, final SMSPullTaskCallback callback) {
//		this.callbackTable.put(topic, callback);
//		this.defaultMQPullConsumer.registerMessageQueueListener(topic, null);
//	}

	public void shutdown() {
		if (this.scheduledThreadPoolExecutor != null) {
			this.scheduledThreadPoolExecutor.shutdown();
		}

		if (this.defaultMQPullConsumer != null) {
			this.defaultMQPullConsumer.shutdown();
		}
	}

	public ConcurrentHashMap<String, SMSPullTaskCallback> getCallbackTable() {
		return callbackTable;
	}

	public void setCallbackTable(ConcurrentHashMap<String, SMSPullTaskCallback> callbackTable) {
		this.callbackTable = callbackTable;
	}

	public int getPullThreadNums() {
		return pullThreadNums;
	}

	public void setPullThreadNums(int pullThreadNums) {
		this.pullThreadNums = pullThreadNums;
	}

	public DefaultMQPullConsumer getDefaultMQPullConsumer() {
		return defaultMQPullConsumer;
	}

	public void setDefaultMQPullConsumer(DefaultMQPullConsumer defaultMQPullConsumer) {
		this.defaultMQPullConsumer = defaultMQPullConsumer;
	}

	public MessageModel getMessageModel() {
		return this.defaultMQPullConsumer.getMessageModel();
	}

	public void setMessageModel(MessageModel messageModel) {
		this.defaultMQPullConsumer.setMessageModel(messageModel);
	}
}
