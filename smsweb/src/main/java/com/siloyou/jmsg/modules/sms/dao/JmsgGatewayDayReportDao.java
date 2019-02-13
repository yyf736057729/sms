/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.sms.dao;

import java.util.List;

import com.siloyou.core.common.persistence.CrudDao;
import com.siloyou.core.common.persistence.annotation.MyBatisDao;
import com.siloyou.jmsg.modules.sms.entity.JmsgGatewayDayReport;
import com.siloyou.jmsg.modules.sms.entity.JmsgGatewayMonthReport;

/**
 * 通道日发送报表DAO接口
 * @author zhukc
 * @version 2016-08-04
 */
@MyBatisDao
public interface JmsgGatewayDayReportDao extends CrudDao<JmsgGatewayDayReport> {
	
	public List<JmsgGatewayDayReport> findListByMonth(JmsgGatewayDayReport jmsgGatewayDayReport);
	
	public List<JmsgGatewayDayReport> findListReport(JmsgGatewayDayReport jmsgGatewayDayReport);
	
	public List<JmsgGatewayMonthReport> findMonthListReport(JmsgGatewayDayReport jmsgGatewayDayReport);
	
	public List<JmsgGatewayDayReport> findGatewaySendListByDay(JmsgGatewayDayReport param);
	public List<JmsgGatewayDayReport> findGatewaySendListByDayNew(JmsgGatewayDayReport param);
	public JmsgGatewayDayReport findGatewaySendListByDayV2(JmsgGatewayDayReport param);
	
	public void batchInsert(JmsgGatewayDayReport param);
	
}