package com.siloyou.jmsg.modules.sms.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.siloyou.core.common.utils.SpringContextHolder;
import com.siloyou.jmsg.modules.sms.service.JmsgSmsTaskService;

//定时任务 短信任务汇总
@Service
@Lazy(false)
public class SmsTaskReportTask {
	
	public static Logger logger = LoggerFactory.getLogger(SmsTaskReportTask.class);
	
	private static JmsgSmsTaskService jmsgSmsTaskService = SpringContextHolder.getBean(JmsgSmsTaskService.class);
	
	
	//每5分钟统计一次短信发送任务
	@Scheduled(fixedDelay=300000)
	@Transactional(readOnly = false)
	public void execSmsTaskReportDay(){
		logger.info("===============每5分钟统计一次短信发送任务=============");
		jmsgSmsTaskService.smsTaskReport(0);
	}
	
	
//	//每天 2:30:35执行统计短信发送任务
//	@Scheduled(cron = "35 30 2 * * ?")
//	@Transactional(readOnly = false)
//	public void execSmsTaskReport(){
//		jmsgSmsTaskService.smsTaskReport(-4);
//	}
	
}
