<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>自定义任务管理</title>
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
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/sms/jmsgCustomTask/">信息列表</a></li>
		<shiro:hasPermission name="sms:jmsgCustomTask:edit"><li><a href="${ctx}/sms/jmsgCustomTask/form">自定义任务添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="jmsgCustomTask" action="${ctx}/sms/jmsgCustomTask/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>任务名称：</label>
				<form:input path="taskName" htmlEscape="false" maxlength="64" class="input-medium"/>
			</li>
			<li><label>任务类型：</label>
				<form:input path="type" htmlEscape="false" maxlength="1" class="input-medium"/>
			</li>
			<li><label>任务状态：</label>
				<form:select path="status" class="input-medium">
					<form:option value="" label="全部"/>
					<form:options items="${fns:getDictList('task_send_status')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>ID</th>
				<th>任务名称</th>
				<th>任务类型</th>
				<th>执行结果</th>
				<th>任务状态</th>
				<th>创建时间</th>
				<th>创建人</th>
				<th>备注</th>
				<shiro:hasPermission name="sms:jmsgCustomTask:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="jmsgCustomTask">
			<tr>
				<td>
					${jmsgCustomTask.id}
				</td>
				<td>
					${jmsgCustomTask.taskName}
				</td>
				<td>
					${jmsgCustomTask.type}
				</td>
				<td>
					${jmsgCustomTask.executeResult}
				</td>
				<td>
					${fns:getDictLabel(jmsgCustomTask.status, 'task_send_status', '')}
				</td>
				<td>
					<fmt:formatDate value="${jmsgCustomTask.createTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
					${jmsgCustomTask.createBy.name}
				</td>
				<td>
					${jmsgCustomTask.remarks}
				</td>
				<shiro:hasPermission name="sms:jmsgCustomTask:edit"><td>
    				<a href="${ctx}/sms/jmsgCustomTask/form?id=${jmsgCustomTask.id}">修改</a>
					<a href="${ctx}/sms/jmsgCustomTask/delete?id=${jmsgCustomTask.id}" onclick="return confirmx('确认要删除该自定义任务吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>