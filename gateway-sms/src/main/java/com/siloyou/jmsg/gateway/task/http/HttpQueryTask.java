package com.siloyou.jmsg.gateway.task.http;

import org.springframework.stereotype.Component;

import com.Application;
import com.siloyou.jmsg.gateway.api.GatewayFactory;
import com.siloyou.jmsg.gateway.http.handler.GateWayHttpAbstract;

@Component
public class HttpQueryTask {
	
	private static GatewayFactory     gatewayFactory;
    
    private GatewayFactory getGatewayFactory(){
    	if( gatewayFactory == null ) {
    		gatewayFactory = (GatewayFactory) Application.applicationContext.getBean("gatewayFactory");
    	}
    	return gatewayFactory;
    }
	
    // 88短信
	//@Scheduled(fixedDelay = 10000)
	public void run88DX(){
		
		if( Application.isRun ) {
			GateWayHttpAbstract executer = (GateWayHttpAbstract)getGatewayFactory().getGateway("HT9002");
            if( executer != null) 
            {
                executer.report("", true);
                executer.deliver("", true);
            }
		}
	}
	
	// 云测
//	@Scheduled(fixedDelay = 5000)
	public void runTestIn(){
		
		if( Application.isRun ) {
			GateWayHttpAbstract executer = (GateWayHttpAbstract)getGatewayFactory().getGateway("HT9");
            if( executer != null) 
            {
                executer.report("", true);
                executer.deliver("", true);
            }
		}
	}
}
