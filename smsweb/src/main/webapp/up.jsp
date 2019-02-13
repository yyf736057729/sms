<%@page import="org.apache.commons.lang3.StringUtils"%>
<%@page import="java.io.IOException"%>
<%@page import="com.siloyou.core.common.utils.StreamUtils"%>
<%@page
	import="com.siloyou.jmsg.modules.biz.service.BizRegistryDataService"%>
<%@page import="com.siloyou.core.common.utils.SpringContextHolder"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="utf-8"%>
	<%
		try{
			String charEncoding = request.getCharacterEncoding();
			if (charEncoding == null) {
				charEncoding = "UTF-8";
			}
			String json = StreamUtils.InputStreamTOString(request.getInputStream(), charEncoding);
			if(StringUtils.isNotBlank(json)) {
				BizRegistryDataService bizRegistryDataService = SpringContextHolder.getBean(BizRegistryDataService.class);
				bizRegistryDataService.sendActitiyCode(json);
				out.println("ok");
			}else {
				out.println("null");
			}
		}catch(IOException e) {
			
		}
		
	%>