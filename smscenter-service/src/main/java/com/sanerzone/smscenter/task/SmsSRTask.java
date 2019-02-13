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
//import com.sanerzone.smscenter.entity.SMSSRMessage;
//
////@Service
////@Lazy(false)
//public class SmsSRTask {
//	public static Logger logger = LoggerFactory.getLogger(SmsSRTask.class);
//	
//	private static SqlSessionFactory sqlSessionFactory = SpringContextHolder.getBean(SqlSessionFactory.class);
//	
////	@Scheduled(cron = "*/10 * * * * ?")
//	public static void runUpdateStatus()
//	{
//		if (!SmsSRQueueFactory.getSmsSendQueue().isEmpty())
//		{
//			SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH, false);
//
//			int num = 0;
//
//			long beginTime = System.currentTimeMillis();
//
//			try {
//				SMSSRMessage smsSRMessage;
//				while (!SmsSRQueueFactory.getSmsSendQueue().isEmpty()) {
//					
//					smsSRMessage = (SMSSRMessage) SmsSRQueueFactory.getSmsSendQueue().take();
//					
//					if(smsSRMessage.getMessage() != null)
//					{
//						Map<String,Object> map = Maps.newHashMap();
//	                    map.put("id", smsSRMessage.getMessage().getId());
//	                    map.put("gatewayStatus", smsSRMessage.getResult());
//	                    map.put("submitTime", new Date(smsSRMessage.getMessage().getSubmitTime()));
//	                    map.put("reportTime", new Date(smsSRMessage.getMessage().getReportTime()));
//	                    sqlSession.update("com.sanerzone.smscenter.modules.smsv2.dao.SmsSendDao.batchUpdateGatewayStatus", map);
//	                    
//	                    int count = sqlSession.update("com.sanerzone.smscenter.modules.smsv2.dao.SmsSendDao.batchUpdateGatewayStatus", map);
//						
//						if (count == 0)
//						{
//							SmsSRQueueFactory.getSmsSendQueue().put(smsSRMessage);
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
