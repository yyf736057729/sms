<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>订单信息管理</title>
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
		<li class="active"><a href="${ctx}/order/jmsgOrder/">订单信息列表</a></li>
		<shiro:hasPermission name="order:jmsgOrder:edit"><li><a href="${ctx}/order/jmsgOrder/form">订单信息添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="jmsgOrder" action="${ctx}/order/jmsgOrder/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<shiro:hasPermission name="jmsg:admin">
			<li><label>用户：</label>
				<sys:treeselect id="user" name="user.id" value="${jmsgOrder.user.id}" labelName="user.name" labelValue="${jmsgOrder.user.name}"
					title="用户" url="/sys/office/treeData?type=3" cssClass="input-small" allowClear="true" notAllowSelectParent="true"/>
			</li>
			</shiro:hasPermission>
			<li><label>系统订单号：</label>
				<form:input path="systemOrderId" htmlEscape="false" maxlength="64" class="input-medium"/>
			</li>
			<li><label>客户订单号：</label>
				<form:input path="customerOrderId" htmlEscape="false" maxlength="64" class="input-medium"/>
			</li>
			<li><label>应用类型</label>
				<form:select path="appType" class="input-medium">
					<form:option value="" label="全部"/>
					<form:options items="${fns:getDictList('app_type')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
			</li>
			<li><label>提交方式：</label>
				<form:select path="submitType" class="input-medium">
					<form:option value="" label="全部"/>
					<form:options items="${fns:getDictList('submit_type')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
			</li>
			<li><label>订单状态：</label>
				<form:select path="orderStatus" class="input-medium">
					<form:option value="" label="全部"/>
					<form:options items="${fns:getDictList('order_status')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
			</li>
			<li><label>订单时间：</label>
				<input name="orderDateQ" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					value="<fmt:formatDate value="${jmsgOrder.orderDateQ}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:true});"/> - 
				<input name="orderDateZ" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					value="<fmt:formatDate value="${jmsgOrder.orderDateZ}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:true});"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>用户</th>
				<th>系统订单号</th>
				<th>客户订单号</th>
				<th>应用类型</th>
				<th>提交方式</th>
				<th>订单状态</th>
				<th>订单时间</th>
				<th>审核备注</th>
				<shiro:hasPermission name="order:jmsgOrder:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="jmsgOrder">
			<tr>
				<td>
					${jmsgOrder.user.name}
				</td>
				<td>
					${jmsgOrder.systemOrderId}
				</td>
				<td>
					${jmsgOrder.customerOrderId}
				</td>
				<td>
					${fns:getDictLabel(jmsgOrder.appType, 'app_type', '')}
				</td>
				<td>
					${fns:getDictLabel(jmsgOrder.submitType, 'submit_type', '')}
				</td>
				<td>
					${fns:getDictLabel(jmsgOrder.orderStatus, 'order_status', '')}
				</td>
				<td>
					<fmt:formatDate value="${jmsgOrder.orderDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
					${jmsgOrder.checkRemark}
				</td>
				<shiro:hasPermission name="order:jmsgOrder:edit"><td>
    				<a href="${ctx}/order/jmsgOrder/form?id=${jmsgOrder.id}">修改</a>
					<a href="${ctx}/order/jmsgOrder/delete?id=${jmsgOrder.id}" onclick="return confirmx('确认要删除该订单信息吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>