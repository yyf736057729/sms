<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>活动登记短信发送管理</title>
	<%@include file="/WEB-INF/views/include/head.jsp" %>
	<script type="text/javascript">
	$(document).ready(function() {
		$("#btnExport").click(function(){
			top.$.jBox.confirm("确认要导出活动登记数据吗？","系统提示",function(v,h,f){
				if(v=="ok"){
					$("#searchForm").attr("action","/biz/bizRegistryData/export");
					$("#searchForm").submit();
				}
			},{buttonsFocus:1});
			top.$('.jbox-body .jbox-icon').css('top','55px');
		});
		
	});
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").attr("action","/biz/bizRegistryData/list");
			$("#searchForm").submit();
        	return false;
        }
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="/biz/bizRegistryData/">活动登记短信发送列表</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="bizRegistryData" action="/biz/bizRegistryData/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>手机号码：</label>
				<form:input path="phone" htmlEscape="false" maxlength="32" class="input-medium"/>
			</li>
			<%-- <li><label>登记验证码：</label>
				<form:input path="verificationCode" htmlEscape="false" maxlength="32" class="input-medium"/>
			</li> --%>
			<li><label>登记时间：</label>
				<input name="createTimeQ" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					value="<fmt:formatDate value="${bizRegistryData.createTimeQ}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});"/> - 
				<input name="createTimeZ" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					value="<fmt:formatDate value="${bizRegistryData.createTimeZ}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="button" value="查询" onclick="return page();"/></li>
			    <li class="btns"><input id="btnExport" class="btn btn-primary" type="button" value="导出"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>编号</th>
				<th>客户号码</th>
				<th>登记验证码</th>
				<th>登记时间</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="bizRegistryData">
			<tr>
				<td><a href="/biz/bizRegistryData/form?id=${bizRegistryData.id}">
					${bizRegistryData.id}
				</a></td>
				<td>
					${bizRegistryData.phone}
				</td>
				<td>
					${bizRegistryData.verificationCode}
				</td>
				<td>
					<fmt:formatDate value="${bizRegistryData.createTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>