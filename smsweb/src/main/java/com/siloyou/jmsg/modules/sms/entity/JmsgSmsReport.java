/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.sms.entity;

import com.siloyou.core.modules.sys.entity.User;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.siloyou.core.common.persistence.DataEntity;

/**
 * 状态报告Entity
 * @author zhukc
 * @version 2016-08-05
 */
public class JmsgSmsReport extends DataEntity<JmsgSmsReport> {
	
	private static final long serialVersionUID = 1L;
	private User user;		// 用户ID
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
	private String taskid;		// 任务ID（对应jmsg_sms_send中的taskid）
	private String gatewayId;		// gateway_id
	
	private String tableName;			//查询的数据表名称
	private Date submitTimeDate;	// 提交时间

	public Date getSubmitTimeDate() {
		return submitTimeDate;
	}

	public void setSubmitTimeDate(Date submitTimeDate) {
		this.submitTimeDate = submitTimeDate;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public JmsgSmsReport() {
		super();
	}

	public JmsgSmsReport(String id){
		super(id);
	}

	@NotNull(message="用户ID不能为空")
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	@Length(min=0, max=32, message="网关ID长度必须介于 0 和 32 之间")
	public String getMsgid() {
		return msgid;
	}

	public void setMsgid(String msgid) {
		this.msgid = msgid;
	}
	
	@Length(min=0, max=10, message="接收状态长度必须介于 0 和 10 之间")
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
	
	@Length(min=0, max=32, message="发送号码长度必须介于 0 和 32 之间")
	public String getSrcid() {
		return srcid;
	}

	public void setSrcid(String srcid) {
		this.srcid = srcid;
	}
	
	@Length(min=0, max=32, message="接收号码长度必须介于 0 和 32 之间")
	public String getDestTerminalId() {
		return destTerminalId;
	}

	public void setDestTerminalId(String destTerminalId) {
		this.destTerminalId = destTerminalId;
	}
	
	@Length(min=0, max=11, message="网关侧序号长度必须介于 0 和 11 之间")
	public String getSmscSequence() {
		return smscSequence;
	}

	public void setSmscSequence(String smscSequence) {
		this.smscSequence = smscSequence;
	}
	
	@Length(min=0, max=11, message="业务ID（对应jmsg_sms_send中的id）长度必须介于 0 和 11 之间")
	public String getBizid() {
		return bizid;
	}

	public void setBizid(String bizid) {
		this.bizid = bizid;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@NotNull(message="创建时间不能为空")
	public Date getCreatetime() {
		return createtime;
	}

	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}
	
	@Length(min=0, max=10, message="网关状态0：成功 其他失败长度必须介于 0 和 10 之间")
	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}
	
	@Length(min=1, max=32, message="任务ID（对应jmsg_sms_send中的taskid）长度必须介于 1 和 32 之间")
	public String getTaskid() {
		return taskid;
	}

	public void setTaskid(String taskid) {
		this.taskid = taskid;
	}
	
	@Length(min=0, max=36, message="gateway_id长度必须介于 0 和 36 之间")
	public String getGatewayId() {
		return gatewayId;
	}

	public void setGatewayId(String gatewayId) {
		this.gatewayId = gatewayId;
	}
	
}