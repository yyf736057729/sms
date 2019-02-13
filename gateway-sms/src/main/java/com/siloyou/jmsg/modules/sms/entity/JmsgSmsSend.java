/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.sms.entity;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 短信发送Entity
 * @author zhukc
 * @version 2016-07-16
 */
public class JmsgSmsSend  {
	
	private static final long serialVersionUID = 1L;
	private String id;
	private String dataId;				// 素材ID
	private String taskId;				// 系统订单号
	private String customerOrderId;		// 客户订单号
	private String phone;				// 手机号码
	private String smsContent;			// 短信内容
	private String smsType;				// 短信类型
	private int payCount;				// 扣费条数
	private String userId;					// 用户ID
	private String phoneType;			// 运营商
	private String areaCode;			// 省市代码
	private String payType;				// 扣费方式 0:提交,2:状态报告
	private String payStatus;			// 扣费状态 1:成功 0失败 2未知
	private Date payTime;				// 扣费时间
	private String pushFlag;			// 是否推送 1:需要推送 0:无需推送
	private String sendStatus;			// 发送状态T成功 F失败 P处理中 R审核
	private String channelCode;			// 通道代码
	private String gatewayName;			// 通道名称
	private Date sendDatetime;			// 发送时间
	private Date sendDatetimeQ;			// 发送时间
	private Date sendDatetimeZ;			// 发送时间
	private String companyId;			// 公司ID
	private String submitMode;			// 提交方式(WEB,API)
	private String topic;				// 发送队列
	private Date createDatetime;		// 创建时间
	private Date updateDatetime;		// 操作时间
	private String spNumber;			// 接入号
	private Date createDatetimeQ;
	private Date createDatetimeZ;
	
	private String sendResult;			//发送结果
	
	private String reportGatewayId;		//网关通知编号
	private String reportStatus;		//网关状态
	
	private String msgid;//
	private String groupName;
	private String cityName;
	
	private String resultStatus;		//结果状态
	private String tableName;			//查询的数据表名称

	private String templateId;

	private String provinceId;


	public String getProvinceId() {
		return provinceId;
	}

	public void setProvinceId(String provinceId) {
		this.provinceId = provinceId.substring(0,2);
	}

	public String getTemplateId() {
		return templateId;
	}

	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getResultStatus() {
		return resultStatus;
	}

	public void setResultStatus(String resultStatus) {
		this.resultStatus = resultStatus;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getGroupName()
    {
        return groupName;
    }

    public void setGroupName(String groupName)
    {
        this.groupName = groupName;
    }

    public String getCityName()
    {
        return cityName;
    }

    public void setCityName(String cityName)
    {
        this.cityName = cityName;
    }

    public String getReportStatus() {
		return reportStatus;
	}

	public void setReportStatus(String reportStatus) {
		this.reportStatus = reportStatus;
	}

	public String getMsgid() {
		return msgid;
	}

	public void setMsgid(String msgid) {
		this.msgid = msgid;
	}

	public Date getCreateDatetimeQ() {
		return createDatetimeQ;
	}

	public void setCreateDatetimeQ(Date createDatetimeQ) {
		this.createDatetimeQ = createDatetimeQ;
	}

	public Date getCreateDatetimeZ() {
		return createDatetimeZ;
	}

	public void setCreateDatetimeZ(Date createDatetimeZ) {
		this.createDatetimeZ = createDatetimeZ;
	}

	public String getReportGatewayId() {
		return reportGatewayId;
	}

	public void setReportGatewayId(String reportGatewayId) {
		this.reportGatewayId = reportGatewayId;
	}

	public String getGatewayName() {
		return gatewayName;
	}

	public void setGatewayName(String gatewayName) {
		this.gatewayName = gatewayName;
	}

	public String getSpNumber() {
		return spNumber;
	}

	public void setSpNumber(String spNumber) {
		this.spNumber = spNumber;
	}

	public Date getSendDatetimeQ() {
		return sendDatetimeQ;
	}

	public void setSendDatetimeQ(Date sendDatetimeQ) {
		this.sendDatetimeQ = sendDatetimeQ;
	}

	public Date getSendDatetimeZ() {
		return sendDatetimeZ;
	}

	public void setSendDatetimeZ(Date sendDatetimeZ) {
		this.sendDatetimeZ = sendDatetimeZ;
	}

	public String getSendResult() {
		return sendResult;
	}

	public void setSendResult(String sendResult) {
		this.sendResult = sendResult;
	}

	public JmsgSmsSend() {
		super();
	}

	public String getDataId() {
		return dataId;
	}

	public void setDataId(String dataId) {
		this.dataId = dataId;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	
	public String getCustomerOrderId() {
		return customerOrderId;
	}

	public void setCustomerOrderId(String customerOrderId) {
		this.customerOrderId = customerOrderId;
	}
	
	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	public String getSmsContent() {
		return smsContent;
	}

	public void setSmsContent(String smsContent) {
		this.smsContent = smsContent;
	}
	
	public String getSmsType() {
		return smsType;
	}

	public void setSmsType(String smsType) {
		this.smsType = smsType;
	}
	
	public int getPayCount() {
		return payCount;
	}

	public void setPayCount(int payCount) {
		this.payCount = payCount;
	}
	
	public String getPhoneType() {
		return phoneType;
	}

	public void setPhoneType(String phoneType) {
		this.phoneType = phoneType;
	}
	
	public String getAreaCode() {
		return areaCode;
	}

	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}
	
	public String getPayType() {
		return payType;
	}

	public void setPayType(String payType) {
		this.payType = payType;
	}
	
	public String getPayStatus() {
		return payStatus;
	}

	public void setPayStatus(String payStatus) {
		this.payStatus = payStatus;
	}
	
	public Date getPayTime() {
		return payTime;
	}

	public void setPayTime(Date payTime) {
		this.payTime = payTime;
	}
	
	public String getPushFlag() {
		return pushFlag;
	}

	public void setPushFlag(String pushFlag) {
		this.pushFlag = pushFlag;
	}
	
	public String getSendStatus() {
		return sendStatus;
	}

	public void setSendStatus(String sendStatus) {
		this.sendStatus = sendStatus;
	}
	
	public String getChannelCode() {
		return channelCode;
	}

	public void setChannelCode(String channelCode) {
		this.channelCode = channelCode;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getSendDatetime() {
		return sendDatetime;
	}

	public void setSendDatetime(Date sendDatetime) {
		this.sendDatetime = sendDatetime;
	}
	
	public String getCompanyId() {
		return companyId;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}
	
	public String getSubmitMode() {
		return submitMode;
	}

	public void setSubmitMode(String submitMode) {
		this.submitMode = submitMode;
	}
	
	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}
	
	public Date getCreateDatetime() {
		return createDatetime;
	}

	public void setCreateDatetime(Date createDatetime) {
		this.createDatetime = createDatetime;
	}
	
	public Date getUpdateDatetime() {
		return updateDatetime;
	}

	public void setUpdateDatetime(Date updateDatetime) {
		this.updateDatetime = updateDatetime;
	}
	
}