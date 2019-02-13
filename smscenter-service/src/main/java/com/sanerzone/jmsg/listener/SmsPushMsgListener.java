package com.sanerzone.jmsg.listener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;
import com.sanerzone.common.support.utils.FstObjectSerializeUtil;
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
import com.google.common.collect.Maps;
import com.sanerzone.common.modules.TableNameUtil;
import com.sanerzone.common.modules.account.utils.AccountCacheUtils;
import com.sanerzone.common.support.utils.DateUtils;
import com.sanerzone.common.support.utils.HttpRequest;
import com.sanerzone.common.support.utils.StringUtils;
import com.sanerzone.jmsg.util.MessageExtUtil;
import com.siloyou.jmsg.common.message.SmsRtMessage;

@Service
public class SmsPushMsgListener implements /*MessageListenerConcurrently*/MessageListener {
    private Logger logger = LoggerFactory.getLogger(SmsPushMsgListener.class);

    @Autowired
    SqlSessionFactory sqlSessionFactory;

    @Override
    public Action consume(Message message, ConsumeContext consumeContext) {
        Map<String, List<SmsRtMessage>> map = Maps.newHashMap();
        SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH, false);
        try {
            SmsRtMessage smsRtMessage = (SmsRtMessage) FstObjectSerializeUtil.read(message.getBody());
//			SmsRtMessage smsRtMessage = MessageExtUtil.convertMessageExt(SmsRtMessage.class, message);

            if (null == smsRtMessage) {
                return Action.CommitMessage;
            }

            String userId = smsRtMessage.getSmsMt().getUserid();//获取用户ID
            if (map.containsKey(userId)) {
                map.get(userId).add(smsRtMessage);
            } else {
                List<SmsRtMessage> list = Lists.newArrayList();
                list.add(smsRtMessage);
                map.put(userId, list);
            }


            if (map != null && map.size() > 0) {
                for (String userId1 : map.keySet()) {
                    boolean flag = "0".equals(AccountCacheUtils.getStringValue(userId1, "payMode", "")) ? true : false;
                    //String url = user.getCallbackUrl();
                    String url = AccountCacheUtils.getStringValue(userId1, "callbackUrl", "");
                    if (!StringUtils.startsWith(url.toLowerCase(), "http")) {
                        continue;
                    }
                    List<SmsRtMessage> list = map.get(userId1);
                    String param = null;
                    String result = null;

                    //响应内容类型 0：xml 1：json
                    //if (user.getRspContentType() == 1)
                    if (AccountCacheUtils.getIntegerValue(userId1, "rspContentType", 0) == 1) {
                        param = jsonResult(list, flag);//扣费方式 0:提交扣费 2:状态报告
                        try {
                            result = HttpRequest.sendPostJson(url, param, null, 3000);//推送
                        } catch (Exception e) {
                        }
                    } else {
                        try {
                            param = result(list, flag);//扣费方式 0:提交扣费 2:状态报告
                            result = HttpRequest.sendTextPost(url, param, null, "UTF-8", 3000);//推送
                        } catch (Exception e) {
                        }
                    }

                    logger.info("用户推送:URL:{},参数:{},响应:{}", url, param, result);
                    if (StringUtils.isNotBlank(result) && result.length() > 100) {
                        result = result.substring(0, 100);
                    }
                    //设置推送状态
                    for (SmsRtMessage smsRtMessage1 : list) {
                        Map<String, String> pMap = Maps.newHashMap();
                        pMap.put("bizid", smsRtMessage1.getSmsMt().getId());
                        pMap.put("taskid", smsRtMessage1.getSmsMt().getTaskid());
                        pMap.put("result", result);
                        pMap.put("userId", smsRtMessage1.getSmsMt().getUserid());
                        pMap.put("pushType", "0");
                        pMap.put("tableName", "jmsg_sms_push_" + TableNameUtil.getTableIndex(smsRtMessage1.getSmsMt().getId()));
                        sqlSession.insert("com.sanerzone.jmsg.dao.JmsgSmsSendDao.batchInsertPush", pMap);
                    }
                }
                sqlSession.commit();
            }
        } catch (Exception e) {
            logger.error("{}", e);
        } finally {
            if (sqlSession != null) {
                sqlSession.close();
            }
        }
        return Action.CommitMessage;
    }
//	@Override
//	public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
//
//		Map<String,List<SmsRtMessage>> map = Maps.newHashMap();
//		SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH,false);
//		try {
//			for (MessageExt msg : msgs) {
//				SmsRtMessage smsRtMessage = MessageExtUtil.convertMessageExt(SmsRtMessage.class, msg);
//
//				if (null == smsRtMessage) {
//					continue;
//				}
//
//				String userId = smsRtMessage.getSmsMt().getUserid();//获取用户ID
//				if(map.containsKey(userId)){
//					map.get(userId).add(smsRtMessage);
//				}else{
//					List<SmsRtMessage> list = Lists.newArrayList();
//					list.add(smsRtMessage);
//					map.put(userId, list);
//				}
//			}
//
//			if(map != null && map.size() >0){
//				for(String userId:map.keySet()){
//					boolean flag = "0".equals(AccountCacheUtils.getStringValue(userId, "payMode", ""))?true:false;
//					//String url = user.getCallbackUrl();
//					String url = AccountCacheUtils.getStringValue(userId, "callbackUrl", "");
//					if(!StringUtils.startsWith(url.toLowerCase(), "http")) {
//						continue;
//					}
//					List<SmsRtMessage> list = map.get(userId);
//					String param = null;
//					String result = null;
//
//					//响应内容类型 0：xml 1：json
//					//if (user.getRspContentType() == 1)
//					if (AccountCacheUtils.getIntegerValue(userId, "rspContentType", 0) == 1)
//					{
//					    param = jsonResult(list,flag);//扣费方式 0:提交扣费 2:状态报告
//					    try {
//                        result = HttpRequest.sendPostJson(url, param, null, 3000);//推送
//					    }catch(Exception e) {
//						}
//					}
//					else
//					{
//						try {
//					    param = result(list,flag);//扣费方式 0:提交扣费 2:状态报告
//	                    result = HttpRequest.sendTextPost(url, param, null, "UTF-8", 3000);//推送
//						}catch(Exception e) {
//						}
//					}
//
//					logger.info("用户推送:URL:{},参数:{},响应:{}",url,param, result);
//					if(StringUtils.isNotBlank(result) && result.length() >100){
//						result = result.substring(0,100);
//					}
//					//设置推送状态
//					for (SmsRtMessage smsRtMessage : list) {
//						Map<String,String> pMap = Maps.newHashMap();
//						pMap.put("bizid", smsRtMessage.getSmsMt().getId());
//						pMap.put("taskid",smsRtMessage.getSmsMt().getTaskid());
//						pMap.put("result",result);
//						pMap.put("userId", smsRtMessage.getSmsMt().getUserid());
//						pMap.put("pushType", "0");
//						pMap.put("tableName", "jmsg_sms_push_" + TableNameUtil.getTableIndex(smsRtMessage.getSmsMt().getId()));
//						sqlSession.insert("com.sanerzone.jmsg.dao.JmsgSmsSendDao.batchInsertPush", pMap);
//					}
//				}
//			}
//			sqlSession.commit();
//		}catch(Exception e){
//			logger.error("{}", e);
//		} finally{
//			if(sqlSession != null){
//				sqlSession.close();
//			}
//		}
//		return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
//	}

    private String result(List<SmsRtMessage> list, boolean flag) {
        Map<String, List<SmsRtMessage>> map = Maps.newHashMap();
        for (SmsRtMessage smsRtMessage : list) {
            String taskid = smsRtMessage.getSmsMt().getTaskid();
            if (map.containsKey(taskid)) {
                map.get(taskid).add(smsRtMessage);
            } else {
                List<SmsRtMessage> msgList = Lists.newArrayList();
                msgList.add(smsRtMessage);
                map.put(taskid, msgList);
            }
        }


        StringBuffer sb = new StringBuffer("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        sb.append("<result statustext=\"" + "提交成功" + "\" status=\"" + 0 + "\">");
        for (String taskid : map.keySet()) {
            sb.append("<taskid>").append(taskid).append("</taskid>").append("<list>");
            List<SmsRtMessage> resultList = map.get(taskid);
            for (SmsRtMessage entity : resultList) {
                String status = entity.getStat();
                if (flag) {//提交扣费
                    if (!status.startsWith("F")) {
                        status = "DELIVRD";
                    }
                }

                sb.append("<item>")
                        .append("<phone>").append(entity.getDestTermID()).append("</phone>")
//				.append("<reporttime>").append(formatDateStr(entity.getDoneTime())).append("</reporttime>")
                        .append("<reporttime>").append(DateUtils.getDate("yyyyMMddHHmmss")).append("</reporttime>")
                        .append("<status>").append(status).append("</status>")
                        .append("</item>");
            }
            sb.append("</list>");
        }
        sb.append("</result>");

        return sb.toString();
    }

    private String jsonResult(List<SmsRtMessage> list, boolean flag) {
        Map<String, List<SmsRtMessage>> map = Maps.newHashMap();
        for (SmsRtMessage smsRtMessage : list) {
            String taskid = smsRtMessage.getSmsMt().getTaskid();
            if (map.containsKey(taskid)) {
                map.get(taskid).add(smsRtMessage);
            } else {
                List<SmsRtMessage> msgList = Lists.newArrayList();
                msgList.add(smsRtMessage);
                map.put(taskid, msgList);
            }
        }

        List<Map<String, String>> rList = new ArrayList<Map<String, String>>();
        Map<String, String> resultMap = new HashMap<String, String>();

        for (String taskid : map.keySet()) {
            List<SmsRtMessage> resultList = map.get(taskid);
            for (SmsRtMessage entity : resultList) {
                String status = entity.getStat();
                if (flag) {//提交扣费
                    if (!status.startsWith("F")) {
                        status = "DELIVRD";
                    }
                }

                resultMap = new HashMap<String, String>();
                resultMap.put("taskid", taskid);
                resultMap.put("code", status);
                resultMap.put("msg", "提交成功");
                resultMap.put("mobile", entity.getDestTermID());
                resultMap.put("messageid", entity.getSmsMt().getCstmOrderID());
                resultMap.put("time", DateUtils.getDate("yyyyMMddHHmmss"));

                rList.add(resultMap);
            }
        }

        return JSON.toJSONString(rList);
    }

    private String formatDateStr(String dateStr) {
        if (StringUtils.isBlank(dateStr)) {
            return dateStr;
        }

        SimpleDateFormat sdf10 = new SimpleDateFormat("yyMMddHHmm");
        SimpleDateFormat sdf14 = new SimpleDateFormat("yyyyMMddHHmmss");
        //SimpleDateFormat sdfs = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            if (dateStr.length() == 10) {
                date = sdf10.parse(dateStr);
            } else {
                //date = sdf14.parse(dateStr);
                return dateStr;
            }

            return sdf14.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return dateStr;
    }


}
