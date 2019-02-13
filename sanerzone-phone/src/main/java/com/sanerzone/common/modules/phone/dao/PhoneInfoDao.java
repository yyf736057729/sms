/**
 * Copyright &copy; 2015-2016 SanerZone All rights reserved.
 */
package com.sanerzone.common.modules.phone.dao;

import com.sanerzone.common.support.persistence.CrudDao;
import com.sanerzone.common.support.persistence.annotation.MyBatisDao;
import com.sanerzone.common.modules.phone.entity.PhoneInfo;

/**
 * 分流号段DAO接口
 * @author zhukc
 * @version 2017-03-10
 */
@MyBatisDao
public interface PhoneInfoDao extends CrudDao<PhoneInfo> {
	
}