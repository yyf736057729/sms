<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>通道信息管理</title>
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
		
		function showTestModal(gateWayId, spNumber){
			$('#gatewayId').val(gateWayId);
			$('#sendId').val(spNumber);
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
		        url: "${ctx}/sms/jmsgGatewayInfo/send",
		        //url: "${ctx}/sms/jmsgGatewayInfo/send?sendId="+sendId+"&gatewayId="+gatewayId+"&recvId="+recvId+"&content="+content,
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
		
		function gateWayOpen(id) {
			top.$.jBox.confirm("确认要开启通道吗？","系统提示",function(v,h,f){
				if(v=="ok"){
					$("#searchForm").attr("action","${ctx}/sms/jmsgGatewayInfo/open?gatewayId="+id);
					$("#searchForm").submit();
				}
			},{buttonsFocus:1});
			top.$('.jbox-body .jbox-icon').css('top','55px');
			/**$.ajax({  
		        type: "post",  
		        url: "${ctx}/sms/jmsgGatewayInfo/open?gatewayId="+id,  
		        dataType: 'json',  
		        contentType: "application/x-www-form-urlencoded; charset=utf-8",  
		        success: function(result) {
		        	alert(result.errorMsg);
		        }  
		    });**/
		}
		
		function gateWayClose(id) {
			top.$.jBox.confirm("确认要关闭通道吗？","系统提示",function(v,h,f){
				if(v=="ok"){
					$("#searchForm").attr("action","${ctx}/sms/jmsgGatewayInfo/close?gatewayId="+id);
					$("#searchForm").submit();
				}
			},{buttonsFocus:1});
			top.$('.jbox-body .jbox-icon').css('top','55px');
			/**$.ajax({  
		        type: "post",  
		        url: "${ctx}/sms/jmsgGatewayInfo/close?gatewayId="+id,  
		        dataType: 'json',  
		        contentType: "application/x-www-form-urlencoded; charset=utf-8",  
		        success: function(result) {
		        	alert(result.errorMsg);
		        }  
		    });**/
		}

        function gateWayLink(id) {
		    var url = "link?id="+id;
            windowOpen(url,'链路', 800, 600);
        }
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/sms/jmsgGatewayInfo/">信息列表</a></li>
		<shiro:hasPermission name="sms:jmsgGatewayInfo:edit"><li><a href="${ctx}/sms/jmsgGatewayInfo/form">信息添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="jmsgGatewayInfo" action="${ctx}/sms/jmsgGatewayInfo/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<%-- <li><label>通道名字：</label>
				<form:input path="gatewayName" htmlEscape="false" maxlength="36" class="input-medium"/>
			</li> --%>
			<li><label>通道名称：</label>
				<form:select path="gatewayName" class="input-xlarge">
					<form:option value="" label="全部"/>
					<form:options items="${fns:getGatewayList()}" itemLabel="label" itemValue="label" htmlEscape="false"/>
				</form:select>
			</li>
			<li><label>通道状态：</label>
				<!--<form:input path="gatewayState" htmlEscape="false" maxlength="1" class="input-medium"/>-->
				<form:select path="status" htmlEscape="false" class="input-medium ">
					<form:option value="">全部</form:option>
					<form:option value="1">启用</form:option>
					<form:option value="0">禁用</form:option>
				</form:select>
			</li>
			<li><label>通道协议：</label>
				<form:select path="type" htmlEscape="false" class="input-medium ">
					<form:option value="">全部</form:option>
					<form:option value="CMPP">移动CMPP20</form:option>
					<form:option value="CMPP30">移动CMPP30</form:option>
					<form:option value="SGIP">联通SGIP</form:option>
					<form:option value="SMGP3">电信SMGP</form:option>
					<form:option value="HTTP">HTTP</form:option>
				</form:select>
			</li>
			<li>
				<label>运营商</label>
				<form:select path="phoneType" class="input-medium">
					<form:option value="" label="全部"/>
					<form:options items="${fns:getDictList('phone_type')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
<!-- 			 <input id="btnRef" class="btn btn-primary" type="button" value="刷新缓存" onclick="javascript:windowOpen('http://114.55.90.98:8080/refCache.jsp?t=gateway','group',900,550);"/> 
 -->			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<shiro:hasPermission name="sms:jmsgGatewayInfo:edit"><th>操作　　　　　</th></shiro:hasPermission>
				<th>通道ID</th>
				<th>通道主状态</th>
				<th>通道名称　　　　　　　　　　</th>
				<th>通道协议</th>
				<th>用户名</th>
				<th>网关状态</th>
				<th>接入号</th>
				<!-- <th>服务器IP</th>
				<th>服务器端口</th>
				<th>密码</th>
				<th>协议版本号</th> 
				<th>应用端口</th>-->
				<th>运营商</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="jmsgGatewayInfo">
			<tr>
				<shiro:hasPermission name="sms:jmsgGatewayInfo:edit"><td>
					<a href="javascript:showTestModal('${jmsgGatewayInfo.id}','${jmsgGatewayInfo.spNumber}')">测试</a>

					<c:if test="${jmsgGatewayInfo.gatewayState eq 1}">
						<a href="javascript:gateWayClose('${jmsgGatewayInfo.id}')">停止</a>
					</c:if>
					<c:if test="${jmsgGatewayInfo.gatewayState eq 0}">
						<a href="javascript:gateWayOpen('${jmsgGatewayInfo.id}')">运行</a>
					</c:if>
					<a href="${ctx}/sms/jmsgGatewayInfo/form?id=${jmsgGatewayInfo.id}">修改</a>

					<a href="javascript:gateWayLink('${jmsgGatewayInfo.id}')">链路</a>
				</td></shiro:hasPermission>
				<td>
					${jmsgGatewayInfo.id}
				</td>
				<td>
					${jmsgGatewayInfo.status eq 1 ? '启用' : '禁用'}
				</td>
				<td><a href="${ctx}/sms/jmsgGatewayInfo/form?id=${jmsgGatewayInfo.id}">
					${jmsgGatewayInfo.gatewayName}
				</a></td>
				<td>
					${jmsgGatewayInfo.type}
				</td>
				<td>
					${jmsgGatewayInfo.sourceAddr}
				</td>
				<td>
					${jmsgGatewayInfo.gatewayState eq 1 ? '<label style="color: green;">运行</label>' : '<label style="color: red;">停止</label>'}
				</td>
				<td>
					${jmsgGatewayInfo.spNumber}
				</td>
				<td>
					${fns:getDictLabel(jmsgGatewayInfo.phoneType, 'phone_type', '')}
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
              通道测试
            </h4>
         </div>
         <div class="modal-body">
         		 <form id="gateWaySendForm" class="form-horizontal">
	         		 <input type="hidden" name="gatewayId" id="gatewayId" />
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
	                         <textarea name="content" id="content" rows="6" cols="8">【泛圣科技】您的验证码为：731848，有效期为30分钟。</textarea>
	                     </div>
	                 </div>
	                 <div class="control-group">
	                     <label class="control-label" name="smsType" >短信类型</label>
	                     <div class="controls">
	                         <select id="smsType" name="smsType" onchange="showWap()">
								<option value="sms">SMS</option>
								<option value="wap">WAP</option>
								<option value="mms">MMS</option>
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
