<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>规则关系管理</title>
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
		<li class="active"><a href="${ctx}/sms/jmsgRuleRelation/">信息列表</a></li>
		<shiro:hasPermission name="sms:jmsgRuleRelation:edit"><li><a href="${ctx}/sms/jmsgRuleRelation/form">信息添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="jmsgRuleRelation" action="${ctx}/sms/jmsgRuleRelation/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>规则分组：</label>
				<form:select path="groupId" class="input-medium" htmlEscape="false" >
					<form:option value="">--请选择--</form:option>
					<form:options items="${jmsgRuleRelation.jmsgRuleGroupList}" itemLabel="groupName" itemValue="id" htmlEscape="false"/>
				</form:select>
			</li>
			<li><label>规则分类：</label>
				<form:select path="ruleType" class="input-medium" htmlEscape="false" >
					<form:option value="">--请选择--</form:option>
					<form:option value="1">网址</form:option>
					<form:option value="2">电话</form:option>
					<%-- <form:option value="3">关键字</form:option> --%>
					<form:option value="4">正则式</form:option>
				</form:select>
			</li>
			<li><label>规则名称：</label>
				<form:select path="ruleId" class="input-xlarge" htmlEscape="false" >
					<form:option value="">--请选择--</form:option>
					<form:options items="${jmsgRuleRelation.jmsgRuleInfoList}" itemLabel="ruleName" itemValue="id" htmlEscape="false"/>
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
				<!-- <th>id</th> -->
				<th>规则分组</th>
				<th>规则分类</th>
				<th>规则名称</th>
				<shiro:hasPermission name="sms:jmsgRuleRelation:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="jmsgRuleRelation">
			<tr>
				<%-- <td><a href="${ctx}/sms/jmsgRuleRelation/form?id=${jmsgRuleRelation.id}">
					${jmsgRuleRelation.id}
				</a></td> --%>
				<td>
					${jmsgRuleRelation.jmsgRuleGroup.groupName}
				</td>
				<td>
					${jmsgRuleRelation.ruleType eq 1 ? '网址' : jmsgRuleRelation.ruleType eq 2 ? '电话' : jmsgRuleRelation.ruleType eq 3 ? '关键字' : '正则式'}
				</td>
				<td>
					${jmsgRuleRelation.jmsgRuleInfo.ruleName}
				</td>
				<shiro:hasPermission name="sms:jmsgRuleRelation:edit"><td>
    				<%-- <a href="${ctx}/sms/jmsgRuleRelation/form?id=${jmsgRuleRelation.id}">修改</a> --%>
					<a href="${ctx}/sms/jmsgRuleRelation/delete?id=${jmsgRuleRelation.id}" onclick="return confirmx('确认要删除该规则关系吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>