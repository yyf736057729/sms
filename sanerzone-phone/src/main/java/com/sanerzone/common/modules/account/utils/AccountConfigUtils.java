package com.sanerzone.common.modules.account.utils;

import java.util.Map;

import com.sanerzone.common.support.dubbo.spring.annotation.DubboService;
import com.sanerzone.smscenter.config.AccountConfigInterface;

@DubboService(interfaceClass = AccountConfigInterface.class, cluster = "broadcast")
public class AccountConfigUtils implements AccountConfigInterface{

	
	@Override
	public boolean configAccount(int type,String accId,Map<String,String> map) {
		switch (type) {
		case 1:
			AccountCacheUtils.put(accId, map);
			break;
		case 2:
			AccountCacheUtils.del(accId);
			break;
		default:
			break;
		}
		return false;
	}

}
