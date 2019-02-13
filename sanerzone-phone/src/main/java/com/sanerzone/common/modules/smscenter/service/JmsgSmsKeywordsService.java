/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.sanerzone.common.modules.smscenter.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sanerzone.common.modules.smscenter.dao.JmsgSmsKeywordsDao;
import com.sanerzone.common.modules.smscenter.entity.JmsgSmsKeywords;
import com.sanerzone.common.modules.smscenter.utils.KeywordsUtils;
import com.sanerzone.common.support.persistence.CrudService;
import com.sanerzone.common.support.persistence.Page;

/**
 * 敏感词Service
 * @author zhukc
 * @version 2016-05-18
 */
@Service
@Transactional(readOnly = true)
public class JmsgSmsKeywordsService extends CrudService<JmsgSmsKeywordsDao, JmsgSmsKeywords> {

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
	}
	
	@Transactional(readOnly = false)
	public void delete(JmsgSmsKeywords jmsgSmsKeywords) {
		super.delete(jmsgSmsKeywords);
	}
	
	public JmsgSmsKeywords getByKeywords(String keywords) {
		return dao.getByKeywords(keywords);
	}
	
}