<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>任务状态报告明细</title>
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
	<!-- <ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/mms/jmsgMmsAlldayReport/reportBylistDetail">任务状态报告明细</a></li>
	</ul> -->
	<form:form id="searchForm" modelAttribute="jmsgMmsAlldayReport" action="${ctx}/mms/jmsgMmsAlldayReport/reportBylistDetail" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input name="taskId" value="${jmsgMmsAlldayReport.taskId}" type="hidden">
		<ul class="ul-form">
			<li><label>手机号码：</label>
				<form:input path="phone" htmlEscape="false" maxlength="11" class="input-medium"/>
			</li>
			<li><label>状态：</label>
				<form:input path="stat" htmlEscape="false" maxlength="11" class="input-medium"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
			<input id="btnClose" class="btn btn-primary" type="button" onclick="window.close();" value="关 闭"/>			
			</li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>手机号码</th>
				<th>状态</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="jmsgMmsAlldayReport">
			<tr>
				<td>
					${jmsgMmsAlldayReport.destTerminalId}
				</td>
				<td>
					${jmsgMmsAlldayReport.stat}
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>