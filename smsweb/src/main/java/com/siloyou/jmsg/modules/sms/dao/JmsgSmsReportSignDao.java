/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.sms.dao;

import java.util.List;

import com.siloyou.core.common.persistence.CrudDao;
import com.siloyou.core.common.persistence.annotation.MyBatisDao;
import com.siloyou.jmsg.modules.sms.entity.JmsgSmsReportSign;

/**
 * 签名统计报表DAO接口
 * @author zhukc
 * @version 2017-05-25
 */
@MyBatisDao
public interface JmsgSmsReportSignDao extends CrudDao<JmsgSmsReportSign> {
	public List<JmsgSmsReportSign> usedSignList(JmsgSmsReportSign jmsgSmsReportSign);
}