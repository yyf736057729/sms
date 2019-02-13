package com.siloyou.jmsg.modules.mms.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.siloyou.jmsg.modules.mms.dao.JmsgMmsAlldayReportDao;
import com.siloyou.jmsg.modules.mms.entity.JmsgMmsAlldayReport;

//定时任务 彩信日报表
//@Service
//@Lazy(false)
public class MmsAllDayReportTask {
	
	@Autowired
	private JmsgMmsAlldayReportDao jmsgMmsAlldayReportDao;
	
	//每5分钟统计一次日报表
	@Scheduled(cron = "0 0/5 * * * ?")
	public void execMmsAllDayReport(){
		System.out.println("进入报表定时任务");
		
		 JmsgMmsAlldayReport entity = jmsgMmsAlldayReportDao.queryAllDaySendCount();//发送总量
		 if(entity == null)entity = new JmsgMmsAlldayReport();
		 Long submitCount = jmsgMmsAlldayReportDao.queryAllDaySubmitCount();//网关成功
		 Long reportCount = jmsgMmsAlldayReportDao.queryAllDayReportCount();//状态报告
		 entity.setSubmitCount(submitCount == null ? 0L : submitCount);
		 entity.setReportCount(reportCount == null ? 0L : reportCount);
		 jmsgMmsAlldayReportDao.insert(entity);
	}
}
