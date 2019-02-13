/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.sms.dao;

import java.util.Map;

import com.siloyou.core.common.persistence.CrudDao;
import com.siloyou.core.common.persistence.annotation.MyBatisDao;
import com.siloyou.jmsg.modules.sms.entity.JmsgSmsWarn;

/**
 * 告警表DAO接口
 * @author zj
 * @version 2016-10-15
 */
@MyBatisDao
public interface JmsgSmsWarnDao extends CrudDao<JmsgSmsWarn> {
    
    /**
     * 根据ID修改告警处理状态 
     * @param map
     * @see [类、类#方法、类#成员]
     */
	public void updateStatusById(Map<String, Object> map);
}