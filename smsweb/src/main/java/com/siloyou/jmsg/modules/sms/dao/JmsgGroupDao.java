/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.sms.dao;

import java.util.List;

import com.siloyou.core.common.persistence.CrudDao;
import com.siloyou.core.common.persistence.annotation.MyBatisDao;
import com.siloyou.core.modules.sys.entity.Dict;
import com.siloyou.jmsg.modules.sms.entity.JmsgGroup;

/**
 * 分组信息DAO接口
 * @author zhukc
 * @version 2016-07-29
 */
@MyBatisDao
public interface JmsgGroupDao extends CrudDao<JmsgGroup> {
	public void updateStatus(JmsgGroup param);
	public JmsgGroup getByParam(String name);
	public List<Dict> findLabelValue();
	public List<Dict> findLabelValueBz();
}