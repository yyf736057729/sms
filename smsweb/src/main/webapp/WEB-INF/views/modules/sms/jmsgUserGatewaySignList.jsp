<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>用户签名管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#btnMenu").remove();
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
		<li class="active"><a href="${ctx}/sms/jmsgGatewaySign/toUserGatewayList?user.id=${jmsgGatewaySign.user.id}&user.name=${jmsgGatewaySign.user.name}"">信息列表</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="jmsgGatewaySign" action="${ctx}/sms/jmsgGatewaySign/toUserGatewayList" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>用户：</label>
				<sys:treeselect id="user" name="user.id" value="${jmsgGatewaySign.user.id}" labelName="user.name" labelValue="${jmsgGatewaySign.user.name}"
					title="用户" url="/sys/office/treeData?type=3" cssClass="input-small" allowClear="true" notAllowSelectParent="true"/>
			</li>
			<li><label>签名：</label>
				<form:input path="sign" htmlEscape="false" maxlength="255" class="input-medium"/>
			</li>
			<li><label>日期：</label>
				<input name="createTimeQ" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					value="<fmt:formatDate value="${jmsgGatewaySign.createTimeQ}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});"/>
					--
				<input name="createTimeZ" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					value="<fmt:formatDate value="${jmsgGatewaySign.createTimeZ}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
			</li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>用户ID</th>
				<th>用户名称</th>
				<th>通道代码</th>
				<th>通道名称</th>
				<th>签名</th>
				<th>接入号</th>
				<th>备注</th>
				<th>创建时间</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="jmsgGatewaySign">
			<tr>
				<!-- <td><a href="${ctx}/sms/jmsgUserSign/form?id=${jmsgUserSign.id}">
					${jmsgUserSign.user.name}
				</a></td> -->
				<td>
					${jmsgGatewaySign.user.id}
				</td>
				<td>
					${jmsgGatewaySign.user.name}
				</td>
				<td>
					${jmsgGatewaySign.gatewayId}
				</td>
				<td>
					${jmsgGatewaySign.gatewayName}
				</td>
				<td>
					${jmsgGatewaySign.sign}
				</td>
				<td>
					${jmsgGatewaySign.spNumber}
				</td>
				<td>
					${jmsgGatewaySign.note}
				</td>
				<td>
					<fmt:formatDate value="${jmsgGatewaySign.createTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
