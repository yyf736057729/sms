<%@page import="java.io.Serializable"%>
<%@page import="java.util.concurrent.BlockingQueue"%>
<%@page import="com.siloyou.jmsg.common.storedMap.BDBStoredMapFactoryImpl"%>
<%@page import="com.siloyou.core.modules.sys.utils.UserUtils"%>
<%@page import="org.apache.commons.lang3.StringUtils"%>
<%@page import="com.siloyou.core.common.utils.IPUtils"%>
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
BlockingQueue<Serializable> queue = BDBStoredMapFactoryImpl.INS.getQueue("SMS", "SMSQUERY_" + request.getParameter("userid"));
out.println(queue.size());
%>
</body>
</html>

