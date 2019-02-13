/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.sms.service;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sanerzone.common.support.dubbo.spring.annotation.DubboReference;
import com.sanerzone.smscenter.config.SmsConfigInterface;
import com.siloyou.core.common.persistence.Page;
import com.siloyou.core.common.service.CrudService;
import com.siloyou.jmsg.common.utils.SignUtils;
import com.siloyou.jmsg.modules.sms.dao.JmsgGatewaySignDao;
import com.siloyou.jmsg.modules.sms.entity.JmsgGatewaySign;

/**
 * 通道签名Service
 * @author zhukc
 * @version 2016-07-29
 */
@Service
@Transactional(readOnly = true)
public class JmsgGatewaySignService extends CrudService<JmsgGatewaySignDao, JmsgGatewaySign> {

	@DubboReference
	private SmsConfigInterface smsConfig;
	
	public JmsgGatewaySign get(String id) {
		return super.get(id);
	}
	
	public JmsgGatewaySign getByParam(JmsgGatewaySign param) {
		String method = Thread.currentThread() .getStackTrace()[1].getMethodName();
		logger.info(xmlName + "===>>>" + method);
		return dao.getByParam(param);
	}
	
	/**
     * 获取用户通道签名
     * @param param
     * @return
     * @see [类、类#方法、类#成员]
     */
    public List<JmsgGatewaySign> getUserGatewaySign(JmsgGatewaySign param) {
		String method = Thread.currentThread() .getStackTrace()[1].getMethodName();
		logger.info(xmlName + "===>>>" + method);
    	return dao.getUserGatewaySign(param);
    }
    
    /**
     * 账户下面已分配通道分组下的已经分配好的签名信息
     * @param page
     * @param param
     * @return
     * @see [类、类#方法、类#成员]
     */
    public Page<JmsgGatewaySign> getUserGatewaySingList(Page<JmsgGatewaySign> page, JmsgGatewaySign param) {
        param.setPage(page);
		String method = Thread.currentThread() .getStackTrace()[1].getMethodName();
		logger.info(xmlName + "===>>>" + method);
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
		smsConfig.configSign(1, toDest(jmsgGatewaySign));
	}
	
	@Transactional(readOnly = false)
	public void delete(JmsgGatewaySign jmsgGatewaySign) {
		super.delete(jmsgGatewaySign);
		SignUtils.del(jmsgGatewaySign);
		smsConfig.configSign(2, toDest(jmsgGatewaySign));
	}
	
	private com.sanerzone.common.modules.smscenter.entity.JmsgGatewaySign toDest(JmsgGatewaySign jmsgGatewaySign){
		com.sanerzone.common.modules.smscenter.entity.JmsgGatewaySign dest = new com.sanerzone.common.modules.smscenter.entity.JmsgGatewaySign();
		jmsgGatewaySign.setUserId(jmsgGatewaySign.getUser().getId());
		try {
			BeanUtils.copyProperties(dest, jmsgGatewaySign);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return dest;
	}
	
}