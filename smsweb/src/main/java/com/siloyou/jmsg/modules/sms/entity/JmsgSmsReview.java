/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.sms.entity;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.siloyou.core.common.persistence.DataEntity;
import com.siloyou.core.modules.sys.entity.User;

/**
 * 短信审核Entity
 * @author zhukc
 * @version 2016-07-16
 */
public class JmsgSmsReview extends DataEntity<JmsgSmsReview> {
	
	private static final long serialVersionUID = 1L;
	private String taskId;				// 任务ID
	private String customerOrderId;		// 客户订单号
	private User user;					// 用户ID
	private String smsContent;			// 短信内容
	private String reviewStatus;		// 审核状态 1：审核通过 0：审核未通过 9：待审核
	private String reviewContent;		// 审核意见
	private Date reviewDatetime;		// 审核时间
	private Date createDatetime;		// 创建时间
	
	public JmsgSmsReview() {
		super();
	}

	public JmsgSmsReview(String id){
		super(id);
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	
	public String getCustomerOrderId() {
		return customerOrderId;
	}

	public void setCustomerOrderId(String customerOrderId) {
		this.customerOrderId = customerOrderId;
	}
	
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	public String getSmsContent() {
		return smsContent;
	}

	public void setSmsContent(String smsContent) {
		this.smsContent = smsContent;
	}
	
	public String getReviewStatus() {
		return reviewStatus;
	}

	public void setReviewStatus(String reviewStatus) {
		this.reviewStatus = reviewStatus;
	}
	
	public String getReviewContent() {
		return reviewContent;
	}

	public void setReviewContent(String reviewContent) {
		this.reviewContent = reviewContent;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getReviewDatetime() {
		return reviewDatetime;
	}

	public void setReviewDatetime(Date reviewDatetime) {
		this.reviewDatetime = reviewDatetime;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getCreateDatetime() {
		return createDatetime;
	}

	public void setCreateDatetime(Date createDatetime) {
		this.createDatetime = createDatetime;
	}
	
}