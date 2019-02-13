package com.sanerzone.common.modules.phone.dao;


import com.sanerzone.common.modules.phone.entity.JmsgSmsGatewayTmpl;
import com.sanerzone.common.support.persistence.CrudDao;
import com.sanerzone.common.support.persistence.annotation.MyBatisDao;

import java.util.List;

/**
 * @author yuyunfeng
 * @create_time 2019/1/11
 * @describe ${通道模板管理}
 */
@MyBatisDao
public interface JmsgSmsGatewayTmplDao extends CrudDao<JmsgSmsGatewayTmpl> {
    JmsgSmsGatewayTmpl searchByGatewayName(JmsgSmsGatewayTmpl jmsgSmsGatewayTmpl);
    List<JmsgSmsGatewayTmpl> findList(JmsgSmsGatewayTmpl jmsgSmsGatewayTmpl);
}
