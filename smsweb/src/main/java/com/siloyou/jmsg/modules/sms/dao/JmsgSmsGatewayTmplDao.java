package com.siloyou.jmsg.modules.sms.dao;

import com.siloyou.core.common.persistence.CrudDao;
import com.siloyou.core.common.persistence.annotation.MyBatisDao;
import com.siloyou.jmsg.modules.sms.entity.JmsgSmsGatewayTmpl;

/**
 * @author yuyunfeng
 * @create_time 2019/1/11
 * @describe ${通道模板管理}
 */
@MyBatisDao
public interface JmsgSmsGatewayTmplDao extends CrudDao<JmsgSmsGatewayTmpl> {
    JmsgSmsGatewayTmpl searchByGatewayName(JmsgSmsGatewayTmpl jmsgSmsGatewayTmpl);
}
