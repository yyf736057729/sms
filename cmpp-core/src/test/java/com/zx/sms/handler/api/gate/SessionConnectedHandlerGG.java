package com.zx.sms.handler.api.gate;

import com.zx.sms.BaseMessage;
import com.zx.sms.codec.cmpp.msg.*;
import com.zx.sms.common.util.CachedMillisecondClock;
import com.zx.sms.common.util.ChannelUtil;
import com.zx.sms.common.util.MsgId;
import com.zx.sms.connect.manager.EventLoopGroupFactory;
import com.zx.sms.connect.manager.ExitUnlimitCirclePolicy;
import com.zx.sms.connect.manager.ServerEndpoint;
import com.zx.sms.connect.manager.cmpp.CMPPEndpointEntity;
import com.zx.sms.handler.api.AbstractBusinessHandler;
import com.zx.sms.session.cmpp.SessionState;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 
 * @author Lihuanghe(18852780@qq.com)
 *
 */
public class SessionConnectedHandlerGG extends AbstractBusinessHandler {
	private static final Logger logger = LoggerFactory.getLogger(SessionConnectedHandlerGG.class);

	private AtomicInteger totleCnt = new AtomicInteger(10);
	private volatile boolean inited = false;
	private long lastNum = 0;
	public AtomicInteger getTotleCnt() {
		return totleCnt;
	}
	public void setTotleCnt(AtomicInteger totleCnt) {
		this.totleCnt = totleCnt;
	}

	public SessionConnectedHandlerGG(){
	}

	public SessionConnectedHandlerGG(AtomicInteger t){
		totleCnt = t;
	}

	public SessionConnectedHandlerGG(int t){
		totleCnt = new AtomicInteger(t);
	}
	
	@Override
	public void userEventTriggered(final ChannelHandlerContext ctx, Object evt) throws Exception {
		final AtomicInteger tmptotal = new AtomicInteger(totleCnt.get());
		if (evt == SessionState.Connect) {
		
			final CMPPEndpointEntity finalentity = (CMPPEndpointEntity)getEndpointEntity();
			final Channel ch = ctx.channel();
			EventLoopGroupFactory.INS.submitUnlimitCircleTask(new Callable<Boolean>() {
				private BaseMessage createTestReq(String content) {
					
					if (finalentity instanceof ServerEndpoint) {
						CmppDeliverRequestMessage msg = new CmppDeliverRequestMessage();
//						msg.setDestId("17338147110");
						msg.setDestId("17326060370");
						msg.setLinkid("0000");
//						msg.setMsgContent(sb.toString());

						msg.setMsgContent("【泰木谷】您的验证码是：866795");
//						msg.setMsgContent("【VIP乐园】您的验证码:767826,如非本人操作，请忽略本短信。");
//						msg.setMsgContent("【安智】您好，您的验证码是：447640，若非本人操作请忽略。");

						msg.setMsgId(new MsgId());

						msg.setServiceid("10086");
						msg.setSrcterminalId(String.valueOf(System.nanoTime()));
						msg.setSrcterminalType((short) 1);
//						msg.setMsgContent(new SmsMmsNotificationMessage("http://www.baidu.com/abc/sfd",50*1024));
						
						return msg;
					} else {
						CmppSubmitRequestMessage msg = new CmppSubmitRequestMessage();

//						msg.setDestterminalId("17338147110");
						msg.setDestterminalId("17326060370");
						msg.setLinkID("0000");

//						msg.setMsgContent("【泰木谷】您的验证码是：866795");
//						msg.setMsgContent("【VIP乐园】您的验证码:767826,如非本人操作，请忽略本短信。");
//						msg.setMsgContent("【安智】您好，您的验证码是：447640，若非本人操作请忽略。");
						msg.setMsgContent("【广东奕迅科技】您好，您的验证码是：447640，若非本人操作请忽略。");


						msg.setRegisteredDelivery((short)1);
						msg.setMsgid(new MsgId());
						msg.setServiceId("10086");
						msg.setSrcId(finalentity.getSpCode());
						msg.setMsgsrc(finalentity.getUserName());
//						msg.setMsgContent(new SmsMmsNotificationMessage("http://www.baidu.com/abc/sfd",50*1024));
						/*
						SgipSubmitRequestMessage requestMessage = new SgipSubmitRequestMessage();
		    			requestMessage.setTimestamp(msg.getTimestamp());
		    			requestMessage.setSpnumber("10086");
		    			requestMessage.setUsercount((short)2);
		    			for (int i = 0; i < requestMessage.getUsercount(); i++) {
		    				requestMessage.addUsernumber(String.valueOf(System.nanoTime()));
		    			}
		    			requestMessage.addUsernumber(String.valueOf(System.nanoTime()));
		    			requestMessage.setCorpid("927165");
		    			requestMessage.setReportflag((short)1);
		    		
		    			requestMessage.setMsgContent(content);*/
						return msg;
					}
				}

				@Override
				public Boolean call() throws Exception{
					int cnt = RandomUtils.nextInt() & 0x4ff;
					while(cnt>0 && tmptotal.get()>0) {
						if(ctx.channel().isWritable()){
							List<Promise> futures = null;
							ChannelFuture chfuture = null;
//							chfuture = ChannelUtil.asyncWriteToEntity(getEndpointEntity().getId(), createTestReq("中createT"+UUID.randomUUID().toString()));
							futures = ChannelUtil.syncWriteLongMsgToEntity(getEndpointEntity().getId(), createTestReq("中msg"+UUID.randomUUID().toString()));
//							ChannelFuture future = ctx.writeAndFlush( );
							cnt--;
							tmptotal.decrementAndGet();
							if(chfuture!=null)chfuture.sync();
							
							if(futures==null) continue;
							try{
								for(Promise  future: futures){
									future.sync();
									if(future.isSuccess()){
										logger.info("response:{}",future.get());
									}else{
										logger.error("response:{}",future.cause());
									}
								}


							}catch(Exception e){
								e.printStackTrace();
								cnt--;
								tmptotal.decrementAndGet();
								break;
							}
						}else{
							Thread.sleep(10);
						}
					}
					return true;
				}
			}, new ExitUnlimitCirclePolicy() {
				@Override
				public boolean notOver(Future future) {
					boolean over =   ch.isActive() && tmptotal.get() > 0;
					if(!over) {
						logger.info("========send over.============");
					
//						ch.writeAndFlush(new CmppTerminateRequestMessage());
					}
					return over;
				}
			},1);
			
		}
		
	
		ctx.fireUserEventTriggered(evt);
	}

	public void channelRead(final ChannelHandlerContext ctx, Object msg) throws Exception {
//		logger.info("Receive : {}" ,msg);
		if (msg instanceof CmppDeliverRequestMessage) {
			System.out.println("77777");
			CmppDeliverRequestMessage e = (CmppDeliverRequestMessage) msg;
			CmppDeliverResponseMessage responseMessage = new CmppDeliverResponseMessage(e.getHeader().getSequenceId());
			responseMessage.setResult(0);
			responseMessage.setMsgId(e.getMsgId());
			ctx.channel().writeAndFlush(responseMessage);
			
		} else if (msg instanceof CmppDeliverResponseMessage) {
			CmppDeliverResponseMessage e = (CmppDeliverResponseMessage) msg;

		} else if (msg instanceof CmppSubmitRequestMessage) {
			System.out.println("66666666666");
			CmppSubmitRequestMessage e = (CmppSubmitRequestMessage) msg;
			final CmppSubmitResponseMessage resp = new CmppSubmitResponseMessage(e.getHeader().getSequenceId());
//			resp.setResult(RandomUtils.nextInt()%2 <1 ? 8 : 0);
			resp.setResult(0);
			ctx.channel().writeAndFlush(resp);
			
			//回复状态报告
			final CmppDeliverRequestMessage deliver = new CmppDeliverRequestMessage();
			deliver.setDestId(e.getSrcId());
			deliver.setSrcterminalId(e.getDestterminalId()[0]);
			CmppReportRequestMessage report = new CmppReportRequestMessage();
			report.setDestterminalId(deliver.getSrcterminalId());
			report.setMsgId(resp.getMsgId());
			String t = DateFormatUtils.format(CachedMillisecondClock.INS.now(), "yyMMddHHMM");
			report.setSubmitTime(t);
			report.setDoneTime(t);
			report.setStat("DELIVRD");
			report.setSmscSequence(0);
			deliver.setReportRequestMessage(report);
			
			ctx.executor().schedule(new Runnable() {
				public void run() {
					ctx.channel().writeAndFlush(deliver);
				}
			}, 5, TimeUnit.SECONDS);
			
		} else if (msg instanceof CmppSubmitResponseMessage) {
			CmppSubmitResponseMessage e = (CmppSubmitResponseMessage) msg;
		} else if (msg instanceof CmppQueryRequestMessage) {
			CmppQueryRequestMessage e = (CmppQueryRequestMessage) msg;
			CmppQueryResponseMessage res = new CmppQueryResponseMessage(e.getHeader().getSequenceId());
			ctx.channel().writeAndFlush(res);
		} else {
			ctx.fireChannelRead(msg);
		}
	}
	
	@Override
	public String name() {
		return "SessionConnectedHandler-Gate";
	}
	
	public SessionConnectedHandlerGG clone() throws CloneNotSupportedException {
		SessionConnectedHandlerGG ret = (SessionConnectedHandlerGG) super.clone();
		return ret;
	}

}
