/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.sms.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.siloyou.core.common.persistence.Page;
import com.siloyou.core.common.service.CrudService;
import com.siloyou.core.common.utils.JedisClusterUtils;
import com.siloyou.jmsg.modules.sms.entity.JmsgDeliverNumber;
import com.siloyou.jmsg.modules.sms.dao.JmsgDeliverNumberDao;

/**
 * 用户上行接入号Service
 * @author zhukc
 * @version 2016-08-14
 */
@Service
@Transactional(readOnly = true)
public class JmsgDeliverNumberService extends CrudService<JmsgDeliverNumberDao, JmsgDeliverNumber> {

	public JmsgDeliverNumber get(String id) {
		return super.get(id);
	}
	
	public List<JmsgDeliverNumber> findList(JmsgDeliverNumber jmsgDeliverNumber) {
		return super.findList(jmsgDeliverNumber);
	}
	
	public Page<JmsgDeliverNumber> findPage(Page<JmsgDeliverNumber> page, JmsgDeliverNumber jmsgDeliverNumber) {
		return super.findPage(page, jmsgDeliverNumber);
	}
	
	@Transactional(readOnly = false)
	public void save(JmsgDeliverNumber jmsgDeliverNumber) {
		String spNumber = jmsgDeliverNumber.getSpNumber().trim();
		jmsgDeliverNumber.setSpNumber(spNumber);
		super.save(jmsgDeliverNumber);
		JedisClusterUtils.hset("MONUM", spNumber, jmsgDeliverNumber.getUser().getId());
		
	}
	
	@Transactional(readOnly = false)
	public void delete(JmsgDeliverNumber jmsgDeliverNumber) {
		super.delete(jmsgDeliverNumber);
		//JedisClusterUtils.del(jmsgDeliverNumber.getSpNumber());
		JedisClusterUtils.hdel("MONUM", jmsgDeliverNumber.getSpNumber());
	}
	
	public int userCount(JmsgDeliverNumber jmsgDeliverNumber){
		return dao.queryBySpNumber(jmsgDeliverNumber);
	}
	
}