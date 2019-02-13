<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>用户模板管理</title>
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
			
			
			$('#gatewayId').change(function() 
			{
				$.ajax({
					url : "${ctx}/sms/jmsgGatewayInfo/getGatewayInfo?gatewayId="+$(this).children('option:selected').val(),
					dataType : "json",
					success : function(data) {
						$('#spNumber').val(data.spNumber);
					}
				});
			})
		});
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/sms/jmsgSmsUserTmpl/">信息列表</a></li>
		<li class="active"><a href="${ctx}/sms/jmsgSmsUserTmpl/config?id=${jmsgSmsUserTmpl.id}">信息<shiro:hasPermission name="sms:jmsgSmsUserTmpl:edit">${not empty jmsgSmsUserTmpl.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="sms:jmsgSmsUserTmpl:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="jmsgSmsUserTmpl" action="${ctx}/sms/jmsgSmsUserTmpl/edit" method="post" class="form-horizontal">
		<input  value="${jmsgSmsUserTmpl.id}" name="tempid" style="display: none"/>
		<sys:message content="${message}"/>		
		<div class="control-group">
			<label class="control-label">用户：</label>
			<div class="controls">
				<%--<sys:treeselect id="user" name="userId" value="${jmsgSmsUserTmpl.userId}" labelName="userName" labelValue="${jmsgSmsUserTmpl.userName}"--%>
					<%--title="用户" url="/sys/office/treeData?type=3" cssClass="required" allowClear="true" notAllowSelectParent="true" disabled="${not empty jmsgSmsUserTmpl.id?'disabled':''}"/>--%>
					<sys:treeselect id="user" name="userId" value="${jmsgSmsUserTmpl.userId}" labelName="userName" labelValue="${jmsgSmsUserTmpl.userName}"
									title="用户" url="/sys/office/treeData?type=3" cssClass="input-small" allowClear="true" notAllowSelectParent="true"/>
					<span class="help-inline"><font color="red">*</font> </span>
			</div>

		</div>
		<div class="control-group">
			<label class="control-label">通道模板：</label>
			<div class="controls">
				<%--<c:if test="${empty jmsgSmsUserTmpl.templateName}">--%>
				<form:select path="templateId" class="input-xlarge required">
					<form:option value="" label="请选择"/>
					<form:options items="${fns:getUserTmpl()}" itemLabel="templateName" itemValue="templateId" htmlEscape="false"/>
				</form:select>
				<%--</c:if>--%>
				<%--<c:if test="${not empty jmsgSmsUserTmpl.id}">--%>
				<%--<form:hidden path="gatewayId"/>--%>
				<%--<form:input path="gatewayName" htmlEscape="false" value="${fns:getGatewayInfo(jmsgSmsUserTmpl.gatewayId).gatewayName}" class="input-xlarge required" readonly="${not empty jmsgSmsUserTmpl.id?'true':'false'}"/>--%>
				<%--</c:if>--%>
				<span class="help-inline"><font color="red">*</font></span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">接入号：</label>
			<div class="controls">
				<c:if test="${empty jmsgSmsUserTmpl.id}">
				<form:input path="joinNumber" htmlEscape="false" maxlength="20" class="input-xlarge required" readonly="${not empty jmsgSmsUserTmpl.id?'true':'false'}"/>
				</c:if>
				<c:if test="${not empty jmsgSmsUserTmpl.id}">
				<form:input path="joinNumber" htmlEscape="false" value="${jmsgSmsUserTmpl.joinNumber}" maxlength="20" class="input-xlarge"/>
				</c:if>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">备注：</label>
			<div class="controls">
				<form:textarea path="remarks" htmlEscape="false" rows="3" class="input-xlarge" id="remarks"/>
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="sms:jmsgSmsUserTmpl:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>