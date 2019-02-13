package com.siloyou.jmsg.gateway.wav.handler.impl;

import java.util.ArrayList;
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
import com.siloyou.jmsg.gateway.wav.handler.GateWayWavAbstract;
import com.siloyou.jmsg.gateway.wav.message.VoiceMsgBody;

public class SmsApiVoice extends GateWayWavAbstract
{

    public SmsApiVoice(String gateWayID, String jsonParams)
    {
        super(gateWayID, jsonParams);
    }
    
    /**
     * 生成TTS
     * @param jsonText
     * @see [类、类#方法、类#成员]
     */
    public boolean buildTTS(SmsMtMessage mt)
    {
        Map<String, Object> param = Maps.newConcurrentMap();
        param.put("reqId", mt.getTaskid());
        //param.put("userId", mt.getUserid());
        param.put("userId", paramsMaps.get("userId"));
        
        //TODO 需要拆分短信内容，取出素材ID和内容
        logger.info("--------------buildTTS----------------{}",mt.getMsgContent());
        Map map = JSON.parseObject(mt.getMsgContent(), Map.class);
        
        if (map.containsKey("dataid") && map.containsKey("msgContent"))
        {
            param.put("fileName", map.get("dataid") + ".wav");
            param.put("content", map.get("msgContent"));
        }
        else
        {
            return false;
        }
        //param.put("content", mt.getMsgContent());
        
        param.put("action", "1002");
        
        String postBody = JSON.toJSONString(param);
        String json = HttpRequest.sendTextPost(paramsMaps.get("apiUrl") + "/recv", postBody, null, "UTF-8", 120000);
        logger.info("网关:{}, 请求:{}, 响应:{}", this.gateWayID, postBody, json);
        
        SmsSrMessage message = initGatewayResultMessage(json, mt, "status");
        if(StringUtils.equals(message.getResult(), "F10199")) {
            return true;
        }
        return false;
    }

    @Override
    public List<SmsSrMessage> doSend(SmsMtMessage mt)
    {
        Map<String, Object> param = Maps.newConcurrentMap();
        //param.put("id", mt.getTaskid());
        param.put("id", mt.getId());
        //param.put("userId", mt.getUserid());
        param.put("userId", paramsMaps.get("userId"));
        param.put("action", "1001");
        param.put("displayNum", mt.getSpNumber());      //通知时客户显示的主叫号码/呼入获取验证码的接入号
        param.put("playTimes", 1);                      //播放次数
        param.put("respUrl", paramsMaps.get("respUrl")); //语言通知结果回调接口地址
        /**
         * 通知类型：
         * NOTICE_TYPE_CALLOUT      6000    呼出获取
         * NOTICE_TYPE_CALLIN       6001    呼入获取
         * NOTICE_TYPE_ALL          6002    呼入呼出都支持
         */
        param.put("noticeMode", "6002");        //获取通知的方式
        //param.put("maxCallTime", mt.getUserid());       //通知最大时长，单位秒，如果设定此值，超时则通知结束
        //param.put("isSms", mt.getUserid());             //是否发生SMS短信，暂不支持
        
        Map map = JSON.parseObject(mt.getMsgContent(), Map.class);
        
        //TODO 分组tos对象
        List<VoiceMsgBody> tos = new ArrayList<VoiceMsgBody>();
        VoiceMsgBody to = new VoiceMsgBody();
        to.setReqId(mt.getPhone());
        to.setTo(mt.getPhone());
        to.setVerifyCode(map.get("msgContent").toString());
        to.setLang("5000");
        to.setPlayVerifyCode(map.get("dataid") + ".wav");
        
        tos.add(to);
        param.put("tos", tos);               //通知接收号码，最多32个
        
        
        String postBody = JSON.toJSONString(param);
        String json = HttpRequest.sendTextPost(paramsMaps.get("apiUrl") + "/recv", postBody, null, "UTF-8", 120000);
        logger.info("网关:{}, 请求:{}, 响应:{}", this.gateWayID, postBody, json);
        
        List<SmsSrMessage> resultList = Lists.newArrayList();
        SmsSrMessage message = initGatewayResultMessage(json, mt, "status");
        if(StringUtils.equals(message.getResult(), "F10199")) {
            Map result = JSON.parseObject(json, Map.class);
            setGatewayResultCode(message, String.valueOf(result.get("id")), String.valueOf(result.get("status")), String.valueOf(result.get("content")), "4000");
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
	        if (StringUtils.equals(String.valueOf(resultMap.get("action")), "1000"))
	        {
	            SmsRtMessage smsRtMessage = initSmsRtMessage(String.valueOf(resultMap.get("id")), String.valueOf(resultMap.get("reqId")), 
                    String.valueOf(resultMap.get("status")), "4000");
	            
	            rtList.add(smsRtMessage);
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
        SmsApiVoice api = new SmsApiVoice("TESTIN", "{\"apiKey\":\"fb3bb190ed24f441b63ebd6bf9c421d5\",\"secretKey\":\"07E2A8173F1B107F\",\"templateId\":\"1447\",\"apiUrl\":\"http://api.sms.testin.cn/sms\"}");
        
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