/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.sms.entity;

import java.util.Date;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.siloyou.core.common.persistence.DataEntity;

/**
 * 动态黑名单Entity
 * @author zhukc
 * @version 2016-06-21
 */
public class JmsgPhoneDynamic extends DataEntity<JmsgPhoneDynamic> {
	
	private static final long serialVersionUID = 1L;
	private String phone;		// 手机号码
	private Date createtime;	// 创建时间
	private String type;		// 类型
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public JmsgPhoneDynamic() {
		super();
	}

	public JmsgPhoneDynamic(String id){
		super(id);
	}

	@Length(min=1, max=20, message="手机号码长度必须介于 1 和 20 之间")
	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getCreatetime() {
		return createtime;
	}

	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}
	
}