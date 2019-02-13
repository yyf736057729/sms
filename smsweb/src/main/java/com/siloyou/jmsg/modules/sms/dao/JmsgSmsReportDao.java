/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.sms.dao;

import java.util.List;

import com.siloyou.core.common.persistence.CrudDao;
import com.siloyou.core.common.persistence.annotation.MyBatisDao;
import com.siloyou.jmsg.modules.sms.entity.JmsgSmsReport;

/**
 * 状态报告DAO接口
 * @author zhukc
 * @version 2016-08-05
 */
@MyBatisDao
public interface JmsgSmsReportDao extends CrudDao<JmsgSmsReport> {
	 List<JmsgSmsReport> findListV2(JmsgSmsReport jmsgSmsReport);

	//查询状态报告
	 JmsgSmsReport findByBizid (JmsgSmsReport jmsgSmsReport);
}