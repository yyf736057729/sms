/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.sanerzone.common.modules.smscenter.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sanerzone.common.modules.smscenter.dao.JmsgGatewaySignDao;
import com.sanerzone.common.modules.smscenter.entity.JmsgGatewaySign;
import com.sanerzone.common.modules.smscenter.utils.SignUtils;
import com.sanerzone.common.support.persistence.CrudService;
import com.sanerzone.common.support.persistence.Page;

/**
 * 通道签名Service
 * @author zhukc
 * @version 2016-07-29
 */
@Service
@Transactional(readOnly = true)
public class JmsgGatewaySignService extends CrudService<JmsgGatewaySignDao, JmsgGatewaySign> {

	public JmsgGatewaySign get(String id) {
		return super.get(id);
	}
	
	public JmsgGatewaySign getByParam(JmsgGatewaySign param) {
		return dao.getByParam(param);
	}
	
	/**
     * 获取用户通道签名
     * @param param
     * @return
     * @see [类、类#方法、类#成员]
     */
    public List<JmsgGatewaySign> getUserGatewaySign(JmsgGatewaySign param)
    {
        return dao.getUserGatewaySign(param);
    }
    
    /**
     * 账户下面已分配通道分组下的已经分配好的签名信息
     * @param page
     * @param param
     * @return
     * @see [类、类#方法、类#成员]
     */
    public Page<JmsgGatewaySign> getUserGatewaySingList(Page<JmsgGatewaySign> page, JmsgGatewaySign param)
    {
        param.setPage(page);
        page.setList(dao.getUserGatewaySingList(param));
        return page;
    }
	
	public List<JmsgGatewaySign> findList(JmsgGatewaySign jmsgGatewaySign) {
		return super.findList(jmsgGatewaySign);
	}
	
	public Page<JmsgGatewaySign> findPage(Page<JmsgGatewaySign> page, JmsgGatewaySign jmsgGatewaySign) {
		return super.findPage(page, jmsgGatewaySign);
	}
	
	@Transactional(readOnly = false)
	public void save(JmsgGatewaySign jmsgGatewaySign) {
		super.save(jmsgGatewaySign);
		SignUtils.put(jmsgGatewaySign);
	}
	
	@Transactional(readOnly = false)
	public void delete(JmsgGatewaySign jmsgGatewaySign) {
		super.delete(jmsgGatewaySign);
		SignUtils.del(jmsgGatewaySign);
	}
	
}