/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.sms.entity;

import com.siloyou.core.common.persistence.DataEntity;
import com.siloyou.core.common.utils.excel.annotation.ExcelField;

/**
 * 短信日报表Entity
 * @author zhukc
 * @version 2016-07-28
 */
public class JmsgSmsMonthReport extends DataEntity<JmsgSmsMonthReport> {
	
	private static final long serialVersionUID = 1L;
	private String day;						// 统计时间
	private String companyName;				// 公司名称
	private String userCount;				// 用户扣费条数
	private String sendCount;				// 日总发送量
	
	private String cgl;
	private String userName;
	private String loginName;
	private String price;
	
	
	@ExcelField(title="单价", align=2, sort=80)
	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	@ExcelField(title="登录帐号", align=2, sort=30)
	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	@ExcelField(title="用户名称", align=2, sort=20)
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	@ExcelField(title="成功率", align=2, sort=70)
	public String getCgl() {
		return cgl;
	}

	public void setCgl(String cgl) {
		this.cgl = cgl;
	}

	@ExcelField(title="机构", align=2, sort=40)
	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}



	public JmsgSmsMonthReport() {
		super();
	}

	public JmsgSmsMonthReport(String id){
		super(id);
	}

	@ExcelField(title="统计日期", align=2, sort=10)
	public String getDay() {
		return day;
	}

	public void setDay(String day) {
		this.day = day;
	}
	
	@ExcelField(title="月计费成功量", align=2, sort=60)
	public String getUserCount() {
		return userCount;
	}

	public void setUserCount(String userCount) {
		this.userCount = userCount;
	}
	
	@ExcelField(title="月发送总量", align=2, sort=50)
	public String getSendCount() {
		return sendCount;
	}

	public void setSendCount(String sendCount) {
		this.sendCount = sendCount;
	}
	
	
}