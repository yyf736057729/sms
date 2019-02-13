<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>彩信素材管理</title>
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
		function onCheck(status){
			var ids = getCheckboxValue("id");
			if(!ids){
				alertx("请选择要审核的数据");
			}else{
				if('0' == status){
					$("#searchForm").attr("action","${ctx}/mms/jmsgMmsData/checkMmsForm?ids="+ids);
				}else{
					$("#searchForm").attr("action","${ctx}/mms/jmsgMmsData/checkMms?status="+status+"&ids="+ids);
				}
				$("#searchForm").submit();
				return false;
			}
		}
		
		function onView(id){
			var url = "${ctx}/mms/jmsgMmsData/view?id="+id+"&viewFlag=1";
			windowOpen(url,id,800,600);
		}
		
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<shiro:hasPermission name="mms:jmsgMmsData:check"><li class="active"><a href="${ctx}/mms/jmsgMmsData/checkList">彩信素材待审核列表</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="jmsgMmsData" action="${ctx}/mms/jmsgMmsData/checkList" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>创建人：</label>
				<sys:treeselect id="user" name="user.id" value="${jmsgMmsData.user.id}" labelName="" labelValue="${jmsgMmsData.user.name}"
					title="用户" url="/sys/office/treeData?type=3" cssClass="input-small" allowClear="true" notAllowSelectParent="true"/>
			</li>
			<li><label>彩信名称：</label>
				<form:input path="mmsTitle" htmlEscape="false" maxlength="20" class="input-medium"/>
			</li>
			<li><label>时间：</label>
				<input name="createDatetimeQ" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					value="<fmt:formatDate value="${jmsgMmsData.createDatetimeQ}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:true});"/> - 
				<input name="createDatetimeZ" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					value="<fmt:formatDate value="${jmsgMmsData.createDatetimeZ}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:true});"/>		
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<shiro:hasPermission name="mms:jmsgMmsData:check">
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="button" value="审核通过" onclick="javascript:onCheck('1')"/></li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="button" value="审核不通过" onclick="javascript:onCheck('0')"/></li>
			</shiro:hasPermission>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th><input type="checkbox" onclick="doCheckAll(this,'id', this.checked)"></th>
				<th>创建人</th>
				<th>登录名</th>
				<th>组织机构</th>
				<th>彩信ID</th>
				<th>名称</th>
				<th>创建时间</th>
				<th>最后修改时间</th>
				<!-- <th>审核状态</th> -->
				<shiro:hasPermission name="mms:jmsgMmsData:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="jmsgMmsData">
			<tr onclick="selectTr(this, '${jmsgMmsData.id}' ,'id')">
				<td><input type="checkbox" name="id" id="${jmsgMmsData.id}" value="${jmsgMmsData.id}" onclick="if(this.checked){this.checked=false;}else{this.checked=true;}"></td>
				<td>
					${jmsgMmsData.user.name}
				</td>
				<td>${jmsgMmsData.user.loginName}</td>
				<td>${jmsgMmsData.user.company.name}</td>
				<td>
					${jmsgMmsData.id}
				</td>
				<td>
					${jmsgMmsData.mmsTitle}
				</td>
				<td>
					<fmt:formatDate value="${jmsgMmsData.createDatetime}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
					<fmt:formatDate value="${jmsgMmsData.updateDatetime}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<!-- 
				<td>
					${fns:getDictLabel(jmsgMmsData.checkStatus,'check_status','')} 
				</td> -->
				<td>
    				<a href="javascript:onView(${jmsgMmsData.id})">查看</a>
					<!-- <a href="${ctx}/mms/jmsgMmsData/delete?id=${jmsgMmsData.id}" onclick="return confirmx('确认要删除该彩信素材吗？', this.href)">删除</a> -->
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>