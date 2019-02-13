/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.sms.entity;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.siloyou.core.common.persistence.DataEntity;
import com.siloyou.core.common.utils.excel.annotation.ExcelField;

/**
 * 短信日报表Entity
 * @author zhukc
 * @version 2016-07-28
 */
public class JmsgSmsGatewayReport extends DataEntity<JmsgSmsGatewayReport> {
	
	private static final long serialVersionUID = 1L;
	private Date day;						// 统计时间
	private String companyName;				// 公司名称
	private String userCount;					// 用户扣费条数
	private String sendCount;					// 日总发送量
	private String failCount;					// 总失败量
	private String sendFailCount;				// 发送失败量
	private String successCount;				// 成功量
	private String submitCount;				// 网关量
	private String submitSuccessCount;			// 网关成功量
	private String submitFailCount;			// 网关失败量
	private String reportCount;				// 状态报告量
	private String reportSuccessCount;			// 状态成功量
	private String reportFailCount;			// 状态失败量
	private String reportNullCount;			// 无状态量
	private Date updateDatetime;			// 更新时间
	
	private String cgl;						//平台成功率
	private String submitCgl;				//网关成功率
	private String reportSuccessZb;			//状态报告占比
	private String reportNullZb;			//状态空占比
	private String userName;
	private String loginName;
	
	
	@ExcelField(title="状态空占比", align=2, sort=150)
	public String getReportNullZb() {
		return reportNullZb;
	}

	public void setReportNullZb(String reportNullZb) {
		this.reportNullZb = reportNullZb;
	}

	@ExcelField(title="状态成功占比", align=2, sort=130)
	public String getReportSuccessZb() {
		return reportSuccessZb;
	}

	public void setReportSuccessZb(String reportSuccessZb) {
		this.reportSuccessZb = reportSuccessZb;
	}

	@ExcelField(title="网关失败率", align=2, sort=110)
	public String getSubmitCgl() {
		return submitCgl;
	}

	public void setSubmitCgl(String submitCgl) {
		this.submitCgl = submitCgl;
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
	
	@ExcelField(title="平台成功率", align=2, sort=70)
	public String getCgl() {
		return cgl;
	}

	public void setCgl(String cgl) {
		this.cgl = cgl;
	}

	@ExcelField(title="网关失败量", align=2, sort=100)
	public String getSubmitFailCount() {
		return submitFailCount;
	}

	public void setSubmitFailCount(String submitFailCount) {
		this.submitFailCount = submitFailCount;
	}

	public String getSendFailCount() {
		return sendFailCount;
	}

	public void setSendFailCount(String sendFailCount) {
		this.sendFailCount = sendFailCount;
	}

	public String getFailCount() {
		return failCount;
	}

	public void setFailCount(String failCount) {
		this.failCount = failCount;
	}

	
	public String getReportFailCount() {
		return reportFailCount;
	}

	public void setReportFailCount(String reportFailCount) {
		this.reportFailCount = reportFailCount;
	}

	@ExcelField(title="状态报告空", align=2, sort=140)
	public String getReportNullCount() {
		return reportNullCount;
	}

	public void setReportNullCount(String reportNullCount) {
		this.reportNullCount = reportNullCount;
	}

	@ExcelField(title="机构", align=2, sort=40)
	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}


	public JmsgSmsGatewayReport() {
		super();
	}

	public JmsgSmsGatewayReport(String id){
		super(id);
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@ExcelField(title="统计日期", align=2, sort=10)
	public Date getDay() {
		return day;
	}

	public void setDay(Date day) {
		this.day = day;
	}
	
	@ExcelField(title="计费成功量", align=2, sort=60)
	public String getUserCount() {
		return userCount;
	}

	public void setUserCount(String userCount) {
		this.userCount = userCount;
	}
	
	@ExcelField(title="发送总量", align=2, sort=50)
	public String getSendCount() {
		return sendCount;
	}

	public void setSendCount(String sendCount) {
		this.sendCount = sendCount;
	}
	
	@ExcelField(title="网关总量", align=2, sort=80)
	public String getSuccessCount() {
		return successCount;
	}

	public void setSuccessCount(String successCount) {
		this.successCount = successCount;
	}
	
	
	public String getSubmitCount() {
		return submitCount;
	}

	public void setSubmitCount(String submitCount) {
		this.submitCount = submitCount;
	}
	
	@ExcelField(title="网关成功量", align=2, sort=90)
	public String getSubmitSuccessCount() {
		return submitSuccessCount;
	}

	public void setSubmitSuccessCount(String submitSuccessCount) {
		this.submitSuccessCount = submitSuccessCount;
	}
	
	public String getReportCount() {
		return reportCount;
	}

	public void setReportCount(String reportCount) {
		this.reportCount = reportCount;
	}
	
	@ExcelField(title="状态报告成功", align=2, sort=120)
	public String getReportSuccessCount() {
		return reportSuccessCount;
	}

	public void setReportSuccessCount(String reportSuccessCount) {
		this.reportSuccessCount = reportSuccessCount;
	}
	
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@ExcelField(title="更新时间", align=2, sort=160)
	public Date getUpdateDatetime() {
		return updateDatetime;
	}

	public void setUpdateDatetime(Date updateDatetime) {
		this.updateDatetime = updateDatetime;
	}
	
}