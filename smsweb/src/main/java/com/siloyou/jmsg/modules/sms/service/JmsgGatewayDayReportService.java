/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.sms.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.siloyou.core.common.persistence.Page;
import com.siloyou.core.common.service.CrudService;
import com.siloyou.jmsg.modules.sms.dao.JmsgGatewayDayReportDao;
import com.siloyou.jmsg.modules.sms.entity.JmsgGatewayDayReport;
import com.siloyou.jmsg.modules.sms.entity.JmsgGatewayMonthReport;

/**
 * 通道日发送报表Service
 * @author zhukc
 * @version 2016-08-04
 */
@Service
@Transactional(readOnly = true)
public class JmsgGatewayDayReportService extends CrudService<JmsgGatewayDayReportDao, JmsgGatewayDayReport> {

	public JmsgGatewayDayReport get(String id) {
		return super.get(id);
	}
	
	public List<JmsgGatewayDayReport> findList(JmsgGatewayDayReport jmsgGatewayDayReport) {
		return super.findList(jmsgGatewayDayReport);
	}
	
	public Page<JmsgGatewayDayReport> findMonthPage(Page<JmsgGatewayDayReport> page, JmsgGatewayDayReport jmsgGatewayDayReport) {
		jmsgGatewayDayReport.setPage(page);
		page.setList(dao.findListByMonth(jmsgGatewayDayReport));
		return page;
	}
	
	public List<JmsgGatewayDayReport> findListReport(JmsgGatewayDayReport jmsgGatewayDayReport) {
		return dao.findListReport(jmsgGatewayDayReport);
	}
	
	public List<JmsgGatewayMonthReport> findMonthListReport(JmsgGatewayDayReport jmsgGatewayDayReport) {
		return dao.findMonthListReport(jmsgGatewayDayReport);
	}
	
	public Page<JmsgGatewayDayReport> findPage(Page<JmsgGatewayDayReport> page, JmsgGatewayDayReport jmsgGatewayDayReport) {
		return super.findPage(page, jmsgGatewayDayReport);
	}
	
	@Transactional(readOnly = false)
	public void save(JmsgGatewayDayReport jmsgGatewayDayReport) {
		super.save(jmsgGatewayDayReport);
	}
	
	@Transactional(readOnly = false)
	public void delete(JmsgGatewayDayReport jmsgGatewayDayReport) {
		super.delete(jmsgGatewayDayReport);
	}
	
}