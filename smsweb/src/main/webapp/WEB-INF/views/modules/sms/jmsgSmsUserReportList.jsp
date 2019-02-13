<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>用户状态报告监控管理</title>
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
			$("#searchForm").attr("action","${ctx}/sms/jmsgSmsDayReport/cmppUserReportStatus");
			$("#searchForm").submit();
        	return false;
        }
		
		function pushTask(id,userId,day,userName,pushFlag){
			//$("#searchForm").attr("action","${ctx}/sms/jmsgSmsDayReport/taskPush?pUserId="+userId+"&createDatetime="+day+"&name="+userName+"&pushFlag="+pushFlag);
			//$("#searchForm").submit();
			userName = encodeURI(encodeURI(userName));
			$.ajax({  
		        type: "post",  
		        url: "${ctx}/sms/jmsgSmsDayReport/taskPush?userId="+userId+"&createDatetime="+day+"&name="+userName+"&pushFlag="+pushFlag,
		        dataType: 'json',  
		        contentType: "application/x-www-form-urlencoded; charset=utf-8",  
				success : function(result) {
					alertx(result);
					$('#'+id).css("background-color", "#90EE90");
				}
		    });
		}
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/sms/jmsgSmsDayReport/cmppUserReportStatus">信息列表</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="jmsgSmsDayReport" action="${ctx}/sms/jmsgSmsDayReport/cmppUserReportStatus" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input type="hidden" name="userCategory" value="1">
		<ul class="ul-form">
			<li><label>机构：</label><sys:treeselect id="company" name="company.id" value="${jmsgSmsDayReport.company.id}" labelName="company.name" labelValue="${jmsgSmsDayReport.company.name}" 
				title="公司" url="/sys/office/treeData?type=1" cssClass="input-small" allowClear="true"/></li>
			<li><label>用户名称：</label>
				<sys:treeselect id="user" name="user.id" value="${jmsgSmsDayReport.user.id}" labelName="user.name" labelValue="${jmsgSmsDayReport.user.name}"
					title="用户" url="/sys/office/treeData?type=3" cssClass="input-small" allowClear="true" notAllowSelectParent="true"/>
			</li>
			<li><label>用户ID：</label>
				<form:input path="userId" class="input-medium" maxlength="10"/>
			</li>
			<li><label>统计时间：</label>
				<input name="day" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					value="<fmt:formatDate value="${jmsgSmsDayReport.day}" pattern="yyyy-MM-dd"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询" onclick="return page();"/>
			</li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<span class="help-inline"><font color="red">说明：已手动推送用户状态报告数据 “操作”背景色标识为浅绿色</font> </span>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>操作　　　　　　　　</th>
				<th>统计时间　　</th>
				<th>用户名称　　　　　　　　</th>
				<th>用户ID</th>
				<th>登录账号</th>
				<th>机构　　　</th>
				<th>网关成功量</th>
				<th>状态报告失败</th>
				<th>状态报告空</th>
				<th>状态空占比</th>
				<th>状态报告成功</th>
				<th>状态成功占比</th>
				<th>推送成功量</th>
				<th>推送成功率</th>
				<th>更新时间　　　　　　</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="jmsgSmsDayReport" varStatus="index">
			<tr>
				<td id="idx_${index.index}"   ${fns:getUserReportStatus(jmsgSmsDayReport.user.id,jmsgSmsDayReport.day) eq 1 ? 'style="background-color: #90EE90;"' : ''}>
					<a href='javascript:pushTask("idx_${index.index}","${jmsgSmsDayReport.user.id}","<fmt:formatDate value="${jmsgSmsDayReport.day}" pattern="yyyy-MM-dd"/>","${jmsgSmsDayReport.user.name}","")'>全部补推</a>
					<a href='javascript:pushTask("idx_${index.index}","${jmsgSmsDayReport.user.id}","<fmt:formatDate value="${jmsgSmsDayReport.day}" pattern="yyyy-MM-dd"/>","${jmsgSmsDayReport.user.name}","9")'>失败补推</a>
				</td>
				<td>
					<fmt:formatDate value="${jmsgSmsDayReport.day}" pattern="yyyy-MM-dd"/>
				</td>
				<td>
					${jmsgSmsDayReport.user.name}
				</td>
				<td>
					${jmsgSmsDayReport.user.id}
				</td>
				<td>
					${jmsgSmsDayReport.user.loginName}
				</td>
				<td>
					${jmsgSmsDayReport.companyName}
				</td>
				<td>
					${jmsgSmsDayReport.submitSuccessCount}
				</td>
				<td>
					${jmsgSmsDayReport.reportFailCount-jmsgSmsDayReport.submitFailCount}
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
					<font color="blue">${jmsgSmsDayReport.reportSuccessCount}</font> 
				</td>
				<td>
					<c:if test="${jmsgSmsDayReport.successCount eq 0 }">0%</c:if>
					<c:if test="${jmsgSmsDayReport.successCount gt 0 }">
					<fmt:formatNumber type="number" value="${jmsgSmsDayReport.reportSuccessCount*100/jmsgSmsDayReport.successCount}" maxFractionDigits="2" pattern="0.00"></fmt:formatNumber>%
					</c:if>
				</td>
				<td>
					<font color="red">${jmsgSmsDayReport.pushSuccessCount}</font>
				</td>
				<td>
					<c:if test="${jmsgSmsDayReport.sendCount-jmsgSmsDayReport.reportNullCount eq 0 }">0%</c:if>
					<c:if test="${jmsgSmsDayReport.sendCount-jmsgSmsDayReport.reportNullCount gt 0 }">
					<fmt:formatNumber type="number" value="${jmsgSmsDayReport.pushSuccessCount*100/(jmsgSmsDayReport.sendCount-jmsgSmsDayReport.reportNullCount)}" maxFractionDigits="2" pattern="0.00"></fmt:formatNumber>%
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