/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.sanerzone.common.modules.phone.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sanerzone.common.modules.phone.dao.JmsgPhoneDao;
import com.sanerzone.common.modules.phone.entity.JmsgPhone;
import com.sanerzone.common.support.persistence.CrudService;
import com.sanerzone.common.support.persistence.Page;
import com.sanerzone.common.support.utils.StringUtils;

/**
 * 动态黑名单Service
 * @author zj
 * @version 2016-09-26
 */
@Service
@Transactional(readOnly = true)
public class JmsgPhoneService extends CrudService<JmsgPhoneDao, JmsgPhone> {

	
	public JmsgPhone get(String id) {
		return super.get(id);
	}
	
	public List<JmsgPhone> findList(JmsgPhone jmsgPhone) {
		return super.findList(jmsgPhone);
	}
	
	public Page<JmsgPhone> findPage(Page<JmsgPhone> page, JmsgPhone jmsgPhone) {
		return super.findPage(page, jmsgPhone);
	}
	
	@Transactional(readOnly = false)
	public void save(JmsgPhone jmsgPhone) {
		String phones = jmsgPhone.getPhone();
		//获取文本框中的发送号码
        if(StringUtils.isNotBlank(phones)){
            String[] tmp = phones.split("\r\n");
            JmsgPhone param;
            if(tmp != null && tmp.length > 0){
	            for (String phone : tmp){
	            	param = new JmsgPhone();
	            	param.setPhone(phone);
	            	param.setScope(jmsgPhone.getScope());
	            	param.setType(jmsgPhone.getType());
	            	deleteByPhone(phone);
	            	super.save(jmsgPhone);
	            }
            }
        }
	}
	
	@Transactional(readOnly = false)
	public void delete(JmsgPhone jmsgPhone) {
		super.delete(jmsgPhone);
	}
	
	@Transactional(readOnly = false)
	public void deleteByPhone(String phone){
		dao.deleteByPhone(phone);
	}
	
}