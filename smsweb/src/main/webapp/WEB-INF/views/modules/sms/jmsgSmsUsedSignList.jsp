<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>用户签名报备</title>
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
		<li class="active"><a href="${ctx}/sms/jmsgSmsReportSign/usedList">信息列表</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="jmsgSmsReportSign" action="${ctx}/sms/jmsgSmsReportSign/usedList" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>日期：</label>
				<input name="dayQ" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					value="<fmt:formatDate value="${jmsgSmsReportSign.dayQ}" pattern="yyyy-MM-dd"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:true});"/> - 
				<input name="dayZ" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					value="<fmt:formatDate value="${jmsgSmsReportSign.dayZ}" pattern="yyyy-MM-dd"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:true});"/>
			</li>
			<li><label>用户名称：</label>
				<sys:treeselect id="user" name="user.id" value="${jmsgSmsReportSign.user.id}" labelName="user.name" labelValue="${jmsgSmsReportSign.user.name}"
					title="用户" url="/sys/office/treeData?type=3" cssClass="input-small" allowClear="true" notAllowSelectParent="true"/>
			</li>
			<li><label>签名：</label>
				<form:input path="smsSign" htmlEscape="false" maxlength="64" class="input-medium"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>日期</th>
				<th>用户ID</th>
				<th>用户名称</th>
				<th>签名</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="jmsgSmsReportSign">
			<tr>
				<td>
					<fmt:formatDate value="${jmsgSmsReportSign.day}" pattern="yyyy-MM-dd"/>
				</td>
				<td>
					${jmsgSmsReportSign.user.id}
				</td>
				<td>
					${jmsgSmsReportSign.user.name}
				</td>
				<td>
					${jmsgSmsReportSign.smsSign}
				</td>
				<!--<shiro:hasPermission name="sms:jmsgSmsReportSign:edit"><td>
    				<a href="${ctx}/sms/jmsgSmsReportSign/form?id=${jmsgSmsReportSign.id}">修改</a>
					<a href="${ctx}/sms/jmsgSmsReportSign/delete?id=${jmsgSmsReportSign.id}" onclick="return confirmx('确认要删除该签名统计报表吗？', this.href)">删除</a>
				</td></shiro:hasPermission>-->
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>