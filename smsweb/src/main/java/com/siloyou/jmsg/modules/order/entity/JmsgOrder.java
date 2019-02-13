/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.order.entity;

import org.hibernate.validator.constraints.Length;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import javax.validation.constraints.NotNull;
import com.siloyou.core.modules.sys.entity.User;

import com.siloyou.core.common.persistence.DataEntity;

/**
 * 订单信息Entity
 * @author zhukc
 * @version 2016-05-18
 */
public class JmsgOrder extends DataEntity<JmsgOrder> {
	
	private static final long serialVersionUID = 1L;
	private String systemOrderId;		// 系统订单号
	private String customerOrderId;		// 客户订单号
	private String orderContent;		// 订单内容（JSON格式）
	private String appType;				// 应用类型：短信、彩信、流量、话费
	private String submitType;			// 提交方式：WEB、API
	private String orderStatus;			// 订单状态
	private Date orderDate;				// 订单时间
	private Date orderDateQ;			// 订单时间
	private Date orderDateZ;			// 订单时间
	private User user;					// 用户ID
	private Date createDatetime;		// 创建时间
	private Date updateDatetime;		// 操作时间
	private String checkRemark;			// 审核备注
	
	public Date getOrderDateQ() {
		return orderDateQ;
	}

	public void setOrderDateQ(Date orderDateQ) {
		this.orderDateQ = orderDateQ;
	}

	public Date getOrderDateZ() {
		return orderDateZ;
	}

	public void setOrderDateZ(Date orderDateZ) {
		this.orderDateZ = orderDateZ;
	}

	public JmsgOrder() {
		super();
	}

	public JmsgOrder(String id){
		super(id);
	}

	@Length(min=1, max=64, message="系统订单号长度必须介于 1 和 64 之间")
	public String getSystemOrderId() {
		return systemOrderId;
	}

	public void setSystemOrderId(String systemOrderId) {
		this.systemOrderId = systemOrderId;
	}
	
	@Length(min=1, max=64, message="客户订单号长度必须介于 1 和 64 之间")
	public String getCustomerOrderId() {
		return customerOrderId;
	}

	public void setCustomerOrderId(String customerOrderId) {
		this.customerOrderId = customerOrderId;
	}
	
	public String getOrderContent() {
		return orderContent;
	}

	public void setOrderContent(String orderContent) {
		this.orderContent = orderContent;
	}
	
	@Length(min=1, max=10, message="应用类型：短信、彩信、流量、话费长度必须介于 1 和 10 之间")
	public String getAppType() {
		return appType;
	}

	public void setAppType(String appType) {
		this.appType = appType;
	}
	
	@Length(min=1, max=8, message="提交方式长度必须介于 1 和 8 之间")
	public String getSubmitType() {
		return submitType;
	}

	public void setSubmitType(String submitType) {
		this.submitType = submitType;
	}
	
	@Length(min=1, max=10, message="订单状态长度必须介于 1 和 10 之间")
	public String getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@NotNull(message="订单时间不能为空")
	public Date getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(Date orderDate) {
		this.orderDate = orderDate;
	}
	
	@NotNull(message="用户ID不能为空")
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@NotNull(message="创建时间不能为空")
	public Date getCreateDatetime() {
		return createDatetime;
	}

	public void setCreateDatetime(Date createDatetime) {
		this.createDatetime = createDatetime;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getUpdateDatetime() {
		return updateDatetime;
	}

	public void setUpdateDatetime(Date updateDatetime) {
		this.updateDatetime = updateDatetime;
	}
	
	@Length(min=0, max=256, message="审核备注长度必须介于 0 和 256 之间")
	public String getCheckRemark() {
		return checkRemark;
	}

	public void setCheckRemark(String checkRemark) {
		this.checkRemark = checkRemark;
	}
	
}