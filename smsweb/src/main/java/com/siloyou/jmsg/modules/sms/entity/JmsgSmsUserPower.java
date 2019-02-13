/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.sms.entity;

import com.siloyou.core.modules.sys.entity.User;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

import com.siloyou.core.common.persistence.DataEntity;

/**
 * 用户短信能力Entity
 * @author zhukc
 * @version 2016-05-18
 */
public class JmsgSmsUserPower extends DataEntity<JmsgSmsUserPower> {
	
	private static final long serialVersionUID = 1L;
	private User user;		// 用户ID
	private String phoneType;		// 运营商
	private String areaCode;		// 省份
	private String channelCode;		// 通道代码
	private Integer level;		// 优先级
	
	public JmsgSmsUserPower() {
		super();
	}

	public JmsgSmsUserPower(String id){
		super(id);
	}

	@NotNull(message="用户ID不能为空")
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	@Length(min=1, max=10, message="运营商长度必须介于 1 和 10 之间")
	public String getPhoneType() {
		return phoneType;
	}

	public void setPhoneType(String phoneType) {
		this.phoneType = phoneType;
	}
	
	@Length(min=1, max=10, message="省份长度必须介于 1 和 10 之间")
	public String getAreaCode() {
		return areaCode;
	}

	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}
	
	@Length(min=1, max=64, message="通道代码长度必须介于 1 和 64 之间")
	public String getChannelCode() {
		return channelCode;
	}

	public void setChannelCode(String channelCode) {
		this.channelCode = channelCode;
	}
	
	@NotNull(message="优先级不能为空")
	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}
	
}