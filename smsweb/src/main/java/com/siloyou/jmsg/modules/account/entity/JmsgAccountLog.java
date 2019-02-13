/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.account.entity;

import com.siloyou.core.modules.sys.entity.Office;
import com.siloyou.core.modules.sys.entity.User;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

import com.siloyou.core.common.persistence.DataEntity;

/**
 * 资金变动日志Entity
 * @author zhukc
 * @version 2016-05-17
 */
public class JmsgAccountLog extends DataEntity<JmsgAccountLog> {
	
	private static final long serialVersionUID = 1L;
	private User user;				// 用户ID
	private Office company;			// 公司
	private String companyId;		// 公司ID
	private String changeType;		// 资金变动类型:消费:XF开头、充值：CZ开头
	private Long money;				// 变动金额
	private Date changeDate;		// 变动时间
	private Date changeDateQ;		// 变动时间起
	private Date changeDateZ;		// 变动时间至
	private String appType;			// 账户类型
	private String orderId;			// 关联订单号
	private String ext1;			// 扩展字段1
	private String ext2;			// 扩展字段2
	private String remark;			// 备注
	private String userId;			//用户ID
	
	public Office getCompany() {
		return company;
	}

	public void setCompany(Office company) {
		this.company = company;
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

	public Date getChangeDateQ() {
		return changeDateQ;
	}

	public void setChangeDateQ(Date changeDateQ) {
		this.changeDateQ = changeDateQ;
	}

	public Date getChangeDateZ() {
		return changeDateZ;
	}

	public void setChangeDateZ(Date changeDateZ) {
		this.changeDateZ = changeDateZ;
	}

	public JmsgAccountLog() {
		super();
	}

	public JmsgAccountLog(String id){
		super(id);
	}

	@NotNull(message="用户ID不能为空")
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	@Length(min=1, max=10, message="资金变动类型:消费:XF开头、充值：CZ开头长度必须介于 1 和 10 之间")
	public String getChangeType() {
		return changeType;
	}

	public void setChangeType(String changeType) {
		this.changeType = changeType;
	}
	
	@NotNull(message="变动金额不能为空")
	public Long getMoney() {
		return money;
	}

	public void setMoney(Long money) {
		this.money = money;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@NotNull(message="变动时间不能为空")
	public Date getChangeDate() {
		return changeDate;
	}

	public void setChangeDate(Date changeDate) {
		this.changeDate = changeDate;
	}
	
	@Length(min=1, max=10, message="账户类型长度必须介于 1 和 10 之间")
	public String getAppType() {
		return appType;
	}

	public void setAppType(String appType) {
		this.appType = appType;
	}
	
	@Length(min=0, max=64, message="关联订单号长度必须介于 0 和 64 之间")
	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	
	@Length(min=0, max=64, message="扩展字段1长度必须介于 0 和 64 之间")
	public String getExt1() {
		return ext1;
	}

	public void setExt1(String ext1) {
		this.ext1 = ext1;
	}
	
	@Length(min=0, max=64, message="扩展字段2长度必须介于 0 和 64 之间")
	public String getExt2() {
		return ext2;
	}

	public void setExt2(String ext2) {
		this.ext2 = ext2;
	}
	
	@Length(min=0, max=256, message="备注长度必须介于 0 和 256 之间")
	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
	
}