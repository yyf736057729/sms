<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>告警表管理</title>
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
		<li class="active"><a href="${ctx}/sms/jmsgSmsWarn/warnList">信息列表</a></li>
		<!--<shiro:hasPermission name="sms:jmsgSmsWarn:edit"><li><a href="${ctx}/sms/jmsgSmsWarn/form">信息添加</a></li></shiro:hasPermission>-->
	</ul>
	<form:form id="searchForm" modelAttribute="jmsgSmsWarn" action="${ctx}/sms/jmsgSmsWarn/warnList" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>告警类型</th>
				<th>告警内容</th>
				<th>告警状态</th>
				<th>告警时间</th>
				<shiro:hasPermission name="sms:jmsgSmsWarn:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="jmsgSmsWarn">
			<tr>
				<td>
					${jmsgSmsWarn.warnType eq 1 ? '网关告警' : ''}
				</td>
				<td>
					${jmsgSmsWarn.warnContent}
				</td>
				<td>
					${jmsgSmsWarn.warnStatus eq 1 ? '已处理' : '未处理'}
				</td>
				<td>
					<fmt:formatDate value="${jmsgSmsWarn.createTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<c:if test="${jmsgSmsWarn.warnStatus eq '0'}">
					<shiro:hasPermission name="sms:jmsgSmsWarn:edit">
						<td>
							<a href="${ctx}/sms/jmsgSmsWarn/updateStatus?warnStatus=1&id=${jmsgSmsWarn.id}" onclick="return confirmx('确认告警已处理吗？', this.href)">处理</a>
						</td>
					</shiro:hasPermission>
				</c:if>
				<c:if test="${jmsgSmsWarn.warnStatus eq '1'}">
						<td>
						</td>
				</c:if>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<!--
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<th>网关编码</th>
			<th>5分钟内失败次数</th>
			<th>连续失败次数</th>
			<th>网关状态</th>
			<th>运行状态</th>
			<th>统计时间</th>
		</thead>
		<tbody>
		< c : f orEach items="$ {page.list}" var="jmsgGatewayMonitor">
		<tr>
			<td>
				$ {jmsgGatewayMonitor.gatewayId}
			</td>
			<td>
				$ {jmsgGatewayMonitor.timeFailCount}
			</td>
			<td>
				$ {jmsgGatewayMonitor.continuousFailCount}
			</td>
			<td>
				$ {jmsgGatewayMonitor.status eq 1 ? '启用' : '禁用'}
			</td>
			<td>
				$ {jmsgGatewayMonitor.gatewayStatus eq 1 ? '<label style="color: green;">运行</label>' : '<label style="color: red;">停止</label>'}
			</td>
			<td>
				< f m t : formatDate value=" $ { jmsgGatewayMonitor.createTime } " pattern="yyyy-MM-dd HH:mm:ss"/>
			</td>
			</tr>
		< / c : f orEach>
		</tbody>
	</table>
	-->
	<div class="pagination">${page}</div>
</body>
</html>