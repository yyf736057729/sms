/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.sms.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.siloyou.core.common.persistence.Page;
import com.siloyou.core.common.service.CrudService;
import com.siloyou.jmsg.modules.sms.entity.JmsgSmsUserAttr;
import com.siloyou.jmsg.modules.sms.dao.JmsgSmsUserAttrDao;

/**
 * 用户短信属性Service
 * @author zhukc
 * @version 2016-05-18
 */
@Service
@Transactional(readOnly = true)
public class JmsgSmsUserAttrService extends CrudService<JmsgSmsUserAttrDao, JmsgSmsUserAttr> {

	public JmsgSmsUserAttr get(String id) {
		return super.get(id);
	}
	
	public List<JmsgSmsUserAttr> findList(JmsgSmsUserAttr jmsgSmsUserAttr) {
		return super.findList(jmsgSmsUserAttr);
	}
	
	public Page<JmsgSmsUserAttr> findPage(Page<JmsgSmsUserAttr> page, JmsgSmsUserAttr jmsgSmsUserAttr) {
		return super.findPage(page, jmsgSmsUserAttr);
	}
	
	@Transactional(readOnly = false)
	public void save(JmsgSmsUserAttr jmsgSmsUserAttr) {
		super.save(jmsgSmsUserAttr);
	}
	
	@Transactional(readOnly = false)
	public void delete(JmsgSmsUserAttr jmsgSmsUserAttr) {
		super.delete(jmsgSmsUserAttr);
	}
	
}