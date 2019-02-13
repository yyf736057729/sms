package com.siloyou.jmsg.modules.api.cmpp;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import com.zx.sms.codec.cmpp.msg.CmppDeliverRequestMessage;
import com.zx.sms.codec.cmpp.msg.CmppReportRequestMessage;
import com.zx.sms.common.util.MsgId;
import com.zx.sms.common.util.ChannelUtil;
import com.zx.sms.handler.api.BusinessHandlerInterface;
import com.zx.sms.connect.manager.CMPPEndpointManager;
import com.zx.sms.connect.manager.cmpp.CMPPServerChildEndpointEntity;
import com.zx.sms.connect.manager.cmpp.CMPPServerEndpointEntity;

import com.zx.sms.mbean.ConnState;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.RateLimiter;
import com.Application;
import com.siloyou.jmsg.common.gateway.SmsGateWay;
import com.siloyou.jmsg.common.message.SmsMoMessage;
import com.siloyou.jmsg.common.message.SmsMtMessage;
import com.siloyou.jmsg.common.message.SmsRtMessage;
import com.siloyou.jmsg.common.util.enums.GateEnum;
import com.siloyou.jmsg.gateway.Result;
import com.siloyou.jmsg.gateway.api.GateWayMessageAbstract;
import com.siloyou.jmsg.gateway.api.GatewayFactory;
import com.siloyou.jmsg.modules.sms.dao.JmsgUserGatewayDao;
import com.siloyou.jmsg.modules.sms.entity.JmsgUserGateway;
import com.siloyou.jmsg.modules.sms.web.SettingAction;

import io.netty.channel.ChannelFuture;
import io.netty.util.concurrent.GenericFutureListener;

public class GateWayFactory implements GatewayFactory 
{
	private static final Logger                logger      = LoggerFactory.getLogger(GateWayFactory.class);
	
	RateLimiter reportLimit = RateLimiter.create(2000);
	// key:spnumber value:username
	public static Map<String, String> map = new HashMap<String, String>();
	public static Map<String, JmsgUserGateway> userGateways = Maps.newHashMap();
	private GateWayMessage gateWayMessage;
	private JmsgUserGatewayDao jmsgUserGatewayDao;

	final CMPPEndpointManager manager = CMPPEndpointManager.INS;
	final CMPPServerEndpointEntity server = new CMPPServerEndpointEntity();
	
	private static Cache<String, String> cache =CacheBuilder.newBuilder().expireAfterAccess(3600, TimeUnit.SECONDS).build();
	private static ConcurrentMap<String, String> reportOver = cache.asMap();
    
    public void initGateway(String appCode) {
    	
    	Timer timer = new Timer();
        timer.schedule(new MonitorTask(), 30000, 10000);
        
        //加载上行规则
        SettingAction moRule = Application.applicationContext.getBean(SettingAction.class);
        moRule.moRule();
        logger.info("上行转发规则加载完成，共:{}条", map.size());
        
    	//设置socket监听
		server.setId(Application.evn.getProperty("app.gatewayid"));
//		server.setHost(Application.evn.getProperty("app.gatewayip"));
		server.setPort(Integer.parseInt(Application.evn.getProperty("app.gatewayport")));
		server.setValid(true);
		//使用ssl加密数据流
		server.setUseSSL(false);
		
		//List<JmsgUserGateway> list = jmsgUserGatewayDao.findAll();
		List<JmsgUserGateway> list = jmsgUserGatewayDao.loadValidAll();
		for(JmsgUserGateway jmsgUserGateway : list) {
			initGate(jmsgUserGateway);
		}
		
		logger.info("模拟网关用户数:{}", server.getAllChild().size());
		manager.addEndpointEntity(server);
		
		try {
			manager.openAll();
			logger.info("模拟网关启动完成");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//开消息启监听
		Application.applicationContext.getBean(MQStartup.class).init();
    }
    
    public boolean initGate(JmsgUserGateway userGateWay){
    	if (logger.isInfoEnabled()) {
            logger.info("初始化网关:[" + userGateWay + "]\r\n" + "params:" + JSON.toJSONString(userGateWay));
        }
    	
    	userGateways.put(userGateWay.getUserid(), userGateWay);
    	
    	CMPPServerChildEndpointEntity child = new CMPPServerChildEndpointEntity();
    	child.setValid(true);
    	child.setVersion((short) 32);
    	child.setChartset(Charset.forName("utf-8"));
    	child.setReSendFailMsg(true);
    	
    	child.setGroupName(userGateWay.getAppCode());
		child.setId(userGateWay.getId());
		child.setUserName(userGateWay.getUserid());
		child.setPassword(userGateWay.getPassword());
//		child.setMsgSrc(userGateWay.getUserid());
		child.setHost(userGateWay.getAllowIP());
		
		child.setMaxChannels(getDefaultArgs(userGateWay.getMaxChannels(), (short)1));//20
		child.setRetryWaitTimeSec(getDefaultArgs(userGateWay.getRetryWaitTime(), (short)10));//100
		child.setMaxRetryCnt(getDefaultArgs(userGateWay.getMaxRetryCnt(), (short)3));
		child.setReadLimit(getDefaultArgs(userGateWay.getReadLimit(), (short) 0));
		child.setWriteLimit(getDefaultArgs(userGateWay.getWriteLimit(), (short) 0));
	
		List<BusinessHandlerInterface> serverhandlers = new ArrayList<BusinessHandlerInterface>();
		serverhandlers.add(new CmppGateWayHandler());
		child.setBusinessHandlerSet(serverhandlers);
		server.addchild(child);

		if (!map.containsKey(userGateWay.getSpnumber()))
		{
//		    map.put(userGateWay.getSpnumber(), userGateWay.getUsername());

			map.put(userGateWay.getSpnumber(), userGateWay.getUserid());
		}
    	
    	return true;
    }
    
    public void closeAll() {
        if (manager != null) {
            manager.close();
        }
    }
    
    public void setGateWayMessage(GateWayMessage gateWayMessage) {
    	this.gateWayMessage = gateWayMessage;
    }

	@Override
	public boolean closeGateway(String userid) {
		CMPPServerChildEndpointEntity cmppEntity = (CMPPServerChildEndpointEntity) server.getChild(userid);//登录名username
		if(null == cmppEntity) {
			return true;
		}
		server.removechild(cmppEntity);
		logger.info("网关关闭 {}" , userid);
		return true;
	}

	@Override
	public boolean closeGatewayTemp(String userid) {
		return closeGateway(userid);
	}

	@Override
	public boolean openGateway(String userid) {
		CMPPServerChildEndpointEntity cmppEntity = (CMPPServerChildEndpointEntity) server.getChild(userid);//登录名username
		if ( null != cmppEntity ) {
			return true;
		}
//		JmsgUserGateway jmsgUserGateway = (JmsgUserGateway)jmsgUserGatewayDao.selectByPrimaryKey(username);
		JmsgUserGateway jmsgUserGateway = (JmsgUserGateway)jmsgUserGatewayDao.getUserGatewayByUserid(userid);
		if (null != jmsgUserGateway)
		{
		    return initGate(jmsgUserGateway);
		}
		
		return false;
	}

	@Override
	public boolean hasGateway(String userid) {
		CMPPServerChildEndpointEntity cmppEntity = (CMPPServerChildEndpointEntity) server.getChild(userid);//登录名username
		if(null == cmppEntity) {
			return false;
		}
		return true;
	}

	@Override
	public Object getGateway(String gatewayId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Result sendMsg(SmsMtMessage msg) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Result sendMsg(SmsMtMessage msg, int level) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GateEnum getGatewayType(String gatewayId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SmsMtMessage getSubmitResult(String gID, String msgid) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public boolean sendReport(SmsRtMessage message) {
		final String username = message.getSmsMt().getUserid();
		
		CMPPServerChildEndpointEntity cmppServerChild = (CMPPServerChildEndpointEntity) server.getChild(username);
		if( null == cmppServerChild) {
			logger.info("响应节点为空,[{}, {}, {}]", username, message.getSmsMt().getTaskid(), message.getDestTermID());
			return false;
		}
		
		final String taskid = message.getSmsMt().getTaskid();
		String[] msgIds = {taskid};
		if(StringUtils.isNotBlank(message.getSmsMt().getCstmOrderID())) {
			msgIds = message.getSmsMt().getCstmOrderID().split("-");
			
			String oldTaskId = reportOver.putIfAbsent(taskid, "1");
			if(oldTaskId != null) {
				return true;
			}
		}
		
		int idx = 0;
		for(final String msgid : msgIds) {
			reportLimit.acquire();

			CmppReportRequestMessage cmppReportRequestMessage = new CmppReportRequestMessage();
			cmppReportRequestMessage.setMsgId(new MsgId(msgid));

			CmppDeliverRequestMessage cmppDeliverRequestMessage = new CmppDeliverRequestMessage();
			cmppDeliverRequestMessage.setReportRequestMessage(cmppReportRequestMessage); //1 状态报告
			cmppDeliverRequestMessage.setAttachment(message.getSmsMt().getId());
			
			if(StringUtils.isNotBlank(message.getSrcTermID())) {
				cmppDeliverRequestMessage.setDestId(message.getSrcTermID());
			}
			
			if(StringUtils.isNotBlank(message.getDestTermID())) {
				cmppDeliverRequestMessage.setSrcterminalId(message.getDestTermID());
			}
//			cmppDeliverRequestMessage.setReport(true);
			cmppDeliverRequestMessage.setServiceid("");
			cmppDeliverRequestMessage.setMsgId(new MsgId());
			

			if(StringUtils.isNotBlank(message.getDestTermID())) {
				cmppReportRequestMessage.setDestterminalId(message.getDestTermID());
			}
			
			if(StringUtils.isNotBlank(message.getDoneTime())) {
				cmppReportRequestMessage.setDoneTime(message.getDoneTime());
			}
			
			if (StringUtils.isNotBlank(message.getSubmitTime())) {
				cmppReportRequestMessage.setSubmitTime(message.getSubmitTime());
			}
			
			if(StringUtils.isNotBlank(message.getStat())) {
				cmppReportRequestMessage.setStat(message.getStat());
			}
			
			cmppDeliverRequestMessage.setReportRequestMessage(cmppReportRequestMessage);
			ChannelFuture future = ChannelUtil.asyncWriteToEntity(cmppServerChild, cmppDeliverRequestMessage, new GenericFutureListener<ChannelFuture>() {
				@Override
				public void operationComplete(ChannelFuture future) throws Exception {
					if (!future.isSuccess()) {
//						if(StringUtils.equals(msgid , taskid)) {
//							getReportMap(username,  null).remove(taskid);
//						}
						
						StringBuilder sb = new StringBuilder();
						sb.append("Send cmppDeliverRequestMessage [taskid=").append(taskid).append(", msgid=").append(msgid).append("] Failed. ");
						logger.error(sb.toString(), future.cause());
					}
				}
			});
			if(future == null) {
				logger.error("SendMessage {} Failed. Channel is not Active", cmppDeliverRequestMessage.toString());
			}
			
		}
		
		return true;
	}
	
	public boolean sendDeliver(SmsMoMessage message) {

		/**
		 * 优先全号匹配，如果匹配不到，轮循查找
		 */
		String username = map.get(message.getSrcTermID());
		String srcSpnumber = "";
		if(StringUtils.isBlank(username))
		{
			for(Entry<String, String> entry : map.entrySet())
			{
				String key = entry.getKey();
//			    if (message.getSrcTermID().startsWith(key))
				if(message.getSrcTermID().endsWith(key))
			    {
			    	if (key.length() > srcSpnumber.length()) {
				        username = entry.getValue();
				        srcSpnumber = key;
			    	}
			    }
			}
		}
		if(StringUtils.isBlank(srcSpnumber)) {
			srcSpnumber = message.getSrcTermID();
		}
		
		if(StringUtils.isBlank(username)) {
			logger.info("接收上行客户端未配置, [{}, {}, {}]", username, message.getSrcTermID(), message.getDestTermID());
			return false;
		}
		
		CMPPServerChildEndpointEntity cmppServerChild = (CMPPServerChildEndpointEntity) server.getChild(username);
		if( null == cmppServerChild ) {
            logger.error("接收上行客户端未连接, [{}, {}, {}]", username, message.getSrcTermID(), message.getDestTermID());
			return false;
		}
		
        if(!userGateways.isEmpty() && userGateways.containsKey(username)) {
        	String spNumber = userGateways.get(username).getSpnumber();
        	if(StringUtils.isNotBlank(spNumber) && StringUtils.equals(spNumber, "00000000"))  {
        		spNumber = "";
        	}
        	
        	switch(userGateways.get(username).getAllnumPush()) {
        	case 0:   // 默认全号
        		break;
        		
        	case 1:  // 截取匹配长度
        		message.setSrcTermID(spNumber + message.getSrcTermID().substring(srcSpnumber.length()));
        		break;
        	case 2:  // 截取固定长度
        		if(message.getSrcTermID().length() >= userGateways.get(username).getSubstringLength()) {
        			message.setSrcTermID(spNumber + message.getSrcTermID().substring(userGateways.get(username).getSubstringLength()));
        		}
        		break;
        		default:
        			break;
        	}
        }
		
        if(message.getSrcTermID().length() > 20) {
        	message.setSrcTermID(message.getSrcTermID().substring(0, 20));
        }
        
		CmppDeliverRequestMessage cmppDeliverRequestMessage = new CmppDeliverRequestMessage();
//		cmppDeliverRequestMessage.setReport(false);
		cmppDeliverRequestMessage.setDestId(message.getSrcTermID());
		cmppDeliverRequestMessage.setSrcterminalId(message.getDestTermID());
		cmppDeliverRequestMessage.setMsgId(new MsgId());
		cmppDeliverRequestMessage.setServiceid("");
		cmppDeliverRequestMessage.setMsgContent(message.getMsgContent());
		System.out.println("下游-----------------发送");
		ChannelUtil.asyncWriteToEntity(cmppServerChild, cmppDeliverRequestMessage);
		return true;
	}
	
//	public Map<String, Serializable> getReportMap(String id, String time) {
//		if(time == null) {
//			time = String.format("%tY%<tm%<td", CachedMillisecondClock.INS.now());
//		}
//		return BDBStoredMapFactoryImpl.INS.buildMap(id, "Report_" + time);
//	}
	
	private short getDefaultArgs(String value, short defaultValue) {
		if(StringUtils.isBlank(value)) {
			return defaultValue;
		}
		
		return Short.parseShort(value);
	}

	public void setJmsgUserGatewayDao(JmsgUserGatewayDao jmsgUserGatewayDao) {
		this.jmsgUserGatewayDao = jmsgUserGatewayDao;
	}
	
	public GateWayMessageAbstract getGateWayMessage()
    {
        return null;
    }
	
	
	public void addSmsGateWay(String id, SmsGateWay gateWay) {
		
	}
	
	public SmsGateWay getSmsGateWay(String id) {
		return null;
	}
	
	public Result setGatewaySendRate(String gatewayId, int permitsPerSecond) {
		return null;
	}
	
	public boolean closev1(String id) {
		return true;
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

//      Iterator<EndpointEntity> gatewayIt = manager.allEndPointEntity().iterator();
//      StringBuilder buff = new StringBuilder();
//      buff.append("当前启用的网关:数量:" + manager.allEndPointEntity().size()  + "\r\n");
//      while (gatewayIt.hasNext()) {
//      	EndpointEntity gEntry = gatewayIt.next();
//          buff.append("网关ID：" + gEntry.getId() + ",网关状态:" + manager.getEndpointConnector(gEntry).fetch().isActive() + ";\r\n");
//      }
//      logger.warn(buff.toString());

      ConnState connState = new ConnState();
      logger.warn(connState.print(""));
      
  }
}
