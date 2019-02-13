<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>状态报告管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#btnMenu").remove();
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
	<br/>
	<form:form id="searchForm" modelAttribute="jmsgSmsReport" action="${ctx}/sms/jmsgSmsReport/listBytaskId" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<form:hidden path="taskid"/>
		<ul class="ul-form">
			<li><label>手机号码：</label>
				<form:input path="destTerminalId" htmlEscape="false" maxlength="32" class="input-medium"/>
			</li>
			<li><label>状态报告：</label>
				<form:input path="stat" htmlEscape="false" maxlength="10" class="input-medium"/>
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
				<th>状态报告</th>
				<th>状态报告时间</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="jmsgSmsReport">
			<tr>
				<td>
					${jmsgSmsReport.destTerminalId}
				</td>
				<td>
					${jmsgSmsReport.stat}
				</td>
				<td>
					<fmt:formatDate value="${jmsgSmsReport.createtime}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>		
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>