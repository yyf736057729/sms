/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.sms.dao;

import java.util.List;

import com.siloyou.core.common.persistence.CrudDao;
import com.siloyou.core.common.persistence.annotation.MyBatisDao;
import com.siloyou.jmsg.modules.sms.entity.JmsgPhoneBlacklist;

/**
 * 白名单DAO接口
 * @author zhukc
 * @version 2016-05-18
 */
@MyBatisDao
public interface JmsgPhoneWhitelistDao extends CrudDao<JmsgPhoneBlacklist> {
	public JmsgPhoneBlacklist getByPhone(String phone);
	
	public List<String> findAllPhone();
	
	public void deleteByPhone(String phone);
}