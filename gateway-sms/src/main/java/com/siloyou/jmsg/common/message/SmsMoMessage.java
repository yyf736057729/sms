package com.siloyou.jmsg.common.message;

import java.io.Serializable;

public class SmsMoMessage implements Serializable {
	private static final long serialVersionUID = 1L;
	private String uuid;		//随机ID	
	private String msgid; 		//消息流水号
	private String srcTermID; 	//上行手机号
	private String destTermID; 	//spnumber
	private String msgContent; 	//上行内容

	private String gateWayID; 	//网关编号
	
	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
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

	public String getMsgContent() {
		return msgContent;
	}

	public void setMsgContent(String msgContent) {
		this.msgContent = msgContent;
	}

	public String getGateWayId() {
		return gateWayID;
	}

	public void setGateWayID(String gateWayID) {
		this.gateWayID = gateWayID;
	}

}
