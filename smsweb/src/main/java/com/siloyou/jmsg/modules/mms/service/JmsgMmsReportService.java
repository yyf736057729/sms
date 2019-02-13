/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.mms.service;

import java.util.List;

import org.apache.ibatis.executor.BatchResult;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.siloyou.core.common.persistence.Page;
import com.siloyou.core.common.service.CrudService;
import com.siloyou.jmsg.modules.mms.entity.JmsgMmsReport;
import com.siloyou.jmsg.modules.mms.dao.JmsgMmsReportDao;

/**
 * 状态报告Service
 * @author zhukc
 * @version 2016-05-28
 */
@Service
@Transactional(readOnly = true)
public class JmsgMmsReportService extends CrudService<JmsgMmsReportDao, JmsgMmsReport> {
	
	@Autowired
	private SqlSessionFactory sqlSessionFactory;

	public JmsgMmsReport get(String id) {
		return super.get(id);
	}
	
	public List<JmsgMmsReport> findList(JmsgMmsReport jmsgMmsReport) {
		return super.findList(jmsgMmsReport);
	}
	
	public Page<JmsgMmsReport> findPage(Page<JmsgMmsReport> page, JmsgMmsReport jmsgMmsReport) {
		return super.findPage(page, jmsgMmsReport);
	}
	
	@Transactional(readOnly = false)
	public void save(JmsgMmsReport jmsgMmsReport) {
		super.save(jmsgMmsReport);
	}
	
	@Transactional(readOnly = false)
	public void delete(JmsgMmsReport jmsgMmsReport) {
		super.delete(jmsgMmsReport);
	}
	
	public JmsgMmsReport getByBizid(String bizid) {
		return dao.findByBizid(bizid);
	}
	
	public JmsgMmsReport getByMsgid(String msgid) {
		return dao.findByMsgid(msgid);
	}
}