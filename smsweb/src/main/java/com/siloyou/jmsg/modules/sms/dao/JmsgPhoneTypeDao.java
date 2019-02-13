/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.sms.dao;

import com.siloyou.core.common.persistence.CrudDao;
import com.siloyou.core.common.persistence.annotation.MyBatisDao;
import com.siloyou.jmsg.modules.sms.entity.JmsgPhoneType;

/**
 * 号码运营商DAO接口
 * @author zhukc
 * @version 2016-05-18
 */
@MyBatisDao
public interface JmsgPhoneTypeDao extends CrudDao<JmsgPhoneType> {
	public JmsgPhoneType getByNum(String num);
}