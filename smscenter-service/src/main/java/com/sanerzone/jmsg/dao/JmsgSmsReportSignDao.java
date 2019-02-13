/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.sanerzone.jmsg.dao;

import com.sanerzone.common.support.persistence.CrudDao;
import com.sanerzone.common.support.persistence.annotation.MyBatisDao;
import com.sanerzone.jmsg.entity.JmsgSmsReportSign;

/**
 * 签名统计报表DAO接口
 * @author zhukc
 * @version 2017-05-25
 */
@MyBatisDao
public interface JmsgSmsReportSignDao extends CrudDao<JmsgSmsReportSign> {
	
}