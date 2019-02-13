/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.sanerzone.common.modules.smscenter.entity;

import java.util.Date;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sanerzone.common.support.persistence.DataEntity;

/**
 * 敏感词Entity
 * @author zhukc
 * @version 2016-05-18
 */
public class JmsgSmsKeywords extends DataEntity<JmsgSmsKeywords> {
	
	private static final long serialVersionUID = 1L;
	private String keywords;		// 敏感词
	private String scope;		// 范围 0：全局 1：用户
	private Date createDatetime;		// 创建日期
	
	public JmsgSmsKeywords() {
		super();
	}

	public JmsgSmsKeywords(String id){
		super(id);
	}

	@Length(min=1, max=256, message="敏感词长度必须介于 1 和 256 之间")
	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}
	
	@Length(min=1, max=1, message="范围 0：全局 1：用户长度必须介于 1 和 1 之间")
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