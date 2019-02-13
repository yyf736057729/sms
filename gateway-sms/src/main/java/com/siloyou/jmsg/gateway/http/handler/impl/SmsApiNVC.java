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

public class SmsApiNVC extends GateWayHttpAbstract
{

    public SmsApiNVC(String gateWayID, String jsonParams)
    {
        super(gateWayID, jsonParams);
    }
    
//    1
//    name
//    必填参数。用户账号
//    2
//    pswd
//    必填参数。用户密码
//    3
//    mobile
//    必填参数。合法的手机号码，号码间用英文逗号分隔
//     
//    4
//    msg
//    必填参数。短信内容，短信内容长度丌能超过 1000 个字。使用 URL 方式编码为 UTF-8 格式。短信内容(含签名)超过 70 个字符时以长 短信的格式发送(计费为 67 字/条)。
//    5
//    needstatus
//    可选参数。是否需要状态报告，默讣 true，true，表明需要状态报告; false 丌需要状态报告
//    6
//    sender
//     
//    可选参数。扩展码，用户定义扩展码，2 位，默讣空
//    7
//    type
//    可选参数。返回类型，默讣 json，暂丌支持其他
//    8
//    attime
//    可选参数。定时发送时间。yyyyMMddHHmm 格式

    @Override
    public List<SmsSrMessage> doSend(SmsMtMessage mt)
    {
        StringBuilder strBuffer = new StringBuilder();
        try
        {
            strBuffer.append("name=").append(paramsMaps.get("name"));
            strBuffer.append("&pswd=").append(paramsMaps.get("pswd"));
            strBuffer.append("&mobile=").append(mt.getPhone());
            strBuffer.append("&msg=").append(URLEncoder.encode(mt.getMsgContent(),"utf-8"));
        }
        catch (UnsupportedEncodingException e)
        {
            logger.error("网关:{}, 响应:{}", this.gateWayID, e);
        }
        
        String json =  HttpRequest.sendFormPost(paramsMaps.get("apiUrl") + "/send", strBuffer.toString(), null, "UTF-8", 3000);
        
        logger.info("网关:{}, 请求:{}, 响应:{}", this.gateWayID, strBuffer.toString(), json);
        
//        1
//        resptime
//        必返参数。响应时间，yyyyMMddHHmmss 格式
//        2
//        respstatus
//         
//        必返参数。响应状态，见 1.3.3 响应状态值
//        3
//        msgid
//        可返参数。respstatus 为 0 时才返回，13 位字符串，提供后续状态报 告匹配时使用。一次发送请求只返回一个 msgid，如 respstatus 丌为 0，此参数无
        
        List<SmsSrMessage> resultList = Lists.newArrayList();
        SmsSrMessage message = initGatewayResultMessage(json, mt, "respstatus");
        if(StringUtils.equals(message.getResult(), "F10199")) {
            Map result = JSON.parseObject(json, Map.class);
            setGatewayResultCode(message, String.valueOf(result.get("msgid")), String.valueOf(result.get("respstatus")), "", "0");
        }
        resultList.add(message);
        return resultList;
    }
    
    /**
     *  1
		msgid
		必返参数。提交短信时平台返回的 msgid，见 1.3.1
		2
		reporttime
		必返参数。响应时间，yyMMddHHmmss 格式
		3
		mobile
		必返参数。单一的手机号码
		4
		status
		必返参数。状态报告数值，见 2.3.3
     */
    
    @Override
    public SmsHttpRtMessageResponse parseReport(String text, List<SmsRtMessage> rtList)
    {
        logger.info("{}网关推送状态报告:{}", gateWayID, text);
        try {
            Map<String,String> paramers = HttpRequest.formParamToMap(text);
            if(paramers != null && !paramers.isEmpty()) {
	            SmsRtMessage smsRtMessage = initSmsRtMessage(paramers.get("msgid"), 
					            		paramers.get("mobile"), 
					            		paramers.get("status"), "DELIVRD");
				rtList.add(smsRtMessage);
				return new SmsHttpRtMessageResponse("1");
            }
        } catch(Exception e) {
        	logger.error("解析绿城接口状态报告失败: {}", e);
        }
        return new SmsHttpRtMessageResponse("ERR");       
    }
    
    
//    1
//    msgid
//    必返参数。用户上行回复的短信 id
//    2
//    reporttime
//    必返参数。响应时间，yyMMddHHmmss 格式
//    3
//    mobile
//    必返参数。单一的手机号码
//    4
//    destcode
//    必返参数。上行回复的目的号码
//    4
//    msg
//    必返参数。上行回复的短信内容

    @Override
    public Map<String, String> parseDeliver(String text, List<SmsMoMessage> moList)
    {
    	logger.info("{}网关推送用户上行:{}", gateWayID, text);
    	String result = "ERR";
        try {
        	Map<String,String> paramers = HttpRequest.formParamToMap(text);
        	if(paramers != null && !paramers.isEmpty()) {
                SmsMoMessage smsMoMessage = new SmsMoMessage();
    			smsMoMessage.setSrcTermID(paramers.get("destcode"));
    			smsMoMessage.setDestTermID(paramers.get("mobile"));
    			smsMoMessage.setMsgContent(paramers.get("msg"));
    			smsMoMessage.setMsgid(paramers.get("msgid"));
    			smsMoMessage.setGateWayID(gateWayID);
    			moList.add(smsMoMessage);
    			
    			result = "1";
        	}
        } catch (Exception e) {
        	logger.error("接收上行异常", e);
        }
        
        Map<String, String> deliverRetMap = Maps.newConcurrentMap();
    	deliverRetMap.put("contentType", "text/plain");
    	deliverRetMap.put("characterEncoding", "utf-8");
    	deliverRetMap.put("result", result);
        return deliverRetMap;
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
    
    
    /**
     * 1
	resptime
	必返参数。响应时间，yyyyMMddHHmmss 格式
	2
	respstatus
	必返参数。响应状态，见 1.3.3 响应状态值
	3
	respbalance
	可返参数。respstatus 为 0 时才返回当前余额，如 respstatus 丌为 0， 此参数无
     */
    public String balance()
    {
    	StringBuilder strBuffer = new StringBuilder();
        strBuffer.append("name=").append(paramsMaps.get("name"));
        strBuffer.append("&pswd=").append(paramsMaps.get("pswd"));
        String json =  HttpRequest.sendFormPost(paramsMaps.get("apiUrl") + "/balance", strBuffer.toString(), null, "UTF-8", 3000);
        if  ( StringUtils.isNotBlank(json) ) {
        	Map<String,String> result = JSON.parseObject(json, Map.class);
        	if(result != null && !result.isEmpty()){
	        	if(StringUtils.equals(String.valueOf(result.get("respstatus")), "0")) {
	        		return String.valueOf(result.get("respbalance"));
	        	}
	        	return result.get("respstatus");
        	}
        }
        return "获取余额异常， " + json;
    }
    
    public static void main(String[] args)
    {
        SmsApiNVC api = new SmsApiNVC("NVC", "{\"name\":\"kxkj\",\"pswd\":\"kxkj001\",\"apiUrl\":\"http://api.china95059.net:8080/sms\"}");
        
//        System.out.println(api.balance());
//        
//        SmsMtMessage mt = new SmsMtMessage();
//        mt.setPhone("13666672546");
//        mt.setMsgContent("【泛圣科技】验证码:123456");
//        List<SmsSrMessage> list = api.doSend(mt);
//        System.out.println(JSON.toJSONString(list));
        
        
        Map result = JSON.parseObject("{\"msgid\":160750026333859840,\"respstatus\":0,\"resptime\":\"20170319140312\"}", Map.class);
        if (StringUtils.equals("0", String.valueOf(result.get("respstatus")))) {
        	System.out.println("====");
        }
        
    }
    
}