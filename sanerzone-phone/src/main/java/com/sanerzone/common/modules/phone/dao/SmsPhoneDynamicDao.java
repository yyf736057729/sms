/**
 * Copyright &copy; 2015-2016 SanerZone All rights reserved.
 */
package com.sanerzone.common.modules.phone.dao;

import com.sanerzone.common.support.persistence.CrudDao;
import com.sanerzone.common.support.persistence.annotation.MyBatisDao;
import com.sanerzone.common.modules.phone.entity.SmsPhoneDynamic;

/**
 * 营销黑名单DAO接口
 * @author zhukc
 * @version 2017-03-10
 */
@MyBatisDao
public interface SmsPhoneDynamicDao extends CrudDao<SmsPhoneDynamic> {
	
}