<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>发送彩信信息</title>
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
		<li><a href="${ctx}/mms/jmsgMmsData/">彩信发送管理列表</a></li>
		<li class="active"><a >彩信单发管理</a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="jmsgMmsTask" action="${ctx}/mms/jmsgMmsTask/sendOne" method="post" class="form-horizontal">
		<sys:message content="${message}"/>
		<input name="mmsId" value="${jmsgMmsTask.mmsId}" type="hidden">
		<div class="control-group">
			<label class="control-label">彩信主题</label>
			<div class="controls">
				<form:input path="mmsTitle" htmlEscape="false" readonly="true"/>
			</div>
		</div>	
		<div class="control-group">
			<label class="control-label">接收号码：</label>
			<div class="controls">
				<form:input path="phone" htmlEscape="false" maxlength="11" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="mms:jmsgMmsData:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="提 交"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>