/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.sms.dao;

import java.util.List;

import com.siloyou.core.common.persistence.CrudDao;
import com.siloyou.core.common.persistence.annotation.MyBatisDao;
import com.siloyou.jmsg.modules.sms.entity.JmsgSmsSubmit;

/**
 * 网关状态DAO接口
 * @author zhukc
 * @version 2016-08-05
 */
@MyBatisDao
public interface JmsgSmsSubmitDao extends CrudDao<JmsgSmsSubmit>
{
    int insert(JmsgSmsSubmit record);
    
    public List<JmsgSmsSubmit> findErrorForReSend(JmsgSmsSubmit record);
    
    public List<JmsgSmsSubmit> findListV2(JmsgSmsSubmit record);
}