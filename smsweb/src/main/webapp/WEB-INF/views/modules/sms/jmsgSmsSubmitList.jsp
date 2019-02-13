<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>网关状态管理</title>
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
		<li class="active"><a href="${ctx}/sms/jmsgSmsSubmit/">信息列表</a></li>
		<shiro:hasPermission name="sms:jmsgSmsSubmit:edit"><li><a href="${ctx}/sms/jmsgSmsSubmit/form">信息添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="jmsgSmsSubmit" action="${ctx}/sms/jmsgSmsSubmit/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<!--
				<th>msgid</th>
				<th>result</th>
				<th>bizid</th>
				<th>createtime</th>
				<th>taskid</th>
				<th>userid</th>
				<th>gatewayid</th>
				<th>reserve</th>
				<th>phone</th>
				-->
				<shiro:hasPermission name="sms:jmsgSmsSubmit:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="jmsgSmsSubmit">
			<tr>
				<!--
				<td>
					$ {jmsgSmsSubmit.msgid}
				</td>
				<td>
					$ {jmsgSmsSubmit.result}
				</td>
				<td>
					$ {jmsgSmsSubmit.bizid}
				</td>
				<td>
					$ {jmsgSmsSubmit.createtime}
				</td>
				<td>
					$ {jmsgSmsSubmit.taskid}
				</td>
				<td>
					$ {jmsgSmsSubmit.userid}
				</td>
				<td>
					$ {jmsgSmsSubmit.gatewayid}
				</td>
				<td>
					$ {jmsgSmsSubmit.reserve}
				</td>
				<td>
					$ {jmsgSmsSubmit.phone}
				</td>
				-->
				<shiro:hasPermission name="sms:jmsgSmsSubmit:edit">
					<td>
    					<a href="${ctx}/sms/jmsgSmsSubmit/form?id=${jmsgSmsSubmit.id}">修改</a>
						<a href="${ctx}/sms/jmsgSmsSubmit/delete?id=${jmsgSmsSubmit.id}" onclick="return confirmx('确认要删除该网关状态吗？', this.href)">删除</a>
					</td>
				</shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>