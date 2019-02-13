/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.account.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.sanerzone.common.modules.account.utils.AccountCacheUtils;
import com.sanerzone.common.support.utils.JedisClusterUtils;
import com.siloyou.core.common.persistence.Page;
import com.siloyou.core.common.service.CrudService;
import com.siloyou.core.common.utils.StringUtils;
import com.siloyou.core.modules.sys.entity.User;
import com.siloyou.jmsg.modules.account.dao.JmsgAccountDao;
import com.siloyou.jmsg.modules.account.entity.JmsgAccount;

/**
 * 资金账户信息Service
 * @author zhukc
 * @version 2016-05-17
 */
@Service
@Transactional(readOnly = true)
public class JmsgAccountService extends CrudService<JmsgAccountDao, JmsgAccount> {
	
	@Autowired
	private JmsgAccountLogService jmsgAccountLogService;

	public JmsgAccount get(String id) {
		return super.get(id);
	}
	
	public JmsgAccount getByAgency(JmsgAccount jmsgAccount) {
		String method = Thread.currentThread() .getStackTrace()[1].getMethodName();
		logger.info(xmlName + "===>>>" + method);
		return dao.getByAgency(jmsgAccount);
	}
	
	public List<JmsgAccount> findList(JmsgAccount jmsgAccount) {
		return super.findList(jmsgAccount);
	}
	
	public Page<JmsgAccount> findPage(Page<JmsgAccount> page, JmsgAccount jmsgAccount) {
		return super.findPage(page, jmsgAccount);
	}
	
	@Transactional(readOnly = false)
	public void save(JmsgAccount jmsgAccount) {
		super.save(jmsgAccount);
	}
	
	@Transactional(readOnly = false)
	public void delete(JmsgAccount jmsgAccount) {
		super.delete(jmsgAccount);
	}
	
	@Transactional(readOnly = false)
	public void agencyDelete(JmsgAccount jmsgAccount){
		String method = Thread.currentThread() .getStackTrace()[1].getMethodName();
		logger.info(xmlName + "===>>>" + method);
		dao.agencyDelete(jmsgAccount);
	}
	
	
	/**
	 * 消费
	 * @param userId 用户ID
	 * @param changeCode 资金变动编码 可为空
	 * @param money 变动金额 单位:分
	 * @param
	 * @param remark 备注 可为空
	 * @param createUserId 创建人
	 * @param orderId 关联订单ID 可为空
	 */
	@Transactional(readOnly = false)
	public void consumeMoney(String userId, String changeCode,long money,String appType,String remark, String createUserId, String orderId) {
		
		JmsgAccount account = new JmsgAccount();
		User user = new User();
		user.setId(userId);
		account.setUser(user);
		account.setMoney(money);
		account.setAppType(appType);
		String method = Thread.currentThread() .getStackTrace()[1].getMethodName();
		logger.info(xmlName + "===>>>" + method);
		dao.consumeMoney(account);//消费
		
		String changeType = "XF";
		if(StringUtils.isNotBlank(changeCode)){
			changeType = changeType+changeCode;
		}
		//写日志
		jmsgAccountLogService.save(userId, changeType,-money,appType,remark, createUserId, orderId);
		
	}
	
	@Transactional(readOnly = false)
	public void consumeMoneyV2(String userId, String changeCode,long money,String appType,String remark, String createUserId, String orderId) {
		
		JmsgAccount account = new JmsgAccount();
		User user = new User();
		user.setId(userId);
		account.setUser(user);
		account.setMoney(money);
		account.setAppType(appType);
		logger.info(xmlName + "===>>>consumeMoney");
		dao.consumeMoney(account);//消费
	}
	
	/**
	 * 充值
	 * @param userId 用户ID
	 * @param changeCode 资金变动编码 可为空
	 * @param money 变动金额 单位:分
	 * @param
	 * @param remark 备注 可为空
	 * @param createUserId 创建人
	 * @param orderId 关联订单ID 可为空
	 */
	@Transactional(readOnly = false)
	public void rechargeMoney(String userId, String changeCode,long money,String appType,String remark, String createUserId, String orderId) {
		
		JmsgAccount account = new JmsgAccount();
		User user = new User();
		user.setId(userId);
		account.setUser(user);
		account.setMoney(money);
		account.setAppType(appType);
		String method = Thread.currentThread() .getStackTrace()[1].getMethodName();
		logger.info(xmlName + "===>>>" + method);
		dao.rechargeMoney(account);//充值
		
		String changeType = "CZ";
		if(StringUtils.isNotBlank(changeCode)){
			changeType = changeType+changeCode;
		}
		//写日志
		jmsgAccountLogService.save(userId, changeType,money,appType,remark, createUserId, orderId);
	}
	
	/**
	 * 新增账户
	 * @param userId 用户ID
	 * @param accountType 账号类型
	 * @param createUserId 创建人
	 * @param payMode 扣费方式
	 */
	@Transactional(readOnly = false)
	public void save(String userId,String appType,String createUserId,String payMode){
		JmsgAccount entity = new JmsgAccount();
		User user = new User();
		user.setId(userId);
		entity.setUser(user);
		entity.setAppType(appType);
		User createBy = new User();
		createBy.setId(createUserId);
		entity.setCreateBy(createBy);
		entity.setMoney(0L);
		entity.setPayMode(payMode);
		if(null==entity.getUsedFlag()){
			entity.setUsedFlag("1");//新增用户默认为已启用
		}
		super.save(entity);
	}
	
	/**
	 * 获取用户余额
	 * @param userId
	 * @param appType
	 * @return
	 */
	public Long findUserMoeny(String userId,String appType){
		Map<String,String> map = new HashMap<String, String>();
		map.put("userId", userId);
		map.put("appType", appType);
		String method = Thread.currentThread() .getStackTrace()[1].getMethodName();
		logger.info(xmlName + "===>>>" + method);
		Long money = dao.findUserMoeny(map);
		if(money == null){
			money = 0L;
		}
		return money;
	}
	
	public void updatePayMode(String userId,String appType,String payMode){
		Map<String,String> map = Maps.newHashMap();
		map.put("userId", userId);
		map.put("appType", appType);
		map.put("payMode", payMode);
		String method = Thread.currentThread() .getStackTrace()[1].getMethodName();
		logger.info(xmlName + "===>>>" + method);
		dao.updatePayMode(map);
	}
	
	/**
	 * 初始化用户账户
	 */
	public void initAccount(){
		List<JmsgAccount> list = findList(new JmsgAccount());
		if(list != null && list.size() > 0){
			String key="";
			for (JmsgAccount jmsgAccount : list) {
				key = AccountCacheUtils.getAmountKey("sms", jmsgAccount.getUser().getId());
				JedisClusterUtils.set(key, String.valueOf(jmsgAccount.getMoney()),0);
			}
		}
	}
	
	public List<Map<String,String>> getMapByUser(JmsgAccount jmsgAccount){
		String method = Thread.currentThread() .getStackTrace()[1].getMethodName();
		logger.info(xmlName + "===>>>" + method);
		return dao.getMapByUser(jmsgAccount);
	}
	
	//获取用户启用标识 1:启用 0:禁用
	public String findUsedFlag(String userId){
		JmsgAccount param = new JmsgAccount();
		param.setUserId(userId);
		param.setAppType("sms");
		String method = Thread.currentThread() .getStackTrace()[1].getMethodName();
		logger.info(xmlName + "===>>>" + method);
		return dao.findUsedFlag(param);
	}
	
}