<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>用户日报表</title>
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
		<li class="active"><a href="${ctx}/sms/jmsgSmsSend/listByUserDayReport">用户日报表</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="jmsgSmsSend" action="${ctx}/sms/jmsgSmsSend/listByUserDayReport" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>用户：</label>
				<sys:treeselect id="user" name="user.id" value="${jmsgSmsSend.user.id}" labelName="user.name" labelValue="${jmsgSmsSend.user.name}"
					title="用户" url="/sys/office/treeData?type=3" cssClass="input-small" allowClear="true" notAllowSelectParent="true"/>
			</li>
			<li><label>统计日期：</label>
				<input name="sendDatetimeQ" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					value="<fmt:formatDate value="${jmsgSmsSend.sendDatetimeQ}" pattern="yyyy-MM-dd"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:true});"/> - 
				<input name="sendDatetimeZ" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					value="<fmt:formatDate value="${jmsgSmsSend.sendDatetimeZ}" pattern="yyyy-MM-dd"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:true});"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>统计日期</th>
				<th>用户账号</th>
				<th>发送总量</th>
				<shiro:hasPermission name="sms:jmsgSmsSend:userDayReprot:status">
				<th>状态成功</th>
				<th>未知状态</th>
				<th>通道失败</th>
				</shiro:hasPermission>
				<shiro:hasPermission name="sms:jmsgSmsSend:userDayReprot:submit">
				<th>发送成功</th>
				<th>发送失败</th>
				<th>发送中</th>
				</shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="jmsgSmsSend">
			<tr>
				<td>
					<fmt:formatDate value="${jmsgSmsSend.sendDatetime}" pattern="yyyy-MM-dd"/>
				</td>
				<td>
					${jmsgSmsSend.user.name}
				</td>
				<td>
					${jmsgSmsSend.count}
				</td>
				<shiro:hasPermission name="sms:jmsgSmsSend:userDayReprot:status">
				<td>
					${jmsgSmsSend.reportTCount}
				</td>
				<td>
					${jmsgSmsSend.reportUNCount}
				</td>
				<td>
					${jmsgSmsSend.count-jmsgSmsSend.reportTCount-jmsgSmsSend.reportUNCount}
				</td>
				</shiro:hasPermission>
				<shiro:hasPermission name="sms:jmsgSmsSend:userDayReprot:submit">
				<td>
					${jmsgSmsSend.sendTCount}
				</td>
				<td>
					${jmsgSmsSend.sendFCount}
				</td>
				<td>
					${jmsgSmsSend.count-jmsgSmsSend.sendTCount-jmsgSmsSend.sendFCount}
				</td>
				</shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>