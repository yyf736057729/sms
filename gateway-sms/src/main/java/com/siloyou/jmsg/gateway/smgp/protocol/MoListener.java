package com.siloyou.jmsg.gateway.smgp.protocol;

import java.util.Map;

import com.siloyou.jmsg.gateway.smgp.protocol.smgp.message.SMGPDeliverMessage;
import com.siloyou.jmsg.gateway.smgp.protocol.smgp.message.SMGPSubmitRespMessage;

public interface MoListener {
	
	public void onDeliver(SMGPDeliverMessage deliver, Map<String,String> args);
	
	public void onSubmitResp(SMGPSubmitRespMessage deliver, Map<String,String> args);
	
	public void onTerminate(Map<String,String> args);

}
