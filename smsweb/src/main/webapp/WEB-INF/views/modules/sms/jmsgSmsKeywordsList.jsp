<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>敏感词管理</title>
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
		<li class="active"><a href="${ctx}/sms/jmsgSmsKeywords/">信息列表</a></li>
		<shiro:hasPermission name="sms:jmsgSmsKeywords:edit"><li><a href="${ctx}/sms/jmsgSmsKeywords/form">信息添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="jmsgSmsKeywords" action="${ctx}/sms/jmsgSmsKeywords/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>敏感词：</label>
				<form:input path="keywords" htmlEscape="false" maxlength="20" class="input-medium"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>敏感词</th>
				<th>范围 </th>
				<!-- <th>创建日期</th> -->
				<shiro:hasPermission name="sms:jmsgSmsKeywords:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="jmsgSmsKeywords">
			<tr>
				<td><a href="${ctx}/sms/jmsgSmsKeywords/form?id=${jmsgSmsKeywords.id}">
					${jmsgSmsKeywords.keywords}
				</a></td>
				<td>
					${fns:getDictLabel(jmsgSmsKeywords.scope, 'scope_type', '')}
				</td>
				<!-- <td>
					<fmt:formatDate value="${jmsgSmsKeywords.createDatetime}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td> -->
				<shiro:hasPermission name="sms:jmsgSmsKeywords:edit"><td>
    				<!-- <a href="${ctx}/sms/jmsgSmsKeywords/form?id=${jmsgSmsKeywords.id}">修改</a> -->
					<a href="${ctx}/sms/jmsgSmsKeywords/delete?id=${jmsgSmsKeywords.id}&keywords=${jmsgSmsKeywords.keywords}" onclick="return confirmx('确认要删除该敏感词吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>