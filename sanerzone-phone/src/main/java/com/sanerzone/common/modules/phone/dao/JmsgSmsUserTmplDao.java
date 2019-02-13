package com.sanerzone.common.modules.phone.dao;

import com.sanerzone.common.modules.phone.entity.JmsgSmsUserTmpl;
import com.sanerzone.common.support.persistence.CrudDao;
import com.sanerzone.common.support.persistence.annotation.MyBatisDao;


import java.util.List;

/**
 * @author yuyunfeng
 * @create_time 2019/1/11
 * @describe ${用户通道模板管理}
 */
@MyBatisDao
public interface JmsgSmsUserTmplDao extends CrudDao<JmsgSmsUserTmpl> {
    List<JmsgSmsUserTmpl> selectByUseridTemps(JmsgSmsUserTmpl jmsgSmsGatewayTmpl);
}
