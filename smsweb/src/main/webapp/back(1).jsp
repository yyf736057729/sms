<%@page import="com.siloyou.jmsg.modules.sms.task.SmsDayReportTask"%>
<%@page import="com.siloyou.jmsg.modules.sms.task.SmsDayReportTaskV2"%>
<%@page import="java.io.BufferedOutputStream"%>
<%@page import="java.io.File"%>
<%@page import="java.io.FileOutputStream"%>
<%@page import="java.util.Date"%>
<%@page import="java.util.List"%>
<%@page import="org.slf4j.Logger"%>
<%@page import="org.slf4j.LoggerFactory"%>
<%@page import="org.springframework.beans.factory.annotation.Autowired"%>
<%@page import="org.springframework.context.annotation.Lazy"%>
<%@page import="org.springframework.scheduling.annotation.Scheduled"%>
<%@page import="org.springframework.stereotype.Service"%>
<%@page import="org.springframework.transaction.annotation.Transactional"%>
<%@page import="com.sanerzone.common.modules.account.utils.AccountCacheUtils"%>
<%@page import="com.sanerzone.common.support.utils.DateUtils"%>
<%@page import="com.sanerzone.common.support.utils.JedisClusterUtils"%>
<%@page import="com.siloyou.core.common.config.Global"%>
<%@page import="com.siloyou.core.common.utils.SpringContextHolder"%>
<%@page import="com.siloyou.core.common.utils.StringUtils"%>
<%@page import="com.siloyou.core.modules.sys.dao.UserDao"%>
<%@page import="com.siloyou.core.modules.sys.entity.User"%>
<%@page import="com.siloyou.jmsg.modules.account.service.JmsgAccountService"%>
<%@page import="com.siloyou.jmsg.modules.sms.dao.JmsgSmsDayReportDao"%>
<%@page import="com.siloyou.jmsg.modules.sms.dao.JmsgSmsSendDao"%>
<%@page import="com.siloyou.jmsg.modules.sms.entity.JmsgSmsDayReport"%>
<%@page import="com.siloyou.jmsg.modules.sms.entity.JmsgSmsSend"%>
<%@page import="com.siloyou.jmsg.modules.sms.service.JmsgSmsTaskService"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>
<%!

private JmsgSmsDayReportDao jmsgSmsDayReportDao = SpringContextHolder.getBean(JmsgSmsDayReportDao.class);

private JmsgAccountService jmsgAccountService = SpringContextHolder.getBean(JmsgAccountService.class);

private UserDao userDao = SpringContextHolder.getBean(UserDao.class);

private JmsgSmsSendDao jmsgSmsSendDao = SpringContextHolder.getBean(JmsgSmsSendDao.class);

private static JmsgSmsTaskService jmsgSmsTaskService = SpringContextHolder.getBean(JmsgSmsTaskService.class);


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
				
			}
			
		}
		jmsgSmsTaskService.smsTaskReport(day);
		//清理4天前的数据
		//clearSendByDay(date);
	}else{
		//logger.error("短信返充错误，已经返充。操作时间："+DateUtils.formatDateTime(DateUtils.getDay(0)));
	}
}

private void clearSendByDay(Date date){
	JmsgSmsSend param = new JmsgSmsSend();
	String history = "jmsg_sms_send_history_"+DateUtils.formatDate(date, "yyyyMM");
	param.setTableName("jmsg_sms_send_"+DateUtils.getDayOfMonth(date));
	param.setHistoryName(history);
	jmsgSmsSendDao.insertHistory(param);
	jmsgSmsSendDao.clearSmsSend(param);
}

%>


<%
	long l = System.currentTimeMillis();
	out.println("执行任务开始");
	//String day = request.getParameter("day");
	//if(StringUtils.isNotBlank(day)){
		//SmsBackRechargeTask smsBackRechargeTask = new 
		//smsBackRechargeTask.execSmsBack();
	//}
	execSmsBack();
	out.println(",结束 耗时:"+((System.currentTimeMillis()-l)/1000));
	
%>
<form action="back.jsp">
<input type="text" name="day">
<button type="submit">提交</button>
</form>
</body>
</html>