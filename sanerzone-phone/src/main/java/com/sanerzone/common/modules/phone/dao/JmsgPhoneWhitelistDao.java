/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.sanerzone.common.modules.phone.dao;

import java.util.List;

import com.sanerzone.common.modules.phone.entity.JmsgPhoneBlacklist;
import com.sanerzone.common.support.persistence.CrudDao;
import com.sanerzone.common.support.persistence.annotation.MyBatisDao;

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