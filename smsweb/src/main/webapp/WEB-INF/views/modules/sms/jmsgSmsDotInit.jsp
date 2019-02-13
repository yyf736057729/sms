<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>普通点对点</title>
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
				loading('正在导入，请稍等...');
				$.ajax({  
			        type: "post",  
			        url: "${ctx}/sms/jmsgSmsTask/importDot",
			        dataType: 'json',  
			        data: new FormData($('#inputForm')[0]),
			        processData: false,
			        contentType: false,
					success : function(result) {
						closeLoading();
						if(result.code == 1){
							var tmp = '';
							var content = eval(result.content);
							$.each(content,function(i,item){
								tmp += item.phone + '	' + item.smsContent + '\n';
				            });
							$("#phones").val(tmp);
							$("#id").val(result.taskId);
							countPhone(result.count);
							alertx("导入信息成功");
						}else if(result.code == -5){
							alertx("只支持导入xls,xlsx文件");
						}else if(result.code == -1){
							alertx("导入excel信息失败,列长度不符");
						}else if(result.code == -9){
							alertx("导入文件过大，导入文件不能超过5M");
						}else{
							alertx("导入信息失败");
						}
					},
					error : function(){
						closeLoading();
						alertx("系统错误");
					}
			    });
		    });
			
		});
		
		function onSubmit(){
			var phones = $("#phones").val();
			if(phones==null|| phones==''){
				alertx("请导入发送内容");
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
		
		
		//计算手机号码个数
		function countPhone(count){
			$("#sendCount").val(count);
			$("#phoneNumber").text("共计号码："+count+"个");
		}
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/sms/jmsgSmsTask/smsDotInit">普通点对点</a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="jmsgSmsTask" action="${ctx}/sms/jmsgSmsTask/dotSave" method="post" class="form-horizontal" enctype="multipart/form-data">
		<input type="hidden" name="sendCount" id="sendCount"/>
		<input type="hidden" name="id" id="id"/>
		<sys:message content="${message}"/>
		<div class="control-group">
			<label class="control-label">发送时间：</label>
			<div class="controls">
				<input id="sendDatetime" name="sendDatetime" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					value="<fmt:formatDate value="${jmsgSmsTask.sendDatetime}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:true});"/>
				<span class="help-inline"><font color="red">注：发送时间为空，则为立即发送</font></span>
			</div>
		</div>	
		<div class="control-group">
			<label class="control-label">短信发送预览：</label>
			<div class="controls">
				<form:textarea path="phones" rows="8"  htmlEscape="false" class="input-xxlarge" id="phones" readonly="true"/>
				<span class="help-inline"></span>
			</div>
			<div class="controls">
				<span class="help-inline" id="phoneNumber">共计号码：0个</span>
			</div>
			</br>
		</div>
		<div class="control-group">
			<label class="control-label">EXCEL导入</label>
			<div class="controls">
				<input name="phoneFile" type="file" class="input-xlarge" id="phoneFile" >
				<span class="help-inline"><font color="red">每行一个号码，一条短信内容</font></span>
			</div>
		</div>		
		<div class="form-actions">
			<shiro:hasPermission name="sms:jmsgSmsTask:edit"><c:if test="${usedFlag eq 1 }"><input id="btnSubmit" class="btn btn-primary" type="button" value="提 交" onclick="onSubmit();"/>&nbsp;</c:if></shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>