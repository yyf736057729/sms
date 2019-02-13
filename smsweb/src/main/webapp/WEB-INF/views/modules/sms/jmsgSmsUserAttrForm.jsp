<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>用户短信属性管理</title>
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
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/sms/jmsgSmsUserAttr/">信息列表</a></li>
		<li class="active"><a href="${ctx}/sms/jmsgSmsUserAttr/form?id=${jmsgSmsUserAttr.id}">信息<shiro:hasPermission name="sms:jmsgSmsUserAttr:edit">${not empty jmsgSmsUserAttr.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="sms:jmsgSmsUserAttr:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="jmsgSmsUserAttr" action="${ctx}/sms/jmsgSmsUserAttr/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>
		<div class="control-group">
			<label class="control-label">用户：</label>
			<div class="controls">
				<input id="oldUserId" name="oldUserId" type="hidden" value="${jmsgSmsUserAttr.user.id}">
				<sys:treeselect id="user" name="user.id" value="${jmsgSmsUserAttr.user.id}" labelName="user.name" labelValue="${jmsgSmsUserAttr.user.name}"
					title="用户" url="/sys/office/treeData?type=3" cssClass="required userName" allowClear="true" notAllowSelectParent="true" 
					disabled="${not empty jmsgSmsUserAttr.user.id ? 'disabled':'false'}"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">用户状态：</label>
			<div class="controls">
				<form:select path="userStatus" class="input-xlarge required">
					<form:option value="" label="请选择"/>
					<form:options items="${fns:getDictList('user_status')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<!-- <div class="control-group">
			<label class="control-label">apisecret：</label>
			<div class="controls">
				<form:input path="apisecret" htmlEscape="false" maxlength="100" class="input-xlarge required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div> -->
		<div class="control-group">
			<label class="control-label">签名校验：</label>
			<div class="controls">
				<form:select path="signCheck" class="input-xlarge required">
					<form:option value="" label="请选择"/>
					<form:options items="${fns:getDictList('yes_no')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
				<span class="help-inline"><font color="red">*1是，如果短信签名为空则只校验是否存在【】 0否</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">短信签名：</label>
			<div class="controls">
				<form:input path="smsSign" htmlEscape="false" maxlength="1000" class="input-xlarge "/>
				<span class="help-inline"><font color="red">多个以逗号,隔开</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">鉴权IP：</label>
			<div class="controls">
				<form:input path="autIp" htmlEscape="false" maxlength="20" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">审核条数：</label>
			<div class="controls">
				<form:input path="checkCount" htmlEscape="false" maxlength="11" class="input-xlarge required digits"/>
				<span class="help-inline"><font color="red">*0不审核 >0审核</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">启用全局黑名单：</label>
			<div class="controls">
				<form:select path="globalBlacklist" class="input-xlarge required">
					<form:option value="" label="请选择"/>
					<form:options items="${fns:getDictList('yes_no')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">启用全局过滤词：</label>
			<div class="controls">
				<form:select path="globalFilter" class="input-xlarge required">
					<form:option value="" label="请选择"/>
					<form:options items="${fns:getDictList('yes_no')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">重号过滤：</label>
			<div class="controls">
				<form:select path="repeatFilter" class="input-xlarge required">
					<form:option value="" label="请选择"/>
					<form:options items="${fns:getDictList('yes_no')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">上行推送地址：</label>
			<div class="controls">
				<form:input path="upsideAddr" htmlEscape="false" maxlength="100" class="input-xlarge "/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">状态报告推送地址：</label>
			<div class="controls">
				<form:input path="reportAddr" htmlEscape="false" maxlength="100" class="input-xlarge "/>
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="sms:jmsgSmsUserAttr:edit"><input id="btnSubmit" class="btn btn-primary" type="button" value="保 存" onclick="javascript:onSave();"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>