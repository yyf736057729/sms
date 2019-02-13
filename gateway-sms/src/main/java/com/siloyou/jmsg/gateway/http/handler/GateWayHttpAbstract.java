package com.siloyou.jmsg.gateway.http.handler;

import com.zx.sms.connect.manager.EventLoopGroupFactory;
import com.zx.sms.connect.manager.ExitUnlimitCirclePolicy;
import io.netty.util.concurrent.Future;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.Application;
import com.siloyou.jmsg.common.message.SmsHttpRtMessageResponse;
import com.siloyou.jmsg.common.message.SmsMoMessage;
import com.siloyou.jmsg.common.message.SmsMtMessage;
import com.siloyou.jmsg.common.message.SmsRtMessage;
import com.siloyou.jmsg.common.message.SmsSrMessage;
import com.siloyou.jmsg.common.storedMap.BDBStoredMapFactoryImpl;
import com.siloyou.jmsg.common.util.CachedMillisecondClock;
import com.siloyou.jmsg.common.util.Encodes;
import com.siloyou.jmsg.gateway.api.GateWayMessageAbstract;
//import com.tingfv.sms.connect.manager.EventLoopGroupFactory;
//import com.tingfv.sms.connect.manager.ExitUnlimitCirclePolicy;

public abstract class GateWayHttpAbstract
{
    protected Logger logger = LoggerFactory.getLogger(getClass());
    private static String STOREDKEY_FORMART = "%s_%s";
    
    protected String gateWayID = "";
    
    protected Map<String, String> paramsMaps = Maps.newHashMap();
    
    private boolean gatewaySign = false;
    
    private static GateWayMessageAbstract gateWayMessage;
    
    private Map<String, Serializable> storedMap ;
    
    public GateWayMessageAbstract getGateWayMessage()
    {
        if (gateWayMessage == null)
        {
            gateWayMessage = Application.applicationContext.getBean(GateWayMessageAbstract.class);
        }
        return gateWayMessage;
    }
    
    public boolean isGatewaySign()
    {
        return gatewaySign;
    }
    
    @SuppressWarnings("unchecked")
    public GateWayHttpAbstract(String gateWayID, String jsonParams)
    {
        this.gateWayID = gateWayID;
        if (StringUtils.isNotBlank(jsonParams))
        {
            jsonParams = Encodes.unescapeHtml(jsonParams);
            try
            {
                paramsMaps = (Map<String, String>)JSON.parseObject(jsonParams, Map.class);
                this.gatewaySign =
                    (StringUtils.isBlank(paramsMaps.get("gateway_sign")) || StringUtils.equals(paramsMaps.get("gateway_sign"),
                        "0")) ? false : true;
                
                storedMap = BDBStoredMapFactoryImpl.INS.buildMap("http", "message_" + gateWayID);
            }
            catch (Exception e)
            {
                logger.error("初始化通道:{},参数出错，参数:{}，错误信息：{}", gateWayID, jsonParams, e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    public SmsSrMessage initGatewayResultMessage(String httpBody, SmsMtMessage message, String key)
    {
        SmsSrMessage srMessage = new SmsSrMessage();
        srMessage.setMessage(message);
        if (StringUtils.isBlank(httpBody))
        { //返因结果为空 121111
            srMessage.setResult("F10101");
            srMessage.setReserve("接口返回空");
        }
        else if (StringUtils.startsWith(httpBody, "JYC.ERROR.HTTP."))
        { //HTTP调用异常 13112开头
            srMessage.setResult("F10102" + StringUtils.substring(httpBody, 16));
            srMessage.setReserve("接口调用异常");
        }
        else if (httpBody.indexOf(key) == -1)
        { //检查关键字段是否存在
            srMessage.setResult("F10103");
            srMessage.setReserve("接口返回格式错");
        }
        else
        { //请求成功未报错, 如果最终还是这个状态，可能是 解析结果失败
            srMessage.setResult("F10199");
            srMessage.setReserve("通过");
        }
        return srMessage;
    }
    
    public void setGatewayResultCode(SmsSrMessage message, String msgid, String resultCode, String resultMessage,
        String successCode)
    {
        message.setMsgid(msgid);
        if (StringUtils.equals(successCode, resultCode))
        {//提交通道成功
            message.setResult("0");
        }
        else
        {
            message.setResult("F10001");
            message.setReserve(resultCode + ":" + resultMessage);
        }
    }
    
    public SmsRtMessage initSmsRtMessage(String msgid, String destTermID, String stat, String successStat){
    	SmsRtMessage smsRtMessage = new SmsRtMessage();
    	smsRtMessage.setGateWayID(gateWayID);
    	smsRtMessage.setMsgid(msgid);
    	smsRtMessage.setDestTermID(destTermID);
		smsRtMessage.setStat(stat);
		if(StringUtils.equals(stat, successStat)) {
			smsRtMessage.setStat("DELIVRD");
		}
		
		smsRtMessage.setSubmitTime(DateFormatUtils.format(CachedMillisecondClock.INS.now(),"yyMMddHHmmss"));
		smsRtMessage.setDoneTime(DateFormatUtils.format(CachedMillisecondClock.INS.now(),"yyMMddHHmmss"));
		
		SmsMtMessage smsMtMessage = (SmsMtMessage) storedMap.remove(String.format(STOREDKEY_FORMART, msgid, destTermID));
    	if( null != smsMtMessage) {
    		smsRtMessage.setSmsMt(smsMtMessage);
    		smsRtMessage.setSrcTermID(smsMtMessage.getSpNumber());
    		smsRtMessage.setSmscSequence("0");
    		//smsRtMessage.setSubmitTime(smsMtMessage.getSubmitTime());
    	}
    	return smsRtMessage;
    }
    
    public boolean send(SmsMtMessage mt)
    {
        List<SmsSrMessage> srList = doSend(mt);
        
        for(SmsSrMessage message : srList) {
        	
        	if(StringUtils.isNotBlank(message.getMsgid()))
        		storedMap.put(String.format(STOREDKEY_FORMART, message.getMsgid(),  message.getMessage().getPhone()), message.getMessage());
        	
            getGateWayMessage().sendSmsSRMessage(message, this.gateWayID);
        }
        
        return true;
    }
    
    public SmsHttpRtMessageResponse report(String text, boolean isQuery)
    {
        List<SmsRtMessage> rtList = Lists.newArrayList();
        if (isQuery)
        { // 执行查询
            text = queryReport(text);
        }
        SmsHttpRtMessageResponse smsHttpRtMessageResponse = parseReport(text, rtList);
        
        for (SmsRtMessage message : rtList)
        {
            getGateWayMessage().sendSmsRTMessage(message, this.gateWayID);
        }
        
        return smsHttpRtMessageResponse;
    }
    
    public Map<String, String> deliver(String text, boolean isQuery)
    {
        List<SmsMoMessage> moList = Lists.newArrayList();
        Map<String, String> retMap = Maps.newConcurrentMap();
        if (isQuery)
        { // 执行查询
            text = queryDeliver(text);
        }
        retMap = parseDeliver(text, moList);
        
        for (SmsMoMessage message : moList)
        {
            getGateWayMessage().sendSmsMOMessage(message, this.gateWayID);
        }
        
        return retMap;
    }
    
    /**
     * 余额查询
     * @see [类、类#方法、类#成员]
     */
    public String balance()
    {
        return "未实现";
    }
    
    public void startListener(int reportSecTime, int deliverSecTime){
        if (reportSecTime > 0)
        {
            EventLoopGroupFactory.INS.submitUnlimitCircleTask(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    report("", true);
                    return true;
                }
            }, new ExitUnlimitCirclePolicy() {
                @Override
                public boolean notOver(Future future) {
                    return true;
                }
            }, reportSecTime * 1000);
        }
        
        if (deliverSecTime > 0)
        {
            EventLoopGroupFactory.INS.submitUnlimitCircleTask(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    deliver("", true);
                    return true;
                }
            }, new ExitUnlimitCirclePolicy() {
                @Override
                public boolean notOver(Future future) {
                    return true;
                }
            }, deliverSecTime * 1000);
        }
    }
    
    /**
     * 发送 
     * 如果实现群发功能，SmsMtMessage中phone字段以逗号分隔多个号码
     * @param mt
     * @see [类、类#方法、类#成员]
     */
    public abstract List<SmsSrMessage> doSend(SmsMtMessage mt);
    
    /**
     * 解析状态报告
     * @param text
     * @see [类、类#方法、类#成员]
     */
    public abstract SmsHttpRtMessageResponse parseReport(String text, List<SmsRtMessage> rtList);
    
    /**
     * 解析短信上行
     * @param text
     * @see [类、类#方法、类#成员]
     */
    public abstract Map<String, String> parseDeliver(String text, List<SmsMoMessage> moList);
    
    /**
     * 状态报告查询
     * @param jsonText
     * @see [类、类#方法、类#成员]
     */
    public abstract String queryReport(String jsonText);
    
    public abstract String queryDeliver(String jsonText);
    
}
