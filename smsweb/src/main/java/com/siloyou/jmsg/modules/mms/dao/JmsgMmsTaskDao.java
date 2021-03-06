/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.mms.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.siloyou.core.common.persistence.CrudDao;
import com.siloyou.core.common.persistence.annotation.MyBatisDao;
import com.siloyou.jmsg.modules.mms.entity.JmsgMmsTask;

/**
 * 彩信发送管理DAO接口
 * @author zhukc
 * @version 2016-05-20
 */
@MyBatisDao
public interface JmsgMmsTaskDao extends CrudDao<JmsgMmsTask> {
	
	public List<String> findPendingSendMms();
	public void updateStatus(Map<String,String> map);
	public void updateTaskInfo(JmsgMmsTask jmsgMmsTask);
	public JmsgMmsTask queryMmsBody(String taskId);
	
	public List<JmsgMmsTask> findMmsSendTongjiList(JmsgMmsTask jmsgMmsTask);
	
	public List<JmsgMmsTask> findXFDetailList(JmsgMmsTask jmsgMmsTask);
	
	public List<JmsgMmsTask> findTaskPayModeList(Date sendDatetime);
	
	public List<JmsgMmsTask> findBackRechargeList(Date createDatetime);
	
	public List<JmsgMmsTask> findBackStatusList();
	
	public void updateTaskBackStatus(JmsgMmsTask jmsgMmsTask);
}