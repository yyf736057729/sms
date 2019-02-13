<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>资金账户充值</title>
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
		
		function onSave(){
		    var money = $("#money").val();
		    var re =/^\d+(.\d{0,2})?$/;
	     	if (!re.test(money)){
	        	alertx("请输入正确充值条数，最多可输入2位小数正数");
	        	return;
	     	}
		    $("#inputForm").submit();
		}
		
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/account/jmsgAccount/listByRechargeAgency?appType=${jmsgAccount.appType}">${fns:getDictLabel(jmsgAccount.appType, 'app_type', jmsgAccount.appType)}账户信息列表</a></li>
		<shiro:hasPermission name="jmsg:agency"><li class="active"><a>${fns:getDictLabel(jmsgAccount.appType, 'app_type', '')}账户充值</a></li></shiro:hasPermission>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="jmsgAccount" action="${ctx}/account/jmsgAccount/rechargeAgency" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<form:hidden path="appType"/>
		<sys:message content="${message}"/>		
		<div class="control-group">
			<label class="control-label">用户：</label>
			<div class="controls">
				<form:select path="user.id" class="input-xlarge required">
					<form:option value="" label="请选择"/>
					<form:options items="${fns:getAccountList(jmsgAccount.appType)}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>	
		<div class="control-group">
			<label class="control-label">充值条数：</label>
			<div class="controls">
				<form:input path="money" htmlEscape="false" maxlength="8" class="input-xlarge required digits" id="money"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">充值方式：</label>
			<div class="controls">
				<form:select path="payment" class="input-xlarge required">
					<form:option value="" label="请选择"/>
					<form:option value="CZ00" label="充值转入"/>
					<form:option value="CZ01" label="手动返充"/>
					<form:option value="XF01" label="手动扣款"/>
				</form:select>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">备注：</label>
			<div class="controls">
				<form:input path="remark" htmlEscape="false" maxlength="250" class="input-xlarge "/>
			</div>
		</div>		
		<div class="form-actions">
			<shiro:hasPermission name="account:jmsgAccount:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>