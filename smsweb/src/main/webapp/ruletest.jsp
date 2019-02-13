<%@page import="com.siloyou.jmsg.common.utils.RuleUtils"%>
<%@page import="com.siloyou.core.common.mapper.JsonMapper"%>
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
	RuleUtils.initRule();
	out.println(RuleUtils.ruleMap.size());
	out.println(JsonMapper.toJsonString(RuleUtils.ruleMap.get("rule_"+request.getParameter("rid"))));
%>
</body>
</html>

