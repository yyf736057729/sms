package com.siloyou.jmsg.modules.sms.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Application;
import com.siloyou.jmsg.gateway.Result;
import com.siloyou.jmsg.modules.api.cmpp.GateWayFactory;
import com.siloyou.jmsg.modules.sms.dao.JmsgDeiverNumberDao;
import com.siloyou.jmsg.modules.sms.entity.JmsgDeliverNumber;

@RestController
@RequestMapping("/api/sms/gateway")
public class SettingAction {

	@Autowired
    private JmsgDeiverNumberDao jmsgDeiverNumberDao;
	
	private static GateWayFactory     gatewayFactory;
    
    private GateWayFactory getGatewayFactory(){
    	if( gatewayFactory == null ) {
    		gatewayFactory = (GateWayFactory) Application.applicationContext.getBean("gatewayFactory");
    	}
    	return gatewayFactory;
    }
	
    @RequestMapping("/morule")
    public Result moRule() {
    	List<JmsgDeliverNumber> list = jmsgDeiverNumberDao.findList(null);
    	
    	Map<String, String> map = new HashMap<String, String>();
    	for(JmsgDeliverNumber deliverNumber : list) {
    		map.put(deliverNumber.getSpNumber(), String.valueOf(deliverNumber.getUserId()));
    	}
    	
		GateWayFactory.map = map;
    	return new Result("0", "T000");
    }
}
