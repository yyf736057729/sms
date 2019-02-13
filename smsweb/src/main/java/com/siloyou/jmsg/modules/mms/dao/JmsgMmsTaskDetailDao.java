/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.mms.dao;

import java.util.List;
import java.util.Map;

import com.siloyou.core.common.persistence.CrudDao;
import com.siloyou.core.common.persistence.annotation.MyBatisDao;
import com.siloyou.jmsg.modules.mms.entity.JmsgMmsTaskDetail;

/**
 * 彩信发送明细DAO接口
 * @author zhukc
 * @version 2016-05-20
 */
@MyBatisDao
public interface JmsgMmsTaskDetailDao extends CrudDao<JmsgMmsTaskDetail> {
	public List<String> findPhoneByTaskId(String taskId);//获取手机号码
	public List<JmsgMmsTaskDetail> findPendingSendMmsByTaskId(Map<String,String> map);
	public List<JmsgMmsTaskDetail> findPendingSendMmsByStatus();
	public void updateSendStatus(Map<String,String> map);
	
	public JmsgMmsTaskDetail findByTaskIdAndPhone(JmsgMmsTaskDetail entity);
	
	public void deleteByTaskId(String taskId);
	
	public void updateSendStatusP2(Map<String,String> map);//修改发送状态：暂停发送
	
	public Integer queryDownloadCount(String taskId);
	
	public List<JmsgMmsTaskDetail> queryDetailSendList(String taskId);
}