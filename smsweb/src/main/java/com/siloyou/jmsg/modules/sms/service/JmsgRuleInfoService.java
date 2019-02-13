/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.sms.service;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sanerzone.common.support.dubbo.spring.annotation.DubboReference;
import com.sanerzone.smscenter.config.SmsConfigInterface;
import com.siloyou.core.common.persistence.Page;
import com.siloyou.core.common.service.CrudService;
import com.siloyou.jmsg.common.utils.RuleUtils;
import com.siloyou.jmsg.modules.sms.dao.JmsgRuleInfoDao;
import com.siloyou.jmsg.modules.sms.dao.JmsgRuleRelationDao;
import com.siloyou.jmsg.modules.sms.entity.JmsgRuleInfo;
import com.siloyou.jmsg.modules.sms.entity.JmsgRuleRelation;

/**
 * 规则管理Service
 * @author zj
 * @version 2017-03-26
 */
@Service
@Transactional(readOnly = true)
public class JmsgRuleInfoService extends CrudService<JmsgRuleInfoDao, JmsgRuleInfo> {

	@DubboReference
	private SmsConfigInterface smsConfig;
	
	@Autowired
	private JmsgRuleRelationDao jmsgRuleRelationDao;
	
	public JmsgRuleInfo get(String id) {
		return super.get(id);
	}
	
	public List<JmsgRuleInfo> findList(JmsgRuleInfo jmsgRuleInfo) {
		return super.findList(jmsgRuleInfo);
	}
	
	public Page<JmsgRuleInfo> findPage(Page<JmsgRuleInfo> page, JmsgRuleInfo jmsgRuleInfo) {
		return super.findPage(page, jmsgRuleInfo);
	}
	
	@Transactional(readOnly = false)
	public void save(JmsgRuleInfo jmsgRuleInfo) {
		super.save(jmsgRuleInfo);
		RuleUtils.put(jmsgRuleInfo);
		smsConfig.configRule(1, toDest(jmsgRuleInfo));
	}
	
	@Transactional(readOnly = false)
	public void delete(JmsgRuleInfo jmsgRuleInfo) {
		super.delete(jmsgRuleInfo);
		RuleUtils.del(jmsgRuleInfo);
		smsConfig.configRule(2, toDest(jmsgRuleInfo));
		
		JmsgRuleRelation ruleRelation = new JmsgRuleRelation();
		ruleRelation.setRuleId(jmsgRuleInfo.getId());
		jmsgRuleRelationDao.deleteByRuleId(ruleRelation);
	}
	
	private com.sanerzone.common.modules.smscenter.entity.JmsgRuleInfo toDest(JmsgRuleInfo jmsgRuleInfo){
		com.sanerzone.common.modules.smscenter.entity.JmsgRuleInfo dest = new com.sanerzone.common.modules.smscenter.entity.JmsgRuleInfo();
		try {
			BeanUtils.copyProperties(dest, jmsgRuleInfo);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return dest;
	}
	
	//同步规则
	public String syncRule(){
		JmsgRuleInfo param = new JmsgRuleInfo();
		param.setStatus("0");
		List<JmsgRuleInfo> list = dao.findList(param);
		if(list != null && list.size() > 0){
			for (JmsgRuleInfo jmsgRuleInfo : list) {
				smsConfig.configRule(1, toDest(jmsgRuleInfo));
			}
		}
		return "1";
	}
	
	public List<JmsgRuleInfo> initRuleInfo(JmsgRuleInfo jmsgRuleInfo){
		return dao.initRuleInfo(jmsgRuleInfo);
	}
}