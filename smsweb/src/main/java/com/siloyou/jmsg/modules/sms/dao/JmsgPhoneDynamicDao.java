/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.sms.dao;

import java.util.List;

import com.siloyou.core.common.persistence.CrudDao;
import com.siloyou.core.common.persistence.annotation.MyBatisDao;
import com.siloyou.jmsg.modules.sms.entity.JmsgPhoneDynamic;

/**
 * 动态黑名单DAO接口
 * @author zhukc
 * @version 2016-06-21
 */
@MyBatisDao
public interface JmsgPhoneDynamicDao extends CrudDao<JmsgPhoneDynamic> {
	public List<String> findPhoneList(JmsgPhoneDynamic jmsgPhoneDynamic);
	
}