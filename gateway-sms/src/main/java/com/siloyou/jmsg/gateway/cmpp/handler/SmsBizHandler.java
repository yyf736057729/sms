package com.siloyou.jmsg.gateway.cmpp.handler;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicLong;

import com.siloyou.jmsg.common.util.DateUtils;
import com.siloyou.jmsg.common.util.LimitQueue;
import com.siloyou.jmsg.gateway.cmpp.CmppGatewayFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.Application;
import com.siloyou.jmsg.common.gateway.SmsGateWay;
import com.siloyou.jmsg.common.storedMap.BDBStoredMapFactoryImpl;
import com.siloyou.jmsg.gateway.api.GatewayFactory;
import com.siloyou.jmsg.gateway.cmpp.message.CmppGateWayMessage;
import com.zx.sms.codec.cmpp.msg.CmppDeliverRequestMessage;
import com.zx.sms.codec.cmpp.msg.CmppDeliverResponseMessage;
import com.zx.sms.codec.cmpp.msg.CmppSubmitResponseMessage;
import com.zx.sms.connect.manager.CMPPEndpointManager;
import com.zx.sms.connect.manager.EventLoopGroupFactory;
import com.zx.sms.connect.manager.ExitUnlimitCirclePolicy;
import com.zx.sms.handler.api.AbstractBusinessHandler;
import com.zx.sms.session.cmpp.SessionState;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.Future;

public class SmsBizHandler extends AbstractBusinessHandler {
	private static final Logger logger = LoggerFactory.getLogger(SmsBizHandler.class);
	private int rate = 2;

	private AtomicLong cnt = new AtomicLong();
	private long lastNum = 0;
	private Map<String, Serializable> storeWaitReportMap;
	private CmppGateWayMessage gateWayMessage ;
	//	private GatewayFactory gatewayFactory = Application.applicationContext.getBean(GatewayFactory.class);
	private CmppGatewayFactory cmppGatewayFactory = Application.applicationContext.getBean(CmppGatewayFactory.class);

	public static final HashMap<String, LimitQueue<String>> spend_map = new HashMap<>();
	
	@Override
	public String name() {
		return this.getEndpointEntity().getId();
	}

	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if (evt == SessionState.Connect) {
			final Channel ch = ctx.channel();
			storeWaitReportMap = BDBStoredMapFactoryImpl.INS.buildMap(getEndpointEntity().getId(), "Report_" +getEndpointEntity().getId());
			EventLoopGroupFactory.INS.submitUnlimitCircleTask(new Callable<Boolean>() {
				@Override
				public Boolean call() throws Exception {
					long nowcnt = cnt.get();
					logger.info("ID:{}, Totle Receive Msg Num:{},   speed : {}/s", name(), nowcnt, (nowcnt - lastNum) / rate);

					//发送速度入队列
					if(spend_map.containsKey(name())){
						LimitQueue<String> limitQueue =spend_map.get(name());
						limitQueue.offer(DateUtils.getDateTime()+"  ID:"+name()+", Totle Receive Msg Num:"+nowcnt+", speed :"+(nowcnt - lastNum) / rate+ "/s");
					}else {
						LimitQueue<String> limitQueue = new LimitQueue<>(120);
						limitQueue.offer(DateUtils.getDateTime()+"  ID:"+name()+", Totle Receive Msg Num:"+nowcnt+", speed :"+(nowcnt - lastNum) / rate+ "/s");
						spend_map.put(name(),limitQueue);
					}

					lastNum = nowcnt;
					return true;
				}
			}, new ExitUnlimitCirclePolicy() {
				@Override
				public boolean notOver(Future future) {
					return ch.isActive();
				}
			}, rate * 1000);
			
			//启动发送线程
			synchronized (this) {
				SmsGateWay cmppSmsGateWay = cmppGatewayFactory.getSmsGateWay(getEndpointEntity().getId());
				if(cmppSmsGateWay == null) {
					int limit = ((CmppGateWayMessage) cmppGatewayFactory.getCmppGateWayMessage()).map.get(getEndpointEntity().getId()).getWriteLimit();
					cmppSmsGateWay = new SmsGateWay(getEndpointEntity().getId(), limit, cmppGatewayFactory);
					cmppSmsGateWay.startup();
				}
			}
		}
		ctx.fireUserEventTriggered(evt);
	}
	
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {

		logger.info("=======  Client Connection closed. channel:{}", ctx.channel());

		String gatewayid = getEndpointEntity().getId();
		this.cmppGatewayFactory.closev1(gatewayid); //getSmsGateWay(gatewayid).shutdown();
		
		Thread.sleep(10000);
		CMPPEndpointManager.INS.openEndpoint(getEndpointEntity());

	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if (msg instanceof CmppDeliverRequestMessage) {
			CmppDeliverRequestMessage e = (CmppDeliverRequestMessage) msg;
			CmppDeliverResponseMessage responseMessage = new CmppDeliverResponseMessage(e.getHeader().getSequenceId());
			responseMessage.setMsgId(e.getMsgId());
			responseMessage.setResult(0);
			ctx.writeAndFlush(responseMessage);
			if (e.isReport()) {
				System.out.println("推送状态报告");
				System.out.println("回执MsgId："+e.getMsgId()+",短信内容："+e.getMsgContent());
				//组装状态回执
				e.setAttachment(storeWaitReportMap.remove(e.getReportRequestMessage().getMsgId().toString()));
				//推送状态报告
				cmppGatewayFactory.getCmppGateWayMessage().sendSmsRTMessage(e, getEndpointEntity().getId());
			} else {
				System.out.println("推送用户上行");
				//推送用户上行
				cmppGatewayFactory.getCmppGateWayMessage().sendSmsMOMessage(e, getEndpointEntity().getId());
			}
		} else if (msg instanceof CmppSubmitResponseMessage) { //统计发送
			cnt.incrementAndGet();
			CmppSubmitResponseMessage e = (CmppSubmitResponseMessage) msg;
			//等待状态报告返回
			storeWaitReportMap.put(e.getMsgId().toString(), e.getAttachment());
			//推送发送回执消息
			cmppGatewayFactory.getCmppGateWayMessage().sendSmsSRMessage(e, getEndpointEntity().getId());
		} else {
			ctx.fireChannelRead(msg);
		}
	}

	public SmsBizHandler clone() throws CloneNotSupportedException {
		SmsBizHandler ret = (SmsBizHandler) super.clone();
		ret.cnt = new AtomicLong();
		ret.lastNum = 0;
		ret.storeWaitReportMap=null;
		return ret;
	}

}
