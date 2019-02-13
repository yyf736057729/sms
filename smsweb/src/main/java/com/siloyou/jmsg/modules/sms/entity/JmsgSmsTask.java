/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.sms.entity;

import java.util.Date;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.siloyou.core.common.persistence.DataEntity;
import com.siloyou.core.modules.sys.entity.Dict;
import com.siloyou.core.modules.sys.entity.User;

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
	private String status;			// 状态：-2审核不通过 -1:审核中  1:待发送,2:发送中,3:发送完成,5:暂停,8:继续发送,9:停止发送
	private String countDetail;		// 总数明细（移动|联通|电信 数量）
	private Date createDatetime;	// 创建时间
	private Date createDatetimeQ;
	private Date createDatetimeZ;
	private Date updateDatetime;	// 操作时间
	private String phones;			// 发送号码
	private String content;			// 短信发送内容
	private User user;
	private String companyId;		//公司ID
	private String createUserId;	//创建人ID
	private String tableName;
	private String reviewUserId;	//审核人
	private Date reviewTime;		//审核时间
	private String reviewRemarks;	//审核说明
	
	private String sign;
	private List<Dict> userSign;
	private List<Dict> unsubType;
	private String taskType;		//任务类型 0:普通 1:点对点 2:模板 3:批量		
	
	private int rowNumber;
	private int version;
	private String userIdText;			// 用户ID

	public String getTaskType() {
		return taskType;
	}

	public void setTaskType(String taskType) {
		this.taskType = taskType;
	}

	public Date getCreateDatetimeQ() {
		return createDatetimeQ;
	}

	public void setCreateDatetimeQ(Date createDatetimeQ) {
		this.createDatetimeQ = createDatetimeQ;
	}

	public Date getCreateDatetimeZ() {
		return createDatetimeZ;
	}

	public void setCreateDatetimeZ(Date createDatetimeZ) {
		this.createDatetimeZ = createDatetimeZ;
	}

	public String getReviewUserId() {
		return reviewUserId;
	}

	public void setReviewUserId(String reviewUserId) {
		this.reviewUserId = reviewUserId;
	}

	public Date getReviewTime() {
		return reviewTime;
	}

	public void setReviewTime(Date reviewTime) {
		this.reviewTime = reviewTime;
	}

	public String getReviewRemarks() {
		return reviewRemarks;
	}

	public void setReviewRemarks(String reviewRemarks) {
		this.reviewRemarks = reviewRemarks;
	}

	public int getRowNumber() {
		return rowNumber;
	}

	public void setRowNumber(int rowNumber) {
		this.rowNumber = rowNumber;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public List<Dict> getUserSign() {
		return userSign;
	}

	public void setUserSign(List<Dict> userSign) {
		this.userSign = userSign;
	}

	public List<Dict> getUnsubType() {
		return unsubType;
	}

	public void setUnsubType(List<Dict> unsubType) {
		this.unsubType = unsubType;
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
	
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
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

	public String getUserIdText() {
		return userIdText;
	}

	public void setUserIdText(String userIdText) {
		this.userIdText = userIdText;
	}
}