<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>资金账户信息管理</title>
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
		
		function showAmount(userId){
			var url="${ctx}/account/jmsgAccount/showAccount?userId="+userId;
			windowOpen(url,userId,300,150);
		}
	</script>
	<style type="text/css">
		body {
		    width: 1120px;
		}
	</style>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/account/jmsgAccount/list">信息列表</a></li>
		<!--<shiro:hasPermission name="account:jmsgAccount:edit"><li><a href="${ctx}/account/jmsgAccount/form?user.company.id=${jmsgAccount.company.id}&user.company.name=${jmsgAccount.company.name}&appType=mms">彩信账户信息添加</a></li></shiro:hasPermission>-->
		<shiro:hasPermission name="account:jmsgAccount:edit"><li><a href="${ctx}/account/jmsgAccount/form?user.company.id=${jmsgAccount.company.id}&user.company.name=${jmsgAccount.company.name}&appType=sms">信息添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="jmsgAccount" action="${ctx}/account/jmsgAccount/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<shiro:hasPermission name="jmsg:admin">
			<li><label>机构：</label><sys:treeselect id="company" name="company.id" value="${jmsgAccount.company.id}" labelName="company.name" labelValue="${jmsgAccount.company.name}" 
				title="公司" url="/sys/office/treeData?type=1" cssClass="input-small" allowClear="true"/></li>			
			<!-- <li><label>用户：</label>
				<sys:treeselect id="user" name="user.id" value="${jmsgAccount.user.id}" labelName="user.name" labelValue="${jmsgAccount.user.name}"
					title="用户" url="/sys/office/treeData?type=3" cssClass="input-small" allowClear="true" notAllowSelectParent="true"/>
			</li> -->
			</shiro:hasPermission>
			<%-- <li><label>登录名：</label><form:input path="user.loginName" htmlEscape="false" maxlength="50" class="input-medium"/></li> --%>
			<!-- <li><label>归属部门：</label><sys:treeselect id="office" name="office.id" value="${user.office.id}" labelName="office.name" labelValue="${user.office.name}" 
				title="部门" url="/sys/office/treeData?type=2" cssClass="input-small" allowClear="true" notAllowSelectParent="true"/></li> -->
			<%-- <li><label>用户名称：</label><form:input path="user.name" htmlEscape="false" maxlength="50" class="input-medium"/></li> --%>
			<li><label>用户名称：</label>
				<sys:treeselect id="user" name="user.id" value="${jmsgAccount.user.id}" labelName="user.name" labelValue="${jmsgAccount.user.name}"
					title="用户" url="/sys/office/treeData?type=3" cssClass="input-small" allowClear="true" notAllowSelectParent="true"/>
			</li>
			<li>
				<label>用户ID：</label>
				<form:input path="userId" htmlEscape="false" maxlength="50" class="input-small"/>
			</li>
			<li>
				<label>校验签名:</label>
				<form:select path="user.signFlag" class="input-small">
					<form:option value="">全部</form:option>
					<form:option value="1">校验</form:option>
					<form:option value="0">不校验</form:option>
				</form:select>
			</li>
			<li>
				<label>二级黑名单:</label>
				<form:select path="user.sysBlacklistFlag" class="input-small">
					<form:option value="">全部</form:option>
					<form:option value="1">校验</form:option>
					<form:option value="0">不校验</form:option>
				</form:select>
			</li>
			<li>
				<label>群发黑名单:</label>
				<form:select path="user.userBlacklistFlag" class="input-small">
					<form:option value="">全部</form:option>
					<form:option value="1">校验</form:option>
					<form:option value="0">不校验</form:option>
				</form:select>
			</li>
			<li>
				<label>通道分组:</label>
				<form:select path="user.groupId" class="input-xlarge">
					<form:option value="">全部</form:option>
					<form:options items="${fns:getGroupList()}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
			</li>
			<li><label>通道备注：</label><form:input path="remarks" htmlEscape="false" maxlength="50" class="input-medium"/></li>
			<li class="btns">
				<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
				<!-- <a href="${ctx}/sms/jmsgUserSign/list" class="btn btn-primary" target="_blank;">签名配置</a> -->
			</li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed" width="2200">
		<thead>
			<tr>
				<shiro:hasPermission name="jmsg:agency"><th width="165px;">操作　　　　　　　　　</th></shiro:hasPermission>
				<th width="40px;">状态</th>
				<th width="60px;">用户ID</th>
				<th width="100px;">用户名称　　　　　　　　</th>
				<th width="100px;">用户账号</th>
				<th width="100px;">用户角色　</th>
				<th width="100px;">组织机构　</th>
				<th width="50px;">校签名</th>
				<th width="60px;">二级黑名单</th>
				<th width="60px;">群发黑名单</th>
				<th width="80px;">账户余额</th>
				<th width="130px;">通道分组　　　　　　　　　　　　</th>
				<th width="165px;">通道备注　　　　　　　　　　　　</th>
				<th width="90px;">创建时间　　</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="jmsgAccount">
			<tr>
				<shiro:hasPermission name="jmsg:agency"><td>
    				<a href="${ctx}/account/jmsgAccount/form?id=${jmsgAccount.id}&appType=${jmsgAccount.appType}">修改</a> |
					<a href="${ctx}/account/jmsgAccount/agencyDelete?id=${jmsgAccount.id}&usedFlag=${jmsgAccount.usedFlag}&redirect=1&user.id=${jmsgAccount.user.id}" onclick="return confirmx('确认要${jmsgAccount.usedFlag eq 1?'禁用':'启用'}该短信账户吗？', this.href)">${jmsgAccount.usedFlag eq 1?'禁用':'启用'}</a> |
					<c:if test="${jmsgAccount.usedFlag eq 1}">	
						<c:if test="${jmsgAccount.user.userType eq 3 }">
							<c:if test="${jmsgAccount.appType eq 'mms'}">
								<a href="${ctx}/account/jmsgAccount/rechargeInit?user.id=${jmsgAccount.user.id}" target="top">充值</a> |
							</c:if>
							<c:if test="${jmsgAccount.appType eq 'sms'}">
								<a href="${ctx}/account/jmsgAccount/rechargeSmsInit?user.id=${jmsgAccount.user.id}" target="top">充值</a> |
							</c:if>	
						</c:if>
					</c:if>
					<%-- <a href="${ctx}/account/jmsgAccount/toGroupConfig?id=${jmsgAccount.id}&appType=${jmsgAccount.appType}">通道</a> | --%>
					<a href="${ctx}/sms/jmsgGatewaySign/toUserGatewayList?user.id=${jmsgAccount.user.id}&user.name=${jmsgAccount.user.name}" target="_blank;">签名</a> 
					<%-- |
					<a href="javascript:windowOpen('http://114.55.90.98:8080/refCache.jsp?t=user&uid=${jmsgAccount.user.id}','group',900,550);"  target="_blank;">刷新缓存</a> |
					<a href="javascript:showAmount(${jmsgAccount.user.id});">可用额度</a> --%>
					
					<!-- <a href="${ctx}/sms/jmsgUserSign/form?user.id=${jmsgAccount.user.id}&user.name=${jmsgAccount.user.name}" target="_blank;">签名配置</a> -->
				</td>
				</shiro:hasPermission>
			
				<td>
					${jmsgAccount.usedFlag eq 1?'启用':'<font color="red">禁用</font>'}
				</td>
				<td>
					${jmsgAccount.user.id}
				</td>
				<td>
					${jmsgAccount.user.name}
				</td>
				<td>
					${jmsgAccount.user.loginName}
				</td>
				<td>
					${fns:getUserById(jmsgAccount.user.id).roleNames}
				</td>
				<td>
					${jmsgAccount.user.company.name}
				</td>
				<td>
					${fns:getUserById(jmsgAccount.user.id).signFlag== 1 ? '是':'否'}
				</td>
				<td>
					${fns:getUserById(jmsgAccount.user.id).sysBlacklistFlag== 1 ? '是':'否'}
				</td>
				<td>
					${fns:getUserById(jmsgAccount.user.id).userBlacklistFlag == 1 ? '是':'否'}
				</td>
				<td>
					${fns:getAmount(jmsgAccount.user.id)}
				</td>
				<td>
				${fns:getUserById(jmsgAccount.user.id).groupId == '-1' ?"未配置": fns:getJmsgGroup( fns:getUserById(jmsgAccount.user.id).groupId ).name }
				<%-- ${fns:getJmsgGroup(getUserById(jmsgAccount.user.id).groupId).name} --%>
				</td>
				<td>${fns:getJmsgGroup( fns:getUserById(jmsgAccount.user.id).groupId ).description} </td>
				<td>
					<fmt:formatDate value="${jmsgAccount.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>