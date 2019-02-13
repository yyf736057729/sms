package com.sanerzone.common.modules.smscenter.service;

import java.util.List;
import java.util.Map;

import com.sanerzone.common.modules.smscenter.dao.JmsgGatewayGroupDao;
import com.sanerzone.common.modules.smscenter.entity.JmsgGatewayGroup;
import com.sanerzone.common.modules.smscenter.utils.GatewayGroupUtils;
import com.sanerzone.common.support.utils.SpringContextHolder;

public class InitGatewayGroup {

	private static JmsgGatewayGroupDao jmsgGatewayGroupDao = SpringContextHolder.getBean(JmsgGatewayGroupDao.class);
	
	//初始化通道分组
	public void initGatewayGroup(){
		List<JmsgGatewayGroup> list = jmsgGatewayGroupDao.findList(new JmsgGatewayGroup());
		if(list != null && list.size() >0){
			Map<String,List<String>> map = GatewayGroupUtils.gatewayGroupMap(list);
			for (Map.Entry<String, List<String>> entry : map.entrySet()) {
				GatewayGroupUtils.put(entry.getKey(),entry.getValue());
			}
		}
	}

}
