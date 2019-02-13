/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.order.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.siloyou.core.common.persistence.Page;
import com.siloyou.core.common.service.CrudService;
import com.siloyou.jmsg.modules.order.entity.JmsgOrder;
import com.siloyou.jmsg.modules.order.dao.JmsgOrderDao;

/**
 * 订单信息Service
 * @author zhukc
 * @version 2016-05-18
 */
@Service
@Transactional(readOnly = true)
public class JmsgOrderService extends CrudService<JmsgOrderDao, JmsgOrder> {

	public JmsgOrder get(String id) {
		return super.get(id);
	}
	
	public List<JmsgOrder> findList(JmsgOrder jmsgOrder) {
		return super.findList(jmsgOrder);
	}
	
	public Page<JmsgOrder> findPage(Page<JmsgOrder> page, JmsgOrder jmsgOrder) {
		return super.findPage(page, jmsgOrder);
	}
	
	@Transactional(readOnly = false)
	public void save(JmsgOrder jmsgOrder) {
		super.save(jmsgOrder);
	}
	
	@Transactional(readOnly = false)
	public void delete(JmsgOrder jmsgOrder) {
		super.delete(jmsgOrder);
	}
	
}