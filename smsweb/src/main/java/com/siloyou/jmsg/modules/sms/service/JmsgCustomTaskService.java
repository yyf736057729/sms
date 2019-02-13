/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.sms.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.siloyou.core.common.persistence.Page;
import com.siloyou.core.common.service.CrudService;
import com.siloyou.jmsg.modules.sms.entity.JmsgCustomTask;
import com.siloyou.jmsg.modules.sms.dao.JmsgCustomTaskDao;

/**
 * 自定义任务Service
 * @author zj
 * @version 2017-04-07
 */
@Service
@Transactional(readOnly = true)
public class JmsgCustomTaskService extends CrudService<JmsgCustomTaskDao, JmsgCustomTask> {

	public JmsgCustomTask get(String id) {
		return super.get(id);
	}
	
	public List<JmsgCustomTask> findList(JmsgCustomTask jmsgCustomTask) {
		return super.findList(jmsgCustomTask);
	}
	
	public Page<JmsgCustomTask> findPage(Page<JmsgCustomTask> page, JmsgCustomTask jmsgCustomTask) {
		return super.findPage(page, jmsgCustomTask);
	}
	
	@Transactional(readOnly = false)
	public void save(JmsgCustomTask jmsgCustomTask) {
		super.save(jmsgCustomTask);
	}
	
	@Transactional(readOnly = false)
	public void delete(JmsgCustomTask jmsgCustomTask) {
		super.delete(jmsgCustomTask);
	}
	
	@Transactional(readOnly = false)
	public void updateParam(JmsgCustomTask jmsgCustomTask){
		dao.updateParam(jmsgCustomTask);
	}
	
}