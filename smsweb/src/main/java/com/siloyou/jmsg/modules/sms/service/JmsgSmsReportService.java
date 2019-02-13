/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.sms.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.siloyou.core.common.persistence.Page;
import com.siloyou.core.common.service.CrudService;
import com.siloyou.jmsg.modules.sms.entity.JmsgSmsReport;
import com.siloyou.jmsg.modules.sms.dao.JmsgSmsReportDao;

/**
 * 状态报告Service
 * @author zhukc
 * @version 2016-08-05
 */
@Service
@Transactional(readOnly = true)
public class JmsgSmsReportService extends CrudService<JmsgSmsReportDao, JmsgSmsReport> {

	public JmsgSmsReport get(String id) {
		return super.get(id);
	}
	
	public List<JmsgSmsReport> findList(JmsgSmsReport jmsgSmsReport) {
		return super.findList(jmsgSmsReport);
	}
	
	public Page<JmsgSmsReport> findPage(Page<JmsgSmsReport> page, JmsgSmsReport jmsgSmsReport) {
		return super.findPage(page, jmsgSmsReport);
	}
	
	@Transactional(readOnly = false)
	public void save(JmsgSmsReport jmsgSmsReport) {
		super.save(jmsgSmsReport);
	}
	
	@Transactional(readOnly = false)
	public void delete(JmsgSmsReport jmsgSmsReport) {
		super.delete(jmsgSmsReport);
	}
	
	public List<JmsgSmsReport> findListV2(JmsgSmsReport jmsgSmsReport)
	{
		return dao.findListV2(jmsgSmsReport);
	}

	public JmsgSmsReport findByBizid (JmsgSmsReport jmsgSmsReport){
		return dao.findByBizid(jmsgSmsReport);
	};
}