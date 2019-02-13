/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.biz.entity;
import java.util.Date;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.siloyou.core.common.persistence.DataEntity;
import com.siloyou.core.common.utils.excel.annotation.ExcelField;

/**
 * 活动登录短信发送记录Entity
 * @author huangjie
 * @version 2017-07-20
 */
public class BizRegistryData extends DataEntity<BizRegistryData> {
	
	private static final long serialVersionUID = 1L;
	private String phone;		// 客户手机号码
	private String verificationCode;		// 验证码
	private Date createTime;		// 创建时间
	private Date createTimeQ;		// 开始 创建时间
	private Date createTimeZ;		// 结束 创建时间
	
	public Date getCreateTimeQ() {
		return createTimeQ;
	}

	public void setCreateTimeQ(Date createTimeQ) {
		this.createTimeQ = createTimeQ;
	}

	public Date getCreateTimeZ() {
		return createTimeZ;
	}

	public void setCreateTimeZ(Date createTimeZ) {
		this.createTimeZ = createTimeZ;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public BizRegistryData() {
		super();
	}

	public BizRegistryData(String id){
		super(id);
	}

	@Length(min=1, max=32, message="客户手机号码长度必须介于 1 和 32 之间")
	@ExcelField(title="用户手机号码", align=2, sort=40)
	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	@Length(min=1, max=45, message="验证码长度必须介于 1 和 45 之间")
	@ExcelField(title="邀请码", align=2, sort=20)
	public String getVerificationCode() {
		return verificationCode;
	}

	public void setVerificationCode(String verificationCode) {
		this.verificationCode = verificationCode;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@NotNull(message="创建时间不能为空")
	public Date getCreateTime() {
		return createTime;
	}

	

	@ExcelField(title="ID", type=1, align=2, sort=1)
	public String getId() {
		
		return super.getId();
	}
		
}