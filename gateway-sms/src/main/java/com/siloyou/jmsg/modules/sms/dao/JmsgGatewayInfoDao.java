/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.sms.dao;

import java.util.List;
import java.util.Map;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import com.siloyou.jmsg.gateway.api.JmsgGateWayInfo;

/**
 * 通道信息DAO接口
 * @author zhukc
 * @version 2016-07-29
 */
public interface JmsgGatewayInfoDao extends BaseDao {
	
    @CacheEvict(value = "GatewayInfo", allEntries = true)
    int deleteByPrimaryKey(String id);

    @CacheEvict(value = "GatewayInfo", allEntries = true)
    int insert(JmsgGateWayInfo record);

    @Cacheable(value = "GatewayInfo")
    JmsgGateWayInfo selectByPrimaryKey(String id);

    @CacheEvict(value = "GatewayInfo", allEntries = true)
    int updateByPrimaryKey(JmsgGateWayInfo record);

    @CacheEvict(value = "GatewayInfo", allEntries = true)
    int updateStatusByPrimaryKey(Map<String, String> map);

	
	//更新网关启用的状态
    int updateGatewayState(Map<String, String> map);
	
	/**
     *分页查询网关信息
     *
     * @param map 包括地起始页及最后页
     * @return
     */
    List<JmsgGateWayInfo> selectAll(Map<String, Object> map);

    /**
     *获取网关个数
     *
     * @return
     */
    int selectCount();

    /**
     * 查询所有网关信息
     *
     * @return
     */
    List<JmsgGateWayInfo> findAll();

    List<JmsgGateWayInfo> loadValidAll(String appCode);

    /**
     * 根据网关类型查找可用的网关
     * @param type
     * @return
     */
    @Cacheable(value = "GatewayInfo")
    List<JmsgGateWayInfo> findWithType(String type);

    @Cacheable(value = "GatewayInfo")
    List<JmsgGateWayInfo> findWithParam(Map<String, Object> map);

    JmsgGateWayInfo findOneValid();

    /**
     * 根据网关类型查找所有的网关
     * @param type
     * @return
     */
    @Cacheable(value = "GatewayInfo")
    List<JmsgGateWayInfo> findAllByType(String type);
}