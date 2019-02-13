<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>分省统计</title>
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
		
		function onCreate(){
			var dayQ = $("#dayQ").val();
			var dayZ = $("#dayZ").val();
			if(dayQ == ''){
				alertx("请选择时间起");
				return;
			}
			if(dayZ == ''){
				alertx("请选择时间止");
				return;
			}
			var taskName = $("#taskName").val();
			if(taskName == null || taskName ==''){
				alertx("请输入任务名称");
				return;
			}
			//时间可选择4天内和4天前，如果开始时间选择4天前的，则结束时间不能选择4天内的日期。
			$("#searchForm").attr('action','${ctx}/sms/jmsgCustomTask/createPovinceReport');
			$("#searchForm").submit();
		}
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/sms/jmsgCustomTask/provinceReportIndex">信息列表</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="jmsgCustomTask" action="${ctx}/sms/jmsgCustomTask/provinceReportIndex" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>时间：</label>
				<input name="dayQ" id="dayQ" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					value="<fmt:formatDate value="${jmsgCustomTask.dayQ}" pattern="yyyy-MM-dd"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});"/>-
				<input name="dayZ" id="dayZ" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					value="<fmt:formatDate value="${jmsgCustomTask.dayZ}" pattern="yyyy-MM-dd"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});"/>
			</li>
			
			<li><label>通道名称：</label>
				<select name="gatewayId" class="input-xlarge">
					<option value="">请选择</option>
					<c:forEach var="gateway" items="${fns:getGatewayList()}">
					<option value="${gateway.value}">${gateway.label}</option>
					</c:forEach>
				</select>			
			</li>
			<li><label>任务名称：</label>
				<form:input path="taskName" cssClass="input-xlarge" maxlength="40"/>
			</li>
			<li class="btns">
			<input id="btnCreate" class="btn btn-primary" type="button" value="生成任务" onclick="return onCreate();"/>	
			<!-- <input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li> -->
			<li class="clearfix"></li>
		</ul>
	</form:form>
	 
	<sys:message content="${message}"/>
	<!--<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>ID</th>
				<th>任务名称</th>
				<th>执行结果</th>
				<th>任务状态</th>
				<th>生成时间</th>
				<th>预约时间</th>
				<th>预约人</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="jmsgCustomTask">
			<tr>
				<td>
					${jmsgCustomTask.id}
				</td>
				<td>
					${jmsgCustomTask.taskName}
				</td>
				<td>
					${jmsgCustomTask.executeResult}
				</td>
				<td>
					${fns:getDictLabel(jmsgCustomTask.status, 'custom_task_status', '')}
				</td>
				<td>
					<fmt:formatDate value="${jmsgCustomTask.executeEndTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
					<fmt:formatDate value="${jmsgCustomTask.createTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
					${jmsgCustomTask.createBy.name}
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div> -->
</body>
</html>