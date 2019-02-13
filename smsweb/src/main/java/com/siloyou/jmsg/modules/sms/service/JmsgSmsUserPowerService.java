/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.sms.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.siloyou.core.common.persistence.Page;
import com.siloyou.core.common.service.CrudService;
import com.siloyou.jmsg.modules.sms.entity.JmsgSmsUserPower;
import com.siloyou.jmsg.modules.sms.dao.JmsgSmsUserPowerDao;

/**
 * 用户短信能力Service
 * @author zhukc
 * @version 2016-05-18
 */
@Service
@Transactional(readOnly = true)
public class JmsgSmsUserPowerService extends CrudService<JmsgSmsUserPowerDao, JmsgSmsUserPower> {

	public JmsgSmsUserPower get(String id) {
		return super.get(id);
	}
	
	public List<JmsgSmsUserPower> findList(JmsgSmsUserPower jmsgSmsUserPower) {
		return super.findList(jmsgSmsUserPower);
	}
	
	public Page<JmsgSmsUserPower> findPage(Page<JmsgSmsUserPower> page, JmsgSmsUserPower jmsgSmsUserPower) {
		return super.findPage(page, jmsgSmsUserPower);
	}
	
	@Transactional(readOnly = false)
	public void save(JmsgSmsUserPower jmsgSmsUserPower) {
		super.save(jmsgSmsUserPower);
	}
	
	@Transactional(readOnly = false)
	public void delete(JmsgSmsUserPower jmsgSmsUserPower) {
		super.delete(jmsgSmsUserPower);
	}
	
}