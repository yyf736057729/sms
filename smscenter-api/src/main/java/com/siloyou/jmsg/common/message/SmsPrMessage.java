package com.siloyou.jmsg.common.message;

import java.io.Serializable;

public class SmsPrMessage implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String msgid;
	private String bizid;
	private String userid;
	private String result;
	private long recvTime;
	private String reserve;
	
	public SmsPrMessage(){}
	
	public SmsPrMessage(String msgid, String result, String bizid){
		this.msgid = msgid;
		this.result = result;
		this.bizid = bizid;
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

	public String getBizid() {
		return bizid;
	}

	public void setBizid(String bizid) {
		this.bizid = bizid;
	}

	public long getRecvTime() {
		return recvTime;
	}

	public void setRecvTime(long recvTime) {
		this.recvTime = recvTime;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}
	
}
