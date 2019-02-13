/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.sanerzone.common.modules.smscenter.dao;


import com.sanerzone.common.modules.smscenter.entity.JmsgUserGateway;
import com.sanerzone.common.support.persistence.CrudDao;
import com.sanerzone.common.support.persistence.annotation.MyBatisDao;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 用户通道DAO接口
 * @author zhukc
 * @version 2016-08-28
 */
@MyBatisDao
public interface JmsgUserGatewayDao  extends CrudDao<JmsgUserGateway>
{
    public List<JmsgUserGateway> findAll();
    
    public JmsgUserGateway selectByPrimaryKey(int id);
    
    public List<JmsgUserGateway> loadValidAll();
    
    public void updateStateById(Map<String, String> map);

    JmsgUserGateway getUserGatewayByUserid(String id);
}