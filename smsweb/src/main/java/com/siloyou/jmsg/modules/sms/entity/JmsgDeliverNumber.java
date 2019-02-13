/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.sms.entity;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.siloyou.core.common.persistence.DataEntity;
import com.siloyou.core.common.utils.excel.annotation.ExcelField;
import com.siloyou.core.modules.sys.entity.User;

/**
 * 用户上行接入号Entity
 * @author zhukc
 * @version 2016-08-14
 */
public class JmsgDeliverNumber extends DataEntity<JmsgDeliverNumber> {
	
	private static final long serialVersionUID = 1L;
	private User user;				// 用户ID
	private String spNumber;		// 接入号
	private Date createtime;		// 创建时间
	
	private String gatewayId;		// 通道ID
	private String extNumber;       // 扩展号
	private String userId;			// 用户ID
	
	private List<JmsgDeliverNumber> deliverNumber;
	
	public JmsgDeliverNumber() {
		super();
	}

	public JmsgDeliverNumber(String id){
		super(id);
	}

	@ExcelField(title="用户ID", align=2, sort=10)
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getGatewayId() {
		return gatewayId;
	}

	public void setGatewayId(String gatewayId) {
		this.gatewayId = gatewayId;
	}

	public String getExtNumber() {
		return extNumber;
	}

	public void setExtNumber(String extNumber) {
		this.extNumber = extNumber;
	}

	public List<JmsgDeliverNumber> getDeliverNumber() {
		return deliverNumber;
	}

	public void setDeliverNumber(List<JmsgDeliverNumber> deliverNumber) {
		this.deliverNumber = deliverNumber;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	
	@ExcelField(title="接入号", align=2, sort=20)
	public String getSpNumber() {
		return spNumber;
	}

	public void setSpNumber(String spNumber) {
		this.spNumber = spNumber;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getCreatetime() {
		return createtime;
	}

	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}
	
}