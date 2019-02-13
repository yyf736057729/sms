package com.siloyou.jmsg.modules.api.cmpp;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicLong;

import com.zx.sms.codec.cmpp.msg.CmppDeliverResponseMessage;
import com.zx.sms.codec.cmpp.msg.CmppSubmitRequestMessage;
import com.zx.sms.codec.cmpp.msg.CmppSubmitResponseMessage;
import com.zx.sms.handler.api.AbstractBusinessHandler;
import com.zx.sms.connect.manager.EventLoopGroupFactory;
import com.zx.sms.connect.manager.ExitUnlimitCirclePolicy;
import com.zx.sms.connect.manager.cmpp.CMPPEndpointEntity;
import com.zx.sms.connect.manager.cmpp.CMPPServerChildEndpointEntity;
import com.zx.sms.session.cmpp.SessionState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.siloyou.jmsg.common.util.SystemClock;
import com.siloyou.jmsg.gateway.Result;
//import com.tingfv.sms.codec.cmpp.msg.CmppDeliverResponseMessage;
//import com.tingfv.sms.codec.cmpp.msg.CmppSubmitRequestMessage;
//import com.tingfv.sms.codec.cmpp.msg.CmppSubmitResponseMessage;
//import com.tingfv.sms.common.util.CachedMillisecondClock;
//import com.tingfv.sms.common.util.MsgId;
//import com.tingfv.sms.connect.manager.EventLoopGroupFactory;
//import com.tingfv.sms.connect.manager.ExitUnlimitCirclePolicy;
//import com.tingfv.sms.connect.manager.cmpp.CMPPEndpointEntity;
//import com.tingfv.sms.connect.manager.cmpp.CMPPServerChildEndpointEntity;
//import com.tingfv.sms.handler.api.AbstractBusinessHandler;
//import com.tingfv.sms.session.cmpp.SessionState;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.Future;

public class CmppGateWayHandler extends AbstractBusinessHandler {
	private static final Logger logger = LoggerFactory.getLogger(CmppGateWayHandler.class);
	private int rate = 2;

	private AtomicLong cnt = new AtomicLong();
	private long lastNum = 0;
	private GateWayMessage gateWayMessage ;
	
	@Override
	public String name() {
		return this.getEndpointEntity().getId();
	}

	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if (evt == SessionState.Connect) {
			final Channel ch = ctx.channel();
			EventLoopGroupFactory.INS.submitUnlimitCircleTask(new Callable<Boolean>() {
				@Override
				public Boolean call() throws Exception {
					long nowcnt = cnt.get();
					logger.info("ID:{}, Totle Receive Msg Num:{},   speed : {}/s", name(), nowcnt, (nowcnt - lastNum) / rate);
					lastNum = nowcnt;
					return true;
				}
			}, new ExitUnlimitCirclePolicy() {
				@Override
				public boolean notOver(Future future) {
					return ch.isActive();
				}
			}, rate * 1000);
		}
		ctx.fireUserEventTriggered(evt);
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		
		if (msg instanceof CmppSubmitRequestMessage) {  //提交消息
			CmppSubmitRequestMessage e = (CmppSubmitRequestMessage) msg;
			
//			e.setMsgid(new MsgId());
			//发送 UMT消息
			long startTime = SystemClock.now();
            Result result =
                gateWayMessage.sendSmsUMTMessage(e,
                    getEndpointEntity().getId(),
						((CmppSubmitRequestMessage) msg).getMsgsrc());
			
			logger.info("接收消息：userid:{}, msgid:{}, phone:{}, cusmsgid:{}, time: {}ms", 
					getEndpointEntity().getId(), e.getMsgid(), e.getDestterminalId(), e.getAttachment(), (SystemClock.now() - startTime));
			CmppSubmitResponseMessage resp = new CmppSubmitResponseMessage(e.getHeader().getSequenceId());
			resp.setMsgId(e.getMsgid());
			if (result.isSuccess())
			{
			    resp.setResult(0);
			}
			else
			{
			    resp.setResult(9);
			    logger.error(result.getErrorMsg());
			}
			
			ctx.writeAndFlush(resp);

			cnt.incrementAndGet();
		} else if (msg instanceof CmppDeliverResponseMessage) {	//上行及状态响应
			
			CmppDeliverResponseMessage e = (CmppDeliverResponseMessage) msg;
			if(e.getAttachment() != null) {
				gateWayMessage.sendSmsPushResultMessage(e, ((CMPPServerChildEndpointEntity)getEndpointEntity()).getUserName());
			}
			logger.info("cmpp客户端响应消息[clientid:{}, msgId:{}, bizid:{}, result:{}]", getEndpointEntity().getId(), e.getMsgId(), e.getAttachment(), e.getResult());

		} else {
			ctx.fireChannelRead(msg);
		}
	}

	public CmppGateWayHandler clone() throws CloneNotSupportedException {
		CmppGateWayHandler ret = (CmppGateWayHandler) super.clone();
		ret.cnt = new AtomicLong();
		ret.lastNum = 0;
		ret.gateWayMessage = new GateWayMessage();
		return ret;
	}

}
