/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.sanerzone.common.modules.phone.entity;

import com.sanerzone.common.support.persistence.DataEntity;

/**
 * 号段管理Entity
 * @author zhukc
 * @version 2016-07-30
 */
public class JmsgPhoneInfo extends DataEntity<JmsgPhoneInfo> {
	
	private static final long serialVersionUID = 1L;
	private String phone;				// 号码前七位
	private String phoneType;			// 运营商
	private String phoneProv;			// 归属省
	private String phoneCity;			// 归属市
	private String phoneCityCode;		// 省市代码
	private String zip;					// 邮编
	private String types;				// 类型
	
	public JmsgPhoneInfo() {
		super();
	}

	public JmsgPhoneInfo(String id){
		super(id);
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	public String getPhoneType() {
		return phoneType;
	}

	public void setPhoneType(String phoneType) {
		this.phoneType = phoneType;
	}
	
	public String getPhoneProv() {
		return phoneProv;
	}

	public void setPhoneProv(String phoneProv) {
		this.phoneProv = phoneProv;
	}
	
	public String getPhoneCity() {
		return phoneCity;
	}

	public void setPhoneCity(String phoneCity) {
		this.phoneCity = phoneCity;
	}
	
	public String getPhoneCityCode() {
		return phoneCityCode;
	}

	public void setPhoneCityCode(String phoneCityCode) {
		this.phoneCityCode = phoneCityCode;
	}
	
	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}
	
	public String getTypes() {
		return types;
	}

	public void setTypes(String types) {
		this.types = types;
	}
	
}