package com.siloyou.jmsg.gateway.api;

import com.siloyou.jmsg.common.gateway.SmsGateWay;
import com.siloyou.jmsg.common.message.SmsMtMessage;
import com.siloyou.jmsg.common.util.enums.GateEnum;
import com.siloyou.jmsg.gateway.Result;

public interface GatewayFactory {
	
	void initGateway(String appCode);

	void closeAll();

	boolean closeGateway(String id);

	boolean closeGatewayTemp(String id);

	boolean openGateway(String id);

	boolean hasGateway(String id);

	Object getGateway(String gatewayId);

	Result sendMsg(SmsMtMessage msg);
	
	Result sendMsg(SmsMtMessage msg, int level);
	
	GateEnum getGatewayType(String gatewayId);
	
	SmsMtMessage getSubmitResult(String gID, String msgid);
	
	GateWayMessageAbstract getGateWayMessage();
	
	void addSmsGateWay(String id, SmsGateWay gateWay);
	
	SmsGateWay getSmsGateWay(String id);
	
	Result setGatewaySendRate(String gatewayId, int permitsPerSecond);
	
	boolean closev1(String id);
	
	
}
