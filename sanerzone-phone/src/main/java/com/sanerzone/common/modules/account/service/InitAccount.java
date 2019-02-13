package com.sanerzone.common.modules.account.service;

import java.util.List;
import java.util.Map;

import com.sanerzone.common.modules.account.dao.BaseAccountDao;
import com.sanerzone.common.modules.account.utils.AccountCacheUtils;
import com.sanerzone.common.support.utils.SpringContextHolder;
import com.sanerzone.common.support.utils.StringUtils;

public class InitAccount {
	
	private BaseAccountDao baseAccountDao = SpringContextHolder.getBean(BaseAccountDao.class);
	
	public void initAccount(){
		List<Map<String,String>> list = baseAccountDao.findAccountList();
		if(list != null && list.size() >0){
			for (Map<String, String> result : list) {
				String accId = String.valueOf(result.get("id"));
				if(StringUtils.isNotBlank(accId)){
					AccountCacheUtils.put(accId, result);
				}
			}
		}
	}
}
