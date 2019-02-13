<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>短信批量发送</title>
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
			
			$("#phoneFile").change(function(){
				$.ajax({  
			        type: "post",  
			        url: "${ctx}/sms/jmsgSmsTask/upload",
			        dataType: 'json',  
			        data: new FormData($('#inputForm')[0]),
			        processData: false,
			        contentType: false,
					success : function(result) {
						var tmp = '';
						$.each(result,function(i,item){
							tmp += item + '\n';
			            });
						$("#phones").val(tmp);
						countPhone();
					}
			    });
		    });
			
			$('input[id^="sign"]').change(function(){
				if ($(this).is(':checked'))
				{
					$("#content").val($(this).val() + $("#content").val());
				}
				else
				{
					$("#content").val($("#content").val().replace($(this).val(),''));
				}
				
				countNum();
			});
			
			$('input[id^="unsubType"]').change(function(){
				if ($(this).is(':checked'))
				{
					$("#content").val($("#content").val() + $(this).val());
				}
				else
				{
					$("#content").val($("#content").val().replace($(this).val(),''));
				}
				
				countNum();
			});
			
			countNum();
		});
		
		function onSubmit(){
			
			if(!checkSign($("#content").val())){
				alertx("签名错误,签名必须在首位且签名长度1个字符及以上");
				return;
			}
			
			var phones = $("#phones").val();
			var phoneFile = $("#phoneFile").val();
			if((phones == null || phones =='')&&(phoneFile==null|| phoneFile=='')){
				alertx("请输入发送号码或者选择号码文件");
				return;
			}
			
			var sendDatetime = $("#sendDatetime").val();
			 var d1 = new Date(sendDatetime.replace(/\-/g, "\/")); 
			 var d2 = new Date();
			 if((d1.getTime() - d2.getTime()) / (1000 * 60 * 60 * 24) >= 16){
				alertx("发送时间有误,短信发送只支持15天内");
				return;
			 }
			$("#btnSubmit").attr({"disabled":"disabled"});
			$("#inputForm").submit();
			
		}
		
		function checkSign(content){
			var reg = new RegExp("^【(.*?)】");
			var array = content.match(reg);
			if(array == null)return false;
			if(array.length == 2){
				if(array[1].length >=1){
					return true;
				}
			}
			return false;
		}
		
		function countNum()
		{
			var content = $("#content").val();
			if (content)
			{
				if (content.length > 0 && content.length <= 70)
				{
					$("#contentLabel").text("共 "+content.length+" 字，1 条短信");
				}
				else
				{
					$("#contentLabel").text("共 "+content.length+" 字，共 "+ Math.ceil(content.length/67) +" 条短信");
				}
			}
			else
			{
				$("#contentLabel").text("共 0 字，0 条短信");
			}
		}
		
		function checkKeywords()
		{
			var content = $("#content").val();
			if (content)
			{
				$.ajax({  
			        type: "post",  
			        url: "${ctx}/sms/jmsgSmsTask/checkKeywords/?content="+encodeURI(encodeURI(content)),
			        dataType: 'json',  
			        contentType: "application/x-www-form-urlencoded; charset=utf-8",  
					success : function(result) {
						
						if (null != result && result != '')
						{
							alertx("输入发送内容包含关键字："+result+"  请确认修改！");
						}
						else
						{
							alertx("输入发送内容不包含关键字，可以发送。");
						}
					}
			    });
			}
		}
		
		function checkError()
		{
			var content = $("#phones").val();
			if (content)
			{
				$.ajax({  
			        type: "post",  
			        url: "${ctx}/sms/jmsgSmsTask/filterPhonesError",
			        dataType: 'json',  
			        data:$('#inputForm').serialize(),
			        contentType: "application/x-www-form-urlencoded; charset=utf-8",  
					success : function(result) {
						var tmp = '';
						$.each(result,function(i,item){
							tmp += item + '\n';
			            });
						$("#phones").val(tmp);
						countPhone();
					}
			    });
			}
			else
			{
				alertx("请输入发送号码！");
			}
		}
		
		function checkSame()
		{
			var content = $("#phones").val();
			if (content)
			{
				$.ajax({  
			        type: "post",  
			        url: "${ctx}/sms/jmsgSmsTask/filterPhonesSame",
			        dataType: 'json',  
			        data:$('#inputForm').serialize(),
			        contentType: "application/x-www-form-urlencoded; charset=utf-8",  
					success : function(result) {
						var tmp = '';
						$.each(result,function(i,item){
							tmp += item + '\n';
			            });
						$("#phones").val(tmp);
						countPhone();
					}
			    });
			}
			else
			{
				alertx("请输入发送号码！");
			}
		}
		
		function selectAdd()
		{
			$('#addressModal').modal('show');
		}
		function groupTreeselectCallBack(v, h, f){
			$.ajax({  
		        type: "post",  
		        url: "${ctx}/sms/jmsgAddresslistInfo/findList?groupId="+$("#groupId").val(),
		        dataType: 'json',  
				success : function(result) {
					var tmp = '';
					$.each(result,function(i,item){
						tmp += item.phone + '\n';
		            });
					$("#phones").val(tmp);
					countPhone();
				}
		    });
			$('#addressModal').modal('hide');
		}
		
		//计算手机号码个数
		function countPhone(){
			var count = 1;
			var phones = $("#phones").val();
			var index = phones.split("\n");
			if (index.length > 0){
				count = index.length;	
			}
			if($.trim(index[index.length-1]) == '')count = count-1;
			if($.trim(phones) == '')count = 0;
			
			$("#phoneNumber").text("共"+count+"个号码");
		}
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/sms/jmsgSmsTask/smsGroupInit">短信发送</a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="jmsgSmsTask" action="${ctx}/sms/jmsgSmsTask/save" method="post" class="form-horizontal" enctype="multipart/form-data">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>		
		<div class="control-group">
			<label class="control-label">发送内容：</label>
			<div class="controls">
				<form:textarea path="content" htmlEscape="false" rows="4" class="input-xxlarge required" oninput="countNum();" onpropertychange="countNum();"/>
				<span class="help-inline"><font color="red">*注：签名必须在首位,签名长度1个字符及以上  <br/>&nbsp;例：</font><font color="black">【泛圣科技】您的验证码为：731848，有效期为30分钟。</font> </span>
			</div>
			<div class="controls">
				<span class="help-inline" id="contentLabel">共 0 字，0 条短信</span><span class="help-inline" style="color: red">说明：计数仅供参考，以实际发送计费为准</span>
			</div>
			<div class="controls">
				使用签名：<form:checkboxes path="sign" items="${jmsgSmsTask.userSign}" itemLabel="label" itemValue="value" htmlEscape="false"/>
			</div>
			<div class="controls">
				使用退订语：<form:checkboxes path="unsubType" items="${fns:getDictList('unsub_type')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
			</div>
			<div class="controls">
				<input id="btnCancel" class="btn btn-primary" type="button" value="检测关键字" onclick="checkKeywords();"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">发送号码：</label>
			<div class="controls">
				<form:textarea path="phones" rows="4"  htmlEscape="false" class="input-xxlarge" id="phones" oninput="countPhone();" onpropertychange="countPhone();" onchange="countPhone();"/>
				<span class="help-inline"><font color="red">注：内容为1行1个号码</font> </span>
			</div>
			<div class="controls">
				<span class="help-inline" id="phoneNumber">共0个号码</span>
			</div>
			</br>
			<div class="controls">
				<input id="btnCancel" class="btn btn-primary" type="button" value="过滤错号" onclick="checkError();"/>
				<input id="btnCancel" class="btn btn-primary" type="button" value="过滤重号" onclick="checkSame();"/>
				<input id="btnCancel" class="btn btn-primary" type="button" value="通讯录" onclick="selectAdd();"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">号码文件</label>
			<div class="controls">
				<input name="phoneFile" type="file" class="input-xlarge" id="phoneFile">
				<span class="help-inline"><font color="red">注：号码文件为txt文件，内容为1行1个号码</font> </span>
			</div>
		</div>		
		<div class="control-group">
			<label class="control-label">发送时间：</label>
			<div class="controls">
				<input id="sendDatetime" name="sendDatetime" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					value="<fmt:formatDate value="${jmsgSmsTask.sendDatetime}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:true});"/>
				<span class="help-inline"><font color="red">注：开始时间为空，则为立即发送</font></span>
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="sms:jmsgSmsTask:edit">
				<c:if test="${usedFlag eq 1 }">
					<input id="btnSubmit" class="btn btn-primary" type="button" value="提 交" onclick="onSubmit();"/>&nbsp;
				</c:if></shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
	
	<div class="modal fade" id="addressModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true" style="display: none;top: 35%;">
	   <div class="modal-dialog">
	      <div class="modal-content">
	         <div class="modal-header">
	            <button type="button" class="close" 
	               data-dismiss="modal" aria-hidden="true">
	                  &times;
	            </button>
	            <h4 class="modal-title" id="myModalLabel">
	              	选择通讯录
	            </h4>
	         </div>
	         <div class="modal-body">
         		 <form id="gateWaySendForm" class="form-horizontal">
	         		 <div class="control-group">
	                     <label class="control-label">通讯录</label>
	                     <div class="controls">
	                         <sys:treeselect id="group" name="group.id" value="${jmsgAddresslistInfo.group.id}" labelName="group.name" labelValue="${jmsgAddresslistInfo.group.name}"
								title="群组" url="/sms/jmsgAddresslistGroup/treeData" cssClass="" allowClear="true"/>
	                     </div>
	                     </br>
	                 </div>
                 </form>
	         </div>
	      </div><!-- /.modal-content -->
		</div><!-- /.modal -->
	</div>
</body>
</html>