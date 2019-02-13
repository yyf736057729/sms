<%@ page contentType="text/html;charset=UTF-8" %>


<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <title></title>
   <!-- <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <meta name="keywords" content="opensource rich wysiwyg text editor jquery bootstrap execCommand html5" />
    <meta name="description" content="This tiny jQuery Bootstrap WYSIWYG plugin turns any DIV into a HTML5 rich text editor" />
    -->



<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">







<script type="text/javascript">
var sy = sy || {};
sy.contextPath = '';
sy.basePath = 'http://10.252.249.166:8281';
sy.version = '20131115';
sy.pixel_0 = '/style/images/pixel_0.gif';//0像素的背景，一般用于占位


</script>

   <link href="http://120.26.120.46:8888/securityJsp/bootstrap/external/google-code-prettify/prettify.css" rel="stylesheet">
    <link href="http://120.26.120.46:8888/securityJsp/bootstrap/css/bootstrap-combined.no-icons.min.css" rel="stylesheet">
    <link href="http://120.26.120.46:8888/securityJsp/bootstrap/css/bootstrap-responsive.min.css" rel="stylesheet">
    <link href="http://120.26.120.46:8888/securityJsp/bootstrap/css/font-awesome.css" rel="stylesheet">
    <script src="http://120.26.120.46:8888/securityJsp/bootstrap/jquery.min.js"></script>
    <script src="http://120.26.120.46:8888/securityJsp/bootstrap/external/jquery.hotkeys.js"></script>
    <script src="http://120.26.120.46:8888/securityJsp/bootstrap/js/bootstrap.min.js"></script>
    <script src="http://120.26.120.46:8888/securityJsp/bootstrap/external/google-code-prettify/prettify.js"></script>
    <link href="http://120.26.120.46:8888/securityJsp/bootstrap/index.css" rel="stylesheet">
    <script src="http://120.26.120.46:8888/securityJsp/bootstrap/bootstrap-wysiwyg.js"></script>


<script src="http://120.26.120.46:8888/jslib/syExtJquery.js?version=20131115" type="text/javascript" charset="utf-8"></script>




<script src="http://120.26.120.46:8888/jslib/syExtJavascript.js?version=20131115" type="text/javascript" charset="utf-8"></script>

 
    <script src="http://120.26.120.46:8888/securityJsp/bootstrap/js/base64.js"></script>
    <script type="text/javascript">
    
    var submitNow = function($dialog, $grid, $pjq) {
  	  //alert(sy.contextPath);
  	  if(!checktitle())
  	  	  return;
  	  
  		var url;
  		url = sy.contextPath + '/addmms';
  		//alert(url);
  			//url ='http://localhost:8080/caixinweb/addmmd';
  			//alert(222);
  			var str = $('#editor').html();
  			//alert($('#editor').html());
  			$('#mms').val(str);
  			//alert(str);
  			//return false;
  		$.post(url, sy.serializeObject($('form')), function(result) {
  			parent.sy.progressBar('close');//关闭上传进度条
  			
  			if (result.result==1) {
  				$pjq.messager.alert('提示', "添加成功", 'info');
  				$grid.datagrid('load');
  				$dialog.dialog('destroy');
  			} else {
  				$pjq.messager.alert('提示', "添加失败 "+result.msg, 'error');
  			}
  		}, 'json');
  	};
  	var submitForm = function($dialog, $grid, $pjq) {
  			
  				submitNow($dialog, $grid, $pjq);
  			

  	};
  	$(function() {
  		
  		var id = $('#id').val();
  		if(id!=''){
  			//alert(333);
  			var url;

			url = sy.contextPath + '/mmscontent?id='+id;
			//url = sy.contextPath + '/json/error.json';
			 $.post(url, function(result){ 
				// alert(result.content);
				 $('#title').val(result.title);//result.content
				 $('#editor').html(Base64.decode(result.content));
				  },'json');
  		}
  		
  	});

  	function checktitle() 
  	{
  	  var usernameVal = $('#title').val();
  	  //if (!usernameVal.match( /[^\a-\z\A-\Z0-9\u4E00-\u9FA5\.]/g)) {
  		//$pjq.messager.alert('提示','必须由汉字、英文字母组成', 'error');
  	   // return false;
  	  //}
  	  //var str = "[@/'\"#$%&^*]+";
  	  //var reg = new RegExp(str);
  	  //alert(usernameVal);
  	  //if(reg.test(usernameVal))
  	  //{
  	//	$pjq.messager.alert('提示','标题中不能包含 双引号 ', 'error');
  	  //  return false; 
  	  //}
  	  usernameVal = usernameVal.replace(/\\/g,"|");
  	  usernameVal = usernameVal.replace(/\r/g,"");
  	  usernameVal = usernameVal.replace(/\n/g,"");
  	  usernameVal = usernameVal.replace(/\t/g,"");
  	  usernameVal = usernameVal.replace(/\b/g,"");
  	  usernameVal = usernameVal.replace(/\f/g,"");
  	  usernameVal = usernameVal.replace(/\'/g,"“");
  	  usernameVal = usernameVal.replace(/>/g,"》");
  	  usernameVal = usernameVal.replace(/</g,"《");
  	  $('#title').val(usernameVal.replace(/\"/g,"“"));
  	//if(usernameVal.indexOf("\r")>=0||usernameVal.indexOf("\n")>=0){
  	//	$pjq.messager.alert('提示','标题内容不能 换行 ', 'error');
  	 //   return false; 
  	  //}
  	  return true;
  	}

  	
    </script>
  </head>
  
  <body>

<table border="1" width="400" >
<form method="post" class="form">
<div class="container">
<input type="hidden" id="id" name="id" value=""/>
<br/><br/>
标题：<input type="text" id="title" name="title" >
    <div class="btn-toolbar" data-role="editor-toolbar" data-target="#editor" >
      <div class="btn-group">
        <a class="btn" title="插入图片" id="pictureBtn"><i class="fa fa-picture-o"></i></a>
        <input type="file" data-role="magic-overlay" data-target="#pictureBtn" data-edit="insertImage" />
      </div>
      <!-- 
      <div class="btn-group">
        <a class="btn" data-edit="save" title="保存?" onclick="show();"><i class="fa fa-save"></i></a>
      </div>
       -->
      <!-- <input type="text" data-edit="inserttext" id="voiceBtn"> -->
    </div>
	
    <div id="editor" style="height:800">
      
    </div>
    <div>
    1、彩信文件大小建议在100K以下<br>
    2、文字请直接在框内编辑，图片请在框内选中插入位置，然后点击图标上传
    
    </div>
    <input type="hidden" id="mms" name="mms">
  </div>
  </form>
</table>

<script>
function show(){
alert($('#editor').html());
 var str =$('#editor').html();
$('#textareaValidate').val(str);
}
  $(function(){
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
  });
  
 
</script>
<!-- <div id="fb-root"></div>
<textarea cols="75" rows="5" id="textareaValidate" style="display: none;"></textarea>
-->
</html>
