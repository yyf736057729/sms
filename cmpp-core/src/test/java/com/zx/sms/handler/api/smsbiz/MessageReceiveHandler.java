package com.zx.sms.handler.api.smsbiz;

import com.zx.sms.codec.cmpp.msg.*;
import com.zx.sms.common.util.CachedMillisecondClock;
import com.zx.sms.connect.manager.EndpointManager;
import com.zx.sms.connect.manager.EventLoopGroupFactory;
import com.zx.sms.connect.manager.ExitUnlimitCirclePolicy;
import com.zx.sms.handler.api.AbstractBusinessHandler;
import com.zx.sms.session.cmpp.SessionState;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.apache.commons.lang.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicLong;

@Sharable
public class MessageReceiveHandler extends AbstractBusinessHandler {
	private static final Logger logger = LoggerFactory.getLogger(MessageReceiveHandler.class);
	private int rate = 1;

	private AtomicLong cnt = new AtomicLong();
	private long lastNum = 0;
	private volatile boolean inited = false;
	@Override
	public String name() {
		return "MessageReceiveHandler-smsBiz";
	}

	public synchronized void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if (evt == SessionState.Connect && !inited) {
			EventLoopGroupFactory.INS.submitUnlimitCircleTask(new Callable<Boolean>(){
				
				@Override
				public Boolean call() throws Exception {
				
					long nowcnt = cnt.get();
					logger.info("channels : {},Totle Receive Msg Num:{},   speed : {}/s", EndpointManager.INS.getEndpointConnector(getEndpointEntity()).getConnectionNum(),nowcnt, (nowcnt - lastNum)/rate);
					lastNum = nowcnt;
					return true;
				}
			}, new ExitUnlimitCirclePolicy() {
				@Override
				public boolean notOver(Future future) {
					return true;
				}
			},rate*1000);
			inited = true;
		}
		ctx.fireUserEventTriggered(evt);
	}

	@Override
	public void channelRead(final ChannelHandlerContext ctx, Object msg) throws Exception {

		if (msg instanceof CmppDeliverRequestMessage) {
			System.out.println("11111111111111");
			CmppDeliverRequestMessage e = (CmppDeliverRequestMessage) msg;
			
			if(e.getFragments()!=null) {
				//长短信会带有片断
				for(CmppDeliverRequestMessage frag:e.getFragments()) {
					CmppDeliverResponseMessage responseMessage = new CmppDeliverResponseMessage(frag.getHeader().getSequenceId());
					responseMessage.setResult(0);
					responseMessage.setMsgId(frag.getMsgId());
					ctx.channel().write(responseMessage);
				}
			}
			
			CmppDeliverResponseMessage responseMessage = new CmppDeliverResponseMessage(e.getHeader().getSequenceId());
			responseMessage.setResult(0);
			responseMessage.setMsgId(e.getMsgId());
			ctx.channel().writeAndFlush(responseMessage).addListener(new GenericFutureListener() {
			@Override
			public void operationComplete(Future future) throws Exception {
				cnt.incrementAndGet();
			}
		});

		} else if (msg instanceof CmppDeliverResponseMessage) {
			System.out.println("222222222222222");
			CmppDeliverResponseMessage e = (CmppDeliverResponseMessage) msg;

		} else if (msg instanceof CmppSubmitRequestMessage) {
			System.out.println("3333333333333333");
			//接收到 CmppSubmitRequestMessage 消息
			CmppSubmitRequestMessage e = (CmppSubmitRequestMessage) msg;
			
			final List<CmppDeliverRequestMessage> reportlist = new ArrayList<CmppDeliverRequestMessage>();
			
			if(e.getFragments()!=null) {
				//长短信会可能带有片断，每个片断都要回复一个response
				for(CmppSubmitRequestMessage frag:e.getFragments()) {
					CmppSubmitResponseMessage responseMessage = new CmppSubmitResponseMessage(frag.getHeader().getSequenceId());
					responseMessage.setResult(0);
					ctx.channel().write(responseMessage);
					
					CmppDeliverRequestMessage deliver = new CmppDeliverRequestMessage();
					deliver.setDestId(e.getSrcId());
					deliver.setSrcterminalId(e.getDestterminalId()[0]);
					CmppReportRequestMessage report = new CmppReportRequestMessage();
					report.setDestterminalId(deliver.getSrcterminalId());
					report.setMsgId(responseMessage.getMsgId());
					String t = DateFormatUtils.format(CachedMillisecondClock.INS.now(), "yyMMddHHMM");
					report.setSubmitTime(t);
					report.setDoneTime(t);
					report.setStat("DELIVRD");
					report.setSmscSequence(0);
					deliver.setReportRequestMessage(report);
					reportlist.add(deliver);
				}
			}
			
			final CmppSubmitResponseMessage resp = new CmppSubmitResponseMessage(e.getHeader().getSequenceId());
			resp.setResult(0);
			
			ctx.channel().writeAndFlush(resp).addListener(new GenericFutureListener() {
				@Override
				public void operationComplete(Future future) throws Exception {
					cnt.incrementAndGet();
				}
			});
			
			//回复状态报告
			if(e.getRegisteredDelivery()==1) {

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
				reportlist.add(deliver);

				ctx.executor().submit(new Runnable() {
					public void run() {
						for(CmppDeliverRequestMessage t : reportlist)
							ctx.channel().writeAndFlush(t);
					}
				});
			}
			
		} else if (msg instanceof CmppSubmitResponseMessage) {
			System.out.println("4444444444444444444");
			CmppSubmitResponseMessage e = (CmppSubmitResponseMessage) msg;
		} else if (msg instanceof CmppQueryRequestMessage) {
			CmppQueryRequestMessage e = (CmppQueryRequestMessage) msg;
			CmppQueryResponseMessage res = new CmppQueryResponseMessage(e.getHeader().getSequenceId());
			ctx.channel().writeAndFlush(res).addListener(new GenericFutureListener() {
				@Override
				public void operationComplete(Future future) throws Exception {
					cnt.incrementAndGet();
				}
			});
		} else {
			ctx.fireChannelRead(msg);
		}
	}
	
	public MessageReceiveHandler clone() throws CloneNotSupportedException {
		MessageReceiveHandler ret = (MessageReceiveHandler) super.clone();
		ret.cnt = new AtomicLong();
		ret.lastNum = 0;
		return ret;
	}

}
