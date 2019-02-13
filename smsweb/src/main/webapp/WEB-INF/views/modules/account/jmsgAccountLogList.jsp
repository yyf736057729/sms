<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>资金变动日志管理</title>
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
		<li class="active"><a href="${ctx}/account/jmsgAccountLog/">资金变动日志列表</a></li>
		<%--<shiro:hasPermission name="jmsg:admin"><li><a href="${ctx}/account/jmsgAccountLog/detailedList">充值记录清单</a></li></shiro:hasPermission>--%>
	</ul>
	<form:form id="searchForm" modelAttribute="jmsgAccountLog" action="${ctx}/account/jmsgAccountLog/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<shiro:hasPermission name="jmsg:admin">
			<li><label>用户：</label>
				<sys:treeselect id="user" name="user.id" value="${jmsgAccountLog.user.id}" labelName="user.name" labelValue="${jmsgAccountLog.user.name}"
					title="用户" url="/sys/office/treeData?type=3" cssClass="input-small" allowClear="true" notAllowSelectParent="true"/>
			</li>
			</shiro:hasPermission>
			<li><label>资金变动类型</label>
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
			<li><label>业务类型：</label>
				<form:select path="appType" class="input-medium">
					<form:option value="" label="全部"/>
					<form:options items="${fns:getDictList('app_type')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
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
				<th>用户名称</th>
				<th>业务类型</th>	
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
					${fns:getDictLabel(jmsgAccountLog.appType, 'app_type', '')}
				</td>
				<td>
					${fns:getDictLabel(fn:substring(jmsgAccountLog.changeType,0,2), 'change_type', '')}
				</td>
				<td>
					<c:if test='${jmsgAccountLog.appType eq "1" || jmsgAccountLog.appType eq "2"}'>
						${jmsgAccountLog.money}
					</c:if>
					<c:if test='${jmsgAccountLog.appType eq "3" || jmsgAccountLog.appType eq "4"}'>
						${jmsgAccountLog.money/100}元
					</c:if>
				</td>
				<td>
					<fmt:formatDate value="${jmsgAccountLog.changeDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
					${jmsgAccountLog.remark}
				</td>
				<shiro:hasPermission name="account:jmsgAccountLog:edit"><td>
    				<a href="${ctx}/account/jmsgAccountLog/form?id=${jmsgAccountLog.id}">修改</a>
					<a href="${ctx}/account/jmsgAccountLog/delete?id=${jmsgAccountLog.id}" onclick="return confirmx('确认要删除该资金变动日志吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>