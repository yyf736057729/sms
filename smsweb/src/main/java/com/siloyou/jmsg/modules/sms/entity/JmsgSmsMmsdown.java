/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.sms.entity;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.siloyou.core.common.persistence.DataEntity;

/**
 * 短信彩信下载Entity
 * @author zhukc
 * @version 2017-05-23
 */
public class JmsgSmsMmsdown extends DataEntity<JmsgSmsMmsdown> {
	
	private static final long serialVersionUID = 1L;
	private String userAgent;		// 用户UA
	private Date downTime;			// 下载时间
	private Date createTime;		// 创建时间
	private String tableName;
	
	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public JmsgSmsMmsdown() {
		super();
	}

	public JmsgSmsMmsdown(String id){
		super(id);
	}

	public String getUserAgent() {
		return userAgent;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getDownTime() {
		return downTime;
	}

	public void setDownTime(Date downTime) {
		this.downTime = downTime;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
}