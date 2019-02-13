package com.zx.sms.connect.manager.cmpp;

import com.zx.sms.connect.manager.EndpointEntity.SupportLongMessage;
import com.zx.sms.connect.manager.EndpointManager;
import com.zx.sms.handler.api.BusinessHandlerInterface;
import com.zx.sms.handler.api.gate.SessionConnectedHandler;
import com.zx.sms.handler.api.smsbiz.MessageReceiveHandler;
import io.netty.util.ResourceLeakDetector;
import io.netty.util.ResourceLeakDetector.Level;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.LockSupport;

/**
 *经测试，35个连接，每个连接每200/s条消息
 *lenovoX250能承担7000/s消息编码解析无压力。
 *10000/s的消息服务不稳定，开个网页，或者打开其它程序导致系统抖动，会有大量消息延迟 (超过500ms)
 *
 *低负载时消息编码解码可控制在10ms以内。
 *
 */

public class TestCMPPEndPoint {
	private static final Logger logger = LoggerFactory.getLogger(TestCMPPEndPoint.class);

	@Test
	public void testCMPPEndpoint() throws Exception {
		ResourceLeakDetector.setLevel(Level.ADVANCED);
		final EndpointManager manager = EndpointManager.INS;

		CMPPServerEndpointEntity server = new CMPPServerEndpointEntity();
		server.setId("server");
//		server.setHost("127.0.0.1");
//		101.37.77.0
		server.setPort(7890);
		server.setValid(true);
		//使用ssl加密数据流
		server.setUseSSL(false);

		CMPPServerChildEndpointEntity child = new CMPPServerChildEndpointEntity();
		child.setId("child");
		child.setChartset(Charset.forName("utf-8"));
		child.setGroupName("8900");
		child.setUserName("350114");
		child.setPassword("350114");

		child.setValid(true);
		child.setVersion((short)0x20);
//		child.setVersion((short) 32);

		child.setMaxChannels((short)20);
		child.setRetryWaitTimeSec((short)30);
		child.setMaxRetryCnt((short)3);
//		child.setReSendFailMsg(true);
//		child.setWriteLimit(200);
//		child.setReadLimit(200);
		List<BusinessHandlerInterface> serverhandlers = new ArrayList<BusinessHandlerInterface>();
		serverhandlers.add(new MessageReceiveHandler());
		child.setBusinessHandlerSet(serverhandlers);
		server.addchild(child);
		manager.addEndpointEntity(server);
		
		manager.openAll();
		manager.startConnectionCheckTask();

        System.out.println("start.....");
        
//		Thread.sleep(300000);
        LockSupport.park();
		EndpointManager.INS.close();
	}
}
