/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.mms.entity;

import java.util.Date;

import javax.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.siloyou.core.common.persistence.DataEntity;

/**
 * 彩信总日报Entity
 * @author zhukc
 * @version 2016-06-08
 */
public class JmsgMmsAlldayReport extends DataEntity<JmsgMmsAlldayReport> {
	
	private static final long serialVersionUID = 1L;
	private Date day;					// 统计时间
	private Date dayQ;
	private Date dayZ;
	private String taskId;				// 任务ID
	private String mmsId;				// 彩信ID
	private String mmsTitle;			// 彩信标题
	private Long count;					// 发送总量
	private Long sendCount;				// 日总发送量
	private Long submitCount;			// 网关成功量
	private Long reportCount;			// 状态报告成功
	private Long downloadCount;			// 下载成功
	private Date createDatetime;		// 更新时间
	private String destTerminalId;
	private String stat;
	private String phone;				//手机号码
	
	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getDestTerminalId() {
		return destTerminalId;
	}

	public void setDestTerminalId(String destTerminalId) {
		this.destTerminalId = destTerminalId;
	}

	public String getStat() {
		return stat;
	}

	public void setStat(String stat) {
		this.stat = stat;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getMmsId() {
		return mmsId;
	}

	public void setMmsId(String mmsId) {
		this.mmsId = mmsId;
	}

	public String getMmsTitle() {
		return mmsTitle;
	}

	public void setMmsTitle(String mmsTitle) {
		this.mmsTitle = mmsTitle;
	}

	public Long getCount() {
		if(count == null)return 0L;
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}

	public Date getDayQ() {
		return dayQ;
	}

	public void setDayQ(Date dayQ) {
		this.dayQ = dayQ;
	}

	public Date getDayZ() {
		return dayZ;
	}

	public void setDayZ(Date dayZ) {
		this.dayZ = dayZ;
	}

	public JmsgMmsAlldayReport() {
		super();
	}

	public JmsgMmsAlldayReport(String id){
		super(id);
	}

	@JsonFormat(pattern = "yyyy-MM-dd")
	public Date getDay() {
		return day;
	}

	public void setDay(Date day) {
		this.day = day;
	}
	
	public Long getSendCount() {
		if(sendCount == null)return 0L;
		return sendCount;
	}

	public void setSendCount(Long sendCount) {
		this.sendCount = sendCount;
	}

	public Long getSubmitCount() {
		return submitCount;
	}

	public void setSubmitCount(Long submitCount) {
		this.submitCount = submitCount;
	}

	public Long getReportCount() {
		if(reportCount == null)return 0L;
		return reportCount;
	}

	public void setReportCount(Long reportCount) {
		this.reportCount = reportCount;
	}

	public Long getDownloadCount() {
		if(downloadCount == null)return 0L;
		return downloadCount;
	}

	public void setDownloadCount(Long downloadCount) {
		this.downloadCount = downloadCount;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@NotNull(message="更新时间不能为空")
	public Date getCreateDatetime() {
		return createDatetime;
	}

	public void setCreateDatetime(Date createDatetime) {
		this.createDatetime = createDatetime;
	}
	
}