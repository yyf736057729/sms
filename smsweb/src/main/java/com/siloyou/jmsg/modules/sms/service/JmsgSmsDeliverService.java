/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.sms.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.siloyou.core.common.persistence.Page;
import com.siloyou.core.common.service.CrudService;
import com.siloyou.jmsg.modules.sms.entity.JmsgSmsDeliver;
import com.siloyou.jmsg.modules.sms.dao.JmsgSmsDeliverDao;

/**
 * 短信上行Service
 * @author zhukc
 * @version 2016-08-13
 */
@Service
@Transactional(readOnly = true)
public class JmsgSmsDeliverService extends CrudService<JmsgSmsDeliverDao, JmsgSmsDeliver> {

	public JmsgSmsDeliver get(String id) {
		return super.get(id);
	}
	
	public List<JmsgSmsDeliver> findList(JmsgSmsDeliver jmsgSmsDeliver) {
		return super.findList(jmsgSmsDeliver);
	}
	
	public Page<JmsgSmsDeliver> findPage(Page<JmsgSmsDeliver> page, JmsgSmsDeliver jmsgSmsDeliver) {
		return super.findPage(page, jmsgSmsDeliver);
	}
	
	@Transactional(readOnly = false)
	public void save(JmsgSmsDeliver jmsgSmsDeliver) {
		super.save(jmsgSmsDeliver);
	}
	
	@Transactional(readOnly = false)
	public void delete(JmsgSmsDeliver jmsgSmsDeliver) {
		super.delete(jmsgSmsDeliver);
	}
	
}