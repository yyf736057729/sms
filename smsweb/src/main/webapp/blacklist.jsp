<%@page import="com.siloyou.jmsg.common.utils.BlacklistUtils"%>
<%@page import="com.sanerzone.smscenter.config.PhoneConfigInterface"%>
<%@page import="com.siloyou.core.common.utils.SpringContextHolder"%>
<%@page import="java.io.FileReader"%>
<%@page import="java.io.BufferedReader"%>
<%@page import="java.io.FileInputStream"%>
<%@page import="java.io.InputStreamReader"%>
<%@page import="java.io.File"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>
<% 
	PhoneConfigInterface phoneConfig = SpringContextHolder.getBean("phoneConfigInterface");
	/* 读入TXT文件 */ 
	try{
		String pathname = "phone.txt"; // 绝对路径或相对路径都可以，这里是绝对路径，写入文件时演示相对路径  
		FileReader fr=new FileReader(pathname);
		BufferedReader br = new BufferedReader(fr); // 建立一个对象，它把文件内容转成计算机能读懂的语言  
		String phone = null;
		String[] tmp;
		int i = 0;
		while((phone = br.readLine()) != null) {  
			i++;
			BlacklistUtils.put(phone, 1, 1);
			tmp = new String[]{phone}; 
			phoneConfig.configBlacklist(3,"",1,tmp);
		}
		out.println(i);
	}catch(Exception e){
		e.printStackTrace();
	}
%>
</body>
</html>