/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.sms.dao;

import java.util.List;
import java.util.Map;

import com.siloyou.core.common.persistence.CrudDao;
import com.siloyou.core.common.persistence.annotation.MyBatisDao;
import com.siloyou.core.modules.sys.entity.User;
import com.siloyou.jmsg.common.message.SmsMoMessage;
import com.siloyou.jmsg.common.message.SmsMtMessage;
import com.siloyou.jmsg.modules.sms.entity.JmsgReportStatusTask;
import com.siloyou.jmsg.modules.sms.entity.JmsgSmsMmsdown;
import com.siloyou.jmsg.modules.sms.entity.JmsgSmsPush;
import com.siloyou.jmsg.modules.sms.entity.JmsgSmsSend;
import com.siloyou.jmsg.modules.sms.entity.JmsgSmsSendReport;
import com.siloyou.jmsg.modules.sms.entity.JmsgSmsSendUserReport;
import com.siloyou.jmsg.modules.sms.entity.JmsgSmsTask;

/**
 * 短信发送DAO接口
 * @author zhukc
 * @version 2016-07-16
 */
@MyBatisDao
public interface JmsgSmsSendDao extends CrudDao<JmsgSmsSend> {
	public List<JmsgSmsSend> findListByTaskId(Map<String,Object> map);
	public List<String> findPhoneByTaskId(String taskId);//获取手机号码
	public List<JmsgSmsSend> queryJmsgSmsSend(JmsgSmsSend param);
	public List<JmsgSmsSend> queryJmsgSmsReport(JmsgSmsSend param);
	public List<JmsgSmsSend> querySendDeatilResultBySend(JmsgSmsSend param);
	public List<JmsgSmsSend> querySendDeatilResultByReport(JmsgSmsSend param);
	public List<JmsgSmsSend> querySendDeatilResultByReportV2(JmsgSmsSend param);
	
	public List<JmsgSmsSend> findPushListByTaskId(Map<String,Object> map);
	public List<JmsgSmsSend> findPushListById(String id);
	public User findUser(SmsMoMessage param);
	public List<JmsgSmsPush> queryJmsgSmsPush(String id);
	public SmsMtMessage findSmsMtMessage(String id);
	public List<JmsgSmsSend> findDetailByReprot(JmsgSmsSend jmsgSmsSend);//查看明细 
	
	public JmsgSmsTask findSmsSendByTaskId(JmsgSmsSend jmsgSmsSend);
	
	public void insertHistory(JmsgSmsSend param);
	public void clearSmsSend(JmsgSmsSend param);
	public void clearMmsDown(JmsgSmsMmsdown param);
	
	public List<JmsgSmsSend> queryJmsgSmsSendV2(JmsgSmsSend param);
	public List<JmsgSmsSend> queryJmsgSmsSendHistory(JmsgSmsSend param);
	public List<JmsgSmsSendReport> queryJmsgSmsSendReportV2(JmsgSmsSend param);
	
	public List<JmsgSmsSendUserReport> queryJmsgSmsSendUserReportV2(JmsgSmsSend param);
	
	public JmsgSmsSend getV2(JmsgSmsSend param);
	public List<JmsgSmsPush> queryJmsgSmsPushV2(JmsgSmsPush param);
	public List<String> findPhoneByTaskIdV2(JmsgSmsSend param);//获取手机号码
	
	public List<Map<String,Object>> queryGatewayErrorCount(Map<String,String> map);
	
	public List<JmsgSmsSend> findPushList(Map<String,Object> map);
	
	public List<JmsgSmsSend> findJmsgSmsSendListByProvinceReport(Map<String,Object> map);
	
	public List<JmsgSmsSend> findReportStatusCountList(JmsgReportStatusTask jmsgReportStatusTask);
	
	public Integer findReportStatusCount(JmsgReportStatusTask jmsgReportStatusTask);
	
	public List<JmsgSmsSend> findSucReportPushList(Map<String,Object> map);

	//短信下行明细查询
	public List<JmsgSmsSend> getSmsList(JmsgSmsSend jmsgSmsSend);

	//通过id查询单条记录
	public JmsgSmsSend getSmsById(JmsgSmsSend jmsgSmsSend);

	//短信下行明细查询
	public List<JmsgSmsSend> getSmsListByTime(JmsgSmsSend jmsgSmsSend);


}