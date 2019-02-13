/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.sanerzone.common.modules.phone.entity;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sanerzone.common.support.persistence.DataEntity;

/**
 * 黑名单Entity
 * @author zhukc
 * @version 2016-05-18
 */
public class JmsgPhoneBlacklist extends DataEntity<JmsgPhoneBlacklist> {
	
	private static final long serialVersionUID = 1L;
	private String phone;				// 手机号码
	private String scope;				// 范围 0：全局 1：用户
	private Date createDatetime;		// 创建日期
	private String type;				// 类型
	
	public JmsgPhoneBlacklist() {
		super();
	}

	public JmsgPhoneBlacklist(String id){
		super(id);
	}
	

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getCreateDatetime() {
		return createDatetime;
	}

	public void setCreateDatetime(Date createDatetime) {
		this.createDatetime = createDatetime;
	}
	
}