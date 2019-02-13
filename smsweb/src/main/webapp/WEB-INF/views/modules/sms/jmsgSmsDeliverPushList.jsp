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
						$("#searchForm").attr("action","${ctx}/sms/jmsgSmsDeliverPush/export");
						$("#searchForm").submit();
					}
				},{buttonsFocus:1});
				top.$('.jbox-body .jbox-icon').css('top','55px');
			});
		});
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").attr("action","${ctx}/sms/jmsgSmsDeliverPush/list");
			$("#searchForm").submit();
        	return false;
        }
		
		function toSend(content){
			$('#fullForm').find('input[id="phones"]').val(content);
			$("#fullForm").attr("action", "${ctx}/sms/jmsgSmsTask/smsGroupInit/");
	    	$("#fullForm").submit();
		}
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/sms/jmsgSmsDeliverPush/">信息列表</a></li>
	</ul>
	<form:form id="fullForm" method="post">
    	<input type="hidden" id="phones" name="phones" />
    </form:form>
	<form:form id="searchForm" modelAttribute="jmsgSmsDeliverPush" action="${ctx}/sms/jmsgSmsDeliverPush/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<shiro:hasPermission name="jmsg:admin">
			<li><label>用户ID：</label>
				<sys:treeselect id="user" name="user.id" value="${jmsgSmsDeliverPush.user.id}" labelName="user.name" labelValue="${jmsgSmsDeliverPush.user.name}"
					title="用户" url="/sys/office/treeData?type=3" cssClass="input-small" allowClear="true" notAllowSelectParent="true"/>
			</li>
			<li><label>接入号：</label>
				<form:input path="srcTermId" htmlEscape="false" maxlength="32" class="input-medium"/>
			</li>
			</shiro:hasPermission>
			<li><label>手机号：</label>
				<form:input path="destTermId" htmlEscape="false" maxlength="32" class="input-small"/>
			</li>
			<li><label>推送标识：</label>
				<form:select path="pushFlag" class="input-medium">
					<form:option value="" label="全部"/>
					<form:options items="${fns:getDictList('yes_no')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
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
				<input id="btnSubmit" class="btn btn-primary" type="button" value="查询" onclick="return page();"/>
				 <!-- <input id="btnExport" class="btn btn-primary" type="button" value="导出"/> -->
			</li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<shiro:hasPermission name="jmsg:admin"><th>用户</th></shiro:hasPermission>
				<th>接入号</th>
				<th>上行手机号</th>
				<th>短信内容</th>
				<th>是否推送</th>
				<th>创建时间</th>
				<th>操作</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="jmsgSmsDeliverPush">
			<tr>
				<shiro:hasPermission name="jmsg:admin">
				<td>
					${jmsgSmsDeliverPush.user.name}
				</td>
				</shiro:hasPermission>
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
					${fns:getDictLabel(jmsgSmsDeliverPush.pushFlag, 'yes_no', jmsgSmsDeliverPush.pushFlag)}
				</td>	
				<td>
					<fmt:formatDate value="${jmsgSmsDeliverPush.createtime}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
					<a href='javascript:toSend("${jmsgSmsDeliverPush.destTermId}")'>回复</a>
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>