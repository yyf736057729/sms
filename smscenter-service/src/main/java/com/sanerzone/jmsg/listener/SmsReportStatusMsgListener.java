package com.sanerzone.jmsg.listener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.MessageListener;
import com.sanerzone.common.support.utils.FstObjectSerializeUtil;
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
import com.siloyou.jmsg.common.message.SmsRtMessage;

@Service
public class SmsReportStatusMsgListener implements /*MessageListenerConcurrently*/MessageListener {

	public static Logger logger = LoggerFactory.getLogger(SmsReportStatusMsgListener.class);
	
	@Autowired
	SqlSessionFactory sqlSessionFactory;

	@Override
	public Action consume(Message message, ConsumeContext consumeContext) {
// 逐个更新，不要求实时
		SqlSession sqlSession = sqlSessionFactory.openSession();
		SmsRtMessage smsRtMessage;
		try{
			smsRtMessage = (SmsRtMessage) FstObjectSerializeUtil.read(message.getBody());
//			smsRtMessage = MessageExtUtil.convertMessageExt(SmsRtMessage.class, msg);

			if( smsRtMessage != null) {
				if(smsRtMessage.getSmsMt() != null){ //只需处理带有MT的消费，没带MT对象的消费会在入库时进入重试表，查找MT后入库
					Map<String,String> map = Maps.newHashMap();
					map.put("id", smsRtMessage.getSmsMt().getId());
					if("DELIVRD".equals(smsRtMessage.getStat())){//成功
						map.put("reportStatus", "T100");
					}else{
						map.put("reportStatus", "F2" + smsRtMessage.getStat());//失败
					}
					map.put("tableName", "jmsg_sms_send_" + TableNameUtil.getTableIndex(smsRtMessage.getSmsMt().getId()));
					int num = sqlSession.update("com.sanerzone.jmsg.dao.JmsgSmsSendDao.batchUpdateReprotStatus", map);

					// 如果num=0,则说明MT还未入库就进行了更新，需要插入重试库，由定时任务扫描入库, 新建重试表，如果超过 48小时未同步，则告警
					if (num == 0)
					{
						Map<String, Object> mapO = new HashMap<String, Object>();
						mapO.put("tableName", "jmsg_sms_report_sync");
						mapO.put("data", smsRtMessage);

						sqlSession.insert("com.sanerzone.jmsg.dao.JmsgSmsReportDao.batchInsert", mapO);
					}

					//					if (num == 0)
					//					{
					//					    //sqlSession.insert("com.sanerzone.smscenter.modules.sms.dao.JmsgSmsReportRetryDao.batchInsert", smsRtMessage);
					//					    JmsgSmsReportRetry jmsgSmsReportRetry = convertJmsgSmsReportRetry(smsRtMessage);
					//					    sqlSession.insert("com.sanerzone.jmsg.dao.JmsgSmsReportRetryDao.batchInsertRetry", jmsgSmsReportRetry);
					//					}

				}
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
//		// 逐个更新，不要求实时
//		SqlSession sqlSession = sqlSessionFactory.openSession();
//		SmsRtMessage smsRtMessage;
//		try{
//			for (MessageExt msg : msgs) {
//				//smsRtMessage = (SmsRtMessage)FstObjectSerializeUtil.read(msg.getBody());
//				smsRtMessage = MessageExtUtil.convertMessageExt(SmsRtMessage.class, msg);
//
//				if( smsRtMessage != null) {
//					if(smsRtMessage.getSmsMt() != null){ //只需处理带有MT的消费，没带MT对象的消费会在入库时进入重试表，查找MT后入库
//						Map<String,String> map = Maps.newHashMap();
//						map.put("id", smsRtMessage.getSmsMt().getId());
//						if("DELIVRD".equals(smsRtMessage.getStat())){//成功
//							map.put("reportStatus", "T100");
//						}else{
//							map.put("reportStatus", "F2" + smsRtMessage.getStat());//失败
//						}
//						map.put("tableName", "jmsg_sms_send_" + TableNameUtil.getTableIndex(smsRtMessage.getSmsMt().getId()));
//						int num = sqlSession.update("com.sanerzone.jmsg.dao.JmsgSmsSendDao.batchUpdateReprotStatus", map);
//
//						// 如果num=0,则说明MT还未入库就进行了更新，需要插入重试库，由定时任务扫描入库, 新建重试表，如果超过 48小时未同步，则告警
//						if (num == 0)
//	                    {
//							Map<String, Object> mapO = new HashMap<String, Object>();
//							mapO.put("tableName", "jmsg_sms_report_sync");
//							mapO.put("data", smsRtMessage);
//
//							sqlSession.insert("com.sanerzone.jmsg.dao.JmsgSmsReportDao.batchInsert", mapO);
//	                    }
//
//	//					if (num == 0)
//	//					{
//	//					    //sqlSession.insert("com.sanerzone.smscenter.modules.sms.dao.JmsgSmsReportRetryDao.batchInsert", smsRtMessage);
//	//					    JmsgSmsReportRetry jmsgSmsReportRetry = convertJmsgSmsReportRetry(smsRtMessage);
//	//					    sqlSession.insert("com.sanerzone.jmsg.dao.JmsgSmsReportRetryDao.batchInsertRetry", jmsgSmsReportRetry);
//	//					}
//
//					}
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

	
	/*private JmsgSmsReportRetry convertJmsgSmsReportRetry(SmsRtMessage smsRtMessage)
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
	}*/

}
