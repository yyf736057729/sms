/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.sms.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.siloyou.core.common.persistence.Page;
import com.siloyou.core.common.service.CrudService;
import com.siloyou.jmsg.modules.sms.entity.JmsgSmsDeliverPush;
import com.siloyou.jmsg.modules.sms.dao.JmsgSmsDeliverPushDao;

/**
 * 上行推送信息Service
 * @author zhukc
 * @version 2016-08-14
 */
@Service
@Transactional(readOnly = true)
public class JmsgSmsDeliverPushService extends CrudService<JmsgSmsDeliverPushDao, JmsgSmsDeliverPush> {

	public JmsgSmsDeliverPush get(String id) {
		return super.get(id);
	}
	
	public List<JmsgSmsDeliverPush> findList(JmsgSmsDeliverPush jmsgSmsDeliverPush) {
		return super.findList(jmsgSmsDeliverPush);
	}
	
	public List<JmsgSmsDeliverPush> findListV2(JmsgSmsDeliverPush jmsgSmsDeliverPush) {
		logger.info(xmlName + "===>>>findListNew");
		return dao.findListNew(jmsgSmsDeliverPush);
	}
	
	public Page<JmsgSmsDeliverPush> findPage(Page<JmsgSmsDeliverPush> page, JmsgSmsDeliverPush jmsgSmsDeliverPush) {
		return super.findPage(page, jmsgSmsDeliverPush);
	}
	
	public Page<JmsgSmsDeliverPush> findPageNew(Page<JmsgSmsDeliverPush> page, JmsgSmsDeliverPush jmsgSmsDeliverPush) {
		logger.info(xmlName + "===>>>findListNew");
		jmsgSmsDeliverPush.setPage(page);
		page.setList(dao.findListNew(jmsgSmsDeliverPush));
		return page;
	}
	
	@Transactional(readOnly = false)
	public void save(JmsgSmsDeliverPush jmsgSmsDeliverPush) {
		super.save(jmsgSmsDeliverPush);
	}
	
	@Transactional(readOnly = false)
	public void delete(JmsgSmsDeliverPush jmsgSmsDeliverPush) {
		super.delete(jmsgSmsDeliverPush);
	}
	
	public List<JmsgSmsDeliverPush> findExportList(JmsgSmsDeliverPush jmsgSmsDeliverPush){
		logger.info(xmlName + "===>>>findExportList");
		return dao.findExportList(jmsgSmsDeliverPush);
	}
	
}