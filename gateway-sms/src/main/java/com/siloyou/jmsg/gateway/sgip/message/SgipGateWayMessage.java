package com.siloyou.jmsg.gateway.sgip.message;

import java.io.Serializable;
import java.util.List;

import com.zx.sms.codec.cmpp.wap.LongMessageFrame;
import org.apache.commons.lang3.StringUtils;
import org.marre.sms.SmsException;
import org.marre.sms.SmsMessage;
import org.marre.sms.SmsTextMessage;
import org.marre.wap.push.SmsMmsNotificationMessage;

import com.siloyou.jmsg.common.message.SmsMoMessage;
import com.siloyou.jmsg.common.message.SmsMtMessage;
import com.siloyou.jmsg.common.message.SmsRtMessage;
import com.siloyou.jmsg.common.message.SmsSrMessage;
import com.siloyou.jmsg.common.util.CommonUtils;
import com.siloyou.jmsg.gateway.api.GateWayMessageAbstract;
import com.zx.sms.codec.cmpp.wap.LongMessageFrameHolder;

public class SgipGateWayMessage extends GateWayMessageAbstract{
	
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
	    
	    SmsMessage smsMsg = null;
	    if(StringUtils.equals(message.getSmsType(), "mms")) {
	        String mmsUrl = message.getWapUrl();
            if( "10000".compareTo(message.getId()) < 0 ) {
                mmsUrl = String.format("%s_%X", mmsUrl, Integer.parseInt(message.getId()));
            } 
	        smsMsg = new SmsMmsNotificationMessage(mmsUrl, message.getContentSize());
	        ((SmsMmsNotificationMessage) smsMsg).setFrom("iMessage");
	    } else {
	        smsMsg = new SmsTextMessage(message.getMsgContent());
	    }
		
		try {
			List<LongMessageFrame> frameList = LongMessageFrameHolder.INS.splitmsgcontent(smsMsg);
			return (Serializable) frameList;
		} catch (SmsException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
