/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.sms.dao;

import java.util.List;

import com.siloyou.core.common.persistence.CrudDao;
import com.siloyou.core.common.persistence.annotation.MyBatisDao;
import com.siloyou.jmsg.modules.sms.entity.JmsgPhoneInfo;

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