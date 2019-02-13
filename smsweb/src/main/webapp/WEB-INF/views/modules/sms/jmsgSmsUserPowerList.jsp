<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>用户短信能力管理</title>
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
		<li class="active"><a href="${ctx}/sms/jmsgSmsUserPower/">信息列表</a></li>
		<shiro:hasPermission name="sms:jmsgSmsUserPower:edit"><li><a href="${ctx}/sms/jmsgSmsUserPower/form">信息添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="jmsgSmsUserPower" action="${ctx}/sms/jmsgSmsUserPower/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>用户：</label>
				<sys:treeselect id="user" name="user.id" value="${jmsgSmsUserPower.user.id}" labelName="user.name" labelValue="${jmsgSmsUserPower.user.name}"
					title="用户" url="/sys/office/treeData?type=3" cssClass="input-small" allowClear="true" notAllowSelectParent="true"/>
			</li>
			<li><label>运营商：</label>
				<form:select path="phoneType" class="input-medium">
					<form:option value="" label="全部"/>
					<form:options items="${fns:getDictList('phone_type')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
			</li>
			<li><label>省份：</label>
				<form:select path="areaCode" class="input-medium">
					<form:option value="" label="全国"/>
					<form:options items="${fns:getDictList('area_type')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
			</li>
			<!-- <li><label>通道代码：</label>
				<form:select path="channelCode" class="input-medium">
					<form:option value="" label=""/>
					<form:options items="${fns:getDictList('')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
			</li> -->
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>用户</th>
				<th>运营商</th>
				<th>省份</th>
				<th>通道代码</th>
				<th>优先级</th>
				<shiro:hasPermission name="sms:jmsgSmsUserPower:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="jmsgSmsUserPower">
			<tr>
				<td><a href="${ctx}/sms/jmsgSmsUserPower/form?id=${jmsgSmsUserPower.id}">
					${jmsgSmsUserPower.user.name}
				</a></td>
				<td>
					${fns:getDictLabel(jmsgSmsUserPower.phoneType, 'phone_type', '')}
				</td>
				<td>
					${fns:getDictLabel(jmsgSmsUserPower.areaCode, 'area_type', '')}
				</td>
				<td>
					${fns:getDictLabel(jmsgSmsUserPower.channelCode, '', '')}
				</td>
				<td>
					${jmsgSmsUserPower.level}
				</td>
				<shiro:hasPermission name="sms:jmsgSmsUserPower:edit"><td>
    				<a href="${ctx}/sms/jmsgSmsUserPower/form?id=${jmsgSmsUserPower.id}">修改</a>
					<a href="${ctx}/sms/jmsgSmsUserPower/delete?id=${jmsgSmsUserPower.id}" onclick="return confirmx('确认要删除该用户短信能力吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>