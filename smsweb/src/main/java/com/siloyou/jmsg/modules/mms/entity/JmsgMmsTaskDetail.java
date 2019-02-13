/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.mms.entity;

import java.util.Date;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.siloyou.core.common.persistence.DataEntity;
import com.siloyou.core.modules.sys.entity.User;

/**
 * 彩信发送明细Entity
 * @author zhukc
 * @version 2016-05-20
 */
public class JmsgMmsTaskDetail extends DataEntity<JmsgMmsTaskDetail> {
	
	private static final long serialVersionUID = 1L;
	private String taskId;				// 任务ID
	private String phone;				// 手机号码
	private String sendStatus;			// 发送状态
	private Date sendDatetime;			// 发送时间
	private Date receiveDatetime;		// 接收时间
	private String deviceType;			// 接收设备类型
	private Date createDatetime;		// 创建日期
	private String createUserId;		// 创建人
	private User user;
	private String phoneType;			//手机类型
	private String mmsUrl;				//彩信url
	private String mmsTitle;			//彩信标题
	private int mmsSize;				//彩信大小
	private String mmsId;				//彩信ID
	
	private String submitStatus;		//网关状态
	private String reportStatus;		//状态报告
	private String payMode;				//扣款方式
	private String sendResult;			//发送结果
	
	public String getSendResult() {
		return sendResult;
	}

	public void setSendResult(String sendResult) {
		this.sendResult = sendResult;
	}

	public String getPayMode() {
		return payMode;
	}

	public void setPayMode(String payMode) {
		this.payMode = payMode;
	}

	public String getMmsId() {
		return mmsId;
	}

	public void setMmsId(String mmsId) {
		this.mmsId = mmsId;
	}

	public int getMmsSize() {
		return mmsSize;
	}

	public void setMmsSize(int mmsSize) {
		this.mmsSize = mmsSize;
	}

	public String getSubmitStatus() {
		return submitStatus;
	}

	public void setSubmitStatus(String submitStatus) {
		this.submitStatus = submitStatus;
	}

	public String getReportStatus() {
		return reportStatus;
	}

	public void setReportStatus(String reportStatus) {
		this.reportStatus = reportStatus;
	}

	public String getMmsUrl() {
		return mmsUrl;
	}

	public void setMmsUrl(String mmsUrl) {
		this.mmsUrl = mmsUrl;
	}

	public String getMmsTitle() {
		return mmsTitle;
	}

	public void setMmsTitle(String mmsTitle) {
		this.mmsTitle = mmsTitle;
	}

	public String getPhoneType() {
		return phoneType;
	}

	public void setPhoneType(String phoneType) {
		this.phoneType = phoneType;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public JmsgMmsTaskDetail() {
		super();
	}

	public JmsgMmsTaskDetail(String id){
		super(id);
	}

	@Length(min=1, max=11, message="任务ID长度必须介于 1 和 11 之间")
	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	
	@Length(min=1, max=20, message="手机号码长度必须介于 1 和 20 之间")
	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	@Length(min=1, max=1, message="发送状态长度必须介于 1 和 1 之间")
	public String getSendStatus() {
		return sendStatus;
	}

	public void setSendStatus(String sendStatus) {
		this.sendStatus = sendStatus;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getSendDatetime() {
		return sendDatetime;
	}

	public void setSendDatetime(Date sendDatetime) {
		this.sendDatetime = sendDatetime;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getReceiveDatetime() {
		return receiveDatetime;
	}

	public void setReceiveDatetime(Date receiveDatetime) {
		this.receiveDatetime = receiveDatetime;
	}
	
	@Length(min=0, max=20, message="接收设备类型长度必须介于 0 和 20 之间")
	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getCreateDatetime() {
		return createDatetime;
	}

	public void setCreateDatetime(Date createDatetime) {
		this.createDatetime = createDatetime;
	}
	
	public String getCreateUserId() {
		return createUserId;
	}

	public void setCreateUserId(String createUserId) {
		this.createUserId = createUserId;
	}
	
}