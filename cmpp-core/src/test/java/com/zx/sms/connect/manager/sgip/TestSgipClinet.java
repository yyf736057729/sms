package com.zx.sms.connect.manager.sgip;

import com.zx.sms.codec.sgip12.codec.Sgip2CMPPBusinessHandler;
import com.zx.sms.connect.manager.EndpointEntity;
import com.zx.sms.connect.manager.EndpointManager;
import com.zx.sms.handler.api.BusinessHandlerInterface;
import com.zx.sms.handler.api.gate.SessionConnectedHandler;
import io.netty.util.ResourceLeakDetector;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;

/**
 * @program: smscenter
 * @description:
 * @author: Cral
 * @create: 2019-01-16 15:40
 */
public class TestSgipClinet {

    @Test
    public void sgipTest() throws Exception {
        ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.ADVANCED);
        final EndpointManager manager = EndpointManager.INS;

        SgipClientEndpointEntity client = new SgipClientEndpointEntity();
        client.setId("sgipclient");
        client.setHost("106.15.42.125");
        client.setPort(7891);
        client.setLoginName("yhhblt");
        client.setLoginPassowrd("yhhb@lt");
        client.setChannelType(EndpointEntity.ChannelType.DOWN);

        client.setMaxChannels((short)1);
        client.setRetryWaitTimeSec((short)100);
        client.setUseSSL(false);
        //		client.setReSendFailMsg(true);
//		client.setWriteLimit(200);
//		client.setReadLimit(200);
        List<BusinessHandlerInterface> clienthandlers = new ArrayList<BusinessHandlerInterface>();
        clienthandlers.add(new Sgip2CMPPBusinessHandler()); //  将CMPP的对象转成sgip对象，然后再经sgip解码器处理
        clienthandlers.add(new SessionConnectedHandler(new AtomicInteger(1))); //// 复用CMPP的Handler
        client.setBusinessHandlerSet(clienthandlers);
        manager.addEndpointEntity(client);
        manager.openAll();
//		manager.openEndpoint(client);
        System.out.println("start.....");
        LockSupport.park();

        EndpointManager.INS.close();
    }

}