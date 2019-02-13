package com.siloyou.jmsg.modules.sms.dao;

import java.io.Serializable;

public interface BaseDao<T extends Serializable , ID extends Serializable> {
    int deleteByPrimaryKey(ID id);

    int insert(T record);

    int insertSelective(T record);


    T selectByPrimaryKey(ID id);


    int updateByPrimaryKeySelective(T record);

    int updateByPrimaryKey(T record);
}
