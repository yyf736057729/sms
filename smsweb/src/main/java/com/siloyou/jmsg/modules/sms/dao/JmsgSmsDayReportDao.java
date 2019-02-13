/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.sms.dao;

import java.util.Date;
import java.util.List;

import com.siloyou.core.common.persistence.CrudDao;
import com.siloyou.core.common.persistence.annotation.MyBatisDao;
import com.siloyou.jmsg.modules.sms.entity.JmsgSmsDayReport;
import com.siloyou.jmsg.modules.sms.entity.JmsgSmsGatewayReport;
import com.siloyou.jmsg.modules.sms.entity.JmsgSmsMonthReport;
import com.siloyou.jmsg.modules.sms.entity.SmsUserIndex;

/**
 * 短信日报表DAO接口
 * @author zhukc
 * @version 2016-07-28
 */
@MyBatisDao
public interface JmsgSmsDayReportDao extends CrudDao<JmsgSmsDayReport> {
	
	
	public List<JmsgSmsDayReport> findListReport(JmsgSmsDayReport jmsgSmsDayReport);
	
	public List<JmsgSmsMonthReport> findMonthReport(JmsgSmsDayReport jmsgSmsDayReport);
	
	public List<JmsgSmsGatewayReport> findGatewayReport(JmsgSmsDayReport jmsgSmsDayReport);
	
	public List<JmsgSmsDayReport> findSendListByDay(JmsgSmsDayReport param);
	
	public List<JmsgSmsDayReport> findSendListByDayNew(JmsgSmsDayReport param);
	
	public void batchInsert(JmsgSmsDayReport param);
	
	public List<JmsgSmsDayReport> findRechargeList(Date day);
	
	public void updateBackFlag(JmsgSmsDayReport param);
	
	public int queryBackFlagCount(Date day);
	
	public List<JmsgSmsDayReport> findListPhoneType(JmsgSmsDayReport jmsgSmsDayReport);
	
	public List<JmsgSmsDayReport> findListReportPhoneType(JmsgSmsDayReport jmsgSmsDayReport);
	
	public List<JmsgSmsDayReport> findSendListByDayV2(JmsgSmsDayReport param);
	
	public List<JmsgSmsDayReport> findDaySendListV2(JmsgSmsDayReport param);
	
	public List<JmsgSmsDayReport> findList4Index(JmsgSmsDayReport jmsgSmsDayReport);
	
	public List<JmsgSmsDayReport> findSendListByDayV3(JmsgSmsDayReport param);
	
	public List<JmsgSmsDayReport> findUserGatewayList(JmsgSmsDayReport jmsgSmsDayReport);//用户通道报表
	
	public List<JmsgSmsDayReport> findGatewayList(JmsgSmsDayReport jmsgSmsDayReport);//通道报表
	
	public List<JmsgSmsDayReport> findDayReportList(JmsgSmsDayReport jmsgSmsDayReport);//日报表
	
	public List<JmsgSmsDayReport> findCreateDayReportList(JmsgSmsDayReport param);
	
	public JmsgSmsDayReport findSmsDayReportByDay(JmsgSmsDayReport param);//获取日数据汇总
	
	public List<SmsUserIndex> findCountByDay(JmsgSmsDayReport param);//用户近30天发送量数据趋势
	
	public List<JmsgSmsDayReport> findCountByPhoneType(JmsgSmsDayReport param);//获取运营商数据汇总
	
	public List<SmsUserIndex> findCountByDayPhoneType(JmsgSmsDayReport param);//运营商网关成功量数据
}