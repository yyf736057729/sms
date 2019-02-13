/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.sms.entity;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.siloyou.core.common.persistence.DataEntity;
import com.siloyou.core.common.utils.excel.annotation.ExcelField;
import com.siloyou.core.modules.sys.entity.Office;
import com.siloyou.core.modules.sys.entity.User;

/**
 * 短信日报表Entity
 * @author zhukc
 * @version 2016-07-28
 */
public class JmsgSmsDayReport extends DataEntity<JmsgSmsDayReport> {
	
	private static final long serialVersionUID = 1L;
	private User user;						// 用户ID
	private String userId;					// 用户ID
	private Date day;						// 统计时间
	private String phoneType;				// 运营商
	private String gatewayId;				// 通道ID
	private String gatewayName;				// 通道名称
	private Office company;					// 公司
	private String companyId;				// 公司ID
	private String companyName;				// 公司名称
	private String payType;					// 扣费类型 1：提交 2：状态报告
	private int userCount;					// 用户扣费条数
	private int sendCount;					// 日总发送量
	private int failCount;					// 总失败量
	private int sendFailCount;				// 发送失败量
	private int successCount;				// 成功量
	private int submitCount;				// 网关量
	private int submitSuccessCount;			// 网关成功量
	private int submitFailCount;			// 网关失败量
	private int reportCount;				// 状态报告量
	private int reportSuccessCount;			// 状态成功量
	private int reportFailCount;			// 状态失败量
	private int reportNullCount;			// 无状态量
	private int pushSuccessCount;			// 推送成功量
	private int pushFailCount;				// 推送失败量
	private int pushUnkownCount;			// 推送未知量
	private Date backDatetime;				// back_datetime
	private String backFlag;				// 返充状态 1:已返充 0:未返充
	private int userBackCount;				// 返充用户条数
	private int backCount;					// 返充代理条数
	private Date createDatetime;			// create_datetime
	private Date updateDatetime;			// 更新时间
	
	private String reportResult;			//状态报告结果
	
	private Date dayQ;//时间起
	private Date dayZ;//时间止
	private String queryDay;//统计日期String
	private String userCategory;//用户类别
	
	private int pageNo;
	private int pageSize;
	private String cgl;
	private String userName;
	private String loginName;
	
	private String queryType;//查询类型 day:日报表 month:月报表
	
	private String tableName;
	
	public String getUserCategory() {
		return userCategory;
	}

	public void setUserCategory(String userCategory) {
		this.userCategory = userCategory;
	}

	public int getPushSuccessCount() {
		return pushSuccessCount;
	}

	public void setPushSuccessCount(int pushSuccessCount) {
		this.pushSuccessCount = pushSuccessCount;
	}

	public int getPushFailCount() {
		return pushFailCount;
	}

	public void setPushFailCount(int pushFailCount) {
		this.pushFailCount = pushFailCount;
	}

	public int getPushUnkownCount() {
		return pushUnkownCount;
	}

	public void setPushUnkownCount(int pushUnkownCount) {
		this.pushUnkownCount = pushUnkownCount;
	}

	public String getGatewayName() {
		return gatewayName;
	}

	public void setGatewayName(String gatewayName) {
		this.gatewayName = gatewayName;
	}

	public String getGatewayId() {
		return gatewayId;
	}

	public void setGatewayId(String gatewayId) {
		this.gatewayId = gatewayId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getQueryType() {
		return queryType;
	}

	public void setQueryType(String queryType) {
		this.queryType = queryType;
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

	public String getPhoneType() {
		return phoneType;
	}

	public void setPhoneType(String phoneType) {
		this.phoneType = phoneType;
	}

	public int getSubmitFailCount() {
		return submitFailCount;
	}

	public void setSubmitFailCount(int submitFailCount) {
		this.submitFailCount = submitFailCount;
	}

	public int getSendFailCount() {
		return sendFailCount;
	}

	public void setSendFailCount(int sendFailCount) {
		this.sendFailCount = sendFailCount;
	}

	public int getFailCount() {
		return failCount;
	}

	public void setFailCount(int failCount) {
		this.failCount = failCount;
	}

	public int getReportFailCount() {
		return reportFailCount;
	}

	public void setReportFailCount(int reportFailCount) {
		this.reportFailCount = reportFailCount;
	}

	public int getReportNullCount() {
		return reportNullCount;
	}

	public void setReportNullCount(int reportNullCount) {
		this.reportNullCount = reportNullCount;
	}

	public Office getCompany() {
		return company;
	}

	public void setCompany(Office company) {
		this.company = company;
	}

	@ExcelField(title="机构", align=2, sort=40)
	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getCompanyId() {
		return companyId;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	public String getPayType() {
		return payType;
	}

	public void setPayType(String payType) {
		this.payType = payType;
	}

	public String getQueryDay() {
		return queryDay;
	}

	public void setQueryDay(String queryDay) {
		this.queryDay = queryDay;
	}

	public String getReportResult() {
		return reportResult;
	}

	public void setReportResult(String reportResult) {
		this.reportResult = reportResult;
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

	public int getPageNo() {
		return pageNo;
	}

	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public JmsgSmsDayReport() {
		super();
	}

	public JmsgSmsDayReport(String id){
		super(id);
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
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
	public int getUserCount() {
		return userCount;
	}

	public void setUserCount(int userCount) {
		this.userCount = userCount;
	}
	
	@ExcelField(title="发送总量", align=2, sort=50)
	public int getSendCount() {
		return sendCount;
	}

	public void setSendCount(int sendCount) {
		this.sendCount = sendCount;
	}
	
	public int getSuccessCount() {
		return successCount;
	}

	public void setSuccessCount(int successCount) {
		this.successCount = successCount;
	}
	
	public int getSubmitCount() {
		return submitCount;
	}

	public void setSubmitCount(int submitCount) {
		this.submitCount = submitCount;
	}
	
	public int getSubmitSuccessCount() {
		return submitSuccessCount;
	}

	public void setSubmitSuccessCount(int submitSuccessCount) {
		this.submitSuccessCount = submitSuccessCount;
	}
	
	public int getReportCount() {
		return reportCount;
	}

	public void setReportCount(int reportCount) {
		this.reportCount = reportCount;
	}
	
	public int getReportSuccessCount() {
		return reportSuccessCount;
	}

	public void setReportSuccessCount(int reportSuccessCount) {
		this.reportSuccessCount = reportSuccessCount;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@ExcelField(title="返充时间", align=2, sort=90)
	public Date getBackDatetime() {
		return backDatetime;
	}

	public void setBackDatetime(Date backDatetime) {
		this.backDatetime = backDatetime;
	}
	
	@ExcelField(title="返充状态", align=2, sort=80)
	public String getBackFlag() {
		return backFlag;
	}

	public void setBackFlag(String backFlag) {
		this.backFlag = backFlag;
	}
	
	@ExcelField(title="返充条数", align=2, sort=100)
	public int getUserBackCount() {
		return userBackCount;
	}

	public void setUserBackCount(int userBackCount) {
		this.userBackCount = userBackCount;
	}
	
	@ExcelField(title="返充代理条数", align=2, sort=110)
	public int getBackCount() {
		return backCount;
	}

	public void setBackCount(int backCount) {
		this.backCount = backCount;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getCreateDatetime() {
		return createDatetime;
	}

	public void setCreateDatetime(Date createDatetime) {
		this.createDatetime = createDatetime;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@ExcelField(title="更新时间", align=2, sort=120)
	public Date getUpdateDatetime() {
		return updateDatetime;
	}

	public void setUpdateDatetime(Date updateDatetime) {
		this.updateDatetime = updateDatetime;
	}
	
}