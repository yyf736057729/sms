<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>短信审核管理</title>
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
		<li class="active"><a href="${ctx}/sms/jmsgSmsReview/">信息列表</a></li>
		<shiro:hasPermission name="sms:jmsgSmsReview:edit"><li><a href="${ctx}/sms/jmsgSmsReview/form">信息添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="jmsgSmsReview" action="${ctx}/sms/jmsgSmsReview/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>用户ID：</label>
				<sys:treeselect id="user" name="user.id" value="${jmsgSmsReview.user.id}" labelName="user.name" labelValue="${jmsgSmsReview.user.name}"
					title="用户" url="/sys/office/treeData?type=3" cssClass="input-small" allowClear="true" notAllowSelectParent="true"/>
			</li>
			<li><label>短信内容：</label>
				<form:input path="smsContent" htmlEscape="false" maxlength="200" class="input-medium"/>
			</li>
			<li><label>创建时间：</label>
				<input name="createDatetime" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					value="<fmt:formatDate value="${jmsgSmsReview.createDatetime}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>主键</th>
				<th>用户ID</th>
				<th>短信内容</th>
				<th>创建时间</th>
				<shiro:hasPermission name="sms:jmsgSmsReview:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="jmsgSmsReview">
			<tr>
				<td><a href="${ctx}/sms/jmsgSmsReview/form?id=${jmsgSmsReview.id}">
					${jmsgSmsReview.id}
				</a></td>
				<td>
					${jmsgSmsReview.user.name}
				</td>
				<td>
					${jmsgSmsReview.smsContent}
				</td>
				<td>
					<fmt:formatDate value="${jmsgSmsReview.createDatetime}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<shiro:hasPermission name="sms:jmsgSmsReview:edit"><td>
    				<a href="${ctx}/sms/jmsgSmsReview/form?id=${jmsgSmsReview.id}">修改</a>
					<a href="${ctx}/sms/jmsgSmsReview/delete?id=${jmsgSmsReview.id}" onclick="return confirmx('确认要删除该短信审核吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>