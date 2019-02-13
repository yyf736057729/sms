/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.sms.dao;

import java.util.List;

import com.siloyou.core.common.persistence.CrudDao;
import com.siloyou.core.common.persistence.annotation.MyBatisDao;
import com.siloyou.jmsg.modules.sms.entity.JmsgSmsDeliverPush;

/**
 * 上行推送信息DAO接口
 * @author zhukc
 * @version 2016-08-14
 */
@MyBatisDao
public interface JmsgSmsDeliverPushDao extends CrudDao<JmsgSmsDeliverPush> {
	public List<JmsgSmsDeliverPush> findListNew(JmsgSmsDeliverPush param);
	public List<JmsgSmsDeliverPush> findExportList(JmsgSmsDeliverPush param);
}