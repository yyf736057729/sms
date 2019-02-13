/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.mms.entity;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.siloyou.core.common.persistence.DataEntity;
import com.siloyou.core.modules.sys.entity.User;

/**
 * 彩信下载Entity
 * @author zj
 * @version 2016-12-19
 */
public class JmsgMmsDownload extends DataEntity<JmsgMmsDownload> {
	
	private static final long serialVersionUID = 1L;
	private User user;		// 用户ID
	private String taskid;		// 任务ID
	private String bizid;		// 业务ID
	private Date receiveDatetime;		// 接收时间
	private String deviceType;		// 接收设备类型
	private Date createDatetime;		// 创建日期
	
	public JmsgMmsDownload() {
		super();
	}

	public JmsgMmsDownload(String id){
		super(id);
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	public String getTaskid() {
		return taskid;
	}

	public void setTaskid(String taskid) {
		this.taskid = taskid;
	}
	
	public String getBizid() {
		return bizid;
	}

	public void setBizid(String bizid) {
		this.bizid = bizid;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getReceiveDatetime() {
		return receiveDatetime;
	}

	public void setReceiveDatetime(Date receiveDatetime) {
		this.receiveDatetime = receiveDatetime;
	}
	
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
	
}