package com.siloyou.jmsg.modules.sms.entity;
//通道匹配信息
public class GatewayResult {
	
	private String errorCode;//消息码
	private String gatewayId;//通道ID
	private String spNumber;//接入号
	
	public boolean isExists() {//通道是否存在
		if("T000".equals(errorCode))return true;
		return false;
	}
	
	public String getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	
	public String getGatewayId() {
		return gatewayId;
	}
	public void setGatewayId(String gatewayId) {
		this.gatewayId = gatewayId;
	}
	
	public String getSpNumber() {
		return spNumber;
	}
	public void setSpNumber(String spNumber) {
		this.spNumber = spNumber;
	}
	
}


