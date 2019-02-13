/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.sms.entity;

import java.util.Date;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.siloyou.core.common.persistence.DataEntity;

/**
 * 号码运营商Entity
 * @author zhukc
 * @version 2016-05-18
 */
public class JmsgPhoneType extends DataEntity<JmsgPhoneType> {
	
	private static final long serialVersionUID = 1L;
	private String num;		// 号码前3/4位数字
	private String oldNum;	//号段
	private String phoneType;		// 运营商
	private Date createDatetime;		// 创建日期
	
	public String getOldNum() {
		return oldNum;
	}

	public void setOldNum(String oldNum) {
		this.oldNum = oldNum;
	}

	public JmsgPhoneType() {
		super();
	}

	public JmsgPhoneType(String id){
		super(id);
	}

	@Length(min=1, max=4, message="号码前3/4位数字长度必须介于 1 和 4 之间")
	public String getNum() {
		return num;
	}

	public void setNum(String num) {
		this.num = num;
	}
	
	@Length(min=1, max=10, message="运营商长度必须介于 1 和 10 之间")
	public String getPhoneType() {
		return phoneType;
	}

	public void setPhoneType(String phoneType) {
		this.phoneType = phoneType;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getCreateDatetime() {
		return createDatetime;
	}

	public void setCreateDatetime(Date createDatetime) {
		this.createDatetime = createDatetime;
	}
	
}