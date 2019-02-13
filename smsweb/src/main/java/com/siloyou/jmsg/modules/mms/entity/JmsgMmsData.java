/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.mms.entity;

import java.util.Date;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.siloyou.core.common.persistence.DataEntity;
import com.siloyou.core.modules.sys.entity.User;

/**
 * 彩信素材Entity
 * @author zhukc
 * @version 2016-05-20
 */
public class JmsgMmsData extends DataEntity<JmsgMmsData> {
	
	private static final long serialVersionUID = 1L;
	private String mmsTitle;			// 名称(标题)
	private User user;
	private String content;				// 内容
	private String mmsCode;				// 彩信编码
	private String checkStatus;			// 审核状态
	private Date createDatetime;		// 创建时间
	private Date createDatetimeQ;		// 创建时间
	private Date createDatetimeZ;		// 创建时间
	private Date updateDatetime;		// 修改时间
	private String viewFlag;			//查看标识 1:只读
	private String useFlag;				//使用标识
	private String checkContent;		//审核意见
	private Date checkDatetime;			//审核时间
	private String checkUserId;			//审核人
	private String checkUserName;		//审核人名称
	private String remark;				//备注
	private String companyId;			//公司ID
	private String userName;
	
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getCheckUserName() {
		return checkUserName;
	}

	public void setCheckUserName(String checkUserName) {
		this.checkUserName = checkUserName;
	}

	public String getCompanyId() {
		return companyId;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getCheckUserId() {
		return checkUserId;
	}

	public void setCheckUserId(String checkUserId) {
		this.checkUserId = checkUserId;
	}

	public String getUseFlag() {
		return useFlag;
	}

	public void setUseFlag(String useFlag) {
		this.useFlag = useFlag;
	}

	public String getCheckContent() {
		return checkContent;
	}

	public void setCheckContent(String checkContent) {
		this.checkContent = checkContent;
	}

	public Date getCheckDatetime() {
		return checkDatetime;
	}

	public void setCheckDatetime(Date checkDatetime) {
		this.checkDatetime = checkDatetime;
	}

	public String getViewFlag() {
		return viewFlag;
	}

	public void setViewFlag(String viewFlag) {
		this.viewFlag = viewFlag;
	}

	public JmsgMmsData() {
		super();
	}

	public User getUser() {
		return user;
	}



	public void setUser(User user) {
		this.user = user;
	}



	public JmsgMmsData(String id){
		super(id);
	}

	@Length(min=1, max=200, message="名称(标题)长度必须介于 1 和 200 之间")
	public String getMmsTitle() {
		return mmsTitle;
	}

	public void setMmsTitle(String mmsTitle) {
		this.mmsTitle = mmsTitle;
	}
	
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	@Length(min=1, max=6, message="彩信编码长度必须介于 1 和 6 之间")
	public String getMmsCode() {
		return mmsCode;
	}

	public void setMmsCode(String mmsCode) {
		this.mmsCode = mmsCode;
	}
	
	@Length(min=1, max=2, message="审核状态长度必须介于 1 和 2 之间")
	public String getCheckStatus() {
		return checkStatus;
	}

	public void setCheckStatus(String checkStatus) {
		this.checkStatus = checkStatus;
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
	
	
	
}