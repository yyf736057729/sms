<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>用户短信能力管理</title>
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
		<li><a href="${ctx}/sms/jmsgSmsUserPower/">信息列表</a></li>
		<li class="active"><a href="${ctx}/sms/jmsgSmsUserPower/form?id=${jmsgSmsUserPower.id}">信息<shiro:hasPermission name="sms:jmsgSmsUserPower:edit">${not empty jmsgSmsUserPower.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="sms:jmsgSmsUserPower:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="jmsgSmsUserPower" action="${ctx}/sms/jmsgSmsUserPower/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>		
		<div class="control-group">
			<label class="control-label">用户：</label>
			<div class="controls">
				<sys:treeselect id="user" name="user.id" value="${jmsgSmsUserPower.user.id}" labelName="user.name" labelValue="${jmsgSmsUserPower.user.name}"
					title="用户" url="/sys/office/treeData?type=3" cssClass="required" allowClear="true" notAllowSelectParent="true" disabled="${not empty jmsgSmsUserPower.id?'disabled':'false'  }"/>
				<span class="help-inline"><font color="red">*</font> </span>
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
		<div class="control-group">
			<label class="control-label">省份：</label>
			<div class="controls">
				<form:select path="areaCode" class="input-xlarge required">
					<form:option value="0" label="全国"/>
					<form:options items="${fns:getDictList('area_type')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">通道代码：</label>
			<div class="controls">
				<form:select path="channelCode" class="input-xlarge required">
					<form:option value="0" label="请选择"/>
					<form:options items="${fns:getDictList('channel_code')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">优先级：</label>
			<div class="controls">
				<form:input path="level" htmlEscape="false" maxlength="2" class="input-xlarge required number"/>
				<span class="help-inline"><font color="red">*请输入0-99,数字越大优先级越高</font> </span>
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="sms:jmsgSmsUserPower:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>