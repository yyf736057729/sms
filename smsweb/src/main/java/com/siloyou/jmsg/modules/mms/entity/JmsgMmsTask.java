/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.mms.entity;

import java.util.Date;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.siloyou.core.common.persistence.DataEntity;
import com.siloyou.core.modules.sys.entity.Office;
import com.siloyou.core.modules.sys.entity.User;

/**
 * 彩信发送管理Entity
 * @author zhukc
 * @version 2016-05-20
 */
public class JmsgMmsTask extends DataEntity<JmsgMmsTask> {
	
	private static final long serialVersionUID = 1L;
	private String mmsId;				// 素材ID
	private String mmsTitle;			// 名称(标题)
	private String mmsUrl;				// 彩信地址
	private int sendCount;				// 发送数量
	private Date sendDatetime;			// 发送时间
	private Date sendDatetimeQ;			// 发送时间
	private Date sendDatetimeZ;			// 发送时间
	private Date endDatetime;			// 结束时间
	private String status;				// 状态：待发送1，发送中2，发送完成3，暂停5,停止9
	private Date createDatetime;		// 创建时间
	private String createUserId;		// 创建人
	private Date updateDatetime;		// update_datetime
	private String updateUserId;		// update_by
	private String phone;				//手机号码
	private User user;
	private int mmsSize;				//彩信大小
	private String phonelist;			//号码列表
	private String countDetail;			//总数明细(移动|联通|电信)
	private byte[] mmsBody;				//彩信内容
	private String companyId;			//公司ID
	private Office company;				//公司
	private Date tongjiDatetime;		//统计时间
	private Date tongjiDatetimeQ;		//统计时间起
	private Date tongjiDatetimeZ;		//统计时间止
	private int successCount;			//成功量
	private String backStatus;			//返充状态
	private int backCount;				//返充条数
	private Date backDatetime;			//返充时间
	private String payMode;				//扣款方式
	
	public String getPayMode() {
		return payMode;
	}

	public void setPayMode(String payMode) {
		this.payMode = payMode;
	}

	public String getBackStatus() {
		return backStatus;
	}

	public void setBackStatus(String backStatus) {
		this.backStatus = backStatus;
	}

	public int getBackCount() {
		return backCount;
	}

	public void setBackCount(int backCount) {
		this.backCount = backCount;
	}

	public Date getBackDatetime() {
		return backDatetime;
	}

	public void setBackDatetime(Date backDatetime) {
		this.backDatetime = backDatetime;
	}

	public Office getCompany() {
		return company;
	}

	public void setCompany(Office company) {
		this.company = company;
	}

	public Date getTongjiDatetime() {
		return tongjiDatetime;
	}

	public void setTongjiDatetime(Date tongjiDatetime) {
		this.tongjiDatetime = tongjiDatetime;
	}

	public Date getTongjiDatetimeQ() {
		return tongjiDatetimeQ;
	}

	public void setTongjiDatetimeQ(Date tongjiDatetimeQ) {
		this.tongjiDatetimeQ = tongjiDatetimeQ;
	}

	public Date getTongjiDatetimeZ() {
		return tongjiDatetimeZ;
	}

	public void setTongjiDatetimeZ(Date tongjiDatetimeZ) {
		this.tongjiDatetimeZ = tongjiDatetimeZ;
	}

	public int getSuccessCount() {
		return successCount;
	}

	public void setSuccessCount(int successCount) {
		this.successCount = successCount;
	}

	public String getCompanyId() {
		return companyId;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	public byte[] getMmsBody() {
		return mmsBody;
	}

	public void setMmsBody(byte[] mmsBody) {
		this.mmsBody = mmsBody;
	}

	public String getPhonelist() {
		return phonelist;
	}

	public void setPhonelist(String phonelist) {
		this.phonelist = phonelist;
	}

	public String getCountDetail() {
		return countDetail;
	}

	public void setCountDetail(String countDetail) {
		this.countDetail = countDetail;
	}

	public int getMmsSize() {
		return mmsSize;
	}

	public void setMmsSize(int mmsSize) {
		this.mmsSize = mmsSize;
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

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public JmsgMmsTask() {
		super();
	}

	public JmsgMmsTask(String id){
		super(id);
	}

	@Length(min=1, max=11, message="素材ID长度必须介于 1 和 11 之间")
	public String getMmsId() {
		return mmsId;
	}

	public void setMmsId(String mmsId) {
		this.mmsId = mmsId;
	}
	
	@Length(min=1, max=200, message="名称(标题)长度必须介于 1 和 200 之间")
	public String getMmsTitle() {
		return mmsTitle;
	}

	public void setMmsTitle(String mmsTitle) {
		this.mmsTitle = mmsTitle;
	}
	
	@Length(min=0, max=100, message="彩信地址长度必须介于 0 和 100 之间")
	public String getMmsUrl() {
		return mmsUrl;
	}

	public void setMmsUrl(String mmsUrl) {
		this.mmsUrl = mmsUrl;
	}
	
	@Length(min=1, max=11, message="发送数量长度必须介于 1 和 11 之间")
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
	
	@Length(min=1, max=10, message="状态：待发送，发送中，发送完成，暂停长度必须介于 1 和 10 之间")
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getCreateDatetime() {
		return createDatetime;
	}

	public void setCreateDatetime(Date createDatetime) {
		this.createDatetime = createDatetime;
	}
	
	@Length(min=1, max=64, message="创建人长度必须介于 1 和 64 之间")
	public String getCreateUserId() {
		return createUserId;
	}

	public void setCreateUserId(String createUserId) {
		this.createUserId = createUserId;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getUpdateDatetime() {
		return updateDatetime;
	}

	public void setUpdateDatetime(Date updateDatetime) {
		this.updateDatetime = updateDatetime;
	}
	
	@Length(min=0, max=64, message="update_by长度必须介于 0 和 64 之间")
	public String getUpdateUserId() {
		return updateUserId;
	}

	public void setUpdateUserId(String updateUserId) {
		this.updateUserId = updateUserId;
	}
	
}