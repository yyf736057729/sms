/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.sanerzone.jmsg.entity;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sanerzone.common.support.persistence.DataEntity;

/**
 * 状态报告同步Entity
 * @author zj
 * @version 2017-03-17
 */
public class JmsgSmsReportSync extends DataEntity<JmsgSmsReportSync> {
	
	private static final long serialVersionUID = 1L;
	private String userId;		// 用户ID
	private String msgid;		// 网关ID
	private String stat;		// 接收状态
	private Long submitTime;		// 提交时间
	private Long doneTime;		// 接收时间
	private String srcid;		// 发送号码
	private String destTerminalId;		// 接收号码
	private String smscSequence;		// 网关侧序号
	private String bizid;		// 业务ID（对应jmsg_sms_send中的id）
	private Date createtime;		// 创建时间
	private String result;		// 网关状态0：成功 其他失败
	private String taskid;		// 任务ID（对应jmsg_mms_send_detail中的taskid）
	private String gatewayId;		// gateway_id
	
	public JmsgSmsReportSync() {
		super();
	}

	public JmsgSmsReportSync(String id){
		super(id);
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getMsgid() {
		return msgid;
	}

	public void setMsgid(String msgid) {
		this.msgid = msgid;
	}
	
	public String getStat() {
		return stat;
	}

	public void setStat(String stat) {
		this.stat = stat;
	}
	
	public Long getSubmitTime() {
		return submitTime;
	}

	public void setSubmitTime(Long submitTime) {
		this.submitTime = submitTime;
	}
	
	public Long getDoneTime() {
		return doneTime;
	}

	public void setDoneTime(Long doneTime) {
		this.doneTime = doneTime;
	}
	
	public String getSrcid() {
		return srcid;
	}

	public void setSrcid(String srcid) {
		this.srcid = srcid;
	}
	
	public String getDestTerminalId() {
		return destTerminalId;
	}

	public void setDestTerminalId(String destTerminalId) {
		this.destTerminalId = destTerminalId;
	}
	
	public String getSmscSequence() {
		return smscSequence;
	}

	public void setSmscSequence(String smscSequence) {
		this.smscSequence = smscSequence;
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
	
	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}
	
	public String getTaskid() {
		return taskid;
	}

	public void setTaskid(String taskid) {
		this.taskid = taskid;
	}
	
	public String getGatewayId() {
		return gatewayId;
	}

	public void setGatewayId(String gatewayId) {
		this.gatewayId = gatewayId;
	}
	
}