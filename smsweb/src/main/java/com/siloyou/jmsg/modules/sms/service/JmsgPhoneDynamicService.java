/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.sms.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.siloyou.core.common.persistence.Page;
import com.siloyou.core.common.service.CrudService;
import com.siloyou.jmsg.modules.sms.entity.JmsgPhoneDynamic;
import com.siloyou.jmsg.modules.sms.dao.JmsgPhoneDynamicDao;

/**
 * 动态黑名单Service
 * @author zhukc
 * @version 2016-06-21
 */
@Service
@Transactional(readOnly = true)
public class JmsgPhoneDynamicService extends CrudService<JmsgPhoneDynamicDao, JmsgPhoneDynamic> {

	public JmsgPhoneDynamic get(String id) {
		return super.get(id);
	}
	
	public List<JmsgPhoneDynamic> findList(JmsgPhoneDynamic jmsgPhoneDynamic) {
		return super.findList(jmsgPhoneDynamic);
	}
	
	public List<String> findPhoneList(JmsgPhoneDynamic jmsgPhoneDynamic) {
		return dao.findPhoneList(jmsgPhoneDynamic);
	}
	
	public Page<JmsgPhoneDynamic> findPage(Page<JmsgPhoneDynamic> page, JmsgPhoneDynamic jmsgPhoneDynamic) {
		return super.findPage(page, jmsgPhoneDynamic);
	}
	
	@Transactional(readOnly = false)
	public void save(JmsgPhoneDynamic jmsgPhoneDynamic) {
		super.save(jmsgPhoneDynamic);
	}
	
	@Transactional(readOnly = false)
	public void delete(JmsgPhoneDynamic jmsgPhoneDynamic) {
		super.delete(jmsgPhoneDynamic);
	}
	
}