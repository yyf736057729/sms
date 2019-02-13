/**
 * zoneland.net Inc.
 * Copyright (c) 2002-2012 All Rights Reserved.
 */
package com.siloyou.jmsg.gateway.ums;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.alibaba.rocketmq.client.consumer.DefaultMQPullConsumer;
import com.siloyou.jmsg.common.message.SmsMtMessage;
import com.siloyou.jmsg.common.util.enums.GateEnum;
import com.siloyou.jmsg.common.util.enums.GateStateEnum;
import com.siloyou.jmsg.gateway.Result;
import com.siloyou.jmsg.gateway.api.BaseGatewayFactory;
import com.siloyou.jmsg.gateway.api.GateWayMessageAbstract;
import com.siloyou.jmsg.gateway.api.GatewayFactory;
import com.siloyou.jmsg.gateway.api.GatewayService;
import com.siloyou.jmsg.gateway.api.JmsgGateWayInfo;
import com.siloyou.jmsg.gateway.ums.handler.MessageFactory;
import com.siloyou.jmsg.gateway.ums.handler.SendThread;
import com.siloyou.jmsg.gateway.ums.handler.TheadFactory;

import net.zoneland.gateway.MoListener;
import net.zoneland.gateway.comm.cmpp.CMPPSMProxy;
import net.zoneland.gateway.comm.cmpp3.CMPP30SMProxy;
import net.zoneland.gateway.comm.sgip.SGIPConnection;
import net.zoneland.gateway.comm.sgip.SGIPSMProxy;
import net.zoneland.gateway.comm.smgp.SMGPSMProxy;
import net.zoneland.gateway.comm.smgp3.SMGP3SMProxy;
import net.zoneland.gateway.message.factory.SGIPMsgFactory;

public class UmsGatewayFactory extends BaseGatewayFactory {

    private static final Logger                logger      = LoggerFactory.getLogger(UmsGatewayFactory.class);

    protected static final Logger              stat        = LoggerFactory.getLogger("STATISTIC");
    private static final Map<String, Object>   gateway     = new HashMap<String, Object>();

    private static final Map<String, GateEnum> gatewayType = new HashMap<String, GateEnum>();

    private GatewayService                     gatewayService ;
    private MoListener                         moListener ;
    private MessageFactory       	 		   messageFactory;
    private SGIPMsgFactory                     sgipMsgFactory = new SGIPMsgFactory();

    public static final String     PHONE_PREFIX      = "86";
	public static final Long       SLEEP        	 = 150L;
	
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
            Collection<Object> gateway = this.gateway.values();
            Iterator<Object> it = gateway.iterator();
            while (it.hasNext()) {
                Object gate = it.next();
                if (gate instanceof CMPPSMProxy) {
                    CMPPSMProxy proxy = (CMPPSMProxy) gate;
                    proxy.close();
                } else if (gate instanceof CMPP30SMProxy) {
                    CMPP30SMProxy proxy = (CMPP30SMProxy) gate;
                    proxy.close();
                } else if (gate instanceof SGIPSMProxy) {
                    SGIPSMProxy proxy = (SGIPSMProxy) gate;
                    proxy.close();
                    proxy.stopService();
                } else if (gate instanceof SMGPSMProxy) {
                    SMGPSMProxy proxy = (SMGPSMProxy) gate;
                    proxy.close();
                } else if (gate instanceof SMGP3SMProxy) {
                    SMGP3SMProxy proxy = (SMGP3SMProxy) gate;
                    proxy.close();
                }
            }
        }
    }

    private boolean initGate(JmsgGateWayInfo gate) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("host", StringUtils.trimAllWhitespace(gate.getHost()));
        params.put("port", getStr(gate.getPort()));
        params.put("local-host", getStr(gate.getLocalHost()));
        params.put("local-port", getStr(gate.getLocalPort()));
        params.put("read-timeout", getStr(gate.getReadTimeout()));
        params.put("reconnect-interval", getStr(gate.getReconnectInterval()));
        params.put("transaction-timeout", getStr(gate.getTransactionTimeout()));
        params.put("heartbeat-interval", getStr(gate.getHeartbeatInterval()));
        params.put("heartbeat-noresponseout", getStr(gate.getHeartbeatNoresponseout()));
        params.put("source-addr", StringUtils.trimAllWhitespace(gate.getSourceAddr()));
        params.put("shared-secret", StringUtils.trimAllWhitespace(gate.getSharedSecret()));
        params.put("version", StringUtils.trimAllWhitespace(gate.getVersion()));
        params.put("spNumber", StringUtils.trimAllWhitespace(gate.getSpNumber()));
        params.put("debug", getBoolean(gate.getDebug()));
        params.put("corpId", StringUtils.trimAllWhitespace(gate.getCorpId()));
        params.put("msg_Src", StringUtils.trimAllWhitespace(gate.getCorpId()));
        if (logger.isInfoEnabled()) {
            logger.info("初始化网关:[" + gate + "]\r\n" + "params:" + toString(params));
        }
        boolean res = false;
        if (GateEnum.CMPP.getValue().equalsIgnoreCase(gate.getType())) {
            CMPPSMProxy proxy = new CMPPSMProxy(gate.getId(), params);
            proxy.setMoListener(moListener);
            gateway.put(gate.getId(), proxy);
            gatewayType.put(gate.getId(), GateEnum.CMPP);
            res = true;
        } else if (GateEnum.CMPP30.getValue().equalsIgnoreCase(gate.getType())) {
            CMPP30SMProxy proxy = new CMPP30SMProxy(gate.getId(), params);
            proxy.setMoListener(moListener);
            gateway.put(gate.getId(), proxy);
            gatewayType.put(gate.getId(), GateEnum.CMPP30);
            res = true;
        } else if (GateEnum.SGIP.getValue().equalsIgnoreCase(gate.getType())) {
            params.remove("local-host");
            params.remove("local-port");

            params.put("serviceType", sgipMsgFactory.getServiceType());
            params.put("feeType", getStr(sgipMsgFactory.getFeeType()));
            params.put("feeValue", sgipMsgFactory.getFeeValue());
            params.put("givenValue", sgipMsgFactory.getGivenValue());
            params.put("morelatetoMTFlag", getStr(sgipMsgFactory.getMorelatetoMTFlag()));
            SGIPSMProxy proxy = new SGIPSMProxy(gate.getId(), params);
            boolean result = proxy.connect(params.get("source-addr"), params.get("shared-secret"));
            if (result) {
                proxy.startService(getStr(gate.getLocalHost()),
                    Integer.parseInt(getStr(gate.getLocalPort())));
                proxy.setMoListener(moListener);
                gateway.put(gate.getId(), proxy);
                gatewayType.put(gate.getId(), GateEnum.SGIP);
                res = true;
            } else {
                proxy.close();
            }

        } else if (GateEnum.SMGP.getValue().equalsIgnoreCase(gate.getType())) {
            SMGPSMProxy proxy = new SMGPSMProxy(gate.getId(), params);
            proxy.setMoListener(moListener);
            gateway.put(gate.getId(), proxy);
            gatewayType.put(gate.getId(), GateEnum.SMGP);
            res = true;
        } else if (GateEnum.SMGP3.getValue().equalsIgnoreCase(gate.getType())) {
            SMGP3SMProxy proxy = new SMGP3SMProxy(gate.getId(), params);
            proxy.setMoListener(moListener);
            gateway.put(gate.getId(), proxy);
            gatewayType.put(gate.getId(), GateEnum.SMGP3);
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
        GateEnum type = gatewayType.get(id);
        boolean res = false;
        Object gate = gateway.get(id);
        if (type == null || gate == null) {
            return res;
        }
        if (GateEnum.CMPP == type) {
            CMPPSMProxy proxy = (CMPPSMProxy) gate;
            logger.warn("关闭网关,:[" + proxy + "]");
            proxy.close();
            logger.warn("关闭网关成功,:[" + proxy + "]");
            res = true;
        } else if (GateEnum.CMPP30 == type) {
            CMPP30SMProxy proxy = (CMPP30SMProxy) gate;
            logger.warn("关闭网关,:[" + proxy + "]");
            proxy.close();
            logger.warn("关闭网关成功,:[" + proxy + "]");
            res = true;
        } else if (GateEnum.SGIP == type) {
            SGIPSMProxy proxy = (SGIPSMProxy) gate;
            logger.warn("关闭网关,:[" + proxy + "]");
            try {
                proxy.close();
            } catch (Exception e) {

            }
            proxy.stopService();
            logger.warn("关闭网关成功,:[" + proxy + "]");
            res = true;
        } else if (GateEnum.SMGP == type) {
            SMGPSMProxy proxy = (SMGPSMProxy) gate;
            logger.warn("关闭网关,:[" + proxy + "]");
            proxy.close();
            logger.warn("关闭网关成功,:[" + proxy + "]");
            res = true;
        } else if (GateEnum.SMGP3 == type) {
            SMGP3SMProxy proxy = (SMGP3SMProxy) gate;
            logger.warn("关闭网关,:[" + proxy + "]");
            proxy.close();
            logger.warn("关闭网关成功,:[" + proxy + "]");
            res = true;
        } else {
            logger.error("关闭网关失败，未知网关类型:[" + gate + "]");
        }
        if (res) {
            gatewayType.remove(id);
            gateway.remove(id);
        }
        return res;
    }

    public boolean closeGatewayTemp(String id) {
        GateEnum type = gatewayType.get(id);
        boolean res = false;
        Object gate = gateway.get(id);
        if (type == null || gate == null) {
            return res;
        }
        if (GateEnum.CMPP == type) {
            CMPPSMProxy proxy = (CMPPSMProxy) gate;
            logger.warn("关闭网关,:[" + proxy + "]");
            proxy.close();
            logger.warn("关闭网关成功,:[" + proxy + "]");
            res = true;
        } else if (GateEnum.CMPP30 == type) {
            CMPP30SMProxy proxy = (CMPP30SMProxy) gate;
            logger.warn("关闭网关,:[" + proxy + "]");
            proxy.close();
            logger.warn("关闭网关成功,:[" + proxy + "]");
            res = true;
        } else if (GateEnum.SGIP == type) {
            SGIPSMProxy proxy = (SGIPSMProxy) gate;
            logger.warn("关闭网关,:[" + proxy + "]");
            proxy.close();
            proxy.stopService();
            logger.warn("关闭网关成功,:[" + proxy + "]");
            res = true;
        } else if (GateEnum.SMGP == type) {
            SMGPSMProxy proxy = (SMGPSMProxy) gate;
            logger.warn("关闭网关,:[" + proxy + "]");
            proxy.close();
            logger.warn("关闭网关成功,:[" + proxy + "]");
            res = true;
        } else if (GateEnum.SMGP3 == type) {
            SMGP3SMProxy proxy = (SMGP3SMProxy) gate;
            logger.warn("关闭网关,:[" + proxy + "]");
            proxy.close();
            logger.warn("关闭网关成功,:[" + proxy + "]");
            res = true;
        } else {
            logger.error("关闭网关失败，未知网关类型:[" + gate + "]");
        }
        return res;
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
        return gatewayType.get(gatewayId);
    }

    public Object getGateway(String gatewayId) {
        return gateway.get(gatewayId);
    }

    private String getStr(Object obj) {
        if (obj == null) {
            return null;
        }
        String str = obj.toString();
        if (StringUtils.hasText(str)) {
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

        Iterator<Map.Entry<String, GateEnum>> tt = gatewayType.entrySet().iterator();
        while (tt.hasNext()) {
            Entry<String, GateEnum> gEntry = tt.next();
            GateEnum type = gEntry.getValue();
            if (GateEnum.SGIP == type) {
                String key = gEntry.getKey();
                SGIPSMProxy proxy = (SGIPSMProxy) gateway.get(key);
                statSGIP(proxy);
                if (proxy == null || proxy.isClosed()) {
                    if (proxy != null) {
                        try {
                            proxy.close();
                        } catch (Exception e) {

                        }
                        try {
                            if (proxy.isClosed()) {
                                proxy.stopService();
                            }
                        } catch (Exception e) {

                        }
                    }
                    stat.warn("网关已经关闭,:[" + proxy + "]");
                    try {
                        Thread.sleep(7 * 1000);
                    } catch (InterruptedException e) {
                        logger.error("", e);
                    }
                    restart(key);
                }
            } else if (GateEnum.SMGP == type) {
                String key = gEntry.getKey();
                SMGPSMProxy proxy = (SMGPSMProxy) gateway.get(key);
                if (proxy == null || proxy.isClosed()) {
                    if (proxy != null) {
                        proxy.close();
                    }
                    stat.warn("网关已经关闭,:[" + proxy + "]");
                    try {
                        Thread.sleep(7 * 1000);
                    } catch (InterruptedException e) {
                        logger.error("", e);
                    }
                    restart(key);
                }
            } else if (GateEnum.SMGP3 == type) {
                String key = gEntry.getKey();
                SMGP3SMProxy proxy = (SMGP3SMProxy) gateway.get(key);
                if (proxy == null || proxy.isClosed()) {
                    if (proxy != null) {
                        proxy.close();
                    }
                    stat.warn("网关已经关闭,:[" + proxy + "]");
                    try {
                        Thread.sleep(7 * 1000);
                    } catch (InterruptedException e) {
                        logger.error("", e);
                    }
                    restart(key);
                }
            } else {
                logger.error("未知网关类型:[" + type + "]");
            }

        }
        Iterator<Map.Entry<String, GateEnum>> typeIt = gatewayType.entrySet().iterator();
        StringBuilder buff = new StringBuilder();
        buff.append("当前网关类型:数量:" + gatewayType.size() + "\r\n");
        while (typeIt.hasNext()) {
            Entry<String, GateEnum> typeEntry = typeIt.next();
            buff.append("网关ID：" + typeEntry.getKey() + ",网关类型:" + typeEntry.getValue() + ";\r\n");
        }

        Iterator<Map.Entry<String, Object>> gatewayIt = gateway.entrySet().iterator();

        buff.append("当前启用的网关:数量:" + gateway.size() + "\r\n");
        while (gatewayIt.hasNext()) {
            Entry<String, Object> gEntry = gatewayIt.next();
            buff.append("网关ID：" + gEntry.getKey() + ",网关:" + gEntry.getValue() + ";\r\n");
        }
        stat.warn(buff.toString());

    }

    private void statSGIP(SGIPSMProxy proxy) {
        if (proxy == null) {
            return;
        }
        HashMap<String, SGIPConnection> secConn = proxy.getSerconns();
        StringBuilder buf = new StringBuilder();
        buf.append("sgip service:\n");
        if (secConn != null) {
            Iterator<SGIPConnection> cols = secConn.values().iterator();
            while (cols.hasNext()) {
                buf.append(cols.next()).append("\n");
            }
        }
        stat.warn(buf.toString());
    }

    private void restart(String key) {
        JmsgGateWayInfo gate = gatewayService.findGateway(key);
        logger.warn("重新初始化网关,:[" + gate + "]");
        //gatewayType.remove(key);
        //gateway.remove(key);
        try {
            initGate(gate);
        } catch (Exception e) {
            gatewayService.updateGatewayStateById(GateStateEnum.ERROR.getValue(), gate.getId());
        }

    }
    
    private String append86(String phone) {
        if (org.apache.commons.lang3.StringUtils.isBlank(phone)) {
            return null;
        }
        return PHONE_PREFIX + phone;
    }
    
    @Override
    public Result sendMsg(SmsMtMessage msg) {
    	try {
//    		msg.setPhone(append86(msg.getPhone()));
            GateEnum gateEnum = getGatewayType(msg.getGateWayID());
            if (GateEnum.SGIP == gateEnum || GateEnum.SMGP == gateEnum || GateEnum.SMGP3 == gateEnum) {
                SendThread send = TheadFactory.newSendThread(messageFactory, msg, gateEnum);
                if (send == null) {
                    logger.warn(gateEnum.getDescription() + "线程池已满," + msg);
                    return new Result("F10131", gateEnum.getDescription() + "线程池已满");
                }
                try {
                    Boolean res = send.call();
                    if (res == null || !res) {
                        return new Result("F10132", "网关发送消息失败");
                    } else {
                        return new Result("T100", "成功");
                    }
                } finally {
                    TheadFactory.releaseThread(gateEnum);
                }
            } else {
                SendThread send = new SendThread(messageFactory, msg);
                Boolean res = send.call();
                if (res == null || !res) {
                    return new Result("F10132", "网关发送消息失败");
                } else {
                    return new Result("T100", "成功");
                }
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
    
    public SmsMtMessage getSubmitResult(String gID, String msgid){
    	return null;
    }

    public void setGatewayService(GatewayService gatewayService) {
        this.gatewayService = gatewayService;
    }

    public void setMoListener(MoListener moListener) {
        this.moListener = moListener;
    }

    public void setSgipMsgFactory(SGIPMsgFactory sgipMsgFactory) {
        this.sgipMsgFactory = sgipMsgFactory;
    }

	public void setMessageFactory(MessageFactory messageFactory) {
		this.messageFactory = messageFactory;
	}
	
	public GateWayMessageAbstract getGateWayMessage()
    {
        return null;
    }

}
