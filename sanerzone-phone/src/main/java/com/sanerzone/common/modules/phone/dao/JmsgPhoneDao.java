/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.sanerzone.common.modules.phone.dao;

import com.sanerzone.common.modules.phone.entity.JmsgPhone;
import com.sanerzone.common.support.persistence.CrudDao;
import com.sanerzone.common.support.persistence.annotation.MyBatisDao;

/**
 * 动态黑名单DAO接口
 * @author zj
 * @version 2016-09-26
 */
@MyBatisDao
public interface JmsgPhoneDao extends CrudDao<JmsgPhone> {
	public void deleteByPhone(String phone);
}