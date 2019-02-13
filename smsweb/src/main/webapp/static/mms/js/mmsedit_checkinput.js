/**
 * 此文件用于验证用户输入公共方法
 * 方法说明:
 * checkNumber(content) ##判断是否数值
 * checkNum()           ##文本框只允许输入数字
 * checkAllRow(type)    ##全选
 * insertNewRow()       ##新增彩信帧
 * deleteRow(type)      ##删除
 * getLen(s)			##计算字符串大小
**/
  
     //判断是否数值  
 function checkNumber(content)       
 {     
 var NUM = $(content).value;
 
  var i,j,strTemp;       
  strTemp="0123456789";       
  if ( NUM.length==0)    
  {   
  	$(content).focus();
  	return false;      
  }
  for (i=0;i<NUM.length;i++)  
  {       
   j=strTemp.indexOf(NUM.charAt(i));       
   if (j==-1)       
   {        
    $(content).focus();
    return false;       
   }  
  }     
  return true;       
 } 
 
 // 文本框只允许输入数字
function checkNum(obj){
	var pattern = /^\d{0,4}$/;
	for(var i=1;i<obj.value.length+1;i++){
		if(!pattern.test(obj.value.substr(0,i))){
			alert('只能输入不超过4位的数字');
			obj.value = obj.value.substr(0,i-1);
		} 
	}
}
 
 //type|0全不选|1全选|2反选|
 function checkAllRow(type){
	 var _array = document.getElementsByName("dtRowID");
	 if(type==1){
		 for (var i=0;i<_array.length;i++){
			  var e = _array[i];
			  e.checked =true; 
		 } 
	 }else if(type==2){
	 	 for (var i=0;i<_array.length;i++){
			  var e = _array[i];
			  if(e.checked){
			     e.checked =false;
			  }else{
			     e.checked =true;
			  } 
		 }
	 }else if(type==0){
	 	for (var i=0;i<_array.length;i++){
			  var e = _array[i];
			  e.checked =false; 
		 } 
	 }
 }

function showContentList(objName){
	if(document.getElementById('MM_MMSLIB_CHARGE_FRAME_DATA').style.display==''){
		document.getElementById('MM_MMSLIB_CHARGE_FRAME_DATA').style.display= 'none';
	}else{
		document.getElementById('MM_MMSLIB_CHARGE_FRAME_DATA').style.display= ''; 
	}
}

function getLen(s){
	var   _len   =   0; 
	if (s == null) { 
	    return   0; 
	} 
	if (s.length != 0){ 
		for (var i=0;i <s.length;i++){ 
               if   ((/[^\x00-\xff]/).test(s.charAt(i))) 
               _len   +=   3; 
               else 
               _len++   ; 
        } 
	} 
	return   _len;
}

function getSizeHtml(){
	var rows = document.getElementsByName("dtRowID");
	var txtvalue;
	var totallen=0;//文本容量
	var size=0;
	var photosize;
	var musicsize;
	for(var i=1;i<=rows.length;i++){
		
		photosize=document.getElementById('photosize'+rows[i-1].value).value;
		if(photosize.length>0){
			size+=parseInt(photosize);
		}
		
//		musicsize=document.getElementById('musicsize'+rows[i-1].value).value;
//		if(musicsize.length>0){
//			size+=parseInt(musicsize);
//		}
		
		txtvalue=document.getElementById('txtcontent'+rows[i-1].value).value;
		if(txtvalue!=null){
			totallen+=getLen(txtvalue);
			
		}
	}
		var curSize = totallen /1024+size;
		curSize = curSize.toFixed(2);
		if(curSize>100){
			alert("对不起，可编辑文字已满！");
			return ;
		}
		document.getElementById('mmssize').value=curSize;
		var leavSize = 100 - curSize;
		document.getElementById("MessageObject_SIZE").innerHTML = '当前:' + curSize + 'K 剩余可编辑:' + leavSize.toFixed(2) + 'K';
}

function judgeSize(size,obj){
	var curSize = document.getElementById('mmssize').value;
	if(parseFloat(curSize)+parseInt(size)-parseInt(document.getElementById(obj).value)>100){
		alert("对不起，上传的文件不能大于规定可编辑数，请删除相关的文件！");
		return false;
	}else{
		return true;
	}
}

function setParentSize(){
	var rows = document.getElementsByName("dtRowID");
	var size=0;
	var txtvalue;
	var totallen=0;
	var photosize;
	var musicsize;
	for(var i=1;i<=rows.length;i++){
		photosize=document.getElementById('photosize'+rows[i-1].value).value;
		if(photosize.length>0){
			size+=parseInt(photosize);
		}
//		musicsize=document.getElementById('musicsize'+rows[i-1].value).value;
//		if(musicsize.length>0){
//			size+=parseInt(musicsize);
//		}
		txtvalue=document.getElementById('txtcontent'+rows[i-1].value).value;
		if(txtvalue!=null){
			totallen+=getLen(txtvalue);
		}
	}
		var curSize = totallen /1024+size;
		curSize = curSize.toFixed(2);
		document.getElementById('mmssize').value=curSize;
		var leavSize = 100 - curSize;
		document.getElementById("MessageObject_SIZE").innerHTML = '当前:' + curSize + 'K 剩余可编辑:' + leavSize.toFixed(2) + 'K';
}

//新增一行
function insertNewRow() { 
	 var rows = document.getElementsByName("dtRowID");
	 var rowId=rows.length+1;
	 var rowNum;
	 if(rows.length!=0){
	     	rowNum=parseInt(rows[rows.length-1].value)+1;
	 }else{
	     	rowNum=rowId;
	 }
	 var newTr = document.createElement("tr"); 
	 newTr.align = 'center';
	 document.getElementById('MM_MMSLIB_CHARGE_FRAME_DATA').appendChild(newTr);
	 var td0 = document.createElement("td"); 
	 var td1 = document.createElement("td"); 
	 var td2 = document.createElement("td"); 
	 newTr.appendChild(td0);
	 newTr.appendChild(td1);
	 newTr.appendChild(td2);
	 td0.width=16;
	 td0.innerHTML = '<input name="dtRowID" type="checkbox" class="noborder" value="'+rowNum+'"/>';
	 td1.width=20;
	 td1.id='rowNo'+rowNum;
     td1.innerHTML = '<input type="hidden" name="sorts" value="'+rowId+'"/>'+rowId; 
	 td2.innerHTML = '<table id="MM_MMSLIB_CHARGE_FRAME_DATA_ROW" width="100%" border="0" cellspacing="1" cellpadding="1" class="tb-list">'
		 + '<tr>'
		 + '<td class="td-left" width="5%"  nowrap="nowrap">图片<span class="must">（jpg或gif）</span></td>'
		 + '<td class="td-right" width="85%" nowrap="nowrap">'															 
		 + '	<input type="hidden" name="photoname" id="photoname'+rowNum+'" />'
		 + '	<input type="hidden" name="photosize" id="photosize'+rowNum+'" value="0"/>'
		 + '    <input id="imageupload'+rowNum+'" type="file" style="width:60;" accept="image/*" onchange="gen_base64('+rowNum+');"/>&nbsp;&nbsp;<a href="javascript:void(0);" id="delphoto'+rowNum+'" style="display: none" onclick="delElement('+rowNum+',1)">删除</a>'
		 + '</td>'
		 + '</tr>'
		 + '<tr>'
		 + '<td width="5%" class="td-left">文本内容</td>'
		 + '<td class="td-right" width="85%">'
		 + '	<textarea name="txtcontent" id="txtcontent'+rowNum+'" rows="5" class="wid90" onkeyup="setFrameText(this.value,'+rowNum+');getSizeHtml();"></textarea>'
		 + '</td>'
		 + '</tr>'
		 + '</table>'
}

//新增预览帧
function InsertFrame(){
	 var rows = document.getElementsByName("dtRowID");
	 var rowNum=rows.length;
	 var rowId;
	 if(rowNum!=1){
		 rowId=parseInt(rows[rows.length-1].value);
	 }else{
		 rowId=rowNum;
	 }
	 if(rowNum==1){
		 document.getElementById("MessageObject_FRAMES").innerHTML='<div id="MessageObject_FRAME_'+rowNum+'" style="width:100%;" order="'+(rowNum)+'"><div id="SwapContent'+(rowNum)+'" style="width:80%;" align="center"><br><input id="FrameId" type="hidden" value=""><div id="Image" size="" path="" type="hidden"></div><div id="Text" size="" align="left" type="hidden"></div><div id="Audio" size="" path="" type="hidden"></div></div><div id="Foot" style="width:100%; height:21px; padding-top:4px; background-color:#2C6DBF;font-size:9pt;"><font color="#BEF170">第&nbsp;<span id="Order">'+(rowNum)+'</span>&nbsp;帧</font>&nbsp;</div></div>';
	 }else{
		 $("#MessageObject_FRAME_"+rows[rows.length-2].value).after('<div id="MessageObject_FRAME_'+rowId+'" style="width:100%;" order="'+(rowId)+'"><div id="SwapContent'+(rowId)+'" style="width:80%;" align="center"><br><input id="FrameId" type="hidden" value=""><div id="Image" size="" path="" type="hidden"></div><div id="Text" size="" align="left" type="hidden"></div><div id="Audio" size="" path="" type="hidden"></div></div><div id="Foot" style="width:100%; height:21px; padding-top:4px; background-color:#2C6DBF;font-size:9pt;"><font color="#BEF170">第&nbsp;<span id="Order">'+(rowNum)+'</span>&nbsp;帧</font>&nbsp;</div></div>');
	 }
}

//删除一行
function deleteRow(type){
	var rows = document.getElementsByName("dtRowID");
 	if(type==1){//删除
		for(var i=rows.length;i>0;i--){
			var e=rows[i-1];
			if(e.checked){
				document.getElementById('MM_MMSLIB_CHARGE_FRAME_DATA').deleteRow(i-1);
			}
		}
		for(var j=0;j<rows.length;j++){
			document.getElementById('rowNo'+rows[j].value).innerHTML='<input type="hidden" name="sorts" value="'+(j+1)+'"/>'+(j+1);
			document.getElementById("MessageObject_FRAME_"+rows[j].value).getElementsByTagName("span")[0].innerHTML=''+(j+1);
		}
		getSizeHtml();
	}
 }

//删除预览帧
function DeleteFrame(type)
{
	var rows = document.getElementsByName("dtRowID");
 	if(type==1){//删除
		for(var i=rows.length;i>0;i--){
			var e=rows[i-1];
			if(e.checked){
				var my = document.getElementById("MessageObject_FRAME_"+e.value);
			    if (my != null){
			    	my.parentNode.removeChild(my);
			    }
			}
		}
	}
}

function setTitle(title){
	this.title = title;
	document.getElementById("MessageObject_TITLE").innerHTML = title;
}

function setFrameText(text,num){
	this.text = text;
	var textObj=document.getElementById("SwapContent"+num).getElementsByTagName("div")[1];
	this.text = this.text.replace(/\n/g,"<br/>");
	textObj.innerHTML = this.text.replace(/ /g,"&nbsp;");
	
}

 function up(num){
	var minus=(num-1);
	if(num>1){
		
		var photosize1=document.getElementById('photosize'+minus).value;
		var photosize2=document.getElementById('photosize'+num).value;
		
		document.getElementById('photosize'+minus).value=photosize2;
		document.getElementById('photosize'+num).value=photosize1;
		
		var photoname1=document.getElementById('photoname'+minus).value;
		var photoname2=document.getElementById('photoname'+num).value;
		
		document.getElementById('photoname'+minus).value=photoname2;
		document.getElementById('photoname'+num).value=photoname1;
		
		
		var txtcontentvalue1 =document.getElementById("txtcontent"+minus).value;
		var txtcontentvalue2 =document.getElementById("txtcontent"+num).value;
		
		document.getElementById('txtcontent'+minus).value=txtcontentvalue2;
		document.getElementById('txtcontent'+num).value=txtcontentvalue1
		
		var SwapContent1=document.getElementById("SwapContent"+minus).innerHTML;
		var SwapContent2=document.getElementById("SwapContent"+num).innerHTML;
		
		document.getElementById("SwapContent"+minus).innerHTML=SwapContent2;
		document.getElementById("SwapContent"+num).innerHTML=SwapContent1;
	}
}

function down(num){
		if(document.getElementById("txtcontent"+(num+1))!=null){
			
			var photosize1=document.getElementById('photosize'+num).value;
			var photosize2=document.getElementById('photosize'+(num+1)).value;
			
			document.getElementById('photosize'+num).value=photosize2;
			document.getElementById('photosize'+(num+1)).value=photosize1;
			
			var photoname1=document.getElementById('photoname'+num).value;
			var photoname2=document.getElementById('photoname'+(num+1)).value;
			
			document.getElementById('photoname'+num).value=photoname2;
			document.getElementById('photoname'+(num+1)).value=photoname1;
			
			var imageupload1=document.getElementById('imageupload'+num).value;
			var imageupload2=document.getElementById('imageupload'+(num+1)).value;
			
			document.getElementById('imageupload'+num).value=imageupload2;
			document.getElementById('imageupload'+(num+1)).value=imageupload1;
			
			var txtcontentvalue1 =document.getElementById("txtcontent"+num).value;
			var txtcontentvalue2 =document.getElementById("txtcontent"+(num+1)).value;
			
			document.getElementById('txtcontent'+num).value=txtcontentvalue2;
			document.getElementById('txtcontent'+(num+1)).value=txtcontentvalue1;
			
			var SwapContent1=document.getElementById("SwapContent"+num).innerHTML;
			var SwapContent2=document.getElementById("SwapContent"+(num+1)).innerHTML;
			
			document.getElementById("SwapContent"+num).innerHTML=SwapContent2;
			document.getElementById("SwapContent"+(num+1)).innerHTML=SwapContent1;
			
		}
		
}

function updateImg(imgPath,order) {
	this.imgPath = imgPath;
//	if (imgSize != "")
//		this.imgSize = imgSize;
//	else
//		this.imgSize = 0;
//	this.setSize();
	var imgObj = document.getElementById("SwapContent" + order).getElementsByTagName("div")[0];
//	imgObj.size = this.imgSize;
	imgObj.path = this.imgPath;
//	imgObj.width = 200;
//	imgObj.Height = 200;
	if (this.imgPath == "")
	{
		imgObj.innerHTML = '';
		imgObj.style.display = 'none';
	}
	else
	{
		imgObj.innerHTML = '<img src="' + this.imgPath + '"/>';
		imgObj.style.display = 'block';
	}
}


function updateAudio(audioPath, order) {
	this.audioPath = audioPath;
//	if (audioSize != "")
//		this.audioSize = audioSize;
//	else
//		this.audioSize = 0;
//	this.setSize();
	var audioObj = parent.document.getElementById("SwapContent" + order).getElementsByTagName("div")[2];
//	audioObj.size = this.audioSize;
	audioObj.path = this.audioPath;
	if (this.audioPath == "")
	{
		audioObj.innerHTML = '';
		audioObj.style.display = 'none';
	}
	else
	{
		audioObj.innerHTML = '<embed src="' + this.audioPath + '" width="200" height="25" autostart="false" ShowPositionControls="false" ShowTracker="false" loop="false" type="application/x-mplayer2"></embed>';
//		audioObj.innerHTML = '<embed src="' + this.audioPath + '" autostart="false" loop="false" width="180" height="25"/>';
		audioObj.style.display = 'block';
	}
}

//删除元素
function delElement(target,type){
	if(confirm("是否删除?")){
		if(type==1){//删除图片
			document.getElementById('photoname'+target).value='';
			document.getElementById('photosize'+target).value=0;
			objFile = document.getElementById('imageupload'+target);
			objFile.outerHTML=objFile.outerHTML.replace(/(value=\").+\"/i,"$1\"");
			var imgObj = document.getElementById("SwapContent" + target).getElementsByTagName("div")[0];
			imgObj.innerHTML = '';
			imgObj.style.display = 'none';
			
			document.getElementById('delphoto'+target).style.display="none";
		}
		if(type==2){//删除音乐
			document.getElementById('musicname'+target).value='';
			document.getElementById('musicsize'+target).value=0;
			var audioObj = document.getElementById("SwapContent" + target).getElementsByTagName("div")[2];
			audioObj.innerHTML = '';
			audioObj.style.display = 'none';
			document.getElementById('delmusic'+target).style.display="none";
		}
		getSizeHtml();
		
	}
	
}


