/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.sms.dao;

import com.siloyou.core.common.persistence.CrudDao;
import com.siloyou.core.common.persistence.annotation.MyBatisDao;
import com.siloyou.jmsg.modules.sms.entity.JmsgSmsMmsdown;

/**
 * 短信彩信下载DAO接口
 * @author zhukc
 * @version 2017-05-23
 */
@MyBatisDao
public interface JmsgSmsMmsdownDao extends CrudDao<JmsgSmsMmsdown> {
	
}