package com.siloyou.jmsg.modules.mms.task;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.siloyou.core.common.utils.DateUtils;
import com.siloyou.jmsg.modules.account.service.JmsgAccountService;
import com.siloyou.jmsg.modules.mms.dao.JmsgMmsTaskDao;
import com.siloyou.jmsg.modules.mms.entity.JmsgMmsTask;

//彩信返充
//@Service
//@Lazy(false)
public class MmsBackRechargeTask {
	
	@Autowired
	private JmsgMmsTaskDao jmsgMmsTaskDao;
	
	@Autowired
	private JmsgAccountService jmsgAccountService;
	
	//每天 1:30:35执行返充任务
	@Scheduled(cron = "35 30 1 * * ?")
	@Transactional(readOnly = false)
	public void execMmsBack(){
		Date day = DateUtils.getDay(-4);
		MmsSendSuccessTask mmsSendSuccessTask = new MmsSendSuccessTask();//执行4天前成功数
		mmsSendSuccessTask.mmsSendSuccessCount(day);
		List<JmsgMmsTask> list = jmsgMmsTaskDao.findBackRechargeList(day);
		if(list != null && list.size() >0){
			for(JmsgMmsTask jmsgMmsTask : list){
				int money = jmsgMmsTask.getSendCount() - jmsgMmsTask.getSuccessCount();//返充条数
				String taskId = jmsgMmsTask.getId();
				String userId = jmsgMmsTask.getCreateUserId();
				if(money !=0){
					jmsgAccountService.rechargeMoney(userId, "02", money, "mms", "失败返充操作(任务ID："+taskId+")", "1", taskId);//返充操作
				}
				jmsgMmsTask.setBackCount(money);
				jmsgMmsTaskDao.updateTaskBackStatus(jmsgMmsTask);//修改返充状态
				
				//生成对账单
				mmsSendSuccessTask.crateDetailAccount(userId, DateUtils.formatDate(jmsgMmsTask.getCreateDatetime(), "yyyyMM"), taskId);
			}
		}
		
	}
	
}
