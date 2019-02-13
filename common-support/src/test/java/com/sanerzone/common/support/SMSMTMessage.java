package com.sanerzone.common.support;

import java.io.Serializable;

public class SMSMTMessage implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String id;
	private String accId;       // acc_id
    private String userid;      // 用户ID
    private String feeType;     // 计费类型(0：提交、1：状态（为空）、 2：状态（实际)
    private String feePayment;      // 计费方式(预付、后付)
    private String taskid;      // taskid
    private String customTaskid;        // 客户批次ID
    private String customServiceid;     // custom_serviceid
    private String phone;       // phone
    private String phoneType;       // 运营商
    private String phoneArea;       // 运营商
    private String spnumber;        // 发送号码
    private String smsContent;      // sms_content
    private String wapUrl;			//
    private String smsType;         // 短信类型 1:短短信 2:长短信 3:彩信
    private String contentType;		// 内容类型
    private int    smsSize;         // 扣费条数
    private String sendStatus;      // 发送状态
    private String gatewayId;       // 目标网关编号
    private String gatewayAppPort;  // 目标网关应用  8989、8988
    private String gatewayGroup;    // 目标网关分组  行业、验证码
    
    private Long sendTime;          // 发送时间，用户指定的时间
    private Long receiveTime = System.currentTimeMillis();
    private Long bizTime;           // 业务处理时间，匹配号段网关花的时间
    private Long submitTime;        // 提交时间
    private Long reportTime;        // report_time
	
	private String registeredDelivery ; // 是否要求返回状态确认报告： 0：不需要 1：需要
	private String deliveryGateWayId;   // 返回状态确认报告的网关编号
	private String sourceGateWayId;    //来源网关编号
	private String sourceGateWayProto; //来源网关协议
	
	private String serviceId;          //保留
	private String massFlag;		// 群发标示  0：单发 1：群发

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getMassFlag() {
		return massFlag;
	}

	public void setMassFlag(String massFlag) {
		this.massFlag = massFlag;
	}

	public String getAccId()
    {
        return accId;
    }

    public void setAccId(String accId)
    {
        this.accId = accId;
    }

    public String getUserid()
    {
        return userid;
    }

    public void setUserid(String userid)
    {
        this.userid = userid;
    }

    public String getFeeType()
    {
        return feeType;
    }

    public void setFeeType(String feeType)
    {
        this.feeType = feeType;
    }

    public String getFeePayment()
    {
        return feePayment;
    }

    public void setFeePayment(String feePayment)
    {
        this.feePayment = feePayment;
    }

    public String getTaskid()
    {
        return taskid;
    }

    public void setTaskid(String taskid)
    {
        this.taskid = taskid;
    }

    public String getCustomTaskid()
    {
        return customTaskid;
    }

    public void setCustomTaskid(String customTaskid)
    {
        this.customTaskid = customTaskid;
    }

    public String getCustomServiceid()
    {
        return customServiceid;
    }

    public void setCustomServiceid(String customServiceid)
    {
        this.customServiceid = customServiceid;
    }

    public String getPhone()
    {
        return phone;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    public String getPhoneType()
    {
        return phoneType;
    }

    public void setPhoneType(String phoneType)
    {
        this.phoneType = phoneType;
    }

    public String getPhoneArea()
    {
        return phoneArea;
    }

    public void setPhoneArea(String phoneArea)
    {
        this.phoneArea = phoneArea;
    }

    public String getSpnumber()
    {
        return spnumber;
    }

    public void setSpnumber(String spnumber)
    {
        this.spnumber = spnumber;
    }

    public String getSmsContent()
    {
        return smsContent;
    }

    public void setSmsContent(String smsContent)
    {
        this.smsContent = smsContent;
    }

    public String getSmsType()
    {
        return smsType;
    }

    public void setSmsType(String smsType)
    {
        this.smsType = smsType;
    }

    public int getSmsSize()
    {
        return smsSize;
    }

    public String getWapUrl() {
		return wapUrl;
	}

	public void setWapUrl(String wapUrl) {
		this.wapUrl = wapUrl;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public void setSmsSize(int smsSize)
    {
        this.smsSize = smsSize;
    }

    public String getSendStatus()
    {
        return sendStatus;
    }

    public void setSendStatus(String sendStatus)
    {
        this.sendStatus = sendStatus;
    }

    public String getGatewayId()
    {
        return gatewayId;
    }

    public void setGatewayId(String gatewayId)
    {
        this.gatewayId = gatewayId;
    }

    public String getGatewayAppPort()
    {
        return gatewayAppPort;
    }

    public void setGatewayAppPort(String gatewayAppPort)
    {
        this.gatewayAppPort = gatewayAppPort;
    }

    public String getGatewayGroup()
    {
        return gatewayGroup;
    }

    public void setGatewayGroup(String gatewayGroup)
    {
        this.gatewayGroup = gatewayGroup;
    }

    public Long getSendTime()
    {
        return sendTime;
    }

    public void setSendTime(Long sendTime)
    {
        this.sendTime = sendTime;
    }

    public Long getReceiveTime()
    {
        return receiveTime;
    }

    public void setReceiveTime(Long receiveTime)
    {
        this.receiveTime = receiveTime;
    }

    public Long getBizTime()
    {
        return bizTime;
    }

    public void setBizTime(Long bizTime)
    {
        this.bizTime = bizTime;
    }

    public Long getSubmitTime()
    {
        return submitTime;
    }

    public void setSubmitTime(Long submitTime)
    {
        this.submitTime = submitTime;
    }

    public Long getReportTime()
    {
        return reportTime;
    }

    public void setReportTime(Long reportTime)
    {
        this.reportTime = reportTime;
    }

    public String getRegisteredDelivery()
    {
        return registeredDelivery;
    }

    public void setRegisteredDelivery(String registeredDelivery)
    {
        this.registeredDelivery = registeredDelivery;
    }

    public String getDeliveryGateWayId()
    {
        return deliveryGateWayId;
    }

    public void setDeliveryGateWayId(String deliveryGateWayId)
    {
        this.deliveryGateWayId = deliveryGateWayId;
    }

    public String getSourceGateWayId()
    {
        return sourceGateWayId;
    }

    public void setSourceGateWayId(String sourceGateWayId)
    {
        this.sourceGateWayId = sourceGateWayId;
    }

    public String getSourceGateWayProto()
    {
        return sourceGateWayProto;
    }

    public void setSourceGateWayProto(String sourceGateWayProto)
    {
        this.sourceGateWayProto = sourceGateWayProto;
    }

    public String getServiceId()
    {
        return serviceId;
    }

    public void setServiceId(String serviceId)
    {
        this.serviceId = serviceId;
    }
	
	
	
}
