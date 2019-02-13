/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.mms.entity;

import java.util.Date;

import com.siloyou.core.common.persistence.DataEntity;

/**
 * 网关状态Entity
 * @author zhukc
 * @version 2016-05-28
 */
public class JmsgMmsSubmit extends DataEntity<JmsgMmsSubmit> {
	
	private static final long serialVersionUID = 1L;
	private String msgid;		// 网关ID
	private String result;		// 网关状态0：成功 其他失败
	private String bizid;		// 业务ID（对应jmsg_mms_send_detail中的id）
	private String taskid;		// 任务ID
	private Date createtime;	// createtime
	
	public JmsgMmsSubmit() {
		super();
	}

	public JmsgMmsSubmit(String id){
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

}