/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.sms.dao;

import java.util.Map;

import com.siloyou.core.common.persistence.CrudDao;
import com.siloyou.core.common.persistence.annotation.MyBatisDao;
import com.siloyou.jmsg.modules.sms.entity.JmsgUserGateway;

/**
 * 用户通道DAO接口
 * @author zhukc
 * @version 2016-08-28
 */
@MyBatisDao
public interface JmsgUserGatewayDao extends CrudDao<JmsgUserGateway> {
	public JmsgUserGateway getUserGatewayByUsername(String username);
	
	public JmsgUserGateway getUserGatewayByUserid(String userid);
	
	/**
	 * 修改用户通道状态
	 * @param map
	 * @see [类、类#方法、类#成员]
	 */
	public void updateStateById(Map map);
}