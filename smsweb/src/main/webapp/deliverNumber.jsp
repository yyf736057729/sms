<%@page import="com.siloyou.jmsg.common.utils.GatewayUtils"%>
<%@page import="com.siloyou.jmsg.modules.sms.service.JmsgSmsTaskService"%>
<%@page import="com.siloyou.jmsg.common.utils.SignUtils"%>
<%@page import="com.siloyou.core.common.mapper.JsonMapper"%>
<%@page import="com.siloyou.jmsg.modules.sms.entity.GatewayResult"%>
<%@page import="java.util.Map"%>
<%@page import="com.siloyou.core.common.utils.StringUtils"%>
<%@page import="java.util.List"%>
<%@page import="com.siloyou.core.common.utils.SpringContextHolder"%>
<%@page import="com.siloyou.jmsg.modules.sms.dao.JmsgPhoneBlacklistDao"%>
<%@page import="com.siloyou.core.common.utils.JedisClusterUtils"%>

<%@page import="com.siloyou.jmsg.modules.sms.dao.JmsgDeliverNumberDao"%>
<%@page import="com.siloyou.jmsg.modules.sms.entity.JmsgDeliverNumber"%>

<%@page import="com.siloyou.core.modules.sys.utils.UserUtils"%>
<%@page import="com.siloyou.core.modules.sys.entity.User"%>
<%@page import="com.siloyou.jmsg.common.utils.PhoneUtils"%>
<%@page import="com.siloyou.jmsg.common.utils.SignUtils"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>
<%
	JmsgDeliverNumberDao jmsgDeliverNumberDao = SpringContextHolder.getBean(JmsgDeliverNumberDao.class);
	List<JmsgDeliverNumber> list = jmsgDeliverNumberDao.findAllList(new JmsgDeliverNumber());
	
	for (JmsgDeliverNumber param : list)
	{
		JedisClusterUtils.hset("MONUM", param.getSpNumber(), param.getUser().getId());
		out.println("sp_number : " + param.getSpNumber() + "       -       user_id : " + param.getUser().getId() + "<br/>");
	}
%>
<form action="deliverNumber.jsp">
<button type="submit">提交</button>
</form>
</body>
</html>

