<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>规则管理管理</title>
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
		function showModal(ruleContent,ruleType,bz){
			$("#ruleContent").val(ruleContent);
			$("#tRuleType").val(ruleType);
			$("#smsContent").val(bz);
			$('#testModal').modal('show');
		}
		function showTestAll(){
			$("#smsContentGroup").val('');
			$('#testModalAll').modal('show');
		}
		function onTestRule(){
			var ruleContent = $("#ruleContent").val();
			var ruleType = $("#tRuleType").val();
			var smsContent = $("#smsContent").val();
			if(smsContent == null || smsContent == ''){
				alertx("请输入测试内容");
				return;
			}
			
			ruleContent = encodeURI(encodeURI(ruleContent));
			smsContent = encodeURI(encodeURI(smsContent));
			
			$.ajax({  
		        type: "post",  
		        url: "${ctx}/sms/jmsgRuleInfo/testRule?ruleContent="+ruleContent+"&ruleType="+ruleType+"&smsContent="+smsContent,
		        dataType: 'json',  
		        contentType: "application/x-www-form-urlencoded; charset=utf-8",  
				success : function(result) {
					alertx(result);
				},
				error:function(){
					alertx("系统错误");
				}
		    });
		}
		
		function onTestGroup(){
			var groupId = $("#groupId").val();
			var smsContent = $("#smsContentGroup").val();
			if(smsContent == null || smsContent == ''){
				alertx("请输入测试内容");
				return;
			}
			smsContent = encodeURI(encodeURI(smsContent));
			
			$.ajax({  
		        type: "post",  
		        url: "${ctx}/sms/jmsgRuleInfo/testGroup?groupId="+groupId+"&smsContent="+smsContent,
		        dataType: 'json',  
		        contentType: "application/x-www-form-urlencoded; charset=utf-8",  
				success : function(result) {
					alertx(result);
				},
				error:function(){
					alertx("系统错误");
				}
		    });
		}
		
		function syncRule(){
			$.ajax({  
		        type: "post",  
		        url: "${ctx}/sms/jmsgRuleInfo/syncRule",
		        dataType: 'json',  
		        contentType: "application/x-www-form-urlencoded; charset=utf-8",
				success : function(result) {
					if(result == 1){
						alertx("同步规则成功");
					}else{
						alertx("同步规则失败");
					}
					
				},
				error:function(){
					alertx("系统错误");
				}
		    });
		}
		
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/sms/jmsgRuleInfo/">信息列表</a></li>
		<shiro:hasPermission name="sms:jmsgRuleInfo:edit"><li><a href="${ctx}/sms/jmsgRuleInfo/form">信息添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="jmsgRuleInfo" action="${ctx}/sms/jmsgRuleInfo/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>规则代码：</label>
				<form:input path="ruleCode" class="input-medium" maxlength="4"/>
			</li>
			<li><label>规则名称：</label>
				<form:input path="ruleName" class="input-medium" maxlength="4"/>
			</li>			
			<li><label>规则分类：</label>
				<form:select path="ruleType" class="input-medium" htmlEscape="false" >
					<form:option value="">--请选择--</form:option>
					<form:option value="1">网址</form:option>
					<form:option value="2">电话</form:option>
					<%-- <form:option value="3">关键字</form:option> --%>
					<form:option value="4">正则式</form:option>
				</form:select>
			</li>
			<li><label>状态：</label>
				<form:select path="status" class="input-medium" htmlEscape="false" >
					<form:option value="">--请选择--</form:option>
					<form:option value="0">启用</form:option>
					<form:option value="1">禁用</form:option>
				</form:select>
			</li>
			<li><label>规则内容：</label>
				<form:input path="ruleContent" class="input-medium" maxlength="40"/>
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
			<shiro:hasPermission name="sms:jmsgRuleInfo:edit"><input id="btnSubmit" class="btn btn-primary" type="button" value="同步" onclick="javascript:syncRule();"/></shiro:hasPermission>
			<input id="testBtn" class="btn btn-primary" type="button" value="测试" onclick="javascript:showTestAll();"/>
			</li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<!-- <th>id</th> -->
				<th>规则代码</th>
				<th>规则名称</th>
				<th>规则内容</th>
				<th>规则分类</th>
				<th>检验通过</th>
				<th>状态</th>
				<th>描述</th>
				<th>创建人</th>
				<th>创建时间</th>
				<shiro:hasPermission name="sms:jmsgRuleInfo:edit"><th>操作</th></shiro:hasPermission>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="jmsgRuleInfo">
			<tr>
				<%-- <td><a href="${ctx}/sms/jmsgRuleInfo/form?id=${jmsgRuleInfo.id}">
					${jmsgRuleInfo.id}
				</a></td> --%>
				<td>
					${jmsgRuleInfo.ruleCode}
				</td>
				<td>
					${jmsgRuleInfo.ruleName}
				</td>
				<td>
					${jmsgRuleInfo.ruleContent}
				</td>
				<td>
					${jmsgRuleInfo.ruleType eq 1 ? '网址' : jmsgRuleInfo.ruleType eq 2 ? '电话' : jmsgRuleInfo.ruleType eq 3 ? '关键字' : '正则式'}
				</td>
				<td>
					${jmsgRuleInfo.ispass eq 1 ? '不通过' : '通过'}
				</td>
				<td>
					${jmsgRuleInfo.status eq 1 ? '禁用' : '启用'}
				</td>
				<td>
					${jmsgRuleInfo.description}
				</td>
				<td>
					${fns:getUserById(jmsgRuleInfo.createBy.id).name}
				</td>
				<td>
					<fmt:formatDate value="${jmsgRuleInfo.createtime}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<shiro:hasPermission name="sms:jmsgRuleInfo:edit"><td>
					<a href="javascript:showModal('${jmsgRuleInfo.ruleContent}','${jmsgRuleInfo.ruleType}','${jmsgRuleInfo.description}');">测试</a>
    				<a href="${ctx}/sms/jmsgRuleInfo/form?id=${jmsgRuleInfo.id}">修改</a>
					<a href="${ctx}/sms/jmsgRuleInfo/delete?id=${jmsgRuleInfo.id}" onclick="return confirmx('确认要删除该规则管理吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
	
	
	<div class="modal fade" style="display:none;" id="testModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel1" aria-hidden="true">
   		<div class="modal-dialog">
      		<div class="modal-content">
        		<div class="modal-header">
            		<button type="button" class="close" 
               			data-dismiss="modal" aria-hidden="true">
                  		&times;
            		</button>
		            <h4 class="modal-title" id="myModalLabel">
		              	测试内容
		            </h4>
         		</div>
         		<div class="modal-body">
         			<form id="gateWaySendForm" class="form-horizontal">
	                	<div class="control-group">
	                     	<div class="controls1">
	                     		<input type="hidden" id="ruleContent"/>
	                     		<input type="hidden" id="tRuleType"/>
	                     		<textarea style="width:498px;" name="smsContent" id="smsContent" rows="6" cols="8"></textarea>
	                     	</div>
	                 	</div>
                 	</form>
         		</div>
         		<div class="modal-footer">
         			<input id="btnSubmit1" class="btn btn-primary" type="button" value="测试" onclick="javascript:onTestRule();"/>
      				<input id="btnSubmit2" class="btn btn-primary" type="button" value="关 闭" data-dismiss="modal" aria-hidden="true"/>
         		</div>
      		</div><!-- /.modal-content -->
		</div><!-- /.modal -->
	</div>
	
	<div class="modal fade" style="display:none;" id="testModalAll" tabindex="-1" role="dialog" aria-labelledby="myModalLabel2" aria-hidden="true">
   		<div class="modal-dialog">
      		<div class="modal-content">
        		<div class="modal-header">
            		<button type="button" class="close" 
               			data-dismiss="modal" aria-hidden="true">
                  		&times;
            		</button>
		            <h4 class="modal-title" id="myModalLabel">
		              	测试
		            </h4>
         		</div>
         		<div class="modal-body">
         			<form id="gateWaySendForm1" class="form-horizontal">
	                	<div class="control-group">
	                		
	                     	<div class="controls1">测试内容：
	                     		<textarea style="width:398px;" name="smsContentGroup" id="smsContentGroup" rows="4" cols="6" htmlEscape="false"></textarea>
	                     	</div>
	                 	</div>
	                 	<div class="control-group">
	                		
	                     	<div class="controls1">规则分组：
	                     		<select name="groupId" class="input-xlarge" htmlEscape="false" id="groupId">
									<option value="">--全部--</option>
									<c:forEach var="item" items="${jmsgRuleInfo.groupList}">
										<option value="${item.id}">${item.groupName}</option>
									</c:forEach>
								</select>
	                     	</div>
	                 	</div>
                 	</form>
         		</div>
         		<div class="modal-footer">
         			<input id="btnSubmit1" class="btn btn-primary" type="button" value="测试" onclick="javascript:onTestGroup();"/>
      				<input id="btnSubmit2" class="btn btn-primary" type="button" value="关 闭" data-dismiss="modal" aria-hidden="true"/>
         		</div>
      		</div><!-- /.modal-content -->
		</div><!-- /.modal -->
	</div>	
</body>
</html>