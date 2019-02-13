/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.mms.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.siloyou.core.common.persistence.Page;
import com.siloyou.core.common.service.CrudService;
import com.siloyou.jmsg.modules.mms.entity.JmsgMmsAlldayReport;
import com.siloyou.jmsg.modules.mms.dao.JmsgMmsAlldayReportDao;

/**
 * 彩信总日报Service
 * @author zhukc
 * @version 2016-06-08
 */
@Service
@Transactional(readOnly = true)
public class JmsgMmsAlldayReportService extends CrudService<JmsgMmsAlldayReportDao, JmsgMmsAlldayReport> {

	public JmsgMmsAlldayReport get(String id) {
		return super.get(id);
	}
	
	public List<JmsgMmsAlldayReport> findList(JmsgMmsAlldayReport jmsgMmsAlldayReport) {
		return super.findList(jmsgMmsAlldayReport);
	}
	
	public Page<JmsgMmsAlldayReport> findPage(Page<JmsgMmsAlldayReport> page, JmsgMmsAlldayReport jmsgMmsAlldayReport) {
		return super.findPage(page, jmsgMmsAlldayReport);
	}
	
	public Page<JmsgMmsAlldayReport> reportFindPage(Page<JmsgMmsAlldayReport> page, JmsgMmsAlldayReport jmsgMmsAlldayReport) {
		jmsgMmsAlldayReport.setPage(page);
		page.setList(dao.findTaskReportList(jmsgMmsAlldayReport));
		return page;
	}
	
	public Page<JmsgMmsAlldayReport> reportFindPageDetail(Page<JmsgMmsAlldayReport> page, JmsgMmsAlldayReport jmsgMmsAlldayReport) {
		jmsgMmsAlldayReport.setPage(page);
		page.setList(dao.findTaskReportListDetail(jmsgMmsAlldayReport));
		return page;
	}	
	
	@Transactional(readOnly = false)
	public void save(JmsgMmsAlldayReport jmsgMmsAlldayReport) {
		super.save(jmsgMmsAlldayReport);
	}
	
	@Transactional(readOnly = false)
	public void delete(JmsgMmsAlldayReport jmsgMmsAlldayReport) {
		super.delete(jmsgMmsAlldayReport);
	}
	
	public Long submitCountByTaskId(String taskId){
		return dao.querySubmitCountByTaskId(taskId);
	}
	
	public Long reportCountByTaskId(String taskId){
		return dao.queryReportCountByTaskId(taskId);
	}
	
}