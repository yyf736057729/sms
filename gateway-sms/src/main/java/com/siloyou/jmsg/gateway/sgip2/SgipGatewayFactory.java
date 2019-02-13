package com.siloyou.jmsg.gateway.sgip2;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;

import com.blueline.net.sms.common.MsgId;
import com.siloyou.jmsg.gateway.api.CMPPGatewayFactory;
import com.siloyou.jmsg.gateway.api.GatewayFactory;
import com.zx.sms.codec.cmpp.wap.LongMessageFrame;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sanerzone.smscenter.gateway.sgip.MoListener;
import com.sanerzone.smscenter.gateway.sgip.SGIPSMProxy;
import com.sanerzone.smscenter.gateway.sgip.comm.sgip.message.SGIPSubmitMessage;
import com.sanerzone.smscenter.gateway.sgip.comm.sgip.message.SGIPSubmitRepMessage;
import com.sanerzone.smscenter.gateway.sgip.util.Args;
import com.siloyou.jmsg.common.gateway.SmsGateWay;
import com.siloyou.jmsg.common.message.SmsMtMessage;
import com.siloyou.jmsg.common.message.SmsSrMessage;
import com.siloyou.jmsg.common.util.Encodes;
import com.siloyou.jmsg.common.util.enums.GateEnum;
import com.siloyou.jmsg.common.util.enums.GateStateEnum;
import com.siloyou.jmsg.gateway.Result;
import com.siloyou.jmsg.gateway.api.BaseGatewayFactory;
import com.siloyou.jmsg.gateway.api.JmsgGateWayInfo;

public class SgipGatewayFactory extends BaseGatewayFactory 
{
    private static final Logger logger = LoggerFactory.getLogger(SgipGatewayFactory.class);
    protected static final Logger              stat        = LoggerFactory.getLogger("STATISTIC");
    private MoListener moListener;
    private Map<String, Object> GATEWAY_MAP = Maps.newConcurrentMap();
    
    @Override
    public void initGateway(String appCode)
    {
        Timer timer = new Timer();
        timer.schedule(new MonitorTask(), 30000, 10000);
        List<JmsgGateWayInfo> gateways = gatewayService.loadValidAll(appCode);
        Iterator<JmsgGateWayInfo> it = gateways.iterator();
        while (it.hasNext())
        {
            JmsgGateWayInfo gate = it.next();
            boolean res = false;
            try
            {
                res = initGate(gate);
                if (res)
                {
                    gatewayService.updateGatewayStateById(GateStateEnum.ENABLED.getValue(), gate.getId());
                }
                else
                {
                    gatewayService.updateGatewayStateById(GateStateEnum.ERROR.getValue(), gate.getId());
                }
            }
            catch (Exception e)
            {
                gatewayService.updateGatewayStateById(GateStateEnum.ERROR.getValue(), gate.getId());
                logger.error("初始化网关失败", e);
            }
            if (res)
            {
                if (logger.isInfoEnabled())
                {
                    logger.info("初始化网关成功:[" + gate + "]");
                }
            }
            else
            {
                if (logger.isInfoEnabled())
                {
                    logger.info("初始化网关失败:[" + gate + "]");
                }
            }
        }
    }
    
    private boolean initGate(JmsgGateWayInfo gate)
    {
    	
    	Map<String, String> params = new HashMap<String, String>();
    	params.put("id", gate.getId());
        params.put("host", StringUtils.trimToEmpty(gate.getHost()));
        params.put("port", getStr(gate.getPort()));
        params.put("local-host2", getStr(gate.getLocalHost()));
        params.put("local-port2", getStr(gate.getLocalPort()));
        params.put("read-timeout", getStr(gate.getReadTimeout()));
        params.put("reconnect-interval", getStr(gate.getReconnectInterval()));
        params.put("transaction-timeout", getStr(gate.getTransactionTimeout()));
        params.put("heartbeat-interval", getStr(gate.getHeartbeatInterval()));
        params.put("heartbeat-noresponseout", getStr(gate.getHeartbeatNoresponseout()));
        params.put("source-addr", StringUtils.trimToEmpty(gate.getSourceAddr()));
        params.put("shared-secret", StringUtils.trimToEmpty(gate.getSharedSecret()));
        params.put("login-name", StringUtils.trimToEmpty(gate.getSourceAddr()));
        params.put("login-pass", StringUtils.trimToEmpty(gate.getSharedSecret()));
        params.put("version", StringUtils.trimToEmpty(gate.getVersion()));
        params.put("spNumber", StringUtils.trimToEmpty(gate.getSpNumber()));
        params.put("debug", getBoolean(gate.getDebug()));
        params.put("corpId", StringUtils.trimToEmpty(gate.getCorpId()));
        params.put("msg_Src", StringUtils.trimToEmpty(gate.getCorpId()));
        params.put("gateWayID", StringUtils.trimToEmpty(gate.getId()));
        params.put("limit", String.valueOf(gate.getWriteLimit()));
        if(StringUtils.isNotBlank(gate.getExtParam())) {
        	String jsonParams = Encodes.unescapeHtml(gate.getExtParam());
	        Map<String,String> extParam = JSON.parseObject(jsonParams, Map.class);
	        params.putAll(extParam);
        }
        
        if (logger.isInfoEnabled())
        {
            logger.info("初始化网关:[" + gate + "]\r\n" + "params:" + gate.toString());
        }
        
        SGIPSMProxy proxy = new SGIPSMProxy(params);
//        String localUser = getStr(params.get("local-user"));
//        String localPass = getStr(params.get("local-pass"));
//        if(StringUtils.isNotBlank(localUser) && StringUtils.isNotBlank(localPass)) {
//        	proxy.setLocalUser(localUser);
//        	proxy.setLocalPass(localPass);
//        }
		boolean result = proxy.connect();
		logger.info("网关ID:{}, connect:{}", gate.getId(), result);
		if (result) {
			proxy.startService();
			proxy.setMoListener(moListener);
			
			synchronized (this) {
				GATEWAY_MAP.put(gate.getId(), proxy);
				SmsGateWay smsGateWay = getSmsGateWay(gate.getId());
				if(smsGateWay == null) {
					smsGateWay = new SmsGateWay(gate.getId(), gate.getWriteLimit(), (CMPPGatewayFactory) this);
					smsGateWay.startup();
				}
				logger.info("网关ID:{}, startup", gate.getId());
			}
			
			return true;
		}
        
        return false;
    }
    
    @Override
    public void closeAll()
    {
    	Iterator<String> gatewayIt = GATEWAY_MAP.keySet().iterator();
        while (gatewayIt.hasNext()) {
            closeGateway(gatewayIt.next());
        }
        
        GATEWAY_MAP.clear();
    }
    
    @Override
    public boolean closeGateway(String id)
    {
        //修改网关状态
//        gatewayService.updateGatewayStateById(GateStateEnum.DISABLED.getValue(), id);
        SGIPSMProxy proxy = (SGIPSMProxy) GATEWAY_MAP.remove(id);
        try
        {
        	//关闭队列
        	SmsGateWay smsGateway = getSmsGateWay(id);
        	if( smsGateway != null) smsGateway.shutdown();
        	
        	//关闭连接
            if (proxy.getConn().available())
            {
                proxy.close();
            }
        }
        catch (Exception e)
        {
            //e.printStackTrace();
            logger.error("网关关闭失败", e);
        } finally {
            proxy.stopService();
            
            proxy = null;
        }
        
        return true;
    }
    
    @Override
    public boolean closeGatewayTemp(String id)
    {
        //修改网关状态
        closeGateway(id);
        return true;
    }
    
    @Override
    public boolean openGateway(String id)
    {
        Object gate = GATEWAY_MAP.get(id);
        if (gate != null)
        {
            return true;
        }
        JmsgGateWayInfo gateway = gatewayService.findGateway(id);
        if (gateway == null)
        {
            return false;
        }
        try
        {
            boolean res = initGate(gateway);
            if (res)
            {
                gatewayService.updateGatewayStateById(GateStateEnum.ENABLED.getValue(), id);
            }
            else
            {
                gatewayService.updateGatewayStateById(GateStateEnum.ERROR.getValue(), id);
            }
            return res;
        }
        catch (Exception e)
        {
            gatewayService.updateGatewayStateById(GateStateEnum.ERROR.getValue(), id);
            logger.error("初始化网关失败", e);
        }
        return false;
    }
    
    @Override
    public boolean hasGateway(String id)
    {
        return GATEWAY_MAP.get(id) != null;
    }
    
    @Override
    public Object getGateway(String gateWayID)
    {
        logger.info("{}通道 ", gateWayID);
        if (!GATEWAY_MAP.containsKey(gateWayID))
        {
            try
            {
                JmsgGateWayInfo gateway = gatewayService.findGateway(gateWayID);
                
                if (!initGate(gateway))
                {
                    return null;
                }
            }
            catch (Exception e)
            {
                return null;
            }
        }
        return (SGIPSMProxy)GATEWAY_MAP.get(gateWayID);
    }
    
    @Override
    public Result sendMsg(SmsMtMessage msg)
    {
    	
//    	if(StringUtils.equals(msg.getGateWayID(), "LT8004") && StringUtils.equals(msg.getUserid(), "387679")) {
//    		storedMap.put(msg.getId(), msg);
//    		return new Result("T100", "成功");
//    	}
    	
    	SGIPSMProxy proxy = (SGIPSMProxy)this.getGateway(msg.getGateWayID());
        if (proxy == null)
        {
            logger.error("{}通道, 未启动", msg.getGateWayID());
            return new Result("F10104", String.format("%s通道, 未启动", msg.getGateWayID()));
        }
        else
        {
        	List<SGIPSubmitMessage> submitIt = convertMTMessage(msg, false, proxy.getArgs()); 
        	Iterator<SGIPSubmitMessage> it = submitIt.iterator();
        	while(it.hasNext()) {
        		SGIPSubmitMessage submit = it.next();
        		submit.setAttachment(msg);
	        	try {
	        		SGIPSubmitRepMessage res = (SGIPSubmitRepMessage) proxy.send(submit);
					if(res == null) {
						SmsSrMessage smsSrMessage = new SmsSrMessage(new MsgId().toString(), "1503", msg);
	                    gateWayMessage.sendSmsSRMessage(smsSrMessage, proxy.getArgs().get("id","未知"));
						break;
					}
					
					int result = res.getResult();
					SmsSrMessage smsSrMessage = new SmsSrMessage(res.getSubmitSequenceNumberStr(), String.valueOf(result), msg);
                    gateWayMessage.sendSmsSRMessage(smsSrMessage, proxy.getArgs().get("id","未知"));
        		
					if (result != 0) {
						break;
                	}
					
				} catch (Exception e) {
					return new Result("F10121", e.getMessage());
				}
        	}
        	return new Result("T100", "成功");
        }
    }
    
    public List<SGIPSubmitMessage> convertMTMessage(SmsMtMessage msg, boolean gatewaySign, Args args){
    	List<SGIPSubmitMessage> submitList = Lists.newArrayList();
    	List<LongMessageFrame> messageFrames = (List<LongMessageFrame>)gateWayMessage.convertMTMessage(msg, gatewaySign);
    	for(LongMessageFrame frame : messageFrames) {
    		String SPNumber     = msg.getSpNumber();//之后为附加码
    		String ChargeNumber = "000000000000000000000";
    		String[] UserNumber = msg.getPhone().split(",");//拆分手机号码
    		String CorpId       = String.valueOf(args.get("corpId", ""));
    		String serviceType = getStr(args.get("serviceType", "HELP"));
    		
    		SGIPSubmitMessage submit = new SGIPSubmitMessage(
                    SPNumber,  //"10690843", // SP的接入号码
                    ChargeNumber, //"", // 付费号码 string
                    UserNumber, // 接收该短消息的手机号，最多100个号码 string[]
                    CorpId, //"98801", // 企业代码，取值范围为0～99999 string
                    serviceType, //"9951005601", // 业务代码，由SP定义 stirng
                    03, // 计费类型 int
                    "0", // 该条短消息的收费值 stirng
                    "0", // 赠送用户的话费 string
                    0, // 代收费标志0：应收1：实收 int
                    0, // 引起MT消息的原因 int
                    06, // 优先级0～9从低 到高，默认为0 int
                    null, // 短消息寿命的终止时间 date
                    null, // 短消息定时发送的时间 date
                    1, // 状态报告标记 int
                    frame.getTppid(), // GSM协议类型 int
                    frame.getTpudhi(), // GSM协议类型 int
                    frame.getMsgfmt().getValue(), //15, // 短消息的编码格式 int
                    0, // 信息类型 int
                    frame.getMsgLength(),   // 短消息内容长度 int
                    frame.getMsgContentBytes(),   //MessageContent, // 短消息的内容 btye[]
                    "0" // 保留，扩展用 string
            );


            submitList.add(submit);
    	}
    	return submitList;
    }
    
    @Override
    public GateEnum getGatewayType(String gatewayId)
    {
        return GateEnum.HTTP;
    }
    
    @Override
    public SmsMtMessage getSubmitResult(String gID, String msgid)
    {
        return null;
    }
    
    public void setMoListener(MoListener moListener)
    {
        this.moListener = moListener;
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
        Iterator<Map.Entry<String, Object>> gatewayIt = GATEWAY_MAP.entrySet().iterator();
        StringBuilder buff = new StringBuilder();
        buff.append("当前启用的网关:数量:" + GATEWAY_MAP.size() + "\r\n");
        while (gatewayIt.hasNext()) {
            Entry<String, Object> gEntry = gatewayIt.next();
            buff.append("网关ID：" + gEntry.getKey() + ",网关状态:" + ((SGIPSMProxy)gEntry.getValue()).getStatus() + ";\r\n");
        }
        stat.warn(buff.toString());
    }
}
