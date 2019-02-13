<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>规则管理管理</title>
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
		<li><a href="${ctx}/sms/jmsgRuleInfo/">信息列表</a></li>
		<li class="active"><a href="${ctx}/sms/jmsgRuleInfo/form?id=${jmsgRuleInfo.id}">信息<shiro:hasPermission name="sms:jmsgRuleInfo:edit">${not empty jmsgRuleInfo.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="sms:jmsgRuleInfo:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="jmsgRuleInfo" action="${ctx}/sms/jmsgRuleInfo/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>
		<div class="control-group">
			<label class="control-label">规则代码：</label>
			<div class="controls">
				<form:input path="ruleCode" htmlEscape="false" maxlength="2" class="input-xlarge"/>
			</div>
		</div>		
		<div class="control-group">
			<label class="control-label">规则名称：</label>
			<div class="controls">
				<form:input path="ruleName" htmlEscape="false" maxlength="128" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">规则内容：</label>
			<div class="controls">
				<form:input path="ruleContent" htmlEscape="false" maxlength="512" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">规则分类：</label>
			<div class="controls">
				<form:select path="ruleType" class="input-xlarge required" htmlEscape="false" >
					<form:option value="">--请选择--</form:option>
					<form:option value="1">网址</form:option>
					<form:option value="2">电话</form:option>
					<%-- <form:option value="3">关键字</form:option> --%>
					<form:option value="4">正则式</form:option>
				</form:select>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">检验通过：</label>
			<div class="controls">
				<form:select path="ispass" class="input-xlarge required" htmlEscape="false" >
					<form:option value="">--请选择--</form:option>
					<form:option value="0">通过</form:option>
					<form:option value="1">不通过</form:option>
				</form:select>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">状态：</label>
			<div class="controls">
				<form:select path="status" class="input-xlarge required" htmlEscape="false" >
					<form:option value="">--请选择--</form:option>
					<form:option value="0">启用</form:option>
					<form:option value="1">禁用</form:option>
				</form:select>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">描述：</label>
			<div class="controls">
				<form:input path="description" htmlEscape="false" maxlength="512" class="input-xlarge "/>
			</div>
		</div>
		<%-- <div class="control-group">
			<label class="control-label">创建时间：</label>
			<div class="controls">
				<input name="createtime" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate "
					value="<fmt:formatDate value="${jmsgRuleInfo.createtime}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});"/>
			</div>
		</div> --%>
		<div class="form-actions">
			<shiro:hasPermission name="sms:jmsgRuleInfo:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>