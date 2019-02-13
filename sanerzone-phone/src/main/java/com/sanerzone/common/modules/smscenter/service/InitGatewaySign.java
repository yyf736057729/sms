package com.sanerzone.common.modules.smscenter.service;

import java.util.List;

import com.sanerzone.common.modules.smscenter.dao.JmsgGatewaySignDao;
import com.sanerzone.common.modules.smscenter.entity.JmsgGatewaySign;
import com.sanerzone.common.modules.smscenter.utils.SignUtils;
import com.sanerzone.common.support.utils.SpringContextHolder;

public class InitGatewaySign {
	
	private static JmsgGatewaySignDao gatewaySignDao = SpringContextHolder.getBean(JmsgGatewaySignDao.class);
	
	/**
     * 初始化用户通道签名
     * @see [类、类#方法、类#成员]
     */
    public void initGatewaySign() {
        List<JmsgGatewaySign> list = gatewaySignDao.findList(new JmsgGatewaySign());
        if(list != null && list.size() >0){
	        for (JmsgGatewaySign gatewaySign : list){
	            SignUtils.put(gatewaySign);
	        }
        }
    }
    
}
