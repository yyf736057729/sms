/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.sanerzone.common.modules.smscenter.service;

import java.util.ArrayList;
import java.util.List;

import com.sanerzone.common.modules.smscenter.dao.JmsgRuleGroupDao;
import com.sanerzone.common.modules.smscenter.dao.JmsgRuleInfoDao;
import com.sanerzone.common.modules.smscenter.dao.JmsgRuleRelationDao;
import com.sanerzone.common.modules.smscenter.entity.JmsgRuleGroup;
import com.sanerzone.common.modules.smscenter.entity.JmsgRuleInfo;
import com.sanerzone.common.modules.smscenter.entity.JmsgRuleRelation;
import com.sanerzone.common.modules.smscenter.utils.RuleUtils;
import com.sanerzone.common.support.utils.SpringContextHolder;
import com.sanerzone.common.support.utils.StringUtils;

/**
 * 规则管理Service
 * @author zj
 * @version 2017-03-26
 */
public class InitRuleInfo{
	
	private static JmsgRuleGroupDao jmsgRuleGroupDao = SpringContextHolder.getBean(JmsgRuleGroupDao.class);
	
	private static JmsgRuleInfoDao jmsgRuleInfoDao = SpringContextHolder.getBean(JmsgRuleInfoDao.class);
	
	private static JmsgRuleRelationDao jmsgRuleRelationDao = SpringContextHolder.getBean(JmsgRuleRelationDao.class);

	/**
	 * 实例化
	 */
	public void initRule()
	{
		JmsgRuleInfo ruleInfo = new JmsgRuleInfo();
		ruleInfo.setStatus("0");
		List<JmsgRuleInfo> list = jmsgRuleInfoDao.findList(ruleInfo);
		
		for (JmsgRuleInfo jmsgRuleInfo : list)
		{
			RuleUtils.put(jmsgRuleInfo);
		}
	}
	
	/**
	 * 实例化
	 */
	public void initRuleGroup()
	{
		JmsgRuleGroup param = new JmsgRuleGroup();
		param.setStatus("0");
		List<JmsgRuleGroup> list = jmsgRuleGroupDao.findList(param);
		
		JmsgRuleRelation paramRelation = new JmsgRuleRelation();
		List<JmsgRuleRelation> paramRelationList = jmsgRuleRelationDao.findList(paramRelation);
		
		List<JmsgRuleRelation> urleIdList = new ArrayList<JmsgRuleRelation>();
		
		for (JmsgRuleGroup jmsgRuleGroup : list)
		{
			urleIdList = new ArrayList<JmsgRuleRelation>();
			
			for (JmsgRuleRelation relation : paramRelationList)
			{
				if (StringUtils.equals(jmsgRuleGroup.getId(), relation.getGroupId()))
				{
					urleIdList.add(relation);
				}
			}
			
			RuleUtils.put(jmsgRuleGroup.getId(), urleIdList);
		}
	}
}