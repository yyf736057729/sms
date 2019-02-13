<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>修改状态报告</title>
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
			var userId = $("#userId").val();
			if(userId == null || userId ==''){
				alertx("请选择用户");
				return;
			}
			var gatewayId = $("#gatewayId").val();
			if(gatewayId == null || gatewayId ==''){
				alertx("请选择通道");
				return;
			}
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
			var count = $("#count").val();
			if(isNaN(count) || count <= 0){ 
				alertx("请输入正确修改数量"); 
			　　return;
			}
			
			var nullType = $("#nullType");
			var errorType = $("#errorType");
			
			if(!(nullType.attr('checked')||errorType.attr('checked'))){
				alertx("请选择状态类型");
				return;
			}
			
			var taskName = $("#taskName").val();
			if(taskName == null || taskName  ==''){
				alertx("请输入任务名称");
				return;
			}

			//时间可选择4天内和4天前，如果开始时间选择4天前的，则结束时间不能选择4天内的日期。
			$("#searchForm").attr('action','${ctx}/sms/jmsgCustomTask/createReportStatus');
			$("#searchForm").submit();
		}
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/sms/jmsgCustomTask/modifyReportStatusInit">信息列表</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="jmsgReportStatusTask" action="${ctx}/sms/jmsgCustomTask/modifyReportStatusInit" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="0"/>
		<input id="pageSize" name="pageSize" type="hidden" value="500"/>
		<ul class="ul-form">
			<li><label>用户名称：</label>
				<sys:treeselect id="user" name="userId" value="${jmsgReportStatusTask.userId}" labelName="userName" labelValue="${jmsgReportStatusTask.userName}"
					title="用户" url="/sys/office/treeData?type=3" cssClass="input-small" allowClear="true" notAllowSelectParent="true"/>
			</li>
			<li><label>通道名称：</label>
				<form:select path="gatewayId" cssClass="input-xlarge">
					<form:option value="">请选择</form:option>
					<form:options items="${fns:getGatewayList()}" itemLabel="label" itemValue="value"/>
				</form:select>
			</li>			
			<li><label>时间：</label>
				<input name="dayQ" id="dayQ" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					value="<fmt:formatDate value="${jmsgReportStatusTask.dayQ}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});"/>-
				<input name="dayZ" id="dayZ" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					value="<fmt:formatDate value="${jmsgReportStatusTask.dayZ}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});"/>
			</li>
			<li><label>修改数量：</label>
				<form:input path="count" cssClass="input-small"/>
			</li>
			<li><label>状态类型：</label>
				<form:checkbox path="nullType" id="nullType" value="1"/>状态空&nbsp;&nbsp;<form:checkbox path="errorType" id="errorType" value="1"/>状态失败
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
</body>
</html>