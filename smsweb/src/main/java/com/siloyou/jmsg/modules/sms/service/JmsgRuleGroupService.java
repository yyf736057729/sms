/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.sms.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.siloyou.core.common.persistence.Page;
import com.siloyou.core.common.service.CrudService;
import com.siloyou.jmsg.modules.sms.dao.JmsgRuleGroupDao;
import com.siloyou.jmsg.modules.sms.dao.JmsgRuleRelationDao;
import com.siloyou.jmsg.modules.sms.entity.JmsgRuleGroup;

/**
 * 规则分组Service
 * @author zj
 * @version 2017-03-26
 */
@Service
@Transactional(readOnly = true)
public class JmsgRuleGroupService extends CrudService<JmsgRuleGroupDao, JmsgRuleGroup> {
	@Autowired
	private JmsgRuleRelationDao jmsgRuleRelationDao;

	public JmsgRuleGroup get(String id) {
		return super.get(id);
	}
	
	public List<JmsgRuleGroup> findList(JmsgRuleGroup jmsgRuleGroup) {
		return super.findList(jmsgRuleGroup);
	}
	
	public Page<JmsgRuleGroup> findPage(Page<JmsgRuleGroup> page, JmsgRuleGroup jmsgRuleGroup) {
		return super.findPage(page, jmsgRuleGroup);
	}
	
	@Transactional(readOnly = false)
	public void save(JmsgRuleGroup jmsgRuleGroup) {
		super.save(jmsgRuleGroup);
	}
	
	@Transactional(readOnly = false)
	public void delete(JmsgRuleGroup jmsgRuleGroup) {
		super.delete(jmsgRuleGroup);
	}
	
	public List<JmsgRuleGroup> findRuleGroup(){
		return dao.findRuleGroup();
	}
	
}