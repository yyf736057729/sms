/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.sms.dao;

import java.util.List;
import java.util.Map;

import com.siloyou.core.common.persistence.CrudDao;
import com.siloyou.core.common.persistence.annotation.MyBatisDao;
import com.siloyou.jmsg.modules.sms.entity.JmsgSmsTask;

/**
 * 短信任务发送DAO接口
 * @author zhukc
 * @version 2016-07-20
 */
@MyBatisDao
public interface JmsgSmsTaskDao extends CrudDao<JmsgSmsTask> {
	
	public void updateJmsgSmsTask(JmsgSmsTask jmsgSmsTask);
	
	public void updateJmsgSmsTaskByDataId(JmsgSmsTask jmsgSmsTask);
	
	public List<JmsgSmsTask> findPendingSendSms();//获取待发送短信任务
	
	public List<JmsgSmsTask> findPendingSendSmsDot();//获取待发送短信任务(点对点)
	
	public List<JmsgSmsTask> findPendingSendBatchSms();//获取待发送批量短信任务
	
	public List<JmsgSmsTask> findPendingSendMms();//获取待发送短信彩信任务
	
	public void updateStatus(Map<String,String> map);
	
	public List<JmsgSmsTask> findSmsTaskReport(JmsgSmsTask jmsgSmsTask);
	
	public void updateReport(JmsgSmsTask jmsgSmsTask);
	
	public void updateRowNumber(Map<String,String> map);
	
	public int updateSendStatus(Map<String,String> map);
	
	public void reviewSmsContent(JmsgSmsTask jmsgSmsTask);

	public void updateSmsContent(JmsgSmsTask jmsgSmsTask);

	public int findReviewCount();

	/**
	 * @Description: 一键审核
	 * @param: jmsgSmsTask
	 * @return: void
	 * @author: zhanghui
	 * @Date: 2019-01-10
	 */
	public void onekeyReview(JmsgSmsTask jmsgSmsTask);

}