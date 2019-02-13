<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>用户上行接入号管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#btnImport").click(function(){
				$.jBox($("#importBox").html(), {title:"导入数据", buttons:{"关闭":true}, 
					bottomText:"导入文件不能超过5M，仅允许导入“xls”或“xlsx”格式文件！"});
			});
		});
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").submit();
        	return false;
        }
		function refreshMoRule() {
			/* $.ajax({
				url : "http://114.55.90.98:8901/api/sms/gateway/morule",
				dataType : "json",
				success : function(data) {
					alert(data.success);
				}
			}); */
			
			
			$.ajax({
			    type : "get",
			    async:false,
			    url : "http://114.55.137.104:18985/api/sms/gateway/morule",
			    dataType : "jsonp",
			    jsonp: "callbackparam",//服务端用于接收callback调用的function名的参数
			    jsonpCallback:"success_jsonpCallback",//callback的function名称
			    success : function(json){
			       
			    },
			    error:function(){
			        alert('成功');
			    }
			});
		}
	</script>
</head>
<body>

	<div id="importBox" class="hide">
		<form id="importForm" action="${ctx}/sms/jmsgDeliverNumber/import" method="post" enctype="multipart/form-data"
			class="form-search" style="padding-left:20px;text-align:center;" onsubmit="loading('正在导入，请稍等...');"><br/>
			<input id="uploadFile" name="file" type="file" style="width:330px"/><br/><br/>　
			<input id="btnImportSubmit" class="btn btn-primary" type="submit" value="   导    入   "/>
			&nbsp;&nbsp;
			<a href="${ctx}/sms/jmsgDeliverNumber/import/template">下载模板</a>
			<span class="help-inline"><font color="red">注意事项：导入时不需要表头，单元格格式是文本</font> </span>
		</form>
	</div>

	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/sms/jmsgDeliverNumber/">信息列表</a></li>
		<shiro:hasPermission name="sms:jmsgDeliverNumber:edit">
		<li><a href="${ctx}/sms/jmsgDeliverNumber/config">信息添加</a></li>
		</shiro:hasPermission>
	</ul>
	
	<form:form id="searchForm" modelAttribute="jmsgDeliverNumber" action="${ctx}/sms/jmsgDeliverNumber/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>用户ID：</label>
				<sys:treeselect id="user" name="user.id" value="${jmsgDeliverNumber.user.id}" labelName="user.name" labelValue="${jmsgDeliverNumber.user.name}"
					title="用户" url="/sys/office/treeData?type=3" cssClass="input-small" allowClear="true" notAllowSelectParent="true"/>
			</li>
			<li><label>接入号：</label>
				<form:input path="spNumber" htmlEscape="false" maxlength="32" class="input-medium"/>
			</li>
			<li class="btns">
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
			<!-- <input class="btn btn-primary" type="button" onclick="refreshMoRule()"  value="刷新缓存"/> -->
			<input id="btnImport" class="btn btn-primary" type="button" value="导入"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>用户</th>
				<th>接入号</th>
				<th>创建时间</th>
				<shiro:hasPermission name="sms:jmsgDeliverNumber:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="jmsgDeliverNumber">
			<tr>
				<td>
					${jmsgDeliverNumber.user.name}
				</td>
				<td>
					${jmsgDeliverNumber.spNumber}
				</td>
				<td>
					<fmt:formatDate value="${jmsgDeliverNumber.createtime}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<shiro:hasPermission name="sms:jmsgDeliverNumber:edit"><td>
    				<!-- <a href="${ctx}/sms/jmsgDeliverNumber/form?id=${jmsgDeliverNumber.id}">修改</a> -->
					<a href="${ctx}/sms/jmsgDeliverNumber/delete?id=${jmsgDeliverNumber.id}&spNumber=${jmsgDeliverNumber.spNumber}" onclick="return confirmx('确认要删除该用户上行接入号吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>