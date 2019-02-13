package com.sanerzone.jmsg.listener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;
import com.sanerzone.common.support.utils.DateUtils;
import com.sanerzone.jmsg.entity.JmsgSmsSend;
import com.siloyou.jmsg.common.message.SmsMtMessage;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.ResultHandler;
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
import com.sanerzone.common.modules.TableNameUtil;
import com.sanerzone.common.modules.smscenter.utils.JmsgCacheKeys;
import com.sanerzone.common.support.utils.FstObjectSerializeUtil;
import com.sanerzone.jmsg.util.MessageExtUtil;
import com.sanerzone.smscenter.utils.MQUtils;
import com.siloyou.jmsg.common.message.SmsRtMessage;

@Service
public class SmsReportMsgListener implements /*MessageListenerConcurrently*/ MessageListener {

	public static Logger logger = LoggerFactory.getLogger(SmsReportMsgListener.class);
	
	@Autowired
	private MQUtils mQUtils;
	
	@Autowired
	SqlSessionFactory sqlSessionFactory;
	@Override
	public Action consume(Message message, ConsumeContext consumeContext) {
		SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH,false);
		SmsRtMessage smsRtMessage;
		try{
			Map<String, Object> map = null;
			smsRtMessage = (SmsRtMessage)FstObjectSerializeUtil.read(message.getBody());
//			smsRtMessage = MessageExtUtil.convertMessageExt(SmsRtMessage.class, msg);
			if(smsRtMessage != null) {
				map = new HashMap<String, Object>();
				map.put("data", smsRtMessage);

//					if(smsRtMessage.getSmsMt() == null){//retry
//						sqlSession.insert("com.sanerzone.jmsg.dao.JmsgSmsReportRetryDao.batchInsert", map);
//					}else{//report
//						if("9".equals(smsRtMessage.getSmsMt().getUserReportNotify())){
////							mQUtils.sendSmsMT(JmsgCacheKeys.getPushTopic(), smsRtMessage.getSmsMt().getUserReportGateWayID(), smsRtMessage.getSmsMt().getId(),FstObjectSerializeUtil.write(smsRtMessage));
//							mQUtils.sendSmsMT(JmsgCacheKeys.getPushTopic(), smsRtMessage.getSmsMt().getUserReportGateWayID(),
//										smsRtMessage.getSmsMt().getId(),FstObjectSerializeUtil.write(smsRtMessage));
//						}
//						map.put("tableName", "jmsg_sms_report_" + TableNameUtil.getTableIndex(smsRtMessage.getSmsMt().getId()));
//						sqlSession.insert("com.sanerzone.jmsg.dao.JmsgSmsReportDao.batchInsert", map);
//					}
				if(smsRtMessage.getSmsMt() == null){
					SmsMtMessage smsMtMessage = new SmsMtMessage();
//						msg.getMsgId();
//						smsRtMessage.getDestTermID();
					map.put("tableName", "jmsg_sms_send_" + TableNameUtil.getTableIndex(smsRtMessage.getMsgid()));
					Map<String,String>map1 = new HashMap<>();
					JmsgSmsSend jmsgSmsSend =sqlSession.selectOne("com.sanerzone.jmsg.dao.JmsgSmsSendDao.findByPhone",map);
					smsMtMessage.setTaskid(jmsgSmsSend.getTaskId());
					smsMtMessage.setCstmOrderID(jmsgSmsSend.getCustomerOrderId());
					smsMtMessage.setUserid(jmsgSmsSend.getUserId());
					smsMtMessage.setId(jmsgSmsSend.getId());
					smsMtMessage.setUserReportGateWayID(jmsgSmsSend.getReportGatewayId());
					smsRtMessage.setSmsMt(smsMtMessage);
				}

				//更新send表，将网关返回信息记录
				Map<String, Object> updateMap = new HashMap<String, Object>();
				updateMap.put("tableName","jmsg_sms_send_" + TableNameUtil.getTableIndex(smsRtMessage.getSmsMt().getId()));
				updateMap.put("submitTime",smsRtMessage.getSubmitTime());
				updateMap.put("id",smsRtMessage.getSmsMt().getId());
				sqlSession.update("com.sanerzone.jmsg.dao.JmsgSmsSendDao.updateSendRecord",updateMap);


				mQUtils.sendSmsMT(JmsgCacheKeys.getPushTopic(), smsRtMessage.getSmsMt().getUserReportGateWayID(),
						smsRtMessage.getSmsMt().getId(),FstObjectSerializeUtil.write(smsRtMessage));
				System.out.println("状态报告回执：sendMQ:topic-"+JmsgCacheKeys.getPushTopic()+"---tag:"+smsRtMessage.getSmsMt().getUserReportGateWayID());
				map.put("tableName", "jmsg_sms_report_" + TableNameUtil.getTableIndex(smsRtMessage.getSmsMt().getId()));
				sqlSession.insert("com.sanerzone.jmsg.dao.JmsgSmsReportDao.batchInsert", map);

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
//		SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH,false);
//		SmsRtMessage smsRtMessage;
//		try{
//			Map<String, Object> map = null;
//			for (MessageExt msg : msgs) {
//				//SmsRtMessage smsRtMessage = (SmsRtMessage)FstObjectSerializeUtil.read(msg.getBody());
//				smsRtMessage = MessageExtUtil.convertMessageExt(SmsRtMessage.class, msg);
//				if(smsRtMessage != null) {
//					map = new HashMap<String, Object>();
//					map.put("data", smsRtMessage);
//
////					if(smsRtMessage.getSmsMt() == null){//retry
////						sqlSession.insert("com.sanerzone.jmsg.dao.JmsgSmsReportRetryDao.batchInsert", map);
////					}else{//report
////						if("9".equals(smsRtMessage.getSmsMt().getUserReportNotify())){
//////							mQUtils.sendSmsMT(JmsgCacheKeys.getPushTopic(), smsRtMessage.getSmsMt().getUserReportGateWayID(), smsRtMessage.getSmsMt().getId(),FstObjectSerializeUtil.write(smsRtMessage));
////							mQUtils.sendSmsMT(JmsgCacheKeys.getPushTopic(), smsRtMessage.getSmsMt().getUserReportGateWayID(),
////										smsRtMessage.getSmsMt().getId(),FstObjectSerializeUtil.write(smsRtMessage));
////						}
////						map.put("tableName", "jmsg_sms_report_" + TableNameUtil.getTableIndex(smsRtMessage.getSmsMt().getId()));
////						sqlSession.insert("com.sanerzone.jmsg.dao.JmsgSmsReportDao.batchInsert", map);
////					}
//					if(smsRtMessage.getSmsMt() == null){
//						SmsMtMessage smsMtMessage = new SmsMtMessage();
////						msg.getMsgId();
////						smsRtMessage.getDestTermID();
//						map.put("tableName", "jmsg_sms_send_" + TableNameUtil.getTableIndex(smsRtMessage.getMsgid()));
//						Map<String,String>map1 = new HashMap<>();
//						JmsgSmsSend jmsgSmsSend =sqlSession.selectOne("com.sanerzone.jmsg.dao.JmsgSmsSendDao.findByPhone",map);
//						smsMtMessage.setTaskid(jmsgSmsSend.getTaskId());
//						smsMtMessage.setCstmOrderID(jmsgSmsSend.getCustomerOrderId());
//						smsMtMessage.setUserid(jmsgSmsSend.getUserId());
//						smsMtMessage.setId(jmsgSmsSend.getId());
//						smsMtMessage.setUserReportGateWayID(jmsgSmsSend.getReportGatewayId());
//						smsRtMessage.setSmsMt(smsMtMessage);
//					}
//
//					//更新send表，将网关返回信息记录
//					Map<String, Object> updateMap = new HashMap<String, Object>();
//					updateMap.put("tableName","jmsg_sms_send_" + TableNameUtil.getTableIndex(smsRtMessage.getSmsMt().getId()));
//					updateMap.put("submitTime",smsRtMessage.getSubmitTime());
//					updateMap.put("id",smsRtMessage.getSmsMt().getId());
//					sqlSession.update("com.sanerzone.jmsg.dao.JmsgSmsSendDao.updateSendRecord",updateMap);
//
//
//					mQUtils.sendSmsMT(JmsgCacheKeys.getPushTopic(), smsRtMessage.getSmsMt().getUserReportGateWayID(),
//							smsRtMessage.getSmsMt().getId(),FstObjectSerializeUtil.write(smsRtMessage));
//					System.out.println("状态报告回执：sendMQ:topic-"+JmsgCacheKeys.getPushTopic()+"---tag:"+smsRtMessage.getSmsMt().getUserReportGateWayID());
//					map.put("tableName", "jmsg_sms_report_" + TableNameUtil.getTableIndex(smsRtMessage.getSmsMt().getId()));
//					sqlSession.insert("com.sanerzone.jmsg.dao.JmsgSmsReportDao.batchInsert", map);
//
//					}
//
//
//
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
