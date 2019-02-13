package com.siloyou.jmsg.gateway.wav.message;

import java.io.Serializable;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import com.alibaba.fastjson.JSON;
import com.siloyou.jmsg.common.message.SmsMoMessage;
import com.siloyou.jmsg.common.message.SmsMtMessage;
import com.siloyou.jmsg.common.message.SmsRtMessage;
import com.siloyou.jmsg.common.message.SmsSrMessage;
import com.siloyou.jmsg.common.util.CommonUtils;
import com.siloyou.jmsg.gateway.api.GateWayMessageAbstract;

public class WavGateWayMessage extends GateWayMessageAbstract
{
    @Override
    protected SmsSrMessage convertSRMessage(Serializable smsSrMessage)
    {
        return (SmsSrMessage) smsSrMessage;
    }
    
    @Override
    protected SmsRtMessage convertRTMessage(Serializable smsRtMessage)
    {
        SmsRtMessage message = (SmsRtMessage)smsRtMessage;
        message.setDestTermID(CommonUtils.resetPhone(message.getDestTermID()));
        return message;
    }
    
    @Override
    protected SmsMoMessage convertMOMessage(Serializable smsRoMessage)
    {
        SmsMoMessage message = (SmsMoMessage)smsRoMessage;
        message.setDestTermID(CommonUtils.resetPhone(message.getDestTermID()));
        return message;
    }
    
    @Override
    public Serializable convertMTMessage(SmsMtMessage message, boolean gatewaySign)
    {
        String msgContent = message.getMsgContent();
        
        if (gatewaySign && StringUtils.isNotBlank(msgContent))
        {
            Map<String, String> map = JSON.parseObject(msgContent, Map.class);
            
            if (map.containsKey("dataid") && map.containsKey("msgContent") && StringUtils.isNotBlank(map.get("msgContent")))
            {
                //map.put("msgContent", map.get("msgContent").replaceAll("^\\【(.*?)\\】", ""));
                map.put("msgContent", map.get("msgContent"));
                message.setMsgContent(JSON.toJSONString(map));
            }
            
            //去除短信头部签名
            //message.setMsgContent(msgContent.replaceAll("^\\【(.*?)\\】", ""));
        }
        
        return message;
    }
    
}
