/**
 * zoneland.net Inc.
 * Copyright (c) 2002-2012 All Rights Reserved.
 */
package com.siloyou.jmsg.gateway.web;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Maps;
import com.Application;
import com.siloyou.jmsg.common.util.StreamUtils;
import com.siloyou.jmsg.gateway.api.GatewayFactory;
import com.siloyou.jmsg.gateway.http.handler.GateWayHttpAbstract;

@RestController
@RequestMapping(value = "/api/sms/deliver")
public class GatewayDeliver {

    private final static Logger   logger  = Logger.getLogger(GatewayDeliver.class);

    private final ExecutorService es      = Executors.newFixedThreadPool(30);

    private static GatewayFactory     gatewayFactory;
    
    private GatewayFactory getGatewayFactory(){
    	if( gatewayFactory == null ) {
    		gatewayFactory = (GatewayFactory) Application.applicationContext.getBean("gatewayFactory");
    	}
    	return gatewayFactory;
    }
    
    /**
     * POST1获取request.getInputStream()
     * ContentType=application/json; text/html
     * @param code
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/pr/{code}")
    public String POSTReport(@PathVariable String code, HttpServletRequest request, HttpServletResponse response) {
        String respText = null;
        try {
            String charEncoding = request.getCharacterEncoding();
            if (charEncoding == null) {
                charEncoding = "UTF-8";
            }
            respText = StreamUtils.InputStreamTOString(request.getInputStream(), charEncoding);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return push(code, respText, request, response);
    }
        
    /**
     * POST2 读取SpringMVC中的 RequestBody 默认ContentType的情况下，SpringMVC会自动解析，导致request.getInputStream()为空)
     * ContentType=application/x-www-form-urlencoded
     * @param code
     * @param bytes
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/nr/{code}")
    public String FormReport(@PathVariable String code, @RequestBody byte[] bytes, HttpServletRequest request, HttpServletResponse response) {
        String respText = null;
        try {
            String charEncoding = request.getCharacterEncoding();
            if (charEncoding == null) {
                charEncoding = "UTF-8";
            }
            respText = new String(bytes, charEncoding);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return push(code, respText, request, response);
    }
        
    /**
     * GET 请求 request.getQueryString()
     * @param code
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/gr/{code}")
    public String GETReport(@PathVariable String code, HttpServletRequest request, HttpServletResponse response) {
        String respText = request.getQueryString();
        return push(code, respText, request, response);
    }
    
    public String push(String code, String respText, HttpServletRequest request, HttpServletResponse response){
        logger.info(code + "上行推送:"  + respText);
        
        Map<String,String> deliverRetMap = null;
        if (StringUtils.isNotBlank(respText)) {
            GateWayHttpAbstract executer = (GateWayHttpAbstract)getGatewayFactory().getGateway(code);
            if( executer != null) 
            {
            	deliverRetMap = executer.deliver(respText, false);
            }
        }else{
            logger.debug(String.format("accept post error,ContentType:%s, method:%s, CharacterEncoding:%s", request.getContentType(), request.getMethod(), request.getCharacterEncoding()));
        }
        
        if(deliverRetMap == null || deliverRetMap.isEmpty()) {
        	deliverRetMap = Maps.newConcurrentMap();
        	deliverRetMap.put("contentType", "text/plain");
        	deliverRetMap.put("characterEncoding", "utf-8");
        	deliverRetMap.put("result", "ERR");
        }
        
        try {
            response.reset();
            response.setContentType(deliverRetMap.get("contentType"));
            response.setCharacterEncoding(deliverRetMap.get("characterEncoding"));
            response.getWriter().print(deliverRetMap.get("result"));
            return null;
        } catch (IOException e) {
            return null;
        }
    }
}
