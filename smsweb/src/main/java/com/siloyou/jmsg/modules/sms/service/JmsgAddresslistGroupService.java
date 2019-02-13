/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.sms.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.siloyou.core.common.persistence.Page;
import com.siloyou.core.common.service.CrudService;
import com.siloyou.core.common.utils.StringUtils;
import com.siloyou.core.modules.sys.utils.UserUtils;
import com.siloyou.jmsg.modules.sms.entity.JmsgAddresslistGroup;
import com.siloyou.jmsg.modules.sms.dao.JmsgAddresslistGroupDao;

/**
 * 群组管理Service
 * @author zhukc
 * @version 2017-04-01
 */
@Service
@Transactional(readOnly = true)
public class JmsgAddresslistGroupService extends CrudService<JmsgAddresslistGroupDao, JmsgAddresslistGroup> {

	public JmsgAddresslistGroup get(String id) {
		return super.get(id);
	}
	
	public List<JmsgAddresslistGroup> findList(JmsgAddresslistGroup jmsgAddresslistGroup) {
		return super.findList(jmsgAddresslistGroup);
	}
	
	public Page<JmsgAddresslistGroup> findPage(Page<JmsgAddresslistGroup> page, JmsgAddresslistGroup jmsgAddresslistGroup) {
		return super.findPage(page, jmsgAddresslistGroup);
	}
	
	@Transactional(readOnly = false)
	public void save(JmsgAddresslistGroup jmsgAddresslistGroup) {
		JmsgAddresslistGroup parent;
		
		if(jmsgAddresslistGroup.getParent() != null && StringUtils.isNotBlank(jmsgAddresslistGroup.getParent().getId()) && !"0".equals(jmsgAddresslistGroup.getParent().getId())){
			parent = get(jmsgAddresslistGroup.getParent().getId());
		}else{
			parent = new JmsgAddresslistGroup();
			parent.setId("0");
			parent.setParentIds("");
			jmsgAddresslistGroup.setParent(parent);
		}
		jmsgAddresslistGroup.setParentIds(parent.getParentIds()+jmsgAddresslistGroup.getParent().getId()+",");
		if(StringUtils.isBlank(jmsgAddresslistGroup.getId())){
			jmsgAddresslistGroup.setCompanyId(UserUtils.getUser().getCompany().getId());
		}
		super.save(jmsgAddresslistGroup);
	}
	
	@Transactional(readOnly = false)
	public void delete(JmsgAddresslistGroup jmsgAddresslistGroup) {
		super.delete(jmsgAddresslistGroup);
	}
	
}