/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.sanerzone.common.modules.smscenter.dao;

import java.util.List;

import com.sanerzone.common.modules.smscenter.entity.JmsgGatewaySign;
import com.sanerzone.common.support.persistence.CrudDao;
import com.sanerzone.common.support.persistence.annotation.MyBatisDao;

/**
 * 通道签名DAO接口
 * @author zhukc
 * @version 2016-07-29
 */
@MyBatisDao
public interface JmsgGatewaySignDao extends CrudDao<JmsgGatewaySign> {
	public JmsgGatewaySign getByParam(JmsgGatewaySign param);
	
	/**
	 * 获取用户通道签名
	 * @param param
	 * @return
	 * @see [类、类#方法、类#成员]
	 */
	public List<JmsgGatewaySign> getUserGatewaySign(JmsgGatewaySign param);
	
	/**
	 * 账户下面已分配通道分组下的已经分配好的签名信息
	 * @param param
	 * @return
	 * @see [类、类#方法、类#成员]
	 */
	public List<JmsgGatewaySign> getUserGatewaySingList(JmsgGatewaySign param);
}