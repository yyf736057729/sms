/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.sms.entity;

import com.siloyou.core.modules.sys.entity.User;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

import com.siloyou.core.common.persistence.DataEntity;

/**
 * 用户签名Entity
 * @author zj
 * @version 2016-09-08
 */
public class JmsgUserSign extends DataEntity<JmsgUserSign> {
	
	private static final long serialVersionUID = 1L;
	private User user;		// user_id
	private String sign;		// sign
	private String createUserId;		// create_user_id
	private Date createtime;		// createtime
	private Date createtimeQ;
	private Date createtimeZ;
	
	public Date getCreatetimeQ()
    {
        return createtimeQ;
    }

    public void setCreatetimeQ(Date createtimeQ)
    {
        this.createtimeQ = createtimeQ;
    }

    public Date getCreatetimeZ()
    {
        return createtimeZ;
    }

    public void setCreatetimeZ(Date createtimeZ)
    {
        this.createtimeZ = createtimeZ;
    }

    public JmsgUserSign() {
		super();
	}

	public JmsgUserSign(String id){
		super(id);
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}
	
	public String getCreateUserId() {
		return createUserId;
	}

	public void setCreateUserId(String createUserId) {
		this.createUserId = createUserId;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getCreatetime() {
		return createtime;
	}

	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}
	
}