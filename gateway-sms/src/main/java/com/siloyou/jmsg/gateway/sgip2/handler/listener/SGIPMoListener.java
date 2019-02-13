package com.siloyou.jmsg.gateway.sgip2.handler.listener;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Date;

import org.apache.commons.lang3.time.DateFormatUtils;

import com.sanerzone.smscenter.gateway.sgip.MoListener;
import com.sanerzone.smscenter.gateway.sgip.comm.sgip.message.SGIPDeliverMessage;
import com.sanerzone.smscenter.gateway.sgip.comm.sgip.message.SGIPReportMessage;
import com.sanerzone.smscenter.gateway.sgip.util.Args;
import com.siloyou.jmsg.common.message.SmsMoMessage;
import com.siloyou.jmsg.common.message.SmsMtMessage;
import com.siloyou.jmsg.common.message.SmsRtMessage;
import com.siloyou.jmsg.gateway.api.GateWayMessageAbstract;
import com.siloyou.jmsg.gateway.sgip.handler.SgipMessageFactory;

public class SGIPMoListener implements MoListener{
	
	private GateWayMessageAbstract gateWayMessage;

	public void setGateWayMessage(GateWayMessageAbstract gateWayMessage) {
		this.gateWayMessage = gateWayMessage;
	}

	@Override
	public void onDeliver(SGIPDeliverMessage e, Args args) {
		SmsMoMessage message = new SmsMoMessage();
		message.setDestTermID(e.getUserNumber());
		message.setSrcTermID(e.getSPNumber());
		message.setMsgid(e.getSequenceNumberStr());
		try {
			message.setMsgContent(e.getMessageContent());
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		String gateWayID = String.valueOf(args.get("gateWayID",""));
		gateWayMessage.sendSmsMOMessage(message, gateWayID);
		
	}

	@Override
	public void onReport(SGIPReportMessage e, Args args) {
		SmsRtMessage message = new SmsRtMessage();
		String time = DateFormatUtils.format(new Date(), "yyMMddHHmm");
		message.setDestTermID(e.getUserNumber());
		message.setSrcTermID(args.get("spNumber",""));
		message.setDoneTime(time);
		message.setSubmitTime(time);
		message.setMsgid(e.getSubmitSequenceNumberStr());
		message.setSmscSequence("1");
		Serializable mtMsg = SgipMessageFactory.getStoredMap().remove(e.getSubmitSequenceNumberStr());
		if(mtMsg != null)
			message.setSmsMt((SmsMtMessage)mtMsg);
		switch(e.getErrorCode()) {
			case 0:
				message.setStat("DELIVRD");
				break;
			default:
				message.setStat(String.valueOf(e.getErrorCode()));
				break;
		}
		String gateWayID = String.valueOf(args.get("gateWayID", ""));
		gateWayMessage.sendSmsRTMessage(message, gateWayID);
	}

	@Override
	public void onTerminate(Args args) {
		// TODO Auto-generated method stub
		
	}
	

}
