package com.siloyou.jmsg.modules.sms.dao;

import com.siloyou.core.common.persistence.CrudDao;
import com.siloyou.core.common.persistence.annotation.MyBatisDao;
import com.siloyou.jmsg.modules.sms.entity.JmsgGatewayQueue;

/**
 * @author yuyunfeng
 * @create_time 2019/1/9
 * @describe ${通道队列}
 */

@MyBatisDao
public interface JmsgGatewayQueueDao  extends CrudDao<JmsgGatewayQueue> {

    JmsgGatewayQueue searchByGatewayName(JmsgGatewayQueue jmsgGatewayQueue);

    int searchLikeByGateway(JmsgGatewayQueue jmsgGatewayQueue);

}
