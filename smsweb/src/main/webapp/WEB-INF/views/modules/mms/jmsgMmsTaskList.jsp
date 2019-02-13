<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>彩信发送管理管理</title>
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
		
		function phoneView(taskId){
			var url="${ctx}/mms/jmsgMmsTask/phoneView?taskId="+taskId;
			windowOpen(url,taskId,500,380);
		}
		
		function onView(id){
			var url = "${ctx}/mms/jmsgMmsData/view?id="+id+"&viewFlag=1";
			windowOpen(url,id,800,600);
		}
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/mms/jmsgMmsTask/">彩信发送管理列表</a></li>
		<!--<shiro:hasPermission name="mms:jmsgMmsTask:edit"><li><a href="${ctx}/mms/jmsgMmsTask/form">彩信发送管理添加</a></li></shiro:hasPermission>-->
	</ul>
	<form:form id="searchForm" modelAttribute="jmsgMmsTask" action="${ctx}/mms/jmsgMmsTask/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<shiro:hasAnyPermissions name="jmsg:admin,jmsg:agency">
			<li><label>创建人：</label>
				<sys:treeselect id="user" name="user.id" value="${jmsgMmsTask.user.id}" labelName="user.name" labelValue="${jmsgMmsTask.user.name}"
					title="用户" url="/sys/office/treeData?type=3" cssClass="input-small" allowClear="true" notAllowSelectParent="true"/>
			</li>
			</shiro:hasAnyPermissions>
			<li><label>任务ID：</label>
				<form:input path="id" htmlEscape="false" maxlength="10" class="input-medium"/>
			</li>
			<li><label>彩信ID：</label>
				<form:input path="mmsId" htmlEscape="false" maxlength="10" class="input-medium"/>
			</li>
			<li><label>彩信名称：</label>
				<form:input path="mmsTitle" htmlEscape="false" maxlength="20" class="input-medium"/>
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
				<th>任务ID</th>
				<th>彩信ID</th>
				<th>彩信主题</th>
				<th>发送数量</th>
				<th>发送时间</th>
				<th>结束时间</th>
				<th>状态</th>
				<shiro:hasAnyPermissions name="jmsg:admin,jmsg:agency">
				<th>创建人</th>
				<th>登录名</th>
				</shiro:hasAnyPermissions>
				<shiro:hasPermission name="jmsg:admin">
				<th>机构</th>
				</shiro:hasPermission>
				<shiro:hasPermission name="mms:jmsgMmsTask:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="jmsgMmsTask">
			<tr>
				<td><!--<a href="${ctx}/mms/jmsgMmsTask/form?id=${jmsgMmsTask.id}"></a>-->
					${jmsgMmsTask.id}
				</td>
				<td>
					<a href="javascript:onView('${jmsgMmsTask.mmsId}')">${jmsgMmsTask.mmsId}</a>
				</td>
				<td>
					${jmsgMmsTask.mmsTitle}
				</td>
				<td>
					${jmsgMmsTask.sendCount}
				</td>
				<td>
					<fmt:formatDate value="${jmsgMmsTask.sendDatetime}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
					<fmt:formatDate value="${jmsgMmsTask.endDatetime}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
					${fns:getDictLabel(jmsgMmsTask.status,'task_send_status',jmsgMmsTask.status)} 
				</td>
				<shiro:hasAnyPermissions name="jmsg:admin,jmsg:agency">
				<td>${jmsgMmsTask.user.name}</td>
				<td>${jmsgMmsTask.user.loginName}</td>
				</shiro:hasAnyPermissions>
				<shiro:hasPermission name="jmsg:admin">
				<td>${jmsgMmsTask.user.company.name}</td>
				</shiro:hasPermission>
				<shiro:hasPermission name="mms:jmsgMmsTask:edit">
				<td>
					<a href="javascript:phoneView(${jmsgMmsTask.id})">查看号码清单</a>
					<c:if test="${jmsgMmsTask.status eq 1 || jmsgMmsTask.status eq 2 || jmsgMmsTask.status eq 8}">
						<a href="${ctx}/mms/jmsgMmsTask/updateStatus?id=${jmsgMmsTask.id}&status=9" onclick="return confirmx('确认要停止发送彩信吗？', this.href)">停止发送</a>
						<a href="${ctx}/mms/jmsgMmsTask/updateStatus?id=${jmsgMmsTask.id}&status=5" onclick="return confirmx('确认要暂停发送彩信吗？', this.href)">暂停发送</a>
						<!-- <a href="${ctx}/mms/jmsgMmsTask/updateStatus?id=${jmsgMmsTask.id}&status='1'" onclick="return confirmx('确认要继续发送彩信吗？', this.href)">继续发送</a> -->
					</c:if>
					<c:if test="${jmsgMmsTask.status eq 5}">
						<a href="${ctx}/mms/jmsgMmsTask/updateStatus?id=${jmsgMmsTask.id}&status=9" onclick="return confirmx('确认要停止发送彩信吗？', this.href)">停止发送</a>
						<a href="${ctx}/mms/jmsgMmsTask/updateStatus?id=${jmsgMmsTask.id}&status=8" onclick="return confirmx('确认要继续发送彩信吗？', this.href)">继续发送</a>
					</c:if>	
    				<!-- <a href="${ctx}/mms/jmsgMmsTask/form?id=${jmsgMmsTask.id}">修改</a>
					<a href="${ctx}/mms/jmsgMmsTask/delete?id=${jmsgMmsTask.id}" onclick="return confirmx('确认要删除该彩信发送管理吗？', this.href)">删除</a> -->
				</td>
				</shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>