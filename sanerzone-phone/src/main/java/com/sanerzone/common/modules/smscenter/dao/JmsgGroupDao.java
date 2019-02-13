/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.sanerzone.common.modules.smscenter.dao;

import java.util.List;

import com.sanerzone.common.modules.smscenter.entity.JmsgGroup;
import com.sanerzone.common.support.persistence.CrudDao;
import com.sanerzone.common.support.persistence.annotation.MyBatisDao;

/**
 * 分组信息DAO接口
 * @author zhukc
 * @version 2016-07-29
 */
@MyBatisDao
public interface JmsgGroupDao extends CrudDao<JmsgGroup> {
	public void updateStatus(JmsgGroup param);
	public JmsgGroup getByParam(String name);
//	public List<Dict> findLabelValue();
}