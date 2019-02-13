package com.siloyou.jmsg.modules.mms.task;

import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;
import com.siloyou.core.common.utils.SpringContextHolder;
import com.siloyou.jmsg.modules.mms.dao.JmsgMmsTaskDao;
import com.siloyou.jmsg.modules.mms.dao.JmsgMmsTaskDetailDao;
import com.siloyou.jmsg.modules.mms.entity.JmsgMmsTaskDetail;
import com.siloyou.jmsg.modules.mms.task.impl.MmsSendExecutor;

//定时任务
//@Service
//@Lazy(false)
public class MmsSendTask {
	
	public static Map<String, MmsSendExecutor> mmsSendExecMap = Maps.newConcurrentMap(); 
	ThreadPoolTaskExecutor poolTaskExecutor = (ThreadPoolTaskExecutor)SpringContextHolder.getBean("taskExecutor");
	private JmsgMmsTaskDao taskDao = SpringContextHolder.getBean(JmsgMmsTaskDao.class);
	private static JmsgMmsTaskDetailDao taskDetailDao = SpringContextHolder.getBean(JmsgMmsTaskDetailDao.class);
	
	//每10秒执行一次
	@Scheduled(cron = "*/10 * * * * ?")
	public void exec(){
		
		int poolTaskSize = mmsSendExecMap.size();//线程数
		
		if(poolTaskSize <20){
			//查询任务表中待发送的任务，取前20条
			List<String> list = taskDao.findPendingSendMms();
			
			System.out.println("当前线程数："+poolTaskSize);
			
			if(list != null && list.size() >0){
				for (String taskid : list) {
					if(poolTaskSize>=30){
						break;
					}
					
					MmsSendExecutor send = new MmsSendExecutor(taskid);
					if(!mmsSendExecMap.containsKey(taskid)){
						mmsSendExecMap.put(taskid,send);
						poolTaskExecutor.execute(send); //执行作务
					}
				}
			}
		}
	}
	
	//任务执行后间隔30秒 继续执行
//	@Scheduled(fixedDelay=30000)
	public void againSend(){
		long speed = MmsSendExecutor.getSpeed();
		List<JmsgMmsTaskDetail> list = taskDetailDao.findPendingSendMmsByStatus();
		if(list != null && list.size() > 0){
			for (JmsgMmsTaskDetail entity : list) {
				long start = System.currentTimeMillis();
				MmsSendExecutor.sendMms(entity.getTaskId(), entity.getId(), entity.getCreateUserId(), entity.getPhone(), entity.getMmsTitle(), entity.getMmsUrl(),entity.getMmsSize(),true);
				long onceSpeed = System.currentTimeMillis() - start;//单次速率
				long sleepTime = speed - onceSpeed;//休眠时长
//				System.out.println("需要休眠时间:"+sleepTime+"单次执行耗时:"+onceSpeed+"速率:"+speed);
				if(sleepTime >0){
					try {
						Thread.sleep(sleepTime);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
}
