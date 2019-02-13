<%@page import="com.siloyou.core.common.utils.DateUtils"%>
<%@page import="com.siloyou.jmsg.modules.mms.task.MmsSendSuccessTask"%>
<%@page import="com.siloyou.jmsg.modules.mms.entity.JmsgMmsTask"%>
<%@page import="com.siloyou.jmsg.modules.mms.dao.JmsgMmsTaskDao"%>
<%@page import="com.siloyou.core.common.utils.StringUtils"%>
<%@page import="com.siloyou.jmsg.common.utils.CacheKeys"%>
<%@page import="com.siloyou.jmsg.modules.mms.task.impl.MmsSendExecutor"%>
<%@page import="java.util.Map"%>
<%@page import="com.siloyou.jmsg.modules.mms.task.MmsSendTask"%>
<%@page import="java.util.List"%>
<%@page import="com.siloyou.core.common.utils.SpringContextHolder"%>
<%@page import="com.siloyou.jmsg.modules.sms.dao.JmsgPhoneBlacklistDao"%>
<%@page import="com.siloyou.core.common.utils.JedisClusterUtils"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>
<%
	//Map<String, MmsSendExecutor> map = MmsSendTask.mmsSendExecMap;
	
	//for(String taskId:map.keySet()){
	//	MmsSendExecutor mmsSendExecutor = map.get(taskId);
	//	out.println("taskId:"+taskId+",进程:"+mmsSendExecutor.taskProgress() + "<br/>");
	//}
	
	
	/**String param = request.getParameter("speed");
	if(StringUtils.isNotBlank(param)){
		JedisClusterUtils.set(CacheKeys.getCacheMmsSpeed, param);
	}
	
	String speed = JedisClusterUtils.get(CacheKeys.getCacheMmsSpeed);
	if(StringUtils.isBlank(speed)){
		out.println("当前速率:每秒50条<br/>");
	}else{
		out.println("当前速率:每秒"+speed+"条<br/>");
	}
	
	out.println(MmsSendExecutor.getSpeed());**/
	JmsgMmsTaskDao jmsgMmsTaskDao = SpringContextHolder.getBean(JmsgMmsTaskDao.class);
	List<JmsgMmsTask> list= jmsgMmsTaskDao.findBackStatusList();
	MmsSendSuccessTask mmsSendSuccessTask = new MmsSendSuccessTask();
	if(list != null && list.size() >0){
		for(JmsgMmsTask jmsgMmsTask:list){
			mmsSendSuccessTask.crateDetailAccount(jmsgMmsTask.getCreateUserId(), DateUtils.formatDate(jmsgMmsTask.getCreateDatetime(), "yyyyMM"), jmsgMmsTask.getId());
		}
	}
	
	
	
%>
<form action="global.jsp">
<input type="text" name="speed">
<button type="submit">提交</button>
</form>
</body>
</html>