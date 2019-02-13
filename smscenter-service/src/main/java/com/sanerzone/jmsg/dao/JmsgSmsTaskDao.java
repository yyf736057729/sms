/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.sanerzone.jmsg.dao;

import com.sanerzone.common.support.persistence.CrudDao;
import com.sanerzone.common.support.persistence.annotation.MyBatisDao;
import com.sanerzone.jmsg.entity.JmsgSmsTask;

import java.util.Map;

/**
 * 短信任务发送DAO接口
 * @author zhukc
 * @version 2016-07-20
 */
@MyBatisDao
public interface JmsgSmsTaskDao extends CrudDao<JmsgSmsTask> {
	
	void updateJmsgSmsTask(Map map);
	

}