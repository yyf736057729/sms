<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>通道签名管理</title>
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
		<li><a href="${ctx}/sms/jmsgGatewaySign/">信息列表</a></li>
		<li class="active"><a href="${ctx}/sms/jmsgGatewaySign/form?id=${jmsgGatewaySign.id}">信息<shiro:hasPermission name="sms:jmsgGatewaySign:edit">${not empty jmsgGatewaySign.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="sms:jmsgGatewaySign:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="jmsgGatewaySign" action="${ctx}/sms/jmsgGatewaySign/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>		
		<div class="control-group">
			<label class="control-label">用户：</label>
			<div class="controls">
				<sys:treeselect id="user" name="user.id" value="${jmsgGatewaySign.user.id}" labelName="user.name" labelValue="${jmsgGatewaySign.user.name}"
					title="用户" url="/sys/office/treeData?type=3" cssClass="required" allowClear="true" notAllowSelectParent="true" disabled="${not empty jmsgGatewaySign.id?'disabled':''}"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">通道：</label>
			<div class="controls">
				<c:if test="${empty jmsgGatewaySign.id}">
				<form:select path="gatewayId" class="input-xlarge required">
					<form:option value="" label="请选择"/>
					<form:options items="${fns:getGatewayList()}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
				</c:if>
				<c:if test="${not empty jmsgGatewaySign.id}">
				<form:hidden path="gatewayId"/>
				<form:input path="gatewayName" htmlEscape="false" value="${fns:getGatewayInfo(jmsgGatewaySign.gatewayId).gatewayName}" class="input-xlarge required" readonly="${not empty jmsgGatewaySign.id?'true':'false'}"/>
				</c:if>
				<span class="help-inline"><font color="red">*</font></span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">签名：</label>
			<div class="controls">
				<form:input path="sign" htmlEscape="false" maxlength="20" class="input-xlarge required" readonly="${not empty jmsgGatewaySign.id?'true':'false'}"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">接入号：</label>
			<div class="controls">
				<c:if test="${empty jmsgGatewaySign.id}">
				<form:input path="spNumber" htmlEscape="false" maxlength="20" class="input-xlarge required" readonly="${not empty jmsgGatewaySign.id?'true':'false'}"/>
				</c:if>
				<c:if test="${not empty jmsgGatewaySign.id}">
				<form:input path="spNumber" htmlEscape="false" value="${fns:getGatewayInfo(jmsgGatewaySign.gatewayId).spNumber}" maxlength="20" class="input-xlarge required" readonly="${not empty jmsgGatewaySign.id?'true':'false'}"/>
				</c:if>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">扩展号：</label>
			<div class="controls">
				<c:if test="${empty jmsgGatewaySign.id}">
				<form:input path="extNumber" htmlEscape="false" maxlength="20" class="input-xlarge" readonly="${not empty jmsgGatewaySign.id?'true':'false'}"/>
				</c:if>
				<c:if test="${not empty jmsgGatewaySign.id}">
				<form:input path="extNumber" htmlEscape="false" value="${jmsgGatewaySign.spNumber}" maxlength="20" class="input-xlarge" readonly="${not empty jmsgGatewaySign.id?'true':'false'}"/>
				</c:if>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">备注：</label>
			<div class="controls">
				<form:textarea path="note" htmlEscape="false" rows="3" class="input-xlarge" id="note"/>
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="sms:jmsgGatewaySign:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>