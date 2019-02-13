/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.sms.entity;

import java.util.Date;

import com.siloyou.core.common.persistence.DataEntity;

/**
 * 通道Entity
 * @author zhukc
 * @version 2016-07-29
 */
public class JmsgSmsPush extends DataEntity<JmsgSmsPush> {
	
	private static final long serialVersionUID = 1L;
	private String bizid;
	private String taskid;
	private String result;
	private Date createtime;
	private Date pushtime;
	private String pushType;//推送类型 0:http 1:cmpp
	private String userId;
	
	private String tableName;			//查询的数据表名称
	
	public Date getPushtime() {
		return pushtime;
	}

	public void setPushtime(Date pushtime) {
		this.pushtime = pushtime;
	}

	public String getPushType() {
		return pushType;
	}

	public void setPushType(String pushType) {
		this.pushType = pushType;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public JmsgSmsPush() {
		super();
	}

	public String getBizid() {
		return bizid;
	}

	public void setBizid(String bizid) {
		this.bizid = bizid;
	}

	public String getTaskid() {
		return taskid;
	}

	public void setTaskid(String taskid) {
		this.taskid = taskid;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public Date getCreatetime() {
		return createtime;
	}

	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}
	
}