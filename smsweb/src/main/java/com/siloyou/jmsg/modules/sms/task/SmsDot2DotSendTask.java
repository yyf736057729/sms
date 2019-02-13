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
import com.siloyou.jmsg.modules.sms.task.impl.SmsTaskSendDotExecutorV2;

//点对点短信定时任务
@Service
@Lazy(false)
public class SmsDot2DotSendTask {
	
	public static Map<String, SmsTaskSendDotExecutorV2> smsDotSendExecMap = Maps.newConcurrentMap(); 
	ThreadPoolTaskExecutor poolTaskExecutor = (ThreadPoolTaskExecutor)SpringContextHolder.getBean("taskExecutor");
	private JmsgSmsTaskDao taskDao = SpringContextHolder.getBean(JmsgSmsTaskDao.class);
	
	//每10秒执行一次
	@Scheduled(cron = "*/10 * * * * ?")
	public void exec(){
		
		int poolTaskSize = smsDotSendExecMap.size();//线程数
		
		if(poolTaskSize <20){
			//查询任务表中待发送的点对点短信任务，取前20条
			List<JmsgSmsTask> list = taskDao.findPendingSendSmsDot();
			
			System.out.println("当前点对点线程数："+poolTaskSize);
			
			if(list != null && list.size() >0){
				for (JmsgSmsTask jmsgSmsTask : list) {
					if(poolTaskSize>=30){
						break;
					}
					String taskid=jmsgSmsTask.getId();
					SmsTaskSendDotExecutorV2 dotSend = new SmsTaskSendDotExecutorV2(taskid,jmsgSmsTask.getVersion(),jmsgSmsTask.getRowNumber());
					synchronized (dotSend) {
						if(!smsDotSendExecMap.containsKey(taskid)){
							smsDotSendExecMap.put(taskid,dotSend);
							poolTaskExecutor.execute(dotSend); //执行作务
						}
					}
				}
			}
		}
	}
	
	
}
