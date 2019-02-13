<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>通道签名管理</title>
	<meta name="decorator" content="default"/>
	<script src="${ctxStatic}/docheck/docheck.js" type="text/javascript"></script>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#btnImport").click(function(){
				$.jBox($("#importBox").html(), {title:"导入数据", buttons:{"关闭":true}, 
					bottomText:"导入文件不能超过5M，仅允许导入“xls”或“xlsx”格式文件！"});
			});
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
						$("#ids").val(ids);
						$("#deleteForm").submit();
					}
				},{buttonsFocus:1});
				top.$('.jbox-body .jbox-icon').css('top','55px');
			}
		}
		
		function sigleDelete(id, userId, gatewayId, sign)
		{
			sign = encodeURI(encodeURI(sign));
			top.$.jBox.confirm("确认要删除该通道签名吗？","系统提示",function(v,h,f){
				if(v=="ok"){
					$("#searchForm").attr("action","${ctx}/sms/jmsgGatewaySign/delete?id="+id+"&userId="+userId+"&tmpSign="+sign+"&tmpGatewayId="+gatewayId);
					$("#searchForm").submit();
				}
			},{buttonsFocus:1});
			top.$('.jbox-body .jbox-icon').css('top','55px');
		}
		
		
		function showTestModal(gateWayId, spNumber,sign,userId){
			$('.modal-body').find('input[id="gatewayId"]').val(gateWayId);
			$('.modal-body').find('input[id="userId"]').val(userId);
			$('#sendId').val(spNumber);
			$('#content').val("【"+sign+"】您的验证码为：609628，有效期为30分钟！");
			$('#gatewaySendTest').modal('show');
		}
		
		function showWap()
		{
			 if ($('#smsType').val() == 1)
			 {
				 $('#wapUrlDiv').hide();
			 }
			 else
			 {
			 	$('#wapUrlDiv').show();
			 }
		}
		
		function sendTest(){
			var gatewayId = $('#gatewayId').val();
			var sendId = $('#sendId').val();
			var recvId = $('#recvId').val();
			var content = $('#content').val();
			var data = $('#gateWaySendForm').serialize();
			$.ajax({  
		        type: "post",  
		        url: "${ctx}/sms/jmsgGatewaySign/send",
		        data: data,  
		        dataType: 'json',  
		        contentType: "application/x-www-form-urlencoded; charset=utf-8",  
				success : function(result) {
					$('#gatewaySendTest').modal('hide');
					if(result == null || result == ""){
						alertx("通道测试失败");
					}else{
						alertx(result);
					}
				},
				error : function(){
					alertx("通道测试失败");
				}
		    });
		}
	</script>
</head>
<body>
	<div id="deleteDiv" class="hide">
		<form id="deleteForm" action="${ctx}/sms/jmsgGatewaySign/batchDelete" method="post">
			<input type="hidden" id="ids" name="ids">
		</form>
	</div>
	<div id="importBox" class="hide">
		<form id="importForm" action="${ctx}/sms/jmsgGatewaySign/import" method="post" enctype="multipart/form-data"
			class="form-search" style="padding-left:20px;text-align:center;" onsubmit="loading('正在导入，请稍等...');"><br/>
			<input id="uploadFile" name="file" type="file" style="width:330px"/><br/><br/>　
			<input id="btnImportSubmit" class="btn btn-primary" type="submit" value="   导    入   "/>
			&nbsp;&nbsp;
			<a href="${ctx}/sms/jmsgGatewaySign/import/template">下载模板</a>
		</form>
	</div>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/sms/jmsgGatewaySign/">信息列表</a></li>
		<shiro:hasPermission name="sms:jmsgGatewaySign:edit">
			<%-- <li><a href="${ctx}/sms/jmsgGatewaySign/form">信息添加</a></li> --%>
			<li><a href="${ctx}/sms/jmsgGatewaySign/config">信息添加</a></li>
		</shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="jmsgGatewaySign" action="${ctx}/sms/jmsgGatewaySign/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="100"/>
		<ul class="ul-form">
			<li><label>通道：</label>
				<form:select path="gatewayId" class="input-large">
					<form:option value="" label="全部"/>
					<form:options items="${fns:getGatewayList()}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
			</li>
			<li><label>用户ID：</label>
				<sys:treeselect id="user" name="user.id" value="${jmsgGatewaySign.user.id}" labelName="user.name" labelValue="${jmsgGatewaySign.user.name}"
					title="用户" url="/sys/office/treeData?type=3" cssClass="input-small" allowClear="true" notAllowSelectParent="true"/>
			</li>
			<li><label>签名：</label>
				<form:input path="sign" htmlEscape="false" maxlength="20" class="input-medium"/>
			</li>
			<li><label>扩展号：</label>
				<form:input path="spNumber" htmlEscape="false" maxlength="20" class="input-medium"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
			<input id="btnImport" class="btn btn-primary" type="button" value="导入"/>
			<input id="btnDelete" class="btn btn-primary" type="button" value="批量删除" onclick="javascript:onDelete()"/>
			<!-- <input id="btnRef" class="btn btn-primary" type="button" value="刷新缓存" onclick="javascript:windowOpen('http://114.55.90.98:8080/refCache.jsp?t=sign','group',900,550);"/> --> 
			</li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th><input type="checkbox" onclick="doCheckAll(this,'id', this.checked)"></th>
				<shiro:hasPermission name="sms:jmsgGatewaySign:edit"><th>操作　　　　　　</th></shiro:hasPermission>
				<th>用户ID</th>
				<th>用户账号</th>
				<th>通道代码</th>
				<th>通道名称　　　　　　　　　　</th>
				<th>签名　　　　　　　　</th>
				<th>接入号　　</th>
				<th>扩展号</th>
				<th>创建时间　　　　　　</th>
				<th>备注</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="jmsgGatewaySign">
			<tr onclick="selectTr(this, '${jmsgGatewaySign.id}' ,'id')">
				<td>
					<input type="checkbox" name="id" id="${jmsgGatewaySign.id}" value="${jmsgGatewaySign.id}_${jmsgGatewaySign.user.id}_${jmsgGatewaySign.gatewayId}_${jmsgGatewaySign.sign}" 
					onclick="if(this.checked){this.checked=false;}else{this.checked=true;}">
				</td>
				<shiro:hasPermission name="sms:jmsgGatewaySign:edit"><td>
					<a href="javascript:showTestModal('${jmsgGatewaySign.gatewayId}','${fns:getGatewayInfo(jmsgGatewaySign.gatewayId).spNumber}${jmsgGatewaySign.spNumber}','${jmsgGatewaySign.sign}','${jmsgGatewaySign.user.id}')">测试</a> |
					<a href="${ctx}/sms/jmsgGatewaySign/form?id=${jmsgGatewaySign.id}">修改</a> |
    				<!-- <a href="${ctx}/sms/jmsgGatewaySign/form?id=${jmsgGatewaySign.id}">修改</a> 
					<a href="${ctx}/sms/jmsgGatewaySign/delete?id=${jmsgGatewaySign.id}&gatewayId=${jmsgGatewaySign.gatewayId}&sign=${jmsgGatewaySign.sign}" 
					onclick="return confirmx('确认要删除该通道签名吗？', this.href)">删除</a>-->
					<a href="#" 
					onclick='sigleDelete("${jmsgGatewaySign.id}","${jmsgGatewaySign.user.id}","${jmsgGatewaySign.gatewayId}","${jmsgGatewaySign.sign}")'>删除</a>
				</td></shiro:hasPermission>
				<td>
					${jmsgGatewaySign.user.id}
				</td>
				<td>
					${jmsgGatewaySign.user.name}
				</td>
				<td>
					${jmsgGatewaySign.gatewayId}
				</td>
				<td>
					${jmsgGatewaySign.gatewayName}
				</td>
				<td>
					${jmsgGatewaySign.sign}
				</td>
				<td>
					${fns:getGatewayInfo(jmsgGatewaySign.gatewayId).spNumber}${jmsgGatewaySign.spNumber}
				</td>
				<td>
					${jmsgGatewaySign.spNumber}
				</td>
				<td>
					<fmt:formatDate value="${jmsgGatewaySign.createTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
					${jmsgGatewaySign.note}
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
	
	<div class="modal fade" id="gatewaySendTest" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true" style="display: none;">
	   <div class="modal-dialog">
	      <div class="modal-content">
	         <div class="modal-header">
	            <button type="button" class="close" 
	               data-dismiss="modal" aria-hidden="true">
	                  &times;
	            </button>
	            <h4 class="modal-title" id="myModalLabel">
	              	签名测试
	            </h4>
	         </div>
	         <div class="modal-body">
	         		 <form id="gateWaySendForm" class="form-horizontal">
		         		 <input type="hidden" name="gatewayId" id="gatewayId" />
		         		 <input type="hidden" name="userId" id="userId" />
		         		 <div class="control-group">
		                     <label class="control-label" name="tdSpNumber" >接入号</label>
		                     <div class="controls">
		                         <input type="text" name="tdSpNumber" id="sendId"/>
		                     </div>
		                 </div>
		                 <div class="control-group">
		                     <label class="control-label" name="recvId" >手机号码</label>
		                     <div class="controls">
		                         <input type="text" name="recvId" id="recvId"/>
		                     </div>
		                 </div>
		                 <div class="control-group">
		                     <label class="control-label" name="content" >短信内容</label>
		                     <div class="controls">
		                         <textarea name="content" id="content" rows="6" cols="8"></textarea>
		                     </div>
		                 </div>
		                 <div class="control-group" style="display: none;">
		                     <label class="control-label" name="smsType" >短信类型</label>
		                     <div class="controls">
		                         <select id="smsType" name="smsType" onchange="showWap()">
									<option value="1">SMS</option>
									<option value="3">WAP</option>
									<option value="4">MMS</option>
								</select>
		                     </div>
		                 </div>
		                 <div class="control-group" id="wapUrlDiv" style="display: none;">
		                     <label class="control-label" name="wapUrl" >WAP地址</label>
		                     <div class="controls">
		                         <input type="text" name="wapUrl" id="wapUrl"/>
		                     </div>
		                 </div>
	                 </form>
	         </div>
	         <div class="modal-footer">
	            <button type="button" class="btn btn-default" onclick="sendTest()">提交测试</button>
	         </div>
	      </div><!-- /.modal-content -->
		</div><!-- /.modal -->
	</div>
</body>
</html>