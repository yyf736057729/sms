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
import com.siloyou.jmsg.common.util.HttpRequest;
import com.siloyou.jmsg.gateway.http.handler.GateWayHttpAbstract;

public class SmsApiJIESMS extends GateWayHttpAbstract
{

    public SmsApiJIESMS(String gateWayID, String jsonParams)
    {
        super(gateWayID, jsonParams);
    }

    @Override
    public List<SmsSrMessage> doSend(SmsMtMessage mt)
    {
        Long ts = System.currentTimeMillis();
        StringBuffer strBuffer = new StringBuffer();
        
        try
        {
            strBuffer.append("account=").append(paramsMaps.get("account"));
            strBuffer.append("&password=").append(paramsMaps.get("password"));
            //短信类型 0普通短信 1长短信 可为空,值为空时默认为0,以普通短信发送
            strBuffer.append("&mobile=").append(mt.getPhone());
            strBuffer.append("&content=").append(URLEncoder.encode(mt.getMsgContent(),"utf-8"));
            strBuffer.append("&longSms=").append(paramsMaps.get("longSms"));
        }
        catch (UnsupportedEncodingException e)
        {
            logger.error("网关:{}, 响应:{}", this.gateWayID, e);
        }
        
        String json =  HttpRequest.sendGet(paramsMaps.get("apiUrl") + "/submit", strBuffer.toString(), null, "UTF-8", 10000);
        
        logger.info("网关:{}, 请求:{}, 响应:{}", this.gateWayID, strBuffer.toString(), json);
        
        List<SmsSrMessage> resultList = Lists.newArrayList();
        
        SmsSrMessage message = initGatewayResultMessage(json, mt, ",");
        String resultCode = null;
        String resultMessage = null;
        String taskid = null;
        
        if(StringUtils.equals(message.getResult(), "F10199")) {
        	
        	String[] lines = json.split("\n");
        	if(lines.length > 1) {
        		String[] line1 = lines[0].split(",");
        		 resultCode = line1[0];
                 resultMessage = line1[1];
                 
                 String[] line2 = lines[1].split(",");
                 taskid = line2[1];
        	}
        	
//        	String[] lines = json.split(",");
//        	if(lines.length > 2) {
//        		 resultCode = lines[0];
//                 resultMessage = lines[1];
//                 taskid = lines[2];
//        	}
        	
            setGatewayResultCode(message, taskid, resultCode, resultMessage, "0");
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
        System.out.println(strBuffer.toString());
        return HttpRequest.sendGet(paramsMaps.get("apiUrl") + "/mo", strBuffer.toString(), null);
    }
    
    public String balance()
    {
        return "";
    }
    
    public static void main(String[] args)
    {
//        SmsApiJIESMS api = new SmsApiJIESMS("JIESMS", "{\"account\":\"lihua\",\"password\":\"lihua#9988\",\"longSms\":\"1\",\"apiUrl\":\"http://www.88dx.cn\",\"reportSecTime\":\"5\", \"deliverSecTime\":\"5\"}");
    	SmsApiJIESMS api = new SmsApiJIESMS("JIESMS", "{\"account\":\"lihua\",\"password\":\"llh@9988\",\"longSms\":\"\",\"apiUrl\":\"http://www.88dx.cn\",\"reportSecTime\":\"5\", \"deliverSecTime\":\"5\"}");
        
        SmsMtMessage mt = new SmsMtMessage();
        mt.setPhone("13666672546");
        mt.setMsgContent("【泛圣科技】验证码:888888");
        List<SmsSrMessage> list = api.doSend(mt);
        System.out.println(JSON.toJSONString(list));
        
//        System.out.println(api.queryDeliver(""));
//        List<SmsRtMessage> rtList = Lists.newArrayList();
//        api.parseReport(api.queryReport(""), rtList);

        //        System.out.println(JSON.toJSON(rtList));
        
    }
    
}