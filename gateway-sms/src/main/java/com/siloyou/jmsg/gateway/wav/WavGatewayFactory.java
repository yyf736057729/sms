package com.siloyou.jmsg.gateway.wav;

import java.lang.reflect.Constructor;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.rocketmq.client.consumer.DefaultMQPullConsumer;
import com.google.common.collect.Maps;
import com.siloyou.jmsg.common.message.SmsMtMessage;
import com.siloyou.jmsg.common.util.Encodes;
import com.siloyou.jmsg.common.util.enums.GateEnum;
import com.siloyou.jmsg.common.util.enums.GateStateEnum;
import com.siloyou.jmsg.gateway.Result;
import com.siloyou.jmsg.gateway.api.BaseGatewayFactory;
import com.siloyou.jmsg.gateway.api.GateWayMessageAbstract;
import com.siloyou.jmsg.gateway.api.GatewayFactory;
import com.siloyou.jmsg.gateway.api.GatewayService;
import com.siloyou.jmsg.gateway.api.JmsgGateWayInfo;
import com.siloyou.jmsg.gateway.wav.handler.GateWayWavAbstract;

/**
 * 
 * <一句话功能简述>
 * <功能详细描述>
 * 
 * @author  zhangjie
 * @version  [版本号, 2016年8月27日]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public class WavGatewayFactory extends BaseGatewayFactory 
{
    private static final Logger logger = LoggerFactory.getLogger(WavGatewayFactory.class);
    
    private GatewayService gatewayService;
    
    private GateWayMessageAbstract gateWayMessage;
    
    private Map<String, GateWayWavAbstract> GATEWAY_MAP = Maps.newConcurrentMap();
    
    @Override
    public void initGateway(String appCode)
    {
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
    
    private synchronized boolean initGate(JmsgGateWayInfo gateway)
    {
    	if (GATEWAY_MAP.containsKey(gateway.getId())) {
    		return true;
    	}
    	
        if (logger.isInfoEnabled())
        {
            logger.info("初始化网关:[" + gateway + "]\r\n" + "params:" + gateway.toString());
        }
        
        if (GateEnum.HTTP.getValue().equalsIgnoreCase(gateway.getType()))
        {
            if (gateway == null || StringUtils.isBlank(gateway.getExtClass()))
                return false;
            
            String gateWayID = gateway.getId();
            String className = gateway.getExtClass();
            
            try
            {
                Class clazz = Class.forName(className);
                Constructor<GateWayWavAbstract> constructor = clazz.getConstructor(String.class, String.class);
                GateWayWavAbstract gatewayImpl = constructor.newInstance(gateWayID, gateway.getExtParam());
                GATEWAY_MAP.put(gateWayID, gatewayImpl);
                
                //状态获取方式 0：主动查询 1：异步通知
                if (gateway.getReportGetFlag() == 0)
                {
                    String jsonParams = Encodes.unescapeHtml(gateway.getExtParam());
                    Map<String, Object> paraMap = JSON.parseObject(jsonParams);
                    
                    int reportSecTime = 0;
                    int deliverSecTime = 0;
                    if (null != paraMap.get("reportSecTime") && StringUtils.isNotBlank(paraMap.get("reportSecTime").toString()))
                    {
                        reportSecTime = Integer.parseInt(paraMap.get("reportSecTime").toString());
                    }
                    if (null != paraMap.get("deliverSecTime") && StringUtils.isNotBlank(paraMap.get("deliverSecTime").toString()))
                    {
                        deliverSecTime = Integer.parseInt(paraMap.get("deliverSecTime").toString());
                    }
                    
                    gatewayImpl.startListener(reportSecTime, deliverSecTime);
                }
            }
            catch (Exception e)
            {
                logger.error("初始化网关" + gateway.getId() + "失败", e);
                return false;
            }
        }
        else
        {
            return false;
        }
        
        //修改网关状态
        gatewayService.updateGatewayStateById(GateStateEnum.ENABLED.getValue(), gateway.getId());
        
        return true;
    }
    
    @Override
    public void closeAll()
    {
        GATEWAY_MAP.clear();
    }
    
    @Override
    public boolean closeGateway(String id)
    {
        //修改网关状态
        gatewayService.updateGatewayStateById(GateStateEnum.DISABLED.getValue(), id);
        GATEWAY_MAP.remove(id);
        return true;
    }
    
    @Override
    public boolean closeGatewayTemp(String id)
    {
        //修改网关状态
        gatewayService.updateGatewayStateById(GateStateEnum.DISABLED.getValue(), id);
        GATEWAY_MAP.remove(id);
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
        return (GateWayWavAbstract)GATEWAY_MAP.get(gateWayID);
    }
    
    @Override
    public Result sendMsg(SmsMtMessage msg)
    {
        GateWayWavAbstract gateWay = (GateWayWavAbstract)this.getGateway(msg.getGateWayID());
        if (gateWay == null)
        {
            logger.error("{}通道, 未启动", msg.getGateWayID());
            return new Result("F10104", String.format("%s通道, 未启动", msg.getGateWayID()));
        }
        else
        {
            SmsMtMessage e = (SmsMtMessage)gateWayMessage.convertMTMessage(msg, gateWay.isGatewaySign());
            boolean sendFlg = gateWay.send(e);
            if(sendFlg) {
                return new Result("T100", "成功");
            }else{
                return new Result("F10105", String.format("send error", msg.getGateWayID()));
            }
        }
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
    
    public void setGatewayService(GatewayService gatewayService)
    {
        this.gatewayService = gatewayService;
    }
    
    public void setGateWayMessage(GateWayMessageAbstract gateWayMessage)
    {
        this.gateWayMessage = gateWayMessage;
    }
    
    public GateWayMessageAbstract getGateWayMessage()
    {
        return this.gateWayMessage;
    }
    
}
