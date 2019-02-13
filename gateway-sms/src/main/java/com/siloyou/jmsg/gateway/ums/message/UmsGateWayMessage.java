package com.siloyou.jmsg.gateway.ums.message;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;
import org.marre.wap.push.SmsMmsNotificationMessage;
import org.marre.wap.push.SmsWapPushMessage;
import org.marre.wap.push.WapSIPush;
import org.marre.wap.push.WapSLPush;
import org.springframework.stereotype.Service;

import com.siloyou.jmsg.common.message.SmsMoMessage;
import com.siloyou.jmsg.common.message.SmsMtMessage;
import com.siloyou.jmsg.common.message.SmsRtMessage;
import com.siloyou.jmsg.common.message.SmsSrMessage;
import com.siloyou.jmsg.common.util.CommonUtils;
import com.siloyou.jmsg.gateway.api.GateWayMessageAbstract;
import com.zx.sms.codec.cmpp.msg.CmppDeliverRequestMessage;
import com.zx.sms.codec.cmpp.msg.CmppSubmitRequestMessage;
import com.zx.sms.codec.cmpp.msg.CmppSubmitResponseMessage;
import com.zx.sms.common.util.MsgId;

public class UmsGateWayMessage extends GateWayMessageAbstract{
	
	@Override
	protected SmsSrMessage convertSRMessage(Serializable smsSrMessage){
	    SmsSrMessage srMessage = (SmsSrMessage)smsSrMessage;
        if (!StringUtils.equals(srMessage.getResult(), "0"))
        {
            if (!srMessage.getResult().startsWith("F10"))
            {
                srMessage.setResult(String.valueOf("F10110" + srMessage.getResult()));
            }
        }
        return srMessage;
	}

	@Override
	protected SmsRtMessage convertRTMessage(Serializable smsRtMessage) {
	    SmsRtMessage message = (SmsRtMessage)smsRtMessage;
        message.setDestTermID(CommonUtils.resetPhone(message.getDestTermID()));
        return message;
	}

	@Override
	protected SmsMoMessage convertMOMessage(Serializable smsRoMessage) {
	    SmsMoMessage message = (SmsMoMessage)smsRoMessage;
        message.setDestTermID(CommonUtils.resetPhone(message.getDestTermID()));
        return message;
	}
	
	@Override
	public Serializable convertMTMessage(SmsMtMessage message, boolean gatewaySign){
		return null;
	}

}
