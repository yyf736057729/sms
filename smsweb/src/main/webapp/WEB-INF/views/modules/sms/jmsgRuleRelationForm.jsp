<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>规则关系管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			$('#ruleType').change(function(){ 
				if ($('#ruleType').val() == 4)
				{
					var groupId = $("#groupId").val();
					initRuleInfo(groupId,4);
					$('#ruleIdDiv').show();
				}
				else
				{
					$('#ruleIdDiv').hide();
				}
	　　　　});
			
			
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
		
		function changeGroup(){
			var groupId = $("#groupId").val();
			var ruleType = $("#ruleType").val();
			if(ruleType == 4){
				initRuleInfo(groupId,ruleType);
			}
		}
		
		function initRuleInfo(groupId,ruleType){
			$.ajax({
				url : "${ctx}/sms/jmsgRuleRelation/initRuleInfo?groupId="+groupId+"&ruleType="+ruleType,
				dataType : "json",
				success : function(data) {
					$("#ruleIdList").empty();
					$.each(data,function(i,item){
		            	$("#ruleIdList").append("<option value='"+item.id+"'>"+item.ruleName+"</option>");
		            })
				}
			});
		}

	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/sms/jmsgRuleRelation/">信息列表</a></li>
		<li class="active"><a href="${ctx}/sms/jmsgRuleRelation/form?id=${jmsgRuleRelation.id}">信息<shiro:hasPermission name="sms:jmsgRuleRelation:edit">${not empty jmsgRuleRelation.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="sms:jmsgRuleRelation:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="jmsgRuleRelation" action="${ctx}/sms/jmsgRuleRelation/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>		
		<div class="control-group">
			<label class="control-label">规则分组：</label>
			<div class="controls">
				<form:select path="groupId" class="input-xlarge required" htmlEscape="false" onchange="javascript:changeGroup();">
					<form:options items="${jmsgRuleRelation.jmsgRuleGroupList}" itemLabel="groupName" itemValue="id" htmlEscape="false"/>
				</form:select>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">规则分类：</label>
			<div class="controls">
				<form:select path="ruleType" class="input-xlarge required" htmlEscape="false">
					<form:option value="">--请选择--</form:option>
					<form:option value="1">网址</form:option>
					<form:option value="2">电话</form:option>
					<%-- <form:option value="3">关键字</form:option> --%>
					<form:option value="4">正则式</form:option>
				</form:select>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group" id="ruleIdDiv" style="display: none;">
			<label class="control-label">规则名称：</label>
			<div class="controls">
				<form:select path="ruleIdList" class="input-xxlarge required" htmlEscape="false" multiple="multiple">
					<form:options items="${jmsgRuleRelation.jmsgRuleInfoList}" itemLabel="ruleName" itemValue="id" htmlEscape="false"/>
				</form:select>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="sms:jmsgRuleRelation:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>