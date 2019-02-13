/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.sms.entity;

import org.hibernate.validator.constraints.Length;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

import com.siloyou.core.common.persistence.DataEntity;

/**
 * 告警表Entity
 * @author zj
 * @version 2016-10-15
 */
public class JmsgSmsWarn extends DataEntity<JmsgSmsWarn> {
	
	private static final long serialVersionUID = 1L;
	private String warnType;		// 告警类型 1：网关
	private String warnContent;		// 告警内容
	private String warnStatus;		// 告警状态 1：以处理 0：未处理
	private Date createTime;		// 告警时间
	
	public JmsgSmsWarn() {
		super();
	}

	public JmsgSmsWarn(String id){
		super(id);
	}

	@Length(min=1, max=1, message="告警类型长度必须介于 1 和 1 之间")
	public String getWarnType() {
		return warnType;
	}

	public void setWarnType(String warnType) {
		this.warnType = warnType;
	}
	
	@Length(min=0, max=255, message="告警内容长度必须介于 0 和 255 之间")
	public String getWarnContent() {
		return warnContent;
	}

	public void setWarnContent(String warnContent) {
		this.warnContent = warnContent;
	}
	
	@Length(min=1, max=1, message="告警状态长度必须介于 1 和 1 之间")
	public String getWarnStatus() {
		return warnStatus;
	}

	public void setWarnStatus(String warnStatus) {
		this.warnStatus = warnStatus;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
}