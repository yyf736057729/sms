/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.sanerzone.jmsg.entity;
import com.sanerzone.common.support.persistence.DataEntity;


/**
 * 签名统计报表Entity
 * @author zhukc
 * @version 2017-05-25
 */
public class JmsgSmsReportSign extends DataEntity<JmsgSmsReportSign> {
	
	private static final long serialVersionUID = 1L;
	private String day;				// 日期
	private String userId;			// 用户ID
	private String smsSign;			// 签名
	private int sendCount;			// 发送量
	
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public JmsgSmsReportSign() {
		super();
	}

	public JmsgSmsReportSign(String id){
		super(id);
	}

	public String getDay() {
		return day;
	}

	public void setDay(String day) {
		this.day = day;
	}
	
	public String getSmsSign() {
		return smsSign;
	}

	public void setSmsSign(String smsSign) {
		this.smsSign = smsSign;
	}
	
	public int getSendCount() {
		return sendCount;
	}

	public void setSendCount(int sendCount) {
		this.sendCount = sendCount;
	}
	
}