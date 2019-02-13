/**
 * zoneland.net Inc.
 * Copyright (c) 2002-2012 All Rights Reserved.
 */
package com.siloyou.jmsg.gateway.api;

import java.util.List;

/**
 * 网关连接信息业务类
 * @author gag
 * @version $Id: GatewayService.java, v 0.1 2012-8-23 下午4:17:05 gag Exp $
 */
public interface GatewayService {

    /**
     * 根据网关ID查找网关
     * @param gatewayId 网关ID
     * @return
     */
    JmsgGateWayInfo findGateway(String gatewayId);

    /**
     *添加网关信息
     *
     * @param JmsgGateWayInfo
     * @return
     */
    boolean insert(JmsgGateWayInfo JmsgGateWayInfo);

    /**
     *更新网关信息
     *
     * @param JmsgGateWayInfo
     * @return
     */
    boolean update(JmsgGateWayInfo JmsgGateWayInfo);


    /**
     *根据网关ID删除对应网关
     *
     * @param id 网关ID
     * @return
     */
    public boolean deleteGateway(String id);

    /**
     *查询所有网关信息
     *
     * @return
     */
    public List<JmsgGateWayInfo> findAll();

    /**
     * 查询所有网关启用的
     * @return
     */
    List<JmsgGateWayInfo> loadValidAll(String appCode);

    boolean updateStatusById(String status, String id);

    boolean updateGatewayStateById(String gatewayState, String id);

}
