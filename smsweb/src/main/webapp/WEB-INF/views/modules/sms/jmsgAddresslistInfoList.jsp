<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>联系人列表</title>
	<meta name="decorator" content="default"/>
	<script src="${ctxStatic}/docheck/docheck.js" type="text/javascript"></script>
	<script type="text/javascript">
		$(document).ready(function() {
			
		});
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").submit();
        	return false;
        }
		
		function onDelete(){
			var ids = getCheckboxValue("id");
			if(!ids){
				alertx("请选择要删除的数据");
			}else{
				
				top.$.jBox.confirm("确认要删除该数据吗？","系统提示",function(v,h,f){
					if(v=="ok"){
						$("#searchForm").attr("action","${ctx}/sms/jmsgAddresslistInfo/batchDelete?ids="+ids);
						$("#searchForm").submit();
					}
				},{buttonsFocus:1});
				top.$('.jbox-body .jbox-icon').css('top','55px');
			}
		}
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/sms/jmsgAddresslistInfo/">信息列表</a></li>
		<shiro:hasPermission name="sms:jmsgAddresslistInfo:edit"><li><a href="${ctx}/sms/jmsgAddresslistInfo/form?group.id=${jmsgAddresslistInfo.group.id}&group.name=${jmsgAddresslistInfo.group.name}">信息添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="jmsgAddresslistInfo" action="${ctx}/sms/jmsgAddresslistInfo/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>群组：</label>
                <sys:treeselect id="group" name="group.id" value="${jmsgAddresslistInfo.group.id}" labelName="group.name" labelValue="${jmsgAddresslistInfo.group.name}"
					title="群组" url="/sms/jmsgAddresslistGroup/treeData" cssClass="" allowClear="true"/>
			</li>
			<li><label>手机号码：</label>
				<form:input path="phone" htmlEscape="false" maxlength="12" class="input-medium"/>
			</li>
			<li><label>姓名：</label>
				<form:input path="contacts" htmlEscape="false" maxlength="40" class="input-medium"/>
			</li>
			<li><label>时间：</label>
				<input name="createtimeQ" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					value="<fmt:formatDate value="${jmsgAddresslistInfo.createtimeQ}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});"/>-
				<input name="createtimeZ" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					value="<fmt:formatDate value="${jmsgAddresslistInfo.createtimeZ}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
				<input id="btnDelete" class="btn btn-primary" type="button" value="批量删除" onclick="javascript:onDelete()"/> 
			</li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th><input type="checkbox" onclick="doCheckAll(this,'id', this.checked)"></th>
				<th>群组名称</th>
				<th>手机号码</th>
				<th>姓名</th>
				<th>邮箱</th>
				<th>生日</th>
				<shiro:hasPermission name="sms:jmsgAddresslistInfo:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="jmsgAddresslistInfo">
			<tr onclick="selectTr(this, '${jmsgAddresslistInfo.id}' ,'id')">
				<td><input type="checkbox" name="id" id="${jmsgAddresslistInfo.id}" value="${jmsgAddresslistInfo.id}" onclick="if(this.checked){this.checked=false;}else{this.checked=true;}"></td>
				<td>
					${jmsgAddresslistInfo.group.name}
				</td>
				<td>
					${jmsgAddresslistInfo.phone}
				</td>
				<td>
					${jmsgAddresslistInfo.contacts}
				</td>
				<td>
					${jmsgAddresslistInfo.email}
				</td>
				<td>
					${jmsgAddresslistInfo.birthday}
				</td>
				<shiro:hasPermission name="sms:jmsgAddresslistInfo:edit"><td>
    				<a href="${ctx}/sms/jmsgAddresslistInfo/form?id=${jmsgAddresslistInfo.id}">修改</a>
					<a href="${ctx}/sms/jmsgAddresslistInfo/delete?id=${jmsgAddresslistInfo.id}" onclick="return confirmx('确认要删除该联系人吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>