/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.sanerzone.common.modules.smscenter.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sanerzone.common.modules.smscenter.dao.JmsgGatewayInfoDao;
import com.sanerzone.common.modules.smscenter.entity.JmsgGatewayInfo;
import com.sanerzone.common.modules.smscenter.utils.GatewayUtils;
import com.sanerzone.common.support.persistence.CrudService;
import com.sanerzone.common.support.persistence.Page;

/**
 * 通道信息Service
 * @author zhukc
 * @version 2016-07-29
 */
@Service
@Transactional(readOnly = true)
public class JmsgGatewayInfoService extends CrudService<JmsgGatewayInfoDao, JmsgGatewayInfo> {

	public JmsgGatewayInfo get(String id) {
		return super.get(id);
	}
	
	public List<JmsgGatewayInfo> findList(JmsgGatewayInfo jmsgGatewayInfo) {
		return super.findList(jmsgGatewayInfo);
	}
	
	public Page<JmsgGatewayInfo> findPage(Page<JmsgGatewayInfo> page, JmsgGatewayInfo jmsgGatewayInfo) {
		return super.findPage(page, jmsgGatewayInfo);
	}
	
	@Transactional(readOnly = false)
	public void updateGatewayState(JmsgGatewayInfo jmsgGatewayInfo)
	{
	    Map<String, String> param = new HashMap<String, String>();
	    param.put("id", jmsgGatewayInfo.getId());
	    param.put("gatewayState", jmsgGatewayInfo.getGatewayState());
	    dao.updateGatewayState(param);
	    GatewayUtils.del(jmsgGatewayInfo.getId());
	}
	
	@Transactional(readOnly = false)
	public void save(JmsgGatewayInfo jmsgGatewayInfo) {
		super.delete(jmsgGatewayInfo);
		dao.insert(jmsgGatewayInfo);
		GatewayUtils.put(jmsgGatewayInfo.getId(),jmsgGatewayInfo);
	}
	
	@Transactional(readOnly = false)
	public void delete(JmsgGatewayInfo jmsgGatewayInfo) {
		super.delete(jmsgGatewayInfo);
		GatewayUtils.del(jmsgGatewayInfo.getId());
	}
	
}