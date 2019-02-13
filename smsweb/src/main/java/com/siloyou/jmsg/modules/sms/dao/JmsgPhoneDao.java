/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.sms.dao;

import com.siloyou.core.common.persistence.CrudDao;
import com.siloyou.core.common.persistence.annotation.MyBatisDao;
import com.siloyou.jmsg.modules.sms.entity.JmsgPhone;

/**
 * 动态黑名单DAO接口
 * @author zj
 * @version 2016-09-26
 */
@MyBatisDao
public interface JmsgPhoneDao extends CrudDao<JmsgPhone> {
	public void deleteByPhone(String phone);
}