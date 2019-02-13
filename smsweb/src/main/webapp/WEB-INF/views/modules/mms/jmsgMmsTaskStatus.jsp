<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>彩信发送状态</title>
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
		function onQuery(){
			var taskId = $("#taskId").val();
			var phone = $("#phone").val();
			if(taskId == null || taskId ==''){
				alertx("请输入任务ID");
				return;
			}
			
			if(phone == null || phone ==''){
				alertx("请输入手机号码");
				return;
			}
			
			$("#searchForm").submit();
		}
		
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/mms/jmsgMmsTaskDetail/listStatus">彩信发送状态</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="jmsgMmsTaskDetail" action="${ctx}/mms/jmsgMmsTaskDetail/listStatus" method="post" class="breadcrumb form-search">
		<ul class="ul-form">
			<li><label>任务ID：</label>
				<input name="taskId" maxlength="11" class="input-medium required" value="${taskId}" id="taskId">
				<span class="help-inline"><font color="red">*</font> </span>
			</li>
			<li><label>手机号码：</label>
				<input name="phone" maxlength="20" class="input-medium required" value="${phone}" id="phone">
				<span class="help-inline"><font color="red">*</font> </span>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="button" value="查询" onclick="javascript:onQuery();"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>任务ID</th>
				<th>手机号码</th>
				<th>发送状态</th>
				<th>发送时间</th>
				<th>网关状态</th>
				<th>状态报告</th>
				<th>下载状态</th>
				<th>设备类型</th>
			</tr>
		</thead>
		<tbody>
			<tr>
				<td>
					${jmsgMmsTaskDetail.taskId}
				</td>
				<td>
					${jmsgMmsTaskDetail.phone}
				</td>
				<td>
					${fns:getDictLabel(jmsgMmsTaskDetail.sendStatus,'mms_send_status',jmsgMmsTaskDetail.sendStatus)} 
				</td>
				<td>
					<fmt:formatDate value="${jmsgMmsTaskDetail.sendDatetime}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				
				<td>
					<c:if test="${ not empty jmsgMmsSubmit.result}">
					${jmsgMmsSubmit.result eq 0 ? '成功' : '失败'}
					</c:if>
				</td>
				<td>
					<!--<c:if test="${ not empty jmsgMmsReport.stat}">
					${jmsgMmsReport.stat eq '0' ? "成功" : "失败"}
					</c:if>-->
					${jmsgMmsReport.stat}
				</td>
				<td>${ not empty jmsgMmsTaskDetail.receiveDatetime ? "已下载" :""}</td>
				<td>${fns:abbr(jmsgMmsTaskDetail.deviceType,30)}</td>
			</tr>
		</tbody>
	</table>
</body>
</html>