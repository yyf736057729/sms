package com.sanerzone.common.modules.phone.dao;


import com.sanerzone.common.modules.phone.entity.ContentManage;
import com.sanerzone.common.modules.phone.entity.GatewayQueue;
import com.sanerzone.common.support.persistence.CrudDao;
import com.sanerzone.common.support.persistence.annotation.MyBatisDao;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 通道队列查询
 * @author Carl
 * @version 2019-1-7
 */
@MyBatisDao
public interface GatewayQueueDao extends CrudDao<GatewayQueue> {
    /**
     * 通道队列查询
     * @return
     */
    List<GatewayQueue> findByGatewayId(@Param(value="gatewayId")String gatewayId);

}
