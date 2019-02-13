package com.zx.sms.connect.manager.cmpp;

import com.zx.sms.connect.manager.EndpointEntity.SupportLongMessage;
import com.zx.sms.connect.manager.EndpointManager;
import com.zx.sms.handler.api.BusinessHandlerInterface;
import com.zx.sms.handler.api.gate.SessionConnectedHandler;
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

public class TestCMPPEndClient_2 {
	private static final Logger logger = LoggerFactory.getLogger(TestCMPPEndClient_2.class);

	@Test
	public void testCMPPEndpoint() throws Exception {
		ResourceLeakDetector.setLevel(Level.ADVANCED);
		final EndpointManager manager = EndpointManager.INS;

		CMPPClientEndpointEntity client = new CMPPClientEndpointEntity();
		client.setId("client");
//		client.setHost("101.37.77.0");
//		client.setPort(7890);
//
//
		client.setHost("127.0.0.1");
		client.setPort(9999);
//



//		client.setLocalhost("127.0.0.1");
//		client.setLocalport(9999);

		client.setChartset(Charset.forName("utf-8"));
		client.setGroupName("10115");
		client.setUserName("10115");
		client.setPassword("123456");

		client.setMaxChannels((short)1);
		client.setVersion((short)0x20);
		client.setRetryWaitTimeSec((short)30);
		client.setUseSSL(false);
//		client.setWriteLimit(100);
		client.setReSendFailMsg(false);
		client.setSupportLongmsg(SupportLongMessage.BOTH);
		List<BusinessHandlerInterface> clienthandlers = new ArrayList<BusinessHandlerInterface>();
		clienthandlers.add( new SessionConnectedHandler(1));
		client.setBusinessHandlerSet(clienthandlers);
		
		manager.addEndpointEntity(client);
		
		manager.openAll();
		manager.startConnectionCheckTask();

        System.out.println("start.....");
        
//		Thread.sleep(300000);
        LockSupport.park();
		EndpointManager.INS.close();
	}
}
