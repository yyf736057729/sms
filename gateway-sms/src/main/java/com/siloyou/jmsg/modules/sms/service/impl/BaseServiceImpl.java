package com.siloyou.jmsg.modules.sms.service.impl;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.siloyou.jmsg.modules.sms.dao.BaseDao;
import com.siloyou.jmsg.modules.sms.entity.AbstractEntity;
import com.siloyou.jmsg.modules.sms.service.BaseService;

@Transactional
public abstract class BaseServiceImpl<T extends AbstractEntity>
        implements BaseService<T> {

    protected abstract BaseDao<T,  String> getDao();

    protected Class<T> entityClazz;


    @SuppressWarnings("unchecked")
    public BaseServiceImpl() {
        Type genType = getClass().getGenericSuperclass();
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        entityClazz = (Class<T>) params[0];
    }



    @Transactional(readOnly = true)
    public T findOne(String id) {
        Assert.notNull(id);
        return getDao().selectByPrimaryKey(id);
    }

}