<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>手动生成日报表</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
		});
		function onSubmit(){
			var day = $("#day").val();
			if(day == null || day ==''){
				alertx("请选择生成报表时间");
				return;
			}
			
			$("#searchForm").attr("action","${ctx}/sms/jmsgSmsDayReport/createDayReport");
			$("#searchForm").submit();
        	return false;
        }	
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/sms/jmsgSmsDayReport/createDayReportInit">信息列表</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="jmsgSmsDayReport" action="${ctx}/sms/jmsgSmsDayReport/createDayReport" method="post" class="breadcrumb form-search">
		<ul class="ul-form">
			<li><label>时间：</label>
				<input id="day" name="day" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					value="<fmt:formatDate value="${jmsgSmsDayReport.day}" pattern="yyyy-MM-dd"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="button" value="生成报表" onclick="return onSubmit();"/>
			</li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
</body>
</html>