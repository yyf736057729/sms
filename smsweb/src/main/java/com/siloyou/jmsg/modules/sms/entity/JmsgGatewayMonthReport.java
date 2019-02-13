/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.sms.entity;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.siloyou.core.common.persistence.DataEntity;
import com.siloyou.core.common.utils.excel.annotation.ExcelField;

/**
 * 通道日发送报表Entity
 * @author zhukc
 * @version 2016-08-04
 */
public class JmsgGatewayMonthReport extends DataEntity<JmsgGatewayMonthReport> {
	
	private static final long serialVersionUID = 1L;
	private String day;						// 统计日期
	private String gatewayId;				// 通道ID
	private String gatewayName;				// 通道名称
	private Long sendCount;					// 发送总量
	private Long failCount;					// 失败总量
	private Long sendFailCount;				// 发送失败量
	private Long reportCount;				// 状态报告量
	private Long reportSuccessCount;		// 状态报告成功量
	private Long reportFailCount;			// 状态报告失败量
	private Long reportNullCount;			// 无状态报告量
	private Date updateDatetime;			// 更新时间
	
	private String reportResult;			//状态报告
	private Long submitCount;				//提交总量
	private Long submitSuccessCount;		//提交成功量
	private Long submitFailCount;			//提交失败量
	
	private Date dayQ;//时间起
	private Date dayZ;//时间止
	private String queryDay;//统计日期String
	
	private int pageNo;
	private int pageSize;
	
	private String submitCgl;//网关成功率(网关成功量/网关总量，单位%，保留2位小数)
	private String reportSuccessZb;//状态报告占比(状态报告成功/网关成功量，单位%，保留2位小数)
	private String reportNullZb;//状态空占比=状态报告失败/网关成功量，单位%，保留2位小数
	
	private String queryType;//查询类型 day,month
	
	public String getQueryType() {
		return queryType;
	}

	public void setQueryType(String queryType) {
		this.queryType = queryType;
	}

	@ExcelField(title="网关成功率", align=2, sort=70)
	public String getSubmitCgl() {
		return submitCgl;
	}

	public void setSubmitCgl(String submitCgl) {
		this.submitCgl = submitCgl;
	}

	@ExcelField(title="状态成功占比", align=2, sort=90)
	public String getReportSuccessZb() {
		return reportSuccessZb;
	}

	public void setReportSuccessZb(String reportSuccessZb) {
		this.reportSuccessZb = reportSuccessZb;
	}

	@ExcelField(title="状态空占比", align=2, sort=110)
	public String getReportNullZb() {
		return reportNullZb;
	}

	public void setReportNullZb(String reportNullZb) {
		this.reportNullZb = reportNullZb;
	}

	@ExcelField(title="网关总量", align=2, sort=40)
	public Long getSubmitCount() {
		return submitCount;
	}

	public void setSubmitCount(Long submitCount) {
		this.submitCount = submitCount;
	}

	@ExcelField(title="网关成功量", align=2, sort=50)
	public Long getSubmitSuccessCount() {
		return submitSuccessCount;
	}

	public void setSubmitSuccessCount(Long submitSuccessCount) {
		this.submitSuccessCount = submitSuccessCount;
	}

	@ExcelField(title="网关失败量", align=2, sort=60)
	public Long getSubmitFailCount() {
		return submitFailCount;
	}

	public void setSubmitFailCount(Long submitFailCount) {
		this.submitFailCount = submitFailCount;
	}

	public Long getFailCount() {
		return failCount;
	}

	public void setFailCount(Long failCount) {
		this.failCount = failCount;
	}

	public Long getSendFailCount() {
		return sendFailCount;
	}

	public void setSendFailCount(Long sendFailCount) {
		this.sendFailCount = sendFailCount;
	}
	
	public Long getReportFailCount() {
		return reportFailCount;
	}

	public void setReportFailCount(Long reportFailCount) {
		this.reportFailCount = reportFailCount;
	}

	@ExcelField(title="状态报告空", align=2, sort=100)
	public Long getReportNullCount() {
		return reportNullCount;
	}

	public void setReportNullCount(Long reportNullCount) {
		this.reportNullCount = reportNullCount;
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

	public String getQueryDay() {
		return queryDay;
	}

	public void setQueryDay(String queryDay) {
		this.queryDay = queryDay;
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

	public JmsgGatewayMonthReport() {
		super();
	}

	public JmsgGatewayMonthReport(String id){
		super(id);
	}

	@ExcelField(title="统计时间", align=2, sort=10)
	public String getDay() {
		return day;
	}

	public void setDay(String day) {
		this.day = day;
	}
	
	@ExcelField(title="通道ID", align=2, sort=20)
	public String getGatewayId() {
		return gatewayId;
	}

	public void setGatewayId(String gatewayId) {
		this.gatewayId = gatewayId;
	}
	
	@ExcelField(title="通道名称", align=2, sort=30)
	public String getGatewayName() {
		return gatewayName;
	}

	public void setGatewayName(String gatewayName) {
		this.gatewayName = gatewayName;
	}
	
	public Long getSendCount() {
		return sendCount;
	}

	public void setSendCount(Long sendCount) {
		this.sendCount = sendCount;
	}
	
	public Long getReportCount() {
		return reportCount;
	}

	public void setReportCount(Long reportCount) {
		this.reportCount = reportCount;
	}
	
	@ExcelField(title="状态报告成功", align=2, sort=80)
	public Long getReportSuccessCount() {
		return reportSuccessCount;
	}

	public void setReportSuccessCount(Long reportSuccessCount) {
		this.reportSuccessCount = reportSuccessCount;
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