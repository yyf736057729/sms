/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.mms.dao;

import java.util.List;

import com.siloyou.core.common.persistence.CrudDao;
import com.siloyou.core.common.persistence.annotation.MyBatisDao;
import com.siloyou.jmsg.modules.mms.entity.JmsgMmsSubmit;

/**
 * 网关状态DAO接口
 * @author zhukc
 * @version 2016-05-28
 */
@MyBatisDao
public interface JmsgMmsSubmitDao extends CrudDao<JmsgMmsSubmit> {
	public JmsgMmsSubmit findByBizid(String bizid);
	public Integer findSuccessCount(String taskid);
	public List<JmsgMmsSubmit> findDetailSendList(String taskid);
	
}