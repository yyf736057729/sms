/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.sanerzone.jmsg.dao;

import com.sanerzone.common.support.persistence.CrudDao;
import com.sanerzone.common.support.persistence.annotation.MyBatisDao;
import com.sanerzone.jmsg.entity.JmsgSmsDeliver;

/**
 * 短信上行DAO接口
 * @author zhukc
 * @version 2016-08-13
 */
@MyBatisDao
public interface JmsgSmsDeliverDao extends CrudDao<JmsgSmsDeliver> {

}