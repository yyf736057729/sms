<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>短信日报表管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#btnExport").click(function(){
				top.$.jBox.confirm("确认要导出用户统计日报表数据吗？","系统提示",function(v,h,f){
					if(v=="ok"){
						$("#searchForm").attr("action","${ctx}/sms/jmsgSmsDayReport/exportByDay");
						$("#searchForm").submit();
					}
				},{buttonsFocus:1});
				top.$('.jbox-body .jbox-icon').css('top','55px');
			});
		});
		function page(n,s){
			if(n) $("#pageNo").val(n);
			if(s) $("#pageSize").val(s);
			$("#searchForm").attr("action","${ctx}/sms/jmsgSmsDayReport/list");
			$("#searchForm").submit();
        	return false;
        }	
		function onView(userId,day,status){
			//status = 9 无状态量  =0 失败量
			var url="${ctx}/sms/jmsgSmsDayReport/onView?user.id="+userId+"&sendDatetime="+day+"&reportStatus="+status
			windowOpen(url,userId,800,800);
		}
		
		function viewByPhoneType(user,day,queryType,name){
			//status = 9 无状态量  =0 失败量
			var url="${ctx}/sms/jmsgSmsDayReport/viewByPhoneType?user.id="+user+"&day="+day+"&queryType="+queryType+"&user.name="+name
			windowOpen(url,userId+day,600,400);
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
			<li><label>用户名称：</label>
				<sys:treeselect id="user" name="user.id" value="${jmsgSmsDayReport.user.id}" labelName="user.name" labelValue="${jmsgSmsDayReport.user.name}"
					title="用户" url="/sys/office/treeData?type=3" cssClass="input-small" allowClear="true" notAllowSelectParent="true"/>
			</li>
			<li><label>用户ID：</label>
				<form:input path="userId" class="input-medium" maxlength="10"/>
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
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询" onclick="return page();"/>
						<%-- <shiro:hasPermission name="jmsg:admin"><input id="btnExport" class="btn btn-primary" type="button" value="导出"/></shiro:hasPermission> --%>
			</li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<shiro:hasPermission name="jmsg:admin">
				<th>操作</th>
				</shiro:hasPermission>
				<th>统计时间　　</th>
				<shiro:hasAnyPermissions name="jmsg:admin,jmsg:agency">
				<th>用户名称　　　　　　　　</th>
				<th>用户ID　</th>
				<th>登录账号　　　</th>
				</shiro:hasAnyPermissions>
				<shiro:hasPermission name="jmsg:admin">
				<th>机构　　　</th>
				</shiro:hasPermission>
				<th>队列总量　</th>
				<th>计费成功量　</th>
				<th>计费成功率　</th>
				<th>返充状态　</th>
				<th>返充时间　　　　　</th>
				<th>返充条数　</th>
				<shiro:hasAnyPermissions name="jmsg:admin,jmsg:agency">
				<th>返充代理条数　</th>
				</shiro:hasAnyPermissions>
				<th>更新时间　　　　　</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="jmsgSmsDayReport">
			<tr>
				<shiro:hasPermission name="jmsg:admin">
				<td>
					<a href='javascript:viewByPhoneType("${jmsgSmsDayReport.user.id}","<fmt:formatDate value="${jmsgSmsDayReport.day}" pattern="yyyy-MM-dd"/>","day","${jmsgSmsDayReport.user.name}")'>详情</a>
				</td>
				</shiro:hasPermission>
				<td>
					<fmt:formatDate value="${jmsgSmsDayReport.day}" pattern="yyyy-MM-dd"/>
				</td>
				<shiro:hasAnyPermissions name="jmsg:admin,jmsg:agency">
				<td>
					${jmsgSmsDayReport.user.name}
				</td>
				<td>
					${jmsgSmsDayReport.user.id}
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
				<td>

					<c:if test="${jmsgSmsDayReport.userCount == 0 && jmsgSmsDayReport.sendCount == 0}">
						0%
					</c:if>
					<c:if test="${jmsgSmsDayReport.userCount != 0 && jmsgSmsDayReport.sendCount != 0}">
						<fmt:formatNumber type="number"
										  value="${jmsgSmsDayReport.userCount*100/jmsgSmsDayReport.sendCount}"
										  maxFractionDigits="2" pattern="0.00">
						</fmt:formatNumber>%
					</c:if>

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
				<shiro:hasAnyPermissions name="jmsg:admin,jmsg:agency">
				<td>
					${jmsgSmsDayReport.backCount}
				</td>
				</shiro:hasAnyPermissions>
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