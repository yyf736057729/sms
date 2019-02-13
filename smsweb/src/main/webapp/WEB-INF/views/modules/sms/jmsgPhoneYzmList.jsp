<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>验证码黑名单管理</title>
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
		<li class="active"><a href="${ctx}/sms/jmsgPhoneYzm/init">信息列表</a></li>
		<shiro:hasPermission name="sms:jmsgPhoneYzm:edit"><li><a href="${ctx}/sms/jmsgPhoneYzm/form">信息添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="jmsgPhoneYzm" action="${ctx}/sms/jmsgPhoneYzm/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>手机号码：</label>
				<form:input path="phone" htmlEscape="false" maxlength="20" class="input-medium"/>
			</li>
			<li><label>创建时间：</label>
				<input name="createtimeQ" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					value="<fmt:formatDate value="${jmsgPhoneYzm.createtimeQ}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});"/> - 
				<input name="createtimeZ" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					value="<fmt:formatDate value="${jmsgPhoneYzm.createtimeZ}" pattern="yyyy-MM-dd HH:mm:ss"/>"
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
				<th>创建人</th>
				<th>创建时间</th>
				<th>备注</th>
				<shiro:hasPermission name="sms:jmsgPhoneYzm:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="jmsgPhoneYzm">
			<tr>
				<td>
					${jmsgPhoneYzm.phone}
				</td>
				<td>
					${fns:getUserById(jmsgPhoneYzm.createBy.id).name}
				</td>
				<td>
					<fmt:formatDate value="${jmsgPhoneYzm.createtime}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
					${jmsgPhoneYzm.remarks}
				</td>
				<shiro:hasPermission name="sms:jmsgPhoneYzm:edit"><td>
					<a href="${ctx}/sms/jmsgPhoneYzm/delete?phone=${jmsgPhoneYzm.phone}" onclick="return confirmx('确认要删除该验证码黑名单吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>