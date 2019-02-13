/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.sms.service;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sanerzone.common.support.dubbo.spring.annotation.DubboReference;
import com.sanerzone.smscenter.config.SmsConfigInterface;
import com.siloyou.core.common.persistence.Page;
import com.siloyou.core.common.service.CrudService;
import com.siloyou.core.common.utils.StringUtils;
import com.siloyou.jmsg.common.utils.GroupUtils;
import com.siloyou.jmsg.modules.sms.dao.JmsgGroupDao;
import com.siloyou.jmsg.modules.sms.entity.JmsgGroup;

/**
 * 分组信息Service
 * @author zhukc
 * @version 2016-07-29
 */
@Service
@Transactional(readOnly = true)
public class JmsgGroupService extends CrudService<JmsgGroupDao, JmsgGroup> {

	@DubboReference
	private SmsConfigInterface smsConfig;
	
	public JmsgGroup get(String id) {
		return super.get(id);
	}
	
	public JmsgGroup getByParam(String name) {
		return dao.getByParam(name);
	}
	
	public List<JmsgGroup> findList(JmsgGroup jmsgGroup) {
		return super.findList(jmsgGroup);
	}
	
	public Page<JmsgGroup> findPage(Page<JmsgGroup> page, JmsgGroup jmsgGroup) {
		return super.findPage(page, jmsgGroup);
	}
	
	@Transactional(readOnly = false)
	public void save(JmsgGroup jmsgGroup) {
		super.save(jmsgGroup);
		GroupUtils.put(jmsgGroup);
		com.sanerzone.common.modules.smscenter.entity.JmsgGroup dest = new com.sanerzone.common.modules.smscenter.entity.JmsgGroup();
		try {
			BeanUtils.copyProperties(dest, jmsgGroup);
		} catch (IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}
		smsConfig.configGroup(1, dest, "");
	}
	
	@Transactional(readOnly = false)
	public void delete(JmsgGroup jmsgGroup) {
		super.delete(jmsgGroup);
	}
	
	@Transactional(readOnly = false)
	public void updateStatus(JmsgGroup jmsgGroup) {
		dao.update(jmsgGroup);
		
		GroupUtils.put(jmsgGroup);
		
		com.sanerzone.common.modules.smscenter.entity.JmsgGroup dest = new com.sanerzone.common.modules.smscenter.entity.JmsgGroup();
		try {
			BeanUtils.copyProperties(dest, jmsgGroup);
		} catch (IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}
		smsConfig.configGroup(1, dest, "");
	}
	
}