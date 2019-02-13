<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>短信账号</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#no").focus();
			$("#inputForm").validate({
				rules: {
					loginName: {remote: "${ctx}/sys/user/checkLoginName?oldLoginName=" + encodeURIComponent('${jmsgAccount.user.loginName}')}
				},
				messages: {
					loginName: {remote: "用户登录名已存在"},
					confirmNewPassword: {equalTo: "输入与上面相同的密码"}
				},
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
			var validate = /^[0-9a-zA-Z]*$/g;
			var mmsfrom = $("#mmsfrom").val();
			if(mmsfrom != null && mmsfrom !=''){
				if(!validate.test(mmsfrom)){
					alertx("请输入正确接入号,字母或数字");
					return;
				}
			}
			
			$("#inputForm").submit();
		}
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/account/jmsgAccount/agencySmsList">信息列表</a></li>
		<li class="active"><a href="${ctx}/account/jmsgAccount/agencySmsForm?id=${jmsgAccount.id}">信息<shiro:hasPermission name="account:jmsgAccount:edit">${not empty jmsgAccount.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="account:jmsgAccount:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="jmsgAccount" action="${ctx}/account/jmsgAccount/agencySmsSave" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<form:hidden path="user.id"/>
		<form:hidden path="user.callbackUrl"/>
		<form:hidden path="user.noCheck"/>
		<form:hidden path="user.apikey"/>
		<form:hidden path="user.whiteIP"/>
		<form:hidden path="user.mmsfrom"/>
		<form:hidden path="user.upUrl"/>
		<form:hidden path="user.signFlag"/>
		<form:hidden path="user.groupId"/>
		<form:hidden path="user.keyword"/>
		<sys:message content="${message}"/>
		<div class="control-group">
			<label class="control-label">登录名:</label>
			<div class="controls">
				<input id="oldLoginName" name="oldLoginName" type="hidden" value="${jmsgAccount.user.loginName}">
				<form:input path="user.loginName" htmlEscape="false" maxlength="50" class="required userName" readonly="${not empty jmsgAccount.user.id && jmsgAccount.user.id !='' ?'true':'false'}"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">密码:</label>
			<div class="controls">
				<input id="newPassword" name="newPassword" type="password" value="" maxlength="50" minlength="3" class="${empty jmsgAccount.user.id?'required':''}"/>
				<c:if test="${empty jmsgAccount.user.id}"><span class="help-inline"><font color="red">*</font> </span></c:if>
				<c:if test="${not empty jmsgAccount.user.id}"><span class="help-inline">若不修改密码，请留空。</span></c:if>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">确认密码:</label>
			<div class="controls">
				<input id="confirmNewPassword" name="confirmNewPassword" type="password" value="" maxlength="50" minlength="3" equalTo="#newPassword"/>
				<c:if test="${empty user.id}"><span class="help-inline"><font color="red">*</font> </span></c:if>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">姓名:</label>
			<div class="controls">
				<form:input path="user.name" htmlEscape="false" maxlength="50" class="required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<!-- <div class="control-group">
			<label class="control-label">扣费方式:</label>
			<div class="controls">
				<form:radiobuttons path="payMode" items="${fns:getDictList('pay_mode')}" itemLabel="label" itemValue="value" class="required" htmlEscape="false"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div> -->
		
		<div class="control-group">
			<label class="control-label">邮箱:</label>
			<div class="controls">
				<form:input path="user.email" htmlEscape="false" maxlength="100" class="email"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">电话:</label>
			<div class="controls">
				<form:input path="user.phone" htmlEscape="false" maxlength="100"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">手机:</label>
			<div class="controls">
				<form:input path="user.mobile" htmlEscape="false" maxlength="100"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">QQ:</label>
			<div class="controls">
				<form:input path="user.qq" htmlEscape="false" maxlength="100"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">公司名称:</label>
			<div class="controls">
				<form:input path="user.companyName" htmlEscape="false" maxlength="100"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">联系人:</label>
			<div class="controls">
				<form:input path="user.contactName" htmlEscape="false" maxlength="50"/>
			</div>
		</div>
		<!-- <div class="control-group">
			<label class="control-label">接入号:</label>
			<div class="controls">
				<form:input path="user.mmsfrom" htmlEscape="false" maxlength="12" id="mmsfrom"/>
			</div>
		</div> -->		
		<div class="control-group">
			<label class="control-label">备注:</label>
			<div class="controls">
				<form:textarea path="user.remarks" htmlEscape="false" rows="3" maxlength="200" class="input-xlarge"/>
			</div>
		</div>
		<div class="form-actions">
			<input id="btnSubmit" class="btn btn-primary" type="button" onclick="javascript:onSave();" value="保 存"/>&nbsp;
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>