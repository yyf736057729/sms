<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>上行推送信息管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#btnExport").click(function(){
				top.$.jBox.confirm("确认要导出用户上行短信清单吗？","系统提示",function(v,h,f){
					if(v=="ok"){
						$("#searchForm").attr("action","${ctx}/sms/jmsgSmsDeliverPush/exportV2");
						$("#searchForm").submit();
					}
				},{buttonsFocus:1});
				top.$('.jbox-body .jbox-icon').css('top','55px');
			});
		});
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").attr("action","${ctx}/sms/jmsgSmsDeliverPush/listV2");
			$("#searchForm").submit();
        	return false;
        }
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/sms/jmsgSmsDeliverPush/listV2">信息列表</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="jmsgSmsDeliverPush" action="${ctx}/sms/jmsgSmsDeliverPush/listV2" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>接入号：</label>
				<form:input path="srcTermId" htmlEscape="false" maxlength="32" class="input-medium"/>
			</li>
			<li><label>手机号：</label>
				<form:input path="destTermId" htmlEscape="false" maxlength="32" class="input-medium"/>
			</li>
			<li><label>短信内容：</label>
				<form:input path="msgContent" htmlEscape="false" maxlength="32" class="input-medium"/>
			</li>
			<li><label>日期：</label>
				<input name="createtimeQ" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					value="<fmt:formatDate value="${jmsgSmsDeliverPush.createtimeQ}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});"/>
					--
				<input name="createtimeZ" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					value="<fmt:formatDate value="${jmsgSmsDeliverPush.createtimeZ}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});"/>
			</li>
			<li class="btns">
				<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询" onclick="return page();"/>
				<!-- <input id="btnExport" class="btn btn-primary" type="button" value="导出"/> -->
			</li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>接入号</th>
				<th>上行手机号</th>
				<th>短信内容</th>
				<th>创建时间</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="jmsgSmsDeliverPush">
			<tr>
				<td>
					${jmsgSmsDeliverPush.srcTermId}
				</td>
				<td>
					${jmsgSmsDeliverPush.destTermId}
				</td>
				<td>
					${jmsgSmsDeliverPush.msgContent}
				</td>
				<td>
					<fmt:formatDate value="${jmsgSmsDeliverPush.createtime}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>