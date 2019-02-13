<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>退订</title>
<script type="text/javascript">
function onSubmit(){
	var phone = document.getElementById("phoneId").value;
	var reg =  /^(1[3-8])\d{9}$/;
	if(!reg.test(phone)){
		alert("请输入正确手机号码");
		return;
	}
	document.getElementById("inputForm").submit();
}
</script>
</head>
<body>
	<br/>
	<form:form id="inputForm" modelAttribute="jmsgPhoneDynamic" action="${pageContext.request.contextPath}${fns:getApiPath()}/mms/unsubscribe" method="post" class="form-horizontal">
		<sys:message content="${message}"/>		
		手机号码：<input name="phone" maxlength="20" class="input-xlarge" id="phoneId">
		<input id="btnSubmit" class="btn btn-primary" type="button" value="提交"  ${flag eq 1 ? 'disabled="disabled"' : ''} onclick="javascript:onSubmit();"/>
	</form:form>
</body>
</html>