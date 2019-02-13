/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.sanerzone.jmsg.dao;

import java.util.List;
import java.util.Map;

import com.sanerzone.common.support.persistence.CrudDao;
import com.sanerzone.common.support.persistence.annotation.MyBatisDao;
import com.sanerzone.jmsg.entity.JmsgSmsSubmit;

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
    
    public List<JmsgSmsSubmit> findSubmitByBizId(Map<String,Object> map);
}