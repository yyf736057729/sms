<%@page import="org.apache.commons.lang3.StringUtils"%>
<%@page import="com.siloyou.core.common.utils.JedisClusterUtils"%>
<%@page import="com.siloyou.core.modules.sys.utils.UserUtils"%>
<%@page import="com.sanerzone.common.modules.account.utils.AccountCacheUtils"%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>个人信息</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
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
			
			/**$.ajax({
				url : "${ctx}/account/jmsgAccount/balance?userType=sms&t="+new Date().getTime(),
				dataType : "json",
				success : function(data) {
					$('#moneyTotal').html(data);
				}
			});**/
		});
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/sys/user/info">个人信息</a></li>
		<%-- <li><a href="${ctx}/sys/user/modifyPwd">修改密码</a></li> --%>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="user" action="${ctx}/sys/user/info" method="post" class="form-horizontal"><%--
		<form:hidden path="email" htmlEscape="false" maxlength="255" class="input-xlarge"/>
		<sys:ckfinder input="email" type="files" uploadPath="/mytask" selectMultiple="false"/> --%>
		<sys:message content="${message}"/>
		<!-- <div class="control-group">
			<label class="control-label">头像:</label>
			<div class="controls">
				<form:hidden id="nameImage" path="photo" htmlEscape="false" maxlength="255" class="input-xlarge"/>
				<sys:ckfinder input="nameImage" type="images" uploadPath="/photo" selectMultiple="false" maxWidth="100" maxHeight="100"/>
			</div>
		</div> -->
		<!-- <div class="control-group">
			<label class="control-label">归属部门:</label>
			<div class="controls">
				<label class="lbl">${user.office.name}</label>
			</div>
		</div> -->
		<div class="control-group">
			<label class="control-label">账号状态:</label>
			<div class="controls">
				<label class="lbl">${fns:getDictLabel(user.delFlag, 'del_flag', '正常')}</label>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">当前余额:</label>
			<div class="controls">
				<label style="font-weight:bold;font-size:20;color:red" id="moneyTotal">${fns:getAmount(user.id)}</label>
			</div>
		</div>
		<%-- <div class="control-group">
			<label class="control-label">登录账号:</label>
			<div class="controls">
				<form:input path="loginName" htmlEscape="false" maxlength="50" class="required" readonly="true"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">用户名称:</label>
			<div class="controls">
				<form:input path="name" htmlEscape="false" maxlength="50" class="required" readonly="true"/>
			</div>
		</div> --%>
		<div class="control-group">
			<label class="control-label">公司名称:</label>
			<div class="controls">
				<label class="lbl">${user.companyName }</label>
				<%-- <form:input path="companyName" htmlEscape="false" maxlength="50" readonly="true"/> --%>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">联系人:</label>
			<div class="controls">
				<label class="lbl">${user.contactName }</label>
				<%-- <form:input path="contactName" htmlEscape="false" maxlength="50"/> --%>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">联系手机:</label>
			<div class="controls">
				<label class="lbl">${user.mobile }</label>
				<%-- <form:input path="mobile" htmlEscape="false" maxlength="50"/> --%>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">电子邮箱:</label>
			<div class="controls">
				<label class="lbl">${user.email }</label>
				<%-- <form:input path="email" htmlEscape="false" maxlength="50" class="email"/> --%>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">QQ:</label>
			<div class="controls">
				<label class="lbl">${user.qq }</label>
				<%-- <form:input path="qq" htmlEscape="false" maxlength="50"/> --%>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">免审标识:</label>
			<div class="controls">
				<c:choose>
					<c:when test="${user.noCheck == '0'}">
						<label class="lbl">手动审核</label>
					</c:when>
					<c:when test="${user.noCheck == '1'}">
						<label class="lbl">免审</label>
					</c:when>
					<c:when test="${user.noCheck == '2'}">
						<label class="lbl">自动审核</label>
					</c:when>
				</c:choose>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">是否校验签名:</label>
			<div class="controls">
				<c:choose>
					<c:when test="${user.signFlag == '0'}">
						<label class="lbl">不校验</label>
					</c:when>
					<c:when test="${user.signFlag == '1'}">
						<label class="lbl">校验</label>
					</c:when>
				</c:choose>
			</div>
		</div>
		<!-- <div class="control-group">
			<label class="control-label">电话:</label>
			<div class="controls">
				<form:input path="phone" htmlEscape="false" maxlength="50"/>
			</div>
		</div> -->
		<c:if test="${not empty fns:getUser().apikey }">
		<!-- <div class="control-group">
			<label class="control-label">接口信息</label>
			<div class="controls">
				appid: ${fns:getUser().id }<br/>
				apikey: ${fns:getUser().apikey }
			</div>
		</div> -->
		<div class="control-group">
			<label class="control-label">userid:</label>
			<div class="controls">
				<label class="lbl">${user.id}</label>
			</div>
		</div>
		<div class="control-group" ${user.interfaceFlag == 0 ? 'style="display: none;"' : ''}>
			<label class="control-label">apikey:</label>
			<div class="controls">
				<label class="lbl">${user.apikey}</label>
			</div>
		</div>
		<div class="control-group" ${user.interfaceFlag == 0 ? 'style="display: none;"' : ''}>
			<label class="control-label">绑定ip:</label>
			<div class="controls">
				<label class="lbl">${user.whiteIP}</label>
			</div>
		</div>
		<div class="control-group" ${user.interfaceFlag == 0 ? 'style="display: none;"' : ''}>
			<label class="control-label">状态报告回调地址</label>
			<div class="controls">
				<label class="lbl">${user.callbackUrl}</label>
			</div>
		</div>
		<div class="control-group" ${user.interfaceFlag == 0 ? 'style="display: none;"' : ''}>
			<label class="control-label">上行短信地址</label>
			<div class="controls">
				<label class="lbl">${user.upUrl}</label>
			</div>
		</div>
		</c:if>
		<!-- <div class="control-group">
			<label class="control-label">用户类型:</label>
			<div class="controls">
				<label class="lbl">${fns:getDictLabel(user.userType, 'sys_user_type', '无')}</label>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">用户角色:</label>
			<div class="controls">
				<label class="lbl">${user.roleNames}</label>
			</div>
		</div> -->
		<div class="control-group">
			<label class="control-label">上次登录:</label>
			<div class="controls">
				<label class="lbl">IP: ${user.oldLoginIp}&nbsp;&nbsp;&nbsp;&nbsp;时间：<fmt:formatDate value="${user.oldLoginDate}" type="both" dateStyle="full"/></label>
			</div>
		</div>
		<!-- <div class="form-actions">
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>
		</div> -->
	</form:form>
</body>
</html>