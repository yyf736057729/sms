/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.sms.dao;

import java.util.List;

import com.siloyou.core.common.persistence.CrudDao;
import com.siloyou.core.common.persistence.annotation.MyBatisDao;
import com.siloyou.jmsg.modules.sms.entity.JmsgRuleInfo;

/**
 * 规则管理DAO接口
 * @author zj
 * @version 2017-03-26
 */
@MyBatisDao
public interface JmsgRuleInfoDao extends CrudDao<JmsgRuleInfo> {
	public List<JmsgRuleInfo> findRuleInfoByGroup(JmsgRuleInfo param);
	public List<JmsgRuleInfo> initRuleInfo(JmsgRuleInfo param);
}