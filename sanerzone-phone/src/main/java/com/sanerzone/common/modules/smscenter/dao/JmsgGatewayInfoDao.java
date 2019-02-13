/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.sanerzone.common.modules.smscenter.dao;

import java.util.List;
import java.util.Map;

import com.sanerzone.common.modules.smscenter.entity.JmsgGatewayInfo;
import com.sanerzone.common.support.persistence.CrudDao;
import com.sanerzone.common.support.persistence.annotation.MyBatisDao;

/**
 * 通道信息DAO接口
 * @author zhukc
 * @version 2016-07-29
 */
@MyBatisDao
public interface JmsgGatewayInfoDao extends CrudDao<JmsgGatewayInfo> {
	
//	public List<Dict> findLabelValue();
	
	public void updateStatusByPrimaryKey(Map<String, String> param);
	
	public void updateGatewayState(Map<String, String> param);
}