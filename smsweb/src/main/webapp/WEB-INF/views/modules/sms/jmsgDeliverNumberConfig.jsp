<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>用户上行接入号管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			//$("#name").focus();
			$("#inputForm").validate({
				submitHandler: function(form){
					loading('正在提交，请稍等...');
					form.submit();
				},
				errorContainer: "#messageBox",
				errorPlacement: function(error, element) {
					$("#messageBox").text("输入有误，请先更正。");
					if (element.is(":checkbox")||element.is(":radio")||element.parent().is(".input-append")){
						error.appendTo(element.parent().parent());
					} else {
						error.insertAfter(element);
					}
				}
			});
			
			refreshSelect();
		});
		
		function addRow(list, idx, tpl, row){
			$(list).append(Mustache.render(tpl, {
				idx: idx, delBtn: true, row: row
			}));
			$(list+idx).find("select").each(function(){
				$(this).val($(this).attr("data-value"));
				$(this).select2();
			});
			$(list+idx).find("input[type='checkbox'], input[type='radio']").each(function(){
				var ss = $(this).attr("data-value").split(',');
				for (var i=0; i<ss.length; i++){
					if($(this).val() == ss[i]){
						$(this).attr("checked","checked");
					}
				}
			});
			
			refreshSelect();
		}
		
		function delRow(obj, prefix){
			var id = $(prefix+"_id");
			var delFlag = $(prefix+"_delFlag");
			if (id.val() == ""){
				$(obj).parent().parent().remove();
			}else if(delFlag.val() == "0"){
				delFlag.val("1");
				$(obj).html("撤销删除").attr("title", "撤销删除");
				$(obj).parent().parent().addClass("error");
			}else if(delFlag.val() == "1"){
				delFlag.val("0");
				$(obj).html("删除").attr("title", "删除");
				$(obj).parent().parent().removeClass("error");
			}
		}
		
		function refreshSelect()
		{
			$("[id$='gatewayId']").change(function() 
			{
				var curTr = $(this).parent().parent();
				$.ajax({
					url : "${ctx}/sms/jmsgGatewayInfo/getGatewayInfo?gatewayId="+$(this).children('option:selected').val(),
					dataType : "json",
					success : function(data) {
						curTr.find("input[id*='spNumber']").val(data.spNumber);
					}
				});
			})
			
			$("[id='type']").change(function()
			{
				var html = '';
				var curTr = $(this).parent().parent();
				var gatewaySelect = curTr.find("select[id$='gatewayId']");
				$.ajax({
					url : "${ctx}/sms/jmsgGatewayInfo/getGatewayInfoList?type="+$(this).children('option:selected').val(),
					dataType : "json",
					success : function(data) {
						gatewaySelect.empty();
						html += '<option value="">请选择</option>';
						$.each(data,function(i,data){
							//html += '<option value="'+data.id+'" label="'+data.gatewayName+'"/>';
							html += '<option value="'+data.id+'">'+data.gatewayName+'</option>';
			            });
						gatewaySelect.append(html);
						gatewaySelect.select2();
						/* gatewaySelect.selectpicker('refresh');
						gatewaySelect.selectpicker('render'); */
					}
				});
			})
		}
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/sms/jmsgDeliverNumber/">信息列表</a></li>
		<li class="active"><a href="${ctx}/sms/jmsgDeliverNumber/config?id=${jmsgDeliverNumber.id}">信息<shiro:hasPermission name="sms:jmsgDeliverNumber:edit">${not empty jmsgDeliverNumber.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="sms:jmsgDeliverNumber:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="jmsgDeliverNumber" action="${ctx}/sms/jmsgDeliverNumber/batchSave" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>		
		<div class="control-group">
			<label class="control-label">用户：</label>
			<div class="controls">
				<sys:treeselect id="user" name="user.id" value="${jmsgDeliverNumber.user.id}" labelName="user.name" labelValue="${jmsgDeliverNumber.user.name}"
					title="用户" url="/sys/office/treeData?type=3" cssClass="required" allowClear="true" notAllowSelectParent="true"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<table id="contentTable" class="table table-striped table-bordered table-condensed">
			<thead>
				<tr>
					<th>通道协议</th>
					<th>通道</th>
					<th>接入号</th>
					<th>扩展号</th>
					<shiro:hasPermission name="sms:jmsgGatewaySign:edit"><th>操作</th></shiro:hasPermission>
				</tr>
			</thead>
			<tbody id="deliverNumbers">
				<tr>
					<input id="deliverNumber0_id" name="deliverNumber[0].id" type="hidden" value=""/>
					<input id="deliverNumber0_delFlag" name="deliverNumber[0].delFlag" type="hidden" value="0"/>
					<td>
						<select id="type" htmlEscape="false" class="input-medium">
							<option value="">请选择</option>
							<option value="CMPP">移动CMPP</option>
							<option value="SGIP">联通SGIP</option>
							<option value="SMGP3">电信SMGP</option>
							<option value="HTTP">HTTP</option>
						</select>
					</td>
					<td>
						<select id="deliverNumber0_gatewayId" name="deliverNumber[0].gatewayId" class="input-xlarge ">
							<option value="">请选择</option>
						</select>
					</td>
					<td>
						<input id="deliverNumber0_spNumber" name="deliverNumber[0].spNumber"  maxlength="20" class="input-medium "/>
					</td>
					<td>
						<input id="deliverNumber0_extNumber" name="deliverNumber[0].extNumber" maxlength="20" class="input-medium "/>
					</td>
					<shiro:hasPermission name="sms:jmsgDeliverNumber:edit"><td>
						<a href="#" onclick="delRow(this, '#deliverNumber0')">删除</a>
					</td></shiro:hasPermission>
				</tr>
				<tr>
					<input id="deliverNumber1_id" name="deliverNumber[1].id" type="hidden" value=""/>
					<input id="deliverNumber1_delFlag" name="deliverNumber[1].delFlag" type="hidden" value="0"/>
					<td>
						<select id="type" htmlEscape="false" class="input-medium ">
							<option value="">请选择</option>
							<option value="CMPP">移动CMPP</option>
							<option value="SGIP">联通SGIP</option>
							<option value="SMGP3">电信SMGP</option>
							<option value="HTTP">HTTP</option>
						</select>
					</td>
					<td>
						<select id="deliverNumber1_gatewayId" name="deliverNumber[1].gatewayId" class="input-xlarge ">
							<option value="">请选择</option>
						</select>
					</td>
					<td>
						<input id="deliverNumber1_spNumber" name="deliverNumber[1].spNumber" maxlength="20" class="input-medium"/>
					</td>
					<td>
						<input id="deliverNumber1_extNumber" name="deliverNumber[1].extNumber" maxlength="20" class="input-medium "/>
					</td>
					<shiro:hasPermission name="sms:jmsgDeliverNumber:edit"><td>
						<a href="#" onclick="delRow(this, '#deliverNumber1')">删除</a>
					</td></shiro:hasPermission>
				</tr>
				<tr>
					<input id="deliverNumber2_id" name="deliverNumber[2].id" type="hidden" value=""/>
					<input id="deliverNumber2_delFlag" name="deliverNumber[2].delFlag" type="hidden" value="0"/>
					<td>
						<select id="type" htmlEscape="false" class="input-medium ">
							<option value="">请选择</option>
							<option value="CMPP">移动CMPP</option>
							<option value="SGIP">联通SGIP</option>
							<option value="SMGP3">电信SMGP</option>
							<option value="HTTP">HTTP</option>
						</select>
					</td>
					<td>
						<select id="deliverNumber2_gatewayId" name="deliverNumber[2].gatewayId" class="input-xlarge ">
							<option value="">请选择</option>
						</select>
					</td>
					<td>
						<input id="deliverNumber2_spNumber" name="deliverNumber[2].spNumber"  maxlength="20" class="input-medium "/>
					</td>
					<td>
						<input id="deliverNumber2_extNumber" name="deliverNumber[2].extNumber" maxlength="20" class="input-medium "/>
					</td>
					<shiro:hasPermission name="sms:jmsgDeliverNumber:edit"><td>
						<a href="#" onclick="delRow(this, '#deliverNumber2')">删除</a>
					</td></shiro:hasPermission>
				</tr>
			</tbody>
			<tfoot>
				<tr><td colspan="7">
				<a href="javascript:" onclick="addRow('#deliverNumbers', rowIdx, deliverNumberTpl);rowIdx = rowIdx + 1;" class="btn">新增</a>
				</td></tr>
			</tfoot>
		</table>
		
		<script type="text/template" id="deliverNumberTpl">
			<tr id="deliverNumbers{{idx}}">
				<input id="deliverNumber{{idx}}_id" name="deliverNumber[{{idx}}].id" type="hidden" value=""/>
				<input id="deliverNumber{{idx}}_delFlag" name="deliverNumber[{{idx}}].delFlag" type="hidden" value="0"/>
				<td>
					<select id="type" htmlEscape="false" class="input-medium ">
						<option value="">请选择</option>
						<option value="CMPP">移动CMPP</option>
						<option value="SGIP">联通SGIP</option>
						<option value="SMGP3">电信SMGP</option>
						<option value="HTTP">HTTP</option>
					</select>
				</td>
				<td>
					<select id="deliverNumber{{idx}}_gatewayId" name="deliverNumber[{{idx}}].gatewayId" class="input-xlarge ">
						<option value="">请选择</option>
					</select>
				</td>
				<td>
					<input id="deliverNumber{{idx}}_spNumber" name="deliverNumber[{{idx}}].spNumber" htmlEscape="false" maxlength="20" class="input-medium "/>
				</td>
				<td>
					<input id="deliverNumber{{idx}}_extNumber" name="deliverNumber[{{idx}}].extNumber" htmlEscape="false" maxlength="20" class="input-medium "/>
				</td>
				<shiro:hasPermission name="sms:jmsgDeliverNumber:edit"><td>
					<a href="#" onclick="delRow(this, '#deliverNumber{{idx}}')">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</script>
		<script type="text/javascript">
		var rowIdx = 3, deliverNumberTpl = $("#deliverNumberTpl").html().replace(/(\/\/\<!\-\-)|(\/\/\-\->)/g,"");
		</script>
		<div class="form-actions">
			<shiro:hasPermission name="sms:jmsgDeliverNumber:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>