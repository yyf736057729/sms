/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.sms.entity;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.siloyou.core.common.persistence.DataEntity;

/**
 * 自定义任务Entity
 * @author zj
 * @version 2017-04-07
 */
public class JmsgCustomTask extends DataEntity<JmsgCustomTask> {
	
	private static final long serialVersionUID = 1L;
	private String taskName;		// 任务名称
	private String type;		// 任务类型
	private String paramJson;		// 参数
	private String executeClass;		// 执行类
	private String executeResult;		// 执行结果
	private String status;		// 任务状态 待执行
	private Date executeStartTime;		// 任务执行开始时间
	private Date executeEndTime;		// 任务执行结束时间
	private Date createTime;		// 创建时间
	private Date dayQ;				// 时间起
	private Date dayZ;				// 时间止
	private String version;			// 版本
	
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

	public JmsgCustomTask() {
		super();
	}

	public JmsgCustomTask(String id){
		super(id);
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public String getParamJson() {
		return paramJson;
	}

	public void setParamJson(String paramJson) {
		this.paramJson = paramJson;
	}
	
	public String getExecuteClass() {
		return executeClass;
	}

	public void setExecuteClass(String executeClass) {
		this.executeClass = executeClass;
	}
	
	public String getExecuteResult() {
		return executeResult;
	}

	public void setExecuteResult(String executeResult) {
		this.executeResult = executeResult;
	}
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getExecuteStartTime() {
		return executeStartTime;
	}

	public void setExecuteStartTime(Date executeStartTime) {
		this.executeStartTime = executeStartTime;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getExecuteEndTime() {
		return executeEndTime;
	}

	public void setExecuteEndTime(Date executeEndTime) {
		this.executeEndTime = executeEndTime;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}
	
}