package com.sanerzone.smscenter.config.impl;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.sanerzone.smscenter.common.tools.AccountCacheHelper;
import com.sanerzone.smscenter.config.AccountConfigInterface;

@Service("accountConfig")
public class AccountConfigInterfaceImpl implements AccountConfigInterface {

	@Override
	public boolean configAccount(int type,String accId,Map<String,String> map) {
		switch (type) {
		case 1:
			AccountCacheHelper.put(accId, map);
			break;
		case 2:
			AccountCacheHelper.del(accId);
		default:
			break;
		}
		return false;
	}
}
