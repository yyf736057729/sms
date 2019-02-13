<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>通道分组管理</title>
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
						$("#searchForm").attr("action","${ctx}/sms/jmsgGatewayGroup/batchDelete?ids="+ids);
						$("#searchForm").submit();
					}
				},{buttonsFocus:1});
				top.$('.jbox-body .jbox-icon').css('top','55px');
			}
		}

        function onCache(){
            top.$.jBox.confirm("确认要缓存吗？","系统提示",function(v,h,f){
                if(v=="ok"){
                    $("#searchForm").attr("action","${ctx}/sms/jmsgGatewayGroup/onCache");
                    $("#searchForm").submit();
                }
            },{buttonsFocus:1});
            top.$('.jbox-body .jbox-icon').css('top','55px');
        }
		
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/sms/jmsgGatewayGroup/">信息列表</a></li>
		<shiro:hasPermission name="sms:jmsgGatewayGroup:edit"><li><a href="${ctx}/sms/jmsgGatewayGroup/form">信息添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="jmsgGatewayGroup" action="${ctx}/sms/jmsgGatewayGroup/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>分组名称：</label>
				<form:select path="groupId" class="input-xlarge">
					<form:option value="" label="全部"/>
					<form:options items="${fns:getGroupList()}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
			</li>
			<li><label>运营商：</label>
				<form:select path="phoneType" class="input-medium">
					<form:option value="" label="全部"/>
					<form:options items="${fns:getDictList('phone_type')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
			</li>
			<li><label>省份：</label>
				<!--<sys:treeselect id="provinceId" name="provinceId" value="${jmsgGatewayGroup.provinceId}" labelName="provinceName" labelValue="${jmsgGatewayGroup.provinceName}"
					title="区域" url="/sys/area/treeData" cssClass="input-small" allowClear="true" notAllowSelectParent="true"/>-->
				<form:select path="provinceId" class="input-medium ">
					<form:option value="" label="全部"/>
					<form:options items="${fns:getDictList('phone_province')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
			</li>
			<li><label>通道名称：</label>
				<form:select path="gatewayId" class="input-xlarge">
					<form:option value="" label="全部"/>
					<form:options items="${fns:getGatewayList()}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
			</li>
			<li class="btns">
				<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
				 <input id="btnDelete" class="btn btn-primary" type="button" value="批量删除" onclick="javascript:onDelete()"/> 
				 
				 <%--<input id="btnRef" class="btn btn-primary" type="button" value="刷新缓存" onclick="javascript:onCache();"/>--%>
				<%--windowOpen('http://114.55.90.98:8080/refCache.jsp?t=group','group',900,550);--%>
				<!--  <a class="btn btn-primary" href="javascript:onDelete();" onclick="return confirmx('确认要删除该数据吗？', this.href)">删除</a>-->
			</li>
			
			<li class="btns"></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th><input type="checkbox" onclick="doCheckAll(this,'id', this.checked)"></th>
				<th>分组名称</th>
				<th>运营商</th>
				<th>省份</th>
				<th>通道名称</th>
				<th>优先级</th>
				<shiro:hasPermission name="sms:jmsgGatewayGroup:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="jmsgGatewayGroup">
			<tr onclick="selectTr(this, '${jmsgGatewayGroup.id}' ,'id')">
				<td><input type="checkbox" name="id" id="${jmsgGatewayGroup.id}" value="${jmsgGatewayGroup.id}_${jmsgGatewayGroup.groupId}_${jmsgGatewayGroup.phoneType}_${jmsgGatewayGroup.provinceId}_${jmsgGatewayGroup.gatewayId}" 
				onclick="if(this.checked){this.checked=false;}else{this.checked=true;}"></td>
				<td>
					${jmsgGatewayGroup.groupName}
				</td>
				<td>
					${fns:getDictLabel(jmsgGatewayGroup.phoneType, "phone_type", jmsgGatewayGroup.phoneType)}
				</td>
				<td>
					${fns:getDictLabel(jmsgGatewayGroup.provinceId, "phone_province", jmsgGatewayGroup.provinceId)}
				</td>
				<td>
					${jmsgGatewayGroup.gatewayName}
				</td>
				<td>
					${jmsgGatewayGroup.level}
				</td>
				<shiro:hasPermission name="sms:jmsgGatewayGroup:edit"><td>
    				<!-- <a href="${ctx}/sms/jmsgGatewayGroup/form?id=${jmsgGatewayGroup.id}">修改</a> -->
					<a href="${ctx}/sms/jmsgGatewayGroup/delete?id=${jmsgGatewayGroup.id}&groupId=${jmsgGatewayGroup.groupId}&phoneType=${jmsgGatewayGroup.phoneType}&provinceId=${jmsgGatewayGroup.provinceId}&gatewayId=${jmsgGatewayGroup.gatewayId}" 
					onclick="return confirmx('确认要删除该通道关系吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>