<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>消费明细清单</title>
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
		<li class="active"><a href="${ctx}/mms/jmsgMmsTask/xiaofeiDetailList">消费明细清单</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="jmsgMmsTask" action="${ctx}/mms/jmsgMmsTask/xiaofeiDetailList" method="post" class="breadcrumb form-search">
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
			<li><label>返充状态</label>
				<form:select path="backStatus" class="input-medium" htmlEscape="false">
					<form:option value="">全部</form:option>
					<form:option value="1">已返充</form:option>
					<form:option value="0">未返充</form:option>
				</form:select>
			</li>
			<li><label>彩信ID：</label>
				<form:input path="mmsId" htmlEscape="false" maxlength="10" class="input-medium" id="mmsId"/>
			</li>
			<li><label>彩信主题：</label>
				<form:input path="mmsTitle" htmlEscape="false" maxlength="20" class="input-medium"/>
			</li>
			<li><label>任务ID：</label>
				<form:input path="id" htmlEscape="false" maxlength="10" class="input-medium"/>
			</li>
			<li><label>扣费时间：</label>
				<input name="sendDatetimeQ" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					value="<fmt:formatDate value="${jmsgMmsTask.sendDatetimeQ}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:true});"/> - 
				<input name="sendDatetimeZ" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					value="<fmt:formatDate value="${jmsgMmsTask.sendDatetimeZ}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:true});"/>
			</li>
			
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
			</li>
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
				<th>扣费条数</th>
				<th>扣费时间</th>
				<th>返充状态</th>
				<th>返充条数</th>
				<th>返充时间</th>
				<th>实际计费条数</th>
				<th>成功率</th>
				<th>操作</th>
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
					${jmsgMmsTask.sendCount}
				</td>
				<td>
					<fmt:formatDate value="${jmsgMmsTask.createDatetime}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>				
				<td>
					${jmsgMmsTask.backStatus eq 1 ? '已返充' : '未返充'}
				</td>
				<td>
					${jmsgMmsTask.backCount}
				</td>
				<td>
					<fmt:formatDate value="${jmsgMmsTask.backDatetime}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
					 ${jmsgMmsTask.backStatus eq 1 ? (jmsgMmsTask.sendCount-jmsgMmsTask.backCount) : 0}
				</td>
				<td>
					<c:if test="${jmsgMmsTask.backStatus eq 0}">
						-
					</c:if>
					<c:if test="${jmsgMmsTask.backStatus ne 0}">
						<c:if test="${jmsgMmsTask.sendCount eq 0 }">-</c:if>
						<c:if test="${jmsgMmsTask.sendCount gt 0 }">
						<fmt:formatNumber type="number" value=" ${(jmsgMmsTask.backStatus eq 1 ? (jmsgMmsTask.sendCount-jmsgMmsTask.backCount) : 0)/jmsgMmsTask.sendCount *100}" pattern="0.00" maxFractionDigits="2"></fmt:formatNumber>%
						</c:if>
					</c:if>
				</td>
				<td><c:if test="${jmsgMmsTask.backStatus eq 1 && (jmsgMmsTask.sendCount-jmsgMmsTask.backCount) gt 0}">
					<a href="${ctx}/mms/jmsgMmsTask/downloadDetail?taskId=${jmsgMmsTask.id}&createTime=<fmt:formatDate value="${jmsgMmsTask.backDatetime}" pattern="yyyyMM"/>" onclick="return confirmx('确认要下载对账单吗？', this.href)">下载对账单</a>
					</c:if>
				</td>	
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>