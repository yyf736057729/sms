package com.siloyou.jmsg.modules.sms.task;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sanerzone.common.modules.account.utils.AccountCacheUtils;
import com.sanerzone.common.support.utils.DateUtils;
import com.sanerzone.common.support.utils.JedisClusterUtils;
import com.siloyou.jmsg.modules.account.service.JmsgAccountService;
import com.siloyou.jmsg.modules.sms.dao.JmsgSmsDayReportDao;
import com.siloyou.jmsg.modules.sms.entity.JmsgSmsDayReport;


@Service
@Lazy(false)
public class SmsDayAccountClearTask {
	
	private static Logger logger = LoggerFactory.getLogger(SmsDayAccountClearTask.class);
	
	@Autowired
	private JmsgAccountService jmsgAccountService;
	
	@Autowired
	private JmsgSmsDayReportDao jmsgSmsDayReportDao;
	
	//每天 00:10:30执行日账务清算任务
	@Scheduled(cron = "30 10 0 * * ?")
	@Transactional(readOnly = false)
	public void run(){
		try{
			int day = -1;
			SmsDayReportTaskV2 dayReport = new SmsDayReportTaskV2();
			dayReport.saveSmsDayReport(false, day);//统计昨天的发送报表
			JmsgSmsDayReport param = new JmsgSmsDayReport();
			param.setDay(DateUtils.getDay(day));
			List<JmsgSmsDayReport> list = jmsgSmsDayReportDao.findDaySendListV2(param);
			if(list != null && list.size() > 0){
				String tableName = "jmsg_sms_send_"+DateUtils.getDayOfMonth(0);
				JmsgSmsDayReport todayParam = new JmsgSmsDayReport();
				todayParam.setTableName(tableName);
				
				for (JmsgSmsDayReport jmsgSmsDayReport : list) {
					String userId = jmsgSmsDayReport.getUserId();
					todayParam.setUserId(userId);
					List<JmsgSmsDayReport> smsSendList = jmsgSmsDayReportDao.findSendListByDayV2(todayParam);
					int todayCount=0;
					if(smsSendList != null && smsSendList.size() >0){//统计今天发送的
						for (JmsgSmsDayReport smsSend : smsSendList) {
							todayCount=todayCount+smsSend.getSendCount();
						}
					}
					String key = AccountCacheUtils.getAmountKey("sms", userId);
					int amount = jmsgAccountService.findUserMoeny(userId, "sms").intValue() - todayCount - jmsgSmsDayReport.getSendCount();
					JedisClusterUtils.set(key, String.valueOf(amount), 0);//置值
					jmsgAccountService.consumeMoney(userId, "02", jmsgSmsDayReport.getSendCount(), "sms", "自消费操作(消费日期："+DateUtils.formatDate(DateUtils.getDay(day), "yyyy-MM-dd") +")", userId, "");//消费
				}
			}
		}catch(Exception e){
			logger.error("定时任务,执行日账务清算任务失败",e);
		}
		
		
	}
	
}
