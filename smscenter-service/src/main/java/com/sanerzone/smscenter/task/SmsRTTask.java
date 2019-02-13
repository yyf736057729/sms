//package com.sanerzone.smscenter.task;
//
//import java.util.Date;
//import java.util.Map;
//
//import org.apache.ibatis.session.ExecutorType;
//import org.apache.ibatis.session.SqlSession;
//import org.apache.ibatis.session.SqlSessionFactory;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.context.annotation.Lazy;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Service;
//
//import com.google.common.collect.Maps;
//import com.sanerzone.common.support.utils.DateUtils;
//import com.sanerzone.common.support.utils.SpringContextHolder;
//import com.sanerzone.smscenter.entity.SMSRTMessage;
//
////@Service
////@Lazy(false)
//public class SmsRTTask {
//	public static Logger logger = LoggerFactory.getLogger(SmsRTTask.class);
//	
//	private static SqlSessionFactory sqlSessionFactory = SpringContextHolder.getBean(SqlSessionFactory.class);
//	
////	@Scheduled(cron = "*/10 * * * * ?")
//	public static void runUpdateStatus()
//	{
//		if (!SmsRTQueueFactory.getSmsSendQueue().isEmpty())
//		{
//			SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH, false);
//
//			int num = 0;
//
//			long beginTime = System.currentTimeMillis();
//
//			try {
//				SMSRTMessage smsRTMessage;
//				while (!SmsRTQueueFactory.getSmsSendQueue().isEmpty()) {
//					
//					smsRTMessage = (SMSRTMessage) SmsRTQueueFactory.getSmsSendQueue().take();
//					
//					if(smsRTMessage.getSmsMt() != null)
//					{
//						Map<String,Object> map = Maps.newHashMap();
//						map.put("id", smsRTMessage.getSmsMt().getId());
//						if("DELIVRD".equals(smsRTMessage.getStat())){//成功
//							map.put("reportStatus", "T100");
//						}else{
//							map.put("reportStatus", "F1100");//失败
//						}
//						map.put("submitTime", new Date(smsRTMessage.getSmsMt().getSubmitTime()));
//	                    map.put("reportTime", new Date(smsRTMessage.getSmsMt().getReportTime()));
//	                    
//						int count = sqlSession.update("com.sanerzone.smscenter.modules.smsv2.dao.SmsSendDao.batchUpdateGatewayStatus", map);
//						
//						if (count == 0)
//						{
//							SmsRTQueueFactory.getSmsSendQueue().put(smsRTMessage);
//						}
//						
//						num++;
//					}
//
//					if (num % 500 == 0) {
//						sqlSession.commit();
//					}
//				}
//				sqlSession.commit();
//			} catch (InterruptedException e) {
//				logger.error("入库监控, 发送结果入库异常：{}", e.getMessage());
//			} finally {
//				sqlSession.close();
//			}
//
//			long endTime = System.currentTimeMillis();
//
//			logger.info("入库监控, 发送结果入库条数：{}, 用时:{}, 平均:{}", num,
//					DateUtils.formatDateTime(endTime - beginTime), num / ((endTime - beginTime) / 1000));
//		}
//	}
//}
