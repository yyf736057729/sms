<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>导入联系人</title>
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
		
		function onSubmit(){
			var groupId = $("#groupId").val();
			if(groupId == null || groupId == ''){
				alertx("请选择群组");
				return;
			}
			
			var uploadFile = $("#uploadFile").val();
			if(uploadFile == null || uploadFile == ''){
				alertx("请选择上传文件");
				return;
			}
			$("#inputForm").attr("action","${ctx}/sms/jmsgAddresslistInfo/import");
			$("#inputForm").submit();
		}

	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/sms/jmsgAddresslistInfo/batchForm">导入联系人</a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="jmsgAddresslistInfo" action="${ctx}/sms/jmsgAddresslistInfo/import" method="post" class="form-horizontal" enctype="multipart/form-data">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>		
		<div class="control-group">
			<label class="control-label">群组:</label>
			<div class="controls">
                <sys:treeselect id="group" name="groupId" value="${jmsgAddresslistInfo.group.id}" labelName="group.name" labelValue="${jmsgAddresslistInfo.group.name}"
					title="群组" url="/sms/jmsgAddresslistGroup/treeData" cssClass="input-xlarge required"/>
					<span class="help-inline"><font color="red">*</font> </span>
			</div>
			<label class="control-label">文件:</label>
			<div class="controls">
				<input id="uploadFile" name="file" type="file" style="width:430px" accept=".xls,.xlsx"/><br/>
				<span class="help-inline"><font color="red">导入文件不能超过5M，仅允许导入“xls”或“xlsx”格式文件！</font> </span>
			</div>
		</div>
		
		<div class="form-actions">
			<shiro:hasPermission name="sms:jmsgAddresslistInfo:edit">
			<input id="btnImportSubmit" class="btn btn-primary" type="button" value="导 入" onclick="javascript:onSubmit();"/>
			<a href="${ctx}/sms/jmsgAddresslistInfo/import/template">下载模板</a>&nbsp;</shiro:hasPermission>
			<!-- <input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/> -->
		</div>
	</form:form>
</body>
</html>