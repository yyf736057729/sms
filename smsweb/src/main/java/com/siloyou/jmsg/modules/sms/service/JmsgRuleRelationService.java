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
import com.siloyou.jmsg.modules.sms.dao.JmsgRuleRelationDao;
import com.siloyou.jmsg.modules.sms.entity.JmsgRuleRelation;

/**
 * 规则关系Service
 * @author zj
 * @version 2017-03-26
 */
@Service
@Transactional(readOnly = true)
public class JmsgRuleRelationService extends CrudService<JmsgRuleRelationDao, JmsgRuleRelation> {
	@DubboReference
	private SmsConfigInterface smsConfig;
	
	public JmsgRuleRelation get(String id) {
		return super.get(id);
	}
	
	public List<JmsgRuleRelation> findList(JmsgRuleRelation jmsgRuleRelation) {
		return super.findList(jmsgRuleRelation);
	}
	
	public Page<JmsgRuleRelation> findPage(Page<JmsgRuleRelation> page, JmsgRuleRelation jmsgRuleRelation) {
		return super.findPage(page, jmsgRuleRelation);
	}
	
	@Transactional(readOnly = false)
	public void save(JmsgRuleRelation jmsgRuleRelation) {
		if("4".equals(jmsgRuleRelation.getRuleType())){
			JmsgRuleRelation param = null;
			for (String ruleId : jmsgRuleRelation.getRuleIdList()) {
				param = new JmsgRuleRelation(); 
				param.setGroupId(jmsgRuleRelation.getGroupId());
				param.setRuleType(jmsgRuleRelation.getRuleType());
				param.setRuleId(ruleId);
				super.save(param);
			}
		}else{
			super.save(jmsgRuleRelation);
		}
		smsConfig.configRuleGroup(1, jmsgRuleRelation.getGroupId(),toList(jmsgRuleRelation));

	}
	
	@Transactional(readOnly = false)
	public void delete(JmsgRuleRelation jmsgRuleRelation) {
		super.delete(jmsgRuleRelation);
		smsConfig.configRuleGroup(2, "", toDest(jmsgRuleRelation));
	}
	
	private com.sanerzone.common.modules.smscenter.entity.JmsgRuleRelation toDest(JmsgRuleRelation jmsgRuleRelation){
		com.sanerzone.common.modules.smscenter.entity.JmsgRuleRelation dest = new com.sanerzone.common.modules.smscenter.entity.JmsgRuleRelation();
		try {
			BeanUtils.copyProperties(dest, jmsgRuleRelation);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return dest;
	}
	
	private List<com.sanerzone.common.modules.smscenter.entity.JmsgRuleRelation> toList(JmsgRuleRelation jmsgRuleRelation){
		List<com.sanerzone.common.modules.smscenter.entity.JmsgRuleRelation> result = Lists.newArrayList();
		List<JmsgRuleRelation> list = dao.findList(jmsgRuleRelation);
		if(list != null && list.size() > 0){
			for (JmsgRuleRelation entity : list) {
				result.add(toDest(entity));
			}
		}
		
		return result;
	}
	
}