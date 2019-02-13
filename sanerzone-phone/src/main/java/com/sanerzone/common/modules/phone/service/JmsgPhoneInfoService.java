/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.sanerzone.common.modules.phone.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sanerzone.common.modules.phone.dao.JmsgPhoneInfoDao;
import com.sanerzone.common.modules.phone.entity.JmsgPhoneInfo;
import com.sanerzone.common.support.persistence.CrudService;
import com.sanerzone.common.support.persistence.Page;

/**
 * 号段管理Service
 * @author zhukc
 * @version 2016-07-30
 */
@Service
@Transactional(readOnly = true)
public class JmsgPhoneInfoService extends CrudService<JmsgPhoneInfoDao, JmsgPhoneInfo> {
	
	
	public JmsgPhoneInfo get(String id) {
		return super.get(id);
	}
	
	public List<JmsgPhoneInfo> findList(JmsgPhoneInfo jmsgPhoneInfo) {
		return super.findList(jmsgPhoneInfo);
	}
	
	public Page<JmsgPhoneInfo> findPage(Page<JmsgPhoneInfo> page, JmsgPhoneInfo jmsgPhoneInfo) {
		return super.findPage(page, jmsgPhoneInfo);
	}
	
	@Transactional(readOnly = false)
	public void save(JmsgPhoneInfo jmsgPhoneInfo) {
		dao.insert(jmsgPhoneInfo);
	}
	
	@Transactional(readOnly = false)
	public void delete(JmsgPhoneInfo jmsgPhoneInfo) {
		super.delete(jmsgPhoneInfo);
	}
	
}