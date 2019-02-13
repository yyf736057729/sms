<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>短信发送管理管理</title>
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
		
		function phoneView(taskId){
			var url="${ctx}/sms/jmsgSmsTask/phoneView?taskId="+taskId;
			windowOpen(url,taskId,500,380);
		}
		
		function toSend(content){
			$('#fullForm').find('input[id="content"]').val(content);
			$("#fullForm").attr("action", "${ctx}/sms/jmsgSmsTask/smsGroupInit/");
	    	$("#fullForm").submit();
		}
		
		function onView(id){
			var url = "${ctx}/sms/jmsgSmsData/view?id="+id+"&viewFlag=1";
			windowOpen(url,id,800,600);
		}
		
		function showAlert(content){
			alert(content);
		}
		
		function showTestModal(smsContent){
			$('#smsContent').val(smsContent);
			$('#smsContentSize').text(smsContent.length);
			$('#smsContentModal').modal('show');
		}
		
		function onSendView(taskId,resultStatus,sendDatetime){
			var url="${ctx}/sms/jmsgSmsSend/sendResult?taskId="+taskId+"&resultStatus="+resultStatus+"&createDatetimeQ="+sendDatetime;
			windowOpen(url,taskId,1200,800);
		}
		
		function viewYy(remarks,reviewTime){
			$("#reviewRemarks").html("审核意见："+remarks);
			$("#reviewTime").html("审核时间："+reviewTime);
			
			$('#reviewModal').modal('show');
		}
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/sms/jmsgSmsTask/">信息列表</a></li>
		<!--<shiro:hasPermission name="sms:jmsgSmsTask:edit"><li><a href="${ctx}/sms/jmsgSmsTask/form">信息添加</a></li></shiro:hasPermission>-->
	</ul>
	<form:form id="fullForm" method="post">
    	<input type="hidden" id="content" name="content" />
    </form:form>
	<form:form id="searchForm" modelAttribute="jmsgSmsTask" action="${ctx}/sms/jmsgSmsTask/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<shiro:hasAnyPermissions name="jmsg:admin,jmsg:agency">
			<li><label>创建人：</label>
				<sys:treeselect id="user" name="user.id" value="${jmsgSmsTask.user.id}" labelName="user.name" labelValue="${jmsgSmsTask.user.name}"
					title="用户" url="/sys/office/treeData?type=3" cssClass="input-small" allowClear="true" notAllowSelectParent="true"/>
			</li>
			</shiro:hasAnyPermissions>
			<li><label>批次ID：</label>
				<form:input path="id" htmlEscape="false" maxlength="64" class="input-medium"/>
			</li>
			<%-- <li><label>短信ID：</label>
				<form:input path="dataId" htmlEscape="false" maxlength="10" class="input-medium"/>
			</li> --%>
			<li><label>发送时间：</label>
				<input name="sendDatetimeQ" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					value="<fmt:formatDate value="${jmsgSmsTask.sendDatetimeQ}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:true});"/> - 
				<input name="sendDatetimeZ" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					value="<fmt:formatDate value="${jmsgSmsTask.sendDatetimeZ}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:true});"/>			
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-bordered table-condensed">
		<thead>
			<tr>
				<shiro:hasPermission name="sms:jmsgSmsTask:edit"><th>操作　　　　</th></shiro:hasPermission>
				<th>批次ID</th>
				<!-- <th>短信ID</th> -->
				<th>短信内容　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　</th>
				<th>状态　　　</th>
				<th>发送数量</th>
				<th>成功数量</th>
				<th>失败数量</th>
				<th>发送时间　　</th>
				<th>结束时间　　</th>
				<th>更新时间　　</th>
				<shiro:hasAnyPermissions name="jmsg:admin,jmsg:agency">
				<th>创建人　　</th>
				<th>登录名</th>
				</shiro:hasAnyPermissions>
				<shiro:hasPermission name="jmsg:admin">
				<th>机构　　　</th>
				</shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="jmsgSmsTask">
			<tr ${jmsgSmsTask.status eq -2 ? 'class="error"' : '' }>
				<shiro:hasPermission name="sms:jmsgSmsTask:edit">
				<td>
					<a href='javascript:phoneView("${jmsgSmsTask.id}")'>清单</a>
					| <a href='javascript:toSend("${jmsgSmsTask.smsContent}")'>转发</a>
					<c:if test="${jmsgSmsTask.status eq 1 || jmsgSmsTask.status eq 2 || jmsgSmsTask.status eq 8}">
						 | <a href="${ctx}/sms/jmsgSmsTask/updateStatus?id=${jmsgSmsTask.id}&status=9" onclick="return confirmx('确认要停止发送短信吗？', this.href)">停止</a> |
						<a href="${ctx}/sms/jmsgSmsTask/updateStatus?id=${jmsgSmsTask.id}&status=5" onclick="return confirmx('确认要暂停发送短信吗？', this.href)">暂停</a>
						<!-- <a href="${ctx}/sms/jmsgSmsTask/updateStatus?id=${jmsgSmsTask.id}&status='1'" onclick="return confirmx('确认要继续发送短信吗？', this.href)">继续发送</a> -->
					</c:if>
					<c:if test="${jmsgSmsTask.status eq 5}">
						 | <a href="${ctx}/sms/jmsgSmsTask/updateStatus?id=${jmsgSmsTask.id}&status=9" onclick="return confirmx('确认要停止发送短信吗？', this.href)">停止</a> |
						<a href="${ctx}/sms/jmsgSmsTask/updateStatus?id=${jmsgSmsTask.id}&status=8" onclick="return confirmx('确认要继续发送短信吗？', this.href)">继续</a>
					</c:if>	
    				<!-- <a href="${ctx}/sms/jmsgSmsTask/form?id=${jmsgSmsTask.id}">修改</a>
					<a href="${ctx}/sms/jmsgSmsTask/delete?id=${jmsgSmsTask.id}" onclick="return confirmx('确认要删除该短信发送管理吗？', this.href)">删除</a> -->
				</td>
				</shiro:hasPermission>
				<td>
					${jmsgSmsTask.id}
				</td>
				<%-- <td>
					${jmsgSmsTask.dataId}
				</td> --%>
				<td>
					${fn:substring(jmsgSmsTask.smsContent, 0, 60)}...<!-- <a href='javascript:showAlert("${jmsgSmsTask.smsContent}")'>【更多】</a> -->
					<a href="javascript:showTestModal('${jmsgSmsTask.smsContent}')">[更多查看]</a>
				</td>
				<td>
					${fns:getDictLabel(jmsgSmsTask.status,'task_send_status',jmsgSmsTask.status)} 
					<c:if test="${jmsgSmsTask.status eq -2}">
						<a href="javascript:viewYy('${jmsgSmsTask.reviewRemarks}','<fmt:formatDate value="${jmsgSmsTask.reviewTime}" pattern="yyyy-MM-dd HH:mm:ss"/>')">[查看原因]</a>
					</c:if>
				</td>
				<td>
					${jmsgSmsTask.sendCount}
				</td>
				<td>
					<c:if test="${jmsgSmsTask.successCount > 0 && jmsgSmsTask.status eq 3}">
						<a href="javascript:onSendView('${jmsgSmsTask.id}',1,'<fmt:formatDate value="${jmsgSmsTask.sendDatetime}" pattern="yyyy-MM-dd 00:00:00"/>');">${jmsgSmsTask.successCount}</a>
					</c:if>
					<c:if test="${jmsgSmsTask.successCount <= 0}">
						${jmsgSmsTask.successCount}
					</c:if>
				</td>
				<td>
					<c:if test="${jmsgSmsTask.failCount > 0 && jmsgSmsTask.status eq 3}">
						<a href="javascript:onSendView('${jmsgSmsTask.id}',0,'<fmt:formatDate value="${jmsgSmsTask.sendDatetime}" pattern="yyyy-MM-dd 00:00:00"/>');">${jmsgSmsTask.failCount}</a>
					</c:if>
					<c:if test="${jmsgSmsTask.failCount <= 0}">
						${jmsgSmsTask.failCount}
					</c:if>
				</td>
				<td>
					<fmt:formatDate value="${jmsgSmsTask.sendDatetime}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
					<fmt:formatDate value="${jmsgSmsTask.endDatetime}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
					<fmt:formatDate value="${jmsgSmsTask.updateDatetime}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>				
				<shiro:hasAnyPermissions name="jmsg:admin,jmsg:agency">
				<td>${jmsgSmsTask.user.name}</td>
				<td>${jmsgSmsTask.user.loginName}</td>
				</shiro:hasAnyPermissions>
				<shiro:hasPermission name="jmsg:admin">
				<td>${jmsgSmsTask.user.company.name}</td>
				</shiro:hasPermission>
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
	
	<div class="modal fade" style="display:none;" id="reviewModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel1" aria-hidden="true">
   		<div class="modal-dialog">
      		<div class="modal-content">
        		<div class="modal-header">
            		<button type="button" class="close" 
               			data-dismiss="modal" aria-hidden="true">
                  		&times;
            		</button>
		            <h4 class="modal-title" id="myModalLabel">
		              	审核详情
		            </h4>
         		</div>
         		<div class="modal-body">
         			<div class="control-group">
	                     <div class="controls1" id="reviewRemarks">
	                     </div>
	                </div>
	                <div class="control-group">
	                     <div class="controls1" id="reviewTime">
	                     </div>
	                </div>
         		</div>
      		</div><!-- /.modal-content -->
      		<div class="modal-footer">
      			<input id="btnSubmit2" class="btn btn-primary" type="button" value="关 闭" data-dismiss="modal" aria-hidden="true"/>
      		</div>	
		</div><!-- /.modal -->
	</div>		
</body>
</html>