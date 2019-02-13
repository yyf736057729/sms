package com.siloyou.jmsg.modules.sms.entity;

import com.siloyou.core.common.utils.excel.annotation.ExcelField;

//短信普通点对点
public class JmsgSmsDot {
	
	private String phone;//手机号
	
	private String smsContent;//发送内容
	
	private String content;//发送预览
	
	private int count;//发送数量
	
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	@ExcelField(title="phone", type=2, align=2, sort=1)
	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	@ExcelField(title="content", type=2, align=2, sort=2)
	public String getSmsContent() {
		return smsContent;
	}

	public void setSmsContent(String smsContent) {
		this.smsContent = smsContent;
	}
	
}
