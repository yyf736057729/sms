/**
 * Copyright &copy; 2015-2016 SanerZone All rights reserved.
 */
package com.sanerzone.common.modules.phone.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sanerzone.common.modules.phone.dao.SmsPhoneWhitelistDao;
import com.sanerzone.common.modules.phone.entity.SmsPhoneWhitelist;
import com.sanerzone.common.modules.phone.utils.WhitelistUtils;
import com.sanerzone.common.support.persistence.CrudService;
import com.sanerzone.common.support.persistence.Page;

/**
 * 系统白名单Service
 * @author zhukc
 * @version 2017-03-10
 */
@Service
@Transactional(readOnly = true)
public class SmsPhoneWhitelistService extends CrudService<SmsPhoneWhitelistDao, SmsPhoneWhitelist> {

	public SmsPhoneWhitelist get(String id) {
		return super.get(id);
	}
	
	public List<SmsPhoneWhitelist> findList(SmsPhoneWhitelist smsPhoneWhitelist) {
		return super.findList(smsPhoneWhitelist);
	}
	
	public Page<SmsPhoneWhitelist> findPage(Page<SmsPhoneWhitelist> page, SmsPhoneWhitelist smsPhoneWhitelist) {
		return super.findPage(page, smsPhoneWhitelist);
	}
	
	@Transactional(readOnly = false)
	public void save(SmsPhoneWhitelist smsPhoneWhitelist,String userid) {
		SmsPhoneWhitelist param;
		String[] phones = smsPhoneWhitelist.getPhone().split("\r\n");
		if(phones != null && phones.length > 0){
			for (String phone : phones) {
				param = new SmsPhoneWhitelist();
				param.setPhone(phone);
				param.setScope("0");
				param.setCreateBy(userid);
				dao.delete(param);
				super.save(param);
			}
			
			//TODO 系统通知
			WhitelistUtils.put(phones);
		}
	}
	
	@Transactional(readOnly = false)
	public void delete(SmsPhoneWhitelist smsPhoneWhitelist) {
		super.delete(smsPhoneWhitelist);
		WhitelistUtils.del(smsPhoneWhitelist.getPhone());//TODO 系统通知
	}
	
}