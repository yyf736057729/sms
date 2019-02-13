///**
// * zoneland.net Inc.
// * Copyright (c) 2002-2012 All Rights Reserved.
// */
//package com.siloyou.jmsg.gateway.web;
//
//import java.io.IOException;
//import java.io.Serializable;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Map;
//import java.util.Map.Entry;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
//import javax.annotation.PostConstruct;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import org.apache.commons.lang3.StringUtils;
//import org.apache.log4j.Logger;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.google.common.util.concurrent.RateLimiter;
//
//import com.siloyou.jmsg.common.storedMap.BDBStoredMapFactoryImpl;
//import com.siloyou.jmsg.gateway.sgip.SgipGatewayFactory;
//
//@RestController
//@RequestMapping(value = "/api")
//public class GatewayTest {
//
//    private final static Logger   logger  = Logger.getLogger(GatewayTest.class);
//
//    private final ExecutorService es      = Executors.newFixedThreadPool(30);
//    private static Map<String, Serializable> storedMap = BDBStoredMapFactoryImpl.INS.buildMap("LT8004", "387679");
//
//    private static SgipGatewayFactory     gatewayFactory;
//
//    RateLimiter gwRate = RateLimiter.create(60);
//    private int oidx = 31215;
//
//    @RequestMapping(value = "/test")
//    public String POSTReport(HttpServletRequest request, HttpServletResponse response) {
//
//
//    	int oidx = Integer.parseInt(request.getParameter("idx"));
//
//    	logger.info("test => size:" + storedMap.size());
//
////    	Map<String, String> params = new HashMap<String, String>();
////        params.put("host", "220.199.6.27");
////        params.put("port", "8801");
////        params.put("local-host", "120.55.82.118");
////        params.put("local-port", "18703");
////        params.put("read-timeout", "5");
////        params.put("reconnect-interval", "5");
////        params.put("transaction-timeout", "5");
////        params.put("heartbeat-interval", "5");
////        params.put("heartbeat-noresponseout", "5");
////        params.put("source-addr", "106550200475");
////        params.put("shared-secret", "106550200475");
////        params.put("version", "12");
////        params.put("spNumber", "106550200475");
////        params.put("debug", "true");
////        params.put("corpId", "00475");
////        params.put("msg_Src", "00475");
////        params.put("gateWayID", "LT8004");
////        params.put("limit", "100");
////
////
////    	SGIPSMProxy proxy = new SGIPSMProxy("LT8004", params);
////		try {
////			boolean flg = proxy.connect(params.get("source-addr"), params.get("shared-secret"));
////			if(flg) {
////
////	    	}
////		} catch (BindException e1) {
////			// TODO Auto-generated catch block
////			e1.printStackTrace();
////		} catch (IOException e1) {
////			// TODO Auto-generated catch block
////			e1.printStackTrace();
////		}
//
//
//
//    	 try {
//             response.reset();
//             response.setContentType("applications/json");
//             response.setCharacterEncoding("utf-8");
//             response.getWriter().print("ok");
//             return null;
//         } catch (IOException e) {
//             return null;
//         }
//    }
//
//
//
//}
