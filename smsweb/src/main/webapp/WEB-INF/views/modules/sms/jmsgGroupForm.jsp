<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>分组信息管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			//$("#name").focus();
			$("#inputForm").validate({
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
		<li><a href="${ctx}/sms/jmsgGroup/">信息列表</a></li>
		<li class="active"><a href="${ctx}/sms/jmsgGroup/form?id=${jmsgGroup.id}">信息<shiro:hasPermission name="sms:jmsgGroup:edit">${not empty jmsgGroup.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="sms:jmsgGroup:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="jmsgGroup" action="${ctx}/sms/jmsgGroup/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<input name="oldName" value="${jmsgGroup.name}" type="hidden">
		<input type="hidden" name="status" value="1">
		<sys:message content="${message}"/>		
		<div class="control-group">
			<label class="control-label">分组名称：</label>
			<div class="controls">
				<form:input path="name" htmlEscape="false" maxlength="20" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">分组排序：</label>
			<div class="controls">
				<form:input path="sort" htmlEscape="false" maxlength="20" class="input-xlarge"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">分组描述：</label>
			<div class="controls">
				<form:textarea path="description" rows="4" cols="4" class="input-xlarge" htmlEscape="false"></form:textarea>
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="sms:jmsgGroup:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>