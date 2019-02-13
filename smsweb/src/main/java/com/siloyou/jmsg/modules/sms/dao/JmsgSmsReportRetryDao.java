/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.sms.dao;

import java.util.List;
import java.util.Map;

import com.siloyou.core.common.persistence.CrudDao;
import com.siloyou.core.common.persistence.annotation.MyBatisDao;
import com.siloyou.jmsg.common.message.SmsRtMessage;
import com.siloyou.jmsg.modules.sms.entity.JmsgSmsReportRetry;

/**
 * 状态报告重试DAO接口
 * @author zhukc
 * @version 2016-08-09
 */
@MyBatisDao
public interface JmsgSmsReportRetryDao extends CrudDao<JmsgSmsReportRetry> {
	public List<SmsRtMessage> findReportRetryList(Map<String,Object> map);
	
	public List<JmsgSmsReportRetry> findRTRetryList(Map<String,Object> map);
}