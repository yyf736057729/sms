package com.sanerzone.smscenter.entity;

import java.io.Serializable;

public class SMSSRMessage implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String msgid;
	private String result;
	private String reserve;
	private SMSMTMessage message;
	
	public SMSSRMessage(){}
	
	public SMSSRMessage(String msgid, String result, SMSMTMessage message){
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
	public SMSMTMessage getMessage() {
		return message;
	}
	public void setMessage(SMSMTMessage message) {
		this.message = message;
	}
	
	
}
