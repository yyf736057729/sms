<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>http发送测试</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#name").focus();
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
		
		function onSubmit(){
			var url = $("#urlId").val();
			if(url == null || url ==''){
				alertx("请输入url地址");
				return;
			}
			
			var method = $("#method").val();
			if(method == null || method ==''){
				alertx("请输入接口方法");
				return;
			}
			
			url = url+method;
			
			var userid = $("#userid").val();
			if(userid == null || userid ==''){
				alertx("请输入用户ID");
				return;
			}
			
			var apikey = $("#apikey").val();
			if(apikey == null || apikey ==''){
				alertx("请输入apikey");
				return;
			}
			
			var phone = $("#phone").val();
			if(phone == null || phone ==''){
				alertx("请输入手机号码");
				return;
			}
			
			var smsContent = $("#smsContent").val();
			if(smsContent != null && smsContent !=''){
				smsContent = encodeURI(encodeURI(smsContent));
			}else{
				alertx("请输入短信内容");
				return;	
			}
			$.ajax({  
		        type: "post",  
		        url: "${ctx}/sms/jmsgSmsHttpSendTest/send?url="+url+"&content="+smsContent+"&phone="+phone+"&apikey="+apikey+"&userid="+userid,
		        dataType: 'json',  
		        contentType: "application/x-www-form-urlencoded; charset=utf-8",  
				success : function(result) {
					alertx(result);
				}
		    });
		}
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/sms/jmsgSmsHttpSendTest/init">接口发送测试</a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="jmsgAddresslistGroup" action="${ctx}/sms/jmsgSmsHttpSendTest/send" method="post" class="form-horizontal">
		<sys:message content="${message}"/>
		<div class="control-group">
			<label class="control-label">url地址:</label>
			<div class="controls">
			<input type="text" name="url" id="urlId" class="input-xxlarge" maxlength="30">
			<span class="help-inline"><font color="red">* 例：http://118.178.87.60:10082</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">接口方法:</label>
			<div class="controls">
			<input type="text" name="method" id="method" class="input-xxlarge" maxlength="50" value="/api/sms/send">
			<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">用户ID:</label>
			<div class="controls">
				<input type="text" name="userid" id="userid" class="input-xxlarge" maxlength="30">
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">apikey:</label>
			<div class="controls">
				<input type="text" name="apikey" id="apikey" class="input-xxlarge" maxlength="50">
				<span class="help-inline"><font color="red">*</font></span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">手机号码:</label>
			<div class="controls">
				<input type="text" name="phone" id="phone" class="input-xxlarge" maxlength="50">
				<span class="help-inline"><font color="red">*</font></span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">短信内容:</label>
			<div class="controls">
				<textarea name="smsContent" id="smsContent" rows="3" cols="3" class="input-xxlarge" maxlength="500"></textarea>
				<span class="help-inline"><font color="red">*</font></span>
			</div>
		</div>
		<div class="form-actions">
			<input id="btnSubmit" class="btn btn-primary" type="button" value="提&nbsp;&nbsp交" onclick="javascrpt:onSubmit();"/>
		</div>
	</form:form>
</body>
</html>