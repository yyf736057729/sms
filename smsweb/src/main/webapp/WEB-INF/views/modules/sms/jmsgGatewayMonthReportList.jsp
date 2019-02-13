<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>通道月发送报表管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#btnExport").click(function(){
				top.$.jBox.confirm("确认要导出通道统计月报表数据吗？","系统提示",function(v,h,f){
					if(v=="ok"){
						$("#searchForm").attr("action","${ctx}/sms/jmsgGatewayDayReport/exportByMonth");
						$("#searchForm").submit();
					}
				},{buttonsFocus:1});
				top.$('.jbox-body .jbox-icon').css('top','55px');
			});
		});
		function page(n,s){
			if(n) $("#pageNo").val(n);
			if(s) $("#pageSize").val(s);
			$("#searchForm").attr("action","${ctx}/sms/jmsgGatewayDayReport/listByMonth");
			$("#searchForm").submit();
        	return false;
        }
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/sms/jmsgGatewayDayReport/listByMonth">信息列表</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="jmsgGatewayDayReport" action="${ctx}/sms/jmsgGatewayDayReport/listByMonth" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>统计日期：</label>
				<input name="dayQ" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					value="<fmt:formatDate value="${jmsgGatewayDayReport.dayQ}" pattern="yyyy-MM"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM',isShowClear:false});"/>-
				<input name="dayZ" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					value="<fmt:formatDate value="${jmsgGatewayDayReport.dayZ}" pattern="yyyy-MM"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM',isShowClear:false});"/>
			</li>
			<li><label>通道：</label>
				<form:select path="gatewayId" class="input-medium">
					<form:option value="" label="全部"/>
					<form:options items="${fns:getGatewayList()}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
			</li>
			<li><label>通道名称：</label>
				<form:input path="gatewayName" htmlEscape="false" maxlength="40" class="input-medium"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询" onclick="return page();"/>
							 <!-- <input id="btnExport" class="btn btn-primary" type="button" value="导出"/> -->
			</li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>统计日期</th>
				<th>通道ID</th>
				<th>通道名称</th>
				<th>网关总量</th>
				<th>网关成功量</th>
				<th>网关失败量</th>
				<th>网关成功率</th>
				<th>状态报告成功</th>
				<th>状态成功占比</th>
				<th>状态报告空</th>
				<th>状态空占比</th>
				<th>更新时间</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="jmsgGatewayDayReport">
			<tr>
				<td>
					<fmt:formatDate value="${jmsgGatewayDayReport.day}" pattern="yyyy-MM"/>
				</td>
				<td>
					${jmsgGatewayDayReport.gatewayId}
				</td>
				<td>
					${jmsgGatewayDayReport.gatewayName}
				</td>
				<td>
					${jmsgGatewayDayReport.submitCount}
				</td>
				<td>
					${jmsgGatewayDayReport.submitSuccessCount}
				</td>
				<td>
					${jmsgGatewayDayReport.submitFailCount}
				</td>
				<td>
					<fmt:formatNumber type="number" value="${jmsgGatewayDayReport.submitSuccessCount*100/jmsgGatewayDayReport.submitCount}" maxFractionDigits="2" pattern="0.00"></fmt:formatNumber>%
				</td>
				<td>
					${jmsgGatewayDayReport.reportSuccessCount}
				</td>
				<td>
					<fmt:formatNumber type="number" value="${jmsgGatewayDayReport.reportSuccessCount*100/jmsgGatewayDayReport.submitCount}" maxFractionDigits="2" pattern="0.00"></fmt:formatNumber>%
				</td>				
				<td>
					${jmsgGatewayDayReport.reportNullCount}
				</td>
				<td>
					<fmt:formatNumber type="number" value="${jmsgGatewayDayReport.reportNullCount*100/jmsgGatewayDayReport.submitCount}" maxFractionDigits="2" pattern="0.00"></fmt:formatNumber>%
				</td>				
				<td>
					<fmt:formatDate value="${jmsgGatewayDayReport.updateDatetime}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>