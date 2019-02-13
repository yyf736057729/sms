<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>通道月统计报表</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#btnExport").click(function(){
				top.$.jBox.confirm("确认要导出用户的网关统计日报表数据吗？","系统提示",function(v,h,f){
					if(v=="ok"){
						$("#searchForm").attr("action","${ctx}/sms/jmsgSmsDayReport/exportByGateway");
						$("#searchForm").submit();
					}
				},{buttonsFocus:1});
				top.$('.jbox-body .jbox-icon').css('top','55px');
			});
		});
		function page(n,s){
			if(n) $("#pageNo").val(n);
			if(s) $("#pageSize").val(s);
			$("#searchForm").attr("action","${ctx}/sms/jmsgSmsDayReport/gatewayReport?queryType=month");
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
		<li class="active"><a href="${ctx}/sms/jmsgSmsDayReport/gatewayReport?queryType=month">信息列表</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="jmsgSmsDayReport" action="${ctx}/sms/jmsgSmsDayReport/gatewayReport?queryType=month" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>通道ID：</label>
				<form:select path="gatewayId" cssClass="input-xlarge">
					<form:option value="">请选择</form:option>
					<form:options items="${fns:getGatewayList()}" itemValue="value" itemLabel="label"/>
				</form:select>
			</li>
			<li><label>通道名称：</label>
				<form:input path="gatewayName" maxlength="20" cssClass="input-medium"/>
			</li>
			<li><label>统计时间：</label>
				<input name="dayQ" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					value="<fmt:formatDate value="${jmsgSmsDayReport.dayQ}" pattern="yyyy-MM"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM',isShowClear:false});"/>-
				<input name="dayZ" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					value="<fmt:formatDate value="${jmsgSmsDayReport.dayZ}" pattern="yyyy-MM"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM',isShowClear:false});"/>
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
				<th>统计时间　</th>
				<th>通道名称　　　　　　　　　　</th>
				<th>通道ID</th>
				<th>队列总量</th>
				<th>队列失败量</th>
				<th>队列成功量</th>
				<th>网关总量</th>
				<th>网关成功量</th>
				<th>网关失败量</th>
				<th>网关成功率</th>
				<th>状态报告成功</th>
				<th>状态成功占比</th>
				<th>状态报告失败</th>
				<th>状态失败占比</th>
				<th>状态报告空</th>
				<th>状态空占比</th>
				<th>更新时间　　　　　　</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="jmsgSmsDayReport">
			<tr>
				<td>
					<fmt:formatDate value="${jmsgSmsDayReport.day}" pattern="yyyy-MM"/>
				</td>
				<td>
					${empty jmsgSmsDayReport.gatewayName || jmsgSmsDayReport.gatewayName eq '' ? '未知' : jmsgSmsDayReport.gatewayName}
				</td>
				<td>
					${jmsgSmsDayReport.gatewayId}
				</td>
				<td>
					${jmsgSmsDayReport.sendCount}
				</td>
				<td>
					${jmsgSmsDayReport.sendCount-jmsgSmsDayReport.successCount}
				</td>								
				<td>
					${jmsgSmsDayReport.successCount}
				</td>				
				<td>
					${jmsgSmsDayReport.successCount}
				</td>
				<td>
					${jmsgSmsDayReport.submitSuccessCount}
				</td>
				<td>
					${jmsgSmsDayReport.submitFailCount}
				</td>
				<td>
					<c:if test="${jmsgSmsDayReport.successCount eq 0 }">0%</c:if>
					<c:if test="${jmsgSmsDayReport.successCount gt 0 }">
					<fmt:formatNumber type="number" value="${jmsgSmsDayReport.submitSuccessCount*100/jmsgSmsDayReport.successCount}" maxFractionDigits="2" pattern="0.00"></fmt:formatNumber>%
					</c:if>
				</td>
				<td>
					${jmsgSmsDayReport.reportSuccessCount}
				</td>
				<td>
					<c:if test="${jmsgSmsDayReport.successCount eq 0 }">0%</c:if>
					<c:if test="${jmsgSmsDayReport.successCount gt 0 }">
					<fmt:formatNumber type="number" value="${jmsgSmsDayReport.reportSuccessCount*100/jmsgSmsDayReport.successCount}" maxFractionDigits="2" pattern="0.00"></fmt:formatNumber>%
					</c:if>
				</td>
				<td>
					${jmsgSmsDayReport.reportFailCount-jmsgSmsDayReport.submitFailCount}
				</td>
				<td>
					<c:if test="${jmsgSmsDayReport.successCount eq 0 }">0%</c:if>
					<c:if test="${jmsgSmsDayReport.successCount gt 0 }">
					<fmt:formatNumber type="number" value="${(jmsgSmsDayReport.reportFailCount-jmsgSmsDayReport.submitFailCount)*100/jmsgSmsDayReport.successCount}" maxFractionDigits="2" pattern="0.00"></fmt:formatNumber>%
					</c:if>
				</td>	
				<td>
					${jmsgSmsDayReport.reportNullCount}
				</td>
				<td>
					<c:if test="${jmsgSmsDayReport.successCount eq 0 }">0%</c:if>
					<c:if test="${jmsgSmsDayReport.successCount gt 0 }">
					<fmt:formatNumber type="number" value="${jmsgSmsDayReport.reportNullCount*100/jmsgSmsDayReport.successCount}" maxFractionDigits="2" pattern="0.00"></fmt:formatNumber>%
					</c:if>
				</td>
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