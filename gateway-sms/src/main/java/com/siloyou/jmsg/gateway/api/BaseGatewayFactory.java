package com.siloyou.jmsg.gateway.api;

import java.util.concurrent.ConcurrentHashMap;

import com.siloyou.jmsg.common.gateway.SmsGateWay;
import com.siloyou.jmsg.common.message.SmsMtMessage;
import com.siloyou.jmsg.common.util.enums.GateEnum;
import com.siloyou.jmsg.gateway.Result;

public abstract class BaseGatewayFactory implements CMPPGatewayFactory {

	private ConcurrentHashMap<String, SmsGateWay> gateWayMap = new ConcurrentHashMap<String, SmsGateWay>();

	public void initGateway() {

	}

	public void closeAll() {

	}

	public abstract boolean closeGateway(String id);

	public abstract boolean closeGatewayTemp(String id);

	public abstract boolean openGateway(String id);

	public abstract boolean hasGateway(String id);

	public abstract Object getGateway(String gatewayId);

	public abstract Result sendMsg(SmsMtMessage msg);
	public Result sendMsg(SmsMtMessage msg, int level) {
		return sendMsg(msg);
	}

	public abstract GateEnum getGatewayType(String gatewayId);

	public abstract SmsMtMessage getSubmitResult(String gID, String msgid);
	
	/**
	 * 设置网关速率
	 */
	public Result setGatewaySendRate(String gatewayId, int permitsPerSecond) {
		SmsGateWay smsGateWay = getSmsGateWay(gatewayId);
		if( smsGateWay != null ) {
			smsGateWay.setRate(permitsPerSecond);
			return new Result("T0", "设置成功");
		} 
		
		return new Result("F1", "网关不存在");
	}
	
	protected GatewayService gatewayService;
	protected GateWayMessageAbstract gateWayMessage;

	public void setGatewayService(GatewayService gatewayService) {
		this.gatewayService = gatewayService;
	}

	public void setGateWayMessage(GateWayMessageAbstract gateWayMessage) {
		this.gateWayMessage = gateWayMessage;
	}

	public GateWayMessageAbstract getGateWayMessage() {
		return this.gateWayMessage;
	}

	public void addSmsGateWay(String id, SmsGateWay gateWay) {
		gateWayMap.put(id, gateWay);
	}

	public SmsGateWay getSmsGateWay(String id) {
		return gateWayMap.get(id);
	}
	
	public boolean closev1 (String gateWayid){
		
		//关闭数据接收线程
		synchronized (gateWayid) {
			SmsGateWay smsGateWay = getSmsGateWay(gateWayid);
			if( smsGateWay != null ) {
				smsGateWay.shutdown();
				gateWayMap.remove(gateWayid);
			}
		}
		
		//关闭发送线程
		closeGateway(gateWayid);
		
		return true;
	}

}
