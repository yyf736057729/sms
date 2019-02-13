/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.sms.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.siloyou.core.common.persistence.Page;
import com.siloyou.core.common.service.CrudService;
import com.siloyou.jmsg.modules.sms.entity.JmsgSmsWarn;
import com.siloyou.jmsg.modules.sms.dao.JmsgSmsWarnDao;

/**
 * 告警表Service
 * @author zj
 * @version 2016-10-15
 */
@Service
@Transactional(readOnly = true)
public class JmsgSmsWarnService extends CrudService<JmsgSmsWarnDao, JmsgSmsWarn> {

	public JmsgSmsWarn get(String id) {
		return super.get(id);
	}
	
	public List<JmsgSmsWarn> findList(JmsgSmsWarn jmsgSmsWarn) {
		return super.findList(jmsgSmsWarn);
	}
	
	public Page<JmsgSmsWarn> findPage(Page<JmsgSmsWarn> page, JmsgSmsWarn jmsgSmsWarn) {
		return super.findPage(page, jmsgSmsWarn);
	}
	
	@Transactional(readOnly = false)
	public void save(JmsgSmsWarn jmsgSmsWarn) {
		super.save(jmsgSmsWarn);
	}
	
	@Transactional(readOnly = false)
	public void delete(JmsgSmsWarn jmsgSmsWarn) {
		super.delete(jmsgSmsWarn);
	}
	
	/**
     * 根据ID修改告警处理状态 
     * @param map
     * @see [类、类#方法、类#成员]
     */
	@Transactional(readOnly = false)
    public void updateStatusById(JmsgSmsWarn jmsgSmsWarn)
	{
	    Map<String, Object> map = new HashMap<String, Object>();
	    map.put("id", jmsgSmsWarn.getId());
	    map.put("warnStatus", jmsgSmsWarn.getWarnStatus());
	    dao.updateStatusById(map);
	}
	
}