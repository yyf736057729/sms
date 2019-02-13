<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>彩信发送明细管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			
		});
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").submit();
        	return false;
        }
		
		function onQuery(){
			var taskId = $("#taskId").val();
			var phone = $("#phone").val();
			if(taskId == null || taskId ==''){
				alertx("请输入任务ID");
				return;
			}
			
			if(phone == null || phone ==''){
				alertx("请输入手机号码");
				return;
			}
			
			$("#searchForm").submit();
		}	
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/mms/jmsgMmsTaskDetail/init">彩信发送明细列表</a></li>
		<!--<shiro:hasPermission name="mms:jmsgMmsTaskDetail:edit"><li><a href="${ctx}/mms/jmsgMmsTaskDetail/form">彩信发送明细添加</a></li></shiro:hasPermission>-->
	</ul>
	<form:form id="searchForm" modelAttribute="jmsgMmsTaskDetail" action="${ctx}/mms/jmsgMmsTaskDetail/list" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>任务ID：</label>
				<input name="taskId" maxlength="11" class="input-medium required" value="${taskId}" id="taskId">
				<span class="help-inline"><font color="red">*</font> </span>
			</li>
			<li><label>手机号码：</label>
				<input name="phone" maxlength="20" class="input-medium required" value="${phone}" id="phone">
				<span class="help-inline"><font color="red">*</font> </span>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="button" value="查询" onclick="javascript:onQuery();"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>彩信ID</th>
				<th>任务ID</th>
				<th>手机号码</th>
				<th>发送时间</th>
				<th>发送结果</th>
			</tr>
		</thead>
		<tbody>
		<tr>
			<td>
				${jmsgMmsTaskDetail.mmsId}
			</td>
			<td>
				${jmsgMmsTaskDetail.taskId}
			</td>
			<td>
				${jmsgMmsTaskDetail.phone}
			</td>
			<td>
				<fmt:formatDate value="${jmsgMmsTaskDetail.sendDatetime}" pattern="yyyy-MM-dd HH:mm:ss"/>
			</td>
			<td>
				${jmsgMmsTaskDetail.sendResult}
			</td>
		</tr>
		</tbody>
	</table>
</body>
</html>