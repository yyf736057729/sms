package com.siloyou.jmsg.modules.sms.task;

import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;
import com.siloyou.core.common.persistence.Page;
import com.siloyou.core.common.utils.SpringContextHolder;
import com.siloyou.jmsg.modules.sms.entity.JmsgCustomTask;
import com.siloyou.jmsg.modules.sms.service.JmsgCustomTaskService;
import com.siloyou.jmsg.modules.sms.task.impl.PublicTaskSendExecutor;

//公共自定义任务
@Service
@Lazy(false)
public class PublicCustomTask {
	
	
	public static Map<String, PublicTaskSendExecutor> taskExecMap = Maps.newConcurrentMap(); 
	private JmsgCustomTaskService jmsgCustomTaskService = SpringContextHolder.getBean(JmsgCustomTaskService.class);
	ThreadPoolTaskExecutor poolTaskExecutor = (ThreadPoolTaskExecutor)SpringContextHolder.getBean("taskExecutor");
	
	//执行后 休息1分钟继续执行
	@Scheduled(fixedDelay=60000)
	public void run(){
		
		int poolTaskSize = taskExecMap.size();//线程数
		if(poolTaskSize < 5){
		
			JmsgCustomTask param = new JmsgCustomTask();
			Page<JmsgCustomTask> page = new Page<JmsgCustomTask>();
			page.setPageSize(5);
			param.setPage(page);
			param.setStatus("1");//待执行
			List<JmsgCustomTask> list = jmsgCustomTaskService.findList(param);
			if(list != null && list.size() >0){
				for (JmsgCustomTask jmsgCustomTask : list) {
					String taskId = jmsgCustomTask.getId();
					PublicTaskSendExecutor send = new PublicTaskSendExecutor(jmsgCustomTask);
					synchronized (send) {
						if(!taskExecMap.containsKey(taskId)){
							taskExecMap.put(taskId,send);
							poolTaskExecutor.execute(send); //执行作务
						}
					}
					
				}
			}
		}
	}
}
