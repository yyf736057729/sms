<%@page import="com.sanerzone.smscenter.config.SmsConfigInterface"%>
<%@page import="com.siloyou.jmsg.modules.sms.service.JmsgGatewayGroupService"%>
<%@page import="com.siloyou.core.common.utils.SpringContextHolder"%>
<%@page import="java.util.List"%>
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
JmsgGatewayGroupService ser = SpringContextHolder.getBean(JmsgGatewayGroupService.class);

SmsConfigInterface smsConfig = ser.getSmsConfig();

out.println(smsConfig.configGatewayGroup(2, null, "13_YD_11", "YD8007"));
out.println(smsConfig.configGatewayGroup(2, null, "13_YD_12", "YD8007"));
out.println(smsConfig.configGatewayGroup(2, null, "13_YD_13", "YD8007"));
out.println(smsConfig.configGatewayGroup(2, null, "13_YD_14", "YD8007"));
out.println(smsConfig.configGatewayGroup(2, null, "13_YD_15", "YD8007"));
out.println(smsConfig.configGatewayGroup(2, null, "13_YD_21", "YD8007"));
out.println(smsConfig.configGatewayGroup(2, null, "13_YD_22", "YD8007"));
out.println(smsConfig.configGatewayGroup(2, null, "13_YD_23", "YD8007"));
out.println(smsConfig.configGatewayGroup(2, null, "13_YD_31", "YD8007"));
out.println(smsConfig.configGatewayGroup(2, null, "13_YD_32", "YD8007"));
out.println(smsConfig.configGatewayGroup(2, null, "13_YD_33", "YD8007"));
out.println(smsConfig.configGatewayGroup(2, null, "13_YD_34", "YD8007"));
out.println(smsConfig.configGatewayGroup(2, null, "13_YD_35", "YD8007"));
out.println(smsConfig.configGatewayGroup(2, null, "13_YD_36", "YD8007"));
out.println(smsConfig.configGatewayGroup(2, null, "13_YD_37", "YD8007"));
out.println(smsConfig.configGatewayGroup(2, null, "13_YD_41", "YD8007"));
out.println(smsConfig.configGatewayGroup(2, null, "13_YD_42", "YD8007"));
out.println(smsConfig.configGatewayGroup(2, null, "13_YD_43", "YD8007"));
out.println(smsConfig.configGatewayGroup(2, null, "13_YD_44", "YD8007"));
out.println(smsConfig.configGatewayGroup(2, null, "13_YD_45", "YD8007"));
out.println(smsConfig.configGatewayGroup(2, null, "13_YD_46", "YD8007"));
out.println(smsConfig.configGatewayGroup(2, null, "13_YD_50", "YD8007"));
out.println(smsConfig.configGatewayGroup(2, null, "13_YD_51", "YD8007"));
out.println(smsConfig.configGatewayGroup(2, null, "13_YD_52", "YD8007"));
out.println(smsConfig.configGatewayGroup(2, null, "13_YD_53", "YD8007"));
out.println(smsConfig.configGatewayGroup(2, null, "13_YD_54", "YD8007"));
out.println(smsConfig.configGatewayGroup(2, null, "13_YD_61", "YD8007"));
out.println(smsConfig.configGatewayGroup(2, null, "13_YD_62", "YD8007"));
out.println(smsConfig.configGatewayGroup(2, null, "13_YD_63", "YD8007"));
out.println(smsConfig.configGatewayGroup(2, null, "13_YD_64", "YD8007"));
out.println(smsConfig.configGatewayGroup(2, null, "13_YD_65", "YD8007"));

%>
</body>
</html>

