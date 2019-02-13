/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.sms.entity;

import com.siloyou.core.modules.sys.entity.User;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

import com.siloyou.core.common.persistence.DataEntity;

/**
 * 用户短信属性Entity
 * @author zhukc
 * @version 2016-05-18
 */
public class JmsgSmsUserAttr extends DataEntity<JmsgSmsUserAttr> {
	
	private static final long serialVersionUID = 1L;
	private User user;		// 用户ID
	private String userStatus;		// 用户状态 1：启用 0：禁用
	private String apisecret;		// apisecret
	private String signCheck;		// 签名校验（1是，如果签名为空则只校验是否存在 0否）
	private String smsSign;		// 短信签名（多个以逗号,隔开）
	private String autIp;		// 鉴权IP
	private Integer checkCount;		// 审核条数（0不审核 &gt;0审核）
	private String globalBlacklist;		// 启用全局黑名单（1是0否）
	private String globalFilter;		// 启用全局过滤词（1是 0否）
	private String repeatFilter;		// 重号过滤
	private String upsideAddr;		// 上行推送地址
	private String reportAddr;		// 状态报告推送地址
	private Date createDatetime;		// 创建日期
	
	public JmsgSmsUserAttr() {
		super();
	}

	public JmsgSmsUserAttr(String id){
		super(id);
	}

	@NotNull(message="用户ID不能为空")
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	@Length(min=1, max=1, message="用户状态 1：启用 0：禁用长度必须介于 1 和 1 之间")
	public String getUserStatus() {
		return userStatus;
	}

	public void setUserStatus(String userStatus) {
		this.userStatus = userStatus;
	}
	
	@Length(min=1, max=100, message="apisecret长度必须介于 1 和 100 之间")
	public String getApisecret() {
		return apisecret;
	}

	public void setApisecret(String apisecret) {
		this.apisecret = apisecret;
	}
	
	@Length(min=1, max=1, message="签名校验（1是，如果签名为空则只校验是否存在 0否）长度必须介于 1 和 1 之间")
	public String getSignCheck() {
		return signCheck;
	}

	public void setSignCheck(String signCheck) {
		this.signCheck = signCheck;
	}
	
	@Length(min=0, max=1000, message="短信签名（多个以逗号,隔开）长度必须介于 0 和 1000 之间")
	public String getSmsSign() {
		return smsSign;
	}

	public void setSmsSign(String smsSign) {
		this.smsSign = smsSign;
	}
	
	@Length(min=0, max=20, message="鉴权IP长度必须介于 0 和 20 之间")
	public String getAutIp() {
		return autIp;
	}

	public void setAutIp(String autIp) {
		this.autIp = autIp;
	}
	
	@NotNull(message="审核条数（0不审核 &gt;0审核）不能为空")
	public Integer getCheckCount() {
		return checkCount;
	}

	public void setCheckCount(Integer checkCount) {
		this.checkCount = checkCount;
	}
	
	@Length(min=1, max=1, message="启用全局黑名单（1是0否）长度必须介于 1 和 1 之间")
	public String getGlobalBlacklist() {
		return globalBlacklist;
	}

	public void setGlobalBlacklist(String globalBlacklist) {
		this.globalBlacklist = globalBlacklist;
	}
	
	@Length(min=1, max=1, message="启用全局过滤词（1是 0否）长度必须介于 1 和 1 之间")
	public String getGlobalFilter() {
		return globalFilter;
	}

	public void setGlobalFilter(String globalFilter) {
		this.globalFilter = globalFilter;
	}
	
	@Length(min=1, max=1, message="重号过滤长度必须介于 1 和 1 之间")
	public String getRepeatFilter() {
		return repeatFilter;
	}

	public void setRepeatFilter(String repeatFilter) {
		this.repeatFilter = repeatFilter;
	}
	
	@Length(min=0, max=100, message="上行推送地址长度必须介于 0 和 100 之间")
	public String getUpsideAddr() {
		return upsideAddr;
	}

	public void setUpsideAddr(String upsideAddr) {
		this.upsideAddr = upsideAddr;
	}
	
	@Length(min=0, max=100, message="状态报告推送地址长度必须介于 0 和 100 之间")
	public String getReportAddr() {
		return reportAddr;
	}

	public void setReportAddr(String reportAddr) {
		this.reportAddr = reportAddr;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getCreateDatetime() {
		return createDatetime;
	}

	public void setCreateDatetime(Date createDatetime) {
		this.createDatetime = createDatetime;
	}
	
}