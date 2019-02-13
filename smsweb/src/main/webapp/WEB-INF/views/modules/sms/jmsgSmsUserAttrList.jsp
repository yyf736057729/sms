<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>用户短信属性管理</title>
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
		
		function view(userId){
			var url = "${ctx}/sms/jmsgSmsUserAttr/view?id="+userId;
			windowOpen(url, Math.random(), 600, 300);
		}
		
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/sms/jmsgSmsUserAttr/">信息列表</a></li>
		<shiro:hasPermission name="sms:jmsgSmsUserAttr:edit"><li><a href="${ctx}/sms/jmsgSmsUserAttr/form">信息添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="jmsgSmsUserAttr" action="${ctx}/sms/jmsgSmsUserAttr/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>用户：</label>
				<sys:treeselect id="user" name="user.id" value="${jmsgSmsUserAttr.user.id}" labelName="user.name" labelValue="${jmsgSmsUserAttr.user.name}"
					title="用户" url="/sys/office/treeData?type=3" cssClass="input-small" allowClear="true" notAllowSelectParent="true"/>
			</li>
			<li><label>用户状态 ：</label>
				<form:select path="userStatus" class="input-medium">
					<form:option value="" label="全部"/>
					<form:options items="${fns:getDictList('user_status')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
			</li>
			<li><label>启用全局黑名单：</label>
				<form:select path="globalBlacklist" class="input-medium">
					<form:option value="" label="全部"/>
					<form:options items="${fns:getDictList('yes_no')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
			</li>
			<li><label>启用全局过滤词：</label>
				<form:select path="globalFilter" class="input-medium">
					<form:option value="" label="全部"/>
					<form:options items="${fns:getDictList('yes_no')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
			</li>
			<li><label>重号过滤：</label>
				<form:select path="repeatFilter" class="input-medium">
					<form:option value="" label="全部"/>
					<form:options items="${fns:getDictList('yes_no')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
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
				<th>用户</th>
				<th>用户状态</th>
				<th>签名校验</th>
				<th>审核条数</th>
				<th>启用全局黑名单</th>
				<th>启用全局过滤词</th>
				<th>重号过滤</th>
				<th>创建日期</th>
				<shiro:hasPermission name="sms:jmsgSmsUserAttr:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="jmsgSmsUserAttr">
			<tr>
				<td><a href="${ctx}/sms/jmsgSmsUserAttr/form?id=${jmsgSmsUserAttr.user.id}">
					${jmsgSmsUserAttr.user.name}
				</a></td>
				<td>
					${fns:getDictLabel(jmsgSmsUserAttr.userStatus, 'user_status', '')}
				</td>
				<td>
					${fns:getDictLabel(jmsgSmsUserAttr.signCheck, 'yes_no', '')}
				</td>
				<td>
					${jmsgSmsUserAttr.checkCount}
				</td>
				<td>
					${fns:getDictLabel(jmsgSmsUserAttr.globalBlacklist, 'yes_no', '')}
				</td>
				<td>
					${fns:getDictLabel(jmsgSmsUserAttr.globalFilter, 'yes_no', '')}
				</td>
				<td>
					${fns:getDictLabel(jmsgSmsUserAttr.repeatFilter, 'yes_no', '')}
				</td>
				<td>
					<fmt:formatDate value="${jmsgSmsUserAttr.createDatetime}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<shiro:hasPermission name="sms:jmsgSmsUserAttr:edit"><td>
    				<a href="${ctx}/sms/jmsgSmsUserAttr/form?id=${jmsgSmsUserAttr.user.id}">修改</a>
					<a href="${ctx}/sms/jmsgSmsUserAttr/delete?id=${jmsgSmsUserAttr.user.id}" onclick="return confirmx('确认要删除该用户短信属性吗？', this.href)">删除</a>
					<a href='javascript:view("${jmsgSmsUserAttr.user.id}")'>查看</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>