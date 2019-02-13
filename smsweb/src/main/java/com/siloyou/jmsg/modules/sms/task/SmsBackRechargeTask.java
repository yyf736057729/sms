package com.siloyou.jmsg.modules.sms.task;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
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
import com.siloyou.core.common.config.Global;
import com.siloyou.core.common.utils.SpringContextHolder;
import com.siloyou.core.common.utils.StringUtils;
import com.siloyou.core.modules.sys.dao.UserDao;
import com.siloyou.core.modules.sys.entity.User;
import com.siloyou.jmsg.modules.account.service.JmsgAccountService;
import com.siloyou.jmsg.modules.sms.dao.JmsgSmsDayReportDao;
import com.siloyou.jmsg.modules.sms.dao.JmsgSmsSendDao;
import com.siloyou.jmsg.modules.sms.entity.JmsgSmsDayReport;
import com.siloyou.jmsg.modules.sms.entity.JmsgSmsMmsdown;
import com.siloyou.jmsg.modules.sms.entity.JmsgSmsSend;
import com.siloyou.jmsg.modules.sms.service.JmsgSmsTaskService;

//短信返充
@Service
@Lazy(false)
public class SmsBackRechargeTask {
	
	private static Logger logger = LoggerFactory.getLogger(SmsBackRechargeTask.class);
	
	@Autowired
	private JmsgSmsDayReportDao jmsgSmsDayReportDao;
	
	@Autowired
	private JmsgAccountService jmsgAccountService;
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private JmsgSmsSendDao jmsgSmsSendDao;
	
	private static JmsgSmsTaskService jmsgSmsTaskService = SpringContextHolder.getBean(JmsgSmsTaskService.class);
	
	//每天 1:30:35执行返充任务
	@Scheduled(cron = "35 30 1 * * ?")
	@Transactional(readOnly = false)
	public void execSmsBack(){
		int day = -4;
		Date date = DateUtils.getDay(day);
		String sDay = DateUtils.formatDate(date,"yyyy-MM-dd");
		
		int count = jmsgSmsDayReportDao.queryBackFlagCount(date);
		if(count == 0){
			SmsDayReportTaskV2 smsDayReportTaskV2 = new SmsDayReportTaskV2();
			smsDayReportTaskV2.saveSmsDayReport(true,day);//统计日报表
			//smsDayReportTask.saveSmsGatewayDayReport(day);//统计通道日报表
			
			SmsDayReportTask smsDayReportTask = new SmsDayReportTask();
			smsDayReportTask.saveSmsGatewayDayReport(day);
			
			//获取返充列表
			List<JmsgSmsDayReport> rechargeList =  jmsgSmsDayReportDao.findRechargeList(date);
			if(rechargeList != null && rechargeList.size() >0){
				for (JmsgSmsDayReport jmsgSmsDayReport : rechargeList) {
					int userBackCount = jmsgSmsDayReport.getUserBackCount();
					int backCount = jmsgSmsDayReport.getBackCount();//代理商返充条数
					String userId = jmsgSmsDayReport.getUser().getId();
					if(userBackCount >0){//用户返充条数
						String key = AccountCacheUtils.getAmountKey("sms", userId);
						JedisClusterUtils.incrBy(key,userBackCount);//TODO 
						jmsgAccountService.rechargeMoney(userId, "02", userBackCount, "sms", "失败返充操作【"+sDay+"】", "1", "");//返充操作
					}
					if(backCount > 0){//代理商返充条数
						User user = userDao.getAgencyByUserId(userId);
						if(user != null && !userId.equals(user.getId())){
							String key = AccountCacheUtils.getAmountKey("sms", user.getId());
							JedisClusterUtils.incrBy(key, backCount); 
							jmsgAccountService.rechargeMoney(user.getId(), "03", backCount, "sms", "提交计费返充【"+sDay+"】", "1", "");//返充操作
						}
					}
					
					//修改返充状态
					jmsgSmsDayReportDao.updateBackFlag(jmsgSmsDayReport);
					
					//生成对账单
//					createDetailAccount(userId, day, DateUtils.formatDate(date, "yyyyMMdd"));
				}
				
			}
			jmsgSmsTaskService.smsTaskReport(day);
			//清理4天前的数据
			clearSendByDay(date);
		}else{
			logger.error("短信返充错误，已经返充。操作时间："+DateUtils.formatDateTime(DateUtils.getDay(0)));
		}
	}
	
	public void createDetailAccount(String userId,int day,String date){
		
		//String payType = UserUtils.getPayMode(userId,"sms");
		JmsgSmsSend param = new JmsgSmsSend();
		param.setUser(new User(userId));
//		Date dayQ = DateUtils.getDay(DateUtils.getDay(day),0,0,0);
//		Date dayZ = DateUtils.getDay(DateUtils.getDay(day),23,59,59);
		String tableName = "jmsg_sms_send_"+DateUtils.getDayOfMonth(day);
//		param.setSendDatetimeQ(dayQ);
//		param.setSendDatetimeZ(dayZ);
		param.setTableName(tableName);
		
		String result = "";
//		if("0".equals(payType)){//提交
//			result = result(jmsgSmsSendDao.querySendDeatilResultBySend(param));
//		}else if("2".equals(payType)){//网关状态
//			result = result(jmsgSmsSendDao.querySendDeatilResultByReport(param));
//		}
		
		result = resultV2(jmsgSmsSendDao.querySendDeatilResultByReportV2(param));
		
		FileOutputStream outSTr = null;  
        BufferedOutputStream buff = null;  
		try{
			String dayPath = Global.getConfig("sms.detail.send.path")+"/"+date;
			File dayFile = new File(dayPath);
			if(!dayFile.exists()){
				dayFile.mkdir();
			}
			
			String path = dayPath+"/"+userId+"_"+date+".txt";
			File file = new File(path);
			if(!file.exists()){
				file.createNewFile();
			}
			
			outSTr = new FileOutputStream(file);
			buff = new BufferedOutputStream(outSTr); 
			buff.write(result.getBytes("UTF-8"));
			buff.flush();  
	        buff.close();  
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				buff.close();
				outSTr.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	private String result(List<JmsgSmsSend> list){
		StringBuffer sb = new StringBuffer();
		for (JmsgSmsSend jmsgSmsSend : list) {
			String sendResult = jmsgSmsSend.getSendResult();
			if(StringUtils.isBlank(sendResult)){
				if(jmsgSmsSend.getSendStatus().startsWith("T")){
					if(jmsgSmsSend.getReportStatus().startsWith("F")){
						sendResult="失败";
					}else{
						sendResult="成功";
					}
				}else{
					sendResult="失败";
				}
				
			}
			sb.append(jmsgSmsSend.getPhone()).append(" ").append(sendResult).append(" ").append(jmsgSmsSend.getSmsContent()).append("\r\n");
		}
		return sb.toString();
	}
	
	private String resultV2(List<JmsgSmsSend> list){
		StringBuffer sb = new StringBuffer();
		for (JmsgSmsSend jmsgSmsSend : list) {
			String payType = jmsgSmsSend.getPayType();//扣费方式 0：提交 2：状态成功
			
			String sendResult = "";
			if("0".equals(payType)){
				if(jmsgSmsSend.getSendStatus().startsWith("T")){
					sendResult = "成功";
				}else{
					sendResult = "失败";
				}
			}else if("2".equals(payType)){
				if(jmsgSmsSend.getSendStatus().startsWith("T")){
					if(jmsgSmsSend.getReportStatus().startsWith("F")){
						sendResult="失败";
					}else{
						sendResult="成功";
					}
				}else{
					sendResult="失败";
				}
			}
			
			sb.append(jmsgSmsSend.getPhone()).append(" ").append(sendResult).append(" ").append(jmsgSmsSend.getSmsContent()).append("\r\n");
		}
		return sb.toString();
	}
	
	private void clearSendByDay(Date date){
		JmsgSmsSend param = new JmsgSmsSend();
		JmsgSmsMmsdown down = new JmsgSmsMmsdown();
		down.setTableName("jmsg_sms_mmsdown_"+DateUtils.getDayOfMonth(date));
		String history = "jmsg_sms_send_history_"+DateUtils.formatDate(date, "yyyyMM");
		param.setTableName("jmsg_sms_send_"+DateUtils.getDayOfMonth(date));
		param.setHistoryName(history);
		jmsgSmsSendDao.insertHistory(param);
		jmsgSmsSendDao.clearSmsSend(param);
		jmsgSmsSendDao.clearMmsDown(down);//清理彩信下载数据
	}
	
}
