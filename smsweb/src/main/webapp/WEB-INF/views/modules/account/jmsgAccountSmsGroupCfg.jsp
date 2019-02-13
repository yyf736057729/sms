<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>用户管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			
		});
		
		function onSave(){
			$("#inputForm").submit();
		}
		
	</script>
</head>
<body>
	<br/>
	<form:form id="inputForm" modelAttribute="jmsgAccount" action="${ctx}/account/jmsgAccount/groupConfig" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<form:hidden path="user.id"/>
		<input type="hidden" name="appType" value="sms">
		<sys:message content="${message}"/>
		<form:hidden path="user.company.id"/>
		<div class="control-group">
			<label class="control-label">登录名:</label>
			<div class="controls">
				<input id="oldLoginName" name="oldLoginName" type="hidden" value="${jmsgAccount.user.loginName}">
				<form:input path="user.loginName" htmlEscape="false" maxlength="50" class="required userName" readonly="${not empty jmsgAccount.user.id && jmsgAccount.user.id !='' ?'true':'false'}"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">姓名:</label>
			<div class="controls">
				<form:input path="user.name" htmlEscape="false" maxlength="50" class="required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group" id="groupIdDiv">
			<label class="control-label">通道分组:</label>
			<div class="controls">
				<form:select path="user.groupId" class="input-xxlarge required">
					<form:option value="">请选择</form:option>
					<form:options items="${fns:getGroupListBz()}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="sys:user:edit"><input id="btnSubmit" class="btn btn-primary" type="button" onclick="javascript:onSave();" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>