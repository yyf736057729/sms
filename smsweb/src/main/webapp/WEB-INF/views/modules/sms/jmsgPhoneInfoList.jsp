<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>号段管理管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			
		});
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").submit();
        	return false;
        }
        function onCache(){
            top.$.jBox.confirm("确认要缓存吗？","系统提示",function(v,h,f){
                if(v=="ok"){
                    $("#searchForm").attr("action","${ctx}/sms/jmsgPhoneInfo/onCache");
                    $("#searchForm").submit();
                }
            },{buttonsFocus:1});
            top.$('.jbox-body .jbox-icon').css('top','55px');
        }
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/sms/jmsgPhoneInfo/">信息列表</a></li>
		<shiro:hasPermission name="sms:jmsgPhoneInfo:edit"><li><a href="${ctx}/sms/jmsgPhoneInfo/form">信息添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="jmsgPhoneInfo" action="${ctx}/sms/jmsgPhoneInfo/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>号段(模糊)：</label>
				<form:input path="phone" htmlEscape="false" maxlength="7" class="input-medium"/>
			</li>
			<li><label>运营商：</label>
				<form:select path="phoneType" class="input-medium required">
					<form:option value="" label="全部"/>
					<form:options items="${fns:getDictList('phone_type')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
			</li>
			<li><label>归属省：</label>
				<form:input path="phoneProv" htmlEscape="false" maxlength="7" class="input-medium"/>
			</li>
			<li><label>归属市：</label>
				<form:input path="phoneCity" htmlEscape="false" maxlength="7" class="input-medium"/>
			</li>			
			<li><label>省市代码：</label>
				<form:input path="phoneCityCode" htmlEscape="false" maxlength="7" class="input-medium"/>
			</li>						
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
			</li>${fns:getDictLabel('phone_info', "system_update_msg", '')}
			<%--<li class="clearfix"><input id="btnRef" class="btn btn-primary" type="button" value="刷新缓存" onclick="javascript:onCache();"/></li>--%>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>号段</th>
				<th>运营商</th>
				<th>归属省</th>
				<th>归属市</th>
				<th>邮编</th>
				<th>省市代码</th>
				<!--<shiro:hasPermission name="sms:jmsgPhoneInfo:edit"><th>操作</th></shiro:hasPermission>-->
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="jmsgPhoneInfo">
			<tr>
				<td><a href="${ctx}/sms/jmsgPhoneInfo/form?id=${jmsgPhoneInfo.id}">
					${jmsgPhoneInfo.phone}
				</a></td>
				<td>
					${fns:getDictLabel(jmsgPhoneInfo.phoneType, "phone_type", jmsgPhoneInfo.phoneType)}
				</td>
				<td>
					${jmsgPhoneInfo.phoneProv}
				</td>
				<td>
					${jmsgPhoneInfo.phoneCity}
				</td>
				<td>
					${jmsgPhoneInfo.zip}
				</td>
				<td>
					${jmsgPhoneInfo.phoneCityCode}
				</td>	
				<!--<shiro:hasPermission name="sms:jmsgPhoneInfo:edit"><td>
    				<a href="${ctx}/sms/jmsgPhoneInfo/form?id=${jmsgPhoneInfo.id}">修改</a>
					<a href="${ctx}/sms/jmsgPhoneInfo/delete?id=${jmsgPhoneInfo.id}" onclick="return confirmx('确认要删除该号段管理吗？', this.href)">删除</a>
				</td></shiro:hasPermission>-->
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>