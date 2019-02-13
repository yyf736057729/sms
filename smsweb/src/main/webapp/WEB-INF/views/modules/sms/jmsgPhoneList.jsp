<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>动态黑名单管理</title>
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
		<li class="active"><a href="${ctx}/sms/jmsgPhone/">信息列表</a></li>
		<shiro:hasPermission name="sms:jmsgPhone:edit"><li><a href="${ctx}/sms/jmsgPhone/form">信息添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="jmsgPhone" action="${ctx}/sms/jmsgPhone/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>手机号码：</label>
				<form:input path="phone" htmlEscape="false" maxlength="20" class="input-medium"/>
			</li>
			<li><label>范围</label>
				<form:select path="scope" class="input-medium" htmlEscape="false">
					<form:option value="">请选择</form:option>
					<form:option value="0">全局</form:option>
					<form:option value="1">用户</form:option>
				</form:select>
			</li>
			<li><label>创建日期：</label>
				<input name="createDatetimeQ" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					value="<fmt:formatDate value="${jmsgPhone.createDatetimeQ}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});"/>
				-
				<input name="createDatetimeZ" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					value="<fmt:formatDate value="${jmsgPhone.createDatetimeZ}" pattern="yyyy-MM-dd HH:mm:ss"/>"
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
				<th>手机号码</th>
				<th>范围</th>
				<th>类型</th>
				<th>备注</th>
				<th>创建日期</th>
				<shiro:hasPermission name="sms:jmsgPhone:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="jmsgPhone">
			<tr>
				<!-- <td><a href="${ctx}/sms/jmsgPhone/form?id=${jmsgPhone.id}">
					${jmsgPhone.phone}
				</a></td> -->
				<td>
					${jmsgPhone.phone}
				</td>
				<td>
					${jmsgPhone.scope eq 0 ? '全局' : '用户'}
				</td>
				<td>
					${jmsgPhone.type eq 1 ? '退订' : '退订'}
				</td>
				<td>
					${jmsgPhone.remarks}
				</td>
				<td>
					<fmt:formatDate value="${jmsgPhone.createDatetime}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<shiro:hasPermission name="sms:jmsgPhone:edit"><td>
    				<!-- <a href="${ctx}/sms/jmsgPhone/form?id=${jmsgPhone.id}">修改</a> -->
					<a href="${ctx}/sms/jmsgPhone/delete?phone=${jmsgPhone.phone}" onclick="return confirmx('确认要删除该动态黑名单吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>