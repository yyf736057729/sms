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
		function sendOne(mmsId,mmsTitle){
			var url = "${ctx}/mms/jmsgMmsTask/sendOneInit?mmsId="+mmsId+"&mmsTitle="+mmsTitle;
			windowOpen(url,Math.random(),600,240);
		}
		//function sendMore(mmsId,mmsTitle){
		//	var url = "${ctx}/mms/jmsgMmsTask/sendMoreInit?mmsId="+mmsId+"&mmsTitle="+mmsTitle;
		//	windowOpen(url,Math.random(),620,260);
		//}
		function onCheck(status){
			var ids = getCheckboxValue("id");
			if(!ids){
				alertx("请选择要审核的数据");
			}else{
				$("#searchForm").attr("action","${ctx}/mms/jmsgMmsData/checkMms?status="+status);
				$("#searchForm").submit();
				return false;
			}
		}
		
		function onView(id){
			var url = "${ctx}/mms/jmsgMmsData/view?id="+id+"&viewFlag=1";
			windowOpen(url,id,800,600);
		}
		
		function onCheckView(id){
			var url = "${ctx}/mms/jmsgMmsData/checkView?id="+id;
			windowOpen(url,id,600,320);
		}
		
		function sendMore(id,mmsTitle){
			
			alert(mmsTitle);
			//var title = encodeURI(mmsTitle);
			$("#searchForm").attr("action","${ctx}/mms/jmsgMmsTask/sendMoreInit?mmsId="+id+"&mmsTitle="+mmsTitle);
			$("#searchForm").submit();		
		}
		
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/mms/jmsgMmsData/">彩信素材列表</a></li>
		<%-- <shiro:hasPermission name="mms:jmsgMmsData:edit"><li><a href="${ctx}/mms/jmsgMmsData/form">彩信素材添加</a></li></shiro:hasPermission> --%>
	</ul>
	<form:form id="searchForm" modelAttribute="jmsgMmsData" action="${ctx}/mms/jmsgMmsData/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<shiro:hasAnyPermissions name="jmsg:admin,jmsg:agency">
			<li><label>创建人：</label>
				<sys:treeselect id="user" name="user.id" value="${jmsgMmsData.user.id}" labelName="user.name" labelValue="${jmsgMmsData.user.name}"
					title="用户" url="/sys/office/treeData?type=3" cssClass="input-small" allowClear="true" notAllowSelectParent="true"/>
			</li>
			</shiro:hasAnyPermissions>
			<li><label>彩信ID：</label>
				<form:input path="id" htmlEscape="false" maxlength="200" class="input-small"/>
			</li>
			<li><label>彩信名称：</label>
				<form:input path="mmsTitle" htmlEscape="false" maxlength="200" class="input-medium"/>
			</li>
			<shiro:hasPermission name="jmsg:admin">
			<li><label>审核人：</label>
				<form:input path="checkUserName" htmlEscape="false" maxlength="200" class="input-medium"/>
			</li>
			<li><label>审核状态：</label>
				<form:select path="checkStatus" class="input-medium" htmlEscape="false">
					<form:option value="">全部</form:option>
					<form:options items="${fns:getDictList('check_status')}" itemLabel="label" itemValue="value"/>
				</form:select>
			</li>
			</shiro:hasPermission>
			<li><label>时间：</label>
				<input name="createDatetimeQ" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					value="<fmt:formatDate value="${jmsgMmsData.createDatetimeQ}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:true});"/> - 
				<input name="createDatetimeZ" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					value="<fmt:formatDate value="${jmsgMmsData.createDatetimeZ}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:true});"/>		
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>彩信ID</th>
				<th>彩信主题</th>
				<th>创建时间</th>
				<th>最后修改时间</th>
				<th>审核状态</th>
				<shiro:hasPermission name="jmsg:admin">
				<th>审核人</th>
				</shiro:hasPermission>	
				<th>备注</th>
				<shiro:hasAnyPermissions name="jmsg:admin,jmsg:agency">
				<th>创建人</th>
				<th>登录名</th>
				</shiro:hasAnyPermissions>
				<shiro:hasPermission name="jmsg:admin">
				<th>组织</th>
				</shiro:hasPermission>
				<shiro:hasPermission name="mms:jmsgMmsData:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="jmsgMmsData">
			<tr ${jmsgMmsData.checkStatus eq 0?'style="color: red;"':''} >
				<td><a href="javascript:onView(${jmsgMmsData.id})">
					${jmsgMmsData.id}
					</a>
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
				<td>
					${fns:getDictLabel(jmsgMmsData.checkStatus,'check_status','')} 
					<c:if test="${jmsgMmsData.checkStatus eq 0 }">&nbsp;&nbsp;<a href="javascript:onCheckView(${jmsgMmsData.id})">查看</a></c:if>
				</td>
				<shiro:hasPermission name="jmsg:admin">
				<td>
					<c:if test="${jmsgMmsData.checkStatus eq 1 }">${jmsgMmsData.checkUserName}</c:if>
				</td>	
				</shiro:hasPermission>
				<td>
					${jmsgMmsData.remark}
				</td>
				<shiro:hasAnyPermissions name="jmsg:admin,jmsg:agency">
				<td>
					${jmsgMmsData.user.name}
				</td>
				<td>
					${jmsgMmsData.user.loginName}
				</td>
				</shiro:hasAnyPermissions>
				<shiro:hasPermission name="jmsg:admin">
				<td>
					${jmsgMmsData.user.company.name}
				</td>	
				</shiro:hasPermission>
				<shiro:hasPermission name="mms:jmsgMmsData:edit"><td>
    				<c:if test="${jmsgMmsData.checkStatus eq 1 || jmsgMmsData.checkStatus eq 8}">
    				【<a href="${ctx}/mms/jmsgMmsTask/sendMoreInit?mmsId=${jmsgMmsData.id}&mmsTitle=${fns:urlEncode(jmsgMmsData.mmsTitle)}"><b>文件群发</b></a>】
    				【<a href="${ctx}/mms/jmsgMmsTask/sendOneInit?mmsId=${jmsgMmsData.id}&mmsTitle=${fns:urlEncode(jmsgMmsData.mmsTitle)}"><b>单个发送</b></a>】
    				</c:if>
    				<c:if test="${jmsgMmsData.useFlag eq 0}">
    				  <%--  【<a href="${ctx}/mms/jmsgMmsData/form?id=${jmsgMmsData.id}">修改</a>】 --%>
						【<a href="${ctx}/mms/jmsgMmsData/delete?id=${jmsgMmsData.id}" onclick="return confirmx('确认要删除该彩信素材吗？', this.href)"><b>删除</b></a>】
					</c:if>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>