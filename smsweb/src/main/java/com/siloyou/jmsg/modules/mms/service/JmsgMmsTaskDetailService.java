/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.mms.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.siloyou.core.common.persistence.Page;
import com.siloyou.core.common.service.CrudService;
import com.siloyou.jmsg.modules.mms.entity.JmsgMmsTaskDetail;
import com.siloyou.jmsg.modules.mms.dao.JmsgMmsTaskDetailDao;

/**
 * 彩信发送明细Service
 * @author zhukc
 * @version 2016-05-20
 */
@Service
@Transactional(readOnly = true)
public class JmsgMmsTaskDetailService extends CrudService<JmsgMmsTaskDetailDao, JmsgMmsTaskDetail> {

	public JmsgMmsTaskDetail get(String id) {
		return super.get(id);
	}
	
	public List<JmsgMmsTaskDetail> findList(JmsgMmsTaskDetail jmsgMmsTaskDetail) {
		return super.findList(jmsgMmsTaskDetail);
	}
	
	public Page<JmsgMmsTaskDetail> findPage(Page<JmsgMmsTaskDetail> page, JmsgMmsTaskDetail jmsgMmsTaskDetail) {
		return super.findPage(page, jmsgMmsTaskDetail);
	}
	
	@Transactional(readOnly = false)
	public void save(JmsgMmsTaskDetail jmsgMmsTaskDetail) {
		super.save(jmsgMmsTaskDetail);
	}
	
	@Transactional(readOnly = false)
	public void delete(JmsgMmsTaskDetail jmsgMmsTaskDetail) {
		super.delete(jmsgMmsTaskDetail);
	}
	
	//获取手机号码
	public List<String> findPhone(String taskId){
		return dao.findPhoneByTaskId(taskId);
	}
	
	public JmsgMmsTaskDetail getByTaskIdAndPhone(JmsgMmsTaskDetail entity){
		return dao.findByTaskIdAndPhone(entity);
	}
	
}