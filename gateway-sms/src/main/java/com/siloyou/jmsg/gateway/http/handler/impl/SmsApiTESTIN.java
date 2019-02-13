package com.siloyou.jmsg.gateway.http.handler.impl;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.siloyou.jmsg.common.message.SmsHttpRtMessageResponse;
import com.siloyou.jmsg.common.message.SmsMoMessage;
import com.siloyou.jmsg.common.message.SmsMtMessage;
import com.siloyou.jmsg.common.message.SmsRtMessage;
import com.siloyou.jmsg.common.message.SmsSrMessage;
import com.siloyou.jmsg.common.util.HttpRequest;
import com.siloyou.jmsg.gateway.http.handler.GateWayHttpAbstract;

public class SmsApiTESTIN extends GateWayHttpAbstract
{

    public SmsApiTESTIN(String gateWayID, String jsonParams)
    {
        super(gateWayID, jsonParams);
    }

    @Override
    public List<SmsSrMessage> doSend(SmsMtMessage mt)
    {
        Long ts = System.currentTimeMillis();
        Map<String, Object> param = Maps.newConcurrentMap();
        param.put("op", "Sms.send");
        param.put("apiKey", paramsMaps.get("apiKey"));
        param.put("ts", ts);
        param.put("templateId", paramsMaps.get("templateId"));
        param.put("phone", mt.getPhone());
        param.put("content", mt.getMsgContent());
        param.put("taskId", mt.getTaskid());
        String sign = HttpRequest.md5(HttpRequest.sortParams(param) + paramsMaps.get("secretKey"));
        param.put("sig", sign);
        
        String postBody = JSON.toJSONString(param);
        String json = HttpRequest.sendTextPost(paramsMaps.get("apiUrl"), postBody, null, "UTF-8", 120000);
        logger.info("网关:{}, 请求:{}, 响应:{}", this.gateWayID, postBody, json);
        
        List<SmsSrMessage> resultList = Lists.newArrayList();
        SmsSrMessage message = initGatewayResultMessage(json, mt, "code");
        if(StringUtils.equals(message.getResult(), "F10199")) {
            Map result = JSON.parseObject(json, Map.class);
            setGatewayResultCode(message, mt.getTaskid(), String.valueOf(result.get("code")), String.valueOf(result.get("msg")), "1000");
        }
        
        resultList.add(message);
        return resultList;
    }

    @Override
    public SmsHttpRtMessageResponse parseReport(String text, List<SmsRtMessage> rtList)
    {
    	logger.info("{}网关推送状态报告:{}", gateWayID, text);
    	if(StringUtils.isNotBlank(text)) {
	        Map<String, Object> resultMap = JSON.parseObject(text, Map.class);
	        if (StringUtils.equals(String.valueOf(resultMap.get("code")), "1000"))
	        {
	            List<Map<String,String>> restList = (List<Map<String,String>> )resultMap.get("list");
	            if (null != restList)
	            {
	            	SmsRtMessage smsRtMessage ;
	            	for(Map<String,String> item : restList) {
	            		smsRtMessage = initSmsRtMessage(item.get("taskId"), item.get("phone"), 
	            				String.valueOf(item.get("status")), "1");
	    				rtList.add(smsRtMessage);
	            	}
	            }
	        }
    	}
        return new SmsHttpRtMessageResponse("OK");
    }

    @Override
    public Map<String, String> parseDeliver(String text, List<SmsMoMessage> moList)
    {
    	logger.info("{}网关推送用户上行:{}", gateWayID, text);
    	if(StringUtils.isNotBlank(text)) {
    		Map<String, Object> resultMap = JSON.parseObject(text, Map.class);
	        if (StringUtils.equals(String.valueOf(resultMap.get("code")), "1000"))
	        {
	            List<Map<String,String>> restList = (List<Map<String,String>> )resultMap.get("list");
	            if (null != restList)
	            {
			    	SmsMoMessage smsMoMessage;
			    	for(Map<String,String> item : restList) {
						smsMoMessage = new SmsMoMessage();
						smsMoMessage.setSrcTermID(item.get("extNum"));
						smsMoMessage.setDestTermID(item.get("phone"));
						smsMoMessage.setMsgContent(item.get("content"));
						smsMoMessage.setMsgid(UUID.randomUUID().toString());
						smsMoMessage.setGateWayID(gateWayID);
						
						moList.add(smsMoMessage);
			        }
	            }
	        }
    	}
        return null;
    }

    @Override
    public String queryReport(String jsonText)
    {
        Long ts = System.currentTimeMillis();
        Map<String, Object> param = Maps.newConcurrentMap();
        param.put("op", "Sms.status");
        param.put("apiKey", paramsMaps.get("apiKey"));
        param.put("ts", ts);
        String sign = HttpRequest.md5(HttpRequest.sortParams(param) + paramsMaps.get("secretKey"));
        param.put("sig", sign);
        
        String postBody = JSON.toJSONString(param);
        String json = HttpRequest.sendTextPost(paramsMaps.get("apiUrl"), postBody, null, "UTF-8", 120000);
        
        return json;
    }

    @Override
    public String queryDeliver(String jsonText)
    {
    	Long ts = System.currentTimeMillis();
        Map<String, Object> param = Maps.newConcurrentMap();
        param.put("op", "Sms.mo");
        param.put("apiKey", paramsMaps.get("apiKey"));
        param.put("ts", ts);
        String sign = HttpRequest.md5(HttpRequest.sortParams(param) + paramsMaps.get("secretKey"));
        param.put("sig", sign);
        
        String postBody = JSON.toJSONString(param);
        String json = HttpRequest.sendTextPost(paramsMaps.get("apiUrl"), postBody, null, "UTF-8", 120000);
        
        return json;
    }
    
    public String balance()
    {
        Long ts = System.currentTimeMillis();
        Map<String, Object> param = Maps.newConcurrentMap();
        param.put("op", "Sms.account");
        param.put("apiKey", paramsMaps.get("apiKey"));
        param.put("ts", ts);
        String sign = HttpRequest.md5(HttpRequest.sortParams(param) + paramsMaps.get("secretKey"));
        param.put("sig", sign);
        
        String postBody = JSON.toJSONString(param);
        String json = HttpRequest.sendTextPost(paramsMaps.get("apiUrl"), postBody, null, "UTF-8", 120000);
        logger.info("网关:{}, 请求:{}, 响应:{}", this.gateWayID, postBody, json);
        
        Map<String,String> resultMap = JSON.parseObject(json, Map.class);
        if(StringUtils.equals( String.valueOf(resultMap.get("code")) , "1000")) {
            return resultMap.get("balance");
        }
        return json;
    }
    
    public static void main(String[] args)
    {
        SmsApiTESTIN api = new SmsApiTESTIN("TESTIN", "{\"apiKey\":\"fb3bb190ed24f441b63ebd6bf9c421d5\",\"secretKey\":\"07E2A8173F1B107F\",\"templateId\":\"1447\",\"apiUrl\":\"http://api.sms.testin.cn/sms\"}");
        
//        SmsMtMessage mt = new SmsMtMessage();
//        mt.setPhone("13666672546");
//        mt.setMsgContent("【车易贷】验证码:123456");
//        List<SmsSrMessage> list = api.doSend(mt);
//        
//        System.out.println(JSON.toJSONString(list));
        
//        System.out.println(api.queryReport(""));
        String text = "{\"op\":\"Sms.status\",\"list\":[{\"taskId\":\"T0905165512001686026619\",\"phone\":\"13666672546\",\"status\":0,\"descr\":\"SENDFAIL\"}],\"code\":1000,\"msg\":\"成功\"}";
        Map<String, Object> resultMap = JSON.parseObject(text, Map.class);
        
        List<Map<String,String>> restList = (List<Map<String,String>> )resultMap.get("list");
        for(Map<String,String> item : restList) {
        	System.out.println(item.get("taskId"));
        	System.out.println(item.get("phone"));
        	System.out.println(String.valueOf(item.get("status")));
        }
        
    }
    
}