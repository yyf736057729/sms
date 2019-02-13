/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.mms.dao;

import java.util.List;

import com.siloyou.core.common.persistence.CrudDao;
import com.siloyou.core.common.persistence.annotation.MyBatisDao;
import com.siloyou.jmsg.modules.mms.entity.JmsgMmsAlldayReport;

/**
 * 彩信总日报DAO接口
 * @author zhukc
 * @version 2016-06-08
 */
@MyBatisDao
public interface JmsgMmsAlldayReportDao extends CrudDao<JmsgMmsAlldayReport> {
	public JmsgMmsAlldayReport queryAllDaySendCount();//日总发送量、下载成功 
	public Long queryAllDaySubmitCount();//网关成功量
	public Long queryAllDayReportCount();//状态报告量
	public Long queryReportCountByTaskId(String taskId);
	public Long querySubmitCountByTaskId(String taskId);
	
	public List<JmsgMmsAlldayReport> findTaskReportList(JmsgMmsAlldayReport entity);
	
	public List<JmsgMmsAlldayReport> findTaskReportListDetail(JmsgMmsAlldayReport entity);
}