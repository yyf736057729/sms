/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.sms.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.siloyou.jmsg.modules.sms.entity.JmsgUserGateway;
import com.siloyou.jmsg.modules.sms.dao.BaseDao;
import com.siloyou.jmsg.modules.sms.dao.JmsgUserGatewayDao;

/**
 * 用户通道Service
 * @author zhukc
 * @version 2016-08-28
 */
@Service
@Transactional
public class JmsgUserGatewayService extends BaseServiceImpl<JmsgUserGateway>
{
    @Autowired
    private JmsgUserGatewayDao jmsgUserGatewayDao;
    
    @Override
    protected BaseDao<JmsgUserGateway, String> getDao()
    {
        return jmsgUserGatewayDao;
    }
    
    /**
     * 查询所有用户网关
     * @return
     * @see [类、类#方法、类#成员]
     */
    public List<JmsgUserGateway> findAll()
    {
        return jmsgUserGatewayDao.findAll();
    }
    
    /**
     * 根据ID获取用户网关信息
     * @param id
     * @return
     * @see [类、类#方法、类#成员]
     */
    public JmsgUserGateway selectByPrimaryKey(int id)
    {
        return jmsgUserGatewayDao.selectByPrimaryKey(id);
    }
    
    /**
     * 修改用户网关
     * @param jmsgUserGateway
     * @see [类、类#方法、类#成员]
     */
    public void update(JmsgUserGateway jmsgUserGateway)
    {
        jmsgUserGatewayDao.update(jmsgUserGateway);
    }
    
}