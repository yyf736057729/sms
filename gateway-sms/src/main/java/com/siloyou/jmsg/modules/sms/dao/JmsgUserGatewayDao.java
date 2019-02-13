/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.sms.dao;

import java.util.List;
import java.util.Map;

import com.siloyou.jmsg.modules.sms.entity.JmsgUserGateway;

/**
 * 用户通道DAO接口
 * @author zhukc
 * @version 2016-08-28
 */
public interface JmsgUserGatewayDao extends BaseDao
{
    public List<JmsgUserGateway> findAll();
    
    public JmsgUserGateway selectByPrimaryKey(int id);
    
    public void update(JmsgUserGateway jmsgUserGateway);
    
    public List<JmsgUserGateway> loadValidAll();
    
    public void updateStateById(Map<String, String> map);
    
    public JmsgUserGateway getUserGatewayByUserid(String userid);
}