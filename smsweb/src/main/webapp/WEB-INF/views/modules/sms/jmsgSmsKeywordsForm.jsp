<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>敏感词管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			//$("#name").focus();
			$("#inputForm").validate({
				rules: {
					keywords: {remote: "${ctx}/sms/jmsgSmsKeywords/checkKeywords?oldKeywords=" + encodeURIComponent('${jmsgSmsKeywords.keywords}')}
				},
				messages: {
					keywords: {remote: "敏感词已存在"},
				},
				submitHandler: function(form){
					loading('正在提交，请稍等...');
					form.submit();
				},
				errorContainer: "#messageBox",
				errorPlacement: function(error, element) {
					$("#messageBox").text("输入有误，请先更正。");
					if (element.is(":checkbox")||element.is(":radio")||element.parent().is(".input-append")){
						error.appendTo(element.parent().parent());
					} else {
						error.insertAfter(element);
					}
				}
			});
		});
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/sms/jmsgSmsKeywords/">信息列表</a></li>
		<li class="active"><a href="${ctx}/sms/jmsgSmsKeywords/form?id=${jmsgSmsKeywords.id}">信息<shiro:hasPermission name="sms:jmsgSmsKeywords:edit">${not empty jmsgSmsKeywords.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="sms:jmsgSmsKeywords:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="jmsgSmsKeywords" action="${ctx}/sms/jmsgSmsKeywords/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>		
		<div class="control-group">
			<label class="control-label">敏感词：</label>
			<div class="controls">
				<input id="oldKeywords" name="oldKeywords" type="hidden" value="${jmsgSmsKeywords.keywords}">
				<form:input path="keywords" htmlEscape="false" maxlength="256" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<!-- <div class="control-group">
			<label class="control-label">范围 0：全局 1：用户：</label>
			<div class="controls">
				<form:select path="scope" class="input-xlarge required">
					<form:option value="" label=""/>
					<form:options items="${fns:getDictList('scope_type')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div> -->
		<div class="form-actions">
			<shiro:hasPermission name="sms:jmsgSmsKeywords:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>