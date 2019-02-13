package com.siloyou.jmsg.gateway.smgp.protocol;

import java.util.Map;

import org.apache.log4j.Logger;

import com.siloyou.jmsg.gateway.smgp.protocol.smgp.message.SMGPDeliverMessage;
import com.siloyou.jmsg.gateway.smgp.protocol.smgp.message.SMGPSubmitRespMessage;

public class DefaultMoListener implements MoListener {
	
	private final static Logger logger = Logger.getLogger(DefaultMoListener.class);

	public void onDeliver(SMGPDeliverMessage deliver, Map<String,String> args) {
		logger.warn("未实现MO监听器，receive mo message:{}" + deliver);
	}
	
	public void onSubmitResp(SMGPSubmitRespMessage submitResp, Map<String,String> args){
		logger.warn("收到Submit响应" + submitResp);
	}

	public void onTerminate(Map<String,String> args) {
		logger.warn("SMG主动断开哦" );
	}
	

}
