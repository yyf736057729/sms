/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.sms.dao;

import com.siloyou.core.common.persistence.CrudDao;
import com.siloyou.core.common.persistence.annotation.MyBatisDao;
import com.siloyou.jmsg.modules.sms.entity.JmsgPhoneYzm;

/**
 * 验证码黑名单DAO接口
 * @author zhukc
 * @version 2017-12-05
 */
@MyBatisDao
public interface JmsgPhoneYzmDao extends CrudDao<JmsgPhoneYzm> {
	public void deleteByphone(String phone);
}