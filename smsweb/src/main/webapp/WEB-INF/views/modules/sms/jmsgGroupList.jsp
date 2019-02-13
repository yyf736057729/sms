<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>分组信息管理</title>
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
		<li class="active"><a href="${ctx}/sms/jmsgGroup/">信息列表</a></li>
		<shiro:hasPermission name="sms:jmsgGroup:edit"><li><a href="${ctx}/sms/jmsgGroup/form">信息添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="jmsgGroup" action="${ctx}/sms/jmsgGroup/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>分组名称：</label>
				<form:input path="name" htmlEscape="false" maxlength="20" class="input-medium"/>
			</li>
			<li><label>状态：</label>
				<form:select path="status" class="input-medium" htmlEscape="false" >
					<form:option value="">--请选择--</form:option>
					<form:option value="1">启用</form:option>
					<form:option value="0">禁用</form:option>
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
				<th>分组名称</th>
				<th>分组描述</th>
				<th>状态</th>
				<th>创建时间</th>
				<shiro:hasPermission name="sms:jmsgGroup:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="jmsgGroup">
			<tr>
				<td><a href="${ctx}/sms/jmsgGroup/form?id=${jmsgGroup.id}">
					${jmsgGroup.name}
				</a></td>
				<td>
					${jmsgGroup.description}
				</td>
				<td>
					${jmsgGroup.status eq 1 ? '启用' : '禁用'}
				</td>
				<td>
					<fmt:formatDate value="${jmsgGroup.createtime}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<shiro:hasPermission name="sms:jmsgGroup:edit"><td>
    				<a href="${ctx}/sms/jmsgGroup/form?id=${jmsgGroup.id}">修改</a>
					<a href="${ctx}/sms/jmsgGroup/updateStatus?id=${jmsgGroup.id}&oldStatus=${jmsgGroup.status}" onclick="return confirmx('确认要${jmsgGroup.status eq 1 ?'禁用' :'启用'}该分组信息吗？', this.href)">
					${jmsgGroup.status eq 1?"禁用":"启用"}
					</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>