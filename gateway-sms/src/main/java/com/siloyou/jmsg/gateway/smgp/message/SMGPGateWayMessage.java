package com.siloyou.jmsg.gateway.smgp.message;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.zx.sms.codec.cmpp.wap.LongMessageFrame;
import org.apache.commons.lang3.StringUtils;
import org.marre.sms.SmsException;
import com.siloyou.jmsg.common.message.SmsMoMessage;
import com.siloyou.jmsg.common.message.SmsMtMessage;
import com.siloyou.jmsg.common.message.SmsRtMessage;
import com.siloyou.jmsg.common.message.SmsSrMessage;
import com.siloyou.jmsg.common.storedMap.BDBStoredMapFactoryImpl;
import com.siloyou.jmsg.common.util.CommonUtils;
import com.siloyou.jmsg.gateway.api.GateWayMessageAbstract;
import com.zx.sms.codec.cmpp.wap.LongMessageFrameHolder;
import org.marre.sms.SmsTextMessage;

public class SMGPGateWayMessage extends GateWayMessageAbstract {

	private static Map<String, Serializable> storedMT = BDBStoredMapFactoryImpl.INS.buildMap("smgp3", "SMSMT");
	private static Map<String, Serializable> storedSR = BDBStoredMapFactoryImpl.INS.buildMap("smgp3", "SMSSR");

	@Override
	protected SmsSrMessage convertSRMessage(Serializable smsSrMessage) {
		SmsSrMessage srMessage = (SmsSrMessage) smsSrMessage;
		if (!StringUtils.equals(srMessage.getResult(), "0")) {
			srMessage.setResult(String.valueOf("F10110" + srMessage.getResult()));
		}
		return srMessage;
	}

	@Override
	protected SmsRtMessage convertRTMessage(Serializable smsRtMessage) {
		SmsRtMessage message = (SmsRtMessage) smsRtMessage;
		message.setDestTermID(CommonUtils.resetPhone(message.getDestTermID()));
		return message;
	}

	@Override
	protected SmsMoMessage convertMOMessage(Serializable smsRoMessage) {
		SmsMoMessage message = (SmsMoMessage) smsRoMessage;
		message.setDestTermID(CommonUtils.resetPhone(message.getDestTermID()));
		return message;
	}

	@Override
	public Serializable convertMTMessage(SmsMtMessage message, boolean gatewaySign) {
		SmsTextMessage smsMsg = new SmsTextMessage(message.getMsgContent());
		try {
			List<LongMessageFrame> frameList = LongMessageFrameHolder.INS.splitmsgcontent(smsMsg);
			return (Serializable) frameList;
		} catch (SmsException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void cacheSmsMtMessage(String seqid, SmsMtMessage smsMtMessage) {
		storedMT.put(seqid, smsMtMessage);
	}


	public static SmsMtMessage setSmsMtMessage(String seqid, String msgid) {
		SmsMtMessage smsMtMessage = (SmsMtMessage) storedMT.remove(seqid);
		storedSR.put(msgid, smsMtMessage);
		return smsMtMessage;
	}
	
	public static SmsMtMessage getSmsMtMessageByMsgid(String msgid) {
		return (SmsMtMessage) storedSR.remove(msgid);
	}

}
