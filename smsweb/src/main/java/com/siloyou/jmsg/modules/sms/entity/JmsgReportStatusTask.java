package com.siloyou.jmsg.modules.sms.entity;

import java.util.Date;

public class JmsgReportStatusTask {
	
	private String id;
	
	private String userId;
	
	private String userName;
	
	private String gatewayId;
	
	private Date dayQ;
	
	private Date dayZ;
	
	private int count;
	
	private String nullType;
	
	private String errorType;
	
	private String taskName;
	
	private String tableName;
	
	private String status;
	
	private int pageSize;
	private int pageNo;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	public int getPageNo() {
		return pageNo;
	}
	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getTaskName() {
		return taskName;
	}
	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}
	public String getGatewayId() {
		return gatewayId;
	}
	public void setGatewayId(String gatewayId) {
		this.gatewayId = gatewayId;
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
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public String getNullType() {
		return nullType;
	}
	public void setNullType(String nullType) {
		this.nullType = nullType;
	}
	public String getErrorType() {
		return errorType;
	}
	public void setErrorType(String errorType) {
		this.errorType = errorType;
	}
	
}


