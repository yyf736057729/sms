/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.sms.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.siloyou.core.common.persistence.Page;
import com.siloyou.core.common.service.CrudService;
import com.siloyou.jmsg.modules.sms.entity.JmsgSmsMmsdown;
import com.siloyou.jmsg.modules.sms.dao.JmsgSmsMmsdownDao;

/**
 * 短信彩信下载Service
 * @author zhukc
 * @version 2017-05-23
 */
@Service
@Transactional(readOnly = true)
public class JmsgSmsMmsdownService extends CrudService<JmsgSmsMmsdownDao, JmsgSmsMmsdown> {

	public JmsgSmsMmsdown get(String id) {
		return super.get(id);
	}
	
	public List<JmsgSmsMmsdown> findList(JmsgSmsMmsdown jmsgSmsMmsdown) {
		return super.findList(jmsgSmsMmsdown);
	}
	
	public Page<JmsgSmsMmsdown> findPage(Page<JmsgSmsMmsdown> page, JmsgSmsMmsdown jmsgSmsMmsdown) {
		return super.findPage(page, jmsgSmsMmsdown);
	}
	
	@Transactional(readOnly = false)
	public void save(JmsgSmsMmsdown jmsgSmsMmsdown) {
		super.save(jmsgSmsMmsdown);
	}
	
	@Transactional(readOnly = false)
	public void delete(JmsgSmsMmsdown jmsgSmsMmsdown) {
		super.delete(jmsgSmsMmsdown);
	}
	
}