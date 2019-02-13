/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.account.dao;

import java.util.List;

import com.siloyou.core.common.persistence.CrudDao;
import com.siloyou.core.common.persistence.annotation.MyBatisDao;
import com.siloyou.jmsg.modules.account.entity.JmsgAccountLog;

/**
 * 资金变动日志DAO接口
 * @author zhukc
 * @version 2016-05-17
 */
@MyBatisDao
public interface JmsgAccountLogDao extends CrudDao<JmsgAccountLog> {
	public List<JmsgAccountLog> findAccountLogDetailList(JmsgAccountLog jmsgAccountLog);
}