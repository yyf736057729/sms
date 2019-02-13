/**
 * zoneland.net Inc.
 * Copyright (c) 2002-2012 All Rights Reserved.
 */
package com.siloyou.jmsg.gateway.smgp;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;

import com.siloyou.jmsg.gateway.api.*;
import com.zx.sms.codec.cmpp.wap.LongMessageFrame;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.rocketmq.client.consumer.DefaultMQPullConsumer;
import com.google.common.collect.Lists;
import com.siloyou.jmsg.common.gateway.SmsGateWay;
import com.siloyou.jmsg.common.message.SmsMtMessage;
import com.siloyou.jmsg.common.util.Encodes;
import com.siloyou.jmsg.common.util.enums.GateEnum;
import com.siloyou.jmsg.common.util.enums.GateStateEnum;
import com.siloyou.jmsg.gateway.Result;
import com.siloyou.jmsg.gateway.smgp.message.SMGPGateWayMessage;
import com.siloyou.jmsg.gateway.smgp.protocol.MoListener;
import com.siloyou.jmsg.gateway.smgp.protocol.smgp.SMGPConnection;
import com.siloyou.jmsg.gateway.smgp.protocol.smgp.SMGPSession;
import com.siloyou.jmsg.gateway.smgp.protocol.smgp.message.SMGPSubmitMessage;
import com.siloyou.jmsg.gateway.smgp.protocol.smgp.util.SequenceGenerator;

public class SMGPGatewayFactory extends BaseGatewayFactory {

    private static final Logger                logger      = LoggerFactory.getLogger(SMGPGatewayFactory.class);

    protected static final Logger              stat        = LoggerFactory.getLogger("STATISTIC");
    private static final Map<String, SMGPSession>   gateway     = new HashMap<String, SMGPSession>();

    private MoListener                         moListener ;
    
    public void initGateway(String appCode) {
        Timer timer = new Timer();
        timer.schedule(new MonitorTask(), 30000, 10000);
        List<JmsgGateWayInfo> gateways = gatewayService.loadValidAll(appCode);
        Iterator<JmsgGateWayInfo> it = gateways.iterator();
        while (it.hasNext()) {
            JmsgGateWayInfo gate = it.next();
            boolean res = false;
            try {
                res = initGate(gate);
                if (res) {
                    gatewayService.updateGatewayStateById(GateStateEnum.ENABLED.getValue(),
                        gate.getId());
                } else {
                    gatewayService.updateGatewayStateById(GateStateEnum.ERROR.getValue(),
                        gate.getId());
                }
            } catch (Exception e) {
                gatewayService.updateGatewayStateById(GateStateEnum.ERROR.getValue(), gate.getId());
                logger.error("初始化网关失败", e);
            }
            if (res) {
                if (logger.isInfoEnabled()) {
                    logger.info("初始化网关成功:[" + gate + "]");
                }
            } else {
                if (logger.isInfoEnabled()) {
                    logger.info("初始化网关失败:[" + gate + "]");
                }
            }
        }
    }

    public void closeAll() {
        if (gateway != null) {
            Iterator<String> it = this.gateway.keySet().iterator();
            while (it.hasNext()) {
					closeGateway(it.next());
            }
            
            gateway.clear();
        }
    }

    private boolean initGate(JmsgGateWayInfo gate) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("host", StringUtils.trimToNull(gate.getHost()));
        params.put("port", getStr(gate.getPort()));
        params.put("read-timeout", getStr(gate.getReadTimeout()));
        params.put("reconnect-interval", getStr(gate.getReconnectInterval()));
        params.put("transaction-timeout", getStr(gate.getTransactionTimeout()));
        params.put("heartbeat-interval", getStr(gate.getHeartbeatInterval()==0?9000:gate.getHeartbeatInterval()));
        params.put("source-addr", StringUtils.trimToEmpty(gate.getSourceAddr()));
        params.put("shared-secret", StringUtils.trimToEmpty(gate.getSharedSecret()));
        params.put("version", StringUtils.trimToEmpty(gate.getVersion()));
        params.put("spNumber", StringUtils.trimToEmpty(gate.getSpNumber()));
        params.put("debug", getBoolean(gate.getDebug()));
        params.put("corpId", StringUtils.trimToEmpty(gate.getCorpId()));
        params.put("msg_Src", StringUtils.trimToEmpty(gate.getCorpId()));
        params.put("gateWayID", StringUtils.trimToEmpty(gate.getId()));
        params.put("gatewaySign", getBoolean(gate.getGatewaySign()));
        params.put("writeLimit", getStr(gate.getWriteLimit()==0?50:gate.getWriteLimit()));
        if(StringUtils.isNotBlank(gate.getExtParam())) {
            String jsonParams = Encodes.unescapeHtml(gate.getExtParam());
	        Map<String,String> extParam = JSON.parseObject(jsonParams, Map.class);
	        params.putAll(extParam);
        }
        
        if (logger.isInfoEnabled()) {
            logger.info("初始化网关:[" + gate + "]\r\n" + "params:" + toString(params));
        }
        boolean res = false;
        if (GateEnum.SMGP3.getValue().equalsIgnoreCase(gate.getType())) {
        	 SMGPConnection conn = new SMGPConnection();
        	 conn.setVersion((byte) 0x30);
             conn.setAutoReconnect(true);
             conn.setClientId(params.get("source-addr"));
             conn.setPassword(params.get("shared-secret"));
             conn.setSendInterval(Integer.parseInt(params.get("writeLimit")));
             conn.setKeepAliveInterval(Integer.parseInt(params.get("heartbeat-interval")) * 1000);
             
             conn.connect(params.get("host"), Integer.parseInt(params.get("port")));
             if(conn.isConnected()){
                 SMGPSession session = (SMGPSession)conn.getSession();
                 session.init(gate.getId(), gate.getGatewaySign()==1?true:false, params);
                 session.setMoListener(moListener);
                 gateway.put(gate.getId(), session);
                 
                 synchronized (this) {
 					SmsGateWay smsGateWay = getSmsGateWay(gate.getId());
 					if(smsGateWay == null) {
 						smsGateWay = new SmsGateWay(gate.getId(), gate.getWriteLimit(), (CMPPGatewayFactory) this);
 					}
 					smsGateWay.startup();
 				}
             }

            res = true;
        } else {
            logger.error("初始化网关失败，未知网关类型:[" + gate + "]");
            gatewayService.updateGatewayStateById(GateStateEnum.ENABLED.getValue(), gate.getId());
            throw new RuntimeException("初始化网关失败，未知网关类型:[" + gate + "]");
        }
        if (res) {
            gatewayService.updateGatewayStateById(GateStateEnum.ENABLED.getValue(), gate.getId());
        }
        return res;
    }

    private String toString(Map<String, String> map) {
        if (map == null) {
            return null;
        }
        Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
        StringBuilder buf = new StringBuilder();
        while (it.hasNext()) {
            Entry<String, String> entry = it.next();
            buf.append(entry.getKey()).append("=").append(entry.getValue()).append(";");
        }
        return buf.toString();

    }

    public boolean closeGateway(String id) {
        boolean res = false;
        
        //关闭队列
    	SmsGateWay smsGateway = getSmsGateWay(id);
    	if( smsGateway != null) smsGateway.shutdown();
        
        SMGPSession gate = gateway.get(id);
        if(gate == null) return true;
        
        try {
			gate.close();
			res = true;
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        if (res) {
            gateway.remove(id);
        }
        return res;
    }

    public boolean closeGatewayTemp(String id) {
        return closeGateway(id);
    }

    public boolean openGateway(String id) {
        Object gate = gateway.get(id);
        if (gate != null) {
            return true;
        }
        JmsgGateWayInfo gateway = gatewayService.findGateway(id);
        if (gateway == null) {
            return false;
        }
        try {
            boolean res = initGate(gateway);
            if (res) {
                gatewayService.updateGatewayStateById(GateStateEnum.ENABLED.getValue(), id);
            } else {
                gatewayService.updateGatewayStateById(GateStateEnum.ERROR.getValue(), id);
            }
            return res;
        } catch (Exception e) {
            gatewayService.updateGatewayStateById(GateStateEnum.ERROR.getValue(), id);
            logger.error("初始化网关失败", e);
        }
        return false;

    }

    public boolean hasGateway(String id) {
        return gateway.get(id) != null;
    }

    public GateEnum getGatewayType(String gatewayId) {
        return null;
    }

    public Object getGateway(String gatewayId) {
        return gateway.get(gatewayId);
    }
    
    private String getStr(Object obj) {
        if (obj == null) {
            return null;
        }
        String str = obj.toString();
        if (StringUtils.isNotBlank(str)) {
            return str;
        }
        return null;
    }

    private String getBoolean(Integer obj) {
        if (obj == null) {
            return "false";
        }
        if (obj == 1) {
            return "true";
        }

        return "false";
    }

    class MonitorTask extends TimerTask {

        @Override
        public void run() {
            try {
                runMonitorTask();
            } catch (Exception e) {

            }
        }
    }

    private void runMonitorTask() {

        Iterator<Map.Entry<String, SMGPSession>> gatewayIt = gateway.entrySet().iterator();
        StringBuilder buff = new StringBuilder();
        buff.append("当前启用的网关:数量:" + gateway.size() + "\r\n");
        while (gatewayIt.hasNext()) {
            Entry<String, SMGPSession> gEntry = gatewayIt.next();
            buff.append("网关ID：" + gEntry.getKey() + ",网关:" + gEntry.getValue() + ";\r\n");
        }
        stat.warn(buff.toString());

    }

    @Override
    public Result sendMsg(SmsMtMessage msg) {
    	try {
    		
    		SMGPSession smgpSession = gateway.get(msg.getGateWayID());
    		 if (smgpSession == null)
	        {
	            logger.error("{}通道, 未启动", msg.getGateWayID());
	            return new Result("F10104", String.format("%s通道, 未启动", msg.getGateWayID()));
	        }
	        else
	        {
	        	List<SMGPSubmitMessage> submitIt = convertMTMessage(msg, smgpSession.isGatewaySign(), smgpSession.getArgs()); 
	        	Iterator<SMGPSubmitMessage> it = submitIt.iterator();
	        	while(it.hasNext()) {
	        		SMGPSubmitMessage submit = it.next();
					smgpSession.send(submit);
					getGateWayMessage().cacheSmsMtMessage(submit.sequenceString(), msg);
	        	}
	        	return new Result("T100", "成功");
	        }
    		
        } catch (Exception e) {
            logger.error("发送消息失败,msg[" + msg + "].", e);
            if (e instanceof InterruptedException) {
                if (!hasGateway(msg.getGateWayID())) {
                    logger.error("InterruptedException restart gateway:" + msg.getGateWayID());
                    boolean res = closeGatewayTemp(msg.getGateWayID());
                    if (res) {
                        openGateway(msg.getGateWayID());
                    }
                } else {
                    logger
                        .error("InterruptedException restart gateway...gateway is started...gatewayid:"
                               + msg.getGateWayID());
                }
            }
            return new Result("F10133", e.getMessage());
        }
    }
    
    public List<SMGPSubmitMessage> convertMTMessage(SmsMtMessage msg, boolean gatewaySign, Map<String,String> args){
    	List<SMGPSubmitMessage> submitList = Lists.newArrayList();
    	List<LongMessageFrame> messageFrames = (List<LongMessageFrame>)gateWayMessage.convertMTMessage(msg, gatewaySign);
    	for(LongMessageFrame frame : messageFrames) {
    		String SPNumber     = msg.getSpNumber();
    		String[] UserNumber = msg.getPhone().split(",");
    		String CorpId       = String.valueOf(args.get("corpId"));
    		String serviceType = getStr(args.get("serviceType"));
    		if( null == serviceType ) {
    			serviceType = "";
    		}
    		
            SMGPSubmitMessage submit = new SMGPSubmitMessage();
            submit.setSrcTermId(SPNumber);
            submit.setDestTermIdArray(UserNumber);
            submit.setTpUdhi((byte) frame.getTpudhi());
            submit.setTpPid((byte) frame.getTppid());
            
            submit.setPkTotal((byte) frame.getPktotal()); 
            submit.setPkNumber((byte) frame.getPknumber());
            
            submit.setBMsgContent(frame.getMsgContentBytes());
            submit.setMsgFmt((byte) frame.getMsgfmt().getValue());
            submit.setNeedReport((byte) 1);
            submit.setServiceId(serviceType);
            submit.setAtTime("");
            submit.setNeedReport((byte) 1);
            submit.setMsgSrc(CorpId);
            submit.setSequenceNumber(SequenceGenerator.nextSequence());
            
    		submitList.add(submit);
    	}
    	return submitList;
    }
    
    public SmsMtMessage getSubmitResult(String gID, String msgid){
    	return null;
    }

    public void setGatewayService(GatewayService gatewayService) {
        this.gatewayService = gatewayService;
    }

    public void setMoListener(MoListener moListener) {
        this.moListener = moListener;
    }

	public SMGPGateWayMessage getGateWayMessage()
    {
        return (SMGPGateWayMessage) this.gateWayMessage;
    }
}
