<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>黑名单管理</title>
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
		<li class="active"><a href="${ctx}/sms/jmsgPhoneBlacklist/init">信息列表</a></li>
		<shiro:hasPermission name="sms:jmsgPhoneBlacklist:edit"><li><a href="${ctx}/sms/jmsgPhoneBlacklist/form">信息添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="jmsgPhoneBlacklist" action="${ctx}/sms/jmsgPhoneBlacklist/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>手机号码：</label>
				<form:input path="phone" htmlEscape="false" maxlength="20" class="input-medium"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>手机号码</th>
				<th>范围 </th>
				<th>备注</th>
				<th>创建日期</th>
				<shiro:hasPermission name="sms:jmsgPhoneBlacklist:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="jmsgPhoneBlacklist">
			<tr>
				<td>
					${jmsgPhoneBlacklist.phone}
				</td>
				<td>
					${fns:getDictLabel(jmsgPhoneBlacklist.scope, 'scope_type', '')}
				</td>
				<td>
					${jmsgPhoneBlacklist.remarks}
				</td>
				<td>
					<fmt:formatDate value="${jmsgPhoneBlacklist.createDatetime}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<shiro:hasPermission name="sms:jmsgPhoneBlacklist:edit"><td>
    				<!-- <a href="${ctx}/sms/jmsgPhoneBlacklist/form?id=${jmsgPhoneBlacklist.id}">修改</a> -->
					<a href="${ctx}/sms/jmsgPhoneBlacklist/delete?phone=${jmsgPhoneBlacklist.phone}" onclick="return confirmx('确认要删除该黑名单吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>