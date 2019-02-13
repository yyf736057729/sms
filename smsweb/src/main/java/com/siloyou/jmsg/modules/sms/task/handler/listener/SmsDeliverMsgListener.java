package com.siloyou.jmsg.modules.sms.task.handler.listener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.google.common.collect.Lists;
import com.siloyou.core.common.utils.DateUtils;
import com.siloyou.core.common.utils.FstObjectSerializeUtil;
import com.siloyou.core.common.utils.HttpRequest;
import com.siloyou.core.common.utils.IdGen;
import com.siloyou.core.common.utils.JedisClusterUtils;
import com.siloyou.core.common.utils.SpringContextHolder;
import com.siloyou.core.common.utils.StringUtils;
import com.siloyou.core.modules.sys.entity.User;
import com.siloyou.core.modules.sys.utils.UserUtils;
import com.siloyou.jmsg.common.message.SmsMoMessage;
import com.siloyou.jmsg.common.utils.BlacklistUtils;
import com.siloyou.jmsg.modules.sms.dao.JmsgSmsSendDao;
import com.siloyou.jmsg.modules.sms.entity.JmsgPhoneBlacklist;
import com.siloyou.jmsg.modules.sms.entity.JmsgSmsDeliver;

@Service
public class SmsDeliverMsgListener implements MessageListenerConcurrently{
	public static Logger logger = LoggerFactory.getLogger(SmsDeliverMsgListener.class);
	
	@Autowired
	private JmsgSmsSendDao jmsgSmsSendDao;
	
	@Override
	public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
		List<JmsgSmsDeliver> list = Lists.newArrayList();
		SqlSession sqlSession = SpringContextHolder.getBean(SqlSessionFactory.class).openSession(ExecutorType.BATCH,false);
		try {
			for (MessageExt msg : msgs) {
				logger.info("上行接收:msgid:{}, key:{}", msg.getMsgId(), msg.getKeys());
				SmsMoMessage smsMoMessage = (SmsMoMessage)FstObjectSerializeUtil.read(msg.getBody());
				
				smsMoMessage.setUuid(IdGen.uuid());
				String userId = JedisClusterUtils.get(smsMoMessage.getSrcTermID());
				User user = null;
				if(StringUtils.isBlank(userId)) {
					user = jmsgSmsSendDao.findUser(smsMoMessage);
				} else {
					user = UserUtils.getByNow(userId);
				}
				if(user != null) {
					JmsgSmsDeliver deliver = new JmsgSmsDeliver();
					deliver.setMoMsg(smsMoMessage);
					deliver.setUserId(user.getId());
					deliver.setUpUrl(user.getUpUrl());
					deliver.setRspContentType(user.getRspContentType());
					list.add(deliver);
				}
				String content = smsMoMessage.getMsgContent();
				if(StringUtils.isNotBlank(content)){
					if(content.indexOf("TD") >= 0){
						
						String phone = smsMoMessage.getDestTermID();
						JmsgPhoneBlacklist entity = new JmsgPhoneBlacklist();
						entity.setPhone(phone);
						entity.setScope("1");//全局
						entity.setType("1");//退订黑名单
						//插入数据库
						sqlSession.insert("com.siloyou.jmsg.modules.sms.dao.JmsgPhoneBlacklistDao.batchInsert", entity);
						
						BlacklistUtils.put(phone,1,1);
					}
				}
				
				//插入数据库
				sqlSession.insert("com.siloyou.jmsg.modules.sms.dao.JmsgSmsDeliverDao.batchInsert", smsMoMessage);
			}
			sqlSession.commit();
			logger.info("上行接收处理完成!");
			
			int i=0;
			if(list != null && list.size()>0){
				i=0;
				for (JmsgSmsDeliver jmsgSmsDeliver : list) {
					String param = null;
					String pushFlag = "1";
					String result = "";
					String upUrl = jmsgSmsDeliver.getUpUrl();
					
					if(StringUtils.isBlank(upUrl)){
						pushFlag="0";
					}else{
					    //响应内容类型 0：xml 1：json
	                    if (jmsgSmsDeliver.getRspContentType() == 1)
	                    {
	                        param = jsonResult(jmsgSmsDeliver.getMoMsg());
                            result = HttpRequest.sendPostJson(upUrl, param, null, 3000);//推送
	                    }
	                    else
	                    {
	                        param = result(jmsgSmsDeliver.getMoMsg());
	                        result = HttpRequest.sendTextPost(upUrl, param, null, "UTF-8", 3000);//推送
	                    }
					    
						if(StringUtils.isNotBlank(result) && result.length() >100){
							result = result.substring(0,100);
						}
						logger.info("用户上行推送:URL:{},参数:{}",upUrl,param);
					}
					if(StringUtils.isNotBlank(result) && result.length()>100){
						result= result.substring(0, 100);
					}
					jmsgSmsDeliver.setResult(result);
					jmsgSmsDeliver.setPushFlag(pushFlag);
					sqlSession.insert("com.siloyou.jmsg.modules.sms.dao.JmsgSmsDeliverDao.batchInsertPush", jmsgSmsDeliver);
					i++;
					if(i%200==0){
						sqlSession.commit();
					}
				}
				sqlSession.commit();
			}
		}catch(Exception e){
			logger.error("接收上行异常: {}", e);
		} finally{
			if(sqlSession != null){
				sqlSession.close();
			}
		}
		return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
	}
	
	private String result(SmsMoMessage smsMoMessage){
		
		StringBuffer sb = new StringBuffer("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			sb.append("<result statustext=\""+"提交成功"+"\" status=\""+0+"\">");
			sb.append("<list>").append("<item>");
			sb.append("<id>").append(smsMoMessage.getUuid()).append("</id>");
			sb.append("<phone>").append(smsMoMessage.getDestTermID()).append("</phone>");
			sb.append("<recvnumber>").append(smsMoMessage.getSrcTermID()).append("</recvnumber>");
			sb.append("<smscontent>").append(smsMoMessage.getMsgContent()).append("</smscontent>");
			sb.append("<recvtime>").append(DateUtils.formatDate(new Date(), "yyyyMMddHHmmss")).append("</recvtime>");
			sb.append("</item>").append("</list>").append("</result>");
		return sb.toString();
	}
	
	private String jsonResult(SmsMoMessage smsMoMessage){
	    List<Map<String, String>> rList = new ArrayList<Map<String, String>>();
        Map<String, String> resultMap = new HashMap<String, String>();
        resultMap.put("id", smsMoMessage.getUuid());
        resultMap.put("mobile", smsMoMessage.getDestTermID());
        resultMap.put("srcid", smsMoMessage.getSrcTermID());
        resultMap.put("msgcontent", smsMoMessage.getMsgContent());
        resultMap.put("time", DateUtils.formatDate(new Date(), "yyyyMMddHHmmss"));
        
        rList.add(resultMap);
        
        return JSON.toJSONString(rList);
    }

}
