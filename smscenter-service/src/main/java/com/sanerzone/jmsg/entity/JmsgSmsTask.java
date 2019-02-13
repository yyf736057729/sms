/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.sanerzone.jmsg.entity;

import java.util.Date;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sanerzone.common.support.persistence.DataEntity;

/**
 * 短信任务发送Entity
 * @author zhukc
 * @version 2016-07-20
 */
public class JmsgSmsTask extends DataEntity<JmsgSmsTask> {
	
	private static final long serialVersionUID = 1L;
	private String dataId;			// 素材ID
	private String smsContent;		// 短信内容
	private int sendCount;			// 发送数量
	private int successCount;		// 成功量
	private int failCount;			// 失败量
	private Date sendDatetime;		// 发送时间
	private Date sendDatetimeQ;		// 发送时间
	private Date sendDatetimeZ;		// 发送时间
	private Date endDatetime;		// 结束时间
	private String status;			// 状态：-1:审核中  1:待发送,2:发送中,3:发送完成,5:暂停,8:继续发送,9:停止发送
	private String countDetail;		// 总数明细（移动|联通|电信 数量）
	private Date createDatetime;	// 创建时间
	private Date updateDatetime;	// 操作时间
	private String phones;			// 发送号码
	private String content;			// 短信发送内容
	private String userId;
	private String companyId;		//公司ID
	private String createUserId;	//创建人ID
	private String tableName;
	
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

	public int getSuccessCount() {
		return successCount;
	}

	public void setSuccessCount(int successCount) {
		this.successCount = successCount;
	}

	public int getFailCount() {
		return failCount;
	}

	public void setFailCount(int failCount) {
		this.failCount = failCount;
	}

	public String getSmsContent() {
		return smsContent;
	}

	public void setSmsContent(String smsContent) {
		this.smsContent = smsContent;
	}

	public String getCompanyId() {
		return companyId;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	public String getCreateUserId() {
		return createUserId;
	}

	public void setCreateUserId(String createUserId) {
		this.createUserId = createUserId;
	}

	public Date getSendDatetimeQ() {
		return sendDatetimeQ;
	}

	public void setSendDatetimeQ(Date sendDatetimeQ) {
		this.sendDatetimeQ = sendDatetimeQ;
	}

	public Date getSendDatetimeZ() {
		return sendDatetimeZ;
	}

	public void setSendDatetimeZ(Date sendDatetimeZ) {
		this.sendDatetimeZ = sendDatetimeZ;
	}

	private Set<String> phoneList;	//发送号码列表
	
	public Set<String> getPhoneList() {
		return phoneList;
	}

	public void setPhoneList(Set<String> phoneList) {
		this.phoneList = phoneList;
	}
	
	public JmsgSmsTask() {
		super();
	}

	public String getPhones() {
		return phones;
	}

	public void setPhones(String phones) {
		this.phones = phones;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public JmsgSmsTask(String id){
		super(id);
	}

	public String getDataId() {
		return dataId;
	}

	public void setDataId(String dataId) {
		this.dataId = dataId;
	}
	
	public int getSendCount() {
		return sendCount;
	}

	public void setSendCount(int sendCount) {
		this.sendCount = sendCount;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getSendDatetime() {
		return sendDatetime;
	}

	public void setSendDatetime(Date sendDatetime) {
		this.sendDatetime = sendDatetime;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getEndDatetime() {
		return endDatetime;
	}

	public void setEndDatetime(Date endDatetime) {
		this.endDatetime = endDatetime;
	}
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getCountDetail() {
		return countDetail;
	}

	public void setCountDetail(String countDetail) {
		this.countDetail = countDetail;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getCreateDatetime() {
		return createDatetime;
	}

	public void setCreateDatetime(Date createDatetime) {
		this.createDatetime = createDatetime;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getUpdateDatetime() {
		return updateDatetime;
	}

	public void setUpdateDatetime(Date updateDatetime) {
		this.updateDatetime = updateDatetime;
	}
	
}