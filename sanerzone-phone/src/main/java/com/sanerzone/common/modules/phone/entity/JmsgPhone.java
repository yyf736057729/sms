/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.sanerzone.common.modules.phone.entity;

import java.util.Date;

import com.sanerzone.common.support.persistence.DataEntity;

/**
 * 动态黑名单Entity
 * @author zj
 * @version 2016-09-26
 */
public class JmsgPhone extends DataEntity<JmsgPhone> {
	
	private static final long serialVersionUID = 1L;
	private String phone;		// 手机号码
	private String scope;		// 范围 0：全局 1：用户
	private String type;		// 类型(1:退订)
	private Date createDatetime;		// 创建日期
	private Date createDatetimeQ;
	private Date createDatetimeZ;
	
	public Date getCreateDatetimeQ()
    {
        return createDatetimeQ;
    }

    public void setCreateDatetimeQ(Date createDatetimeQ)
    {
        this.createDatetimeQ = createDatetimeQ;
    }

    public Date getCreateDatetimeZ()
    {
        return createDatetimeZ;
    }

    public void setCreateDatetimeZ(Date createDatetimeZ)
    {
        this.createDatetimeZ = createDatetimeZ;
    }

    public JmsgPhone() {
		super();
	}

	public JmsgPhone(String id){
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
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public Date getCreateDatetime() {
		return createDatetime;
	}

	public void setCreateDatetime(Date createDatetime) {
		this.createDatetime = createDatetime;
	}
	
}