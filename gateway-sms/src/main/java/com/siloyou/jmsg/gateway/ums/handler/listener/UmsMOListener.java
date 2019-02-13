/**
 * zoneland.net Inc.
 * Copyright (c) 2002-2012 All Rights Reserved.
 */
package com.siloyou.jmsg.gateway.ums.handler.listener;

import java.io.Serializable;
import java.util.Map;

import com.zx.sms.common.util.CachedMillisecondClock;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;

import com.siloyou.jmsg.common.message.SmsMoMessage;
import com.siloyou.jmsg.common.message.SmsMtMessage;
import com.siloyou.jmsg.common.message.SmsRtMessage;
import com.siloyou.jmsg.gateway.api.GateWayMessageAbstract;
import com.siloyou.jmsg.gateway.ums.handler.MessageFactory;

import net.zoneland.gateway.MoListener;
import net.zoneland.gateway.comm.PMessage;
import net.zoneland.gateway.comm.smgp3.message.SMGP3DeliverMessage;

public class UmsMOListener implements MoListener, InitializingBean {

    private static Logger       logger = Logger.getLogger("UMS-GATEWAY-MO");
    
    private GateWayMessageAbstract gateWayMessage;
    
    /**
     * @see net.zoneland.gateway.MoListener#OnTerminate()
     */
    public void OnTerminate() {
    }

    /**
     * @see net.zoneland.gateway.MoListener#onDeliver(net.zoneland.gateway.comm.PMessage,
     *      java.util.Map)
     */
    public void onDeliver(PMessage arg0, Map<String, String> arg1) {
        if (logger.isInfoEnabled()) {
            logger.info("收到上行短信:" + arg0);
        }
        
        String gateWayID = arg1.get("gatewayId");
        if (arg0 instanceof SMGP3DeliverMessage) {
            SMGP3DeliverMessage e = (SMGP3DeliverMessage) arg0;
            if(e.getIsReport() ==1 ) {
            	SmsRtMessage message = new SmsRtMessage();
	    		message.setDestTermID(e.getSrcTermID());
	    		message.setSrcTermID(e.getDestTermID());

	    		// 暂时写死为当前系统时间（qtnag）
				String t = DateFormatUtils.format(CachedMillisecondClock.INS.now(), "yyMMddHHMM");
	    		message.setDoneTime(/*e.getDoneTime()*/t);
	    		message.setSubmitTime(/*e.getSubTime()*/t);
	    		message.setMsgid(e.getReportMsgID());
	    		message.setSmscSequence("1");
	    		Serializable mtMsg = MessageFactory.getStoredMap().remove(e.getReportMsgID());
	    		if(mtMsg != null)
	    			message.setSmsMt((SmsMtMessage)mtMsg);

				// 接收状态设置（qtang）
	    		message.setStat(String.valueOf(e.getSpDealResult()));
	    		gateWayMessage.sendSmsRTMessage(message, gateWayID);
            } else {
            	SmsMoMessage message = new SmsMoMessage();
        		message.setDestTermID(e.getSrcTermID());
        		message.setSrcTermID(e.getDestTermID());
        		message.setMsgid(e.getMsgID());
        		message.setMsgContent(e.getMsgContentStr());
        		gateWayMessage.sendSmsMOMessage(message, gateWayID);
            }
            
        } else {
            logger.error("接收到上行未知类型消息:" + arg0);
        }

    }

    /**
     * @see net.zoneland.gateway.MoListener#onReport(net.zoneland.gateway.comm.PMessage)
     */
    public PMessage onReport(PMessage arg0) {
        return null;
    }

    /**
     * 默认的就db2;
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    public void afterPropertiesSet() throws Exception {
    }

	public void setGateWayMessage(GateWayMessageAbstract gateWayMessage) {
		this.gateWayMessage = gateWayMessage;
	}
	
	
	private String append86(String phone){
		if(StringUtils.startsWith(phone, "86"))
			return phone;
		return String.format("86%s", phone);
	}
	
	public static void main(String[] args) {
		switch(0) {
		case 0:
		case 1:
			System.out.println("DELIVRD");
			break;
		default:
			System.out.println("=======");
			break;
		}
	}
}
