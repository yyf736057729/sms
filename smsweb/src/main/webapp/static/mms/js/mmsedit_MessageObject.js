MessageFrameObject = function(isInit, msg, id, ord, imagePath, imageSize, audPath, audSize, txt, txtSize) {
	//alert('isInit='+isInit+'   msg='+msg+'	id='+id+'	ord='+ord+'	 imagePath='+imagePath+'	imageSize='+imageSize+'	audPath='+audPath+'	audSize='+audSize+'	 txt='+txt+'	txtSize='+txtSize);
	this.message = msg;
	this.id = id;
	this.order = ord;
	this.imgPath = "";
	if (imagePath != undefined && imagePath != "")
	{
		this.imgPath = imagePath;
	}
	this.imgSize = 0;
	if (imageSize != undefined && imageSize != "")
	{
		this.imgSize = parseInt(imageSize);
	}
	this.audioPath = "";
	if (audPath != undefined && audPath != "")
	{
		this.audioPath = audPath;
	}
	this.audioSize = 0;
	if (audSize != undefined && audSize != "")
	{
		this.audioSize = parseInt(audSize);
	}
	this.text = "";
	if (txt != undefined && txt != "")
	{
		this.text = txt.replace(/<br>/g,"\n");
	}
	this.textSize = 0;
	if (txtSize != undefined && txtSize != "")
	{
		this.textSize = parseInt(txtSize);
	}
	
	this.size = 0;
	this.setSize();
	if (!isInit)
	{
		document.all("MessageObject_DIV").all("MessageObject_FRAME_NEW").outerHTML = this.getHtml() + '<div id="MessageObject_FRAME_NEW" order="-1"/>';
	}
}

MessageFrameObject.prototype.getHtml = function() {
	var htmlArray = [];
	htmlArray.push('<div id="MessageObject_FRAME_' + this.order + '" style="width:100%;" order="' + this.order + '">');
	htmlArray.push('<div id="SwapContent" style="width:80%;" align="center"><br/>');
	htmlArray.push('<input id="FrameId" style="display:none" type="hidden" value="' + this.id + '"/>');
	htmlArray.push('<div id="Image" path="' + this.imgPath + '" size="' + this.imgSize );
	if (this.imgPath != '')
	{		
		htmlArray.push('" style="display:block"><img src="' + this.imgPath + '" ' + DefaultHeight() + '/>');
	}
	else
		htmlArray.push('" style="display:none">');
	htmlArray.push('</div>');
	htmlArray.push('<div id="Text" align="left" size="' + this.textSize);
	if (this.text != '')
		htmlArray.push('" style="display:block">' + this.text.replace(/\n/g,"<br>"));
	else
		htmlArray.push('" style="display:none">');
	htmlArray.push('</div>');
	htmlArray.push('<div id="Audio" path="' + this.audioPath + 'size="' + this.audioSize);
	if (this.audioPath != '')
		htmlArray.push('" style="display:block"><embed src="' + this.audioPath + '" autostart="false" loop="false" width="180" height="25"/>');
	else
		htmlArray.push('" style="display:none">');
	htmlArray.push('</div>');
	htmlArray.push('</div>');
	htmlArray.push('<div id="Foot" style="width:100%; height:21px; padding-top:4px; background-color:#2C6DBF;font-size:9pt;">');
	htmlArray.push('<font color="#BEF170">第<span id="Order">' + (parseInt(this.order) + 1) + '</span> 帧</font>&nbsp;&nbsp;');
	htmlArray.push('<a onclick="Message.up(' + this.order + ');" style="cursor:hand"><img src="images/arrow_up.gif"/><font color="#FFFFFF">上移</font></a>&nbsp;&nbsp;');
	htmlArray.push('<a onclick="Message.down(' + this.order + ');" style="cursor:hand"><img src="images/arrow_down.gif"/><font color="#FFFFFF">下移</font></a>&nbsp;&nbsp;');
	htmlArray.push('</div></div>');

	return htmlArray.join('');
}

MessageFrameObject.prototype.setSize = function() {
	this.size = parseInt(this.imgSize) + parseInt(this.audioSize) + parseInt(this.textSize);
}

MessageFrameObject.prototype.setOrder = function(newOrder) {
	var oldOrder = this.order;
	this.order = newOrder;
	document.all("MessageObject_DIV").all("MessageObject_FRAME_" + oldOrder.toString()).outerHTML = this.getHtml();
}

MessageFrameObject.prototype.update = function(imgPath, imgSize, audioPath, audioSize, text) {
	this.imgPath = imgPath;
	if (imgSize != "")
		this.imgSize = imgSize;
	else
		this.imgSize = 0;
	this.audioPath = audioPath;
	if (audioSize != "")
		this.audioSize = audioSize;
	else
		this.audioSize = 0;
	this.text = text;
	if (text != "")
		this.textSize = (this.message.messageType == "MMS") ? strlen(text) : text.length;
	else
		this.textSize = 0;
	this.setSize();
	document.all("MessageObject_DIV").all("MessageObject_FRAME_" + this.order).outerHTML = this.getHtml();
}

MessageFrameObject.prototype.updateImg = function(imgPath, imgSize) {
	this.imgPath = imgPath;
	if (imgSize != "")
		this.imgSize = imgSize;
	else
		this.imgSize = 0;
	this.setSize();
	var imgObj = document.all("MessageObject_DIV").all("MessageObject_FRAME_" + this.order).all("Image");
	imgObj.size = this.imgSize;
	imgObj.path = this.imgPath;
	imgObj.width = 200;
	imgObj.Height = 200;
	if (this.imgPath == "")
	{
		imgObj.innerHTML = '';
		imgObj.style.display = 'none';
	}
	else
	{
		imgObj.innerHTML = '<img src="' + this.imgPath + '" ' + DefaultHeight() + '/>';
		imgObj.style.display = 'block';
	}
}

MessageFrameObject.prototype.updateAudio = function(audioPath, audioSize) {
	this.audioPath = audioPath;
	if (audioSize != "")
		this.audioSize = audioSize;
	else
		this.audioSize = 0;
	this.setSize();
	var audioObj = document.all("MessageObject_DIV").all("MessageObject_FRAME_" + this.order).all("Audio");
	audioObj.size = this.audioSize;
	audioObj.path = this.audioPath;
	if (this.audioPath == "")
	{
		audioObj.innerHTML = '';
		audioObj.style.display = 'none';
	}
	else
	{
		audioObj.innerHTML = '<embed src="' + this.audioPath + '" autostart="false" loop="false" width="180" height="25"/>';
		audioObj.style.display = 'block';
	}
}

MessageFrameObject.prototype.updateText = function(text) {
	this.text = text;
	if (text != "")
		this.textSize = (this.message.messageType == "MMS") ? strlen(text) : text.length;
	else
		this.textSize = 0;
	this.setSize();
	var textObj = document.all("MessageObject_DIV").all("MessageObject_FRAME_" + this.order).all("Text");
	textObj.size = this.textSize;
	if (text == "")
	{
		textObj.innerHTML = "";
		textObj.style.display = 'none';
	}
	else
	{
		this.text = this.text.replace(/\n/g,"<br/>");
		textObj.innerHTML = this.text.replace(/ /g,"&nbsp;");
		textObj.style.display = 'block';
	}
	var imgObj = document.all("MessageObject_DIV").all("MessageObject_FRAME_" + this.order).all("Image");
	if (imgObj.innerHTML == "")
		imgObj.style.display = 'none';
	else
		imgObj.style.display = 'block';
	var audioObj = document.all("MessageObject_DIV").all("MessageObject_FRAME_" + this.order).all("Audio");
	if (audioObj.innerHTML == "")
		audioObj.style.display = 'none';
	else
		audioObj.style.display = 'block';
}

MessageObject = function(tblName, msgType, totalSize) {	
	this.tableName = tblName;
	this.messageType = msgType;
	if (totalSize != undefined && totalSize != "")
		this.messageTotalSize = totalSize;
	else
		this.messageT0talSize = 100;
	this.title = "";
	this.size = 0;
	this.mframes = [];
	var frameDivList = document.all("MessageObject_DIV").all("MessageObject_FRAMES").childNodes;
	
	for (var i=0; i<frameDivList.length; i++)
	{
		if (frameDivList[i].tagName.toUpperCase() == "DIV" && frameDivList[i].order >= 0)
		{
			var imgPath = "";
			var imgSize = 0;
			if (frameDivList[i].all("Image") != undefined)
			{
				imgPath = frameDivList[i].all("Image").src;
				imgSize = frameDivList[i].all("Image").size;
			}
			var audioPath = "";
			var audioSize = 0;
			if (frameDivList[i].all("Audio") != undefined)
			{
				audioPath = frameDivList[i].all("Audio").src;
				audioSize = frameDivList[i].all("Audio").size;
			}
			var text = "";
			var textSize = 0;
			if (frameDivList[i].all("Text") != undefined)
			{
				text = frameDivList[i].all("Text").innerHTML;
				textSize = frameDivList[i].all("Text").size;
			}
			var frame = new MessageFrameObject(true, this, frameDivList[i].FrameId, frameDivList[i].order, imgPath, imgSize, audioPath, audioSize, text, textSize);
			this.mframes[frame.order] = frame;
			this.size += frame.size;
		}
	}
}

MessageObject.prototype.smsRefresh = function(content, type, isLong) {
	this.mframes.splice(0, this.mframes.length);
	this.size = 0;
	var maxLen = 0;
	switch(type)
	{
		case "0":
			maxLen = 140;
			break;
		case "4":
			maxLen = 280;
			break;
		default:
			maxLen = 70;
			break;
	}
	if(isLong)
		maxLen = 10000;

	//maxLen += 5;
	var total = parseInt(content.length / (maxLen - 5));
	total += (content.length % (maxLen - 5) == 0) ? 0 : 1;
	
	var foot = (type == "0") ? "[--0--/--1--]" : "[--0--/--1--]";
	foot = foot.replace(/--1--/g, total);
	var count = 0;
	var framesHTML = "";

	for (var i=0; i<content.length; )
	{
		count ++;
		var tmpFoot = "";
		if (content.length > maxLen)
			tmpFoot = foot.replace(/--0--/g, count);

		var tmp = content.substr(i, maxLen - tmpFoot.length);
		i = i + maxLen - tmpFoot.length;
		var frame = new MessageFrameObject(false, this, count - 1, count - 1, "", 0, "", 0, tmpFoot + tmp, tmp.length);
		this.mframes[frame.order] = frame;
		this.size += frame.size;
		framesHTML += frame.getHtml();
		if (i >= content.length)
			break;
	}
	framesHTML += '<div id="MessageObject_FRAME_NEW" order="-1"/>';
	document.all("MessageObject_FRAMES").innerHTML = framesHTML;
	document.all("MessageObject_DIV").all("MessageObject_SIZE").innerHTML = this.getSizeHtml();
}

MessageObject.prototype.setTitle = function(title) {
	this.title = title;
	document.all("MessageObject_DIV").all("MessageObject_TITLE").innerHTML = title;
}

MessageObject.prototype.addFrame = function(id) {
	var n = this.mframes.length;
	var frame = new MessageFrameObject(false, this, id, n);
	this.mframes[n] = frame;
	this.size += frame.size;
	document.all("MessageObject_DIV").all("MessageObject_SIZE").innerHTML = this.getSizeHtml();
	return n;
}

MessageObject.prototype.updateFrame = function(order, imgPath, imgSize, audioPath, audioSize, text) {
	if (order < 0)
		return;
	this.size -= this.mframes[order].size;
	this.mframes[order].update(imgPath, imgSize, audioPath, audioSize, text);
	this.size += this.mframes[order].size;
	document.all("MessageObject_DIV").all("MessageObject_SIZE").innerHTML = this.getSizeHtml();
}

MessageObject.prototype.updateFrameImage = function(order, imgPath, imgSize) {
	if (order < 0)
		return;
	this.size -= this.mframes[order].size;
	//this.mframes[order].updateImg(imgPath, imgSize);
	//this.size += this.mframes[order].size;
	this.mframes[order].updateImg(imgPath, imgSize);
	this.size += this.mframes[order].size;
	document.all("MessageObject_DIV").all("MessageObject_SIZE").innerHTML = this.getSizeHtml();
}

MessageObject.prototype.updateFrameAudio = function(order, audioPath, audioSize) {
	if (order < 0)
		return;
	this.size -= this.mframes[order].size;
	this.mframes[order].updateAudio(audioPath, audioSize);
	this.size += this.mframes[order].size;
	document.all("MessageObject_DIV").all("MessageObject_SIZE").innerHTML = this.getSizeHtml();
}

MessageObject.prototype.updateFrameText = function(order, text) {

	if (order < 0)
		return;
	this.size -= this.mframes[order].size;
	this.mframes[order].updateText(text);
	this.size += this.mframes[order].size;
	document.all("MessageObject_DIV").all("MessageObject_SIZE").innerHTML = this.getSizeHtml();
}

MessageObject.prototype.deleteFrame = function(order) {
	if (order < 0)
		return;
	this.size -= this.mframes[order].size;
	//将order帧的id临时改为MessageObject_FRAME_DEL
	document.all("MessageObject_DIV").all("MessageObject_FRAME_" + order.toString()).id = "MessageObject_FRAME_DEL";
	//修改order之后的帧的显示
	for (var i=order+1; i<this.mframes.length; i++)
		this.mframes[i].setOrder(i - 1);
	//清除帧order的显示
	document.all("MessageObject_DIV").all("MessageObject_FRAME_DEL").outerHTML = "";
	this.mframes.splice(order, 1);//删除帧order
	document.all("MessageObject_DIV").all("MessageObject_SIZE").innerHTML = this.getSizeHtml();
}

MessageObject.prototype.deleteAllFrame = function() {
	this.size = 0;
	for (var i=0; i<this.mframes.length; i++)
		document.all("MessageObject_DIV").all("MessageObject_FRAME_" + i.toString()).outerHTML = "";
	this.mframes.splice(0, this.mframes.length);
	document.all("MessageObject_DIV").all("MessageObject_SIZE").innerHTML = this.getSizeHtml();
}

MessageObject.prototype.getSizeHtml = function() {
	if (this.messageType == "MMS")
	{
		var curSize = this.size / 1024;
		curSize = curSize.toFixed(1);
		var leavSize = this.messageTotalSize - curSize;
		return '当前:' + curSize + 'K 剩余可编辑:' + leavSize.toFixed(1) + 'K';
	}
	else if (this.messageType == "SMS")
	{
		return '当前:' + this.size + '字';
	}
	else
		return '';
}

MessageObject.prototype.upAttach = function(){
	if(this.messageType == "SMS")
		return false;
	
	var tableObj = document.all(Message.tableName);
	if(tableObj == null || tableObj == undefined)
		return false;
	
	var dtCount = tableObj.all('dtRowID').length;
	if (dtCount == undefined)
		dtCount = 1;
	
	var mfOrder = false;
	for(i=0;i<dtCount;i++)
		//if(tableObj.all("MF_ORDER", i).value == 200) yss update
		if(document.getElementsByName("MF_ORDER")[i].value == 200)
			mfOrder = true;
		
	if(!mfOrder)
		return false;

	this.up(dtCount - 1);
	//this.up(document.all(Message.tableName).all('dtRowID').length - 1);
}

MessageObject.prototype.up = function(order) {
	if (order <= 0 || this.messageType == "SMS")
		return false;
	this.swap(order, -1);
}

MessageObject.prototype.down = function(order) {
	if (order >= this.mframes.length-1  || this.messageType == "SMS")
		return false;
	this.swap(order, 1);
}

MessageObject.prototype.swap = function(order, n) {


	var thisIndex = parseInt(order);
	var swapIndex = parseInt(order) + parseInt(n);
	var index_2;
	
	if(thisIndex>0){
		index_2=thisIndex-1;
	}else{
		index_2=thisIndex;
	}
	
	var mfOrder =document.getElementsByName("MF_ORDER")[index_2].value;
	//var mfOrder = document.all(Message.tableName).all("MF_ORDER", index_2).value;

	
	var tableObj = document.all(this.tableName);
	var objName = this.tableName + "_DATA_ROW";
	if (tableObj == undefined || document.getElementsByName(objName)[thisIndex] == undefined)
		return;
	//交换帧序号
	document.getElementsByName(objName)[thisIndex].all("MF_ORDER").value = swapIndex;
	document.getElementsByName(objName)[swapIndex].all("MF_ORDER").value = thisIndex;
	if(mfOrder != null && mfOrder != undefined && mfOrder==200)
		document.getElementsByName(objName)[thisIndex].all("MF_ORDER").value = 200;
	//交换帧对象
	var tmpObj = this.mframes[thisIndex];
	this.mframes[thisIndex] = this.mframes[swapIndex];
	this.mframes[swapIndex] = tmpObj;
	this.mframes[thisIndex].order = thisIndex;
	this.mframes[swapIndex].order = swapIndex;
	//交换编辑内容
	tmpContent = document.getElementsByName(objName)[thisIndex].outerHTML;
	document.getElementsByName(objName)[thisIndex].outerHTML = document.getElementsByName(objName)[swapIndex].outerHTML;
	document.getElementsByName(objName)[swapIndex].outerHTML = tmpContent;
	//交换帧内容
	var thisObj = document.all("MessageObject_DIV").all("MessageObject_FRAME_" + thisIndex.toString()).all("SwapContent");
	var swapObj = document.all("MessageObject_DIV").all("MessageObject_FRAME_" + swapIndex.toString()).all("SwapContent");
	var tmpContent = thisObj.innerHTML;
	thisObj.innerHTML = swapObj.innerHTML;
	swapObj.innerHTML = tmpContent;
}

function InsertFrame()
{
	var tableObj = document.all(Message.tableName);
	var order = 0;
	if (tableObj.all("dtRowID") != null && tableObj.all('dtRowID').length != undefined)
		order = tableObj.all('dtRowID').length - 1;
		document.getElementsByName("MF_ORDER")[order].value = Message.addFrame(-1);
	//tableObj.all("MF_ORDER", order).value = Message.addFrame(-1);  yss update
}

function UpdateFrameImageOrAudio(type, order, path, size)
{
	if (type == "image")
		Message.updateFrameImage(order, path, size);
	else
		Message.updateFrameAudio(order, path, size);
}

function DeleteFrame()
{
	var tableObj = document.all(Message.tableName);
	if (tableObj.all("dtRowID") == null)
		return false;
	var dtCount = tableObj.all("dtRowID").length;
	if (dtCount == undefined)
		dtCount = 1;
	for (i=1; i<=dtCount; i++) {
		if (tableObj.all("dtRowID", dtCount-i).checked)	{
			var row = dtCount-i;
			Message.deleteFrame(dtCount-i);
		}
	}
}

function SetFrameOrder()
{
	var tableObj = document.all(Message.tableName);
	if (tableObj.all("dtRowID") == null)
		return false;
	var dtCount = tableObj.all("dtRowID").length;
	if (dtCount == undefined)
		dtCount = 1;
	for (i=1; i<=dtCount; i++) {
		tableObj.all("MF_ORDER", dtCount-i).value = dtCount-i;
	}
}

function DeleteAllFrame()
{
	var tableObj = document.all(Message.tableName);
	if (tableObj.all("dtRowID") == null)
		return false;
	Message.deleteAllFrame();
}

function GetRowIDEx(eleobj)
{
	var objTr = eleobj.offsetParent.parentElement;
	var objTb = eleobj.offsetParent;
	while (objTb.tagName.toUpperCase() != "TABLE")	{
		objTb = objTb.offsetParent;
	}
	objTr = objTb.offsetParent.parentElement;
	objTb = objTb.offsetParent;
	while (objTb.tagName.toUpperCase() != "TABLE")	{
		objTb = objTb.offsetParent;
	}
	var trCount = objTb.rows.length;
	if (objTr.all("dtRowID") == null) 
		return 0;
	if (objTb.all("dtRowID") == null) 
		return 0;
	var dtCount = objTb.all("dtRowID").length;
	if (dtCount == undefined)
		dtCount = 1;
	for (i=0; i<trCount; i++) {
		if (objTr == objTb.rows(i)) {
			//var rownum = i + dtCount - trCount; 
			var rownum = i;
			document.getElementsByName("MF_IMAGE_POS")[rownum].value=rownum;
			document.getElementsByName("MF_AUDIO_POS")[rownum].value=rownum;
			document.getElementsByName("MF_ORDER")[rownum].value=rownum;
		//	$('MM_MMSLIB_CHARGE_FRAME').all('MF_IMAGE_POS', rownum).value = rownum;   yss update
			//$('MM_MMSLIB_CHARGE_FRAME').all('MF_AUDIO_POS', rownum).value = rownum;  yss update
			return rownum;
		}
	}
	return 0;
}

function strlen(str)
{
	var len;
	var i;
	len = 0;
	for (i=0;i<str.length;i++)
	{
		if (str.charCodeAt(i)>255)
			len+=2;
		else
			len++;
	}
	return len;
}

function DefaultHeight()
{
	var stringHeight = "200";
	var onLoadString = " onload='if(this.width >" + stringHeight + ")";
	onLoadString += "{this.width=" + stringHeight+"}'";
//	onLoadString += "{this.height=this.height*" + stringHeight;
//	onLoadString += "/this.width;this.width=" + stringHeight+"}'";
  	return onLoadString;
}

