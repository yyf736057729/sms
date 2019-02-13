<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>短信发送管理</title>
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
		<li class="active"><a href="${ctx}/sms/jmsgSmsSend/list">信息列表</a></li>
		<shiro:hasPermission name="sms:jmsgSmsSend:edit"><li><a href="${ctx}/sms/jmsgSmsSend/form">信息添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="jmsgSmsSend" action="${ctx}/sms/jmsgSmsSend/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>主键：</label>
				<form:input path="id" htmlEscape="false" maxlength="20" class="input-medium"/>
			</li>
			<li><label>手机号码：</label>
				<form:input path="phone" htmlEscape="false" maxlength="20" class="input-medium"/>
			</li>
			<li><label>短信内容：</label>
				<form:input path="smsContent" htmlEscape="false" maxlength="2000" class="input-medium"/>
			</li>
			<li><label>用户ID：</label>
				<sys:treeselect id="user" name="user.id" value="${jmsgSmsSend.user.id}" labelName="user.name" labelValue="${jmsgSmsSend.user.name}"
					title="用户" url="/sys/office/treeData?type=3" cssClass="input-small" allowClear="true" notAllowSelectParent="true"/>
			</li>
			<li><label>发送时间：</label>
				<input name="sendDatetime" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					value="<fmt:formatDate value="${jmsgSmsSend.sendDatetime}" pattern="yyyy-MM-dd HH:mm:ss"/>"
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
				<th>手机号码</th>
				<th>短信内容</th>
				<th>发送状态T成功 F失败 P处理中 R审核</th>
				<th>发送时间</th>
				<th>公司ID</th>
				<shiro:hasPermission name="sms:jmsgSmsSend:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="jmsgSmsSend">
			<tr>
				<td><a href="${ctx}/sms/jmsgSmsSend/form?id=${jmsgSmsSend.id}">
					${jmsgSmsSend.id}
				</a></td>
				<td>
					${jmsgSmsSend.phone}
				</td>
				<td>
					${jmsgSmsSend.smsContent}
				</td>
				<td>
					${jmsgSmsSend.sendStatus}
				</td>
				<td>
					<fmt:formatDate value="${jmsgSmsSend.sendDatetime}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
					${jmsgSmsSend.companyId}
				</td>
				<shiro:hasPermission name="sms:jmsgSmsSend:edit"><td>
    				<a href="${ctx}/sms/jmsgSmsSend/form?id=${jmsgSmsSend.id}">修改</a>
					<a href="${ctx}/sms/jmsgSmsSend/delete?id=${jmsgSmsSend.id}" onclick="return confirmx('确认要删除该短信发送吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>