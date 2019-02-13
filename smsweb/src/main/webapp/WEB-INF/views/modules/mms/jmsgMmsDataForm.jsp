<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>

<html>
<head>
	<title>彩信素材管理</title>
	<meta name="decorator" content="default"/>
	<link href="${pageContext.request.contextPath}/static/bootstrap-wysiwyg/prettify.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/static/bootstrap-wysiwyg/bootstrap-combined.no-icons.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/static/bootstrap-wysiwyg/bootstrap-responsive.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/static/bootstrap-wysiwyg/font-awesome.css" rel="stylesheet">
    <script src="${pageContext.request.contextPath}/static/bootstrap-wysiwyg/jquery.min.js"></script>
    <script src="${pageContext.request.contextPath}/static/bootstrap-wysiwyg/jquery.hotkeys.js"></script>
    <script src="${pageContext.request.contextPath}/static/bootstrap-wysiwyg/bootstrap.min.js"></script>
    <script src="${pageContext.request.contextPath}/static/bootstrap-wysiwyg/prettify.js"></script>
    <link href="${pageContext.request.contextPath}/static/bootstrap-wysiwyg/index.css" rel="stylesheet">
    <script src="${pageContext.request.contextPath}/static/bootstrap-wysiwyg/bootstrap-wysiwyg.js"></script>
	<script src="${pageContext.request.contextPath}/static/bootstrap-wysiwyg/syExtJquery.js?version=20131115" type="text/javascript" charset="utf-8"></script>
	<script src="${pageContext.request.contextPath}/static/bootstrap-wysiwyg/syExtJavascript.js?version=20131115" type="text/javascript" charset="utf-8"></script>
    <script src="${pageContext.request.contextPath}/static/bootstrap-wysiwyg/base64.js"></script>
	<script type="text/javascript">
		$(document).ready(function() {
			
			function initToolbarBootstrapBindings() {
			      var fonts = ['Serif', 'Sans', 'Arial', 'Arial Black', 'Courier', 
			            'Courier New', 'Comic Sans MS', 'Helvetica', 'Impact', 'Lucida Grande', 'Lucida Sans', 'Tahoma', 'Times',
			            'Times New Roman', 'Verdana'],
			            fontTarget = $('[title=Font]').siblings('.dropdown-menu');
			      $.each(fonts, function (idx, fontName) {
			          fontTarget.append($('<li><a data-edit="fontName ' + fontName +'" style="font-family:\''+ fontName +'\'">'+fontName + '</a></li>'));
			      });
			      $('a[title]').tooltip({container:'body'});
			    	$('.dropdown-menu input').click(function() {return false;})
					    .change(function () {$(this).parent('.dropdown-menu').siblings('.dropdown-toggle').dropdown('toggle');})
			        .keydown('esc', function () {this.value='';$(this).change();});

			      $('[data-role=magic-overlay]').each(function () { 
			        var overlay = $(this), target = $(overlay.data('target')); 
			        overlay.css('opacity', 0).css('position', 'absolute').offset(target.offset()).width(target.outerWidth()).height(target.outerHeight());
			      });
			      if ("onwebkitspeechchange"  in document.createElement("input")) {
			        var editorOffset = $('#editor').offset();
			        $('#voiceBtn').css('position','absolute').offset({top: editorOffset.top, left: editorOffset.left+$('#editor').innerWidth()-35});
			      } else {
			        $('#voiceBtn').hide();
			      }
				};
				
				function showErrorAlert (reason, detail) {
					var msg='';
					if (reason==='unsupported-file-type') { msg = "Unsupported format " +detail; }
					else {
						console.log("error uploading file", reason, detail);
					}
					$('<div class="alert"> <button type="button" class="close" data-dismiss="alert">&times;</button>'+ 
					 '<strong>File upload error</strong> '+msg+' </div>').prependTo('#alerts');
				};
				
			    initToolbarBootstrapBindings();
				$('#editor').wysiwyg({ fileUploadError: showErrorAlert} );
			    window.prettyPrint && prettyPrint();
			    var id = $("#id").val();
			    if(id !='' && id != null){
					$.ajax({
						url : "${ctx}/mms/jmsgMmsData/getContent?id="+id,
						dataType : "json",
						success : function(data) {
							$('#editor').html(data);
						}
					});
			    }
			    
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
		});
		
	  	function checktitle(){
	  		var usernameVal = $('#title').val();
	  		if(usernameVal == '' || usernameVal == null)return false;
	  	  	usernameVal = usernameVal.replace(/\\/g,"|");
	  	  	usernameVal = usernameVal.replace(/\r/g,"");
	  	  	usernameVal = usernameVal.replace(/\n/g,"");
	  	  	usernameVal = usernameVal.replace(/\t/g,"");
	  	  	usernameVal = usernameVal.replace(/\b/g,"");
	  	  	usernameVal = usernameVal.replace(/\f/g,"");
	  	  	usernameVal = usernameVal.replace(/\'/g,"“");
	  	  	usernameVal = usernameVal.replace(/>/g,"》");
	  	  	usernameVal = usernameVal.replace(/</g,"《");
	  		usernameVal = usernameVal.replace(/\·/g,"~");
	  	  	$('#title').val(usernameVal.replace(/\"/g,"“"));
	  	  	return true;
	  	}
	  	
	  	function onSave(){
	  		if(!checktitle()){
	  			alertx("请输入标题");
	  		}
			var str = $('#editor').html();
			$('#content').val(str);
			$("#inputForm").submit();
	  	}

	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/mms/jmsgMmsData/">彩信素材列表</a></li>
		<li class="active"><a href="${ctx}/mms/jmsgMmsData/form?id=${jmsgMmsData.id}">彩信素材<shiro:hasPermission name="mms:jmsgMmsData:edit">${not empty jmsgMmsData.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="mms:jmsgMmsData:edit">查看</shiro:lacksPermission></a></li>
	</ul>
	<form:form id="inputForm" modelAttribute="jmsgMmsData" action="${ctx}/mms/jmsgMmsData/save" method="post" class="form-horizontal">
		<form:hidden path="id" id="id"/>
		<sys:message content="${message}"/>		
		<div class="control-group">
			<label class="control-label">彩信主题：</label>
			<div class="controls">
				<form:input path="mmsTitle" htmlEscape="false" maxlength="20" class="input-xlarge required" id="title"/>
				<span class="help-inline"><font color="red">*最大支持20个字</font></span>
			</div>
		</div>
		<div class="controls">
		<font color="red">
	          注： 文本文字请勿直接拷贝，只支持从写字板复制过来粘贴，如从其他地方拷贝需要先用写字板保存后使用。
	    </font>
	 	<div class="btn-toolbar" data-role="editor-toolbar" data-target="#editor" >
	      <div class="btn-group">
	        <a class="btn" title="插入图片" id="pictureBtn"><i class="fa fa-picture-o"></i></a>
	        <input type="file" data-role="magic-overlay" data-target="#pictureBtn" data-edit="insertImage" />
	      </div>
	    </div>
		
	    <div id="editor" style="height:650px;width: 88%;">
	      
	    </div>
	    <div>
	    1、彩信文件大小建议在100K以下<br>
	    2、文字请直接在框内编辑，图片请在框内选中插入位置，然后点击图标上传
	    </div>
	    </div>
	    <input type="hidden" id="content" name="content"/>
	   		<div class="control-group">
			<label class="control-label">备注：</label>
			<div class="controls">
				<form:input path="remark" htmlEscape="false" maxlength="15" class="input-xlarge" />
				<span class="help-inline">最大支持15个字</span>
			</div>
		</div> 		
    	<div class="form-actions">
			<c:if test="${jmsgMmsData.viewFlag ne 1}"><shiro:hasPermission name="mms:jmsgMmsData:edit"><input id="btnSubmit" class="btn btn-primary" type="button" value="保 存" onclick="javascript:onSave();"/>&nbsp;</shiro:hasPermission></c:if>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>