//package com.sanerzone.smscenter.service;
//
//import java.util.Date;
//import java.util.List;
//
//import org.apache.ibatis.session.ExecutorType;
//import org.apache.ibatis.session.SqlSession;
//import org.apache.ibatis.session.SqlSessionFactory;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Service;
//
//import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
//import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
//import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
//import com.alibaba.rocketmq.common.message.MessageExt;
//import com.sanerzone.common.support.utils.FstObjectSerializeUtil;
//import com.sanerzone.common.support.utils.SpringContextHolder;
//import com.sanerzone.smscenter.entity.SMSRTMessage;
//import com.sanerzone.smscenter.modules.smsv2.entity.SmsReport;
//
//@Service
//public class SMSRTV2Listener implements MessageListenerConcurrently {
//
//	public static Logger logger = LoggerFactory
//			.getLogger(SMSRTV2Listener.class);
//
//	@Override
//	public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs,ConsumeConcurrentlyContext context) {
//		SqlSession sqlSession = SpringContextHolder.getBean(SqlSessionFactory.class).openSession(ExecutorType.BATCH, false);
//		try {
//			int index = 0;
//			SMSRTMessage smsRTMessage = null;
//			SmsReport smsReport = null;
//			
//			for (MessageExt msg : msgs) {
//				smsRTMessage = (SMSRTMessage) FstObjectSerializeUtil.read(msg.getBody());
//				
//				smsReport = new SmsReport();
//				smsReport.setMsgid(smsRTMessage.getMsgid());
//				smsReport.setBizid(smsRTMessage.getSmsMt().getId());
//                smsReport.setTaskid(smsRTMessage.getSmsMt().getTaskid());
//                smsReport.setUserid(smsRTMessage.getSmsMt().getUserid());
//                smsReport.setAccId(smsRTMessage.getSmsMt().getAccId());
//                smsReport.setGatewayId(smsRTMessage.getSmsMt().getGatewayId());
//                smsReport.setPhone(smsRTMessage.getSmsMt().getPhone());
//                smsReport.setStat(smsRTMessage.getStat());
//				smsReport.setSpnumber(smsRTMessage.getSpnumber());
//				smsReport.setSubmitTime(Long.parseLong(smsRTMessage.getSubmitTime()));
//				smsReport.setDoneTime(Long.parseLong(smsRTMessage.getDoneTime()));
//				smsReport.setGatewaySequence(smsRTMessage.getSmscSequence());
//				smsReport.setCreatetime(new Date());
//
//				sqlSession.insert("com.sanerzone.smscenter.modules.smsv2.dao.SmsReportDao.insert", smsReport);
//
//				index++;
//				if (index % 200 == 0) {
//					sqlSession.commit();
//				}
//			}
//			sqlSession.commit();
//
//		} catch (Exception e) {
//			logger.error("{}", e);
//		} finally {
//			if (sqlSession != null) {
//				sqlSession.close();
//			}
//		}
//		return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
//	}
//
//}
