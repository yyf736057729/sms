package com.siloyou.jmsg.modules.sms.task;

import java.util.TimerTask;

import javax.annotation.PostConstruct;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import sun.tools.tree.ThisExpression;

import com.siloyou.core.common.utils.DateUtils;
import com.siloyou.core.common.utils.SpringContextHolder;
import com.siloyou.jmsg.modules.sms.entity.JmsgSmsSend;

//@Service
//@Lazy(false)
public class SmsSendStatusTask {
	
	public static Logger logger = LoggerFactory.getLogger(SmsSendStatusTask.class);
	
//	private BlockingQueue<Serializable> mqStatusQueue = BDBStoredMapFactoryImpl.INS.getQueue("SMS", "SMSMQSTATUS");//短信发送状态
//	
//	private BlockingQueue<Serializable> mqUTQueue= BDBStoredMapFactoryImpl.INS.getQueue("SMS", "SMSUTSTATUS");//推送短信状态报告 队列
	
	private static SqlSessionFactory sqlSessionFactory = SpringContextHolder.getBean(SqlSessionFactory.class);
	
	int prevSize = 0;
	
	//@PostConstruct
    public void init(){
        
        //启动监控
        /*new java.util.Timer().schedule(new TimerTask() {
             @Override
                public void run() {
                    int thisSize = SmsSendQueueFactory.getSmsSendQueue().size();
                    logger.info("入库监控, 当前待入库:{}", thisSize);
                    prevSize = thisSize;
                }
            }, 0, 2000);*/       //每10秒钟统计一次入库速度
        
        
        /*for(int i=0; i<6;i ++) {
            new Thread(new Runnable()
            {
                //@Override
                public void run()
                {
                    while(true) {
                        //logger.info("入库监控, 线程:{}", Thread.currentThread().getName());
                        if (!SmsSendQueueFactory.getSmsSendQueue().isEmpty())
                        {
                            SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH, false);
                            
                            int num = 0;
                            
                            long beginTime = System.currentTimeMillis();
                            
                            try
                            {
                                //Map<String,String> map = new HashMap<String,String>(); 
                                JmsgSmsSend jmsgSmsSend;
                                //while(true)
                                while(!SmsSendQueueFactory.getSmsSendQueue().isEmpty())
                                {
                                    //map = (Map<String, String>)SmsSendQueueFactory.getSmsSendQueue().take();
                                    jmsgSmsSend = (JmsgSmsSend)SmsSendQueueFactory.getSmsSendQueue().poll();
                                    
                                    if(jmsgSmsSend == null) {
                                        //break;
                                        continue;
                                    }
                                    
                                    num ++;
                                    //sqlSession.update("com.siloyou.jmsg.modules.sms.dao.JmsgSmsSendDao.batchUpdate",map);
                                    sqlSession.insert("com.siloyou.jmsg.modules.sms.dao.JmsgSmsSendDao.insert", jmsgSmsSend);
                                    
                                    if(num % 500 == 0){
                                        sqlSession.commit();
                                    }
                                }
                                sqlSession.commit();
                            }
                            catch (InterruptedException e)
                            {
                                logger.error("入库监控, 发送结果入库异常：{}", e.getMessage());
                            }
                            finally
                            {
                                sqlSession.close();
                            }
                            
                            long endTime = System.currentTimeMillis();
                            
                            logger.info("入库监控, 线程:{}, 发送结果入库条数：{}, 用时:{}, 平均:{}",Thread.currentThread().getName(), num,  DateUtils.formatDateTime(endTime - beginTime), num/(((endTime - beginTime) > 0 ? (endTime - beginTime) : 1) / 1000));
                        }
                        else
                        {
                            try
                            {
                                Thread.sleep(100);
                            }
                            catch (InterruptedException e)
                            {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }, "ThreadName_"+i).start();
        }*/
    }
	
	@Scheduled(cron = "*/1 * * * * ?")
	public static void runUpdateStatus()
	{
//        if (!SmsSendQueueFactory.getSmsSendQueue().isEmpty())
//        {
//            SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH, false);
//            
//            int num = 0;
//            
//            long beginTime = System.currentTimeMillis();
//            
//            try
//            {
//                //Map<String,String> map = new HashMap<String,String>(); 
//                JmsgSmsSend jmsgSmsSend;
//                while(!SmsSendQueueFactory.getSmsSendQueue().isEmpty())
//                {
//                    num ++;
//                    //map = (Map<String, String>)SmsSendQueueFactory.getSmsSendQueue().take();
//                    jmsgSmsSend = (JmsgSmsSend)SmsSendQueueFactory.getSmsSendQueue().take();
//                    
//                    //sqlSession.update("com.siloyou.jmsg.modules.sms.dao.JmsgSmsSendDao.batchUpdate",map);
//                    sqlSession.insert("com.siloyou.jmsg.modules.sms.dao.JmsgSmsSendDao.insert", jmsgSmsSend);
//                    
//                    if(num % 500 == 0){
//                        sqlSession.commit();
//                    }
//                }
//                sqlSession.commit();
//            }
//            catch (InterruptedException e)
//            {
//                logger.error("入库监控, 发送结果入库异常：{}", e.getMessage());
//            }
//            finally
//            {
//                sqlSession.close();
//            }
//            
//            long endTime = System.currentTimeMillis();
//            
//            logger.info("入库监控, 发送结果入库条数：{}, 用时:{}, 平均:{}",num,  DateUtils.formatDateTime(endTime - beginTime), num/((endTime - beginTime)/1000) );
//        }
	}
	
	//入库短信发送状态 任务执行完后休息一秒继续执行
//	@Scheduled(fixedDelay=1000)
	public void runSendStatus(){
		
		
//		long l = System.currentTimeMillis();
//		logger.info("入库修改短信发送状态开始==============");
//		SqlSession sqlSession = SpringContextHolder.getBean(SqlSessionFactory.class).openSession(ExecutorType.BATCH, false);
//		int index=0;
//		try{
//			while(!StatusQueueFactory.getStatusRespQueue().isEmpty()){
//				@SuppressWarnings("unchecked")
//				Map<String,String> param = (Map<String,String>) StatusQueueFactory.getStatusRespQueue().take();
//				index++;
//				
//				logger.info("入库修改短信发送状态SIZE=============="+index);
//				sqlSession.update("com.siloyou.jmsg.modules.sms.dao.JmsgSmsSendDao.batchUpdate", param);
//				if (index % 200 == 0) {
//					sqlSession.commit();
//				}
//			}
//			sqlSession.commit();
//		}catch(Exception e){
//			logger.debug("{}", e);
//		}finally {
//			if(sqlSession!=null)sqlSession.close();
//		}
//		logger.info("入库修改短信发送状态结束==============耗时:"+(System.currentTimeMillis()-l)/1000);
//		
	}
	
	//推送短信状态报告队列 任务执行完后休息一秒继续执行
//	@Scheduled(fixedDelay=1000)
	public void runPushStatus(){
//		logger.info("推送短信状态报告队列开始");
//		try{
//			while(!StatusQueueFactory.getuTRespQueue().isEmpty()){
//				SmsRtMessage smsRtMessage = (SmsRtMessage)StatusQueueFactory.getuTRespQueue().take();
//				MQUtils.pushSmsMQ(smsRtMessage.getSmsMt().getId(), FstObjectSerializeUtil.write(smsRtMessage));//推送短信状态报告
//			}
//		}catch(Exception e){
//			logger.debug("{}", e);
//		}
////		logger.info("推送短信状态报告队列结束");
	}
	

}
