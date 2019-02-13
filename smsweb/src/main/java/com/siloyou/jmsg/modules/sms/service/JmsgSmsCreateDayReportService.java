/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.jmsg.modules.sms.service;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.siloyou.core.common.utils.DateUtils;
import com.siloyou.core.common.utils.SpringContextHolder;
import com.siloyou.core.modules.sys.dao.UserDao;
import com.siloyou.core.modules.sys.entity.User;
import com.siloyou.core.modules.sys.utils.UserUtils;
import com.siloyou.jmsg.modules.account.entity.JmsgAccount;
import com.siloyou.jmsg.modules.account.service.JmsgAccountService;
import com.siloyou.jmsg.modules.sms.dao.JmsgSmsDayReportDao;
import com.siloyou.jmsg.modules.sms.entity.JmsgSmsDayReport;


/**
 * 生成日报表
 * @author zhukc
 * @version 2017-06-23
 */
@Service
@Transactional(readOnly = true)
public class JmsgSmsCreateDayReportService{
	
	private static Logger logger = LoggerFactory.getLogger(JmsgSmsCreateDayReportService.class);
	
	private static final int BATCH_COMMIT_MAX_COUNT = 200;//批量提交默认值
	
	@Autowired
	private JmsgAccountService jmsgAccountService;
	
	@Autowired
	private JmsgSmsDayReportDao jmsgSmsDayReportDao;
	
	@Autowired
	private UserDao userDao;
	
	@Transactional(readOnly = false)
	public void createDayReport(Date day,boolean flag){
		String tableIndex = DateUtils.getTableIndex(day);
		String tableName = "jmsg_sms_send_"+tableIndex;
		if(tableIndex.startsWith("history_")){
			flag = true;
		}
		saveSmsDayReport(flag, tableName, day);
	}
	
	
	@Transactional(readOnly = false)
	public void saveSmsDayReport(boolean flag,String tableName,Date day){
		SqlSession sqlSession = SpringContextHolder.getBean(SqlSessionFactory.class).openSession(ExecutorType.BATCH, false);
		try{
			List<JmsgSmsDayReport> result = getDaySendList(flag, tableName, day);
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
			logger.error("生成日报表失败:{}", e);
		}finally{
			if(sqlSession != null)sqlSession.close();
		}
	}
	
	public List<JmsgSmsDayReport> getDaySendList(boolean flag, String tableName, Date day){
		List<JmsgSmsDayReport> result = Lists.newArrayList();
		JmsgSmsDayReport param = new JmsgSmsDayReport();
		param.setTableName(tableName);
		param.setDay(day);
		List<JmsgAccount> accountList = jmsgAccountService.findList(new JmsgAccount());
		if(accountList != null && accountList.size() >0){
			for (JmsgAccount jmsgAccount : accountList) {
				param.setUserId(jmsgAccount.getUser().getId());
				result.addAll(getDaySendList(flag, param));
			}
		}
		return result;
	}
	
	public List<JmsgSmsDayReport> getDaySendList(boolean flag,JmsgSmsDayReport param){
		List<JmsgSmsDayReport> result = Lists.newArrayList();
		List<JmsgSmsDayReport> smsSendList = jmsgSmsDayReportDao.findCreateDayReportList(param);
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
				String backFlag = "0";
				submitSuccessCount = successCount - submitFailCount;//提交成功量 = 提交总量-提交失败量
				if(flag){
					userBackCount=entity.getSendCount()-userCount;
					User parentUser = userDao.getAgency(entity.getCompanyId());//代理商
					String curPayMode = UserUtils.getPayMode(entity.getUser().getId(), "sms");//当前用户扣费类型
					if(parentUser != null){
						if("2".equals(parentUser.getPayMode())){//按网关状态
							if("0".equals(curPayMode)){//按提交
								backCount = userCount - (reportSuccessCount+reportNullCount);
								if(backCount <0)backCount=0;
							}
						}
					}
					backFlag="1";
				}
				failCount = sendFailCount+reportFailCount;//失败量 = 发送失败量+状态失败量
				entity.setUserBackCount(userBackCount);
				entity.setBackCount(backCount);
				entity.setFailCount(failCount);
				entity.setSubmitSuccessCount(submitSuccessCount);
				entity.setSuccessCount(successCount);//发送成功量
				entity.setBackFlag(backFlag);
				result.add(entity);
			}
		}
		return result;
	}
	
}