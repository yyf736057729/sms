/**
 * zoneland.net Inc.
 * Copyright (c) 2002-2012 All Rights Reserved.
 */
package com.siloyou.jmsg.modules.api.cmpp.web;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;



//import org.apache.log4j.Logger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Application;
import com.siloyou.jmsg.common.message.SmsMtMessage;
import com.siloyou.jmsg.gateway.Result;
import com.siloyou.jmsg.gateway.api.GatewayFacade;
import com.siloyou.jmsg.gateway.api.GatewayFactory;

@RestController
@RequestMapping(value = "/api/cmpp/gateway")
public class SrvGatewayFacadeImpl implements GatewayFacade {

    private final static Logger logger = LoggerFactory.getLogger(SrvGatewayFacadeImpl.class);

    private final ExecutorService es      = Executors.newFixedThreadPool(30);

    private static GatewayFactory     gatewayFactory;
    
    private GatewayFactory getGatewayFactory(){
    	if( gatewayFactory == null ) {
    		gatewayFactory = (GatewayFactory) Application.applicationContext.getBean("gatewayFactory");
    	}
    	return gatewayFactory;
    }
    
    @RequestMapping(value = "send")
    public Result sendMsg(SmsMtMessage msg) {
        try {
            //logger.info("-----------------------sendMsg msg: {} ", JSON.toJSON(msg));
        	return getGatewayFactory().sendMsg(msg);
        } catch (Exception e) {
            logger.error("发送消息失败,msg[" + msg + "].", e);
            if (e instanceof InterruptedException) {
                if (!getGatewayFactory().hasGateway(msg.getGateWayID())) {
                    logger.error("InterruptedException restart gateway:" + msg.getGateWayID());
                    boolean res = getGatewayFactory().closeGatewayTemp(msg.getGateWayID());
                    if (res) {
                        openGateway(msg.getGateWayID());
                    }
                } else {
                    logger
                        .error("InterruptedException restart gateway...gateway is started...gatewayid:"
                               + msg.getGateWayID());
                }
            }
            return new Result("F0", e.getMessage());
        }
    }

    @RequestMapping(value = "close")
    public Result closeGateway(String userid) {
    	logger.info("关闭通道: {}" + userid);
        try {
            if (!getGatewayFactory().hasGateway(userid)) {
                return new Result("T0", "网关已经关闭");
            }
            boolean res = getGatewayFactory().closeGateway(userid);
            if (res) {
            	return new Result("T0", "网闭关闭成功");
            } else {
                return new Result("F1", "关闭网关失败");
            }
        } catch (Exception e) {
            logger.error("关闭网关失败", e);
            return new Result("F1", "关闭网关失败" + e.getMessage());
        }

    }

    @RequestMapping(value = "open")
    public Result openGateway(String userid) {
    	logger.info("开启通道: {}" + userid);
        try {
            if (getGatewayFactory().hasGateway(userid)) {
                return new Result("T0", "网关已经开启");
            }
            boolean res = getGatewayFactory().openGateway(userid);
            if (res) {
            	return new Result("T0", "网关开启成功");
            } else {
                return new Result("F2", "开启网关失败");
            }
        } catch (Exception e) {
            logger.error("开启网关失败", e);
            return new Result("F2", "开启网关失败" + e.getMessage());
        }
    }

    @RequestMapping(value = "status")
    public boolean isOpen(String userid) {
        return getGatewayFactory().hasGateway(userid);
    }

    public List<Boolean> isOpen(List<String> list) {
        if (list == null || list.size() == 0) {
            return null;
        }
        List<Boolean> listResult = new ArrayList<Boolean>();
        for (int i = 0; i < list.size(); i++) {
            if (getGatewayFactory().hasGateway(list.get(i))) {
                listResult.add(true);
            } else {
                listResult.add(false);
            }
        }
        logger.info("查询网关状态：" + listResult);
        return listResult;
    }
    
    @RequestMapping(value = "submitresult")
    public SmsMtMessage submitResult(String userid, String msgid) {
        return getGatewayFactory().getSubmitResult(userid, msgid);
    }
    
    public List<Result> sendMsgBatch(List<SmsMtMessage> msgs) {
        return null;
    }

}
