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
			$("#btnExport").click(function(){
				top.$.jBox.confirm("确认要导出下行短信明细数据吗？","系统提示",function(v,h,f){
					if(v=="ok"){
						$("#searchForm").attr("action","${ctx}/sms/jmsgSmsSend/detailListReport");
						$("#searchForm").submit();
					}
				},{buttonsFocus:1});
				top.$('.jbox-body .jbox-icon').css('top','55px');
			});
			
			$("#btnPush").click(function(){
				var ids = getCheckboxValue("id");
				if(!ids){
					alertx("请选择需要推送的短信！");
				}else{
					$.ajax({  
				        type: "post",  
				        url: "${ctx}/sms/jmsgSmsSend/batchPush?ids="+ids,
				        dataType: 'json',  
				        contentType: "application/x-www-form-urlencoded; charset=utf-8",  
						success : function(result) {
							alertx(result);
							//取消全选   
							$(":checkbox").removeAttr("checked"); 
						}
				    });
				}
			});
			
			$("#btnTaskPush").click(function(){
				$.ajax({  
			        type: "post",  
			        url: "${ctx}/sms/jmsgSmsSend/taskPush",
			        data:$('#searchForm').serialize(),
			        dataType: 'json',  
			        contentType: "application/x-www-form-urlencoded; charset=utf-8",  
					success : function(result) {
						alertx(result);
						//取消全选   
						$(":checkbox").removeAttr("checked"); 
					}
			    });
			});
		});
		
		function onQuery(){
			$("#searchForm").submit();
		}
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").attr("action","${ctx}/sms/jmsgSmsSend/errorDetailList");
			$("#searchForm").submit();
        	return false;
        }
		function onView(taskid,id,createDatetime,smsType){
			var url = "${ctx}/sms/jmsgSmsSend/detailView?taskid="+taskid+"&bizid="+id+"&createDatetime="+createDatetime+"&smsType="+smsType;
			windowOpen(url,taskid,900,550);
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
		<li><a href="${ctx}/sms/jmsgSmsSend/detailInit">信息列表</a></li>
		<li class="active"><a href="${ctx}/sms/jmsgSmsSend/errorDetailInit">发送失败短信记录</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="jmsgSmsSend" action="${ctx}/sms/jmsgSmsSend/errorDetailList" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>手机号码：</label>
				<form:input path="phone" maxlength="20" class="input-medium" id="phone"/>
			</li>
			<li><label>批次号：</label>
				<form:input path="taskId" maxlength="64" class="input-medium" id="taskId"/>
			</li>
			<li><label>短信ID：</label>
				<form:input path="id" maxlength="64" class="input-medium" id="id"/>
			</li>
			<li><label>用户名称：</label>
				<sys:treeselect id="user" name="user.id" value="${jmsgSmsSend.user.id}" labelName="user.name" labelValue="${jmsgSmsSend.user.name}"
					title="用户" url="/sys/office/treeData?type=3" cssClass="input-small" allowClear="true" notAllowSelectParent="true"/>
			</li>
			<li><label>运营商：</label>
				<form:select path="phoneType" class="input-medium">
					<form:option value="" label="全部"/>
					<form:options items="${fns:getDictList('phone_type')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
			</li>
			<li><label>发送状态：</label>
				<form:select path="sendStatus" class="input-medium">
					<form:option value="F" label="全部"/>
					<form:options items="${fns:getDictList('send_error_status')}" itemLabel="label" itemValue="value"/>
				</form:select>
			</li>
			<shiro:hasPermission name="jmsg:admin">
			<li><label>通道：</label>
				<form:select path="channelCode" class="input-xlarge">
					<form:option value="" label="请选择"/>
					<form:options items="${fns:getGatewayList()}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
			</li>
			</shiro:hasPermission>		
			<li><label>日期：</label>
				<input name="createDatetimeQ" id="createDatetimeQ" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					value="<fmt:formatDate value="${jmsgSmsSend.createDatetimeQ}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});"/>
					--
				<input name="createDatetimeZ" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					value="<fmt:formatDate value="${jmsgSmsSend.createDatetimeZ}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});"/>
			</li>
			<li class="btns">
				<input id="btnSubmit" class="btn btn-primary" type="button" value="查询" onclick="return page();"/>
			</li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th><input type="checkbox" onclick="doCheckAll(this,'id', this.checked)"></th>
				<th>操作　</th>
				<th>用户ID</th>
				<th>用户名称　　</th>
				<shiro:hasPermission name="jmsg:admin">
				<th>机构　　　</th>
				</shiro:hasPermission>
				<th>提交时间　</th>
				<th>短信内容　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　</th>
				<th>计数</th>
				<th>手机号码</th>
				<th>运营商</th>
				<th>省份</th>
				<th>地区</th>
				<th>发送状态</th>
				<th>状态报告</th>
				<shiro:hasPermission name="jmsg:admin">
				<th>通道ID</th>
				<th>通道分组名称　　　　　　</th>
				<th>运营商通道名称　　　　　　</th>
				</shiro:hasPermission>
				<th>接入号</th>
				<th>短信ID</th>
				<th>批次号</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="jmsgSmsSend">
			<tr onclick="selectTr(this, '${jmsgSmsSend.id}' ,'id')">
				<td><input type="checkbox" name="id" id="${jmsgSmsSend.id}" value="${jmsgSmsSend.id}" onclick="if(this.checked){this.checked=false;}else{this.checked=true;}"></td>
				<td>
					<a href='javascript:onView("${jmsgSmsSend.taskId}","${jmsgSmsSend.id}","<fmt:formatDate value="${jmsgSmsSend.createDatetime}" pattern="yyyy-MM-dd HH:mm:ss"/>","${jmsgSmsSend.smsType}")'>查看</a>
				</td>
				<td>
					${jmsgSmsSend.user.id}
				</td>
				<td>
					${fns:getUserById(jmsgSmsSend.user.id).name}
				</td>
				<shiro:hasPermission name="jmsg:admin">
				<td>
					${fns:getUserById(jmsgSmsSend.user.id).company}
				</td>
				</shiro:hasPermission>		
				<td>
					<fmt:formatDate value="${jmsgSmsSend.sendDatetime}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>		
				<td>
					${fn:substring(jmsgSmsSend.smsContent, 0, 60)}...
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
					${fns:getDictLabel(jmsgSmsSend.sendStatus,'mms_send_status',jmsgSmsSend.sendStatus)} 
				</td>
				<td>
					<c:if test="${fns:startsWith(jmsgSmsSend.sendStatus,'F')}">
						失败
					</c:if>
					<c:if test="${!fns:startsWith(jmsgSmsSend.sendStatus,'F')}">
						<c:if test="${fns:startsWith(jmsgSmsSend.reportStatus,'P')}">
							待返回
						</c:if>
						<c:if test="${fns:startsWith(jmsgSmsSend.reportStatus,'T')}">
							成功
						</c:if>
						<c:if test="${fns:startsWith(jmsgSmsSend.reportStatus,'F')}">
							失败
						</c:if>	
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
				<td><a href="${ctx}/sms/jmsgSmsSend/detailView?taskid=${jmsgSmsSend.taskId}&bizid=${jmsgSmsSend.id}&createDatetime=<fmt:formatDate value="${jmsgSmsSend.createDatetime}" pattern="yyyy-MM-dd HH:mm:ss"/>">${jmsgSmsSend.id}</a></td>
				<td>
					${jmsgSmsSend.taskId}
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
	
	<div class="modal fade" style="display:none;" id="testModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel1" aria-hidden="true">
   		<div class="modal-dialog">
      		<div class="modal-content">
        		<div class="modal-header">
            		<button type="button" class="close" 
               			data-dismiss="modal" aria-hidden="true">
                  		&times;
            		</button>
		            <h4 class="modal-title" id="myModalLabel">
		              	测试结果
		            </h4>
         		</div>
         		<div class="modal-body">
         			<form id="gateWaySendForm" class="form-horizontal">
	                	<div class="control-group">
	                     	<div class="controls1" id="result">
	                     	</div>
	                 	</div>
                 	</form>
         		</div>
         		<div class="modal-footer">
         			<input id="btnSubmit1" class="btn btn-primary" type="button" value="获取结果" onclick="javascript:gatewayResult();"/>
      				<input id="btnSubmit2" class="btn btn-primary" type="button" value="关 闭" data-dismiss="modal" aria-hidden="true"/>
         		</div>
      		</div><!-- /.modal-content -->
		</div><!-- /.modal -->
	</div>
	
</body>
</html>