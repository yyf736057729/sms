<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>规则分组管理</title>
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
		<li class="active"><a href="${ctx}/sms/jmsgRuleGroup/">信息列表</a></li>
		<shiro:hasPermission name="sms:jmsgRuleGroup:edit"><li><a href="${ctx}/sms/jmsgRuleGroup/form">信息添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="jmsgRuleGroup" action="${ctx}/sms/jmsgRuleGroup/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>分组名称：</label>
				<form:input path="groupName" htmlEscape="false" maxlength="255" class="input-medium"/>
			</li>
			<li><label>状态：</label>
				<form:select path="status" class="input-medium" htmlEscape="false" >
					<form:option value="">--请选择--</form:option>
					<form:option value="0">启用</form:option>
					<form:option value="1">禁用</form:option>
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
				<!-- <th>id</th> -->
				<th>分组名称</th>
				<th>分组描述</th>
				<th>状态</th>
				<th>创建人</th>
				<th>创建时间</th>
				<shiro:hasPermission name="sms:jmsgRuleGroup:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="jmsgRuleGroup">
			<tr>
				<%-- <td><a href="${ctx}/sms/jmsgRuleGroup/form?id=${jmsgRuleGroup.id}">
					${jmsgRuleGroup.id}
				</a></td> --%>
				<td>
					${jmsgRuleGroup.groupName}
				</td>
				<td>
					${jmsgRuleGroup.description}
				</td>
				<td>
					${jmsgRuleGroup.status eq 1 ? '禁用' : '启用'}
				</td>
				<td>
					${fns:getUserById(jmsgRuleGroup.createBy.id).name}
				</td>
				<td>
					<fmt:formatDate value="${jmsgRuleGroup.createtime}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<shiro:hasPermission name="sms:jmsgRuleGroup:edit"><td>
    				<a href="${ctx}/sms/jmsgRuleGroup/form?id=${jmsgRuleGroup.id}">修改</a>
					<!-- <a href="${ctx}/sms/jmsgRuleGroup/delete?id=${jmsgRuleGroup.id}" onclick="return confirmx('确认要删除该规则分组吗？', this.href)">删除</a> -->
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>