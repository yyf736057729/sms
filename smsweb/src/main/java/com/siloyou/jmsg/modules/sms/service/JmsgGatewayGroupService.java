/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.sms.service;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.sanerzone.common.support.dubbo.spring.annotation.DubboReference;
import com.sanerzone.smscenter.config.SmsConfigInterface;
import com.siloyou.core.common.persistence.Page;
import com.siloyou.core.common.service.CrudService;
import com.siloyou.core.modules.sys.entity.Dict;
import com.siloyou.core.modules.sys.utils.DictUtils;
import com.siloyou.jmsg.common.utils.CacheKeys;
import com.siloyou.jmsg.common.utils.GatewayGroupUtils;
import com.siloyou.jmsg.modules.sms.dao.JmsgGatewayGroupDao;
import com.siloyou.jmsg.modules.sms.entity.JmsgGatewayGroup;

/**
 * 通道分组Service
 * @author zhukc
 * @version 2016-07-29
 */
@Service
@Transactional(readOnly = true)
public class JmsgGatewayGroupService extends CrudService<JmsgGatewayGroupDao, JmsgGatewayGroup> {
	
	@DubboReference
	private SmsConfigInterface smsConfig;
	
	public SmsConfigInterface getSmsConfig() {
		return smsConfig;
	}

	public JmsgGatewayGroup get(String id) {
		return super.get(id);
	}
	
	public JmsgGatewayGroup getByParam(JmsgGatewayGroup jmsgGatewayGroup){
		return dao.getByParam(jmsgGatewayGroup);
	}
	
	public List<JmsgGatewayGroup> findList(JmsgGatewayGroup jmsgGatewayGroup) {
		return super.findList(jmsgGatewayGroup);
	}
	
	public Page<JmsgGatewayGroup> findPage(Page<JmsgGatewayGroup> page, JmsgGatewayGroup jmsgGatewayGroup) {
		return super.findPage(page, jmsgGatewayGroup);
	}
	
	@Transactional(readOnly = false)
	public void save(JmsgGatewayGroup jmsgGatewayGroup) {
		if("10".equals(jmsgGatewayGroup.getProvinceId())){//全国
			List<Dict> dictLit = DictUtils.getDictList("phone_province");
			for (Dict dict : dictLit) {
				String provinceId = dict.getValue();
				if("10".equals(provinceId)){
					continue;
				}
				jmsgGatewayGroup.setProvinceId(provinceId);
				dao.deleteByParam(jmsgGatewayGroup);
				dao.insert(jmsgGatewayGroup);
				List<JmsgGatewayGroup> list = gatewayList(jmsgGatewayGroup);
				GatewayGroupUtils.put(list);
				dubboCache(list);
			}
		}else{
			dao.deleteByParam(jmsgGatewayGroup);
			dao.insert(jmsgGatewayGroup);
			List<JmsgGatewayGroup> list = gatewayList(jmsgGatewayGroup);
			GatewayGroupUtils.put(list);
			dubboCache(list);
		}
	}

	private void dubboCache(List<JmsgGatewayGroup> list) {
		List<com.sanerzone.common.modules.smscenter.entity.JmsgGatewayGroup> dest = Lists.newArrayList();
		com.sanerzone.common.modules.smscenter.entity.JmsgGatewayGroup destEntity;
		for (JmsgGatewayGroup entity : list) {
			destEntity = new com.sanerzone.common.modules.smscenter.entity.JmsgGatewayGroup();
			try {
				BeanUtils.copyProperties(destEntity, entity);
				dest.add(destEntity);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		
		smsConfig.configGatewayGroup(1, dest, "", "");
	}
	
	@Transactional(readOnly = false)
	public void delete(JmsgGatewayGroup jmsgGatewayGroup) {
		super.delete(jmsgGatewayGroup);
		String key = CacheKeys.getCacheGatewayGroupKey(jmsgGatewayGroup.getGroupId(), jmsgGatewayGroup.getPhoneType(), jmsgGatewayGroup.getProvinceId());
		GatewayGroupUtils.del(key,jmsgGatewayGroup.getGatewayId());
		smsConfig.configGatewayGroup(2, null, key, jmsgGatewayGroup.getGatewayId());
	}
	
	@Transactional(readOnly = false)
	public void delete(String id,String groupId,String phoneType,String provinceId,String gatewayId) {
		JmsgGatewayGroup jmsgGatewayGroup = new JmsgGatewayGroup();
		jmsgGatewayGroup.setId(id);
		jmsgGatewayGroup.setGroupId(groupId);
		jmsgGatewayGroup.setPhoneType(phoneType);
		jmsgGatewayGroup.setProvinceId(provinceId);
		super.delete(jmsgGatewayGroup);
		String key = CacheKeys.getCacheGatewayGroupKey(groupId, phoneType, provinceId);
		GatewayGroupUtils.del(key,gatewayId);
		smsConfig.configGatewayGroup(2, null, key,gatewayId);
	}
	
	private List<JmsgGatewayGroup> gatewayList(JmsgGatewayGroup jmsgGatewayGroup){
		return dao.findGroupList(jmsgGatewayGroup);
	}
}