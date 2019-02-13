package com.siloyou.jmsg.gateway.http.handler.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.siloyou.jmsg.common.message.SmsHttpRtMessageResponse;
import com.siloyou.jmsg.common.message.SmsMoMessage;
import com.siloyou.jmsg.common.message.SmsMtMessage;
import com.siloyou.jmsg.common.message.SmsRtMessage;
import com.siloyou.jmsg.common.message.SmsSrMessage;
import com.siloyou.jmsg.common.util.DateUtils;
import com.siloyou.jmsg.common.util.HttpRequest;
import com.siloyou.jmsg.gateway.http.handler.GateWayHttpAbstract;

public class SmsApiBJSY extends GateWayHttpAbstract
{

    public SmsApiBJSY(String gateWayID, String jsonParams)
    {
        super(gateWayID, jsonParams);
    }

    @Override
    public List<SmsSrMessage> doSend(SmsMtMessage mt)
    {
        String seed = DateUtils.getDate("yyyyMMddHHmmss");
        String key = HttpRequest.md5(HttpRequest.md5(paramsMaps.get("passwd")) + seed);
        String content = mt.getMsgContent();
        try {
			content = URLEncoder.encode(content, "GBK");
		} catch (UnsupportedEncodingException e1) {		
			e1.printStackTrace();
		}
        
        StringBuffer strBuffer = new StringBuffer();
    	strBuffer.append("name=").append(paramsMaps.get("name"));
		strBuffer.append("&seed=").append(seed);
		strBuffer.append("&key=").append(key);
		strBuffer.append("&dest=").append(mt.getPhone());
		strBuffer.append("&content=").append(content);
		strBuffer.append("&ext=");
		strBuffer.append("&reference=").append(mt.getTaskid());
        
        String result =  HttpRequest.sendGet(paramsMaps.get("apiUrl") + "/eums/send_strong.do", strBuffer.toString(), null, "GBK", 5000);
        
        logger.info("网关:{}, 请求:{}, 响应:{}", this.gateWayID, strBuffer.toString(), result);
        
        List<SmsSrMessage> resultList = Lists.newArrayList();
        
        SmsSrMessage message = initGatewayResultMessage(result, mt, ":");
        String resultCode = null;
        String resultMessage = null;
        String taskid = null;
        
        if(StringUtils.equals(message.getResult(), "F10199")) {
        	
        	
        	String resultRows[] = result.split(":");
			if(resultRows.length > 1){
				resultCode = resultRows[0];
				if("success".equals(resultCode)){
					taskid = resultRows[1];
				}else{
					resultCode = resultRows[1];
					resultMessage = resultRows[2];
				}
			}
        	
            setGatewayResultCode(message, taskid, resultCode, resultMessage, "success");
        }
        
        resultList.add(message);
        return resultList;
    }
    
    @Override
    public SmsHttpRtMessageResponse parseReport(String text, List<SmsRtMessage> rtList)
    {
        logger.info("{}网关推送状态报告:{}", gateWayID, text);
        try {
        	if(StringUtils.isNotBlank(text)) {
        		String[] lines = text.split("\n");
        		if(lines.length > 0) {
        			String[] line = lines[0].split(",");
        			if(StringUtils.equals(line[0], "0")) {
        				SmsRtMessage smsRtMessage ;
        				for (int i = 1; i < lines.length; i++) {
        					if(StringUtils.isNotBlank(lines[i])) {
	        					line = lines[i].split(",");
	        					smsRtMessage = initSmsRtMessage(line[0], line[1], line[2], "1");
	            				rtList.add(smsRtMessage);
        					}
						}
        			}
        		}
        	}
        } catch(Exception e) {
        	 return new SmsHttpRtMessageResponse("ERR");
        }
        return new SmsHttpRtMessageResponse("OK");
    }

    @Override
    public Map<String, String> parseDeliver(String text, List<SmsMoMessage> moList)
    {
    	logger.info("{}网关推送用户上行:{}", gateWayID, text);
        try {
        	if(StringUtils.isNotBlank(text)) {
        		String[] lines = text.split("\n");
        		if(lines.length > 0) {
        			String[] line = lines[0].split(",");
        			if(StringUtils.equals(line[0], "0")) {
        				SmsMoMessage smsMoMessage;
        				for (int i = 1; i < lines.length; i++) {
        					if(StringUtils.isNotBlank(lines[i])) {
	        					line = lines[i].split(",");
	        					smsMoMessage = new SmsMoMessage();
	                			smsMoMessage.setSrcTermID("");
	                			smsMoMessage.setDestTermID(line[1]);
	                			smsMoMessage.setMsgContent(line[2]);
	                			smsMoMessage.setMsgid(UUID.randomUUID().toString());
	                			smsMoMessage.setGateWayID(gateWayID);
	                			
	                			moList.add(smsMoMessage);
        					}
						}
        			}
        		}
        	}
        } catch (Exception e) {
        	
        }
        return null;
    }

    @Override
    public String queryReport(String jsonText)
    {
    	StringBuffer strBuffer = new StringBuffer();
        strBuffer.append("account=").append(paramsMaps.get("account"));
        strBuffer.append("&password=").append(paramsMaps.get("password"));
        return HttpRequest.sendGet(paramsMaps.get("apiUrl") + "/report", strBuffer.toString(), null);
    }

    @Override
    public String queryDeliver(String jsonText)
    {
    	StringBuffer strBuffer = new StringBuffer();
        strBuffer.append("account=").append(paramsMaps.get("account"));
        strBuffer.append("&password=").append(paramsMaps.get("password"));
        return HttpRequest.sendGet(paramsMaps.get("apiUrl") + "/mo", strBuffer.toString(), null);
    }
    
    public String balance()
    {
        return "";
    }
    
    public static void main(String[] args)
    {
        SmsApiBJSY api = new SmsApiBJSY("BJSY", "{\"name\":\"hzpxyx\",\"passwd\":\"6k8u70gg\",\"apiUrl\":\"http://smsapi.wuxian365.cn:8080\"}");
        
        SmsMtMessage mt = new SmsMtMessage();
        mt.setPhone("13666672546");
        mt.setMsgContent("【泛圣科技】验证码:888888");
        List<SmsSrMessage> list = api.doSend(mt);
//        
        System.out.println(JSON.toJSONString(list));
        
        
//        List<SmsRtMessage> rtList = Lists.newArrayList();
//        api.parseReport(api.queryReport(""), rtList);
////        
//        System.out.println(JSON.toJSON(rtList));
        
    }
    
}