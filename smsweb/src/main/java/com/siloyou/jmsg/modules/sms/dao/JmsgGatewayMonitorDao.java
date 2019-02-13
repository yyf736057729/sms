/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.sms.dao;

import java.util.List;
import java.util.Map;

import com.siloyou.core.common.persistence.CrudDao;
import com.siloyou.core.common.persistence.annotation.MyBatisDao;
import com.siloyou.jmsg.modules.sms.entity.JmsgGatewayMonitor;

/**
 * 网关告警DAO接口
 * @author zj
 * @version 2016-10-15
 */
@MyBatisDao
public interface JmsgGatewayMonitorDao extends CrudDao<JmsgGatewayMonitor> {
    /**
     * 获取5分钟内submit的监测结果
     * @return
     * @see [类、类#方法、类#成员]
     */
	public List<JmsgGatewayMonitor> countGateWay(Map<String,String> map);
	
	public List<Map> countGateWaySend(Map<String,String> map);
	
}