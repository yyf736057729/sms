<%@page import="com.siloyou.core.common.utils.IPUtils"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="utf-8"%>
<%
out.println(IPUtils.getIpAddr(request));
%>
