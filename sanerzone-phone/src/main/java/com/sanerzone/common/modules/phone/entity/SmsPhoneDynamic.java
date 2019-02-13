/**
 * Copyright &copy; 2015-2016 SanerZone All rights reserved.
 */
package com.sanerzone.common.modules.phone.entity;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sanerzone.common.support.persistence.DataEntity;

/**
 * 营销黑名单Entity
 * @author zhukc
 * @version 2017-03-10
 */
public class SmsPhoneDynamic extends DataEntity<SmsPhoneDynamic> {
	
	private static final long serialVersionUID = 1L;
	private String userid;			// userid
	private String phone;			// 手机号码
	private String type;			// 0:系统退订 1:用户自主退订
	private Date createtime;		// 创建时间
	
	public SmsPhoneDynamic() {
		super();
	}

	public SmsPhoneDynamic(String id){
		super(id);
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}
	
	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getCreatetime() {
		return createtime;
	}

	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}
	
}