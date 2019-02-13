package com.sanerzone.common.modules.phone.service;

import java.util.List;

import com.sanerzone.common.modules.phone.dao.JmsgPhoneWhitelistDao;
import com.sanerzone.common.modules.phone.utils.WhitelistUtils;
import com.sanerzone.common.support.utils.SpringContextHolder;

public class InitWhitelist {
	private JmsgPhoneWhitelistDao jmsgPhoneWhitelistDao = SpringContextHolder.getBean(JmsgPhoneWhitelistDao.class);
	
	//初始化白名单
	public void initWhitelist(){ 
		List<String> list = jmsgPhoneWhitelistDao.findAllPhone();
		if(list != null && list.size() >0){
			WhitelistUtils.put(list);
		}
	}
}
