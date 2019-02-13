package com.sanerzone.jmsg.listener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;
import com.sanerzone.common.support.utils.*;
import com.siloyou.jmsg.common.message.SmsMtMessage;
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
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.sanerzone.common.modules.account.utils.AccountCacheUtils;
import com.sanerzone.common.modules.phone.utils.PhoneUtils;
import com.sanerzone.common.modules.smscenter.entity.JmsgGatewayInfo;
import com.sanerzone.common.modules.smscenter.utils.GatewayUtils;
import com.sanerzone.jmsg.entity.JmsgSmsDeliver;
import com.sanerzone.jmsg.util.MessageExtUtil;
import com.siloyou.jmsg.common.message.SmsMoMessage;

@Service
public class SmsDeliverMsgListener implements /*MessageListenerConcurrently*/ MessageListener {
    private static Logger logger = LoggerFactory.getLogger(SmsDeliverMsgListener.class);

    @Autowired
    SqlSessionFactory sqlSessionFactory;


    @Override
    public Action consume(Message message, ConsumeContext consumeContext) {
        List<JmsgSmsDeliver> list = Lists.newArrayList();
        Calendar current = Calendar.getInstance();
        SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH, false);
        try {
            logger.info("上行接收:msgid:{}, key:{}", message.getMsgID(), message.getKey());
            SmsMoMessage smsMoMessage = (SmsMoMessage) FstObjectSerializeUtil.read(message.getBody());
//            SmsMoMessage smsMoMessage = MessageExtUtil.convertMessageExt(SmsMoMessage.class, msg);
            if (null == smsMoMessage) {
                return Action.CommitMessage;
            }

            smsMoMessage.setUuid(IdGen.uuid());

            //匹配用户
            String spNumber = "", userId = "";
            Map<String, String> moNumMap = JedisClusterUtils.getJedisInstance().hgetAll("MONUM");
            if (moNumMap != null) {
//					userId = moNumMap.get(smsMoMessage.getSrcTermID());
                userId = moNumMap.get(smsMoMessage.getDestTermID());//JedisClusterUtils.hget("MONUM", smsMoMessage.getSrcTermID());
                if (StringUtils.isBlank(userId)) {
                    //JedisClusterUtils.getJedisInstance().hgetAll("MONUM");
                    for (Entry<String, String> entry : moNumMap.entrySet()) {
                        String key = entry.getKey();
                        if (smsMoMessage.getSrcTermID().startsWith(key)) {
                            if (key.length() > spNumber.length()) {
                                spNumber = key;
                                userId = entry.getValue();
                            }
                        }
                    }
                }
            }

            // 判断是否加入营销黑名单
            if (StringUtils.isNotBlank(userId)) {
                String upUrl = AccountCacheUtils.getStringValue(userId, "upUrl", "");
                int rspContentType = AccountCacheUtils.getIntegerValue(userId, "rspContentType", 0);
                JmsgSmsDeliver deliver = new JmsgSmsDeliver();
                deliver.setMoMsg(smsMoMessage);
                deliver.setUserId(userId);
                deliver.setUpUrl(upUrl);
                deliver.setRspContentType(rspContentType);
                list.add(deliver);
            }

            // 插入数据库
            sqlSession
                    .insert("com.sanerzone.jmsg.dao.JmsgSmsDeliverDao.batchInsert",
                            smsMoMessage);
            sqlSession.commit();
            logger.info("上行接收处理完成!");

            int i = 0;
            if (list != null && list.size() > 0) {
                i = 0;
                for (JmsgSmsDeliver jmsgSmsDeliver : list) {


                    String param = null;
                    String pushFlag = "1";
                    String result = "";
                    String upUrl = jmsgSmsDeliver.getUpUrl();

                    if (StringUtils.isBlank(upUrl)) {
                        pushFlag = "0";
                    } else {
                        String userId1 = jmsgSmsDeliver.getUserId();
                        int allnumPush = AccountCacheUtils.getIntegerValue(userId1, "allnumPush", 1);
                        if (allnumPush == 0) {//不是全号推送
                            String srcTermID = jmsgSmsDeliver.getMoMsg().getSrcTermID();//上行号码

                            //获取上行号码
                            int signFlag = AccountCacheUtils.getIntegerValue(userId1, "signFlag", 1);
                            if (1 == signFlag) {//校验签名
                                int substringLength = AccountCacheUtils.getIntegerValue(userId1, "substringLength", 0);//截取长度
                                int maxLength = srcTermID.length();
                                if (maxLength > substringLength) {
                                    srcTermID = srcTermID.substring(substringLength, srcTermID.length());
                                    jmsgSmsDeliver.getMoMsg().setSrcTermID(srcTermID);
                                } else {
                                    logger.info("用户上行推送错误,校验签名,截取长度大于等于上行号码长度");
                                }
                            } else {//不校验签名
                                JmsgGatewayInfo gatewayInfo = GatewayUtils.getGatewayInfo(jmsgSmsDeliver.getMoMsg().getGateWayID());
                                if (gatewayInfo != null) {
                                    String spNumber1 = gatewayInfo.getSpNumber();
                                    String phoneType = PhoneUtils.getPhoneType(jmsgSmsDeliver.getMoMsg().getDestTermID());
                                    String extnumKey = "";
                                    if ("LT".equals(phoneType)) {
                                        extnumKey = "extnumLt";
                                    } else if ("YD".equals(phoneType)) {
                                        extnumKey = "extnumYd";
                                    } else if ("DX".equals(phoneType)) {
                                        extnumKey = "extnumDx";
                                    }
                                    String extnum = AccountCacheUtils.getStringValue(userId1, extnumKey, "");
                                    srcTermID = srcTermID.replaceFirst(spNumber1 + extnum, "");
                                    jmsgSmsDeliver.getMoMsg().setSrcTermID(srcTermID);
                                } else {
                                    logger.info("用户上行推送错误,不校验签名,获取通道接入号错误");
                                }
                            }
                        }


                        // 响应内容类型 0：xml 1：json
                        if (jmsgSmsDeliver.getRspContentType() == 1) {
                            param = jsonResult(jmsgSmsDeliver.getMoMsg());
                            result = HttpRequest.sendPostJson(upUrl, param,
                                    null, 3000);// 推送
                        } else {
                            param = result(jmsgSmsDeliver.getMoMsg());
                            result = HttpRequest.sendTextPost(upUrl, param,
                                    null, "UTF-8", 3000);// 推送
                        }

                        if (StringUtils.isNotBlank(result)
                                && result.length() > 100) {
                            result = result.substring(0, 100);
                        }
                        logger.info("用户上行推送:URL:{},参数:{}", upUrl, param);
                    }
                    if (StringUtils.isNotBlank(result) && result.length() > 100) {
                        result = result.substring(0, 100);
                    }
                    jmsgSmsDeliver.setResult(result);
                    jmsgSmsDeliver.setPushFlag(pushFlag);

                    sqlSession
                            .insert("com.sanerzone.jmsg.dao.JmsgSmsDeliverDao.batchInsertPush",
                                    jmsgSmsDeliver);

                    i++;
                    if (i % 200 == 0) {
                        sqlSession.commit();
                    }
                }
                sqlSession.commit();
            }
        } catch (Exception e) {
            logger.error("接收上行异常: {}", e);
        } finally {
            if (sqlSession != null) {
                sqlSession.close();
            }
        }
        return Action.CommitMessage;
    }

//    @Override
//    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs,
//                                                    ConsumeConcurrentlyContext context) {
//        List<JmsgSmsDeliver> list = Lists.newArrayList();
//        Calendar current = Calendar.getInstance();
//        SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH, false);
//        try {
//            for (MessageExt msg : msgs) {
//                logger.info("上行接收:msgid:{}, key:{}", msg.getMsgId(), msg.getKeys());
//                SmsMoMessage smsMoMessage = MessageExtUtil.convertMessageExt(SmsMoMessage.class, msg);
//                if (null == smsMoMessage) {
//                    continue;
//                }
//
//                smsMoMessage.setUuid(IdGen.uuid());
//
//                //匹配用户
//                String spNumber = "", userId = "";
//                Map<String, String> moNumMap = JedisClusterUtils.getJedisInstance().hgetAll("MONUM");
//                if (moNumMap != null) {
////					userId = moNumMap.get(smsMoMessage.getSrcTermID());
//                    userId = moNumMap.get(smsMoMessage.getDestTermID());//JedisClusterUtils.hget("MONUM", smsMoMessage.getSrcTermID());
//                    if (StringUtils.isBlank(userId)) {
//                        //JedisClusterUtils.getJedisInstance().hgetAll("MONUM");
//                        for (Entry<String, String> entry : moNumMap.entrySet()) {
//                            String key = entry.getKey();
//                            if (smsMoMessage.getSrcTermID().startsWith(key)) {
//                                if (key.length() > spNumber.length()) {
//                                    spNumber = key;
//                                    userId = entry.getValue();
//                                }
//                            }
//                        }
//                    }
//                }
//
//                // 判断是否加入营销黑名单
//                if (StringUtils.isNotBlank(userId)) {
//                    String upUrl = AccountCacheUtils.getStringValue(userId, "upUrl", "");
//                    int rspContentType = AccountCacheUtils.getIntegerValue(userId, "rspContentType", 0);
//                    JmsgSmsDeliver deliver = new JmsgSmsDeliver();
//                    deliver.setMoMsg(smsMoMessage);
//                    deliver.setUserId(userId);
//                    deliver.setUpUrl(upUrl);
//                    deliver.setRspContentType(rspContentType);
//                    list.add(deliver);
//                }
//
//                // 插入数据库
//                sqlSession
//                        .insert("com.sanerzone.jmsg.dao.JmsgSmsDeliverDao.batchInsert",
//                                smsMoMessage);
//            }
//            sqlSession.commit();
//            logger.info("上行接收处理完成!");
//
//            int i = 0;
//            if (list != null && list.size() > 0) {
//                i = 0;
//                for (JmsgSmsDeliver jmsgSmsDeliver : list) {
//
//
//                    String param = null;
//                    String pushFlag = "1";
//                    String result = "";
//                    String upUrl = jmsgSmsDeliver.getUpUrl();
//
//                    if (StringUtils.isBlank(upUrl)) {
//                        pushFlag = "0";
//                    } else {
//                        String userId = jmsgSmsDeliver.getUserId();
//                        int allnumPush = AccountCacheUtils.getIntegerValue(userId, "allnumPush", 1);
//                        if (allnumPush == 0) {//不是全号推送
//                            String srcTermID = jmsgSmsDeliver.getMoMsg().getSrcTermID();//上行号码
//
//                            //获取上行号码
//                            int signFlag = AccountCacheUtils.getIntegerValue(userId, "signFlag", 1);
//                            if (1 == signFlag) {//校验签名
//                                int substringLength = AccountCacheUtils.getIntegerValue(userId, "substringLength", 0);//截取长度
//                                int maxLength = srcTermID.length();
//                                if (maxLength > substringLength) {
//                                    srcTermID = srcTermID.substring(substringLength, srcTermID.length());
//                                    jmsgSmsDeliver.getMoMsg().setSrcTermID(srcTermID);
//                                } else {
//                                    logger.info("用户上行推送错误,校验签名,截取长度大于等于上行号码长度");
//                                }
//                            } else {//不校验签名
//                                JmsgGatewayInfo gatewayInfo = GatewayUtils.getGatewayInfo(jmsgSmsDeliver.getMoMsg().getGateWayID());
//                                if (gatewayInfo != null) {
//                                    String spNumber = gatewayInfo.getSpNumber();
//                                    String phoneType = PhoneUtils.getPhoneType(jmsgSmsDeliver.getMoMsg().getDestTermID());
//                                    String extnumKey = "";
//                                    if ("LT".equals(phoneType)) {
//                                        extnumKey = "extnumLt";
//                                    } else if ("YD".equals(phoneType)) {
//                                        extnumKey = "extnumYd";
//                                    } else if ("DX".equals(phoneType)) {
//                                        extnumKey = "extnumDx";
//                                    }
//                                    String extnum = AccountCacheUtils.getStringValue(userId, extnumKey, "");
//                                    srcTermID = srcTermID.replaceFirst(spNumber + extnum, "");
//                                    jmsgSmsDeliver.getMoMsg().setSrcTermID(srcTermID);
//                                } else {
//                                    logger.info("用户上行推送错误,不校验签名,获取通道接入号错误");
//                                }
//                            }
//                        }
//
//
//                        // 响应内容类型 0：xml 1：json
//                        if (jmsgSmsDeliver.getRspContentType() == 1) {
//                            param = jsonResult(jmsgSmsDeliver.getMoMsg());
//                            result = HttpRequest.sendPostJson(upUrl, param,
//                                    null, 3000);// 推送
//                        } else {
//                            param = result(jmsgSmsDeliver.getMoMsg());
//                            result = HttpRequest.sendTextPost(upUrl, param,
//                                    null, "UTF-8", 3000);// 推送
//                        }
//
//                        if (StringUtils.isNotBlank(result)
//                                && result.length() > 100) {
//                            result = result.substring(0, 100);
//                        }
//                        logger.info("用户上行推送:URL:{},参数:{}", upUrl, param);
//                    }
//                    if (StringUtils.isNotBlank(result) && result.length() > 100) {
//                        result = result.substring(0, 100);
//                    }
//                    jmsgSmsDeliver.setResult(result);
//                    jmsgSmsDeliver.setPushFlag(pushFlag);
//
//                    sqlSession
//                            .insert("com.sanerzone.jmsg.dao.JmsgSmsDeliverDao.batchInsertPush",
//                                    jmsgSmsDeliver);
//
//                    i++;
//                    if (i % 200 == 0) {
//                        sqlSession.commit();
//                    }
//                }
//                sqlSession.commit();
//            }
//        } catch (Exception e) {
//            logger.error("接收上行异常: {}", e);
//        } finally {
//            if (sqlSession != null) {
//                sqlSession.close();
//            }
//        }
//        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
//    }

    private String result(SmsMoMessage smsMoMessage) {

        StringBuffer sb = new StringBuffer(
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        sb.append("<result statustext=\"" + "提交成功" + "\" status=\"" + 0 + "\">");
        sb.append("<list>").append("<item>");
        sb.append("<id>").append(smsMoMessage.getUuid()).append("</id>");
        sb.append("<phone>").append(smsMoMessage.getDestTermID())
                .append("</phone>");
        sb.append("<recvnumber>").append(smsMoMessage.getSrcTermID())
                .append("</recvnumber>");
        sb.append("<smscontent>").append(smsMoMessage.getMsgContent())
                .append("</smscontent>");
        sb.append("<recvtime>")
                .append(DateUtils.formatDate(new Date(), "yyyyMMddHHmmss"))
                .append("</recvtime>");
        sb.append("</item>").append("</list>").append("</result>");
        return sb.toString();
    }

    private String jsonResult(SmsMoMessage smsMoMessage) {
        List<Map<String, String>> rList = new ArrayList<Map<String, String>>();
        Map<String, String> resultMap = new HashMap<String, String>();
        resultMap.put("id", smsMoMessage.getUuid());
        resultMap.put("mobile", smsMoMessage.getDestTermID());
        resultMap.put("srcid", smsMoMessage.getSrcTermID());
        resultMap.put("msgcontent", smsMoMessage.getMsgContent());
        resultMap.put("time",
                DateUtils.formatDate(new Date(), "yyyyMMddHHmmss"));

        rList.add(resultMap);

        return JSON.toJSONString(rList);
    }


}
