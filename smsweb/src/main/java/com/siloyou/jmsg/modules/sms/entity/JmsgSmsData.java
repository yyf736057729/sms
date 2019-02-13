/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.sms.entity;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.siloyou.core.common.persistence.DataEntity;
import com.siloyou.core.modules.sys.entity.User;

/**
 * 短信素材Entity
 * @author zhukc
 * @version 2016-07-18
 */
public class JmsgSmsData extends DataEntity<JmsgSmsData> {
	
	private static final long serialVersionUID = 1L;
	private String content;				// 内容
	private String type;				// 内容类型  mms彩信 sms短信
	private User user;					// 用户ID
	private String contentKey;			// 内容指纹
	private Date createDatetime;		// 创建时间
	private Date createDatetimeQ;		// 创建时间起
	private Date createDatetimeZ;		// 创建时间止
	private String reviewStatus;		// 审核状态 1:审核通过 0:审核不通过 9:待审核
	private Date reviewDatetime;		// 审核时间
	private String reviewContent;		// 审核意见
	private String reviewUserId;		// 审核用户
	private String yxbz;				// 有效标志
	private String templateFlag;		// 模板标识
	
	private String companyId;			//公司ID
	private String reviewUserName;		//审核人
	
	public String getReviewContent() {
		return reviewContent;
	}

	public void setReviewContent(String reviewContent) {
		this.reviewContent = reviewContent;
	}

	public String getReviewUserName() {
		return reviewUserName;
	}

	public void setReviewUserName(String reviewUserName) {
		this.reviewUserName = reviewUserName;
	}

	public String getCompanyId() {
		return companyId;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
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

	public JmsgSmsData() {
		super();
	}

	public JmsgSmsData(String id){
		super(id);
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	public String getContentKey() {
		return contentKey;
	}

	public void setContentKey(String contentKey) {
		this.contentKey = contentKey;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getCreateDatetime() {
		return createDatetime;
	}

	public void setCreateDatetime(Date createDatetime) {
		this.createDatetime = createDatetime;
	}
	
	public String getReviewStatus() {
		return reviewStatus;
	}

	public void setReviewStatus(String reviewStatus) {
		this.reviewStatus = reviewStatus;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getReviewDatetime() {
		return reviewDatetime;
	}

	public void setReviewDatetime(Date reviewDatetime) {
		this.reviewDatetime = reviewDatetime;
	}
	
	public String getReviewUserId() {
		return reviewUserId;
	}

	public void setReviewUserId(String reviewUserId) {
		this.reviewUserId = reviewUserId;
	}
	
	public String getYxbz() {
		return yxbz;
	}

	public void setYxbz(String yxbz) {
		this.yxbz = yxbz;
	}
	
	public String getTemplateFlag() {
		return templateFlag;
	}

	public void setTemplateFlag(String templateFlag) {
		this.templateFlag = templateFlag;
	}
	
}