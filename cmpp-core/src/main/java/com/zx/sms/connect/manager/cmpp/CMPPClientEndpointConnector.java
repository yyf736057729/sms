package com.zx.sms.connect.manager.cmpp;

import io.netty.channel.ChannelPipeline;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zx.sms.common.GlobalConstance;
import com.zx.sms.connect.manager.AbstractClientEndpointConnector;
import com.zx.sms.connect.manager.ClientEndpoint;
import com.zx.sms.connect.manager.EndpointEntity;
import com.zx.sms.handler.MessageLogHandler;
import com.zx.sms.handler.cmpp.CMPPDeliverLongMessageHandler;
import com.zx.sms.handler.cmpp.CMPPSubmitLongMessageHandler;
import com.zx.sms.handler.cmpp.ReWriteSubmitMsgSrcHandler;
import com.zx.sms.session.AbstractSessionStateManager;
import com.zx.sms.session.cmpp.SessionLoginManager;
import com.zx.sms.session.cmpp.SessionStateManager;

/**
 *@author Lihuanghe(18852780@qq.com)
 */

public class CMPPClientEndpointConnector extends AbstractClientEndpointConnector {
	private static final Logger logger = LoggerFactory.getLogger(CMPPClientEndpointConnector.class);
	

	
	public CMPPClientEndpointConnector(CMPPClientEndpointEntity e)
	{
		super(e);
		
	}

	@Override
	protected void doBindHandler(ChannelPipeline pipe, EndpointEntity cmppentity) {
		CMPPEndpointEntity entity = (CMPPEndpointEntity)cmppentity;

		if (entity instanceof ClientEndpoint) {
			pipe.addLast("reWriteSubmitMsgSrcHandler", new ReWriteSubmitMsgSrcHandler(entity));
		}
		//处理长短信
		pipe.addLast( "CMPPDeliverLongMessageHandler", new CMPPDeliverLongMessageHandler(entity));
		pipe.addLast("CMPPSubmitLongMessageHandler",  new CMPPSubmitLongMessageHandler(entity));
		
		pipe.addLast("CmppActiveTestRequestMessageHandler", GlobalConstance.activeTestHandler);
		pipe.addLast("CmppActiveTestResponseMessageHandler", GlobalConstance.activeTestRespHandler);
		pipe.addLast("CmppTerminateRequestMessageHandler", GlobalConstance.terminateHandler);
		pipe.addLast("CmppTerminateResponseMessageHandler", GlobalConstance.terminateRespHandler);
		
	}

	@Override
	protected void doinitPipeLine(ChannelPipeline pipeline) {
		CMPPCodecChannelInitializer codec = null;
		if (getEndpointEntity() instanceof CMPPEndpointEntity) {
			pipeline.addLast(GlobalConstance.IdleCheckerHandlerName,
					new IdleStateHandler(0, 0, ((CMPPEndpointEntity) getEndpointEntity()).getIdleTimeSec(), TimeUnit.SECONDS));
			
			codec = new CMPPCodecChannelInitializer(((CMPPEndpointEntity) getEndpointEntity()).getVersion());

		} else {
			pipeline.addLast(GlobalConstance.IdleCheckerHandlerName, new IdleStateHandler(0, 0, 30, TimeUnit.SECONDS));
			
			codec = new CMPPCodecChannelInitializer();
		}
		pipeline.addLast("CmppServerIdleStateHandler", GlobalConstance.idleHandler);
		pipeline.addLast(codec.pipeName(), codec);
		pipeline.addLast("sessionLoginManager", new SessionLoginManager(getEndpointEntity()));
	}



	@Override
	protected AbstractSessionStateManager createSessionManager(EndpointEntity entity, ConcurrentMap storeMap,
			boolean preSend) {
		return new SessionStateManager(entity, storeMap, preSend);
	}

}
