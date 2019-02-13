/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.sms.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.siloyou.jmsg.gateway.api.GatewayService;
import com.siloyou.jmsg.gateway.api.JmsgGateWayInfo;
import com.siloyou.jmsg.modules.sms.dao.BaseDao;
import com.siloyou.jmsg.modules.sms.dao.JmsgGatewayInfoDao;
import com.siloyou.jmsg.modules.sms.entity.JmsgGatewayInfo;

/**
 * 通道信息Service
 * @author zhukc
 * @version 2016-07-29
 */
@Service
@Transactional
public class JmsgGatewayInfoService extends BaseServiceImpl<JmsgGatewayInfo> implements GatewayService {

	@Autowired
    private JmsgGatewayInfoDao dao;
	
	@Override
    protected BaseDao<JmsgGatewayInfo, String> getDao() {
        return dao;
    }
	
	/**
     * 根据网关ID查找网关
     * @param gatewayId 网关ID
     * @return
     */
    public JmsgGateWayInfo findGateway(String gatewayId){
    	if (gatewayId == null) {
            return null;
        }
        return dao.selectByPrimaryKey(gatewayId);
    }

    /**
     *添加网关信息
     *
     * @param umsGateWayInfo
     * @return
     */
    @Transactional(readOnly = false)
    public boolean insert(JmsgGatewayInfo jmsgGateWayInfo){
    	return true;
    }

    /**
     *更新网关信息
     *
     * @param umsGateWayInfo
     * @return
     */
    @Transactional(readOnly = false)
    public boolean update(JmsgGatewayInfo jmsgGateWayInfo){
    	return true;
    }


    /**
     *根据网关ID删除对应网关
     *
     * @param id 网关ID
     * @return
     */
    @Transactional(readOnly = false)
    public boolean deleteGateway(String id){
    	return true;
    }

    /**
     *查询所有网关信息
     *
     * @return
     */
    public List<JmsgGateWayInfo> findAll(){
    	return null;
    }

    /**
     * 查询所有网关启用的
     * @return
     */
    public List<JmsgGateWayInfo> loadValidAll(String appCode) {
    	return dao.loadValidAll(appCode);
    }

    @Transactional(readOnly = false)
    public boolean updateStatusById(String status, String id){
    	Map<String, String> map = new HashMap<String, String>();
        map.put("id", id);
        map.put("status", status);
        int result = dao.updateStatusByPrimaryKey(map);
        return result == 1;
    }

    @Transactional(readOnly = false)
    public boolean updateGatewayStateById(String gatewayState, String id){
    	Map<String, String> map = new HashMap<String, String>();
        map.put("id", id);
        map.put("gatewayState", gatewayState);
        int result = dao.updateGatewayState(map);
        return result == 1;
    }

	@Override
	@Transactional(readOnly = false)
	public boolean insert(JmsgGateWayInfo arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	@Transactional(readOnly = false)
	public boolean update(JmsgGateWayInfo arg0) {
		// TODO Auto-generated method stub
		return false;
	}
	
}