/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.sms.entity;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.siloyou.core.common.persistence.DataEntity;
import com.siloyou.core.common.utils.excel.annotation.ExcelField;

/**
 * 联系人列表Entity
 * @author zhukc
 * @version 2017-04-01
 */
public class JmsgAddresslistInfo extends DataEntity<JmsgAddresslistInfo> {
	
	private static final long serialVersionUID = 1L;
	private String groupId;				// 分组ID
	private String phone;				// 手机号码
	private String contacts;			// 联系人
	private String email;				// 邮箱
	private String birthday;			// 生日
	private Date createtime;			// 创建时间
	private Date createtimeQ;			// 创建时间
	private Date createtimeZ;			// 创建时间
	private String userId;				// 用户ID
	private String companyId;			// 公司ID
	private String remarks;				// 备注
	
	
	@ExcelField(title="备注", align=2, sort=60)
	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getCompanyId() {
		return companyId;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	public Date getCreatetimeQ() {
		return createtimeQ;
	}

	public void setCreatetimeQ(Date createtimeQ) {
		this.createtimeQ = createtimeQ;
	}

	public Date getCreatetimeZ() {
		return createtimeZ;
	}

	public void setCreatetimeZ(Date createtimeZ) {
		this.createtimeZ = createtimeZ;
	}

	private JmsgAddresslistGroup group;
	
	public JmsgAddresslistGroup getGroup() {
		return group;
	}

	public void setGroup(JmsgAddresslistGroup group) {
		this.group = group;
	}

	public JmsgAddresslistInfo() {
		super();
	}

	public JmsgAddresslistInfo(String id){
		super(id);
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	
	@ExcelField(title="手机号码", align=2, sort=30)
	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	@ExcelField(title="联系人姓名", align=2, sort=20)
	public String getContacts() {
		return contacts;
	}

	public void setContacts(String contacts) {
		this.contacts = contacts;
	}
	
	@ExcelField(title="邮箱", align=2, sort=40)
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd")
	@ExcelField(title="出生日期", align=2, sort=50)
	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getCreatetime() {
		return createtime;
	}

	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}
	
}