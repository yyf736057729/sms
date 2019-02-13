/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.sms.dao;

import java.util.List;

import com.siloyou.core.common.persistence.CrudDao;
import com.siloyou.core.common.persistence.annotation.MyBatisDao;
import com.siloyou.jmsg.modules.sms.entity.JmsgGatewayGroup;

/**
 * 通道分组DAO接口
 * @author zhukc
 * @version 2016-07-29
 */
@MyBatisDao
public interface JmsgGatewayGroupDao extends CrudDao<JmsgGatewayGroup> {
	public List<JmsgGatewayGroup> findGroupList(JmsgGatewayGroup jmsgGatewayGroup);
	public JmsgGatewayGroup getByParam(JmsgGatewayGroup jmsgGatewayGroup);
	public void deleteByParam(JmsgGatewayGroup jmsgGatewayGroup);
	public List<JmsgGatewayGroup> queryGatewayNoSign(JmsgGatewayGroup jmsgGatewayGroup);
}