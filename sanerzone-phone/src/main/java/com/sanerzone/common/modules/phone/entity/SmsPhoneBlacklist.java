/**
 * Copyright &copy; 2015-2016 SanerZone All rights reserved.
 */
package com.sanerzone.common.modules.phone.entity;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sanerzone.common.support.persistence.DataEntity;

/**
 * 系统黑名单Entity
 * @author zhukc
 * @version 2017-03-10
 */
public class SmsPhoneBlacklist extends DataEntity<SmsPhoneBlacklist> {
	
	private static final long serialVersionUID = 1L;
	private String phone;				// 手机号码
	private String scope;				// 范围 0：全局 1：用户
	private Date createDatetime;		// 创建日期
	
	public SmsPhoneBlacklist() {
		super();
	}

	public SmsPhoneBlacklist(String id){
		super(id);
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