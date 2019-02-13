package com.siloyou.jmsg.gateway.http.handler.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

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

public class SmsApiJYCSMS extends GateWayHttpAbstract
{

    public SmsApiJYCSMS(String gateWayID, String jsonParams)
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
            strBuffer.append(paramsMaps.get("apiUrl"));
            strBuffer.append("?method=smssend");
            strBuffer.append("&userid=").append(paramsMaps.get("userId"));
            //短信类型 0普通短信 1长短信 可为空,值为空时默认为0,以普通短信发送
            strBuffer.append("&smstype=").append(1);
            strBuffer.append("&phones=").append(mt.getPhone());
            strBuffer.append("&content=").append(URLEncoder.encode(mt.getMsgContent(),"utf-8"));
            strBuffer.append("&sendtime=").append(ts);
            strBuffer.append("&sendtermid=").append("");
            
            String md5Str = HttpRequest.md5(paramsMaps.get("userId")+"||"+mt.getPhone()+"||"+paramsMaps.get("passWord"));
            strBuffer.append("&md5=").append(md5Str);
        }
        catch (UnsupportedEncodingException e)
        {
            logger.error("网关:{}, 响应:{}", this.gateWayID, e);
        }
        
        String json =  HttpRequest.sendPost(strBuffer.toString(), null, null);
        
        logger.info("网关:{}, 请求:{}, 响应:{}", this.gateWayID, strBuffer.toString(), json);
        
        List<SmsSrMessage> resultList = Lists.newArrayList();
        
        SmsSrMessage message = initGatewayResultMessage(json, mt, "status");
        String resultCode = null;
        String resultMessage = null;
        String taskid = null;
        
        if(StringUtils.equals(message.getResult(), "F10199")) {
            Document doc;
            try {
                doc = DocumentHelper.parseText(json);
                Element root = doc.getRootElement();
                
                resultCode = root.attribute("status").getStringValue();
                resultMessage = root.attribute("statustext").getStringValue();
                
                taskid = HttpRequest.getElementValue(root, "taskid", "");
            } catch (DocumentException e) {
                logger.error("解析错误", e);
            } 
            setGatewayResultCode(message, taskid, resultCode, resultMessage, "0");
        }
        
        resultList.add(message);
        return resultList;
    }
    
    @Override
    public SmsHttpRtMessageResponse parseReport(String text, List<SmsRtMessage> rtList)
    {
        logger.info("{}网关推送状态报告:{}", gateWayID, text);
        Document doc;
        try {
            doc = DocumentHelper.parseText(text);
            Element root = doc.getRootElement();
            String taskid = HttpRequest.getElementValue(root, "taskid", "");
            
            Element relultList = root.element("list");
            if( null != relultList ) {
                List<Element> items = relultList.elements("item");
                
                SmsRtMessage smsRtMessage ;
                for(Element element : items) {
    				smsRtMessage = initSmsRtMessage(taskid, HttpRequest.getElementValue(element, "phone", ""), 
    						HttpRequest.getElementValue(element, "status", ""), "DELIVRD");
    				rtList.add(smsRtMessage);
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
    	Document doc;
        try {
            doc = DocumentHelper.parseText(text);
            Element root = doc.getRootElement();
            Element relultList = root.element("list");
            if( null != relultList ) {
                List<Element> items = relultList.elements("item");
                SmsMoMessage smsMoMessage;
                for(Element element : items) {
        			smsMoMessage = new SmsMoMessage();
        			smsMoMessage.setSrcTermID(HttpRequest.getElementValue(element, "recvnumber", ""));
        			smsMoMessage.setDestTermID(HttpRequest.getElementValue(element, "mobile", ""));
        			smsMoMessage.setMsgContent(HttpRequest.getElementValue(element, "smscontent", ""));
        			smsMoMessage.setMsgid(UUID.randomUUID().toString());
        			smsMoMessage.setGateWayID(gateWayID);
        			
        			moList.add(smsMoMessage);
                }
            }
        } catch (Exception e) {
        	
        }
        return null;
    }

    @Override
    public String queryReport(String jsonText)
    {
        return null;
    }

    @Override
    public String queryDeliver(String jsonText)
    {
        return null;
    }
    
    public String balance()
    {
        return "";
    }
    
    public static void main(String[] args)
    {
        SmsApiJYCSMS api = new SmsApiJYCSMS("JYCSMS", "{\"userId\":\"3862\",\"passWord\":\"6c022671511e4fb89bea0d6a61fc5dc4\",\"templateId\":\"1447\",\"apiUrl\":\"http://114.55.90.98:8083/sismsapi.go\"}");
        
        SmsMtMessage mt = new SmsMtMessage();
        mt.setPhone("13666672546");
        mt.setMsgContent("【泛圣科技】验证码:123456");
        List<SmsSrMessage> list = api.doSend(mt);
        
        System.out.println(JSON.toJSONString(list));
    }
    
}