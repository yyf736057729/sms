/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.sanerzone.jmsg.entity;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sanerzone.common.support.persistence.DataEntity;

/**
 * 网关状态重试Entity
 * @author zj
 * @version 2017-03-17
 */
public class JmsgSmsSubmitSync extends DataEntity<JmsgSmsSubmitSync> {
	
	private static final long serialVersionUID = 1L;
	private String msgid;		// 网关消息ID
	private String result;		// 网关状态0：成功 其他失败
	private String bizid;		// 业务ID（对应jmsg_mms_send_detail中的id）
	private Date createtime;		// createtime
	private String taskid;		// 任务ID（对应jmsg_mms_send_detail中的taskid）
	private String userid;		// userid
	private String gatewayid;		// gatewayid
	private String reserve;		// 描述
	private String phone;		// 用户手机号码
	
	public JmsgSmsSubmitSync() {
		super();
	}

	public JmsgSmsSubmitSync(String id){
		super(id);
	}

	public String getMsgid() {
		return msgid;
	}

	public void setMsgid(String msgid) {
		this.msgid = msgid;
	}
	
	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}
	
	public String getBizid() {
		return bizid;
	}

	public void setBizid(String bizid) {
		this.bizid = bizid;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getCreatetime() {
		return createtime;
	}

	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}
	
	public String getTaskid() {
		return taskid;
	}

	public void setTaskid(String taskid) {
		this.taskid = taskid;
	}
	
	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}
	
	public String getGatewayid() {
		return gatewayid;
	}

	public void setGatewayid(String gatewayid) {
		this.gatewayid = gatewayid;
	}
	
	public String getReserve() {
		return reserve;
	}

	public void setReserve(String reserve) {
		this.reserve = reserve;
	}
	
	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}
	
}