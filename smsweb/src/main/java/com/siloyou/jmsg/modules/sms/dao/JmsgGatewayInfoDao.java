/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.sms.dao;

import java.util.List;
import java.util.Map;

import com.siloyou.core.common.persistence.CrudDao;
import com.siloyou.core.common.persistence.annotation.MyBatisDao;
import com.siloyou.core.modules.sys.entity.Dict;
import com.siloyou.jmsg.modules.sms.entity.JmsgGatewayInfo;

/**
 * 通道信息DAO接口
 * @author zhukc
 * @version 2016-07-29
 */
@MyBatisDao
public interface JmsgGatewayInfoDao extends CrudDao<JmsgGatewayInfo> {
	
	public List<Dict> findLabelValue();
	
	public List<Dict> findLabelValueByType(JmsgGatewayInfo jmsgGatewayInfo);
	
	public void updateStatusByPrimaryKey(Map<String, String> param);
	
	public void updateGatewayState(Map<String, String> param);
}