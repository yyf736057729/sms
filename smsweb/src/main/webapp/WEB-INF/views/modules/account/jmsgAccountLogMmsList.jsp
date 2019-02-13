<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>彩信变动日志管理</title>
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
		<li class="active"><a href="${ctx}/account/jmsgAccountLog/mmsList?appType=${jmsgAccountLog.appType}">${fns:getDictLabel(jmsgAccountLog.appType, 'app_type', '')}账号变动列表</a></li>
		<li><a href="${ctx}/account/jmsgAccountLog/detailedListAgency?appType=${jmsgAccountLog.appType}">充值记录清单</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="jmsgAccountLog" action="${ctx}/account/jmsgAccountLog/mmsList" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<form:hidden path="appType" value="${jmsgAccountLog.appType }"/>
		<ul class="ul-form">
			<shiro:hasPermission name="jmsg:agency">
			<li><label>用户：</label>
				<sys:treeselect id="user" name="user.id" value="${jmsgAccountLog.user.id}" labelName="user.name" labelValue="${jmsgAccountLog.user.name}"
					title="用户" url="/sys/office/treeData?type=3" cssClass="input-small" allowClear="true" notAllowSelectParent="true"/>
			</li>
			</shiro:hasPermission>
			<li><label>变动类型</label>
				<form:select path="changeType" class="input-medium">
					<form:option value="" label="全部"/>
					<form:options items="${fns:getDictList('change_type')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
			</li>
			<li><label>变动时间：</label>
				<input name="changeDateQ" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					value="<fmt:formatDate value="${jmsgAccountLog.changeDateQ}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:true});"/>-
				<input name="changeDateZ" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					value="<fmt:formatDate value="${jmsgAccountLog.changeDateZ}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:true});"/>
			</li>
			<!-- <li><label>账户类型：</label>
				<form:select path="appType" class="input-medium">
					<form:option value="" label="全部"/>
					<form:options items="${fns:getDictList('app_type')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
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
				<th>用户名称</th>
				<th>用户账号</th>
				<!-- <th>账户类型</th> -->	
				<th>变动类型</th>
				<th>变动额度</th>
				<th>变动时间</th>
				<th>备注</th>
				<!--<shiro:hasPermission name="account:jmsgAccountLog:edit"><th>操作</th></shiro:hasPermission>-->
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="jmsgAccountLog">
			<tr>
				<td>
					${jmsgAccountLog.user.name}
				</td>
				<td>
					${jmsgAccountLog.user.loginName}
				</td>
				<td>
					${fns:getDictLabel(fn:substring(jmsgAccountLog.changeType,0,2), 'change_type', '')}
				</td>
				<td>
					${jmsgAccountLog.money}
				</td>
				<td>
					<fmt:formatDate value="${jmsgAccountLog.changeDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
					${jmsgAccountLog.remark}
				</td>
				<!--<shiro:hasPermission name="account:jmsgAccountLog:edit"><td>
    				<a href="${ctx}/account/jmsgAccountLog/form?id=${jmsgAccountLog.id}">修改</a>
					<a href="${ctx}/account/jmsgAccountLog/delete?id=${jmsgAccountLog.id}" onclick="return confirmx('确认要删除该资金变动日志吗？', this.href)">删除</a>
				</td></shiro:hasPermission>-->
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>