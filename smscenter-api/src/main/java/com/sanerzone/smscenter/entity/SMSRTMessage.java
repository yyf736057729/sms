package com.sanerzone.smscenter.entity;

import java.io.Serializable;

public class SMSRTMessage implements Serializable {
	private static final long serialVersionUID = 1L;
	private String msgid;		// 网关流水ID
	private	String gateWayID;	//网关编号
	
	private String spnumber;		// 发送号码（联通为空）
	private String phone;		    // 接收号码
	private String stat;			// 接收状态
	private String submitTime;		// 提交时间（联通为空）
	private String doneTime;		// 接收时间（联通为空）
	private String smscSequence;	// 网关侧序号
	private Long reportReceiveTime = System.currentTimeMillis();
	private SMSMTMessage smsMt;
	
	
	public String getSpnumber()
    {
        return spnumber;
    }

    public void setSpnumber(String spnumber)
    {
        this.spnumber = spnumber;
    }

    public String getPhone()
    {
        return phone;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    public Long getReportReceiveTime()
    {
        return reportReceiveTime;
    }

    public void setReportReceiveTime(Long reportReceiveTime)
    {
        this.reportReceiveTime = reportReceiveTime;
    }

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

	public SMSMTMessage getSmsMt() {
		return smsMt;
	}

	public void setSmsMt(SMSMTMessage smsMt) {
		this.smsMt = smsMt;
	}
	
}
