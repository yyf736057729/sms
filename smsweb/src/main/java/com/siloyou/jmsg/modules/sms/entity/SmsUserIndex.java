package com.siloyou.jmsg.modules.sms.entity;

import java.io.Serializable;

public class SmsUserIndex implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private String day;//日期
	private String phoneType;//运营商
	
	private int count;//总量(网关成功量)
	private int successCount;//成功量(状态报告成功量)
	
	private String countArray;//总量数组
	private String successArray;//成功量数组

	private long ydCount;
	private long ltCount;
	private long dxCount;
	
	private String ydCountArray;
	private String ltCountArray;
	private String dxCountArray;
	
	public String getYdCountArray() {
		return ydCountArray;
	}
	public void setYdCountArray(String ydCountArray) {
		this.ydCountArray = ydCountArray;
	}
	public String getLtCountArray() {
		return ltCountArray;
	}
	public void setLtCountArray(String ltCountArray) {
		this.ltCountArray = ltCountArray;
	}
	public String getDxCountArray() {
		return dxCountArray;
	}
	public void setDxCountArray(String dxCountArray) {
		this.dxCountArray = dxCountArray;
	}
	public String getPhoneType() {
		return phoneType;
	}
	public void setPhoneType(String phoneType) {
		this.phoneType = phoneType;
	}
	public long getYdCount() {
		return ydCount;
	}
	public void setYdCount(long ydCount) {
		this.ydCount = ydCount;
	}
	public long getLtCount() {
		return ltCount;
	}
	public void setLtCount(long ltCount) {
		this.ltCount = ltCount;
	}
	public long getDxCount() {
		return dxCount;
	}
	public void setDxCount(long dxCount) {
		this.dxCount = dxCount;
	}
	public String getCountArray() {
		return countArray;
	}
	public void setCountArray(String countArray) {
		this.countArray = countArray;
	}
	public String getSuccessArray() {
		return successArray;
	}
	public void setSuccessArray(String successArray) {
		this.successArray = successArray;
	}
	public String getDay() {
		return day;
	}
	public void setDay(String day) {
		this.day = day;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public int getSuccessCount() {
		return successCount;
	}
	public void setSuccessCount(int successCount) {
		this.successCount = successCount;
	}

}
