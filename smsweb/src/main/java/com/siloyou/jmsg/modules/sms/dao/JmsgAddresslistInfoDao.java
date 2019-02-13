/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.sms.dao;

import com.siloyou.core.common.persistence.CrudDao;
import com.siloyou.core.common.persistence.annotation.MyBatisDao;
import com.siloyou.jmsg.modules.sms.entity.JmsgAddresslistInfo;

/**
 * 联系人列表DAO接口
 * @author zhukc
 * @version 2017-04-01
 */
@MyBatisDao
public interface JmsgAddresslistInfoDao extends CrudDao<JmsgAddresslistInfo> {
	public void deleteByGroupId(String groupId);
}