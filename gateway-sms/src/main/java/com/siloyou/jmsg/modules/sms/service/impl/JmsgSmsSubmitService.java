/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.sms.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.siloyou.jmsg.gateway.api.GatewayService;
import com.siloyou.jmsg.modules.sms.entity.JmsgSmsSubmit;
import com.siloyou.jmsg.modules.sms.dao.BaseDao;
import com.siloyou.jmsg.modules.sms.dao.JmsgSmsSubmitDao;

/**
 * 网关状态Service
 * @author zhukc
 * @version 2016-08-05
 */
@Service
@Transactional
public class JmsgSmsSubmitService extends BaseServiceImpl<JmsgSmsSubmit>
{
    @Autowired
    private JmsgSmsSubmitDao jmsgSmsSubmitDao;

    @Override
    protected BaseDao<JmsgSmsSubmit, String> getDao()
    {
        return jmsgSmsSubmitDao;
    }
    
}