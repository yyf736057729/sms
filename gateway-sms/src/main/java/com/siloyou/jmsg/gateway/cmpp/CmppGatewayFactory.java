/**
 * zoneland.net Inc.
 * Copyright (c) 2002-2012 All Rights Reserved.
 */
package com.siloyou.jmsg.gateway.cmpp;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.*;

import com.siloyou.jmsg.common.util.LimitQueue;
import com.siloyou.jmsg.gateway.cmpp.handler.SmsBizHandler;
import com.siloyou.jmsg.modules.api.cmpp.GateWayMessage;
import com.zx.sms.common.util.MsgId;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.siloyou.jmsg.common.message.SmsMtMessage;
import com.siloyou.jmsg.common.storedMap.BDBStoredMapFactoryImpl;
import com.siloyou.jmsg.common.util.Encodes;
import com.siloyou.jmsg.common.util.enums.GateEnum;
import com.siloyou.jmsg.common.util.enums.GateStateEnum;
import com.siloyou.jmsg.gateway.Result;
import com.siloyou.jmsg.gateway.api.BaseGatewayFactory;
import com.siloyou.jmsg.gateway.api.GatewayFactory;
import com.siloyou.jmsg.gateway.api.JmsgGateWayInfo;
import com.siloyou.jmsg.gateway.cmpp.message.CmppGateWayMessage;
import com.zx.sms.codec.cmpp.msg.CmppSubmitRequestMessage;
import com.zx.sms.common.util.ChannelUtil;
import com.zx.sms.connect.manager.CMPPEndpointManager;
import com.zx.sms.connect.manager.EndpointEntity;
import com.zx.sms.connect.manager.EndpointEntity.ChannelType;
import com.zx.sms.connect.manager.cmpp.CMPPClientEndpointEntity;
import com.zx.sms.connect.manager.cmpp.CMPPEndpointEntity;
import com.zx.sms.handler.api.BusinessHandlerInterface;
import com.zx.sms.mbean.ConnState;

import io.netty.channel.ChannelFuture;

/**
 *
 * @author gang
 * @version $Id: UmsGatewayFactory.java, v 0.1 2012-8-31 下午10:04:04 gang Exp $
 */
public class CmppGatewayFactory extends BaseGatewayFactory {

    private static final Logger                logger      = LoggerFactory.getLogger(GatewayFactory.class);

    final CMPPEndpointManager manager = CMPPEndpointManager.INS;

    private CmppGateWayMessage cmppGateWayMessage;

    public static final HashMap<String,String> connect_map = new HashMap<>();

    public CmppGateWayMessage getCmppGateWayMessage() {
        return cmppGateWayMessage;
    }

    public void setCmppGateWayMessage(CmppGateWayMessage cmppGateWayMessage) {
        this.cmppGateWayMessage = cmppGateWayMessage;
    }

    public void initGateway(String appCode) {
        Timer timer = new Timer();
        timer.schedule(new MonitorTask(), 30000, 10000);
        List<JmsgGateWayInfo> gateways = gatewayService.loadValidAll(appCode);
        gateways.remove(4);
        List<JmsgGateWayInfo> gateways2 = new ArrayList<JmsgGateWayInfo>();
//        gateways2.add(gateways.get(4));//三个开发人员, 一个人指定用一个, 张辉20181116
        Iterator<JmsgGateWayInfo> it = gateways.iterator();
//        Iterator<JmsgGateWayInfo> it = gateways2.iterator();
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
        logger.info("初始化网关完毕");
    }

    public void closeAll() {
        if (manager != null) {
            manager.close();
        }
    }

    private boolean initGate(JmsgGateWayInfo gate) {
        if (logger.isInfoEnabled()) {
            logger.info("初始化网关:[" + gate + "]\r\n" + "params:" + gate.toString());
        }
        if (GateEnum.CMPP.getValue().equalsIgnoreCase(gate.getType())
        		|| GateEnum.CMPP30.getValue().equalsIgnoreCase(gate.getType()))
        {
			CMPPClientEndpointEntity cmppClient = new CMPPClientEndpointEntity();
			cmppClient.setId(gate.getId());
			cmppClient.setGroupName(gate.getAppCode());
			cmppClient.setChannelType(ChannelType.DUPLEX);
			cmppClient.setHost(gate.getHost());
			cmppClient.setPort(gate.getPort());
			cmppClient.setUserName(gate.getSourceAddr());
			cmppClient.setPassword(gate.getSharedSecret());
			cmppClient.setChartset(Charset.forName("utf-8"));
			
			if(GateEnum.CMPP30.getValue().equalsIgnoreCase(gate.getType())){
				cmppClient.setVersion((short) 48);	
			}else {
				cmppClient.setVersion((short) 32);	
			}
			
			cmppClient.setRetryWaitTimeSec((short) 5);
			cmppClient.setMaxChannels((short) 10);
			cmppClient.setMaxRetryCnt((short) 0);

			// 暂时不保存以下信息到Client（qtang）
			//cmppClient.setMsgrc(gate.getCorpId());
			//cmppClient.setGatewaySign((gate.getGatewaySign() == 1));//服务端签名
			
			List<BusinessHandlerInterface> clienthandlers = new ArrayList<BusinessHandlerInterface>();
			clienthandlers.add(new SmsBizHandler());
			cmppClient.setBusinessHandlerSet(clienthandlers);
			manager.addEndpointEntity(cmppClient);
			
			int connections = 1;
	        if (StringUtils.isNotBlank(gate.getExtParam()))
	        {
	            String jsonParams = Encodes.unescapeHtml(gate.getExtParam());
	            Map<String, String> paraMap = JSON.parseObject(jsonParams, Map.class);
	            
	            if (StringUtils.isNotBlank(paraMap.get("isWholeSpNumber")) && StringUtils.equals(paraMap.get("isWholeSpNumber"), "1"))
	            {
	                gate.setWholeSpNumber(true);
	            }
	            else
	            {
	            	gate.setWholeSpNumber(false);
	            }
	            
	            if (StringUtils.isNotBlank(paraMap.get("serviceid")))
	            {
	                gate.setServiceId(paraMap.get("serviceid"));
	            }
	            
	            if (StringUtils.isNotBlank(paraMap.get("connections")) && StringUtils.isNumeric(paraMap.get("connections")))
	            {
	            	connections = Integer.parseInt(paraMap.get("connections"));
	            }
	            
	            gate.setParam(paraMap);
	        }
	        
	        getGateWayMessage().map.put(gate.getId(), gate);
	        
	      //启动网关
			for(int i=0;i<connections;i ++) {
                manager.openEndpoint(cmppClient);

                //修改网关状态
                System.out.println(GateStateEnum.ENABLED.getValue()+"----"+ gate.getId());
                gatewayService.updateGatewayStateById(GateStateEnum.ENABLED.getValue(), gate.getId());
            }
	        
        }
        return true;
    }

    public boolean closeGateway(String id) {

        EndpointEntity endpointEntity = null;
        try {
            endpointEntity = manager.getEndpointEntity(id);//三个开发人员, 一个人指定用一个, 张辉20181116
        } catch (Exception e) {
        }
        if(null != endpointEntity){
            manager.close();
        }
        manager.remove(id);
        return true;
        /**/

        /*三个开发人员, 一个人指定用一个, 张辉20181116
        manager.close(manager.getEndpointEntity(id));
        manager.remove(id);
        return true;
        */
    }

    public boolean closeGatewayTemp(String id) {
        return closeGateway(id);
    }

    public boolean openGateway(String id) {
        Object gate = manager.getEndpointEntity(id);
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
        return manager.getEndpointEntity(id) != null;
    }


    public EndpointEntity getGateway(String gatewayId) {
    	if(manager.getEndpointEntity(gatewayId) == null) {
    		JmsgGateWayInfo gateway = gatewayService.findGateway(gatewayId);
    		if(!initGate(gateway)){
    			return null;
    		}
    	}
        return manager.getEndpointEntity(gatewayId);
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

//        Iterator<EndpointEntity> gatewayIt = manager.allEndPointEntity().iterator();
//        StringBuilder buff = new StringBuilder();
//        buff.append("当前启用的网关:数量:" + manager.allEndPointEntity().size()  + "\r\n");
//        while (gatewayIt.hasNext()) {
//        	EndpointEntity gEntry = gatewayIt.next();
//            buff.append("网关ID：" + gEntry.getId() + ",网关状态:" + manager.getEndpointConnector(gEntry).fetch().isActive() + ";\r\n");
//        }
//        logger.warn(buff.toString());

        ConnState connState = new ConnState();
        List<String> list = connState.printList("");
        for(int i = 0 ; i<list.size()-1 ; i++){
            String [] arr = list.get(i).split(":");
            String gatewayid = arr[0];//网关id//
            connect_map.put(gatewayid,list.get(i));
        }

        logger.warn(connState.print(""));
        
    }

    //启动修改网关状态
    public void updateGatewayStatus(){
        System.out.println("启动修改网关状态");
        Iterator<EndpointEntity> gatewayIt = manager.allEndPointEntity().iterator();
        while (gatewayIt.hasNext()) {
            EndpointEntity gEntry = gatewayIt.next();
            try {
                if(manager.getEndpointConnector(gEntry).fetch().isActive()){
                    gatewayService.updateGatewayStateById(GateStateEnum.ENABLED.getValue(), gEntry.getId());

                }else {
                    gatewayService.updateGatewayStateById(GateStateEnum.ERROR.getValue(), gEntry.getId());
                }
            }catch (Exception e){
                gatewayService.updateGatewayStateById(GateStateEnum.ERROR.getValue(), gEntry.getId());
            }

        }
        System.out.println("修改网关状态程序完毕");
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
   
   public Result sendMsg(SmsMtMessage msg){
		CMPPEndpointEntity gateWay = (CMPPEndpointEntity)this.getGateway(msg.getGateWayID());
		if( gateWay == null) {
			logger.error("{}通道, 未启动", msg.getGateWayID());
			return new Result("F10104", String.format("%s通道, 未启动", msg.getGateWayID()));
		} else {
            // 暂时写死为没有网关签名（qtang）
            CmppGateWayMessage cmppGateWayMessage = new CmppGateWayMessage();
            CmppSubmitRequestMessage message = (CmppSubmitRequestMessage) cmppGateWayMessage.convertMTMessage(msg, /*gateWay.isGatewaySign()*/false);

            // 暂时不处理MsgSrc字段（qtang）
			//message.setMsgsrc(gateWay.getMsgSrc());
			ChannelFuture future = ChannelUtil.asyncWriteToEntity(gateWay.getId(), message);
            System.out.println("msg_id:"+message.getMsgid());
			if(future == null){
			    logger.error("提交网关网络异常,bizid:{}", msg.getId());
				return new Result("F10111", "提交网关网络异常");
			}
			
			try{
				future.sync();
				return new Result("T100", "成功");
			}catch(Exception ex){
				logger.error("同步到网关异常,bizid:{}, 原因:{}", msg.getId(), ex.getMessage());
				return new Result("F10112", "提交网关网络出错");
			}
		}
   }
   
   public SmsMtMessage getSubmitResult(String gID, String msgid){
	   Map<String, Serializable> storeWaitReportMap = BDBStoredMapFactoryImpl.INS.buildMap(gID, "Report_" +gID);
	   return (SmsMtMessage)storeWaitReportMap.get(msgid);
	   
   }
    
   public GateEnum getGatewayType(String gatewayId){
	   return null;
   }
   
   public CmppGateWayMessage getGateWayMessage()
   {
       return (CmppGateWayMessage) this.gateWayMessage;
   }
}
