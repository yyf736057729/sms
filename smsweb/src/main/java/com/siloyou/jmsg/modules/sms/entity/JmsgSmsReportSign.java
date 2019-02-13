/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.sms.entity;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.siloyou.core.common.persistence.DataEntity;
import com.siloyou.core.modules.sys.entity.Office;
import com.siloyou.core.modules.sys.entity.User;

/**
 * 签名统计报表Entity
 * @author zhukc
 * @version 2017-05-25
 */
public class JmsgSmsReportSign extends DataEntity<JmsgSmsReportSign> {
	
	private static final long serialVersionUID = 1L;
	private Office company;
	private Date day;				// 日期
	private User user;				// 用户ID
	private String smsSign;			// 签名
	private String sendCount;		// 发送量
	private Date dayQ;
	private Date dayZ;
	private Date updateDatetime;
	
	public Date getUpdateDatetime() {
		return updateDatetime;
	}

	public void setUpdateDatetime(Date updateDatetime) {
		this.updateDatetime = updateDatetime;
	}

	public Office getCompany() {
		return company;
	}

	public void setCompany(Office company) {
		this.company = company;
	}

	public Date getDayQ() {
		return dayQ;
	}

	public void setDayQ(Date dayQ) {
		this.dayQ = dayQ;
	}

	public Date getDayZ() {
		return dayZ;
	}

	public void setDayZ(Date dayZ) {
		this.dayZ = dayZ;
	}

	public JmsgSmsReportSign() {
		super();
	}

	public JmsgSmsReportSign(String id){
		super(id);
	}

	@JsonFormat(pattern = "yyyy-MM-dd")
	public Date getDay() {
		return day;
	}

	public void setDay(Date day) {
		this.day = day;
	}
	
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	public String getSmsSign() {
		return smsSign;
	}

	public void setSmsSign(String smsSign) {
		this.smsSign = smsSign;
	}
	
	public String getSendCount() {
		return sendCount;
	}

	public void setSendCount(String sendCount) {
		this.sendCount = sendCount;
	}
	
}