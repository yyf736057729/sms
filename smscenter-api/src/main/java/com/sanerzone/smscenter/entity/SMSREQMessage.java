package com.sanerzone.smscenter.entity;

import java.io.Serializable;
import java.util.Date;

public class SMSREQMessage implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String taskid;      // taskid
	
	private String accId;       // acc_id
    private String userid;      // 用户ID
    private String feeType;     // 计费类型(0：提交、1：状态（为空）、 2：状态（实际)
    private String feePayment;  // 计费方式(预付、后付)
    
    private String customTaskid;        // 客户批次ID
    private String customServiceid;     // custom_serviceid
    private String phones;           // phone
    private String spnumber;        // 发送号码
    
    private String msgContent;      // msgContent
    private String smsContentId;    // 短信模板ID
    private String wapUrl;			//
    private String smsType;         // 短信类型 1:短短信 2:长短信 3:彩信
    private int    smsSize;         // 扣费条数
    
    private String registeredDelivery ; // 是否要求返回状态确认报告： 0：不需要 1：需要
	private String deliveryGateWayId;   // 返回状态确认报告的网关编号
	private String sourceGateWayId;     //来源网关编号
	private String sourceGateWayProto;  //来源网关协议
	
	private Date sendTime;              // 发送时间，用户指定的时间
	private Date receiveTime;
	private String massFlag;		   // 群发标示  0：单发 1：群发
	public String getTaskid() {
		return taskid;
	}
	public void setTaskid(String taskid) {
		this.taskid = taskid;
	}
	public String getAccId() {
		return accId;
	}
	public void setAccId(String accId) {
		this.accId = accId;
	}
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getFeeType() {
		return feeType;
	}
	public void setFeeType(String feeType) {
		this.feeType = feeType;
	}
	public String getFeePayment() {
		return feePayment;
	}
	public void setFeePayment(String feePayment) {
		this.feePayment = feePayment;
	}
	public String getCustomTaskid() {
		return customTaskid;
	}
	public void setCustomTaskid(String customTaskid) {
		this.customTaskid = customTaskid;
	}
	public String getCustomServiceid() {
		return customServiceid;
	}
	public void setCustomServiceid(String customServiceid) {
		this.customServiceid = customServiceid;
	}
	public String getPhones() {
		return phones;
	}
	public void setPhones(String phones) {
		this.phones = phones;
	}
	public String getSpnumber() {
		return spnumber;
	}
	public void setSpnumber(String spnumber) {
		this.spnumber = spnumber;
	}
	public String getMsgContent() {
		return msgContent;
	}
	public void setMsgContent(String msgContent) {
		this.msgContent = msgContent;
	}
	public String getSmsContentId() {
		return smsContentId;
	}
	public void setSmsContentId(String smsContentId) {
		this.smsContentId = smsContentId;
	}
	public String getWapUrl() {
		return wapUrl;
	}
	public void setWapUrl(String wapUrl) {
		this.wapUrl = wapUrl;
	}
	public String getSmsType() {
		return smsType;
	}
	public void setSmsType(String smsType) {
		this.smsType = smsType;
	}
	public int getSmsSize() {
		return smsSize;
	}
	public void setSmsSize(int smsSize) {
		this.smsSize = smsSize;
	}
	public String getRegisteredDelivery() {
		return registeredDelivery;
	}
	public void setRegisteredDelivery(String registeredDelivery) {
		this.registeredDelivery = registeredDelivery;
	}
	public String getDeliveryGateWayId() {
		return deliveryGateWayId;
	}
	public void setDeliveryGateWayId(String deliveryGateWayId) {
		this.deliveryGateWayId = deliveryGateWayId;
	}
	public String getSourceGateWayId() {
		return sourceGateWayId;
	}
	public void setSourceGateWayId(String sourceGateWayId) {
		this.sourceGateWayId = sourceGateWayId;
	}
	public String getSourceGateWayProto() {
		return sourceGateWayProto;
	}
	public void setSourceGateWayProto(String sourceGateWayProto) {
		this.sourceGateWayProto = sourceGateWayProto;
	}
	public Date getSendTime() {
		return sendTime;
	}
	public void setSendTime(Date sendTime) {
		this.sendTime = sendTime;
	}
	public Date getReceiveTime() {
		return receiveTime;
	}
	public void setReceiveTime(Date receiveTime) {
		this.receiveTime = receiveTime;
	}
	public String getMassFlag() {
		return massFlag;
	}
	public void setMassFlag(String massFlag) {
		this.massFlag = massFlag;
	}
	
	
}
