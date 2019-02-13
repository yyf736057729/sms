package com.siloyou.jmsg.common.message;

import java.io.Serializable;

public class SmsSrMessage implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String msgid;
	private String result;
	private String reserve;
	private SmsMtMessage message;
	
	public SmsSrMessage(){}
	
	public SmsSrMessage(String msgid, String result, SmsMtMessage message){
		this.msgid = msgid;
		this.result = result;
		this.message = message;
	}
	
	public String getMsgid() {
		return msgid;
	}
	public void setMsgid(String msgid) {
		this.msgid = msgid;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public String getReserve() {
		return reserve;
	}
	public void setReserve(String reserve) {
		this.reserve = reserve;
	}
	public SmsMtMessage getMessage() {
		return message;
	}
	public void setMessage(SmsMtMessage message) {
		this.message = message;
	}
	
	
}
