/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.sanerzone.common.modules.phone.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sanerzone.common.modules.phone.dao.JmsgPhoneBlacklistDao;
import com.sanerzone.common.modules.phone.entity.JmsgPhoneBlacklist;
import com.sanerzone.common.support.dubbo.spring.annotation.DubboReference;
import com.sanerzone.common.support.persistence.CrudService;
import com.sanerzone.common.support.persistence.Page;
import com.sanerzone.common.support.utils.StringUtils;
import com.sanerzone.smscenter.config.PhoneConfigInterface;

/**
 * 黑名单Service
 * @author zhukc
 * @version 2016-05-18
 */
@Service
@Transactional(readOnly = true)
public class JmsgPhoneBlacklistService extends CrudService<JmsgPhoneBlacklistDao, JmsgPhoneBlacklist> {

	@DubboReference
	private PhoneConfigInterface phoneConfig;
	
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
				for(String phone:phones){
					param = new JmsgPhoneBlacklist();
					param.setPhone(phone);
					param.setScope("0");
					deleteByPhone(phone);
					super.save(param);
				}
				phoneConfig.configBlacklist(1,"",0,phones);
			}
		}
	}
	
	@Transactional(readOnly = false)
	public void delete(JmsgPhoneBlacklist jmsgPhoneBlacklist) {
		phoneConfig.configBlacklist(2,jmsgPhoneBlacklist.getPhone(),0,null);
		super.delete(jmsgPhoneBlacklist);
	}
	
	public JmsgPhoneBlacklist getByPhone(String phone) {
		return dao.getByPhone(phone);
	}
	
	@Transactional(readOnly = false)
	public void deleteByPhone(String phone){
		dao.deleteByPhone(phone);
		phoneConfig.configBlacklist(2,phone,0,null);
	}
	
}