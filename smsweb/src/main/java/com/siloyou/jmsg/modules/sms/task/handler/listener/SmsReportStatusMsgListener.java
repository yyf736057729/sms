package com.siloyou.jmsg.modules.sms.task.handler.listener;

import java.util.List;
import java.util.Map;

import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.google.common.collect.Maps;
import com.siloyou.core.common.utils.FstObjectSerializeUtil;
import com.siloyou.core.common.utils.SpringContextHolder;
import com.siloyou.jmsg.common.message.SmsRtMessage;
import com.siloyou.jmsg.modules.sms.entity.JmsgSmsReportRetry;

@Service
public class SmsReportStatusMsgListener implements /*MessageListenerConcurrently*/MessageListener {

	public static Logger logger = LoggerFactory.getLogger(SmsReportStatusMsgListener.class);

	@Override
	public Action consume(Message message, ConsumeContext consumeContext) {
		//		ExecutorType.BATCH,false
		SqlSession sqlSession = SpringContextHolder.getBean(SqlSessionFactory.class).openSession();
		try{
			int index=0;
			SmsRtMessage smsRtMessage = (SmsRtMessage)FstObjectSerializeUtil.read(message.getBody());
			if(smsRtMessage.getSmsMt() != null){
				Map<String,String> map = Maps.newHashMap();
				map.put("id", smsRtMessage.getSmsMt().getId());
				if("DELIVRD".equals(smsRtMessage.getStat())){//成功
					map.put("reportStatus", "T100");
				}else{
					map.put("reportStatus", "F1100");//失败
				}
				int num = sqlSession.update("com.siloyou.jmsg.modules.sms.dao.JmsgSmsSendDao.batchUpdateReprotStatus", map);

				if (num == 0)
				{
					//sqlSession.insert("com.siloyou.jmsg.modules.sms.dao.JmsgSmsReportRetryDao.batchInsert", smsRtMessage);
					JmsgSmsReportRetry jmsgSmsReportRetry = convertJmsgSmsReportRetry(smsRtMessage);
					sqlSession.insert("com.siloyou.jmsg.modules.sms.dao.JmsgSmsReportRetryDao.batchInsertRetry", jmsgSmsReportRetry);
				}

				index++;
			}
//				if(index % 200 ==0){
//					sqlSession.commit();
//				}
		}catch(Exception e){
			logger.error("{}", e);
		}finally{
			if(sqlSession!=null){
				sqlSession.close();
			}
		}
		return Action.CommitMessage;
	}
//	@Override
//	public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
////		ExecutorType.BATCH,false
//		SqlSession sqlSession = SpringContextHolder.getBean(SqlSessionFactory.class).openSession();
//		try{
//			int index=0;
//			for (MessageExt msg : msgs) {
//				SmsRtMessage smsRtMessage = (SmsRtMessage)FstObjectSerializeUtil.read(msg.getBody());
//				if(smsRtMessage.getSmsMt() != null){
//					Map<String,String> map = Maps.newHashMap();
//					map.put("id", smsRtMessage.getSmsMt().getId());
//					if("DELIVRD".equals(smsRtMessage.getStat())){//成功
//						map.put("reportStatus", "T100");
//					}else{
//						map.put("reportStatus", "F1100");//失败
//					}
//					int num = sqlSession.update("com.siloyou.jmsg.modules.sms.dao.JmsgSmsSendDao.batchUpdateReprotStatus", map);
//
//					if (num == 0)
//					{
//					    //sqlSession.insert("com.siloyou.jmsg.modules.sms.dao.JmsgSmsReportRetryDao.batchInsert", smsRtMessage);
//					    JmsgSmsReportRetry jmsgSmsReportRetry = convertJmsgSmsReportRetry(smsRtMessage);
//					    sqlSession.insert("com.siloyou.jmsg.modules.sms.dao.JmsgSmsReportRetryDao.batchInsertRetry", jmsgSmsReportRetry);
//					}
//
//					index++;
//				}
////				if(index % 200 ==0){
////					sqlSession.commit();
////				}
//			}
////			sqlSession.commit();
//
//		}catch(Exception e){
//			logger.error("{}", e);
//		}finally{
//			if(sqlSession!=null){
//				sqlSession.close();
//			}
//		}
//
//		return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
//	}
//
	private JmsgSmsReportRetry convertJmsgSmsReportRetry(SmsRtMessage smsRtMessage)
	{
	    JmsgSmsReportRetry  jmsgSmsReportRetry = new JmsgSmsReportRetry();
	    jmsgSmsReportRetry.setGatewayId(smsRtMessage.getGateWayID());
	    jmsgSmsReportRetry.setMsgid(smsRtMessage.getMsgid());
	    jmsgSmsReportRetry.setStat(smsRtMessage.getStat());
	    jmsgSmsReportRetry.setSubmitTime(Long.parseLong(smsRtMessage.getSubmitTime()));
	    jmsgSmsReportRetry.setDoneTime(Long.parseLong(smsRtMessage.getDoneTime()));
	    jmsgSmsReportRetry.setSrcTermId(smsRtMessage.getSrcTermID());
	    jmsgSmsReportRetry.setDestTermId(smsRtMessage.getDestTermID());
	    jmsgSmsReportRetry.setSmscSequence(smsRtMessage.getSmscSequence());
	    jmsgSmsReportRetry.setSourceFlag(1);
	    
	    return jmsgSmsReportRetry;
	}


}
