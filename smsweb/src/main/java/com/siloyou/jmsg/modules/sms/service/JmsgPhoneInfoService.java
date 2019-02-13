/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.sms.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sanerzone.common.support.dubbo.spring.annotation.DubboReference;
import com.sanerzone.smscenter.config.PhoneConfigInterface;
import com.siloyou.core.common.persistence.Page;
import com.siloyou.core.common.service.CrudService;
import com.siloyou.jmsg.common.utils.PhoneUtils;
import com.siloyou.jmsg.modules.sms.dao.JmsgPhoneInfoDao;
import com.siloyou.jmsg.modules.sms.entity.JmsgPhoneInfo;

/**
 * 号段管理Service
 * @author zhukc
 * @version 2016-07-30
 */
@Service
@Transactional(readOnly = true)
public class JmsgPhoneInfoService extends CrudService<JmsgPhoneInfoDao, JmsgPhoneInfo> {
	
	@DubboReference
	private PhoneConfigInterface phoneConfig; //dubbo系统广播
	
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
		PhoneUtils.put(jmsgPhoneInfo.getPhone(), jmsgPhoneInfo.getPhoneType(), jmsgPhoneInfo.getPhoneCityCode());
		phoneConfig.configPhoneSegment(1, jmsgPhoneInfo.getPhone(), jmsgPhoneInfo.getPhoneType(), jmsgPhoneInfo.getPhoneCityCode());
	}
	
	@Transactional(readOnly = false)
	public void delete(JmsgPhoneInfo jmsgPhoneInfo) {
		super.delete(jmsgPhoneInfo);
	}
	
}