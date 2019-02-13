/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.sanerzone.common.modules.smscenter.dao;

import java.util.List;

import com.sanerzone.common.modules.smscenter.entity.JmsgSmsKeywords;
import com.sanerzone.common.support.persistence.CrudDao;
import com.sanerzone.common.support.persistence.annotation.MyBatisDao;

/**
 * 敏感词DAO接口
 * @author zhukc
 * @version 2016-05-18
 */
@MyBatisDao
public interface JmsgSmsKeywordsDao extends CrudDao<JmsgSmsKeywords> {
	public JmsgSmsKeywords getByKeywords(String keywords);
	public void deleteByKeys(String keywords);
	public List<String> findJmsgSmsKeywords();
}