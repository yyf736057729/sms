<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>号码运营商管理</title>
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
		<li class="active"><a href="${ctx}/sms/jmsgPhoneType/">信息列表</a></li>
		<shiro:hasPermission name="sms:jmsgPhoneType:edit"><li><a href="${ctx}/sms/jmsgPhoneType/form">信息添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="jmsgPhoneType" action="${ctx}/sms/jmsgPhoneType/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>号段：</label>
				<form:input path="num" htmlEscape="false" maxlength="4" class="input-medium"/>
			</li>
			<li><label>运营商：</label>
				<form:select path="phoneType" class="input-medium">
					<form:option value="" label="全部"/>
					<form:options items="${fns:getDictList('phone_type')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
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
				<th>号段</th>
				<th>运营商</th>
				<th>创建日期</th>
				<shiro:hasPermission name="sms:jmsgPhoneType:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="jmsgPhoneType">
			<tr>
				<td><a href="${ctx}/sms/jmsgPhoneType/form?id=${jmsgPhoneType.id}">
					${jmsgPhoneType.num}
				</a></td>
				<td>
					${fns:getDictLabel(jmsgPhoneType.phoneType, 'phone_type', '')}
				</td>
				<td>
					<fmt:formatDate value="${jmsgPhoneType.createDatetime}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<shiro:hasPermission name="sms:jmsgPhoneType:edit"><td>
    				<a href="${ctx}/sms/jmsgPhoneType/form?id=${jmsgPhoneType.id}">修改</a>
					<a href="${ctx}/sms/jmsgPhoneType/delete?num=${jmsgPhoneType.num}" onclick="return confirmx('确认要删除该号码运营商吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>