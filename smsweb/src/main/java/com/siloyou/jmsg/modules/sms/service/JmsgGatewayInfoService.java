/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.sms.service;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sanerzone.common.support.dubbo.spring.annotation.DubboReference;
import com.sanerzone.smscenter.config.SmsConfigInterface;
import com.siloyou.core.common.persistence.Page;
import com.siloyou.core.common.service.CrudService;
import com.siloyou.jmsg.common.utils.GatewayUtils;
import com.siloyou.jmsg.modules.sms.dao.JmsgGatewayInfoDao;
import com.siloyou.jmsg.modules.sms.entity.JmsgGatewayInfo;

/**
 * 通道信息Service
 * @author zhukc
 * @version 2016-07-29
 */
@Service
@Transactional(readOnly = true)
public class JmsgGatewayInfoService extends CrudService<JmsgGatewayInfoDao, JmsgGatewayInfo> {
	
	@DubboReference
	private SmsConfigInterface smsConfig;
	
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
	    if("0".equals(jmsgGatewayInfo.getGatewayState())){
	    	GatewayUtils.del(jmsgGatewayInfo.getId());
	    	smsConfig.configGateway(2,jmsgGatewayInfo.getId(),null);
	    }else if("1".equals(jmsgGatewayInfo.getGatewayState())){
	    	GatewayUtils.put(jmsgGatewayInfo.getId(), jmsgGatewayInfo);
	    	dubboCache(jmsgGatewayInfo);
	    }
	}
	
	@Transactional(readOnly = false)
	public void save(JmsgGatewayInfo jmsgGatewayInfo) {
		super.delete(jmsgGatewayInfo);
		dao.insert(jmsgGatewayInfo);
		GatewayUtils.put(jmsgGatewayInfo.getId(),jmsgGatewayInfo);
		dubboCache(jmsgGatewayInfo);
	}

	private void dubboCache(JmsgGatewayInfo jmsgGatewayInfo) {
		com.sanerzone.common.modules.smscenter.entity.JmsgGatewayInfo dest = new com.sanerzone.common.modules.smscenter.entity.JmsgGatewayInfo();
		try {
			BeanUtils.copyProperties(dest, jmsgGatewayInfo);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		smsConfig.configGateway(1, jmsgGatewayInfo.getId(), dest);
	}
	
	@Transactional(readOnly = false)
	public void delete(JmsgGatewayInfo jmsgGatewayInfo) {
		super.delete(jmsgGatewayInfo);
		GatewayUtils.del(jmsgGatewayInfo.getId());
		smsConfig.configGateway(2,jmsgGatewayInfo.getId(),null);
	}
	
}