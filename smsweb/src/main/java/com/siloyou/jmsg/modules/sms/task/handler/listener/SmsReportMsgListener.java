package com.siloyou.jmsg.modules.sms.task.handler.listener;

import java.util.List;

import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;
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
import com.siloyou.core.common.utils.FstObjectSerializeUtil;
import com.siloyou.core.common.utils.SpringContextHolder;
import com.siloyou.jmsg.common.message.SmsRtMessage;
import com.siloyou.jmsg.common.utils.MQUtils;

@Service
public class SmsReportMsgListener implements /*MessageListenerConcurrently*/ MessageListener {

	public static Logger logger = LoggerFactory.getLogger(SmsReportMsgListener.class);
	
	@Autowired
	private MQUtils mQUtils;
	@Override
	public Action consume(Message message, ConsumeContext consumeContext) {
		SqlSession sqlSession = SpringContextHolder.getBean(SqlSessionFactory.class).openSession(ExecutorType.BATCH,false);
		try{
			int index=0;
			SmsRtMessage smsRtMessage = (SmsRtMessage)FstObjectSerializeUtil.read(message.getBody());

			if(smsRtMessage.getSmsMt() == null){//retry
				sqlSession.insert("com.siloyou.jmsg.modules.sms.dao.JmsgSmsReportRetryDao.batchInsert", smsRtMessage);
			}else{//report
				if("9".equals(smsRtMessage.getSmsMt().getUserReportNotify())){
//						mqUTQueue.put(smsRtMessage);//推送状态
					mQUtils.pushSmsMQ(smsRtMessage.getSmsMt().getId(),smsRtMessage.getSmsMt().getUserReportGateWayID() ,FstObjectSerializeUtil.write(smsRtMessage));//推送短信状态报告
				}
				sqlSession.insert("com.siloyou.jmsg.modules.sms.dao.JmsgSmsReportDao.batchInsert", smsRtMessage);
			}

			index++;
			if(index % 200 ==0){
				sqlSession.commit();
			}
			sqlSession.commit();

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
//		SqlSession sqlSession = SpringContextHolder.getBean(SqlSessionFactory.class).openSession(ExecutorType.BATCH,false);
//		try{
//			int index=0;
//			for (MessageExt msg : msgs) {
//				SmsRtMessage smsRtMessage = (SmsRtMessage)FstObjectSerializeUtil.read(msg.getBody());
//
//				if(smsRtMessage.getSmsMt() == null){//retry
//					sqlSession.insert("com.siloyou.jmsg.modules.sms.dao.JmsgSmsReportRetryDao.batchInsert", smsRtMessage);
//				}else{//report
//					if("9".equals(smsRtMessage.getSmsMt().getUserReportNotify())){
////						mqUTQueue.put(smsRtMessage);//推送状态
//						mQUtils.pushSmsMQ(smsRtMessage.getSmsMt().getId(),smsRtMessage.getSmsMt().getUserReportGateWayID() ,FstObjectSerializeUtil.write(smsRtMessage));//推送短信状态报告
//					}
//					sqlSession.insert("com.siloyou.jmsg.modules.sms.dao.JmsgSmsReportDao.batchInsert", smsRtMessage);
//				}
//
//				index++;
//				if(index % 200 ==0){
//					sqlSession.commit();
//				}
//			}
//			sqlSession.commit();
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


}
