package com.siloyou.jmsg.gateway.sgip;

import net.zoneland.gateway.comm.sgip.SGIPSMProxy;
import net.zoneland.gateway.comm.sgip.message.SGIPMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.siloyou.jmsg.common.workqueue.BaseWorker;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SGIPWorker {

private static final Logger logger = LoggerFactory.getLogger(BaseWorker.class);
	
	protected volatile boolean isRunning = true;
	protected String name;
	protected Queue<SGIPMessage> msgQueue = new ConcurrentLinkedQueue<SGIPMessage>();
	public SGIPSMProxy proxy;
	
	public SGIPWorker(SGIPSMProxy proxy) {
		this.proxy = proxy;
	}
	
    public void closeWorker() {
        isRunning = false;
    }
    
    public void addMessage(SGIPMessage c) {
        if (isRunning) {
            synchronized (msgQueue) {
                msgQueue.offer(c);
            }
        }
    }
    
    public void run() {
		while(true){
			if (msgQueue.isEmpty()) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} else {
				SGIPMessage message =  null;
				while ((message = msgQueue.poll()) != null) {
					try {
						proxy.send(message);
					}catch(Throwable e) {
						logger.error("", e);
					}
				}
			}
		}
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
    
	public int getQueueSize() {
		return this.msgQueue.size();
	}
	
}
