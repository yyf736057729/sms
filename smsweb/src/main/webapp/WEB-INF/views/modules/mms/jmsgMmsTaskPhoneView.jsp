<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>查看号码清单</title>
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
	 <br/>
	<form:form id="inputForm" modelAttribute="jmsgMmsTask" action="${ctx}/mms/jmsgMmsTask/sendOne" method="post" class="form-horizontal">
		<textarea rows="12" cols="80" class="input-xxlarge">${phone}</textarea>
		<div class="form-actions">
			<input id="btnCancel" class="btn" type="button" value="关闭" onclick="window.close();"/>
		</div>
	</form:form>
</body>
</html>