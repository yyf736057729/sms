/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.sms.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.siloyou.core.common.persistence.Page;
import com.siloyou.core.common.service.CrudService;
import com.siloyou.jmsg.modules.sms.entity.JmsgGatewayMonitor;
import com.siloyou.jmsg.modules.sms.dao.JmsgGatewayMonitorDao;

/**
 * 网关告警Service
 * @author zj
 * @version 2016-10-15
 */
@Service
@Transactional(readOnly = true)
public class JmsgGatewayMonitorService extends CrudService<JmsgGatewayMonitorDao, JmsgGatewayMonitor> {

	public JmsgGatewayMonitor get(String id) {
		return super.get(id);
	}
	
	public List<JmsgGatewayMonitor> findList(JmsgGatewayMonitor jmsgGatewayMonitor) {
		return super.findList(jmsgGatewayMonitor);
	}
	
	public Page<JmsgGatewayMonitor> findPage(Page<JmsgGatewayMonitor> page, JmsgGatewayMonitor jmsgGatewayMonitor) {
		return super.findPage(page, jmsgGatewayMonitor);
	}
	
	@Transactional(readOnly = false)
	public void save(JmsgGatewayMonitor jmsgGatewayMonitor) {
		super.save(jmsgGatewayMonitor);
	}
	
	@Transactional(readOnly = false)
	public void delete(JmsgGatewayMonitor jmsgGatewayMonitor) {
		super.delete(jmsgGatewayMonitor);
	}
	
}