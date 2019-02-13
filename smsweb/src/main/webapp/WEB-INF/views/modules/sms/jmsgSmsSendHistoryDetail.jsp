<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>短信发送明细</title>
	<meta name="decorator" content="default"/>
	<script src="${ctxStatic}/docheck/docheck.js" type="text/javascript"></script>
	<script type="text/javascript">
		var curDate = new Date();
		$(document).ready(function() {
		});
		
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").submit();
        	return false;
        }
		function onQuery(){
			$("#searchForm").submit();
		}
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").submit();
        	return false;
        }
		function onView(taskid,id,createDatetime){
			var url = "${ctx}/sms/jmsgSmsSend/detailView?taskid="+taskid+"&bizid="+id+"&createDatetime="+createDatetime;
			windowOpen(url,taskid,900,550);
		}
		function showAlert(content){
			alert(content);
		}
		
		function showTestModal(smsContent){
			$('#smsContent').val(smsContent);
			$('#smsContentSize').text(smsContent.length);
			$('#smsContentModal').modal('show');
		}
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/sms/jmsgSmsSend/historyDetailInit">信息列表</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="jmsgSmsSend" action="${ctx}/sms/jmsgSmsSend/historyDetailList" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>批次号：</label>
				<form:input path="taskId" maxlength="64" class="input-medium" id="taskId"/>
			</li>
			<li><label>手机号码：</label>
				<form:input path="phone" maxlength="20" class="input-medium" id="phone"/>
			</li>
			<li><label>短信ID：</label>
				<form:input path="id" maxlength="64" class="input-medium" id="id"/>
			</li>
			<li><label>用户名称：</label>
				<sys:treeselect id="user" name="user.id" value="${jmsgSmsSend.user.id}" labelName="user.name" labelValue="${jmsgSmsSend.user.name}"
					title="用户" url="/sys/office/treeData?type=3" cssClass="input-small" allowClear="true" notAllowSelectParent="true"/>
			</li>
			<shiro:hasPermission name="jmsg:admin">
			<li><label>通道：</label>
				<form:select path="channelCode" class="input-medium">
					<form:option value="" label="请选择"/>
					<form:options items="${fns:getGatewayList()}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
			</li>
			</shiro:hasPermission>
			<li><label>运营商：</label>
				<form:select path="phoneType" class="input-medium">
					<form:option value="" label="全部"/>
					<form:options items="${fns:getDictList('phone_type')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
			</li>
			<li><label>发送状态：</label>
				<form:select path="sendStatus" class="input-medium">
					<form:option value="" label="全部"/>
					<form:option value="P" label="待发送"/>
					<form:option value="T0" label="发送成功"/>
					<form:option value="F" label="发送失败"/>
				</form:select>
			</li>
			<li><label>状态报告：</label>
				<form:select path="reportStatus" class="input-medium">
					<form:option value="" label="全部"/>
					<form:option value="P" label="状态空"/>
					<form:option value="T" label="成功"/>
					<form:option value="F" label="失败"/>
				</form:select>
			</li>		
			<li><label>日期：</label>
				<input name="createDatetimeQ" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					value="<fmt:formatDate value="${jmsgSmsSend.createDatetimeQ}" pattern="yyyy-MM"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM',isShowClear:false});"/>
					--
				<input name="createDatetimeZ" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					value="<fmt:formatDate value="${jmsgSmsSend.createDatetimeZ}" pattern="yyyy-MM"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM',isShowClear:false});"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="button" value="查询" onclick="javascript:onQuery();"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th><input type="checkbox" onclick="doCheckAll(this,'id', this.checked)"></th>
				<th>短信ID</th>
				<th>用户ID</th>
				<th>用户名称</th>
				<shiro:hasPermission name="jmsg:admin">
				<th>机构</th>
				</shiro:hasPermission>
				<th>批次号</th>
				<th>短信内容</th>
				<th>扣费条数</th>
				<th>手机号码</th>
				<th>运营商</th>
				<th>省份</th>
				<th>地区</th>
				<th>提交时间</th>
				<th>发送状态</th>
				<th>状态报告</th>
				<shiro:hasPermission name="jmsg:admin">
				<th>通道ID</th>
				<th>通道分组</th>
				<th>通道名称</th>
				</shiro:hasPermission>
				<th>接入号</th>
				<th>操作</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="jmsgSmsSend">
			<tr onclick="selectTr(this, '${jmsgSmsSend.id}' ,'id')">
				<td><input type="checkbox" name="id" id="${jmsgSmsSend.id}" value="${jmsgSmsSend.id}" onclick="if(this.checked){this.checked=false;}else{this.checked=true;}"></td>
				<td><a href="${ctx}/sms/jmsgSmsSend/detailView?taskid=${jmsgSmsSend.taskId}&bizid=${jmsgSmsSend.id}&createDatetime=<fmt:formatDate value="${jmsgSmsSend.createDatetime}" pattern="yyyy-MM-dd HH:mm:ss"/>">${jmsgSmsSend.id}</a></td>
				<td>
					${jmsgSmsSend.user.id}
				</td>
				<td>
					${fns:getUserById(jmsgSmsSend.user.id).loginName}
				</td>
				<shiro:hasPermission name="jmsg:admin">
				<td>
					${fns:getUserById(jmsgSmsSend.user.id).company}
				</td>
				</shiro:hasPermission>			
				<td>
					${jmsgSmsSend.taskId}
				</td>
				<td>
					${fn:substring(jmsgSmsSend.smsContent, 0, 8)}...
					<a href="javascript:showTestModal('${fns:escapeHtml(jmsgSmsSend.smsContent)}')">[更多查看]</a>
				</td>
				<td>
					${jmsgSmsSend.payCount}
				</td>
				<td>
					${jmsgSmsSend.phone}
				</td>
				<td>
					${fns:getDictLabel(jmsgSmsSend.phoneType,'phone_type',jmsgSmsSend.phoneType)}
				</td>
				<td>
					${fns:getDictLabel(fn:substring(jmsgSmsSend.areaCode,0,2),'phone_province',jmsgSmsSend.areaCode)}	
				</td>
				<td>
					<!-- ${jmsgSmsSend.cityName} -->
					${fns:getCity(jmsgSmsSend.areaCode).phoneCity}
				</td>
				<td>
					<fmt:formatDate value="${jmsgSmsSend.sendDatetime}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>	
				<td>
					${fns:getDictLabel(jmsgSmsSend.sendStatus,'mms_send_status',jmsgSmsSend.sendStatus)} 
				</td>
				<td>
					<c:if test="${fns:startsWith(jmsgSmsSend.reportStatus,'P')}">
						待返回
					</c:if>
					<c:if test="${fns:startsWith(jmsgSmsSend.reportStatus,'T')}">
						成功
					</c:if>
					<c:if test="${fns:startsWith(jmsgSmsSend.reportStatus,'F')}">
						失败
					</c:if>					
				</td>
				<shiro:hasPermission name="jmsg:admin">				
				<td>
					${jmsgSmsSend.channelCode}
				</td>
				<td>
					<!-- ${jmsgSmsSend.user.groupId} -->
					<!-- ${fns:getUserById(jmsgSmsSend.user.id).groupId} -->
					${fns:getJmsgGroup(fns:getUserById(jmsgSmsSend.user.id).groupId).name}
				</td>
				<td>
					${fns:getGatewayInfo(jmsgSmsSend.channelCode).gatewayName}
				</td>
				</shiro:hasPermission>	
				<td>
					${jmsgSmsSend.spNumber}
				</td>
				<td>
					<a href='javascript:onView("${jmsgSmsSend.taskId}","${jmsgSmsSend.id}","<fmt:formatDate value="${jmsgSmsSend.createDatetime}" pattern="yyyy-MM-dd HH:mm:ss"/>")'>查看</a>
				</td>
			</tr>
		</c:forEach>	
		</tbody>
	</table>
	<div class="pagination">${page}</div>
	
	<div class="modal fade" style="display:none;" id="smsContentModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
   		<div class="modal-dialog">
      		<div class="modal-content">
        		<div class="modal-header">
            		<button type="button" class="close" 
               			data-dismiss="modal" aria-hidden="true">
                  		&times;
            		</button>
		            <h4 class="modal-title" id="myModalLabel">
		              	短信内容
		            </h4>
         		</div>
         		<div class="modal-body">
         			<form id="gateWaySendForm" class="form-horizontal">
	                	<div class="control-group">
	                     	<div class="controls1">
	                         	<textarea style="width:498px;" readonly="readonly" name="smsContent" id="smsContent" rows="6" cols="8"></textarea>
	                         	共 <label name="smsContentSize" id="smsContentSize"></label>  个字。
	                     	</div>
	                 	</div>
                 	</form>
         		</div>
      		</div><!-- /.modal-content -->
		</div><!-- /.modal -->
	</div>
</body>
</html>