/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.sms.entity;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.siloyou.core.common.persistence.DataEntity;

/**
 * 规则分组Entity
 * @author zj
 * @version 2017-03-26
 */
public class JmsgRuleGroup extends DataEntity<JmsgRuleGroup> {
	
	private static final long serialVersionUID = 1L;
	private String groupName;		// 分组名称
	private String description;		// 分组描述
	private String status;		// 状态 0：启用 1：禁用
	private Date createtime;		// 创建时间
	
	public JmsgRuleGroup() {
		super();
	}

	public JmsgRuleGroup(String id){
		super(id);
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getCreatetime() {
		return createtime;
	}

	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}
	
}