package com.siloyou.jmsg.modules.sms.task.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;
import com.sanerzone.common.modules.TableNameUtil;
import com.siloyou.core.common.utils.SpringContextHolder;
import com.siloyou.jmsg.modules.sms.dao.JmsgSmsSendDao;
import com.siloyou.jmsg.modules.sms.entity.JmsgSmsSend;
import com.siloyou.jmsg.modules.sms.service.SmsSendService;

public class SmsSendExecutor implements Runnable {
	
	Logger logger = LoggerFactory.getLogger(SmsSendExecutor.class);
//	JmsgSmsReviewDao reviewDao = SpringContextHolder.getBean(JmsgSmsReviewDao.class);
	JmsgSmsSendDao jmsgSmsSendDao = SpringContextHolder.getBean(JmsgSmsSendDao.class);
	SqlSessionFactory sqlSessionFactory = SpringContextHolder.getBean(SqlSessionFactory.class);
	SmsSendService smsSendService = SpringContextHolder.getBean(SmsSendService.class);
	private final String taskId;
	
	private int sendSmsSize = 0;//发送短信信数量
	
	private static final int pageSize = 1000;
	
//	private static long speed = 1;//速率 默认1秒1000条 单位毫秒  //TODO 不限速
	
	public SmsSendExecutor(String taskId){
		this.taskId = taskId;
	}
	
	/**
	//设置速率
	public static void setSpeed(long speed){
		SmsSendExecutor.speed = speed;
	}
	
	//获取速率
	public static long getSpeed(){
		return speed;
	}**/
	
	//任务进度
	public int taskProgress(){
		return sendSmsSize;
	}
	
	@Override
	public void run() {
		
		long startTime = System.currentTimeMillis();
		System.out.println("-----start:"+ startTime);
		
		try {
			Thread.sleep(1000);
			System.out.println("休眠 1s");
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		String maxId = "";
		String tableIndex = TableNameUtil.getTableIndex(new Date());
		String tableName = "jmsg_sms_send_"+tableIndex;
		SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH, false);
		try{
			while(true){
				Map<String,Object> param = Maps.newHashMap();
				param.put("taskId", taskId);
				param.put("id",maxId);
				param.put("pageSize", pageSize);
				param.put("tableName",tableName);
				List<JmsgSmsSend> list = jmsgSmsSendDao.findListByTaskId(param);//获取待发送任务 每次获取1000条
				logger.info("任务大小:{}", list.size());
				if(list != null && list.size() >0){
					for (JmsgSmsSend jmsgSmsSend : list) {
						sendSmsSize++;//任务执行进度
						
						Map<String,String> map = smsSendService.sendHandler(jmsgSmsSend);//发送短信处理
						map.put("tableName",tableName);
						sqlSession.update("com.siloyou.jmsg.modules.sms.dao.JmsgSmsSendDao.batchUpdate",map);
						if(sendSmsSize % 500 == 0){
							sqlSession.commit();
						}
					}
				}else{
					break;
				}
				
				if(list.size()== pageSize){
					maxId = list.get(pageSize-1).getId();
				}else{
					break;
				}
			}
			sqlSession.commit();
			
			//推送 
			SmsPushExecutor smsPushExecutor = new SmsPushExecutor(taskId,tableIndex);
			smsPushExecutor.run();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(sqlSession != null){
				sqlSession.close();
			}
		}

		long endTime = System.currentTimeMillis();
		System.out.println("任务结束时间endTime:"+(endTime - startTime)/1000);
	}
}
