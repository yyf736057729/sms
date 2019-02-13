/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.sms.entity;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.siloyou.core.common.persistence.DataEntity;

/**
 * 营销黑名单Entity
 * @author zhukc
 * @version 2017-12-05
 */
public class JmsgPhoneMarket extends DataEntity<JmsgPhoneMarket> {
	
	private static final long serialVersionUID = 1L;
	private String userid;			// userid
	private String phone;			// 手机号码
	private String type;			// 0:系统退订 1:用户自主退订
	private Date createtime;		// 创建时间
	private Date createtimeQ;		// 创建时间
	private Date createtimeZ;		// 创建时间
	
	public JmsgPhoneMarket() {
		super();
	}

	public Date getCreatetimeQ() {
		return createtimeQ;
	}

	public void setCreatetimeQ(Date createtimeQ) {
		this.createtimeQ = createtimeQ;
	}

	public Date getCreatetimeZ() {
		return createtimeZ;
	}

	public void setCreatetimeZ(Date createtimeZ) {
		this.createtimeZ = createtimeZ;
	}

	public JmsgPhoneMarket(String id){
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