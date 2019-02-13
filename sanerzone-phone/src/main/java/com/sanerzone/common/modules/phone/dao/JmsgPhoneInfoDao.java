/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.sanerzone.common.modules.phone.dao;

import java.util.List;

import com.sanerzone.common.modules.phone.entity.JmsgPhoneInfo;
import com.sanerzone.common.support.persistence.CrudDao;
import com.sanerzone.common.support.persistence.annotation.MyBatisDao;

/**
 * 号段管理DAO接口
 * @author zhukc
 * @version 2016-07-30
 */
@MyBatisDao
public interface JmsgPhoneInfoDao extends CrudDao<JmsgPhoneInfo> {
    /**
     * 获取省市列表
     * @return
     * @see [类、类#方法、类#成员]
     */
	public List<JmsgPhoneInfo> findCityList();
}