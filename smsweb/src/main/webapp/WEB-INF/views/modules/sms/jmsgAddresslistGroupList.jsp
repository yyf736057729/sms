<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>公司管理</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/treetable.jsp" %>
	<script type="text/javascript">
		$(document).ready(function() {
			var tpl = $("#treeTableTpl").html().replace(/(\/\/\<!\-\-)|(\/\/\-\->)/g,"");
			
			var data = ${fns:toJson(list)}, rootId = rootId = "${not empty jmsgAddresslistGroup.pid ? jmsgAddresslistGroup.pid : '0'}";;
			
			addRow("#treeTableList", tpl, data, rootId, true);
			$("#treeTable").treeTable({expandLevel : 10});
		});
		function addRow(list, tpl, data, pid, root){
			for (var i=0; i<data.length; i++){
				var row = data[i];
				if ((${fns:jsGetVal('row.pid')}) == pid){
					$(list).append(Mustache.render(tpl, {
						pid: (root?0:pid), row: row
					}));
					addRow(list, tpl, data, row.id);
				}
			}
		}
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/sms/jmsgAddresslistGroup/list?pid=${jmsgAddresslistGroup.pid}&parentIds=${jmsgAddresslistGroup.parentIds}&id=${jmsgAddresslistGroup.id}">信息列表</a></li>
		<shiro:hasPermission name="sms:jmsgAddresslistGroup:edit"><li><a href="${ctx}/sms/jmsgAddresslistGroup/form?parent.id=${jmsgAddresslistGroup.id}">信息添加</a></li></shiro:hasPermission>
	</ul>
	<sys:message content="${message}"/>
	<table id="treeTable" class="table table-striped table-bordered table-condensed">
		<thead>
		<tr>
			<th>群组名称</th>
			<th>排序</th>
			<shiro:hasPermission name="sms:jmsgAddresslistGroup:edit"><th>操作</th></shiro:hasPermission>
		</tr>
		</thead>
		<tbody id="treeTableList"></tbody>
	</table>
	<script type="text/template" id="treeTableTpl">
		<tr id="{{row.id}}" pId="{{pid}}">
			<td><a href="${ctx}/sms/jmsgAddresslistGroup/form?id={{row.id}}">{{row.name}}</a></td>
			<td>{{row.sort}}</td>
			<shiro:hasPermission name="sms:jmsgAddresslistGroup:edit"><td>
				<a href="${ctx}/sms/jmsgAddresslistGroup/form?id={{row.id}}">修改</a>
				<a href="${ctx}/sms/jmsgAddresslistGroup/delete?id={{row.id}}" onclick="return confirmx('要删除该群组及所有子群组项吗？', this.href)">删除</a>
				<a href="${ctx}/sms/jmsgAddresslistGroup/form?parent.id={{row.id}}">添加下级群组</a>
				<a href="${ctx}/sms/jmsgAddresslistGroup/deleteAddressInfo?id={{row.id}}" onclick="return confirmx('要删除该群组内通讯录信息吗？', this.href)">删除通讯录信息</a>
			</td></shiro:hasPermission>
		</tr>
	</script>
</body>
</html>