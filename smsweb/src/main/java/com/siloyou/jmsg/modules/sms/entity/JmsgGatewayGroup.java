/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.sms.entity;

import com.siloyou.core.common.persistence.DataEntity;

/**
 * 通道分组Entity
 * @author zhukc
 * @version 2016-07-29
 */
public class JmsgGatewayGroup extends DataEntity<JmsgGatewayGroup> {
	
	private static final long serialVersionUID = 1L;
	private String groupId;			// 分组ID
	private String groupName;		// 分组名称
	private String phoneType;		// 运营商(联通、移动、电信)
	private String provinceId;		// 省份
	private String provinceName;	// 省份
	private String gatewayId;		// 通道ID
	private String gatewayName;		// 通道名称
	private String level;			// 等级
	private String spNumber;		// 接入号
	
	public String getSpNumber() {
		return spNumber;
	}

	public void setSpNumber(String spNumber) {
		this.spNumber = spNumber;
	}

	public String getGatewayName() {
		return gatewayName;
	}

	public void setGatewayName(String gatewayName) {
		this.gatewayName = gatewayName;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getProvinceName() {
		return provinceName;
	}

	public void setProvinceName(String provinceName) {
		this.provinceName = provinceName;
	}

	public JmsgGatewayGroup() {
		super();
	}

	public JmsgGatewayGroup(String id){
		super(id);
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
	
	public String getGatewayId() {
		return gatewayId;
	}

	public void setGatewayId(String gatewayId) {
		this.gatewayId = gatewayId;
	}
	
	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}
	
}