package com.siloyou.jmsg.gateway.sgip;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.google.common.util.concurrent.RateLimiter;
import com.siloyou.jmsg.common.message.SmsMtMessage;

public class SGIPSend {
	private static final Logger logger = LoggerFactory.getLogger(SGIPSend.class);

	private ThreadPoolTaskExecutor taskExecutor;
	RateLimiter gwSpeed ;
	ArrayBlockingQueue<SmsMtMessage> hQueue = new ArrayBlockingQueue<SmsMtMessage>(1000);
	ArrayBlockingQueue<SmsMtMessage> nQueue = new ArrayBlockingQueue<SmsMtMessage>(1000);
	ArrayBlockingQueue<SmsMtMessage> lQueue = new ArrayBlockingQueue<SmsMtMessage>(1000);

	public SGIPSend(int speed) {
		gwSpeed = RateLimiter.create(speed);
		taskExecutor = new ThreadPoolTaskExecutor();
		taskExecutor.setCorePoolSize(2);
		taskExecutor.setMaxPoolSize(4);
		taskExecutor.setKeepAliveSeconds(120);
        taskExecutor.setQueueCapacity(32);
        taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        taskExecutor.initialize();
        
	}

	/**
	 * 应用启动的时候，启动发送线程.
	 */
	public void startup() {
		// 营销定时短信数量很大，我们做特殊处理线程翻倍
		for (int i = 0; i < 10 * 2; ++i) {
			taskExecutor.execute(new SendThread("营销定时任[" + i + "]", ""));
		}
	}
	
	private void sendMsg() {
		gwSpeed.acquire();
		
		
	}

	class SendThread implements Runnable {

		private String name = "";

		private String queueChoose = "";

		public SendThread(String name, String queueChoose) {
			this.name = name;
			this.queueChoose = queueChoose;
		}

		public void run() {
			for (;;) {
				try {
					 sendMsg();
				} catch (Exception e) {
					logger.error("发送消息错误", e);
				}
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					logger.error(name + "发送消息线程中断", e);
				}
			}
		}

	}
	

}
