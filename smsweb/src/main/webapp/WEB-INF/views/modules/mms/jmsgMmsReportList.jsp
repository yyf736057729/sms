<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>任务状态报告报表</title>
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
		
		function onView(taskId){
			var url="${ctx}/mms/jmsgMmsAlldayReport/reportBylistDetail?taskId="+taskId;
			windowOpen(url,taskId,600,550);
		}
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/mms/jmsgMmsAlldayReport/reportBylist">任务状态报告</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="jmsgMmsAlldayReport" action="${ctx}/mms/jmsgMmsAlldayReport/reportBylist" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>任务ID：</label>
				<form:input path="taskId" htmlEscape="false" maxlength="20" class="input-medium"/>
			</li>
			<li><label>统计时间：</label>
				<input name="dayQ" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					value="<fmt:formatDate value="${jmsgMmsAlldayReport.dayQ}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});"/> - 
				<input name="dayZ" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					value="<fmt:formatDate value="${jmsgMmsAlldayReport.dayZ}" pattern="yyyy-MM-dd HH:mm:ss"/>"
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
				<th>任务ID</th>
				<th>彩信ID</th>
				<th>彩信名称</th>
				<th>发送总量</th>
				<th>发送成功数量</th>
				<th>下载成功数量</th>
				<th>创建时间</th>
				<th>操作</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="jmsgMmsAlldayReport">
			<tr>
				<td>
					${jmsgMmsAlldayReport.taskId}
				</td>
				<td>
					${jmsgMmsAlldayReport.mmsId}
				</td>
				<td>
					${jmsgMmsAlldayReport.mmsTitle}
				</td>
				<td>
					${jmsgMmsAlldayReport.count}
				</td>
				<td>
					${jmsgMmsAlldayReport.sendCount}
				</td>	
				<td>
					${jmsgMmsAlldayReport.downloadCount}
				</td>
				<td>
					<fmt:formatDate value="${jmsgMmsAlldayReport.createDatetime}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
					<a href="javascript:onView(${jmsgMmsAlldayReport.taskId})">查看</a>
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>