<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>彩信素材审核不通过</title>
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
	<shiro:hasPermission name="mms:jmsgMmsData:check"><li><a href="${ctx}/mms/jmsgMmsData/checkList">彩信素材待审核列表</a></li></shiro:hasPermission>
	<li class="active"><a>审核不通过</a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="jmsgMmsData" action="${ctx}/mms/jmsgMmsData/checkMms" method="post" class="form-horizontal">
		<input type="hidden" name="ids" value="${ids}"/>
		<input type="hidden" name="status" value="0"/>
		<sys:message content="${message}"/>		
		<div class="control-group">
			<label class="control-label">不通过原因：</label>
			<div class="controls">
				<form:textarea path="checkContent" htmlEscape="false" maxlength="200" class="input-xlarge required" cols="10" rows="5"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="mms:jmsgMmsData:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>