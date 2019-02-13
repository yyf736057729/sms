/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.account.dao;

import java.util.Map;

import com.siloyou.core.common.persistence.CrudDao;
import com.siloyou.core.common.persistence.annotation.MyBatisDao;
import com.siloyou.core.modules.sys.entity.Dict;
import com.siloyou.jmsg.modules.account.entity.JmsgAccount;
import java.util.List;
/**
 * 资金账户信息DAO接口
 * @author zhukc
 * @version 2016-05-17
 */
@MyBatisDao
public interface JmsgAccountDao extends CrudDao<JmsgAccount> {
	public void consumeMoney(JmsgAccount jmsgAccount); //消费
	public void rechargeMoney(JmsgAccount jmsgAccount);//充值
	public Long findUserMoeny(Map<String,String> map);//获取用户余额
	public void agencyDelete(JmsgAccount jmsgAccount);
	public JmsgAccount getByAgency(JmsgAccount jmsgAccount);
	
	public List<Dict> queryAccountList(Map<String,String> map);
	public void updatePayMode(Map<String,String> map);
	public String findUserPayMode(JmsgAccount jmsgAccount);
	public String findUsedFlag(JmsgAccount jmsgAccount);
	
	/**
	 * 获取余额提现的用户列表
	 * @return
	 * @see [类、类#方法、类#成员]
	 */
	public List<JmsgAccount> getBalanceCautionUsers();
	
	public List<Map<String,String>> getMapByUser(JmsgAccount jmsgAccount);
}
