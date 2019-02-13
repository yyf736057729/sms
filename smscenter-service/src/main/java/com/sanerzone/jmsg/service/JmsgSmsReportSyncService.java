/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.sanerzone.jmsg.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sanerzone.common.support.persistence.CrudService;
import com.sanerzone.common.support.persistence.Page;
import com.sanerzone.jmsg.dao.JmsgSmsReportSyncDao;
import com.sanerzone.jmsg.entity.JmsgSmsReportSync;

/**
 * 状态报告同步Service
 * @author zj
 * @version 2017-03-17
 */
@Service
@Transactional(readOnly = true)
public class JmsgSmsReportSyncService extends CrudService<JmsgSmsReportSyncDao, JmsgSmsReportSync> {

	public JmsgSmsReportSync get(String id) {
		return super.get(id);
	}
	
	public List<JmsgSmsReportSync> findList(JmsgSmsReportSync jmsgSmsReportSync) {
		return super.findList(jmsgSmsReportSync);
	}
	
	public Page<JmsgSmsReportSync> findPage(Page<JmsgSmsReportSync> page, JmsgSmsReportSync jmsgSmsReportSync) {
		return super.findPage(page, jmsgSmsReportSync);
	}
	
	@Transactional(readOnly = false)
	public void save(JmsgSmsReportSync jmsgSmsReportSync) {
		super.save(jmsgSmsReportSync);
	}
	
	@Transactional(readOnly = false)
	public void delete(JmsgSmsReportSync jmsgSmsReportSync) {
		super.delete(jmsgSmsReportSync);
	}
	
}