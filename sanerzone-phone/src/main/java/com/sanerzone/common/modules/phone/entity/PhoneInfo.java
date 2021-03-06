/**
 * Copyright &copy; 2015-2016 SanerZone All rights reserved.
 */
package com.sanerzone.common.modules.phone.entity;

import com.sanerzone.common.support.persistence.DataEntity;

/**
 * 分流号段Entity
 * @author zhukc
 * @version 2017-03-10
 */
public class PhoneInfo extends DataEntity<PhoneInfo> {
	
	private static final long serialVersionUID = 1L;
	private String phone;				// 号段
	private String phoneType;			// 运营商
	private String phoneProv;			// 归属省
	private String phoneCity;			// 归属市
	private String phoneCityCode;		// 省市代码
	private String zip;					// 邮编
	private String types;				// 类型
	private String areaCode;			// area_code
	
	public PhoneInfo() {
		super();
	}

	public PhoneInfo(String id){
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
	
	public String getAreaCode() {
		return areaCode;
	}

	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}
	
}