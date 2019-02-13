/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.mms.dao;

import com.siloyou.core.common.persistence.CrudDao;
import com.siloyou.core.common.persistence.annotation.MyBatisDao;
import com.siloyou.jmsg.modules.mms.entity.JmsgMmsData;

/**
 * 彩信素材DAO接口
 * @author zhukc
 * @version 2016-05-20
 */
@MyBatisDao
public interface JmsgMmsDataDao extends CrudDao<JmsgMmsData> {
	public void updateCheckStatus(JmsgMmsData jmsgMmsData);
	public JmsgMmsData findCheckInfo(String id);
	public void updateUseFlag(String id);
	public Long checkCount();
}