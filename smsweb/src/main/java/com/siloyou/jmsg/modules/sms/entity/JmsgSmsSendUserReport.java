/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.sms.entity;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.siloyou.core.common.utils.excel.annotation.ExcelField;

/**
 * 短信发送Entity
 * @author zhukc
 * @version 2016-07-16
 */
public class JmsgSmsSendUserReport {
	
	private String id;					// ID
	private String taskId;				// 系统订单号
	private String userId;				// 用户ID
	private String phone;				// 手机号
	private String spNumber;			// 接入号
	private String smsContent;			// 短信内容
	private Date sendDatetime;			// 发送时间
	private int payCount;				// 扣费条数
	private Date createDatetime;		// 创建时间
	private String reportStatus;		// 网关状态
	
	
	@ExcelField(title="ID", align=2, sort=20)
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	@ExcelField(title="批次ID", align=2, sort=30)
	public String getTaskId() {
		return taskId;
	}
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	@ExcelField(title="手机号码", align=2, sort=50)
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
	
	@ExcelField(title="短信内容", align=2, sort=70)
	public String getSmsContent() {
		return smsContent;
	}
	public void setSmsContent(String smsContent) {
		this.smsContent = smsContent;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@ExcelField(title="发送时间", align=2, sort=100)
	public Date getSendDatetime() {
		return sendDatetime;
	}
	public void setSendDatetime(Date sendDatetime) {
		this.sendDatetime = sendDatetime;
	}
	
	@ExcelField(title="扣费条数", align=2, sort=80)
	public int getPayCount() {
		return payCount;
	}
	public void setPayCount(int payCount) {
		this.payCount = payCount;
	}
	
	@ExcelField(title="发送结果", align=2, sort=110)
	public String getReportStatus() {
		return reportStatus;
	}
	public void setReportStatus(String reportStatus) {
		this.reportStatus = reportStatus;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@ExcelField(title="提交时间", align=2, sort=90)
	public Date getCreateDatetime() {
		return createDatetime;
	}
	public void setCreateDatetime(Date createDatetime) {
		this.createDatetime = createDatetime;
	}
}