/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.sms.dao;

import com.siloyou.core.common.persistence.CrudDao;
import com.siloyou.core.common.persistence.annotation.MyBatisDao;
import com.siloyou.jmsg.modules.sms.entity.JmsgUserSign;

/**
 * 用户签名DAO接口
 * @author zj
 * @version 2016-09-08
 */
@MyBatisDao
public interface JmsgUserSignDao extends CrudDao<JmsgUserSign> {
	
}