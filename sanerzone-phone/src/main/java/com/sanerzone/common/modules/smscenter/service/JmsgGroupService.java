/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.sanerzone.common.modules.smscenter.service;

import com.sanerzone.common.modules.smscenter.dao.JmsgGroupDao;
import com.sanerzone.common.modules.smscenter.entity.JmsgGroup;
import com.sanerzone.common.modules.smscenter.utils.GroupUtils;
import com.sanerzone.common.support.dubbo.spring.annotation.DubboReference;
import com.sanerzone.common.support.mapper.JsonMapper;
import com.sanerzone.common.support.persistence.CrudService;
import com.sanerzone.common.support.persistence.Page;
import com.sanerzone.common.support.utils.StringUtils;
import com.sanerzone.smscenter.config.SmsConfigInterface;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 分组信息Service
 * @author zhukc
 * @version 2016-07-29
 */
@Service
@Transactional(readOnly = true)
public class JmsgGroupService extends CrudService<JmsgGroupDao, JmsgGroup> {

	@DubboReference
	private SmsConfigInterface gatewayGroup;
	
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
		if(StringUtils.isBlank(jmsgGroup.getId())){
			//TODO 
			System.out.println(JsonMapper.toJsonString(jmsgGroup));
			gatewayGroup.configGroup(1, jmsgGroup,jmsgGroup.getId());
		}
		//TODO 
		System.out.println(JsonMapper.toJsonString(jmsgGroup));
		gatewayGroup.configGroup(1, jmsgGroup,jmsgGroup.getId());
		
	}
	
	@Transactional(readOnly = false)
	public void delete(JmsgGroup jmsgGroup) {
		super.delete(jmsgGroup);
	}
	
	@Transactional(readOnly = false)
	public void updateStatus(JmsgGroup jmsgGroup) {
		dao.update(jmsgGroup);
		
		GroupUtils.put(jmsgGroup);
	}
	
}