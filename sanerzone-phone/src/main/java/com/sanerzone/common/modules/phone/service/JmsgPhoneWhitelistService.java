/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.sanerzone.common.modules.phone.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sanerzone.common.modules.phone.dao.JmsgPhoneWhitelistDao;
import com.sanerzone.common.modules.phone.entity.JmsgPhoneBlacklist;
import com.sanerzone.common.modules.phone.utils.WhitelistUtils;
import com.sanerzone.common.support.persistence.CrudService;
import com.sanerzone.common.support.persistence.Page;
import com.sanerzone.common.support.utils.StringUtils;

/**
 * 白名单Service
 * @author zhukc
 * @version 2016-05-18
 */
@Service
@Transactional(readOnly = true)
public class JmsgPhoneWhitelistService extends CrudService<JmsgPhoneWhitelistDao, JmsgPhoneBlacklist> {
	
	@Autowired
	private JmsgPhoneBlacklistService jmsgPhoneBlacklistService;
	
	@Autowired
	private JmsgPhoneService jmsgPhoneService;
	
	public JmsgPhoneBlacklist get(String id) {
		return super.get(id);
	}
	
	public List<JmsgPhoneBlacklist> findList(JmsgPhoneBlacklist jmsgPhoneBlacklist) {
		return super.findList(jmsgPhoneBlacklist);
	}
	
	public Page<JmsgPhoneBlacklist> findPage(Page<JmsgPhoneBlacklist> page, JmsgPhoneBlacklist jmsgPhoneBlacklist) {
		return super.findPage(page, jmsgPhoneBlacklist);
	}
	
	@Transactional(readOnly = false)
	public void save(JmsgPhoneBlacklist jmsgPhoneBlacklist) {
		if(StringUtils.isNotBlank(jmsgPhoneBlacklist.getPhone())){
			String[] phones = jmsgPhoneBlacklist.getPhone().split("\r\n");
			if(phones != null && phones.length >0){
				JmsgPhoneBlacklist param;
				for (String phone : phones) {
					param = new JmsgPhoneBlacklist();
					param.setPhone(phone);
					param.setScope("0");
					deleteByPhone(phone);
					jmsgPhoneBlacklistService.deleteByPhone(phone);
					jmsgPhoneService.deleteByPhone(phone);
					WhitelistUtils.put(phone);
					super.save(param);
				}
			}
		}
	}
	
	@Transactional(readOnly = false)
	public void delete(JmsgPhoneBlacklist jmsgPhoneBlacklist) {
		super.delete(jmsgPhoneBlacklist);
	}
	
	public JmsgPhoneBlacklist getByPhone(String phone) {
		return dao.getByPhone(phone);
	}
	
	@Transactional(readOnly = false)
	public void deleteByPhone(String phone) {
		dao.deleteByPhone(phone);
		WhitelistUtils.del(phone);
	}
	
}