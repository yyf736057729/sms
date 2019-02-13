<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>用户短信属性查看</title>
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
		
		function onSave(){
			var oldUserId = $("#oldUserId").val();
			var userId = $("#userId").val();
			if(userId == null || userId ==''){
				alertx("请选择用户");
				return;
			}
			
			$.ajax({
				  url: '${ctx}/sms/jmsgSmsUserAttr/checkUserId?oldUserId='+oldUserId+'&userId='+userId,
				  success: function(data){
					  if("true"==data){
							$("#inputForm").attr("action","${ctx}/sms/jmsgSmsUserAttr/save");
							$("#inputForm").submit();
					  }else{
						  alertx("用户短信属性已存在");
					  }
				  },
				  error : function(){
					alert("操作失败...........................");  
				  },
				  dataType: "text"
			});
		}
		
	</script>
</head>
<body>
	<br/>
	<form:form id="inputForm" modelAttribute="jmsgSmsUserAttr" action="${ctx}/sms/jmsgSmsUserAttr/updateApisecret" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>
		<div class="control-group">
			<label class="control-label">apikey：</label>
			<div class="controls">
				<form:input path="user.id" htmlEscape="false" maxlength="100" class="input-xlarge" readonly="true"/>
			</div>
		</div> 
		 <div class="control-group">
			<label class="control-label">apisecret：</label>
			<div class="controls">
				<form:input path="apisecret" htmlEscape="false" maxlength="100" class="input-xlarge" readonly="true"/>
			</div>
		</div> 
		<div class="form-actions">
			<shiro:hasPermission name="sms:jmsgSmsUserAttr:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="刷新apisecret"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="关闭" onclick="window.close();"/>
		</div>
	</form:form>
</body>
</html>