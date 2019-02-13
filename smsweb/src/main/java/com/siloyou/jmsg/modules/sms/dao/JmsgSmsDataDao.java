/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.sms.dao;

import com.siloyou.core.common.persistence.CrudDao;
import com.siloyou.core.common.persistence.annotation.MyBatisDao;
import com.siloyou.jmsg.modules.sms.entity.JmsgSmsData;

/**
 * 短信素材DAO接口
 * @author zhukc
 * @version 2016-07-18
 */
@MyBatisDao
public interface JmsgSmsDataDao extends CrudDao<JmsgSmsData> {
	public JmsgSmsData findJmsgSmsDataByContentKey(JmsgSmsData jmsgSmsData);
	public void updateReviewStatus(JmsgSmsData jmsgSmsData);
}