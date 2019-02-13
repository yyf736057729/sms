/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.account.entity;

import com.siloyou.core.modules.sys.entity.Office;
import com.siloyou.core.modules.sys.entity.User;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

import com.siloyou.core.common.persistence.DataEntity;

/**
 * 资金账户信息Entity
 * @author zhukc
 * @version 2016-05-17
 */
public class JmsgAccount extends DataEntity<JmsgAccount> {
	
	private static final long serialVersionUID = 1L;
	private User user;				// 用户ID
	private String appType;			// 账号类型：短信sms、彩信mms、流量flow、话费telfree
	private Long money;				// 可用余额
	private Double moneyD;			// 充值金额
	private String remark;			// 备注
	private String payment;			//充值方式 :充值、返充、扣款 
	private String usedFlag;		//启用标识 1:启用 0:禁用
	private String payMode;			//扣费方式
	private String companyId;		//公司ID
	private String userId;			//用户ID
	private Office company;			//公司
	private String loginName;		//登录名
	
	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

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

	public String getUsedFlag() {
		return usedFlag;
	}

	public void setUsedFlag(String usedFlag) {
		this.usedFlag = usedFlag;
	}

	public String getPayMode() {
		return payMode;
	}

	public void setPayMode(String payMode) {
		this.payMode = payMode;
	}

	public String getPayment() {
		return payment;
	}

	public void setPayment(String payment) {
		this.payment = payment;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Double getMoneyD() {
		return moneyD;
	}

	public void setMoneyD(Double moneyD) {
		this.moneyD = moneyD;
	}

	public JmsgAccount() {
		super();
	}

	public JmsgAccount(String id){
		super(id);
	}

	@NotNull(message="用户ID不能为空")
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	@Length(min=1, max=10, message="账号类型：短信sms、彩信mms、流量flow、话费telfree长度必须介于 1 和 10 之间")
	public String getAppType() {
		return appType;
	}

	public void setAppType(String appType) {
		this.appType = appType;
	}
	
	public Long getMoney() {
		return money;
	}

	public void setMoney(Long money) {
		this.money = money;
	}
	
}