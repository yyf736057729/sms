<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>网关状态管理</title>
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
		<li class="active"><a href="${ctx}/sms/jmsgSmsSubmit/findErrorForReSend">信息列表</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="jmsgSmsSubmit" action="${ctx}/sms/jmsgSmsSubmit/findErrorForReSend" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>手机号码：</label>
				<form:input path="phone" htmlEscape="false" maxlength="11" class="input-small"/>
			</li>
			<li><label>结果：</label>
				<form:select path="result" class="input-medium" htmlEscape="false" >
					<form:option value="">--请选择--</form:option>
					<form:option value="0">成功</form:option>
					<form:option value="1">失败</form:option>
				</form:select>
			</li>
			<li><label>时间：</label>
				<input name="createtimeQ" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					value="<fmt:formatDate value="${jmsgSmsSubmit.createtimeQ}" pattern="yyyy-MM-dd"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});"/> - 
				<input name="createtimeZ" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					value="<fmt:formatDate value="${jmsgSmsSubmit.createtimeZ}" pattern="yyyy-MM-dd"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});"/>					
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
				<th>网关消息ID</th>
				<th>业务ID</th>
				<th>任务ID</th>
				<th>用户ID</th>
				<th>网关ID</th>
				<th>描述</th>
				<th>时间</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="jmsgSmsSubmit">
			<tr>
				<td>
					${jmsgSmsSubmit.phone}
				</td>
				<td>
					${jmsgSmsSubmit.msgid}
				</td>
				<td>
					${jmsgSmsSubmit.bizid}
				</td>
				<td>
					${jmsgSmsSubmit.taskid}
				</td>
				<td>
					${jmsgSmsSubmit.userid}
				</td>
				<td>
					${jmsgSmsSubmit.gatewayid}
				</td>
				<td>
					${jmsgSmsSubmit.reserve}
				</td>
				<td>
					<fmt:formatDate value="${jmsgSmsSubmit.createtime}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<!--<shiro:hasPermission name="sms:jmsgSmsData:edit"><td>
    				<a href="${ctx}/sms/jmsgSmsData/form?id=${jmsgSmsData.id}">修改</a>
					<a href="${ctx}/sms/jmsgSmsData/delete?id=${jmsgSmsData.id}" onclick="return confirmx('确认要删除该短信素材吗？', this.href)">删除</a>
				</td></shiro:hasPermission>-->
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>