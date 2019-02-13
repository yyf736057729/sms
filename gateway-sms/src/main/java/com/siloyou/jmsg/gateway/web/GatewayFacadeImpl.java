/**
 * zoneland.net Inc.
 * Copyright (c) 2002-2012 All Rights Reserved.
 */
package com.siloyou.jmsg.gateway.web;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


//import org.apache.log4j.Logger;
import com.siloyou.jmsg.gateway.cmpp.CmppGatewayFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.Application;
import com.siloyou.jmsg.common.message.SmsMtMessage;
import com.siloyou.jmsg.gateway.Result;
import com.siloyou.jmsg.gateway.api.GatewayFacade;
import com.siloyou.jmsg.gateway.api.GatewayFactory;

@RestController
@RequestMapping(value = "/api/sms/gateway")
public class GatewayFacadeImpl implements GatewayFacade {

    //private final static Logger   logger  = Logger.getLogger(GatewayFacadeImpl.class);
    private final static Logger logger = LoggerFactory.getLogger(GatewayFacadeImpl.class);

    private final ExecutorService es      = Executors.newFixedThreadPool(30);

    private static GatewayFactory     gatewayFactory;

    private static CmppGatewayFactory cmppGatewayFactory;

    private CmppGatewayFactory getCmppGatewayFactory(){
        if(cmppGatewayFactory == null){
            cmppGatewayFactory = (CmppGatewayFactory)Application.applicationContext.getBean("cmppGatewayFactory");
        }
        return cmppGatewayFactory;
    }
    
    private GatewayFactory getGatewayFactory(){
    	if( gatewayFactory == null ) {
    		gatewayFactory = (GatewayFactory) Application.applicationContext.getBean("gatewayFactory");
    	}
    	return gatewayFactory;
    }

    @RequestMapping(value = "send")
    public Result sendMsg(SmsMtMessage msg) {
        try {
            logger.info("-----------------------sendMsg msg: {} ", JSON.toJSON(msg));
        	return getCmppGatewayFactory().sendMsg(msg);
        } catch (Exception e) {
            logger.error("发送消息失败,msg[" + msg + "].", e);
            if (e instanceof InterruptedException) {
                if (!getCmppGatewayFactory().hasGateway(msg.getGateWayID())) {
                    logger.error("InterruptedException restart gateway:" + msg.getGateWayID());
                    boolean res = getCmppGatewayFactory().closeGatewayTemp(msg.getGateWayID());
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
    public Result closeGateway(String id) {
        try {
            boolean res = getCmppGatewayFactory().closeGateway(id);
            if (res) {
                return new Result("T0", "成功");
            } else {
                return new Result("F1", "关闭网关失败");
            }
        } catch (Exception e) {
            logger.error("关闭网关失败", e);
            return new Result("F1", "关闭网关失败" + e.getMessage());
        }
    }

    @RequestMapping(value = "open")
    public Result openGateway(String id) {
        try {
            System.out.println(getCmppGatewayFactory().hasGateway(id));
            if (getCmppGatewayFactory().hasGateway(id)) {
                return new Result("T0", "网关已经开启");
            }
            boolean res = getCmppGatewayFactory().openGateway(id);
            if (res) {
                return new Result();
            } else {
                return new Result("F2", "开启网关失败");
            }
        } catch (Exception e) {
            logger.error("开启网关失败", e);
            return new Result("F2", "开启网关失败" + e.getMessage());
        }
    }

    @RequestMapping(value = "rate")
    public Result setGateWayRate(String id, int rate) {
        try {
            return getCmppGatewayFactory().setGatewaySendRate(id, rate);
        } catch (Exception e) {
            logger.error("设置网关速率失败", e);
            return new Result("F2", "设置网关速率失败" + e.getMessage());
        }
    }

    @RequestMapping(value = "status")
    public boolean isOpen(String id) {
        return getCmppGatewayFactory().hasGateway(id);
    }

    public List<Boolean> isOpen(List<String> list) {
        if (list == null || list.size() == 0) {
            return null;
        }
        List<Boolean> listResult = new ArrayList<Boolean>();
        for (int i = 0; i < list.size(); i++) {
            if (getCmppGatewayFactory().hasGateway(list.get(i))) {
                listResult.add(true);
            } else {
                listResult.add(false);
            }
        }
        logger.info("查询网关状态：" + listResult);
        return listResult;
    }

    @RequestMapping(value = "submitresult")
    public SmsMtMessage submitResult(String id, String msgid) {
        return getCmppGatewayFactory().getSubmitResult(id, msgid);
    }
    
    public List<Result> sendMsgBatch(List<SmsMtMessage> msgs) {
        return null;
    }

}
