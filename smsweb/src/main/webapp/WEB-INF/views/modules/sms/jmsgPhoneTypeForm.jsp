<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>号码运营商管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			//$("#name").focus();
			$("#inputForm").validate({
				rules: {
					num: {remote: "${ctx}/sms/jmsgPhoneType/checkNum?oldNum=" + encodeURIComponent('${jmsgPhoneType.num}')}
				},
				messages: {
					num: {remote: "号段已存在"},
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
		<li><a href="${ctx}/sms/jmsgPhoneType/">信息列表</a></li>
		<li class="active"><a href="${ctx}/sms/jmsgPhoneType/form?id=${jmsgPhoneType.id}">信息<shiro:hasPermission name="sms:jmsgPhoneType:edit">${not empty jmsgPhoneType.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="sms:jmsgPhoneType:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="jmsgPhoneType" action="${ctx}/sms/jmsgPhoneType/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>		
		<div class="control-group">
			<label class="control-label">号段：</label>
			<div class="controls">
				<input id="oldNum" name="oldNum" type="hidden" value="${jmsgPhoneType.num}">
				<form:input path="num" htmlEscape="false" maxlength="4" class="input-xlarge required digits"/>
				<span class="help-inline"><font color="red">*请输入3-4位数字号码</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">运营商：</label>
			<div class="controls">
				<form:select path="phoneType" class="input-xlarge required">
					<form:option value="" label="请选择"/>
					<form:options items="${fns:getDictList('phone_type')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="sms:jmsgPhoneType:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>