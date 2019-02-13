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
import com.siloyou.jmsg.modules.sms.task.impl.SmsMmsTaskSendExecutor;

//短信彩信定时任务
//@Service
//@Lazy(false)
public class SmsMmsSendTask {
	
	public static Map<String, SmsMmsTaskSendExecutor> smsMmsSendExecMap = Maps.newConcurrentMap(); 
	ThreadPoolTaskExecutor poolTaskExecutor = (ThreadPoolTaskExecutor)SpringContextHolder.getBean("taskExecutor");
	private JmsgSmsTaskDao taskDao = SpringContextHolder.getBean(JmsgSmsTaskDao.class);
	
	//每10秒执行一次
	@Scheduled(cron = "*/10 * * * * ?")
	public void exec(){
		
		int poolTaskSize = smsMmsSendExecMap.size();//线程数
		
		if(poolTaskSize <20){
			//查询任务表中待发送的点对点短信任务，取前20条
			List<JmsgSmsTask> list = taskDao.findPendingSendMms();
			
			System.out.println("当前短信彩信线程数："+poolTaskSize);
			
			if(list != null && list.size() >0){
				for (JmsgSmsTask jmsgSmsTask : list) {
					if(poolTaskSize>=30){
						break;
					}
					String taskid=jmsgSmsTask.getId();
					SmsMmsTaskSendExecutor mmsSend = new SmsMmsTaskSendExecutor(taskid,jmsgSmsTask.getVersion(),jmsgSmsTask.getRowNumber());
					synchronized (mmsSend) {
						if(!smsMmsSendExecMap.containsKey(taskid)){
							smsMmsSendExecMap.put(taskid,mmsSend);
							poolTaskExecutor.execute(mmsSend); //执行作务
						}
					}
					
				}
			}
		}
	}
	
	
}
