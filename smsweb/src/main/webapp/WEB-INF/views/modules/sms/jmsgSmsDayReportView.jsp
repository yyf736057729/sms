<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>短信日报表${jmsgSmsSend.reportStatus eq 9 ? "无状态量" : "失败量"}明细</title>
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
	<br>
	<form:form id="searchForm" modelAttribute="jmsgSmsSend" action="${ctx}/sms/jmsgSmsDayReport/onView" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<form:hidden path="user.id"/>
		<form:hidden path="reportStatus"/>
		<input id="sendDatetime" name="sendDatetime" type="hidden" value='<fmt:formatDate value="${jmsgSmsSend.sendDatetime}" pattern="yyyy-MM-dd"/>'/>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>手机号码</th>
				<th>短信内容</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="jmsgSmsSend">
			<tr>
				<td>
					${jmsgSmsSend.phone}
				</td>
				<td>
					${jmsgSmsSend.smsContent}
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>