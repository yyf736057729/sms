<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>用户通道管理</title>
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
		
		function gateWayOpen(id) {
			top.$.jBox.confirm("确认要开启通道吗？","系统提示",function(v,h,f){
				if(v=="ok"){
					$("#searchForm").attr("action","${ctx}/sms/jmsgUserGateway/open");
					$("#userid").val(id);
					$("#searchForm").submit();
				}
			},{buttonsFocus:1});
			top.$('.jbox-body .jbox-icon').css('top','55px');
		}
		
		function gateWayClose(id) {
			top.$.jBox.confirm("确认要关闭通道吗？","系统提示",function(v,h,f){
				if(v=="ok"){
					$("#searchForm").attr("action","${ctx}/sms/jmsgUserGateway/close");
					$("#userid").val(id);
					$("#searchForm").submit();
				}
			},{buttonsFocus:1});
			top.$('.jbox-body .jbox-icon').css('top','55px');
		}
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/sms/jmsgUserGateway/list">信息列表</a></li>
		<shiro:hasPermission name="sms:jmsgUserGateway:edit"><li><a href="${ctx}/sms/jmsgUserGateway/form">信息添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="jmsgUserGateway" action="${ctx}/sms/jmsgUserGateway/list" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>用户名称：</label>
				<sys:treeselect id="user" name="user.id" value="${jmsgUserGateway.user.id}" labelName="user.name" labelValue="${jmsgUserGateway.user.name}"
					title="用户" url="/sys/office/treeData?type=3" cssClass="input-small" allowClear="true" notAllowSelectParent="true"/>
			</li>
			<li>
				<label>用户ID：</label>
				<form:input path="userid" htmlEscape="false" maxlength="50" class="input-medium"/>
			</li>
			<%-- <li><label>登录账号：</label>
				<form:input path="username" htmlEscape="false" maxlength="40" class="input-medium"/>
			</li> --%>
			<li>
				<label>接入号类型：</label>
				<form:select path="fromType" class="input-medium">
					<form:option value="">全部</form:option>
					<form:option value="1">全号</form:option>
					<form:option value="0">扩展号</form:option>
				</form:select>
			</li>
			<li><label>创建时间：</label>
				<input name="createtimeQ" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					value="<fmt:formatDate value="${jmsgUserGateway.createtimeQ}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});"/>-
				<input name="createtimeZ" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					value="<fmt:formatDate value="${jmsgUserGateway.createtimeZ}" pattern="yyyy-MM-dd HH:mm:ss"/>"
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
				<th>企业代码</th>
				<th>用户名称</th>
				<th>运行状态</th>
				<th>接入号</th>
				<th>协议类型</th>
				<th>版本</th>
				<!-- <th>最大连接数</th>
				<th>重试间隔(秒)</th>
				<th>最大重试次数</th>
				<th>是否重发失败消息</th>
				<th>读取限制</th>
				<th>写入限制</th> -->
				<th>接入号类型</th>
				<th>创建时间</th>
				<shiro:hasPermission name="sms:jmsgUserGateway:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="jmsgUserGateway">
			<tr>
				<td><a href="${ctx}/sms/jmsgUserGateway/form?id=${jmsgUserGateway.id}">
					${jmsgUserGateway.userid}
				</a></td>
				<td>
					${fns:getUserById(jmsgUserGateway.userid).name}
				</td>
				<td>
					${jmsgUserGateway.status eq 1 ? '启用' : '禁用'}
				</td>
				<td>
					${jmsgUserGateway.spnumber}
				</td>
				<td>
					${jmsgUserGateway.version}
				</td>
				<td>
				CMPP
				</td>
				<%-- <td>
					${jmsgUserGateway.maxChannels}
				</td>
				<td>
					${jmsgUserGateway.retryWaitTime}
				</td>
				<td>
					${jmsgUserGateway.maxRetryCnt}
				</td>
				<td>
					${jmsgUserGateway.resendFailmsg eq 0 ? '否' : '是'}
				</td>
				<td>
					${jmsgUserGateway.readLimit}
				</td>
				<td>
					${jmsgUserGateway.writeLimit}
				</td> --%>
				<td>
					${jmsgUserGateway.user.cmppUserType eq 1 ? '全号' : '扩展号' }
				</td>
				<td>
					<fmt:formatDate value="${jmsgUserGateway.createtime}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<shiro:hasPermission name="sms:jmsgUserGateway:edit"><td>
					<c:if test="${jmsgUserGateway.status eq 1}">
    					<a href="javascript:gateWayClose('${jmsgUserGateway.userid}')">禁用</a>
    				</c:if>
    				<c:if test="${jmsgUserGateway.status eq 0}">
    					<a href="javascript:gateWayOpen('${jmsgUserGateway.userid}')">启用</a>
    				</c:if>
    				<a href="${ctx}/sms/jmsgUserGateway/form?id=${jmsgUserGateway.id}">修改</a>
					<a href="${ctx}/sms/jmsgUserGateway/delete?id=${jmsgUserGateway.id}" onclick="return confirmx('确认要删除该用户通道吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>