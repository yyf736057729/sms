<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>用户通道管理</title>
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
		<li><a href="${ctx}/sms/jmsgUserGateway/">信息列表</a></li>
		<li class="active"><a href="${ctx}/sms/jmsgUserGateway/form?id=${jmsgUserGateway.id}">信息<shiro:hasPermission name="sms:jmsgUserGateway:edit">${not empty jmsgUserGateway.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="sms:jmsgUserGateway:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="jmsgUserGateway" action="${ctx}/sms/jmsgUserGateway/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>		
		<div class="control-group">
			<label class="control-label">用户ID：</label>
			<sys:treeselect id="userid" name="user.id" value="${jmsgUserGateway.user.id}" labelName="user.name" labelValue="${jmsgUserGateway.user.name}"
				title="用户" url="/sys/office/treeData?type=3" cssClass="required" allowClear="true" notAllowSelectParent="true"/>
			<span class="help-inline"><font color="red">*</font> </span>
		</div>
		<div class="control-group">
			<label class="control-label">登录账号：</label>
			<div class="controls">
				<input type="hidden" name="oldUsername" value="${jmsgUserGateway.username}">
				<form:input path="username" htmlEscape="false" maxlength="40" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">登录密码：</label>
			<div class="controls">
				<form:input path="password" htmlEscape="false" maxlength="32" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">接入号：</label>
			<div class="controls">
				<form:input path="spnumber" htmlEscape="false" maxlength="32" class="input-xlarge required number"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">版本：</label>
			<div class="controls">
				<form:input path="version" htmlEscape="false" maxlength="11" class="input-xlarge required number"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">最大连接数：</label>
			<div class="controls">
				<form:input path="maxChannels" htmlEscape="false" maxlength="11" class="input-xlarge required number"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">MO推送模式：</label>
			<div class="controls">
				<form:select path="allnumPush" class="input-xlarge required" htmlEscape="false" >
					<form:option value="0">全号</form:option>
					<form:option value="1">截取匹配长度(按上行配置)</form:option>
					<form:option value="2">截取固定长度(从头部截取)</form:option>
				</form:select>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>		
		<div class="control-group">
			<label class="control-label">固定长度：</label>
			<div class="controls">
				<form:input path="substringLength" htmlEscape="false" maxlength="2" class="input-xlarge required digits"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">重试间隔(秒)：</label>
			<div class="controls">
				<form:input path="retryWaitTime" htmlEscape="false" maxlength="11" class="input-xlarge required number"/>
				<span class="help-inline"><font color="red">*</font> 默认 3</span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">最大重试次数：</label>
			<div class="controls">
				<form:input path="maxRetryCnt" htmlEscape="false" maxlength="11" class="input-xlarge required number"/>
				<span class="help-inline"><font color="red">*</font> 默认 3</span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">是否重发失败消息：</label>
			<div class="controls">
				<form:select path="resendFailmsg" class="input-xlarge required" htmlEscape="false">
					<form:option value="">请选择</form:option>
					<form:option value="1">是</form:option>
					<form:option value="0">否</form:option>
				</form:select>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">读取限制：</label>
			<div class="controls">
				<form:input path="readLimit" htmlEscape="false" maxlength="11" class="input-xlarge required number"/>
				<span class="help-inline"><font color="red">*</font> 备注：发送速度</span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">写入限制：</label>
			<div class="controls">
				<form:input path="writeLimit" htmlEscape="false" maxlength="11" class="input-xlarge required number"/>
				<span class="help-inline"><font color="red">*</font> 备注：状态报告</span>
			</div>
		</div>
		
		<div class="control-group">
			<label class="control-label">服务代码：</label>
			<div class="controls">
				<form:input path="serviceId" htmlEscape="false" maxlength="11" class="input-xlarge number"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">应用IP：</label>
			<div class="controls">
				<form:input path="appHost" htmlEscape="false" maxlength="20" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> 如：10.25.65.41</span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">应用代码：</label>
			<div class="controls">
				<form:input path="appCode" htmlEscape="false" maxlength="10" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> 默认 8900</span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">绑定IP：</label>
			<div class="controls">
				<%-- <form:input path="allowIP" htmlEscape="false" maxlength="255" class="input-xlarge"/>
				<span class="help-inline">多个IP用逗号分隔,最大255个字符</span> --%>
				<form:textarea path="allowIP" htmlEscape="false" rows="4" maxlength="255" class="input-xlarge" id="allowIP"/>
				<span class="help-inline">多个IP用逗号分隔,最大255个字符</span>
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="sms:jmsgUserGateway:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>