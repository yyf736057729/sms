/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.sms.dao;

import java.util.List;

import com.siloyou.core.common.persistence.CrudDao;
import com.siloyou.core.common.persistence.annotation.MyBatisDao;
import com.siloyou.jmsg.modules.sms.entity.JmsgRuleGroup;

/**
 * 规则分组DAO接口
 * @author zj
 * @version 2017-03-26
 */
@MyBatisDao
public interface JmsgRuleGroupDao extends CrudDao<JmsgRuleGroup> {
	public List<JmsgRuleGroup> findRuleGroup();
}