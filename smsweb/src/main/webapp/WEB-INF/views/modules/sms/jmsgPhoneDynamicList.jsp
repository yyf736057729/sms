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
		
		function sync(){
			$("#searchForm").attr("action","${ctx}/sms/jmsgPhoneDynamic/sync");
			$("#searchForm").submit();
        }	
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/sms/jmsgPhoneDynamic/init">信息列表</a></li>
		<shiro:hasPermission name="sms:jmsgPhoneDynamic:edit"><li><a href="${ctx}/sms/jmsgPhoneDynamic/form">信息添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="jmsgPhoneDynamic" action="${ctx}/sms/jmsgPhoneDynamic/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>手机号码：</label>
				<form:input path="phone" htmlEscape="false" maxlength="20" class="input-medium"/>
			</li>
			
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
							 <input id="btnSync" class="btn btn-primary" type="button" value="同步动态黑名单" onclick="javascript:sync();"/>
							 <font color="green">上次同步时间:${time}</font>
			</li>
			<li class="clearfix"></li>
			
		</ul>
		 
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>手机号码</th>
				<th>创建时间</th>
				<shiro:hasPermission name="sms:jmsgPhoneDynamic:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="jmsgPhoneDynamic">
			<tr>
				<td>
					${jmsgPhoneDynamic.phone}
				</td>
				<td>
					<fmt:formatDate value="${jmsgPhoneDynamic.createtime}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<shiro:hasPermission name="sms:jmsgPhoneDynamic:edit"><td>
    				<!-- <a href="${ctx}/sms/jmsgPhoneDynamic/form?id=${jmsgPhoneDynamic.id}">修改</a> -->
					<a href="${ctx}/sms/jmsgPhoneDynamic/delete?phone=${jmsgPhoneDynamic.phone}" onclick="return confirmx('确认要删除该动态黑名单吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>