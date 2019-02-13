/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.sms.dao;

import java.util.List;

import com.siloyou.core.common.persistence.CrudDao;
import com.siloyou.core.common.persistence.annotation.MyBatisDao;
import com.siloyou.jmsg.modules.sms.entity.JmsgSmsKeywords;

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