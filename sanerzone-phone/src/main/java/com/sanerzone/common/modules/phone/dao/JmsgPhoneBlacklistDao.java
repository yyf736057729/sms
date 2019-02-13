/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.sanerzone.common.modules.phone.dao;

import java.util.List;

import com.sanerzone.common.modules.phone.entity.JmsgPhoneBlacklist;
import com.sanerzone.common.support.persistence.CrudDao;
import com.sanerzone.common.support.persistence.annotation.MyBatisDao;

/**
 * 黑名单DAO接口
 * @author zhukc
 * @version 2016-05-18
 */
@MyBatisDao
public interface JmsgPhoneBlacklistDao extends CrudDao<JmsgPhoneBlacklist> {
	public JmsgPhoneBlacklist getByPhone(String phone);
	
	public List<String> findAllPhone(Integer pageNo);
	
	public List<String> findTDPhone(Integer pageNo);//退订的手机号(群发黑名单)
	
	public List<String> findMarketPhone(Integer pageNo);//营销黑名单
	
	public List<String> findYzmPhone(Integer pageNo);//验证码黑名单
	
	public void deleteByPhone(String phone);
	
}