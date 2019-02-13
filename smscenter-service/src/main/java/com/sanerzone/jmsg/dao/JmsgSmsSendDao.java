/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.sanerzone.jmsg.dao;

import java.util.List;
import java.util.Map;

import com.sanerzone.common.support.persistence.CrudDao;
import com.sanerzone.common.support.persistence.annotation.MyBatisDao;
import com.sanerzone.jmsg.entity.JmsgSmsSend;
import com.siloyou.jmsg.common.message.SmsMoMessage;

/**
 * 短信发送DAO接口
 * @author zhukc
 * @version 2016-07-16
 */
@MyBatisDao
public interface JmsgSmsSendDao extends CrudDao<JmsgSmsSend> {
	public String findUser(SmsMoMessage param);
	public List<JmsgSmsSend> findListByTaskId(Map<String,Object> map);
	public List<JmsgSmsSend> findPushListByTaskId(Map<String,Object> map);
	
	public String findUserV2(Map<String,Object> map);

	public JmsgSmsSend findByPhone(Map<String,Object> map);

	void updateSendRecord(Map<String,Object> map);

}