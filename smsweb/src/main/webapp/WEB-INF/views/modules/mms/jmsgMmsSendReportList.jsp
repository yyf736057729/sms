<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>彩信发送统计</title>
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
		<li class="active"><a href="${ctx}/mms/jmsgMmsTask/mmsSendTongji">彩信发送统计</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="jmsgMmsTask" action="${ctx}/mms/jmsgMmsTask/mmsSendTongji" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<shiro:hasPermission name="jmsg:admin">
			<li><label>机构：</label>
				<sys:treeselect id="company" name="company.id" value="${jmsgMmsTask.company.id}" labelName="company.name" labelValue="${jmsgMmsTask.company.name}" 
				title="机构" url="/sys/office/treeData?type=1" cssClass="input-small" allowClear="true"/>
			</li>
			</shiro:hasPermission>
			<shiro:hasAnyPermissions name="jmsg:admin,jmsg:agency">
			<li><label>创建人：</label>
				<sys:treeselect id="user" name="user.id" value="${jmsgMmsTask.user.id}" labelName="user.name" labelValue="${jmsgMmsTask.user.name}"
					title="用户" url="/sys/office/treeData?type=3" cssClass="input-small" allowClear="true" notAllowSelectParent="true"/>
			</li>
			</shiro:hasAnyPermissions>
		
			<li><label>彩信ID：</label>
				<form:input path="mmsId" htmlEscape="false" maxlength="10" class="input-medium"/>
			</li>
			<li><label>彩信主题：</label>
				<form:input path="mmsTitle" htmlEscape="false" maxlength="20" class="input-medium"/>
			</li>
			<li><label>任务ID：</label>
				<form:input path="id" htmlEscape="false" maxlength="10" class="input-medium"/>
			</li>
			<li><label>发送时间：</label>
				<input name="sendDatetimeQ" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					value="<fmt:formatDate value="${jmsgMmsTask.sendDatetimeQ}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:true});"/> - 
				<input name="sendDatetimeZ" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					value="<fmt:formatDate value="${jmsgMmsTask.sendDatetimeZ}" pattern="yyyy-MM-dd HH:mm:ss"/>"
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
				<shiro:hasAnyPermissions name="jmsg:admin,jmsg:agency">
				<th>创建人</th>
				<th>登录名</th>
				</shiro:hasAnyPermissions>
				<shiro:hasPermission name="jmsg:admin">
				<th>机构</th>
				</shiro:hasPermission>
				<th>彩信ID</th>
				<th>彩信主题</th>
				<th>任务ID</th>
				<th>发送时间</th>
				<th>发送量</th>
				<th>成功量</th>
				<th>统计时间</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="jmsgMmsTask">
			<tr>
				<shiro:hasAnyPermissions name="jmsg:admin,jmsg:agency">
				<td>${jmsgMmsTask.user.name}</td>
				<td>${jmsgMmsTask.user.loginName}</td>
				</shiro:hasAnyPermissions>
				<shiro:hasPermission name="jmsg:admin">
				<td>${jmsgMmsTask.user.company.name}</td>
				</shiro:hasPermission>
				<td>
					${jmsgMmsTask.mmsId}
				</td>
				<td>
					${jmsgMmsTask.mmsTitle}
				</td>
				<td>
					${jmsgMmsTask.id}
				</td>
				<td>
					<fmt:formatDate value="${jmsgMmsTask.sendDatetime}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>	
				<td>
					${jmsgMmsTask.sendCount}
				</td>
				<td>
					${jmsgMmsTask.successCount}
				</td>
				<td>
					<fmt:formatDate value="${jmsgMmsTask.tongjiDatetime}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>