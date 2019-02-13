/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.sanerzone.common.modules.smscenter.dao;

import java.util.List;

import com.sanerzone.common.modules.smscenter.entity.JmsgGatewayGroup;
import com.sanerzone.common.support.persistence.CrudDao;
import com.sanerzone.common.support.persistence.annotation.MyBatisDao;

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