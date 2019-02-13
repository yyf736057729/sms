/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.sanerzone.jmsg.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sanerzone.common.support.persistence.CrudService;
import com.sanerzone.common.support.persistence.Page;
import com.sanerzone.jmsg.dao.JmsgSmsSubmitSyncDao;
import com.sanerzone.jmsg.entity.JmsgSmsSubmitSync;

/**
 * 网关状态重试Service
 * @author zj
 * @version 2017-03-17
 */
@Service
@Transactional(readOnly = true)
public class JmsgSmsSubmitSyncService extends CrudService<JmsgSmsSubmitSyncDao, JmsgSmsSubmitSync> {

	public JmsgSmsSubmitSync get(String id) {
		return super.get(id);
	}
	
	public List<JmsgSmsSubmitSync> findList(JmsgSmsSubmitSync jmsgSmsSubmitSync) {
		return super.findList(jmsgSmsSubmitSync);
	}
	
	public Page<JmsgSmsSubmitSync> findPage(Page<JmsgSmsSubmitSync> page, JmsgSmsSubmitSync jmsgSmsSubmitSync) {
		return super.findPage(page, jmsgSmsSubmitSync);
	}
	
	@Transactional(readOnly = false)
	public void save(JmsgSmsSubmitSync jmsgSmsSubmitSync) {
		super.save(jmsgSmsSubmitSync);
	}
	
	@Transactional(readOnly = false)
	public void delete(JmsgSmsSubmitSync jmsgSmsSubmitSync) {
		super.delete(jmsgSmsSubmitSync);
	}
	
}