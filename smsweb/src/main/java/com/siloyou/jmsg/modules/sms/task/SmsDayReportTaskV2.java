package com.siloyou.jmsg.modules.sms.task;

import java.util.List;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.sanerzone.common.support.utils.DateUtils;
import com.siloyou.core.common.utils.SpringContextHolder;
import com.siloyou.core.modules.sys.dao.UserDao;
import com.siloyou.core.modules.sys.entity.User;
import com.siloyou.core.modules.sys.utils.UserUtils;
import com.siloyou.jmsg.modules.account.entity.JmsgAccount;
import com.siloyou.jmsg.modules.account.service.JmsgAccountService;
import com.siloyou.jmsg.modules.sms.dao.JmsgSmsDayReportDao;
import com.siloyou.jmsg.modules.sms.entity.JmsgSmsDayReport;

//定时任务 短信日报表
@Service
@Lazy(false)
public class SmsDayReportTaskV2 {
	
	public static Logger logger = LoggerFactory.getLogger(SmsDayReportTaskV2.class);
	
	private static JmsgSmsDayReportDao jmsgSmsDayReportDao = SpringContextHolder.getBean(JmsgSmsDayReportDao.class);
	
	private static JmsgAccountService jmsgAccountService = SpringContextHolder.getBean(JmsgAccountService.class);
	
	private static final int BATCH_COMMIT_MAX_COUNT = 200;//批量提交默认值
	
	private static UserDao userDao = SpringContextHolder.getBean(UserDao.class);
	
	//每5分钟统计一次日报表
	@Scheduled(fixedDelay=300000)
	public void execSmsAllDayReport(){
		logger.info("进入报表定时任务:短信日报表");
		saveSmsDayReport(false,0);
	}
	
	public void execSmsAllDayReport(int day){
		logger.info("指定特定日期生成报表");
		saveSmsDayReport(false, day);
	}
	
	//每天 3:30:35执行任务
	@Scheduled(cron = "35 30 3 * * ?")
	@Transactional(readOnly = false)
	public void execDayReport(){
		saveSmsDayReport(false,-3);
		saveSmsDayReport(false,-2);
	}
	
	/**
	 * 
	 * @param backFlag true 返充
	 * @param day 获取当前日期  负数表示N天前  正数表示N天后 0：表示当天
	 */
	@Transactional(readOnly = false)
	public void saveSmsDayReport(boolean backFlag,int day){
		SqlSession sqlSession = SpringContextHolder.getBean(SqlSessionFactory.class).openSession(ExecutorType.BATCH, false);
		try{
			List<JmsgSmsDayReport> result = getDaySendList(backFlag, day);
			if(result != null && result.size() > 0){
				int i=0;
				for (JmsgSmsDayReport resultEntity : result) {
					if(resultEntity.getGatewayId().equals("")){
						resultEntity.setGatewayId("0");
					}
					i++;
					sqlSession.insert("com.siloyou.jmsg.modules.sms.dao.JmsgSmsDayReportDao.batchInsert", resultEntity);//批量提交
					if(i % BATCH_COMMIT_MAX_COUNT == 0){
						sqlSession.commit();
					}
				}
				sqlSession.commit();
			}
		}catch(Exception e){
			logger.error("日报表统计失败:{}", e);
		}finally{
			if(sqlSession != null)sqlSession.close();
		}
	}
	
	public List<JmsgSmsDayReport> getDaySendList(boolean backFlag,int day){
		List<JmsgSmsDayReport> result = Lists.newArrayList();
		String tableName = "jmsg_sms_send_"+DateUtils.getDayOfMonth(day);
		JmsgSmsDayReport param = new JmsgSmsDayReport();
		param.setTableName(tableName);
		List<JmsgAccount> accountList = jmsgAccountService.findList(new JmsgAccount());
		if(accountList != null && accountList.size() >0){
			for (JmsgAccount jmsgAccount : accountList) {
				param.setUserId(jmsgAccount.getUser().getId());
				result.addAll(getDaySendList(backFlag, param));
			}
		}
		return result;
	}
	
	
	public List<JmsgSmsDayReport> getDaySendList(boolean flag,JmsgSmsDayReport param){
		List<JmsgSmsDayReport> result = Lists.newArrayList();
		List<JmsgSmsDayReport> smsSendList = jmsgSmsDayReportDao.findSendListByDayV3(param);
		if(smsSendList != null && smsSendList.size() >0){
			for (JmsgSmsDayReport entity : smsSendList) {
				int failCount=0;
				int successCount = entity.getSuccessCount();//发送成功量
				int reportSuccessCount = entity.getReportSuccessCount();//状态报告成功量
				int sendFailCount = entity.getSendFailCount();//发送失败量
				int reportFailCount = entity.getReportFailCount();//状态报告失败量
				int reportNullCount = entity.getReportNullCount();//状态空量
				int submitFailCount = entity.getSubmitFailCount();//提交失败量
				int userCount = entity.getUserCount();//用户计费
				int userBackCount = 0;
				int backCount = 0;
				int submitSuccessCount = 0;
				submitSuccessCount = successCount - submitFailCount;//提交成功量 = 提交总量-提交失败量
				/**
				if("0".equals(entity.getPayType())){//按提交
					userCount = successCount;
				}else if("2".equals(entity.getPayType())){//按状态报告
					userCount = reportSuccessCount+reportNullCount;//按状态报告计费：用户成功量=状态成功量+状态空量
				}**/
				if(flag){
					userBackCount=entity.getSendCount()-userCount;
					User parentUser = userDao.getAgency(entity.getCompanyId());//代理商
					String curPayMode = UserUtils.getPayMode(entity.getUserId(), "sms");//当前用户扣费类型
					if(parentUser != null){
						if("2".equals(parentUser.getPayMode())){//按网关状态
							if("0".equals(curPayMode)){//按提交
								backCount = userCount - (reportSuccessCount+reportNullCount);
								if(backCount <0)backCount=0;
							}
						}
					}
				}
				failCount = sendFailCount+reportFailCount;//失败量 = 发送失败量+状态失败量
				entity.setUserBackCount(userBackCount);
				entity.setBackCount(backCount);
				entity.setFailCount(failCount);
				entity.setSubmitSuccessCount(submitSuccessCount);
				entity.setSuccessCount(successCount);//发送成功量
				entity.setBackFlag("0");
				result.add(entity);
			}
		}
		return result;
	}
	
}
