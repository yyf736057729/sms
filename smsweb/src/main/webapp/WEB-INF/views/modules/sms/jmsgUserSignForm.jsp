<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>用户签名管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#btnMenu").remove();
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
		<li><a href="${ctx}/sms/jmsgUserSign/list?user.id=${jmsgUserSign.user.id}&user.name=${jmsgUserSign.user.name}">信息列表</a></li>
		<li class="active"><a href="${ctx}/sms/jmsgUserSign/form?user.id=${jmsgUserSign.user.id}&user.name=${jmsgUserSign.user.name}">信息<shiro:hasPermission name="sms:jmsgUserSign:edit">${not empty jmsgUserSign.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="sms:jmsgUserSign:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="jmsgUserSign" action="${ctx}/sms/jmsgUserSign/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>		
		<div class="control-group">
			<label class="control-label">用户：</label>
			<div class="controls">
				<sys:treeselect id="user" name="user.id" value="${jmsgUserSign.user.id}" labelName="user.name" labelValue="${jmsgUserSign.user.name}"
					title="用户" url="/sys/office/treeData?type=3" cssClass="required" allowClear="true" notAllowSelectParent="true"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<!-- 
		<div class="control-group">
			<label class="control-label">签名：</label>
			<div class="controls">
				<form:input path="sign" htmlEscape="false" maxlength="255" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div> -->
		<div class="control-group">
			<label class="control-label">签名:</label>
			<div class="controls">
				<form:textarea path="sign" htmlEscape="false" rows="10" class="input-xlarge required" id="sign"/>
				<span class="help-inline"><font color="red">* 每行一个,多个换行添加</font> </span>
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="sms:jmsgUserSign:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
