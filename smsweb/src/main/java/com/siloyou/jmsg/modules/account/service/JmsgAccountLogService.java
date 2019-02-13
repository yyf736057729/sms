/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.account.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.siloyou.core.common.persistence.Page;
import com.siloyou.core.common.service.CrudService;
import com.siloyou.core.modules.sys.entity.User;
import com.siloyou.jmsg.modules.account.entity.JmsgAccountLog;
import com.siloyou.jmsg.modules.account.dao.JmsgAccountLogDao;

/**
 * 资金变动日志Service
 * @author zhukc
 * @version 2016-05-17
 */
@Service
@Transactional(readOnly = true)
public class JmsgAccountLogService extends CrudService<JmsgAccountLogDao, JmsgAccountLog> {

	public JmsgAccountLog get(String id) {
		return super.get(id);
	}
	
	public List<JmsgAccountLog> findList(JmsgAccountLog jmsgAccountLog) {
		return super.findList(jmsgAccountLog);
	}
	
	public Page<JmsgAccountLog> findPage(Page<JmsgAccountLog> page, JmsgAccountLog jmsgAccountLog) {
		return super.findPage(page, jmsgAccountLog);
	}
	
	public Page<JmsgAccountLog> findDetailPage(Page<JmsgAccountLog> page, JmsgAccountLog jmsgAccountLog) {
		jmsgAccountLog.setPage(page);
		logger.info(xmlName + "===>>>findAccountLogDetailList");
		page.setList(dao.findAccountLogDetailList(jmsgAccountLog));
		return page;
	}
	
	@Transactional(readOnly = false)
	public void save(JmsgAccountLog jmsgAccountLog) {
		super.save(jmsgAccountLog);
	}
	
	@Transactional(readOnly = false)
	public void delete(JmsgAccountLog jmsgAccountLog) {
		super.delete(jmsgAccountLog);
	}
	
	@Transactional(readOnly = false)
	public void save(String userId, String changeType,long money,String appType,String remark, String createUserId, String orderId){
		JmsgAccountLog entity = new JmsgAccountLog();
		User user = new User();
		user.setId(userId);
		entity.setUser(user);
		entity.setChangeType(changeType);
		entity.setMoney(money);
		entity.setAppType(appType);
		entity.setRemark(remark);
		User createBy = new User();
		createBy.setId(createUserId);
		entity.setCreateBy(createBy);
		entity.setOrderId(orderId);
		dao.insert(entity);
	}
	
	
}