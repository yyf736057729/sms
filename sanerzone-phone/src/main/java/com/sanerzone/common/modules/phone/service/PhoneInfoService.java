/**
 * Copyright &copy; 2015-2016 SanerZone All rights reserved.
 */
package com.sanerzone.common.modules.phone.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sanerzone.common.modules.phone.dao.PhoneInfoDao;
import com.sanerzone.common.modules.phone.entity.PhoneInfo;
import com.sanerzone.common.modules.phone.utils.PhoneUtils;
import com.sanerzone.common.support.persistence.CrudService;
import com.sanerzone.common.support.persistence.Page;

/**
 * 分流号段Service
 * @author zhukc
 * @version 2017-03-10
 */
@Service
@Transactional(readOnly = true)
public class PhoneInfoService extends CrudService<PhoneInfoDao, PhoneInfo> {

	public PhoneInfo get(String id) {
		return super.get(id);
	}
	
	public List<PhoneInfo> findList(PhoneInfo phoneInfo) {
		return super.findList(phoneInfo);
	}
	
	public Page<PhoneInfo> findPage(Page<PhoneInfo> page, PhoneInfo phoneInfo) {
		return super.findPage(page, phoneInfo);
	}
	
	@Transactional(readOnly = false)
	public void save(PhoneInfo phoneInfo) {
		dao.delete(phoneInfo);
		super.save(phoneInfo);
		//TODO 系统调用号段
		PhoneUtils.put(phoneInfo.getPhone(), phoneInfo.getPhoneType(), phoneInfo.getPhoneCityCode());
	}
	
	@Transactional(readOnly = false)
	public void delete(PhoneInfo phoneInfo) {
		super.delete(phoneInfo);
	}
	
}