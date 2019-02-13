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
		<li class="active"><a>彩信群发管理</a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="jmsgMmsTask" action="${ctx}/mms/jmsgMmsTask/sendMore" method="post" class="form-horizontal" enctype="multipart/form-data">
		<sys:message content="${message}"/>
		<input name="mmsId" value="${jmsgMmsTask.mmsId}" type="hidden"/>
		 <div class="control-group">
			<label class="control-label">彩信主题</label>
			<div class="controls">
				<form:input path="mmsTitle" htmlEscape="false" readonly="true"/>
			</div>
		</div>		
		<div class="control-group">
			<label class="control-label">号码文件</label>
			<div class="controls">
				<input name="phoneFile" type="file" class="input-xlarge required">
				<span class="help-inline"><font color="red">*注：号码文件为txt文件，内容为1行1个号码</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">开始日期</label>
			<div class="controls">
				<input name="sendDatetime" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					value="<fmt:formatDate value="${jmsgMmsTask.sendDatetime}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:true});"/><span class="help-inline"><font color="red">注：开始时间为空，则为立即发送</font> </span>
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="mms:jmsgMmsData:edit">
				<input id="btnSubmit" class="btn btn-primary" type="submit" value="提 交"/>&nbsp;
			</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>