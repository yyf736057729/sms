/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.mms.dao;

import java.util.List;

import com.siloyou.core.common.persistence.CrudDao;
import com.siloyou.core.common.persistence.annotation.MyBatisDao;
import com.siloyou.jmsg.modules.mms.entity.JmsgMmsReport;

/**
 * 状态报告DAO接口
 * @author zhukc
 * @version 2016-05-28
 */
@MyBatisDao
public interface JmsgMmsReportDao extends CrudDao<JmsgMmsReport> {
	public JmsgMmsReport findByBizid(String bizid);
	public JmsgMmsReport findByMsgid(String msgid);
	
	public int getMaxMmsReportId();
	public List<JmsgMmsReport> getMmsReportList();
	public List<JmsgMmsReport> getRetrySyncMmsReportList();
	
	public Integer findSuccessCount(String taskid);
	public List<JmsgMmsReport> findDetailSendList(String taskid);
	
}