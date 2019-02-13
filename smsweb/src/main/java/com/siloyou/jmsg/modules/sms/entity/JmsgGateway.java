/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.sms.entity;

import com.siloyou.core.common.persistence.DataEntity;

/**
 * 通道Entity
 * @author zhukc
 * @version 2016-07-29
 */
public class JmsgGateway extends DataEntity<JmsgGateway> {
	
	private static final long serialVersionUID = 1L;
	private String gatewayId;				// 网关ID
	private String groupId;					// 分组ID
	private String phoneType;				// 运营商
	private String provinceId;				// 省份
	private int level;						// 优先级
	private String sign;					// 签名
	private String spNumber;				// 接入号
	
	public JmsgGateway() {
		super();
	}

	public String getGatewayId() {
		return gatewayId;
	}

	public void setGatewayId(String gatewayId) {
		this.gatewayId = gatewayId;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getPhoneType() {
		return phoneType;
	}

	public void setPhoneType(String phoneType) {
		this.phoneType = phoneType;
	}

	public String getProvinceId() {
		return provinceId;
	}

	public void setProvinceId(String provinceId) {
		this.provinceId = provinceId;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public String getSpNumber() {
		return spNumber;
	}

	public void setSpNumber(String spNumber) {
		this.spNumber = spNumber;
	}

	
	
}