/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.sms.dao;

import com.siloyou.core.common.persistence.CrudDao;
import com.siloyou.core.common.persistence.annotation.MyBatisDao;
import com.siloyou.jmsg.modules.sms.entity.JmsgCustomTask;

/**
 * 自定义任务DAO接口
 * @author zj
 * @version 2017-04-07
 */
@MyBatisDao
public interface JmsgCustomTaskDao extends CrudDao<JmsgCustomTask> {
	public void updateParam(JmsgCustomTask jmsgCustomTask);
}