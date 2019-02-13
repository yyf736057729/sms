/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.sms.entity;

import java.util.List;

import com.siloyou.core.common.persistence.DataEntity;

/**
 * 规则关系Entity
 * @author zj
 * @version 2017-03-26
 */
public class JmsgRuleRelation extends DataEntity<JmsgRuleRelation> {
	
	private static final long serialVersionUID = 1L;
	private String groupId;			// 分组ID
	private String ruleType;		// 规则分类 1：网址 2：电话 3：关键字 4：正则式
	private String ruleId;			// 规则ID
	
	private JmsgRuleInfo jmsgRuleInfo;
	private JmsgRuleGroup jmsgRuleGroup;
	private List<JmsgRuleInfo> jmsgRuleInfoList;
	private List<JmsgRuleGroup> jmsgRuleGroupList;
	private List<String> ruleIdList;//规则ID列表
	
	public JmsgRuleRelation() {
		super();
	}

	public JmsgRuleRelation(String id){
		super(id);
	}

	public JmsgRuleInfo getJmsgRuleInfo() {
		return jmsgRuleInfo;
	}

	public void setJmsgRuleInfo(JmsgRuleInfo jmsgRuleInfo) {
		this.jmsgRuleInfo = jmsgRuleInfo;
	}

	public JmsgRuleGroup getJmsgRuleGroup() {
		return jmsgRuleGroup;
	}

	public void setJmsgRuleGroup(JmsgRuleGroup jmsgRuleGroup) {
		this.jmsgRuleGroup = jmsgRuleGroup;
	}

	public List<JmsgRuleGroup> getJmsgRuleGroupList() {
		return jmsgRuleGroupList;
	}

	public void setJmsgRuleGroupList(List<JmsgRuleGroup> jmsgRuleGroupList) {
		this.jmsgRuleGroupList = jmsgRuleGroupList;
	}

	public List<JmsgRuleInfo> getJmsgRuleInfoList() {
		return jmsgRuleInfoList;
	}

	public void setJmsgRuleInfoList(List<JmsgRuleInfo> jmsgRuleInfoList) {
		this.jmsgRuleInfoList = jmsgRuleInfoList;
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

	public List<String> getRuleIdList() {
		return ruleIdList;
	}

	public void setRuleIdList(List<String> ruleIdList) {
		this.ruleIdList = ruleIdList;
	}
	
}