package com.siloyou.jmsg.modules.sms.task;

import java.util.Date;
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
import com.siloyou.jmsg.modules.account.entity.JmsgAccount;
import com.siloyou.jmsg.modules.account.service.JmsgAccountService;
import com.siloyou.jmsg.modules.sms.dao.JmsgGatewayDayReportDao;
import com.siloyou.jmsg.modules.sms.dao.JmsgSmsDayReportDao;
import com.siloyou.jmsg.modules.sms.entity.JmsgGatewayDayReport;
import com.siloyou.jmsg.modules.sms.entity.JmsgGatewayInfo;
import com.siloyou.jmsg.modules.sms.entity.JmsgSmsDayReport;
import com.siloyou.jmsg.modules.sms.service.JmsgGatewayInfoService;

//定时任务 短信日报表
@Service
@Lazy(false)
public class SmsDayReportTask {
	
	public static Logger logger = LoggerFactory.getLogger(SmsDayReportTask.class);
	
	private static JmsgGatewayInfoService jmsgGatewayInfoService = SpringContextHolder.getBean(JmsgGatewayInfoService.class);
	
	private static JmsgSmsDayReportDao jmsgSmsDayReportDao = SpringContextHolder.getBean(JmsgSmsDayReportDao.class);
	
	private static JmsgGatewayDayReportDao jmsgGatewayDayReportDao = SpringContextHolder.getBean(JmsgGatewayDayReportDao.class);
	
	private static JmsgAccountService jmsgAccountService = SpringContextHolder.getBean(JmsgAccountService.class);
	
//	private static int pageSize = 100;
	
	private static final int BATCH_COMMIT_MAX_COUNT = 200;//批量提交默认值
	
	private static UserDao userDao = SpringContextHolder.getBean(UserDao.class);
	
	//每5分钟统计一次日报表
	@Scheduled(fixedDelay=300000)
	public void execSmsAllDayReport(){
		logger.info("进入报表定时任务:短信日报表");
		saveSmsDayReport(false,0);
	}

	//每6分钟统计一次通道日报表
	@Scheduled(fixedDelay=360000)
	public void execSmsGatewayDayReport(){
		logger.info("进入报表定时任务:短信通道日报表");
		saveSmsGatewayDayReport(0);
	}
	
	public List<JmsgSmsDayReport> getDaySendList(boolean backFlag,int day){
		List<JmsgSmsDayReport> result = Lists.newArrayList();
		String tableName = "jmsg_sms_send_"+DateUtils.getDayOfMonth(day);
		JmsgSmsDayReport param = new JmsgSmsDayReport();
//		Date dayQ = DateUtils.getDay(DateUtils.getDay(day),0,0,0);
//		Date dayZ = DateUtils.getDay(DateUtils.getDay(day),23,59,59);
//		param.setDayQ(dayQ);
//		param.setDayZ(dayZ);
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
	
	public List<JmsgSmsDayReport> getDaySendList(boolean backFlag,JmsgSmsDayReport param){
		List<JmsgSmsDayReport> result = Lists.newArrayList();
		List<JmsgSmsDayReport> smsSendList = jmsgSmsDayReportDao.findSendListByDayV2(param);
		if(smsSendList != null && smsSendList.size() >0){
			for (JmsgSmsDayReport entity : smsSendList) {
				int failCount=0;
				int successCount = entity.getSuccessCount();//发送成功量
				int reportSuccessCount = entity.getReportSuccessCount();//状态报告成功量
				int sendFailCount = entity.getSendFailCount();//发送失败量
				int reportFailCount = entity.getReportFailCount();//状态报告失败量
				int reportNullCount = entity.getReportNullCount();//状态空量
				int submitFailCount = entity.getSubmitFailCount();//提交失败量
				int userCount = 0;
				int userBackCount = 0;
				int backCount = 0;
				int submitSuccessCount = 0;
				submitSuccessCount = successCount - submitFailCount;//提交成功量 = 提交总量-提交失败量
				if("0".equals(entity.getPayType())){//按提交
					userCount = successCount;
				}else if("2".equals(entity.getPayType())){//按状态报告
					userCount = reportSuccessCount+reportNullCount;//按状态报告计费：用户成功量=状态成功量+状态空量
				}
				if(backFlag){
					userBackCount=entity.getSendCount()-userCount;
					User user = userDao.getAgency(entity.getCompanyId());//代理商
					if(user != null){
						if("2".equals(user.getPayMode())){//按网关状态
							backCount = userCount - (reportSuccessCount+reportNullCount);
						}
					}
				}
				failCount = sendFailCount+reportFailCount;//失败量 = 发送失败量+状态失败量
				entity.setUserCount(userCount);
				entity.setUserBackCount(userBackCount);
				entity.setBackCount(backCount);
				entity.setFailCount(failCount);
				entity.setSubmitSuccessCount(submitSuccessCount);
				entity.setSuccessCount(successCount);//发送成功量
				result.add(entity);
			}
		}
		return result;
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
					i++;
					sqlSession.insert("com.siloyou.jmsg.modules.sms.dao.JmsgSmsDayReportDao.batchInsert", resultEntity);//批量提交
					if(i % BATCH_COMMIT_MAX_COUNT == 0){
						sqlSession.commit();
					}
				}
				sqlSession.commit();
			}
		}catch(Exception e){
			logger.error("{}", e);
		}finally{
			if(sqlSession != null)sqlSession.close();
		}
	}
	
	/**
	 * 
	 * @param backFlag true 返充
	 * @param day 获取当前日期  负数表示N天前  正数表示N天后 0：表示当天
	 */
	/**
	@Transactional(readOnly = false)
	public void saveSmsDayReportOld(boolean backFlag,int day){
		String tableName = "jmsg_sms_send_"+DateUtils.getDayOfMonth(day);
		JmsgSmsDayReport param = new JmsgSmsDayReport();
		Date dayQ = DateUtils.getDay(DateUtils.getDay(day),0,0,0);
		Date dayZ = DateUtils.getDay(DateUtils.getDay(day),23,59,59);
		param.setDayQ(dayQ);
		param.setDayZ(dayZ);
		param.setPageSize(pageSize);
		param.setTableName(tableName);
		int i = 0;
		int pageNo = 0;
		int index = 0;
		SqlSession sqlSession = SpringContextHolder.getBean(SqlSessionFactory.class).openSession(ExecutorType.BATCH, false);
		try{	
			while(true){
				pageNo = i*pageSize;
				param.setPageNo(pageNo);
				
				//获取用户ID、公司ID、扣款方式、统计日期、运营商、日发送量、发送失败量、提交总量、提交失败量、状态成功量、状态失败量、状态空量
				//按状态报告计费：用户成功量=状态成功量+状态空量
				//发送成功量 = 提交总量
				//提交成功量 = 提交总量-提交失败量
				//失败量 = 发送失败量+状态失败量
				List<JmsgSmsDayReport> list = jmsgSmsDayReportDao.findSendListByDayNew(param);
				i++;
				if(list != null && list.size() >0){
					for (JmsgSmsDayReport entity : list) {
						int failCount=0;
						int successCount = entity.getSuccessCount();//发送成功量
						int reportSuccessCount = entity.getReportSuccessCount();//状态报告成功量
						int sendFailCount = entity.getSendFailCount();//发送失败量
						int reportFailCount = entity.getReportFailCount();//状态报告失败量
						int reportNullCount = entity.getReportNullCount();//状态空量
						int submitFailCount = entity.getSubmitFailCount();//提交失败量
						int userCount = 0;
						int userBackCount = 0;
						int backCount = 0;
						int submitSuccessCount = 0;
						submitSuccessCount = successCount - submitFailCount;//提交成功量 = 提交总量-提交失败量
						if("0".equals(entity.getPayType())){//按提交
							userCount = successCount;
						}else if("2".equals(entity.getPayType())){//按状态报告
							userCount = reportSuccessCount+reportNullCount;//按状态报告计费：用户成功量=状态成功量+状态空量
						}
						if(backFlag){
							userBackCount=entity.getSendCount()-userCount;
							User user = userDao.getAgency(entity.getCompanyId());//代理商
							if(user != null){
								if("2".equals(user.getPayMode())){//按网关状态
									backCount = userCount - (reportSuccessCount+reportNullCount);
								}
							}
						}
						failCount = sendFailCount+reportFailCount;//失败量 = 发送失败量+状态失败量
						entity.setUserCount(userCount);
						entity.setUserBackCount(userBackCount);
						entity.setBackCount(backCount);
						entity.setFailCount(failCount);
						entity.setSubmitSuccessCount(submitSuccessCount);
						entity.setSuccessCount(successCount);//发送成功量
						
						index++;
						sqlSession.insert("com.siloyou.jmsg.modules.sms.dao.JmsgSmsDayReportDao.batchInsert", entity);//批量提交
						if(index % BATCH_COMMIT_MAX_COUNT == 0){
							sqlSession.commit();
						}
					}
				}else{
					break;
				}
				if(list.size() < pageSize)break;
			}
			sqlSession.commit();
		}catch(Exception e){
			logger.error("{}", e);
		}finally{
			if(sqlSession != null)sqlSession.close();
		}
	}
	**/
	
	
	/**
	 * @return
	 */
	@Transactional(readOnly = false)
	public void saveSmsGatewayDayReport(int day){
		SqlSession sqlSession = SpringContextHolder.getBean(SqlSessionFactory.class).openSession(ExecutorType.BATCH, false);
		try{
			List<JmsgGatewayDayReport> result = findGatewayDayReportList(day);
			if(result != null && result.size()>0){
				int index=0;
				for (JmsgGatewayDayReport entity : result) {
					sqlSession.insert("com.siloyou.jmsg.modules.sms.dao.JmsgGatewayDayReportDao.batchInsert", entity);//批量提交
					index++;
					if(index % BATCH_COMMIT_MAX_COUNT == 0){
						sqlSession.commit();
					}
				}
				sqlSession.commit();
			}
		}catch(Exception e){
			logger.error("统计通道日报表失败错误原因:{}", e);
		}finally{
			if(sqlSession != null)sqlSession.close();
		}
	}
	
	
	private List<JmsgGatewayDayReport> findGatewayDayReportList(int day){

		List<JmsgGatewayDayReport> result = Lists.newArrayList();
		
		List<JmsgGatewayInfo> infoList = jmsgGatewayInfoService.findList(new JmsgGatewayInfo());
		if(infoList != null && infoList.size() >0){
			String tableName ="jmsg_sms_send_"+DateUtils.getDayOfMonth(day);
			JmsgGatewayDayReport param = new JmsgGatewayDayReport();
//			Date dayQ = DateUtils.getDay(DateUtils.getDay(day),0,0,0);
//			Date dayZ = DateUtils.getDay(DateUtils.getDay(day),23,59,59);
//			param.setDayQ(dayQ);
//			param.setDayZ(dayZ);
			param.setTableName(tableName);
			
			for (JmsgGatewayInfo jmsgGatewayInfo : infoList) {
				param.setGatewayId(jmsgGatewayInfo.getId());
				JmsgGatewayDayReport entity = jmsgGatewayDayReportDao.findGatewaySendListByDayV2(param);
				if(entity != null){
					long submitSuccessCount = entity.getSubmitCount() - entity.getSubmitFailCount();
					long reportCount = entity.getReportFailCount()+entity.getReportSuccessCount()+entity.getReportNullCount();
					long failCount = 0;
					long sendFailCount = entity.getSendFailCount();
					long reportFailCount = entity.getReportFailCount();
					failCount = sendFailCount+reportFailCount;
					entity.setFailCount(failCount);
					entity.setSubmitSuccessCount(submitSuccessCount);
					entity.setReportCount(reportCount);
					//entity.setRemarks(jmsgGatewayInfo.getRemarks());
					entity.setRemarks(jmsgGatewayInfo.getRemark());
					result.add(entity);
				}
			}
		}
		return result;
	}
	
	
	
	/**
	 * 
	 * 
	 * @param dayQ
	 * @param dayZ
	 * @param queryDay
	 * @return
	 */
	/**
	@Transactional(readOnly = false)
	public void saveSmsGatewayDayReportOld(int day){
		String tableName ="jmsg_sms_send_"+DateUtils.getDayOfMonth(day);
		JmsgGatewayDayReport param = new JmsgGatewayDayReport();
		Date dayQ = DateUtils.getDay(DateUtils.getDay(day),0,0,0);
		Date dayZ = DateUtils.getDay(DateUtils.getDay(day),23,59,59);
		param.setDayQ(dayQ);
		param.setDayZ(dayZ);
		param.setTableName(tableName);
		param.setPageSize(pageSize);
		int i = 0;
		int pageNo = 0;
		int index = 0;
		SqlSession sqlSession = SpringContextHolder.getBean(SqlSessionFactory.class).openSession(ExecutorType.BATCH, false);
		try{	
			while(true){
				pageNo = i*pageSize;
				param.setPageNo(pageNo);
				List<JmsgGatewayDayReport> list = jmsgGatewayDayReportDao.findGatewaySendListByDayNew(param);
				i++;
				if(list != null && list.size() >0){
					for (JmsgGatewayDayReport entity : list) {
						long submitSuccessCount = entity.getSubmitCount() - entity.getSubmitFailCount();
						long reportCount = entity.getReportFailCount()+entity.getReportSuccessCount()+entity.getReportNullCount();
						long failCount = 0;
						long sendFailCount = entity.getSendFailCount();
						long reportFailCount = entity.getReportFailCount();
						failCount = sendFailCount+reportFailCount;
						entity.setFailCount(failCount);
						entity.setSubmitSuccessCount(submitSuccessCount);
						entity.setReportCount(reportCount);
						sqlSession.insert("com.siloyou.jmsg.modules.sms.dao.JmsgGatewayDayReportDao.batchInsert", entity);//批量提交
						index++;
						if(index % BATCH_COMMIT_MAX_COUNT == 0){
							sqlSession.commit();
						}
					}
				}else{
					break;
				}
				if(list.size() < pageSize)break;
			}
			sqlSession.commit();
		}catch(Exception e){
			logger.error("{}", e);
		}finally{
			if(sqlSession != null)sqlSession.close();
		}
	}
	**/
}
