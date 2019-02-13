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
import org.springframework.beans.BeanUtils;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.siloyou.jmsg.common.message.SmsHttpRtMessageResponse;
import com.siloyou.jmsg.common.message.SmsMoMessage;
import com.siloyou.jmsg.common.message.SmsMtMessage;
import com.siloyou.jmsg.common.message.SmsRtMessage;
import com.siloyou.jmsg.common.message.SmsSrMessage;
import com.siloyou.jmsg.common.util.HttpRequest;
import com.siloyou.jmsg.gateway.http.handler.GateWayHttpAbstract;

public class SmsApiMYSMS extends GateWayHttpAbstract
{

    public SmsApiMYSMS(String gateWayID, String jsonParams)
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
        	strBuffer.append("action=send");
            strBuffer.append("&userid=").append(paramsMaps.get("userid"));
            strBuffer.append("&account=").append(paramsMaps.get("account"));
            strBuffer.append("&password=").append(paramsMaps.get("password"));
            strBuffer.append("&mobile=").append(mt.getPhone());
            strBuffer.append("&content=").append(URLEncoder.encode(mt.getMsgContent(),"utf-8"));
            strBuffer.append("&extno=").append("");
            
        }
        catch (UnsupportedEncodingException e)
        {
            logger.error("网关:{}, 响应:{}", this.gateWayID, e);
        }
        
        String json =  HttpRequest.sendGet(paramsMaps.get("apiUrl") + "/sms.aspx", strBuffer.toString(), null);
        
        logger.info("网关:{}, 请求:{}, 响应:{}", this.gateWayID, strBuffer.toString(), json);
        
        List<SmsSrMessage> resultList = Lists.newArrayList();
        
        SmsSrMessage message = initGatewayResultMessage(json, mt, "returnsms");
        String resultCode = null;
        String resultMessage = null;
        String taskid = null;
        
        if(StringUtils.equals(message.getResult(), "F10199")) {
            Document doc;
            try {
                doc = DocumentHelper.parseText(json);
                Element root = doc.getRootElement();
                
                resultCode = HttpRequest.getElementValue(root, "returnstatus", "");
                resultMessage = HttpRequest.getElementValue(root, "message", "");
                
                taskid = HttpRequest.getElementValue(root, "taskID", "");
            } catch (DocumentException e) {
                logger.error("解析错误", e);
            } 
            setGatewayResultCode(message, taskid, resultCode, resultMessage, "Success");
        }
        
        // 批量提交
        if(StringUtils.isBlank(mt.getId())) {
        	String[] phones = mt.getPhone().split(",");
            for(String phone : phones) {
            	if(phone.length() != 11) {
            		continue;
            	}
            	SmsMtMessage mtmsg = new SmsMtMessage();
            	BeanUtils.copyProperties(mt, mtmsg);
            	mtmsg.setId(mt.getTaskid() + phone.substring(1));
            	mtmsg.setPhone(phone);
            	SmsSrMessage srmsg = new SmsSrMessage();
            	BeanUtils.copyProperties(message, srmsg);
            	srmsg.setMessage(mtmsg);
            	resultList.add(srmsg);
            }
        } else {
        	resultList.add(message);
        }
        return resultList;
    }
    
    @Override
    public SmsHttpRtMessageResponse parseReport(String text, List<SmsRtMessage> rtList)
    {
        logger.info("{}网关推送状态报告:{}", gateWayID, text);
        Document doc;
        try
        {
            doc = DocumentHelper.parseText(text);
            Element root = doc.getRootElement();
            //String taskid = HttpRequest.getElementValue(root, "taskid", "");
            
            List<Element> items = root.elements();
            
            SmsRtMessage smsRtMessage;
            for (Element element : items)
            {
                smsRtMessage =
                    initSmsRtMessage(HttpRequest.getElementValue(element, "taskid", ""),
                        HttpRequest.getElementValue(element, "mobile", ""),
                        HttpRequest.getElementValue(element, "status", ""),
                        "10");
                rtList.add(smsRtMessage);
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
            List<Element> items = root.elements();
            
            SmsMoMessage smsMoMessage;
            for(Element element : items) {
                smsMoMessage = new SmsMoMessage();
                smsMoMessage.setSrcTermID(HttpRequest.getElementValue(element, "extno", ""));
                smsMoMessage.setDestTermID(HttpRequest.getElementValue(element, "mobile", ""));
                smsMoMessage.setMsgContent(HttpRequest.getElementValue(element, "content", ""));
                smsMoMessage.setMsgid(UUID.randomUUID().toString());
                smsMoMessage.setGateWayID(gateWayID);
                
                moList.add(smsMoMessage);
            }
        } catch (Exception e) {
        	
        }
        return null;
    }

    @Override
    public String queryReport(String jsonText)
    {
        StringBuffer strBuffer = new StringBuffer();
        strBuffer.append("action=query");
        strBuffer.append("&userid=").append(paramsMaps.get("userid"));
        strBuffer.append("&account=").append(paramsMaps.get("account"));
        strBuffer.append("&password=").append(paramsMaps.get("password"));
        return HttpRequest.sendGet(paramsMaps.get("apiUrl") + "/statusApi.aspx", strBuffer.toString(), null);
    }

    @Override
    public String queryDeliver(String jsonText)
    {
        StringBuffer strBuffer = new StringBuffer();
        strBuffer.append("action=query");
        strBuffer.append("&userid=").append(paramsMaps.get("userid"));
        strBuffer.append("&account=").append(paramsMaps.get("account"));
        strBuffer.append("&password=").append(paramsMaps.get("password"));
        return HttpRequest.sendGet(paramsMaps.get("apiUrl") + "/callApi.aspx", strBuffer.toString(), null);
    }
    
    public String balance()
    {
        return "";
    }
    
    public static void main(String[] args)
    {
        SmsApiMYSMS api = new SmsApiMYSMS("MYSMS", "{\"userid\":\"413\",\"account\":\"caixiu139\", \"password\":\"cx201654841\",\"apiUrl\":\"http://182.92.224.167:8888\"}");
        
        /*SmsMtMessage mt = new SmsMtMessage();
        //mt.setPhone("13666672546");
        mt.setPhone("15996480329");
        mt.setMsgContent("【彩秀】您的验证码是666666");
        List<SmsSrMessage> list = api.doSend(mt);
        
        System.out.println(JSON.toJSONString(list));*/
        
        /*List<SmsRtMessage> rtList = Lists.newArrayList();
        api.parseReport(api.queryReport(""), rtList);
        System.out.println(JSON.toJSON(rtList));*/
        
        StringBuilder strBu = new StringBuilder();
        strBu.append("<?xml version=\"1.0\" encoding=\"utf-8\" ?>");
        strBu.append("<returnsms>");
        strBu.append("  <callbox>");
        strBu.append("      <mobile>15996480329</mobile>");
        strBu.append("      <taskid>3766535</taskid>");
        strBu.append("      <content>测试</content>");
        strBu.append("      <receivetime>2016/11/9 10:07:02</receivetime>");
        strBu.append("      <extno>10658139990055413</extno>");
        strBu.append("  </callbox>");
        strBu.append("  <callbox>");
        strBu.append("      <mobile>15996480329</mobile>");
        strBu.append("      <taskid>3766535</taskid>");
        strBu.append("      <content>熊熊</content>");
        strBu.append("      <receivetime>2016/11/9 10:07:11</receivetime>");
        strBu.append("      <extno>10658139990055413</extno>");
        strBu.append("  </callbox>");
        strBu.append("</returnsms>");
                                        
//        List<SmsMoMessage> moList = Lists.newArrayList();
//        //api.parseDeliver(api.queryDeliver(""), moList);
//        api.parseDeliver(strBu.toString(), moList);
//        System.out.println(JSON.toJSON(moList));
        
        System.out.println("13666672546".substring(1));
    }
    
}