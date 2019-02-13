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
import com.siloyou.jmsg.modules.sms.entity.JmsgPhoneType;
import com.siloyou.jmsg.common.utils.PhoneTypeUtils;
import com.siloyou.jmsg.modules.sms.dao.JmsgPhoneTypeDao;

/**
 * 号码运营商Service
 * @author zhukc
 * @version 2016-05-18
 */
@Service
@Transactional(readOnly = true)
public class JmsgPhoneTypeService extends CrudService<JmsgPhoneTypeDao, JmsgPhoneType> {

	public JmsgPhoneType get(String id) {
		return super.get(id);
	}
	
	public List<JmsgPhoneType> findList(JmsgPhoneType jmsgPhoneType) {
		return super.findList(jmsgPhoneType);
	}
	
	public Page<JmsgPhoneType> findPage(Page<JmsgPhoneType> page, JmsgPhoneType jmsgPhoneType) {
		return super.findPage(page, jmsgPhoneType);
	}
	
	@Transactional(readOnly = false)
	public void save(JmsgPhoneType jmsgPhoneType) {
		super.save(jmsgPhoneType);
		if(StringUtils.isNotBlank(jmsgPhoneType.getOldNum())){
			PhoneTypeUtils.remove(jmsgPhoneType.getOldNum());
		}
		
		PhoneTypeUtils.put(jmsgPhoneType.getNum(), jmsgPhoneType.getPhoneType());
		
	}
	
	@Transactional(readOnly = false)
	public void delete(JmsgPhoneType jmsgPhoneType) {
		super.delete(jmsgPhoneType);
	}
	
	public JmsgPhoneType getByNum(String num) {
		return dao.getByNum(num);
	}
	
}