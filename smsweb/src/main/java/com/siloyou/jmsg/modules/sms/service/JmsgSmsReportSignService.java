/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.sms.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.siloyou.core.common.persistence.Page;
import com.siloyou.core.common.service.CrudService;
import com.siloyou.jmsg.modules.sms.entity.JmsgSmsReportSign;
import com.siloyou.jmsg.modules.sms.dao.JmsgSmsReportSignDao;

/**
 * 签名统计报表Service
 * @author zhukc
 * @version 2017-05-25
 */
@Service
@Transactional(readOnly = true)
public class JmsgSmsReportSignService extends CrudService<JmsgSmsReportSignDao, JmsgSmsReportSign> {

	public JmsgSmsReportSign get(String id) {
		return super.get(id);
	}
	
	public List<JmsgSmsReportSign> findList(JmsgSmsReportSign jmsgSmsReportSign) {
		return super.findList(jmsgSmsReportSign);
	}
	
	public Page<JmsgSmsReportSign> findPage(Page<JmsgSmsReportSign> page, JmsgSmsReportSign jmsgSmsReportSign) {
		return super.findPage(page, jmsgSmsReportSign);
	}
	
	public Page<JmsgSmsReportSign> findUsedSignPage(Page<JmsgSmsReportSign> page, JmsgSmsReportSign jmsgSmsReportSign) {
		jmsgSmsReportSign.setPage(page);
		page.setList(dao.usedSignList(jmsgSmsReportSign));
		return page;
	}
	
	@Transactional(readOnly = false)
	public void save(JmsgSmsReportSign jmsgSmsReportSign) {
		super.save(jmsgSmsReportSign);
	}
	
	@Transactional(readOnly = false)
	public void delete(JmsgSmsReportSign jmsgSmsReportSign) {
		super.delete(jmsgSmsReportSign);
	}
	
}