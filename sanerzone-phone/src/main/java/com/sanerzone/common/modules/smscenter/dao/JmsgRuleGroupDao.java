/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.sanerzone.common.modules.smscenter.dao;

import com.sanerzone.common.modules.smscenter.entity.JmsgRuleGroup;
import com.sanerzone.common.support.persistence.CrudDao;
import com.sanerzone.common.support.persistence.annotation.MyBatisDao;

/**
 * 规则分组DAO接口
 * @author zj
 * @version 2017-03-26
 */
@MyBatisDao
public interface JmsgRuleGroupDao extends CrudDao<JmsgRuleGroup> {
	
}