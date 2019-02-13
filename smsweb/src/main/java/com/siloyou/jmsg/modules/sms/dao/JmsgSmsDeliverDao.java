/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.sms.dao;

import com.siloyou.core.common.persistence.CrudDao;
import com.siloyou.core.common.persistence.annotation.MyBatisDao;
import com.siloyou.jmsg.modules.sms.entity.JmsgSmsDeliver;

/**
 * 短信上行DAO接口
 * @author zhukc
 * @version 2016-08-13
 */
@MyBatisDao
public interface JmsgSmsDeliverDao extends CrudDao<JmsgSmsDeliver> {
	
}