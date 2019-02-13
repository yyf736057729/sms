<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>通道签名管理</title>
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
							html += '<option value="'+data.id+'">'+data.gatewayName+'</option>';
			            });
						gatewaySelect.append(html);
						gatewaySelect.select2();
					}
				});
			})
		}
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/sms/jmsgGatewaySign/">信息列表</a></li>
		<li class="active"><a href="${ctx}/sms/jmsgGatewaySign/config?id=${jmsgGatewaySign.id}">信息<shiro:hasPermission name="sms:jmsgGatewaySign:edit">${not empty jmsgGatewaySign.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="sms:jmsgGatewaySign:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="jmsgGatewaySign" action="${ctx}/sms/jmsgGatewaySign/batchSave" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>		
		<div class="control-group">
			<label class="control-label">用户：</label>
			<div class="controls">
				<sys:treeselect id="user" name="user.id" value="${jmsgGatewaySign.user.id}" labelName="user.name" labelValue="${jmsgGatewaySign.user.name}"
					title="用户" url="/sys/office/treeData?type=3" cssClass="required" allowClear="true" notAllowSelectParent="true" disabled="${not empty jmsgGatewaySign.id?'disabled':''}"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		
		<table id="contentTable" class="table table-striped table-bordered table-condensed">
			<thead>
				<tr>
					<th>通道协议</th>
					<th>通道</th>
					<th>签名</th>
					<th>接入号</th>
					<th>扩展号</th>
					<th>备注</th>
					<shiro:hasPermission name="sms:jmsgGatewaySign:edit"><th>操作</th></shiro:hasPermission>
				</tr>
			</thead>
			<tbody id="gatewaySigns">
				<tr>
					<input id="gatewaySignList0_id" name="gatewaySignList[0].id" type="hidden" value=""/>
					<input id="gatewaySignList0_delFlag" name="gatewaySignList[0].delFlag" type="hidden" value="0"/>
					<td>
						<select id="type" htmlEscape="false" class="input-small">
							<option value="">请选择</option>
							<option value="CMPP">移动CMPP</option>
							<option value="SGIP">联通SGIP</option>
							<option value="SMGP3">电信SMGP</option>
							<option value="HTTP">HTTP</option>
						</select>
					</td>
					<td>
						<select id="gatewaySignList0_gatewayId" name="gatewaySignList[0].gatewayId" class="input-xlarge ">
							<option value="">请选择</option>
						</select>
					</td>
					<td>
						<input id="gatewaySignList0_sign" name="gatewaySignList[0].sign" htmlEscape="false" maxlength="20" class="input-small "/>
					</td>
					<td>
						<input id="gatewaySignList0_spNumber" name="gatewaySignList[0].spNumber" readonly="readonly" htmlEscape="false" maxlength="20" class="input-small "/>
					</td>
					<td>
						<input id="gatewaySignList0_extNumber" name="gatewaySignList[0].extNumber" htmlEscape="false" maxlength="20" class="input-small "/>
					</td>
					<td>
						<%-- <form:textarea path="note" htmlEscape="false" rows="3" class="input-large" id="note"/> --%>
						<input id="gatewaySignList0_note" name="gatewaySignList[0].note" htmlEscape="false" maxlength="100" class="input-large"/>
					</td>
					<shiro:hasPermission name="sms:jmsgGatewaySign:edit"><td>
						<a href="#" onclick="delRow(this, '#gatewaySignList0')">删除</a>
					</td></shiro:hasPermission>
				</tr>
				<tr>
					<input id="gatewaySignList1_id" name="gatewaySignList[1].id" type="hidden" value=""/>
					<input id="gatewaySignList1_delFlag" name="gatewaySignList[1].delFlag" type="hidden" value="0"/>
					<td>
						<select id="type" htmlEscape="false" class="input-small ">
							<option value="">请选择</option>
							<option value="CMPP">移动CMPP</option>
							<option value="SGIP">联通SGIP</option>
							<option value="SMGP3">电信SMGP</option>
							<option value="HTTP">HTTP</option>
						</select>
					</td>
					<td>
						<select id="gatewaySignList1_gatewayId" name="gatewaySignList[1].gatewayId" class="input-xlarge ">
							<option value="">请选择</option>
						</select>
					</td>
					<td>
						<input id="gatewaySignList1_sign" name="gatewaySignList[1].sign" htmlEscape="false" maxlength="20" class="input-small "/>
					</td>
					<td>
						<input id="gatewaySignList1_spNumber" name="gatewaySignList[1].spNumber" readonly="readonly" htmlEscape="false" maxlength="20" class="input-small"/>
					</td>
					<td>
						<input id="gatewaySignList1_extNumber" name="gatewaySignList[1].extNumber" htmlEscape="false" maxlength="20" class="input-small "/>
					</td>
					<td>
						<%-- <form:textarea path="note" htmlEscape="false" rows="3" class="input-large" id="note"/> --%>
						<input id="gatewaySignList1_note" name="gatewaySignList[1].note" htmlEscape="false" maxlength="100" class="input-large"/>
					</td>
					<shiro:hasPermission name="sms:jmsgGatewaySign:edit"><td>
						<a href="#" onclick="delRow(this, '#gatewaySignList1')">删除</a>
					</td></shiro:hasPermission>
				</tr>
				<tr>
					<input id="gatewaySignList2_id" name="gatewaySignList[2].id" type="hidden" value=""/>
					<input id="gatewaySignList2_delFlag" name="gatewaySignList[2].delFlag" type="hidden" value="0"/>
					<td>
						<select id="type" htmlEscape="false" class="input-small ">
							<option value="">请选择</option>
							<option value="CMPP">移动CMPP</option>
							<option value="SGIP">联通SGIP</option>
							<option value="SMGP3">电信SMGP</option>
							<option value="HTTP">HTTP</option>
						</select>
					</td>
					<td>
						<select id="gatewaySignList2_gatewayId" name="gatewaySignList[2].gatewayId" class="input-xlarge ">
							<option value="">请选择</option>
						</select>
					</td>
					<td>
						<input id="gatewaySignList2_sign" name="gatewaySignList[2].sign" htmlEscape="false" maxlength="20" class="input-small "/>
					</td>
					<td>
						<input id="gatewaySignList2_spNumber" name="gatewaySignList[2].spNumber" readonly="readonly" htmlEscape="false" maxlength="20" class="input-small "/>
					</td>
					<td>
						<input id="gatewaySignList2_extNumber" name="gatewaySignList[2].extNumber" htmlEscape="false" maxlength="20" class="input-small "/>
					</td>
					<td>
						<%-- <form:textarea path="note" htmlEscape="false" rows="3" class="input-large" id="note"/> --%>
						<input id="gatewaySignList2_note" name="gatewaySignList[2].note" htmlEscape="false" maxlength="100" class="input-large"/>
					</td>
					<shiro:hasPermission name="sms:jmsgGatewaySign:edit"><td>
						<a href="#" onclick="delRow(this, '#gatewaySignList2')">删除</a>
					</td></shiro:hasPermission>
				</tr>
				<tr>
					<input id="gatewaySignList3_id" name="gatewaySignList[3].id" type="hidden" value=""/>
					<input id="gatewaySignList3_delFlag" name="gatewaySignList[3].delFlag" type="hidden" value="0"/>
					<td>
						<select id="type" htmlEscape="false" class="input-small ">
							<option value="">请选择</option>
							<option value="CMPP">移动CMPP</option>
							<option value="SGIP">联通SGIP</option>
							<option value="SMGP3">电信SMGP</option>
							<option value="HTTP">HTTP</option>
						</select>
					</td>
					<td>
						<select id="gatewaySignList3_gatewayId" name="gatewaySignList[3].gatewayId" class="input-xlarge ">
							<option value="">请选择</option>
						</select>
					</td>
					<td>
						<input id="gatewaySignList3_sign" name="gatewaySignList[3].sign" htmlEscape="false" maxlength="20" class="input-small "/>
					</td>
					<td>
						<input id="gatewaySignList3_spNumber" name="gatewaySignList[3].spNumber" readonly="readonly" htmlEscape="false" maxlength="20" class="input-small "/>
					</td>
					<td>
						<input id="gatewaySignList3_extNumber" name="gatewaySignList[3].extNumber" htmlEscape="false" maxlength="20" class="input-small "/>
					</td>
					<td>
						<%-- <form:textarea path="note" htmlEscape="false" rows="3" class="input-large" id="note"/> --%>
						<input id="gatewaySignList3_note" name="gatewaySignList[3].note" htmlEscape="false" maxlength="100" class="input-large"/>
					</td>
					<shiro:hasPermission name="sms:jmsgGatewaySign:edit"><td>
						<a href="#" onclick="delRow(this, '#gatewaySignList3')">删除</a>
					</td></shiro:hasPermission>
				</tr>
				<tr>
					<input id="gatewaySignList4_id" name="gatewaySignList[4].id" type="hidden" value=""/>
					<input id="gatewaySignList4_delFlag" name="gatewaySignList[4].delFlag" type="hidden" value="0"/>
					<td>
						<select id="type" htmlEscape="false" class="input-small ">
							<option value="">请选择</option>
							<option value="CMPP">移动CMPP</option>
							<option value="SGIP">联通SGIP</option>
							<option value="SMGP3">电信SMGP</option>
							<option value="HTTP">HTTP</option>
						</select>
					</td>
					<td>
						<select id="gatewaySignList4_gatewayId" name="gatewaySignList[4].gatewayId" class="input-xlarge ">
							<option value="">请选择</option>
						</select>
					</td>
					<td>
						<input id="gatewaySignList4_sign" name="gatewaySignList[4].sign" htmlEscape="false" maxlength="20" class="input-small "/>
					</td>
					<td>
						<input id="gatewaySignList4_spNumber" name="gatewaySignList[4].spNumber" readonly="readonly" htmlEscape="false" maxlength="20" class="input-small "/>
					</td>
					<td>
						<input id="gatewaySignList4_extNumber" name="gatewaySignList[4].extNumber" htmlEscape="false" maxlength="20" class="input-small "/>
					</td>
					<td>
						<%-- <form:textarea path="note" htmlEscape="false" rows="3" class="input-large" id="note"/> --%>
						<input id="gatewaySignList4_note" name="gatewaySignList[4].note" htmlEscape="false" maxlength="100" class="input-large"/>
					</td>
					<shiro:hasPermission name="sms:jmsgGatewaySign:edit"><td>
						<a href="#" onclick="delRow(this, '#gatewaySignList4')">删除</a>
					</td></shiro:hasPermission>
				</tr>
				<tr>
					<input id="gatewaySignList5_id" name="gatewaySignList[5].id" type="hidden" value=""/>
					<input id="gatewaySignList5_delFlag" name="gatewaySignList[5].delFlag" type="hidden" value="0"/>
					<td>
						<select id="type" htmlEscape="false" class="input-small ">
							<option value="">请选择</option>
							<option value="CMPP">移动CMPP</option>
							<option value="SGIP">联通SGIP</option>
							<option value="SMGP3">电信SMGP</option>
							<option value="HTTP">HTTP</option>
						</select>
					</td>
					<td>
						<select id="gatewaySignList5_gatewayId" name="gatewaySignList[5].gatewayId" class="input-xlarge ">
							<option value="">请选择</option>
						</select>
					</td>
					<td>
						<input id="gatewaySignList5_sign" name="gatewaySignList[5].sign" htmlEscape="false" maxlength="20" class="input-small "/>
					</td>
					<td>
						<input id="gatewaySignList5_spNumber" name="gatewaySignList[5].spNumber" readonly="readonly" htmlEscape="false" maxlength="20" class="input-small "/>
					</td>
					<td>
						<input id="gatewaySignList5_extNumber" name="gatewaySignList[5].extNumber" htmlEscape="false" maxlength="20" class="input-small "/>
					</td>
					<td>
						<%-- <form:textarea path="note" htmlEscape="false" rows="3" class="input-large" id="note"/> --%>
						<input id="gatewaySignList5_note" name="gatewaySignList[5].note" htmlEscape="false" maxlength="100" class="input-large"/>
					</td>
					<shiro:hasPermission name="sms:jmsgGatewaySign:edit"><td>
						<a href="#" onclick="delRow(this, '#gatewaySignList5')">删除</a>
					</td></shiro:hasPermission>
				</tr>
				<tr>
					<input id="gatewaySignList6_id" name="gatewaySignList[6].id" type="hidden" value=""/>
					<input id="gatewaySignList6_delFlag" name="gatewaySignList[6].delFlag" type="hidden" value="0"/>
					<td>
						<select id="type" htmlEscape="false" class="input-small ">
							<option value="">请选择</option>
							<option value="CMPP">移动CMPP</option>
							<option value="SGIP">联通SGIP</option>
							<option value="SMGP3">电信SMGP</option>
							<option value="HTTP">HTTP</option>
						</select>
					</td>
					<td>
						<select id="gatewaySignList6_gatewayId" name="gatewaySignList[6].gatewayId" class="input-xlarge ">
							<option value="">请选择</option>
						</select>
					</td>
					<td>
						<input id="gatewaySignList6_sign" name="gatewaySignList[6].sign" htmlEscape="false" maxlength="20" class="input-small "/>
					</td>
					<td>
						<input id="gatewaySignList6_spNumber" name="gatewaySignList[6].spNumber" readonly="readonly" htmlEscape="false" maxlength="20" class="input-small "/>
					</td>
					<td>
						<input id="gatewaySignList6_extNumber" name="gatewaySignList[6].extNumber" htmlEscape="false" maxlength="20" class="input-small "/>
					</td>
					<td>
						<%-- <form:textarea path="note" htmlEscape="false" rows="3" class="input-large" id="note"/> --%>
						<input id="gatewaySignList6_note" name="gatewaySignList[6].note" htmlEscape="false" maxlength="100" class="input-large"/>
					</td>
					<shiro:hasPermission name="sms:jmsgGatewaySign:edit"><td>
						<a href="#" onclick="delRow(this, '#gatewaySignList6')">删除</a>
					</td></shiro:hasPermission>
				</tr>
				<tr>
					<input id="gatewaySignList7_id" name="gatewaySignList[7].id" type="hidden" value=""/>
					<input id="gatewaySignList7_delFlag" name="gatewaySignList[7].delFlag" type="hidden" value="0"/>
					<td>
						<select id="type" htmlEscape="false" class="input-small ">
							<option value="">请选择</option>
							<option value="CMPP">移动CMPP</option>
							<option value="SGIP">联通SGIP</option>
							<option value="SMGP3">电信SMGP</option>
							<option value="HTTP">HTTP</option>
						</select>
					</td>
					<td>
						<select id="gatewaySignList7_gatewayId" name="gatewaySignList[7].gatewayId" class="input-xlarge ">
							<option value="">请选择</option>
						</select>
					</td>
					<td>
						<input id="gatewaySignList7_sign" name="gatewaySignList[7].sign" htmlEscape="false" maxlength="20" class="input-small "/>
					</td>
					<td>
						<input id="gatewaySignList7_spNumber" name="gatewaySignList[7].spNumber" readonly="readonly" htmlEscape="false" maxlength="20" class="input-small "/>
					</td>
					<td>
						<input id="gatewaySignList7_extNumber" name="gatewaySignList[7].extNumber" htmlEscape="false" maxlength="20" class="input-small "/>
					</td>
					<td>
						<%-- <form:textarea path="note" htmlEscape="false" rows="3" class="input-large" id="note"/> --%>
						<input id="gatewaySignList7_note" name="gatewaySignList[7].note" htmlEscape="false" maxlength="100" class="input-large"/>
					</td>
					<shiro:hasPermission name="sms:jmsgGatewaySign:edit"><td>
						<a href="#" onclick="delRow(this, '#gatewaySignList7')">删除</a>
					</td></shiro:hasPermission>
				</tr>
				<tr>
					<input id="gatewaySignList8_id" name="gatewaySignList[8].id" type="hidden" value=""/>
					<input id="gatewaySignList8_delFlag" name="gatewaySignList[8].delFlag" type="hidden" value="0"/>
					<td>
						<select id="type" htmlEscape="false" class="input-small ">
							<option value="">请选择</option>
							<option value="CMPP">移动CMPP</option>
							<option value="SGIP">联通SGIP</option>
							<option value="SMGP3">电信SMGP</option>
							<option value="HTTP">HTTP</option>
						</select>
					</td>
					<td>
						<select id="gatewaySignList8_gatewayId" name="gatewaySignList[8].gatewayId" class="input-xlarge ">
							<option value="">请选择</option>
						</select>
					</td>
					<td>
						<input id="gatewaySignList8_sign" name="gatewaySignList[8].sign" htmlEscape="false" maxlength="20" class="input-small "/>
					</td>
					<td>
						<input id="gatewaySignList8_spNumber" name="gatewaySignList[8].spNumber" readonly="readonly" htmlEscape="false" maxlength="20" class="input-small "/>
					</td>
					<td>
						<input id="gatewaySignList8_extNumber" name="gatewaySignList[8].extNumber" htmlEscape="false" maxlength="20" class="input-small "/>
					</td>
					<td>
						<%-- <form:textarea path="note" htmlEscape="false" rows="3" class="input-large" id="note"/> --%>
						<input id="gatewaySignList8_note" name="gatewaySignList[8].note" htmlEscape="false" maxlength="100" class="input-large"/>
					</td>
					<shiro:hasPermission name="sms:jmsgGatewaySign:edit"><td>
						<a href="#" onclick="delRow(this, '#gatewaySignList8')">删除</a>
					</td></shiro:hasPermission>
				</tr>
				<tr>
					<input id="gatewaySignList9_id" name="gatewaySignList[9].id" type="hidden" value=""/>
					<input id="gatewaySignList9_delFlag" name="gatewaySignList[9].delFlag" type="hidden" value="0"/>
					<td>
						<select id="type" htmlEscape="false" class="input-small ">
							<option value="">请选择</option>
							<option value="CMPP">移动CMPP</option>
							<option value="SGIP">联通SGIP</option>
							<option value="SMGP3">电信SMGP</option>
							<option value="HTTP">HTTP</option>
						</select>
					</td>
					<td>
						<select id="gatewaySignList9_gatewayId" name="gatewaySignList[9].gatewayId" class="input-xlarge ">
							<option value="">请选择</option>
						</select>
					</td>
					<td>
						<input id="gatewaySignList9_sign" name="gatewaySignList[9].sign" htmlEscape="false" maxlength="20" class="input-small "/>
					</td>
					<td>
						<input id="gatewaySignList9_spNumber" name="gatewaySignList[9].spNumber" readonly="readonly" htmlEscape="false" maxlength="20" class="input-small "/>
					</td>
					<td>
						<input id="gatewaySignList9_extNumber" name="gatewaySignList[9].extNumber" htmlEscape="false" maxlength="20" class="input-small "/>
					</td>
					<td>
						<%-- <form:textarea path="note" htmlEscape="false" rows="3" class="input-large" id="note"/> --%>
						<input id="gatewaySignList9_note" name="gatewaySignList[9].note" htmlEscape="false" maxlength="100" class="input-large"/>
					</td>
					<shiro:hasPermission name="sms:jmsgGatewaySign:edit"><td>
						<a href="#" onclick="delRow(this, '#gatewaySignList9')">删除</a>
					</td></shiro:hasPermission>
				</tr>
			</tbody>
			<tfoot>
				<tr><td colspan="7">
				<a href="javascript:" onclick="addRow('#gatewaySigns', rowIdx, gatewaySignsTpl);rowIdx = rowIdx + 1;" class="btn">新增</a>
				</td></tr>
			</tfoot>
		</table>
		
		<script type="text/template" id="gatewaySignsTpl">
			<tr id="gatewaySigns{{idx}}">
				<input id="gatewaySignList{{idx}}_id" name="gatewaySignList[{{idx}}].id" type="hidden" value=""/>
				<input id="gatewaySignList{{idx}}_delFlag" name="gatewaySignList[{{idx}}].delFlag" type="hidden" value="0"/>
				<td>
					<select id="type" htmlEscape="false" class="input-small ">
						<option value="">请选择</option>
						<option value="CMPP">移动CMPP</option>
						<option value="SGIP">联通SGIP</option>
						<option value="SMGP3">电信SMGP</option>
						<option value="HTTP">HTTP</option>
					</select>
				</td>
				<td>
					<select id="gatewaySignList{{idx}}_gatewayId" name="gatewaySignList[{{idx}}].gatewayId" class="input-xlarge ">
						<option value="">请选择</option>
					</select>
				</td>
				<td>
					<input id="gatewaySignList{{idx}}_sign" name="gatewaySignList[{{idx}}].sign" htmlEscape="false" maxlength="20" class="input-small "/>
				</td>
				<td>
					<input id="gatewaySignList{{idx}}_spNumber" name="gatewaySignList[{{idx}}].spNumber" readonly="readonly" htmlEscape="false" maxlength="20" class="input-small "/>
				</td>
				<td>
					<input id="gatewaySignList{{idx}}_extNumber" name="gatewaySignList[{{idx}}].extNumber" htmlEscape="false" maxlength="20" class="input-small "/>
				</td>
				<td>
					<%-- <form:textarea path="note" htmlEscape="false" rows="3" class="input-large" id="note"/> --%>
					<input id="gatewaySignList{{idx}}_note" name="gatewaySignList[{{idx}}].note" htmlEscape="false" maxlength="100" class="input-large"/>
				</td>
				<shiro:hasPermission name="sms:jmsgGatewaySign:edit"><td>
					<a href="#" onclick="delRow(this, '#gatewaySignList{{idx}}')">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</script>
		<script type="text/javascript">
		var rowIdx = 10, gatewaySignsTpl = $("#gatewaySignsTpl").html().replace(/(\/\/\<!\-\-)|(\/\/\-\->)/g,"");
		</script>
		
		<div class="form-actions">
			<shiro:hasPermission name="sms:jmsgGatewaySign:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>