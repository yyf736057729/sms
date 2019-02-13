package com.siloyou.jmsg.gateway.smgp.handler.listener;

import java.io.Serializable;
import java.util.Map;

import org.apache.log4j.Logger;

import com.siloyou.jmsg.common.message.SmsMoMessage;
import com.siloyou.jmsg.common.message.SmsMtMessage;
import com.siloyou.jmsg.common.message.SmsRtMessage;
import com.siloyou.jmsg.common.message.SmsSrMessage;
import com.siloyou.jmsg.gateway.smgp.message.SMGPGateWayMessage;
import com.siloyou.jmsg.gateway.smgp.protocol.MoListener;
import com.siloyou.jmsg.gateway.smgp.protocol.smgp.message.SMGPDeliverMessage;
import com.siloyou.jmsg.gateway.smgp.protocol.smgp.message.SMGPReportData;
import com.siloyou.jmsg.gateway.smgp.protocol.smgp.message.SMGPSubmitRespMessage;

public class SMGP3MoListener implements MoListener {
	
	private SMGPGateWayMessage gateWayMessage;
	
	private final static Logger logger = Logger.getLogger(SMGP3MoListener.class);

	public void onDeliver(SMGPDeliverMessage deliver, Map<String,String> args) {
		logger.warn("receive mo message:{}" + deliver);
		
		 String gateWayID = args.get("gateWayID");
        if(deliver.getIsReport() ==1 ) {
        	SMGPReportData report = deliver.getReport();
        	SmsRtMessage message = new SmsRtMessage();
    		message.setDestTermID(deliver.getSrcTermId());
    		message.setSrcTermID(deliver.getDestTermId());
    		message.setDoneTime(report.getDoneTime());
    		message.setSubmitTime(report.getSubTime());
    		message.setMsgid(report.msgIdString());
    		message.setSmscSequence("1");
    		Serializable mtMsg = gateWayMessage.getSmsMtMessageByMsgid(report.msgIdString());
    		if(mtMsg != null)
    			message.setSmsMt((SmsMtMessage)mtMsg);
    		message.setStat(String.valueOf(report.getStat()));
    		gateWayMessage.sendSmsRTMessage(message, gateWayID);
        } else {
        	SmsMoMessage message = new SmsMoMessage();
    		message.setDestTermID(deliver.getSrcTermId());
    		message.setSrcTermID(deliver.getDestTermId());
    		message.setMsgid(deliver.msgIdString());
    		message.setMsgContent(deliver.getMsgContent());
    		gateWayMessage.sendSmsMOMessage(message, gateWayID);
        }
		
	}
	
	public void onSubmitResp(SMGPSubmitRespMessage submitResp, Map<String,String> args){
		logger.warn("收到Submit响应" + submitResp);
		String gateWayID = args.get("gateWayID");
		SmsMtMessage message = (SmsMtMessage) gateWayMessage.setSmsMtMessage(submitResp.sequenceString(), submitResp.msgIdString());
		SmsSrMessage smsSrMessage = new SmsSrMessage(submitResp.msgIdString(), String.valueOf(submitResp.getStatus()), message);
		gateWayMessage.sendSmsSRMessage(smsSrMessage, gateWayID);
	}

	public void onTerminate(Map<String,String> args) {
		logger.warn("SMGP3主动断开哦" );
	}

	public void setGateWayMessage(SMGPGateWayMessage gateWayMessage) {
		this.gateWayMessage = gateWayMessage;
	}
	

	
}
