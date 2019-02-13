/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.sanerzone.common.modules.smscenter.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sanerzone.common.modules.smscenter.dao.JmsgGatewayGroupDao;
import com.sanerzone.common.modules.smscenter.entity.JmsgGatewayGroup;
import com.sanerzone.common.modules.smscenter.utils.CacheKeys;
import com.sanerzone.common.modules.smscenter.utils.GatewayGroupUtils;
import com.sanerzone.common.support.persistence.CrudService;
import com.sanerzone.common.support.persistence.Page;

/**
 * 通道分组Service
 * @author zhukc
 * @version 2016-07-29
 */
@Service
@Transactional(readOnly = true)
public class JmsgGatewayGroupService extends CrudService<JmsgGatewayGroupDao, JmsgGatewayGroup> {
	
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
//			List<Dict> dictLit = DictUtils.getDictList("phone_province");
//			for (Dict dict : dictLit) {
//				String provinceId = dict.getValue();
//				if("10".equals(provinceId)){
//					continue;
//				}
//				jmsgGatewayGroup.setProvinceId(provinceId);
//				dao.deleteByParam(jmsgGatewayGroup);
//				dao.insert(jmsgGatewayGroup);
//				GatewayGroupUtils.put(gatewayList(jmsgGatewayGroup));
//			}
		}else{
			dao.deleteByParam(jmsgGatewayGroup);
			dao.insert(jmsgGatewayGroup);
			GatewayGroupUtils.put(gatewayList(jmsgGatewayGroup));
		}
	}
	
	@Transactional(readOnly = false)
	public void delete(JmsgGatewayGroup jmsgGatewayGroup) {
		super.delete(jmsgGatewayGroup);
		String key = CacheKeys.getCacheGatewayGroupKey(jmsgGatewayGroup.getGroupId(), jmsgGatewayGroup.getPhoneType(), jmsgGatewayGroup.getProvinceId());
		GatewayGroupUtils.del(key,jmsgGatewayGroup.getGatewayId());
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
	}
	
	private List<JmsgGatewayGroup> gatewayList(JmsgGatewayGroup jmsgGatewayGroup){
		return dao.findGroupList(jmsgGatewayGroup);
	}
}