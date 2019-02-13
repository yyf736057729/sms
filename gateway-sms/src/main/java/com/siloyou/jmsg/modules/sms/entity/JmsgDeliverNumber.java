package com.siloyou.jmsg.modules.sms.entity;

import java.util.Date;

public class JmsgDeliverNumber extends AbstractEntity {
	
	private static final long serialVersionUID = 1L;
	private int userId;				// 用户ID
	private String spNumber;		// 接入号
	private Date createtime;		// 创建时间
	
	public JmsgDeliverNumber() {
		super();
	}

	public int getUserId() {
		return userId;
	}


	public void setUserId(int userId) {
		this.userId = userId;
	}


	public String getSpNumber() {
		return spNumber;
	}

	public void setSpNumber(String spNumber) {
		this.spNumber = spNumber;
	}
	
	public Date getCreatetime() {
		return createtime;
	}

	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}
	
}