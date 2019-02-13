<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>短信日报表管理</title>
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
		function onView(userId,day,status){
			//status = 9 无状态量  =0 失败量
			var url="${ctx}/sms/jmsgSmsDayReport/onView?user.id="+userId+"&sendDatetime="+day+"&reportStatus="+status
			windowOpen(url,userId,800,800);
		}
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/sms/jmsgSmsDayReport/">信息列表</a></li>
		<shiro:hasPermission name="sms:jmsgSmsDayReport:edit"><li><a href="${ctx}/sms/jmsgSmsDayReport/form">信息添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="jmsgSmsDayReport" action="${ctx}/sms/jmsgSmsDayReport/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<shiro:hasPermission name="jmsg:admin">
			<li><label>机构：</label><sys:treeselect id="company" name="company.id" value="${jmsgSmsDayReport.company.id}" labelName="company.name" labelValue="${jmsgSmsDayReport.company.name}" 
				title="公司" url="/sys/office/treeData?type=1" cssClass="input-small" allowClear="true"/></li>
			</shiro:hasPermission>
			<shiro:hasAnyPermissions name="jmsg:admin,jmsg:agency">
			<li><label>用户ID：</label>
				<sys:treeselect id="user" name="user.id" value="${jmsgSmsDayReport.user.id}" labelName="user.name" labelValue="${jmsgSmsDayReport.user.name}"
					title="用户" url="/sys/office/treeData?type=3" cssClass="input-small" allowClear="true" notAllowSelectParent="true"/>
			</li>
			</shiro:hasAnyPermissions>
			<li><label>统计时间：</label>
				<input name="dayQ" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					value="<fmt:formatDate value="${jmsgSmsDayReport.dayQ}" pattern="yyyy-MM-dd"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});"/>-
				<input name="dayZ" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					value="<fmt:formatDate value="${jmsgSmsDayReport.dayZ}" pattern="yyyy-MM-dd"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});"/>
			</li>
			<li><label>返充状态 ：</label>
				<form:select path="backFlag" class="input-medium" htmlEscape="false">
					<form:option value="" label="全部"/>
					<form:option value="1" label="已返充"/>
					<form:option value="0" label="未返充"/>
				</form:select>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>统计时间</th>
				<shiro:hasAnyPermissions name="jmsg:admin,jmsg:agency">
				<th width="100px;">用户</th>
				<th>登录名</th>
				</shiro:hasAnyPermissions>
				<shiro:hasPermission name="jmsg:admin">
				<th>机构</th>
				</shiro:hasPermission>
				<th>总发送量</th>
				<th>成功量</th>
				<shiro:hasPermission name="jmsg:admin">
				<th>无状态量</th>
				</shiro:hasPermission>
				<th>失败量</th>
				<th>成功率</th>
				<th>返充状态</th>
				<th>返充时间</th>
				<th>返充用户条数</th>
				<shiro:hasPermission name="jmsg:admin">
				<th>返充代理条数</th>
				</shiro:hasPermission>
				<th>更新时间</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="jmsgSmsDayReport">
			<tr>
				<td>
					<fmt:formatDate value="${jmsgSmsDayReport.day}" pattern="yyyy-MM-dd"/>
				</td>
				<shiro:hasAnyPermissions name="jmsg:admin,jmsg:agency">
				<td>
					${jmsgSmsDayReport.user.name}
				</td>
				<td>
					${jmsgSmsDayReport.user.loginName}
				</td>
				</shiro:hasAnyPermissions>
				<shiro:hasPermission name="jmsg:admin">
				<td>
					${jmsgSmsDayReport.companyName}
				</td>
				</shiro:hasPermission>
				<td>
					${jmsgSmsDayReport.sendCount}
				</td>
				<td>
					${jmsgSmsDayReport.userCount}
				</td>
				<shiro:hasPermission name="jmsg:admin">
				<td>
					<c:if test="${jmsgSmsDayReport.reportNullCount > 0}">
						<a href='javascript:onView("${jmsgSmsDayReport.user.id}","<fmt:formatDate value="${jmsgSmsDayReport.day}" pattern="yyyy-MM-dd"/>","9")'>${jmsgSmsDayReport.reportNullCount}</a>
					</c:if>
					<c:if test="${jmsgSmsDayReport.reportNullCount eq 0}">
						0
					</c:if>	
					
				</td>
				</shiro:hasPermission>
				<td>
					<c:if test="${jmsgSmsDayReport.failCount > 0}">
						<a href='javascript:onView("${jmsgSmsDayReport.user.id}","<fmt:formatDate value="${jmsgSmsDayReport.day}" pattern="yyyy-MM-dd"/>","0")'>${jmsgSmsDayReport.failCount}</a>
					</c:if>
					<c:if test="${jmsgSmsDayReport.failCount eq 0}">
						0
					</c:if>
				</td>
				<td>
					<fmt:formatNumber type="number" value="${jmsgSmsDayReport.userCount/jmsgSmsDayReport.sendCount*100}" maxFractionDigits="2" pattern="0.00"></fmt:formatNumber>%
				</td>	
				<td>
					${jmsgSmsDayReport.backFlag eq 1 ? '已返充':'未返充'}
				</td>
				<td>
					<fmt:formatDate value="${jmsgSmsDayReport.backDatetime}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>	
				<td>
					${jmsgSmsDayReport.userBackCount}
				</td>
				<shiro:hasPermission name="jmsg:admin">
				<td>
					${jmsgSmsDayReport.backCount}
				</td>
				</shiro:hasPermission>
				<td>
					<fmt:formatDate value="${jmsgSmsDayReport.updateDatetime}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>