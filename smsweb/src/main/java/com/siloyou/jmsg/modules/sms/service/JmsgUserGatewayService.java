/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.sms.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.siloyou.core.common.persistence.Page;
import com.siloyou.core.common.service.CrudService;
import com.siloyou.jmsg.modules.sms.entity.JmsgUserGateway;
import com.siloyou.jmsg.modules.sms.dao.JmsgUserGatewayDao;

/**
 * 用户通道Service
 * @author zhukc
 * @version 2016-08-28
 */
@Service
@Transactional(readOnly = true)
public class JmsgUserGatewayService extends CrudService<JmsgUserGatewayDao, JmsgUserGateway> {

	public JmsgUserGateway get(String id) {
		return super.get(id);
	}
	
	public JmsgUserGateway getUserGatewayByUsername(String username){
		return dao.getUserGatewayByUsername(username);
	}
	
	public JmsgUserGateway getUserGatewayByUserid(String userid){
        return dao.getUserGatewayByUserid(userid);
    }
	
	public List<JmsgUserGateway> findList(JmsgUserGateway jmsgUserGateway) {
		return super.findList(jmsgUserGateway);
	}
	
	public Page<JmsgUserGateway> findPage(Page<JmsgUserGateway> page, JmsgUserGateway jmsgUserGateway) {
		return super.findPage(page, jmsgUserGateway);
	}
	
	@Transactional(readOnly = false)
	public void save(JmsgUserGateway jmsgUserGateway) {
		super.save(jmsgUserGateway);
	}
	
	@Transactional(readOnly = false)
	public void delete(JmsgUserGateway jmsgUserGateway) {
		super.delete(jmsgUserGateway);
	}
	
	/**
     * 修改用户通道状态
     * @param map
     * @see [类、类#方法、类#成员]
     */
	@Transactional(readOnly = false)
    public void updateStateById(JmsgUserGateway jmsgUserGateway)
    {
        Map<String, String> map = new HashMap<String, String>();
        map.put("id", jmsgUserGateway.getId());
        map.put("status", jmsgUserGateway.getStatus());
        dao.updateStateById(map);
    }
}