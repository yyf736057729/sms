package com.sanerzone.jmsg.listener;

import java.util.List;
import java.util.Map;

import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;
import com.sanerzone.common.support.utils.FstObjectSerializeUtil;
import com.siloyou.jmsg.common.message.SmsMtMessage;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.google.common.collect.Maps;
import com.sanerzone.common.modules.TableNameUtil;
import com.sanerzone.jmsg.util.MessageExtUtil;
import com.siloyou.jmsg.common.message.SmsPrMessage;

@Service
//public class SmsPushResultListener implements MessageListenerConcurrently{
public class SmsPushResultListener implements MessageListener {
    private Logger logger = LoggerFactory.getLogger(SmsPushResultListener.class);

    @Autowired
    SqlSessionFactory sqlSessionFactory;

    @Override
    public Action consume(Message message, ConsumeContext consumeContext) {
        SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH, false);
        Map<String, Object> map;
        Map<String, String> sendMap;
        try {
//            SmsPrMessage smsPrMessage = MessageExtUtil.convertMessageExt(SmsPrMessage.class, msg);
            SmsPrMessage smsPrMessage = (SmsPrMessage) FstObjectSerializeUtil.read(message.getBody());
            if (null == smsPrMessage) {
                return Action.CommitMessage;
            }
            map = Maps.newHashMap();
            sendMap = Maps.newHashMap();
            String bizid = smsPrMessage.getBizid();
            String tableIndex = TableNameUtil.getTableIndex(bizid);
            String sendTable = "jmsg_sms_send_" + tableIndex;
            String pushTable = "jmsg_sms_push_" + tableIndex;
            String pushFlag = "0".equals(smsPrMessage.getResult()) ? "1" : "2";
            sendMap.put("pushFlag", pushFlag);
            sendMap.put("id", smsPrMessage.getBizid());
            sendMap.put("tableName", sendTable);
            map.put("smsPrMessage", smsPrMessage);
            map.put("tableName", pushTable);
            sqlSession.insert("com.sanerzone.jmsg.dao.JmsgSmsSendDao.updatePushFlag", sendMap);
            sqlSession.insert("com.sanerzone.jmsg.dao.JmsgSmsPushDao.batchInsert", map);
            sqlSession.commit();
        } catch (Exception e) {
            logger.error("{}", e);
        } finally {
            if (sqlSession != null) {
                sqlSession.close();
            }
        }
        return Action.CommitMessage;
    }

//    @Override
//    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
//
//        SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH, false);
//        Map<String, Object> map;
//        Map<String, String> sendMap;
//        try {
//            for (MessageExt msg : msgs) {
//                SmsPrMessage smsPrMessage = MessageExtUtil.convertMessageExt(SmsPrMessage.class, msg);
//
//                if (null == smsPrMessage) {
//                    continue;
//                }
//                map = Maps.newHashMap();
//                sendMap = Maps.newHashMap();
//                String bizid = smsPrMessage.getBizid();
//                String tableIndex = TableNameUtil.getTableIndex(bizid);
//                String sendTable = "jmsg_sms_send_" + tableIndex;
//                String pushTable = "jmsg_sms_push_" + tableIndex;
//                String pushFlag = "0".equals(smsPrMessage.getResult()) ? "1" : "2";
//                sendMap.put("pushFlag", pushFlag);
//                sendMap.put("id", smsPrMessage.getBizid());
//                sendMap.put("tableName", sendTable);
//                map.put("smsPrMessage", smsPrMessage);
//                map.put("tableName", pushTable);
//                sqlSession.insert("com.sanerzone.jmsg.dao.JmsgSmsSendDao.updatePushFlag", sendMap);
//                sqlSession.insert("com.sanerzone.jmsg.dao.JmsgSmsPushDao.batchInsert", map);
//
//            }
//            sqlSession.commit();
//        } catch (Exception e) {
//            logger.error("{}", e);
//        } finally {
//            if (sqlSession != null) {
//                sqlSession.close();
//            }
//        }

//        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
//    }


}
