<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>短信素材管理</title>
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
		
		function onCheckView(id){
			var url = "${ctx}/sms/jmsgSmsData/reviewView?id="+id;
			windowOpen(url,id,600,320);
		}
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/sms/jmsgSmsData/">信息列表</a></li>
		<!-- 权限问题,暂时注解 -->
		<%--<shiro:hasPermission name="jmsg:admin">--%>
		<shiro:hasPermission name="sms:jmsgSmsData:edit"><li><a href="${ctx}/sms/jmsgSmsData/form">信息添加</a></li></shiro:hasPermission>
		<%--</shiro:hasPermission>--%>
	</ul>
	<form:form id="searchForm" modelAttribute="jmsgSmsData" action="${ctx}/sms/jmsgSmsData/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<shiro:hasAnyPermissions name="jmsg:admin,jmsg:agency">		
				<li><label>创建人：</label>
					<sys:treeselect id="user" name="user.id" value="${jmsgSmsData.user.id}" labelName="user.name" labelValue="${jmsgSmsData.user.name}"
						title="用户" url="/sys/office/treeData?type=3" cssClass="input-small" allowClear="true" notAllowSelectParent="true"/>
				</li>
			</shiro:hasAnyPermissions>	
			<li><label>短信ID：</label>
				<form:input path="id" htmlEscape="false" maxlength="200" class="input-small"/>
			</li>
			<li><label>短信内容：</label>
				<form:input path="content" htmlEscape="false" class="input-medium"/>
			</li>
			<shiro:hasPermission name="jmsg:admin">
				<li><label>审核人：</label>
					<form:input path="reviewUserName" htmlEscape="false" maxlength="200" class="input-medium"/>
				</li>
				<li><label>审核状态：</label>
					<form:select path="reviewStatus" class="input-medium" htmlEscape="false">
						<form:option value="">全部</form:option>
						<form:options items="${fns:getDictList('check_status')}" itemLabel="label" itemValue="value"/>
					</form:select>
				</li>
			</shiro:hasPermission>
			<li><label>时间：</label>
				<input name="createDatetimeQ" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					value="<fmt:formatDate value="${jmsgSmsData.createDatetimeQ}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});"/> - 
				<input name="createDatetimeZ" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					value="<fmt:formatDate value="${jmsgSmsData.createDatetimeZ}" pattern="yyyy-MM-dd HH:mm:ss"/>"
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
				<th>短信ID</th>
				<th>短信内容</th>
				<th>创建时间</th>
				<th>审核状态</th>
				<shiro:hasPermission name="jmsg:admin">
				<th>审核人</th>
				</shiro:hasPermission>	
				<shiro:hasAnyPermissions name="jmsg:admin,jmsg:agency">
				<th>创建人</th>
				<th>登录名</th>
				</shiro:hasAnyPermissions>
				<shiro:hasPermission name="jmsg:admin">
				<th>组织</th>
				</shiro:hasPermission>
				<!--<shiro:hasPermission name="sms:jmsgSmsData:edit"><th>操作</th></shiro:hasPermission>-->
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="jmsgSmsData">
						<tr ${jmsgSmsData.reviewStatus eq 0?'style="color: red;"':''} >
				<td>
					${jmsgSmsData.id}
				</td>
				<td>
					${jmsgSmsData.content}
				</td>
				<td>
					<fmt:formatDate value="${jmsgSmsData.createDatetime}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
					${fns:getDictLabel(jmsgSmsData.reviewStatus,'check_status','')} 
					<c:if test="${jmsgSmsData.reviewStatus eq 0 }">&nbsp;&nbsp;<a href="javascript:onCheckView(${jmsgSmsData.id})">查看</a></c:if>
				</td>
				<shiro:hasPermission name="jmsg:admin">
				<td>
					<c:if test="${jmsgSmsData.reviewStatus eq 1 || jmsgSmsData.reviewStatus eq 0}">${jmsgSmsData.reviewUserName}</c:if>
				</td>	
				</shiro:hasPermission>
				<shiro:hasAnyPermissions name="jmsg:admin,jmsg:agency">
				<td>
					${jmsgSmsData.user.name}
				</td>
				<td>
					${jmsgSmsData.user.loginName}
				</td>
				</shiro:hasAnyPermissions>
				<shiro:hasPermission name="jmsg:admin">
				<td>
					${jmsgSmsData.user.company.name}
				</td>	
				</shiro:hasPermission>

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