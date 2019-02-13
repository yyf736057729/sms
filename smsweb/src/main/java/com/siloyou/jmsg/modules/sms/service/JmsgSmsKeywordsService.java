/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.sms.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sanerzone.common.support.dubbo.spring.annotation.DubboReference;
import com.sanerzone.smscenter.config.SmsConfigInterface;
import com.siloyou.core.common.persistence.Page;
import com.siloyou.core.common.service.CrudService;
import com.siloyou.jmsg.modules.sms.entity.JmsgSmsKeywords;
import com.siloyou.jmsg.common.utils.KeywordsUtils;
import com.siloyou.jmsg.modules.sms.dao.JmsgSmsKeywordsDao;

/**
 * 敏感词Service
 * @author zhukc
 * @version 2016-05-18
 */
@Service
@Transactional(readOnly = true)
public class JmsgSmsKeywordsService extends CrudService<JmsgSmsKeywordsDao, JmsgSmsKeywords> {

	@DubboReference
	private SmsConfigInterface smsConfig;
	
	public JmsgSmsKeywords get(String id) {
		return super.get(id);
	}
	
	public List<JmsgSmsKeywords> findList(JmsgSmsKeywords jmsgSmsKeywords) {
		return super.findList(jmsgSmsKeywords);
	}
	
	public Page<JmsgSmsKeywords> findPage(Page<JmsgSmsKeywords> page, JmsgSmsKeywords jmsgSmsKeywords) {
		return super.findPage(page, jmsgSmsKeywords);
	}
	
	@Transactional(readOnly = false)
	public void save(JmsgSmsKeywords jmsgSmsKeywords) {
		String value = jmsgSmsKeywords.getKeywords().trim();
		jmsgSmsKeywords.setKeywords(value);
		super.save(jmsgSmsKeywords);
		KeywordsUtils.put(value);
		smsConfig.configKeyWords(1, value);
	}
	
	@Transactional(readOnly = false)
	public void delete(JmsgSmsKeywords jmsgSmsKeywords) {
		super.delete(jmsgSmsKeywords);
		smsConfig.configKeyWords(2, jmsgSmsKeywords.getKeywords().trim());
	}
	
	public JmsgSmsKeywords getByKeywords(String keywords) {
		return dao.getByKeywords(keywords);
	}
	
}