/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.mms.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.siloyou.core.common.persistence.Page;
import com.siloyou.core.common.service.CrudService;
import com.siloyou.jmsg.modules.mms.entity.JmsgMmsSubmit;
import com.siloyou.jmsg.modules.mms.dao.JmsgMmsSubmitDao;

/**
 * 网关状态Service
 * @author zhukc
 * @version 2016-05-28
 */
@Service
@Transactional(readOnly = true)
public class JmsgMmsSubmitService extends CrudService<JmsgMmsSubmitDao, JmsgMmsSubmit> {

	public JmsgMmsSubmit get(String id) {
		return super.get(id);
	}
	
	public List<JmsgMmsSubmit> findList(JmsgMmsSubmit jmsgMmsSubmit) {
		return super.findList(jmsgMmsSubmit);
	}
	
	public Page<JmsgMmsSubmit> findPage(Page<JmsgMmsSubmit> page, JmsgMmsSubmit jmsgMmsSubmit) {
		return super.findPage(page, jmsgMmsSubmit);
	}
	
	@Transactional(readOnly = false)
	public void save(JmsgMmsSubmit jmsgMmsSubmit) {
		super.save(jmsgMmsSubmit);
	}
	
	@Transactional(readOnly = false)
	public void delete(JmsgMmsSubmit jmsgMmsSubmit) {
		super.delete(jmsgMmsSubmit);
	}
	
	public JmsgMmsSubmit getByBizid(String bizid){
		return dao.findByBizid(bizid);
	}
	
}