<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>彩信总日报管理</title>
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
		<li class="active"><a href="${ctx}/mms/jmsgMmsAlldayReport/">彩信总日报列表</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="jmsgMmsAlldayReport" action="${ctx}/mms/jmsgMmsAlldayReport/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>统计时间：</label>
				<input name="dayQ" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					value="<fmt:formatDate value="${jmsgMmsAlldayReport.dayQ}" pattern="yyyy-MM-dd"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:true});"/> - 
				<input name="dayZ" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					value="<fmt:formatDate value="${jmsgMmsAlldayReport.dayZ}" pattern="yyyy-MM-dd"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:true});"/>
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
				<th>日总发送量</th>
				<th>网关成功量</th>
				<th>状态报告成功</th>
				<th>下载成功</th>
				<th>更新时间</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="jmsgMmsAlldayReport">
			<tr>
				<td>
					<fmt:formatDate value="${jmsgMmsAlldayReport.day}" pattern="yyyy-MM-dd"/>
				</td>
				<td>
					${jmsgMmsAlldayReport.sendCount}
				</td>
				<td>
					${jmsgMmsAlldayReport.submitCount}
				</td>
				<td>
					${jmsgMmsAlldayReport.reportCount}
				</td>
				<td>
					${jmsgMmsAlldayReport.downloadCount}
				</td>
				<td>
					<fmt:formatDate value="${jmsgMmsAlldayReport.createDatetime}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>