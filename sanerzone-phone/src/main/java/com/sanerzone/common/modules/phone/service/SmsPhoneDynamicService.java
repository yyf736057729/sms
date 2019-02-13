/**
 * Copyright &copy; 2015-2016 SanerZone All rights reserved.
 */
package com.sanerzone.common.modules.phone.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sanerzone.common.modules.phone.dao.SmsPhoneDynamicDao;
import com.sanerzone.common.modules.phone.entity.SmsPhoneDynamic;
import com.sanerzone.common.modules.phone.utils.BlacklistUtils;
import com.sanerzone.common.support.persistence.CrudService;
import com.sanerzone.common.support.persistence.Page;

/**
 * 营销黑名单Service
 * @author zhukc
 * @version 2017-03-10
 */
@Service
@Transactional(readOnly = true)
public class SmsPhoneDynamicService extends CrudService<SmsPhoneDynamicDao, SmsPhoneDynamic> {

	public SmsPhoneDynamic get(String id) {
		return super.get(id);
	}
	
	public List<SmsPhoneDynamic> findList(SmsPhoneDynamic smsPhoneDynamic) {
		return super.findList(smsPhoneDynamic);
	}
	
	public Page<SmsPhoneDynamic> findPage(Page<SmsPhoneDynamic> page, SmsPhoneDynamic smsPhoneDynamic) {
		return super.findPage(page, smsPhoneDynamic);
	}
	
	@Transactional(readOnly = false)
	public void save(SmsPhoneDynamic smsPhoneDynamic,String userid) {
		
		String[] phones = smsPhoneDynamic.getPhone().split("\r\n");
		if(phones != null && phones.length > 0){
			SmsPhoneDynamic param;
			for (String phone : phones) {
				param = new SmsPhoneDynamic();
				param.setPhone(phone);
				param.setType(smsPhoneDynamic.getType());
				param.setUserid(userid);
				param.setCreateBy(userid);
				dao.delete(param);
				super.save(param);
			}
			//TODO 系统通知
			BlacklistUtils.put(phones,1,1);
		}
	}
	
	@Transactional(readOnly = false)
	public void delete(SmsPhoneDynamic smsPhoneDynamic) {
		super.delete(smsPhoneDynamic);
		//TODO 系统通知
		BlacklistUtils.del(smsPhoneDynamic.getPhone(), 1);
	}
	
}