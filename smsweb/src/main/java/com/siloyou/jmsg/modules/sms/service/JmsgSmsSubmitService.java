/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.sms.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.siloyou.core.common.persistence.Page;
import com.siloyou.core.common.service.CrudService;
import com.siloyou.jmsg.modules.sms.entity.JmsgSmsSubmit;
import com.siloyou.jmsg.modules.sms.dao.JmsgSmsSubmitDao;

/**
 * 网关状态Service
 * @author zhukc
 * @version 2016-08-05
 */
@Service
@Transactional(readOnly = true)
public class JmsgSmsSubmitService extends CrudService<JmsgSmsSubmitDao, JmsgSmsSubmit> {

	public JmsgSmsSubmit get(String id) {
		return super.get(id);
	}
	
	public List<JmsgSmsSubmit> findList(JmsgSmsSubmit jmsgSmsSubmit) {
		return super.findList(jmsgSmsSubmit);
	}
	
	public Page<JmsgSmsSubmit> findPage(Page<JmsgSmsSubmit> page, JmsgSmsSubmit jmsgSmsSubmit) {
		return super.findPage(page, jmsgSmsSubmit);
	}
	
	@Transactional(readOnly = false)
	public void save(JmsgSmsSubmit jmsgSmsSubmit) {
		super.save(jmsgSmsSubmit);
	}
	
	@Transactional(readOnly = false)
	public void delete(JmsgSmsSubmit jmsgSmsSubmit) {
		super.delete(jmsgSmsSubmit);
	}
	
	/**
	 * 获取需要重发的数据
	 * @param map
	 * @return
	 * @see [类、类#方法、类#成员]
	 */
	public Page<JmsgSmsSubmit> findErrorForReSend(Page<JmsgSmsSubmit> page, JmsgSmsSubmit jmsgSmsSubmit)
	{
	    jmsgSmsSubmit.setPage(page);
	    page.setList(dao.findErrorForReSend(jmsgSmsSubmit));
	    return page;
	}
	
	public List<JmsgSmsSubmit> findListV2(JmsgSmsSubmit record)
	{
		return dao.findListV2(record);
	}
}