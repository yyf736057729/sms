/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.sanerzone.jmsg.dao;

import com.sanerzone.common.support.persistence.CrudDao;
import com.sanerzone.common.support.persistence.annotation.MyBatisDao;
import com.sanerzone.jmsg.entity.JmsgSmsReport;

/**
 * 状态报告DAO接口
 * @author zhukc
 * @version 2016-08-05
 */
@MyBatisDao
public interface JmsgSmsReportDao extends CrudDao<JmsgSmsReport> {
	
}