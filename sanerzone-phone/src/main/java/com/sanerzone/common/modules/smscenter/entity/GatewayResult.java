package com.sanerzone.common.modules.smscenter.entity;
//通道匹配信息
public class GatewayResult {

	private String errorCode;//消息码
	private String gatewayId;//通道ID
	private String spNumber;//接入号
	private String msg;
	private int policy;		//命中策略 0 普通 1 验证码 2签名优先  3 内容优先

	public int getPolicy() {
		return policy;
	}

	public void setPolicy(int policy) {
		this.policy = policy;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public boolean isExists() {//通道是否存在
		if("T000".equals(errorCode))return true;
		return false;
	}
	public boolean isExists(String s) {//通道是否存在
		if("T000".equals(s))return true;
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


