/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.sanerzone.jmsg.dao;

import com.sanerzone.common.support.persistence.CrudDao;
import com.sanerzone.common.support.persistence.annotation.MyBatisDao;
import com.sanerzone.jmsg.entity.JmsgSmsReportSync;

/**
 * 状态报告同步DAO接口
 * @author zj
 * @version 2017-03-17
 */
@MyBatisDao
public interface JmsgSmsReportSyncDao extends CrudDao<JmsgSmsReportSync> {
	
}