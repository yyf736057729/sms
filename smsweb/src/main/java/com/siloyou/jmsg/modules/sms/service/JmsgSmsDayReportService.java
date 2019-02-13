/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.sms.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.siloyou.core.common.persistence.Page;
import com.siloyou.core.common.service.CrudService;
import com.siloyou.jmsg.modules.sms.dao.JmsgSmsDayReportDao;
import com.siloyou.jmsg.modules.sms.dao.JmsgSmsSendDao;
import com.siloyou.jmsg.modules.sms.entity.JmsgSmsDayReport;
import com.siloyou.jmsg.modules.sms.entity.JmsgSmsGatewayReport;
import com.siloyou.jmsg.modules.sms.entity.JmsgSmsMonthReport;
import com.siloyou.jmsg.modules.sms.entity.JmsgSmsSend;
import com.siloyou.jmsg.modules.sms.entity.SmsUserIndex;

/**
 * 短信日报表Service
 * @author zhukc
 * @version 2016-07-28
 */
@Service
@Transactional(readOnly = true)
public class JmsgSmsDayReportService extends CrudService<JmsgSmsDayReportDao, JmsgSmsDayReport> {

	@Autowired
	private JmsgSmsSendDao jmsgSmsSendDao;
	
	public JmsgSmsDayReport get(String id) {
		return super.get(id);
	}
	
	public List<JmsgSmsDayReport> findList(JmsgSmsDayReport jmsgSmsDayReport) {
		return super.findList(jmsgSmsDayReport);
	}
	
	public List<JmsgSmsDayReport> findListReport(JmsgSmsDayReport jmsgSmsDayReport) {
		String method = Thread.currentThread() .getStackTrace()[1].getMethodName();
		logger.info(xmlName + "===>>>" + method);
		return dao.findListReport(jmsgSmsDayReport);
	}
	
	public List<JmsgSmsMonthReport> findMonthReport(JmsgSmsDayReport jmsgSmsDayReport) {
		String method = Thread.currentThread() .getStackTrace()[1].getMethodName();
		logger.info(xmlName + "===>>>" + method);
		return dao.findMonthReport(jmsgSmsDayReport);
	}
	
	public List<JmsgSmsGatewayReport> findGatewayReport(JmsgSmsDayReport jmsgSmsDayReport) {
		String method = Thread.currentThread() .getStackTrace()[1].getMethodName();
		logger.info(xmlName + "===>>>" + method);
		return dao.findGatewayReport(jmsgSmsDayReport);
	}
	
	public Page<JmsgSmsDayReport> findPage(Page<JmsgSmsDayReport> page, JmsgSmsDayReport jmsgSmsDayReport) {
		return super.findPage(page, jmsgSmsDayReport);
	}

	
	public Page<JmsgSmsSend> findViewPage(Page<JmsgSmsSend> page, JmsgSmsSend jmsgSmsSend) {
		jmsgSmsSend.setPage(page);
		String method = Thread.currentThread() .getStackTrace()[1].getMethodName();
		logger.info(xmlName + "===>>>findDetailByReprot");
		page.setList(jmsgSmsSendDao.findDetailByReprot(jmsgSmsSend));
		return page;
	}
	
	public Page<JmsgSmsDayReport> findListReportPhoneType(Page<JmsgSmsDayReport> page, JmsgSmsDayReport jmsgSmsDayReport) {
		jmsgSmsDayReport.setPage(page);
		String method = Thread.currentThread() .getStackTrace()[1].getMethodName();
		logger.info(xmlName + "===>>>" + method);
		page.setList(dao.findListReportPhoneType(jmsgSmsDayReport));
		return page;
	}
	
	public List<JmsgSmsDayReport> findListPhoneType(JmsgSmsDayReport jmsgSmsDayReport) {
		String method = Thread.currentThread() .getStackTrace()[1].getMethodName();
		logger.info(xmlName + "===>>>" + method);
		return dao.findListPhoneType(jmsgSmsDayReport);
	}
	
	@Transactional(readOnly = false)
	public void save(JmsgSmsDayReport jmsgSmsDayReport) {
		super.save(jmsgSmsDayReport);
	}
	
	@Transactional(readOnly = false)
	public void delete(JmsgSmsDayReport jmsgSmsDayReport) {
		super.delete(jmsgSmsDayReport);
	}
	
	public List<JmsgSmsDayReport> findList4Index(JmsgSmsDayReport jmsgSmsDayReport) {
		String method = Thread.currentThread() .getStackTrace()[1].getMethodName();
		logger.info(xmlName + "===>>>" + method);
		return dao.findList4Index(jmsgSmsDayReport);
	}
	
	//按用户通道的日统计报表和月统计
	public Page<JmsgSmsDayReport> findUserGatewayPage(Page<JmsgSmsDayReport> page, JmsgSmsDayReport jmsgSmsDayReport) {
		jmsgSmsDayReport.setPage(page);
		String method = Thread.currentThread() .getStackTrace()[1].getMethodName();
		logger.info(xmlName + "===>>>findUserGatewayList");
		page.setList(dao.findUserGatewayList(jmsgSmsDayReport));
		return page;
	}
	
	//按通道的日统计报表和月统计
	public Page<JmsgSmsDayReport> findGatewayPage(Page<JmsgSmsDayReport> page, JmsgSmsDayReport jmsgSmsDayReport) {
		jmsgSmsDayReport.setPage(page);
		String method = Thread.currentThread() .getStackTrace()[1].getMethodName();
		logger.info(xmlName + "===>>>findGatewayList");
		page.setList(dao.findGatewayList(jmsgSmsDayReport));
		return page;
	}
	
	//日统计报表
	public Page<JmsgSmsDayReport> findDayReprotPage(Page<JmsgSmsDayReport> page, JmsgSmsDayReport jmsgSmsDayReport) {
		jmsgSmsDayReport.setPage(page);
		String method = Thread.currentThread() .getStackTrace()[1].getMethodName();
		logger.info(xmlName + "===>>>findDayReportList");
		page.setList(dao.findDayReportList(jmsgSmsDayReport));
		return page;
	}
	
	//获取日数据汇总
	public JmsgSmsDayReport findSmsDayReportByDay(JmsgSmsDayReport param) {
		String method = Thread.currentThread() .getStackTrace()[1].getMethodName();
		logger.info(xmlName + "===>>>" + method);
		return dao.findSmsDayReportByDay(param);
	}
	
	//用户近30天发送量数据趋势
	public List<SmsUserIndex> queryCountByDay(JmsgSmsDayReport param) {
		String method = Thread.currentThread() .getStackTrace()[1].getMethodName();
		logger.info(xmlName + "===>>>findCountByDay");
		return dao.findCountByDay(param);
	}
	
	//运营商网关成功量、状态成功量、状态失败量、状态未知量
	public List<JmsgSmsDayReport> findCountByPhoneType(JmsgSmsDayReport param){
		String method = Thread.currentThread() .getStackTrace()[1].getMethodName();
		logger.info(xmlName + "===>>>" + method);
		return dao.findCountByPhoneType(param);
	}
	
	//运营商网关成功量数据
	public List<SmsUserIndex> findCountByDayPhoneType(JmsgSmsDayReport param){
		String method = Thread.currentThread() .getStackTrace()[1].getMethodName();
		logger.info(xmlName + "===>>>" + method);
		return dao.findCountByDayPhoneType(param);
	}
}