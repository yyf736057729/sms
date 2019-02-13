package com.siloyou.jmsg.modules.sms.service;

import java.io.Serializable;

public interface BaseService<T extends Serializable> {


    T findOne(String id);

}