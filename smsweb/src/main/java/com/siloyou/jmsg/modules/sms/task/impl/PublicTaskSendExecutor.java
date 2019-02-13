package com.siloyou.jmsg.modules.sms.task.impl;

import java.lang.reflect.Constructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.siloyou.core.common.utils.SpringContextHolder;
import com.siloyou.jmsg.modules.sms.entity.JmsgCustomTask;
import com.siloyou.jmsg.modules.sms.service.JmsgCustomTaskService;
import com.siloyou.jmsg.modules.sms.task.IPublicCustomTask;
import com.siloyou.jmsg.modules.sms.task.PublicCustomTask;

public class PublicTaskSendExecutor implements Runnable {
	
	public static Logger logger = LoggerFactory.getLogger(PublicTaskSendExecutor.class);
	
	private JmsgCustomTask jmsgCustomTask;
	
	private JmsgCustomTaskService jmsgCustomTaskService = SpringContextHolder.getBean(JmsgCustomTaskService.class);
	
	public PublicTaskSendExecutor(JmsgCustomTask jmsgCustomTask){
		this.jmsgCustomTask = jmsgCustomTask;
	}

	@Override
	public void run() {
		//修改状态 执行中
		String status = "2";
		jmsgCustomTask.setStatus(status);
		jmsgCustomTaskService.updateParam(jmsgCustomTask);
		
		String resultJson = "";
		try {
			Class<?> clz = Class.forName(jmsgCustomTask.getExecuteClass());
			Constructor<?> constructor = clz.getConstructor();
			IPublicCustomTask publicCustomTask = (IPublicCustomTask) constructor.newInstance();
			resultJson = publicCustomTask.taskRun(jmsgCustomTask.getParamJson());
			status = "3";
		} catch (Exception e) {
			logger.error("自定义任务执行失败：{}", e);
			status = "0";//任务执行失败
		}
		
		//修改状态 更新执行结果
		jmsgCustomTask.setStatus(status);
		jmsgCustomTask.setExecuteResult(resultJson);
		jmsgCustomTaskService.updateParam(jmsgCustomTask);
		
		PublicCustomTask.taskExecMap.remove(jmsgCustomTask.getId());//剔除线程
		
	}

}
