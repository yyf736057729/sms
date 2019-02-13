<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>彩信账户信息</title>
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
		<li class="active"><a href="${ctx}/account/jmsgAccount/agencyList">信息列表</a></li>
		<shiro:hasPermission name="jmsg:agency"><li><a href="${ctx}/account/jmsgAccount/agencyForm">信息添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="jmsgAccount" action="${ctx}/account/jmsgAccount/agencyList" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<shiro:hasPermission name="jmsg:agency">
			<li><label>用户：</label>
				<sys:treeselect id="user" name="user.id" value="${jmsgAccount.user.id}" labelName="user.name" labelValue="${jmsgAccount.user.name}"
					title="用户" url="/sys/office/treeData?type=3" cssClass="input-small" allowClear="true" notAllowSelectParent="true"/>
			</li>
			</shiro:hasPermission>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>用户名称</th>
				<th>用户账号</th>
				<th>可用额</th>
				<th>创建时间</th>
				<th>账号状态</th>
				<shiro:hasPermission name="jmsg:agency"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="jmsgAccount">
			<tr>
				<td>
					${jmsgAccount.user.name}
				</td>
				<td>
					${jmsgAccount.user.loginName}
				</td>
				<td>
					${jmsgAccount.money}
				</td>
				<td>
					<fmt:formatDate value="${jmsgAccount.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
					${jmsgAccount.usedFlag eq 1?'启用':'禁用'}
				</td>
				<shiro:hasPermission name="jmsg:agency"><td>
    				<a href="${ctx}/account/jmsgAccount/agencyForm?id=${jmsgAccount.id}">修改</a>
					<a href="${ctx}/account/jmsgAccount/agencyDelete?id=${jmsgAccount.id}&usedFlag=${jmsgAccount.usedFlag}" onclick="return confirmx('确认要${jmsgAccount.usedFlag eq 1?'禁用':'启用'}该彩信账户吗？', this.href)">${jmsgAccount.usedFlag eq 1?'禁用':'启用'}</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>