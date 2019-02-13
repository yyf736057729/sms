package com.siloyou.jmsg.modules.sms.task;

import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;
import com.siloyou.core.common.utils.SpringContextHolder;
import com.siloyou.jmsg.modules.sms.dao.JmsgSmsTaskDao;
import com.siloyou.jmsg.modules.sms.entity.JmsgSmsTask;
import com.siloyou.jmsg.modules.sms.task.impl.SmsTaskSendExecutorV2;

//定时任务
@Service
@Lazy(false)
public class SmsSendTask {
	
	public static Map<String, SmsTaskSendExecutorV2> smsTaskSendExecMap = Maps.newConcurrentMap(); 
	ThreadPoolTaskExecutor poolTaskExecutor = (ThreadPoolTaskExecutor)SpringContextHolder.getBean("taskExecutor");
	private JmsgSmsTaskDao taskDao = SpringContextHolder.getBean(JmsgSmsTaskDao.class);
	
	//每10秒执行一次
	@Scheduled(cron = "*/10 * * * * ?")
	public void exec(){
		
		int poolTaskSize = smsTaskSendExecMap.size();//线程数
		
		if(poolTaskSize <20){
			//查询任务表中待发送的任务，取前20条
			List<JmsgSmsTask> list = taskDao.findPendingSendSms();
			
			System.out.println("当前线程数："+poolTaskSize);
			
			if(list != null && list.size() >0){
				for (JmsgSmsTask jmsgSmsTask : list) {
					if(poolTaskSize>=30){
						break;
					}
					String taskid=jmsgSmsTask.getId();
					SmsTaskSendExecutorV2 send = new SmsTaskSendExecutorV2(taskid,jmsgSmsTask.getVersion(),jmsgSmsTask.getRowNumber());
					synchronized (send) {
						if(!smsTaskSendExecMap.containsKey(taskid)){
							smsTaskSendExecMap.put(taskid,send);
							poolTaskExecutor.execute(send); //执行作务
						}
					}
					
				}
			}
		}
	}
	
	
}
