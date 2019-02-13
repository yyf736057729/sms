package com.siloyou.jmsg.gateway.ums.handler;
import java.io.IOException;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.siloyou.jmsg.common.message.SmsMtMessage;
import com.siloyou.jmsg.common.message.SmsSrMessage;
import com.siloyou.jmsg.common.storedMap.BDBStoredMapFactoryImpl;
import com.siloyou.jmsg.common.util.enums.GateEnum;
import com.siloyou.jmsg.gateway.api.GateWayMessageAbstract;
import com.siloyou.jmsg.gateway.api.GatewayFactory;

import net.zoneland.gateway.comm.sgip.SGIPSMProxy;
import net.zoneland.gateway.comm.sgip.message.SGIPSubmitMessage;
import net.zoneland.gateway.comm.sgip.message.SGIPSubmitRepMessage;
import net.zoneland.gateway.comm.smgp.SMGPSMProxy;
import net.zoneland.gateway.comm.smgp.message.SMGPSubmitMessage;
import net.zoneland.gateway.comm.smgp.message.SMGPSubmitRespMessage;
import net.zoneland.gateway.comm.smgp3.SMGP3SMProxy;
import net.zoneland.gateway.comm.smgp3.message.SMGP3SubmitMessage;
import net.zoneland.gateway.comm.smgp3.message.SMGP3SubmitRespMessage;
import net.zoneland.gateway.message.factory.CMPP30MsgFactory;
import net.zoneland.gateway.message.factory.CMPPMsgFactory;
import net.zoneland.gateway.message.factory.SGIPMsgFactory;
import net.zoneland.gateway.message.factory.SMGP3MsgFactory;
import net.zoneland.gateway.message.factory.SMGPMsgFactory;


public class MessageFactory {

	private static final Logger                logger      = LoggerFactory.getLogger(MessageFactory.class);

    private CMPPMsgFactory      cmppMsgFactory;
    private CMPP30MsgFactory    cmpp30MsgFactory;
    private SGIPMsgFactory      sgipMsgFactory;
    private SMGPMsgFactory      smgpMsgFactory;
    private SMGP3MsgFactory     smgp3MsgFactory;
    private GatewayFactory   	gatewayFactory;
    private GateWayMessageAbstract gateWayMessage;

    private int                 fmt_cmpp   = 8;
    private int                 fmt_cmpp30 = 8;
    private int                 fmt_sgip   = 8;
    private int                 fmt_smgp   = 8;
    private int                 fmt_smgp3  = 8;
    
    private static Map<String, Serializable> storedMap = getStoredMap();
    
    public static Map<String, Serializable> getStoredMap(){
    	if(storedMap == null) {
    		storedMap = BDBStoredMapFactoryImpl.INS.buildMap("ums", "message");
    	}
    	return storedMap;
    }
    
    public boolean sendMsg(SmsMtMessage message) throws IOException {
    	
        if (message == null) {
            return false;
        }
        if (logger.isInfoEnabled()) {
            logger.info("发送消息:" + message);
        }
        GateEnum type = gatewayFactory.getGatewayType(message.getGateWayID());
        Object gate = gatewayFactory.getGateway(message.getGateWayID());
        if (GateEnum.SGIP == type) {
            int tempType = fmt_sgip;
//            int orgType = getInt(message.getContentType());
//            if (orgType == 4 || orgType == 21) {
//                tempType = orgType;
//            }

            SGIPSMProxy proxy = (SGIPSMProxy) gate;
            List<SGIPSubmitMessage> list = sgipMsgFactory.buildSGIPSubmitMessage(
            		message.getSpNumber(), message.getPhone().split(","), message.getMsgContent(), tempType, 0,
                null, 3, String.valueOf(proxy.getCorpId()));

            Iterator<SGIPSubmitMessage> it = list.iterator();
            int result = -1;
            while (it.hasNext()) {
                SGIPSubmitMessage m = it.next();
                if (!proxy.isClosed()) {
                    if (logger.isInfoEnabled()) {
                        logger.info("发送消息:" + m);
                    }
                    SGIPSubmitRepMessage res = (SGIPSubmitRepMessage) proxy.send(m);
                    if (logger.isInfoEnabled()) {
                        logger.info("返回消息:" + m + ",返回：" + res);
                    }
                    if (res == null) {
                        return true;
                    }
                    result = res.getResult();
                    if (result != 0) {
                        break;
                    }

                    // Submit sequence ID
                    getStoredMap().put(String.valueOf(res.getSequenceId()), message);
                } else {
                    try {
                        Thread.sleep(3000L);
                    } catch (InterruptedException e) {
                        logger.error("联通网关断开睡眠", e);
                    }
                }
            }
            return result == 0;
        } else if (GateEnum.SMGP == type) {
            //由于电信发送速度太快会造成电信运营商返回系统忙的错误，在此增加睡眠降低发送速度
            //            try {
            //                Thread.sleep(180L);
            //            } catch (InterruptedException e) {
            //                logger.error("", e);
            //            }
            int tempType = fmt_smgp;
//            int orgType = getInt(message.getContentType());
//            if (orgType == 4 || orgType == 21) {
//                //电信不支持21编码
//                tempType = 4;
//            }
            List<SMGPSubmitMessage> list = smgpMsgFactory.buildSMGPSubmitMessage(
            		message.getSpNumber(), message.getPhone().split(","), message.getMsgContent(), tempType,
                null, null, 3, 1);
            SMGPSMProxy proxy = (SMGPSMProxy) gate;
            Iterator<SMGPSubmitMessage> it = list.iterator();
            int result = -1;
            while (it.hasNext()) {
                SMGPSubmitMessage m = it.next();
                if (!proxy.isClosed()) {
                    SMGPSubmitRespMessage res = (SMGPSubmitRespMessage) proxy.send(m);
                    if (logger.isInfoEnabled()) {
                        logger.info("发送消息1:" + m + ",返回：" + res);
                    }
                    if (res == null) {
                        return false;
                    }
                    result = res.getStatus();
                    if (result != 0) {
                        break;
                    }
                    getStoredMap().put(res.getMsgIdStr().toLowerCase(), message);
                }
            }
            return result == 0;
        } else if (GateEnum.SMGP3 == type) {
            //由于电信发送速度太快会造成电信运营商返回系统忙的错误，在此增加睡眠降低发送速度
            //            try {
            //                Thread.sleep(180L);
            //            } catch (InterruptedException e) {
            //                logger.error("", e);
            //            }
            int tempType = fmt_smgp3;
//            int orgType = getInt(message.getContentType());
//            if (orgType == 4 || orgType == 21) {
//                //电信不支持21编码
//                tempType = 4;
//            }
            List<SMGP3SubmitMessage> list = smgp3MsgFactory.buildSMGP3SubmitMessage(
            		message.getSpNumber(), message.getPhone().split(","), message.getMsgContent(), tempType,
                null, null, 3, 1);
            SMGP3SMProxy proxy = (SMGP3SMProxy) gate;
            Iterator<SMGP3SubmitMessage> it = list.iterator();
            int result = -1;
            while (it.hasNext()) {
                SMGP3SubmitMessage m = it.next();
                if (!proxy.isClosed()) {
                    if (logger.isInfoEnabled()) {
                        logger.info("发送消息2:" + m);
                    }
                    SMGP3SubmitRespMessage res = (SMGP3SubmitRespMessage) proxy.send(m);
                    if (logger.isInfoEnabled()) {
                        logger.info("返回消息:" + m + ",返回：" + res);
                    }
                    if (res == null) {
                        return false;
                    }
                    result = res.getStatus();
                    
                    SmsSrMessage smsSrMessage = new SmsSrMessage(res.getMsgIdStr(), String.valueOf(result), message);
                    gateWayMessage.sendSmsSRMessage(smsSrMessage, proxy.getId());
                    
                    if (result != 0) {
                        break;
                    }
                    getStoredMap().put(res.getMsgIdStr(), message);
                } else {
                    try {
                        Thread.sleep(3000L);
                    } catch (InterruptedException e) {
                        logger.error("电信网关断开睡眠", e);
                    }
                }
            }
            return result == 0;
        } else {
            logger.error("未知网关类型。");
        }
        return false;

    }

    private int getInt(Integer val) {
        if (val == null) {
            return 0;
        }
        return val.intValue();
    }

    public void setFmt_cmpp(int fmt_cmpp) {
        this.fmt_cmpp = fmt_cmpp;
    }

    public void setFmt_cmpp30(int fmt_cmpp30) {
        this.fmt_cmpp30 = fmt_cmpp30;
    }

    public void setFmt_sgip(int fmt_sgip) {
        this.fmt_sgip = fmt_sgip;
    }

    public void setFmt_smgp(int fmt_smgp) {
        this.fmt_smgp = fmt_smgp;
    }

    public void setFmt_smgp3(int fmt_smgp3) {
        this.fmt_smgp3 = fmt_smgp3;
    }

	public void setCmppMsgFactory(CMPPMsgFactory cmppMsgFactory) {
		this.cmppMsgFactory = cmppMsgFactory;
	}

	public void setCmpp30MsgFactory(CMPP30MsgFactory cmpp30MsgFactory) {
		this.cmpp30MsgFactory = cmpp30MsgFactory;
	}

	public void setSgipMsgFactory(SGIPMsgFactory sgipMsgFactory) {
		this.sgipMsgFactory = sgipMsgFactory;
	}

	public void setSmgpMsgFactory(SMGPMsgFactory smgpMsgFactory) {
		this.smgpMsgFactory = smgpMsgFactory;
	}

	public void setSmgp3MsgFactory(SMGP3MsgFactory smgp3MsgFactory) {
		this.smgp3MsgFactory = smgp3MsgFactory;
	}

	public void setGatewayFactory(GatewayFactory gatewayFactory) {
		this.gatewayFactory = gatewayFactory;
	}

    public void setGateWayMessage(GateWayMessageAbstract gateWayMessage)
    {
        this.gateWayMessage = gateWayMessage;
    }
    
    

}
