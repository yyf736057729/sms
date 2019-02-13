package com.siloyou.jmsg.common.message;

import java.io.Serializable;

public class SmsMtMessage implements Serializable {
	private static final long serialVersionUID = 1L;

	private String id; // 业务流水ID（对应jmsg_sms_send中的id)
	private String taskid; // 任务ID（对应jmsg_sms_send中的taskid）
	private String userid; // 用户ID（对应jmsg_sms_send中的userid）
	private String gateWayID; // 网关编号（对应jmsg_sms_send中的通道代码）
	private String payType; // 计费方式
	private String cstmOrderID; // 客户订单号

	private String userReportNotify; // 用户状态通知标志（对应jmsg_sms_send中的pushflg）
	private String userReportGateWayID; // 用户状态通知网关编号 HTTP01 CMPP01

	private String msgContent; // 短信内容
	private String phone; // 接收号码
	private String spNumber; // 接入号
	private String smsType; // 短信内型
	private String wapUrl; // wap地址
	private long contentSize; // 彩信大小

	private Long sendTime;
	private Long submitTime;

	private String serviceId;
	
	private String sendStatus;
	private String phoneType;
	private String cityCode;
	private String sourceFlag;

    public String getSourceFlag()
    {
        return sourceFlag;
    }

    public void setSourceFlag(String sourceFlag)
    {
        this.sourceFlag = sourceFlag;
    }

    public String getSendStatus()
    {
        return sendStatus;
    }

    public void setSendStatus(String sendStatus)
    {
        this.sendStatus = sendStatus;
    }

    public String getPhoneType()
    {
        return phoneType;
    }

    public void setPhoneType(String phoneType)
    {
        this.phoneType = phoneType;
    }

    public String getCityCode()
    {
        return cityCode;
    }

    public void setCityCode(String cityCode)
    {
        this.cityCode = cityCode;
    }

    public String getServiceId() {
		return serviceId;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTaskid() {
		return taskid;
	}

	public void setTaskid(String taskid) {
		this.taskid = taskid;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getGateWayID() {
		return gateWayID;
	}

	public void setGateWayID(String gateWayID) {
		this.gateWayID = gateWayID;
	}

	public String getUserReportNotify() {
		return userReportNotify;
	}

	public void setUserReportNotify(String userReportNotify) {
		this.userReportNotify = userReportNotify;
	}

	public String getPayType() {
		return payType;
	}

	public void setPayType(String payType) {
		this.payType = payType;
	}

	public String getCstmOrderID() {
		return cstmOrderID;
	}

	public void setCstmOrderID(String cstmOrderID) {
		this.cstmOrderID = cstmOrderID;
	}

	public String getMsgContent() {
		return msgContent;
	}

	public void setMsgContent(String msgContent) {
		this.msgContent = msgContent;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getSpNumber() {
		return spNumber;
	}

	public void setSpNumber(String spNumber) {
		this.spNumber = spNumber;
	}

	public String getSmsType() {
		return smsType;
	}

	public void setSmsType(String smsType) {
		this.smsType = smsType;
	}

	public String getWapUrl() {
		return wapUrl;
	}

	public void setWapUrl(String wapUrl) {
		this.wapUrl = wapUrl;
	}

	public long getContentSize() {
		return contentSize;
	}

	public void setContentSize(long contentSize) {
		this.contentSize = contentSize;
	}

	public String getUserReportGateWayID() {
		return userReportGateWayID;
	}

	public void setUserReportGateWayID(String userReportGateWayID) {
		this.userReportGateWayID = userReportGateWayID;
	}

	public Long getSendTime() {
		return sendTime;
	}

	public void setSendTime(Long sendTime) {
		this.sendTime = sendTime;
	}

	public Long getSubmitTime() {
		return submitTime;
	}

	public void setSubmitTime(Long submitTime) {
		this.submitTime = submitTime;
	}

}
