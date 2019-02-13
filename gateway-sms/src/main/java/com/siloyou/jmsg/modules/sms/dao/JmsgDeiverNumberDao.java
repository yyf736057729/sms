package com.siloyou.jmsg.modules.sms.dao;

import java.util.List;

import com.siloyou.jmsg.modules.sms.entity.JmsgDeliverNumber;

public interface JmsgDeiverNumberDao extends BaseDao
{
    List<JmsgDeliverNumber> findList(JmsgDeliverNumber record);
}