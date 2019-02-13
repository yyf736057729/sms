package com.siloyou.jmsg.common.gateway;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicLong;

import com.aliyun.openservices.ons.api.Consumer;
import com.aliyun.openservices.ons.api.MessageListener;
import com.siloyou.jmsg.gateway.api.CMPPGatewayFactory;
import com.siloyou.jmsg.gateway.cmpp.CmppGatewayFactory;
import com.zx.sms.connect.manager.EventLoopGroupFactory;
import com.zx.sms.connect.manager.ExitUnlimitCirclePolicy;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.google.common.util.concurrent.RateLimiter;
import com.siloyou.jmsg.common.message.SmsMtMessage;
import com.siloyou.jmsg.common.mq.MQCustomerFactory;
import com.siloyou.jmsg.gateway.Result;
import com.siloyou.jmsg.gateway.api.GatewayFactory;
//import com.tingfv.sms.connect.manager.EventLoopGroupFactory;
//import com.tingfv.sms.connect.manager.ExitUnlimitCirclePolicy;

import io.netty.util.concurrent.Future;

public class SmsGateWay {
	private static final Logger logger = LoggerFactory.getLogger(GatewayFactory.class);

	private String gatewayId;
	private CMPPGatewayFactory gatewayFactory ;
	private MessageListener gateWayMTListener ;
	private RateLimiter limiter ;

//	DefaultMQPushConsumer highConsumer;
//	DefaultMQPushConsumer normalConsumer;
//	DefaultMQPushConsumer lowConsumer;
	Consumer highConsumer;
	Consumer normalConsumer;
	Consumer lowConsumer;

    public Consumer getHighConsumer() {
        return highConsumer;
    }

    public void setHighConsumer(Consumer highConsumer) {
        this.highConsumer = highConsumer;
    }

    public Consumer getNormalConsumer() {
        return normalConsumer;
    }

    public void setNormalConsumer(Consumer normalConsumer) {
        this.normalConsumer = normalConsumer;
    }

    public Consumer getLowConsumer() {
        return lowConsumer;
    }

    public void setLowConsumer(Consumer lowConsumer) {
        this.lowConsumer = lowConsumer;
    }

    //速率
	private AtomicLong cnt = new AtomicLong();
	private long lastNum = 0;
	private boolean isRun = true;

	public SmsGateWay(String gatewayId, int permitsPerSecond, CMPPGatewayFactory gatewayFactory) {
		this.gatewayId = gatewayId;

		//设置网关速率
		if(permitsPerSecond == 0) permitsPerSecond = 2000;
		limiter = RateLimiter.create(permitsPerSecond);

		//设置网关工厂
		this.gatewayFactory = gatewayFactory;

		//消费监听
		gateWayMTListener = new GateWayMTListener(this);
		System.out.println("添加:------------------id:"+gatewayId);
		gatewayFactory.addSmsGateWay(gatewayId, this);
	}

	public Result send(SmsMtMessage message) {
	
		if (StringUtils.startsWith(message.getSendStatus(), "F")) {
			return new Result(message.getSendStatus(), "业务错误");
		}

		//速率控制
		int size = (int) message.getContentSize();
		if(size == 0) {
			size = 1;
		}

		limiter.acquire(size);
//		cnt.getAndIncrement();
		cnt.getAndAdd(size);

		//测试 TODO
//		return new Result("T100", "成功");

		//发送
		return gatewayFactory.sendMsg(message);
	}

	public Result send(SmsMtMessage message, int level) {
		
		if (StringUtils.startsWith(message.getSendStatus(), "F")) {
			return new Result(message.getSendStatus(), "业务错误");
		}

		//速率控制
		int size = (int) message.getContentSize();
		if(size == 0) {
			size = 1;
		}

		limiter.acquire(size);
//		cnt.getAndIncrement();
		cnt.getAndAdd(size);

		//测试 TODO
//		return new Result("T100", "成功");

		//发送
		return gatewayFactory.sendMsg(message);
	}

	//启动网关
	public void startup() {

		isRun = true;

		//启动速率控制
		EventLoopGroupFactory.INS.submitUnlimitCircleTask(new Callable<Boolean>(){
			@Override
			public Boolean call() throws Exception {

				long nowcnt = cnt.get();
				logger.info("GWID:{}, rate:{}, Totle Receive Msg Num:{},   speed : {}/s", gatewayId, limiter.getRate() , nowcnt, (nowcnt - lastNum)/10);
				lastNum = nowcnt;
				return true;
			}
		}, new ExitUnlimitCirclePolicy() {
			@Override
			public boolean notOver(Future future) {
				return isRun;
			}
		}, 10000);

		// 启动执行线程
		if(highConsumer == null) {
			startQueueTask(gatewayId, "HIGH", highConsumer);
		}
		//yyf注释
		if(normalConsumer == null)
			startQueueTask(gatewayId, "NORMAL", normalConsumer);

		if(lowConsumer == null) {
			startQueueTask(gatewayId, "LOW", lowConsumer);
		}
	}

	private void startQueueTask(String gatewayId, String level, Consumer singlePushConsumer) {
		singlePushConsumer = MQCustomerFactory.getPushConsumer(SmsGateWayConfig.getSendConsumerGroup(gatewayId, level));
		try {
//			singlePushConsumer.setConsumeThreadMin(1);
//			singlePushConsumer.setConsumeThreadMax(5);
//			singlePushConsumer.setInstanceName(SmsGateWayConfig.getSendConsumerIns(gatewayId, level));
			singlePushConsumer.subscribe(SmsGateWayConfig.getAppTopic(level), "*",gateWayMTListener);
			System.out.println("TOPIC:"+SmsGateWayConfig.getAppTopic(level)+"-----------"+"TAG:"+SmsGateWayConfig.getSendConsumerTag(gatewayId, level));
//			String appTopic = SmsGateWayConfig.getAppTopic(level);
//			singlePushConsumer.subscribe(appTopic, gatewayId);//①
//			System.out.println("topic:"+SmsGateWayConfig.getAppTopic(level));
//			System.out.println("topic--:"+SmsGateWayConfig.getSendConsumerTag(gatewayId, level));
//			singlePushConsumer.setMessageListener(gateWayMTListener);
			singlePushConsumer.start();
			logger.info("网关：{}, 队列级别：{} 发消费程序已启动", gatewayId, level);
		} catch (Exception e) {
			logger.error("网关:" + gatewayId+ ", 队列级别："+level +"消费程序启动异常", e);
		}
	}

	public void shutdown() {
		isRun = false;

		if(highConsumer != null)
			highConsumer.shutdown();

		if(normalConsumer != null)
			normalConsumer.shutdown();

		if(lowConsumer != null)
			lowConsumer.shutdown();
	}

	public void setRate(int permitsPerSecond) {
		this.limiter.setRate(permitsPerSecond);
	}

	public CMPPGatewayFactory getGatewayFactory() {
		return gatewayFactory ;
	}

	public void setGatewayFactory(CMPPGatewayFactory gatewayFactory) {

		this.gatewayFactory = gatewayFactory;
	}
}
