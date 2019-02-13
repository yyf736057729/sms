<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>excel模板</title>
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
			 /*  在textarea处插入文本--Start */
	        (function($) {
	            $.fn
	                .extend({
	                    insertContent: function(myValue, t) {
	                        var $t = $(this)[0];
	                        if (document.selection) { // ie    
	                            this.focus();
	                            var sel = document.selection.createRange();
	                            sel.text = myValue;
	                            this.focus();
	                            sel.moveStart('character', -l);
	                            var wee = sel.text.length;
	                            if (arguments.length == 2) {
	                                var l = $t.value.length;
	                                sel.moveEnd("character", wee + t);
	                                t <= 0 ? sel.moveStart("character", wee - 2 * t
	                                        - myValue.length) : sel.moveStart(
	                                        "character", wee - t - myValue.length);
	                                sel.select();
	                            }
	                        } else if ($t.selectionStart
	                                || $t.selectionStart == '0') {
	                            var startPos = $t.selectionStart;
	                            var endPos = $t.selectionEnd;
	                            var scrollTop = $t.scrollTop;
	                            $t.value = $t.value.substring(0, startPos)
	                                    + myValue
	                                    + $t.value.substring(endPos,
	                                            $t.value.length);
	                            this.focus();
	                            $t.selectionStart = startPos + myValue.length;
	                            $t.selectionEnd = startPos + myValue.length;
	                            $t.scrollTop = scrollTop;
	                            if (arguments.length == 2) {
	                                $t.setSelectionRange(startPos - t,
	                                        $t.selectionEnd + t);
	                                this.focus();
	                            }
	                        } else {
	                            this.value += myValue;
	                            this.focus();
	                        }
	                    }
	                })
	        })(jQuery);
	        /* 在textarea处插入文本--Ending */
			
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
		
		function onShow(){
			$("#smsContent").val('');
			$("#phoneFile").val(null);
			$('#modal').modal('show');
		}
		
		function onOK(){
			var smsContent = $("#smsContent").val();
			if(smsContent == null || smsContent ==''){
				alertx("请输入短信内容");
				return;
			}
			
			
			loading('正在导入，请稍等...');
			$.ajax({  
		        type: "post",  
		        url: "${ctx}/sms/jmsgSmsTask/importDotTmpl",
		        dataType: 'json',  
		        data: new FormData($('#modalForm')[0]),
		        processData: false,
		        contentType: false,
				success : function(result) {
					closeLoading();
					if(result.code == 1){
						$('#modal').modal('hide');
						var tmp = '';
						var content = eval(result.content);
						$.each(content,function(i,item){
							tmp += item.phone + '	' + item.smsContent + '\r\n';
			            });
						$("#phones").val(tmp);
						$("#id").val(result.taskId);
						countPhone(result.count);
						alertx("导入信息成功");
					}else if(result.code == -5){
						alertx("只支持导入xls,xlsx文件");
					}else if(result.code == -1){
						alertx("导入excel信息失败，列长度不一致");
					}else if(result.code == -2){
						alertx("导入excel信息失败，指定的号码列超出范围");
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
		}
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/sms/jmsgSmsTask/smsDotTmplInit">excel模板</a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="jmsgSmsTask" action="${ctx}/sms/jmsgSmsTask/dotTmplSave" method="post" class="form-horizontal" enctype="multipart/form-data">
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
			<label class="control-label"></label>
			<div class="controls">
				<input id="btnSubmit" class="btn btn-primary" type="button" value="EXCEL导入" onclick="javascript:onShow();"/>
			</div>
		</div>
				
			
		<div class="form-actions">
			<shiro:hasPermission name="sms:jmsgSmsTask:edit"><c:if test="${usedFlag eq  1}"><input id="btnSubmit" class="btn btn-primary" type="button" value="提 交" onclick="onSubmit();"/>&nbsp;</c:if></shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
	
	<div class="modal fade" style="display:none;" id="modal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
		<div class="modal-dialog">
      		<div class="modal-content">
        		<div class="modal-header">
            		<button type="button" class="close" 
               			data-dismiss="modal" aria-hidden="true">
                  		&times;
            		</button>
		            <h4 class="modal-title" id="myModalLabel">
		              	EXCEL导入
		            </h4>
         		</div>
         		<div class="modal-body">
         			<form name="modalForm" method="post" id="modalForm" enctype="multipart/form-data">
					<table cellSpacing="0" cellPadding="10" width="100%" border="0">
					    <tr>
					        <td height="25" width="20%" align="right" class="left">号码所在列：</td>
					        <td height="25" align="left">XLS文件中的第
					            <select name="phoneRow" id="phoneRow" style="width: 50px;">
					                <option value="0">A</option>
					                <option value="1">B</option>
					                <option value="2">C</option>
					                <option value="3">D</option>
					                <option value="4">E</option>
					                <option value="5">F</option>
					                <option value="6">G</option>
					                <option value="7">H</option>
					                <option value="8">I</option>
					                <option value="9">J</option>
					                <option value="10">K</option>
					                <option value="11">L</option>
					                <option value="12">M</option>
					                <option value="13">N</option>
					                <option value="14">O</option>
					                <option value="15">P</option>
					                <option value="16">Q</option>
					                <option value="17">R</option>
					                <option value="18">S</option>
					            </select>
					        列(<font color="red">该列内容为接收用户手机号码</font>)</td>
							</tr>
							<tr>
					        <td height="25" width="15%" align="right" class="left">短信内容：</td>
					        <td height="25" align="left">
					            <textarea name="smsContent" 
					                                style="border:1px solid #6E9FDE;width:320px; height:80px; margin-bottom:5px; margin-top: 0px;" 
					                                id="smsContent" cols="20" maxlength="400"></textarea><br />
					                                将XLS文件中的第
					            <select name="insertRow" id="insertRow" style="width:50px;">
					                <option value="[(A)]">A</option>
					                <option value="[(B)]" selected="selected">B</option>
					                <option value="[(C)]">C</option>
					                <option value="[(D)]">D</option>
					                <option value="[(E)]">E</option>
					                <option value="[(F)]">F</option>
					                <option value="[(G)]">G</option>
					                <option value="[(H)]">H</option>
					                <option value="[(I)]">I</option>
					                <option value="[(J)]">J</option>
					                <option value="[(K)]">K</option>
					                <option value="[(L)]">L</option>
					                <option value="[(M)]">M</option>
					                <option value="[(N)]">N</option>
					                <option value="[(O)]">O</option>
					                <option value="[(P)]">P</option>
					                <option value="[(Q)]">Q</option>
					                <option value="[(R)]">R</option>
					                <option value="[(S)]">S</option>
					            </select>
					        列<input type="button" name="put" class="btn btn-primary" value="插入" onclick="$('#smsContent').insertContent($('#insertRow').val()); "/>到消息内容中
					        </td>
						</tr>
					    <tr>
					        <td height="25" style="width:70px;" align="right">
								excel文件：
							</td>
							<td>
							    <input name="phoneFile" type="file" id="phoneFile" style="width:200px" />
							    &nbsp;(文件类型：XLS,XLSX)
							</td>
					    </tr>
					</table>
					</form>
         		</div>
         		<div class="modal-footer">
         			<c:if test="${usedFlag eq  1}"><input id="btnSubmit1" class="btn btn-primary" type="button" value="提 交"  onclick="javascript:onOK();"/></c:if>
      				<input id="btnSubmit2" class="btn btn-primary" type="button" value="关 闭" data-dismiss="modal" aria-hidden="true"/>
      			</div>
      		</div><!-- /.modal-content -->
		</div><!-- /.modal -->
	</div>
</body>
</html>