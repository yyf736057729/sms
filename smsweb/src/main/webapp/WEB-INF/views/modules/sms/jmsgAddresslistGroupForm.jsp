<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>群组管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#name").focus();
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
		<li><a href="${ctx}/sms/jmsgAddresslistGroup/list">信息列表</a></li>
		<li class="active"><a href="${ctx}/sms/jmsgAddresslistGroup/form?id=${jmsgAddresslistGroup.id}&parent.id=${jmsgAddresslistGroup.parent.id}">信息<shiro:hasPermission name="sms:jmsgAddresslistGroup:edit">${not empty jmsgAddresslistGroup.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="sms:jmsgAddresslistGroup:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="jmsgAddresslistGroup" action="${ctx}/sms/jmsgAddresslistGroup/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>
		<div class="control-group">
			<label class="control-label">上级群组:</label>
			<div class="controls">
                <sys:treeselect id="jmsgAddresslistGroup" name="parent.id" value="${jmsgAddresslistGroup.parent.id}" labelName="parent.name" labelValue="${jmsgAddresslistGroup.parent.name}"
					title="群组" url="/sms/jmsgAddresslistGroup/treeData" extId="${jmsgAddresslistGroup.id}" cssClass="" allowClear="${jmsgAddresslistGroup.currentUser.admin}"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">群组名称:</label>
			<div class="controls">
				<form:input path="name" htmlEscape="false" maxlength="50" class="required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">排序:</label>
			<div class="controls">
				<form:input path="sort" htmlEscape="false" maxlength="50" class="required digits"/>
				<span class="help-inline"><font color="red">*</font>值越小,排序越靠前 </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">备注:</label>
			<div class="controls">
				<form:textarea path="remarks" rows="3" cols="5" htmlEscape="false"/>
			</div>
		</div>		
		<div class="form-actions">
			<shiro:hasPermission name="sms:jmsgAddresslistGroup:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
		</div>
	</form:form>
</body>
</html>