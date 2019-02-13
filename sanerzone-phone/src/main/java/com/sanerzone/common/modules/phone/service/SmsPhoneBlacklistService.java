/**
 * Copyright &copy; 2015-2016 SanerZone All rights reserved.
 */
package com.sanerzone.common.modules.phone.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sanerzone.common.modules.phone.dao.SmsPhoneBlacklistDao;
import com.sanerzone.common.modules.phone.entity.SmsPhoneBlacklist;
import com.sanerzone.common.modules.phone.utils.BlacklistUtils;
import com.sanerzone.common.support.persistence.CrudService;
import com.sanerzone.common.support.persistence.Page;

/**
 * 系统黑名单Service
 * @author zhukc
 * @version 2017-03-10
 */
@Service
@Transactional(readOnly = true)
public class SmsPhoneBlacklistService extends CrudService<SmsPhoneBlacklistDao, SmsPhoneBlacklist> {
	
	public SmsPhoneBlacklist get(String id) {
		return super.get(id);
	}
	
	public List<SmsPhoneBlacklist> findList(SmsPhoneBlacklist smsPhoneBlacklist) {
		return super.findList(smsPhoneBlacklist);
	}
	
	public Page<SmsPhoneBlacklist> findPage(Page<SmsPhoneBlacklist> page, SmsPhoneBlacklist smsPhoneBlacklist) {
		return super.findPage(page, smsPhoneBlacklist);
	}
	
	@Transactional(readOnly = false)
	public void save(SmsPhoneBlacklist smsPhoneBlacklist,String userid) {
		SmsPhoneBlacklist param;
		String[] phones = smsPhoneBlacklist.getPhone().split("\r\n");
		if(phones != null && phones.length > 0){
			for (String phone : phones) {
				param = new SmsPhoneBlacklist();
				param.setPhone(phone);
				param.setScope("0");
				param.setCreateBy(userid);
				dao.delete(param);
				super.save(param);
			}
			
			//TODO 系统通知
			BlacklistUtils.put(phones, 0, 0);
		}
		
	}
	
	@Transactional(readOnly = false)
	public void delete(SmsPhoneBlacklist smsPhoneBlacklist) {
		super.delete(smsPhoneBlacklist);
		BlacklistUtils.del(smsPhoneBlacklist.getPhone(),0);//TODO 系统通知
	}
	
}