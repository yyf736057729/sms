package com.siloyou.jmsg.modules.sms.dao;

import com.siloyou.core.common.persistence.CrudDao;
import com.siloyou.core.common.persistence.annotation.MyBatisDao;
import com.siloyou.jmsg.modules.sms.entity.JmsgSmsGatewayTmpl;
import com.siloyou.jmsg.modules.sms.entity.JmsgSmsUserTmpl;

import java.util.List;

/**
 * @author yuyunfeng
 * @create_time 2019/1/11
 * @describe ${用户通道模板管理}
 */
@MyBatisDao
public interface JmsgSmsUserTmplDao extends CrudDao<JmsgSmsUserTmpl> {
    JmsgSmsUserTmpl selectByUseridTemp(JmsgSmsUserTmpl jmsgSmsGatewayTmpl);

    List<JmsgSmsUserTmpl> selectByUseridTemps(JmsgSmsUserTmpl jmsgSmsGatewayTmpl);
}
