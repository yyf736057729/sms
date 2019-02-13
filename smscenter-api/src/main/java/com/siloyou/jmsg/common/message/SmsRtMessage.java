package com.siloyou.jmsg.common.message;

import java.io.Serializable;

public class SmsRtMessage implements Serializable {
	private static final long serialVersionUID = 1L;
	private String msgid;		// 网关流水ID
	private	String gateWayID;	//网关编号
	
	private String srcTermID;		// 发送号码（联通为空）
	private String destTermID;		// 接收号码
	private String stat;			// 接收状态
	private String submitTime;		// 提交时间（联通为空）
	private String doneTime;		// 接收时间（联通为空）
	private String smscSequence;	// 网关侧序号
	
	private SmsMtMessage smsMt;
	
	public String getGateWayID() {
		return gateWayID;
	}

	public void setGateWayID(String gateWayID) {
		this.gateWayID = gateWayID;
	}

	public String getMsgid() {
		return msgid;
	}

	public void setMsgid(String msgid) {
		this.msgid = msgid;
	}

	public String getSrcTermID() {
		return srcTermID;
	}

	public void setSrcTermID(String srcTermID) {
		this.srcTermID = srcTermID;
	}

	public String getDestTermID() {
		return destTermID;
	}

	public void setDestTermID(String destTermID) {
		this.destTermID = destTermID;
	}

	public String getStat() {
		return stat;
	}

	public void setStat(String stat) {
		this.stat = stat;
	}

	public String getSubmitTime() {
		return submitTime;
	}

	public void setSubmitTime(String submitTime) {
		this.submitTime = submitTime;
	}

	public String getDoneTime() {
		return doneTime;
	}

	public void setDoneTime(String doneTime) {
		this.doneTime = doneTime;
	}

	public String getSmscSequence() {
		return smscSequence;
	}

	public void setSmscSequence(String smscSequence) {
		this.smscSequence = smscSequence;
	}

	public SmsMtMessage getSmsMt() {
		return smsMt;
	}

	public void setSmsMt(SmsMtMessage smsMt) {
		this.smsMt = smsMt;
	}
	
}
