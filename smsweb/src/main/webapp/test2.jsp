<%@page import="com.siloyou.core.common.utils.SpringContextHolder"%>
<%@page import="com.siloyou.jmsg.common.utils.MQUtils"%>
<%@page import="com.siloyou.jmsg.common.message.SmsMtMessage"%>
<%@page import="com.alibaba.rocketmq.client.impl.MQAdminImpl"%>
<%@page import="com.siloyou.core.common.mapper.JsonMapper"%>
<%@page import="com.siloyou.jmsg.common.message.SmsMoMessage"%>
<%@page import="com.siloyou.core.common.utils.FstObjectSerializeUtil"%>
<%@page import="com.alibaba.rocketmq.client.QueryResult"%>
<%@page import="com.alibaba.rocketmq.client.exception.MQClientException"%>
<%@page import="com.alibaba.rocketmq.client.producer.DefaultMQProducer"%>
<%@page import="com.siloyou.jmsg.common.mq.MQProducerFactory"%>
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
MQUtils mQUtils = SpringContextHolder.getBean(MQUtils.class);
DefaultMQProducer smsMTProducer = mQUtils.geSmsCenterProducer();
try {
	QueryResult result = smsMTProducer.queryMessage(request.getParameter("topic"), request.getParameter("msgid"), 64, 0, System.currentTimeMillis());
	MQAdminImpl imp = null;
	SmsMtMessage moMsg = (SmsMtMessage) FstObjectSerializeUtil.read(result.getMessageList().get(0).getBody());
	
	out.println(JsonMapper.toJsonString(moMsg));
	
} catch (MQClientException e) {
	e.printStackTrace();
}
%>
</body>
</html>

