<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>用户签名管理</title>
	<meta name="decorator" content="default"/>
	<script src="${ctxStatic}/docheck/docheck.js" type="text/javascript"></script>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#btnMenu").remove();
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
				ids = encodeURI(encodeURI(ids));
				top.$.jBox.confirm("确认要删除该数据吗？","系统提示",function(v,h,f){
					if(v=="ok"){
						$("#searchForm").attr("action","${ctx}/sms/jmsgUserSign/batchDelete?ids="+ids);
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
		<li class="active"><a href="${ctx}/sms/jmsgUserSign/list?user.id=${jmsgUserSign.user.id}&user.name=${jmsgUserSign.user.name}"">信息列表</a></li>
		<shiro:hasPermission name="sms:jmsgUserSign:edit"><li><a href="${ctx}/sms/jmsgUserSign/form?user.id=${jmsgUserSign.user.id}&user.name=${jmsgUserSign.user.name}">信息添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="jmsgUserSign" action="${ctx}/sms/jmsgUserSign/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>用户：</label>
				<sys:treeselect id="user" name="user.id" value="${jmsgUserSign.user.id}" labelName="user.name" labelValue="${jmsgUserSign.user.name}"
					title="用户" url="/sys/office/treeData?type=3" cssClass="input-small" allowClear="true" notAllowSelectParent="true"/>
			</li>
			<li><label>签名：</label>
				<form:input path="sign" htmlEscape="false" maxlength="255" class="input-medium"/>
			</li>
			<li><label>日期：</label>
				<input name="createtimeQ" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					value="<fmt:formatDate value="${jmsgUserSign.createtimeQ}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});"/>
					--
				<input name="createtimeZ" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					value="<fmt:formatDate value="${jmsgUserSign.createtimeZ}" pattern="yyyy-MM-dd HH:mm:ss"/>"
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
				<th>用户</th>
				<th>签名</th>
				<th>创建时间</th>
				<shiro:hasPermission name="sms:jmsgUserSign:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="jmsgUserSign">
			<tr onclick="selectTr(this, '${jmsgUserSign.id}' ,'id')">
				<td>
					<input type="checkbox" name="id" id="${jmsgUserSign.id}" value="${jmsgUserSign.id}_${jmsgUserSign.user.id}_${jmsgUserSign.sign}" 
					onclick="if(this.checked){this.checked=false;}else{this.checked=true;}">
				</td>
				<!-- <td><a href="${ctx}/sms/jmsgUserSign/form?id=${jmsgUserSign.id}">
					${jmsgUserSign.user.name}
				</a></td> -->
				<td>
					${jmsgUserSign.user.name}
				</td>
				<td>
					${jmsgUserSign.sign}
				</td>
				<td>
					<fmt:formatDate value="${jmsgUserSign.createtime}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<shiro:hasPermission name="sms:jmsgUserSign:edit"><td>
    				<!-- <a href="${ctx}/sms/jmsgUserSign/form?id=${jmsgUserSign.id}">修改</a> -->
					<a href="${ctx}/sms/jmsgUserSign/delete?id=${jmsgUserSign.id}" onclick="return confirmx('确认要删除该用户签名吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
