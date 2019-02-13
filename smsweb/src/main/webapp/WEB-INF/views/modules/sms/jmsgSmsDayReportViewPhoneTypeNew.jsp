<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>详情</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#btnMenu").remove();
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
		&nbsp;&nbsp;<h5>&nbsp;&nbsp;用户名称：${jmsgSmsDayReport.user.name }<br/>
		   &nbsp;&nbsp;统计时间：
		   		 <c:if test="${jmsgSmsDayReport.queryType eq 'day'}">
					<fmt:formatDate value="${jmsgSmsDayReport.day}" pattern="yyyy-MM-dd"/>
				 </c:if>
				 <c:if test="${jmsgSmsDayReport.queryType eq 'month'}">
					<fmt:formatDate value="${jmsgSmsDayReport.day}" pattern="yyyy-MM"/>
				 </c:if>
		</h5>
	<br/>	 
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>运营商</th>
				<th>队列总量</th>
				<th>网关总量</th>
				<th>状态报告成功</th>
				<th>状态报告未知</th>
				<th>状态成功率</th>
				<th>运营商占比</th>
			</tr>
		</thead>
		<tbody>
		<c:set var="sum" value="0"/>
		<c:forEach items="${list}" var="jmsgSmsDayReport">
			 <c:set value="${sum + jmsgSmsDayReport.reportSuccessCount}" var="sum"/>
		</c:forEach>
		
		<c:forEach items="${list}" var="jmsgSmsDayReport">
			<tr>
				<td>
					${fns:getDictLabel(jmsgSmsDayReport.phoneType, 'phone_type', '')}
				</td>
				<td>
					${jmsgSmsDayReport.sendCount}
				</td>
				<td>
					${jmsgSmsDayReport.successCount}
				</td>
				<td>
					${jmsgSmsDayReport.reportSuccessCount}
				</td>
				<td>
					${jmsgSmsDayReport.reportNullCount}
				</td>							
				<td>
					<c:if test="${jmsgSmsDayReport.successCount eq 0}">0%</c:if>
					<c:if test="${jmsgSmsDayReport.successCount gt 0}">
					<fmt:formatNumber type="number" value="${jmsgSmsDayReport.reportSuccessCount*100/jmsgSmsDayReport.successCount}" maxFractionDigits="2" pattern="0.00"/>%
					</c:if>
				</td>
				<td>
					<c:if test="${sum eq 0 }">0.00%</c:if>
					<c:if test="${sum gt 0 }">
					<fmt:formatNumber type="number" value="${jmsgSmsDayReport.reportSuccessCount*100/sum}" maxFractionDigits="2" pattern="0.00"/>%
					</c:if>
				</td>				
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>