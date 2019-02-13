/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.sanerzone.common.modules.smscenter.entity;

import com.sanerzone.common.support.persistence.DataEntity;

/**
 * 规则关系Entity
 * @author zj
 * @version 2017-03-26
 */
public class JmsgRuleRelation extends DataEntity<JmsgRuleRelation> {
	
	private static final long serialVersionUID = 1L;
	private String groupId;		// 分组ID
	private String ruleType;		// 规则分类 1：网址 2：电话 3：关键字 4：正则式
	private String ruleId;		// 规则ID
	
	public JmsgRuleRelation() {
		super();
	}

	public JmsgRuleRelation(String id){
		super(id);
	}

	public String getRuleType() {
		return ruleType;
	}

	public void setRuleType(String ruleType) {
		this.ruleType = ruleType;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	
	public String getRuleId() {
		return ruleId;
	}

	public void setRuleId(String ruleId) {
		this.ruleId = ruleId;
	}
	
}