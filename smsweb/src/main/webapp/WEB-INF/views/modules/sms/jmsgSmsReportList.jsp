<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>状态报告管理</title>
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
		<li class="active"><a href="${ctx}/sms/jmsgSmsReport/">信息列表</a></li>
		<shiro:hasPermission name="sms:jmsgSmsReport:edit"><li><a href="${ctx}/sms/jmsgSmsReport/form">信息添加(暂无页面)</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="jmsgSmsReport" action="${ctx}/sms/jmsgSmsReport/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>用户ID：</label>
				<sys:treeselect id="user" name="user.id" value="${jmsgSmsReport.user.id}" labelName="user.name" labelValue="${jmsgSmsReport.user.name}"
					title="用户" url="/sys/office/treeData?type=3" cssClass="input-small" allowClear="true" notAllowSelectParent="true"/>
			</li>
			<li><label>状态报告：</label>
				<form:input path="stat" htmlEscape="false" maxlength="10" class="input-medium"/>
			</li>
			<li><label>手机号码：</label>
				<form:input path="destTerminalId" htmlEscape="false" maxlength="32" class="input-medium"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>用户ID</th>
				<th>状态报告</th>
				<th>提交时间</th>
				<th>接收时间</th>
				<th>状态报告时间</th>
				<th>发送号码</th>
				<th>接收号码</th>
				<th>网关侧序号</th>
				<shiro:hasPermission name="sms:jmsgSmsReport:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="jmsgSmsReport">
			<tr>
				<td>
					${jmsgSmsReport.user.name}
				</td>
				<td>
					${jmsgSmsReport.stat}
				</td>
				<td>
					${jmsgSmsReport.submitTime}
				</td>
				<td>
					${jmsgSmsReport.doneTime}
				</td>
				<td>
					<fmt:formatDate value="${jmsgSmsReport.createtime}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>		
				<td>
					${jmsgSmsReport.srcid}
				</td>
				<td>
					${jmsgSmsReport.destTerminalId}
				</td>
				<td>
					${jmsgSmsReport.smscSequence}
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>