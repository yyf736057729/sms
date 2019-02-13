/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.sms.dao;

import com.siloyou.jmsg.modules.sms.entity.JmsgSmsSubmit;

/**
 * 网关状态DAO接口
 * @author zhukc
 * @version 2016-08-05
 */
public interface JmsgSmsSubmitDao extends BaseDao
{
    int insert(JmsgSmsSubmit record);
}