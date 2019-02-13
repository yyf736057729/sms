package com.sanerzone.common.modules.smscenter.service;

import java.util.List;

import com.sanerzone.common.modules.smscenter.dao.JmsgGatewayInfoDao;
import com.sanerzone.common.modules.smscenter.entity.JmsgGatewayInfo;
import com.sanerzone.common.modules.smscenter.utils.GatewayUtils;
import com.sanerzone.common.support.utils.SpringContextHolder;

public class InitGatewayInfo {
	private static JmsgGatewayInfoDao jmsgGatewayInfoDao = SpringContextHolder.getBean(JmsgGatewayInfoDao.class);
	
	//初始化通道信息
	public void initGatewayInfo(){
		List<JmsgGatewayInfo> list = jmsgGatewayInfoDao.findList(new JmsgGatewayInfo());
		if(list != null && list.size()>0){
			for (JmsgGatewayInfo jmsgGatewayInfo : list) {
				GatewayUtils.put(jmsgGatewayInfo.getId(), jmsgGatewayInfo);
			}
		}
	}
}
