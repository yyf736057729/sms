<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>充值记录清单</title>
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
		<%-- <li ><a href="${ctx}/account/jmsgAccountLog/">资金变动日志列表</a></li> --%>
		<li class="active"><a href="${ctx}/account/jmsgAccountLog/detailedList">信息列表</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="jmsgAccountLog" action="${ctx}/account/jmsgAccountLog/detailedList" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>机构：</label>
				<sys:treeselect id="company" name="company.id" value="${jmsgAccountLog.company.id}" labelName="company.name" labelValue="${jmsgAccountLog.company.name}" 
				title="机构" url="/sys/office/treeData?type=1" cssClass="input-small" allowClear="true"/>
			</li>
			<li><label>用户：</label>
				<sys:treeselect id="user" name="user.id" value="${jmsgAccountLog.user.id}" labelName="user.name" labelValue="${jmsgAccountLog.user.name}"
					title="用户" url="/sys/office/treeData?type=3" cssClass="input-small" allowClear="true" notAllowSelectParent="true"/>
			</li>
			<li><label>类型：</label>
				<form:select path="changeType" class="input-medium">
					<form:option value="" label="全部"/>
					<form:options items="${fns:getDictList('payment')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
			</li>
			<li><label>账户类型：</label>
				<form:select path="user.userType" class="input-medium">
					<form:option value="" label="全部"/>
					<form:options items="${fns:getDictList('user_type')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
					<form:option value="2">其他</form:option>
				</form:select>
			</li>			
			<li><label>时间：</label>
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
				<th>机构</th>
				<th>类型</th>
				<th>操作时间</th>
				<th>条数</th>
				<th>业务类型</th>
				<th>登录名</th>
				<th>用户ID</th>
				<th>姓名</th>
				<th>备注</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="jmsgAccountLog">
			<tr>
				<td>${jmsgAccountLog.company.name}</td>
				<td>
					${fns:getDictLabel(jmsgAccountLog.changeType, 'payment', '')}
				</td>
				<td>
					<fmt:formatDate value="${jmsgAccountLog.changeDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>	
				<td>
					<c:if test='${jmsgAccountLog.appType eq "sms" || jmsgAccountLog.appType eq "mms"}'>
						${jmsgAccountLog.money}
					</c:if>
					<c:if test='${jmsgAccountLog.appType eq "flow" || jmsgAccountLog.appType eq "telfel"}'>
						${jmsgAccountLog.money/100}
					</c:if>
				</td>
				<td>${fns:getDictLabel(jmsgAccountLog.appType, 'app_type', '')}</td>
				<td>${jmsgAccountLog.user.loginName}</td>
				<td>${jmsgAccountLog.user.id}</td>	
				<td>${jmsgAccountLog.user.name}</td>
				<td>${jmsgAccountLog.remark}</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>