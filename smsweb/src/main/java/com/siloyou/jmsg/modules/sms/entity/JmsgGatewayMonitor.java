/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.sms.entity;

import org.hibernate.validator.constraints.Length;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import javax.validation.constraints.NotNull;

import com.siloyou.core.common.persistence.DataEntity;

/**
 * 网关告警Entity
 * @author zj
 * @version 2016-10-15
 */
public class JmsgGatewayMonitor extends DataEntity<JmsgGatewayMonitor> {
	
	private static final long serialVersionUID = 1L;
	private String gatewayId;		// gateway_id
	private Integer timeFailCount;		// N分钟内失败次数
	private Integer continuousFailCount;		// 连续失败次数
	private String status;		// 网关状态 1：启用 0：禁用
	private String gatewayStatus;		// 运行状态 1：运行 0：停止 
	private Date createTime;		// 统计时间
	
	public JmsgGatewayMonitor() {
		super();
	}

	public JmsgGatewayMonitor(String id){
		super(id);
	}

	//@Length(min=1, max=36, message="gateway_id长度必须介于 1 和 36 之间")
	//页面上实在是弄不到网关id, 只好先注释掉 20181204 张辉
	public String getGatewayId() {
		return gatewayId;
	}

	public void setGatewayId(String gatewayId) {
		this.gatewayId = gatewayId;
	}
	
	//@Length(min=0, max=20, message="N分钟内失败次数长度必须介于 0 和 20 之间")
	//参考通道管理/通道分组, 加了这个注解, 就会报错, 先注释掉 20181204 张辉
	public Integer getTimeFailCount() {
		return timeFailCount;
	}

	public void setTimeFailCount(Integer timeFailCount) {
		this.timeFailCount = timeFailCount;
	}
	
	//@Length(min=0, max=20, message="连续失败次数长度必须介于 0 和 20 之间")
	//参考通道管理/通道分组, 加了这个注解, 就会报错, 先注释掉 20181204 张辉
	public Integer getContinuousFailCount() {
		return continuousFailCount;
	}

	public void setContinuousFailCount(Integer continuousFailCount) {
		this.continuousFailCount = continuousFailCount;
	}
	
	//@Length(min=1, max=1, message="网关状态长度必须介于 1 和 1 之间")
	//参考通道管理/通道分组, 加了这个注解, 就会报错, 先注释掉 20181204 张辉
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	//@Length(min=1, max=1, message="网关状态长度必须介于 1 和 1 之间")
	//参考通道管理/通道分组, 加了这个注解, 就会报错, 先注释掉 20181204 张辉
	public String getGatewayStatus() {
		return gatewayStatus;
	}

	public void setGatewayStatus(String gatewayStatus) {
		this.gatewayStatus = gatewayStatus;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@NotNull(message="统计时间不能为空")
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
}