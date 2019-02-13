
var article_page_num_param_name = "pn";

function isNull(formName,objectName,message){
	formObject = eval("document." + formName + "." + objectName);
	if (formObject.value == ""){
		alert(message);
		formObject.focus();
		return true;
	}
	return false;
}

function searchManage(searchStr,operType,name,value){
	var searchArray = new Array();
	var returnStr = "";		
	if (searchStr != ""){
		searchArray = searchStr.split("&");	
		
		for(var i = 0 ; i < searchArray.length ; i ++){
			if (searchArray[i] != ""){
				var paramArray = new Array();
				paramArray = searchArray[i].split("=");		
				if (paramArray[0] == name){
					searchArray.splice(i,1);
					i--;
				}
			}
		}
	}
	
	if (operType == "add"){
		returnStr = name +  "=" + value;
	}
	
	if (operType == "remove"){
	}
	
	
	for(var j = 0; j < searchArray.length; j++){
		if (searchArray[j] != ""){
			returnStr += "&" + searchArray[j];
		}
	}

	return returnStr;
	
}

function getSearchValue(searchStrParam,name){
	if (searchStrParam == "URL"){
		searchStr = this.location.search.substr(1);
	}else{
		searchStr = searchStrParam;
	}
	
	
	var searchArray = new Array();
	var returnStr = "";		
	if (searchStr != ""){
		searchArray = searchStr.split("&");	
		
		for(var i = 0 ; i < searchArray.length ; i ++){
			if (searchArray[i] != ""){
				var paramArray = new Array();
				paramArray = searchArray[i].split("=");		
				if (paramArray[0] == name){
					returnStr = paramArray[1];
					return returnStr;
				}
			}
		}
	}
	
	return "";
}


function addSearch(searchStr,name,value){
	if (searchStr == "URL"){
		return searchManage(this.location.search.substr(1),"add",name,value);
	}
	return searchManage(searchStr,"add",name,value);	
}




function removeSearch(searchStr,name){
	if (searchStr == "URL"){
		return searchManage(this.location.search.substr(1),"remove",name,"");	
	}
	return searchManage(searchStr,"remove",name,"");	
}


function pageJump(){
	var e = event.srcElement;
	this.location.search = addSearch("URL",article_page_num_param_name,e.value);
}


//反选checkbox
function reverseCheckBox(checkBoxID){
	var checkBoxObj = document.getElementById(checkBoxID);
	if(checkBoxObj.checked == false){
		checkBoxObj.checked = true;
	}else{
		checkBoxObj.checked = false;
	}
}

function unselectall(checkBoxID){
	var selectAllObj = document.getElementById(checkBoxID);
    if(selectAllObj.checked){
 		selectAllObj.checked = selectAllObj.checked&0;
    }
}

function checkAll(form){
  var eSource = window.event.srcElement;
  for (var i=0;i<form.elements.length;i++){
    var e = form.elements[i];
    if (e.Name != eSource.name && e.disabled == false)
       e.checked = eSource.checked;
    }
}

function checkAllForCheckBox(element){
	var eSource = window.event.srcElement;
	if(element==null)
		return;	
	if (element.Name != eSource.name && element.disabled == false)
       element.checked = eSource.checked;
	   
	for (var i=0;i<element.length;i++){
    var e = element[i];
    if (e.Name != eSource.name && e.disabled == false)
       e.checked = eSource.checked;
    }
}



function displayCtrl(displayElement,hideElement){
	if(displayElement != ""){
		var displayObj = document.getElementById(displayElement);
		displayObj.style.display = "";
	}
	
	if(hideElement != ""){
		var hideObj = document.getElementById(hideElement);
		hideObj.style.display = "none";
	}
}


function first_page(){
	this.location.search = addSearch("URL",article_page_num_param_name,1);
}


function next_page(pageCount){
	var pn = getSearchValue("URL",article_page_num_param_name);
	if (pn == "" || pn == null){
		var nextPage = 2;
	}else{
		var nextPage = parseInt(pn) + 1;
	}
	
	
	
	if (nextPage > pageCount){
		nextPage = pageCount;
	}
	
	this.location.search = addSearch("URL",article_page_num_param_name,nextPage);
	
}

function prev_page(){
	var pn = getSearchValue("URL",article_page_num_param_name);
	if (pn == "" || pn == null){
		first_page();
		return;
	}
	
	var nextPage = parseInt(pn) - 1;
	
	if (nextPage < 1){
		nextPage = 1;
	}
	
	this.location.search = addSearch("URL",article_page_num_param_name,nextPage);
}

function last_page(pageCount){
	this.location.search = addSearch("URL",article_page_num_param_name,pageCount);
}

function goto_page(pageNum){
	this.location.search = addSearch("URL",article_page_num_param_name,pageNum);
}



function dataFilter(search_param_name){
	var e = event.srcElement;
	var tmp = removeSearch("URL",search_param_name);
	this.location.search = addSearch(tmp,search_param_name,e.value);
}



function addURLParam(paramName,paramValue){
	var tmp = removeSearch("URL",paramName);
	this.location.search = addSearch(tmp,paramName,paramValue);
}



function cTrim(sInputString,iType){
	var sTmpStr = ' '
	var i = -1

	if(iType == 0 || iType == 1){
		while(sTmpStr == ' '){
			++i
			sTmpStr = sInputString.substr(i,1)
		}
      		sInputString = sInputString.substring(i)
	}

	if(iType == 0 || iType == 2){
		sTmpStr = ' '
		i = sInputString.length
		while(sTmpStr == ' '){
			--i
			sTmpStr = sInputString.substr(i,1)
		}
		sInputString = sInputString.substring(0,i+1)
	}
	return sInputString
}


function actionConfirm(text,actionUrl){
	if (! window.confirm(text)){
		return;
	}
	
}




function openFullWin(src,title,scrollbar) {
	woiwo=window.open(src,title,'resizable=yes,scrollbars=' + scrollbar + ',toolbar=no,menubar=no,fullscreen=no');
	woiwo.moveTo(0,0);
	woiwo.resizeTo(screen.availWidth,screen.availHeight);
	woiwo.outerWidth=screen.availWidth;
	woiwo.outerHeight=screen.availHeight;
	//window.close();
}


function openModalDialog(theURL,winName,features) { 
  window.showModalDialog(theURL,'',features+"center=yes;middle=yes;help=no;status=no;scroll=no");
}

function openModalDialog(theURL,winName,features,scrollState) { 
  window.showModalDialog(theURL,'',features+"center=yes;middle=yes;help=no;status=no;scroll=" + scrollState);
}

function openModalDialog2(theURL,w,h,scrollState) { 
  window.showModalDialog(theURL,'','dialogWidth:' + w + 'px;dialogHeight:' + h + 'px;center=yes;help=no;status=no;scroll='+ scrollState);
}

//居中打开某窗口
function openWindow(url,w,h){
	window.open(url,"","scrollbars=yes,width="+w+",height="+h+",left="+(window.screen.width-w)/2+",top="+(window.screen.height-h-50)/2+'"');
}


// 显示模式对话框
function showDialog(url, width, height) {
	showModalDialog(url, "", "dialogWidth:" + width + "px;dialogHeight:" + height + "px;help:no;scroll:no;status:no");
}

// 显示无模式对话框
function showDialog(url, width, height,scrollState) {
	showModalDialog(url, window, "dialogWidth:" + width + "px;dialogHeight:" + height + "px;help:no;scroll:"+scrollState+";status:no");
	
}



//写cookies函数
function SetCookie(name,value,Days){	//两个参数，一个是cookie的名子，一个是值
    var exp  = new Date();    //new Date("December 31, 9998");
    exp.setTime(exp.getTime() + Days*24*60*60*1000);
    document.cookie = name + "="+ escape (value) + ";expires=" + exp.toGMTString();
}

//取cookies函数     
function getCookie(name){	   
    var arr = document.cookie.match(new RegExp("(^| )"+name+"=([^;]*)(;|$)"));
     if(arr != null) return unescape(arr[2]); return null;
}

//删除cookie
function delCookie(name){	
    var exp = new Date();
    exp.setTime(exp.getTime() - 1);
    var cval=getCookie(name);
    if(cval!=null) document.cookie= name + "="+cval+";expires="+exp.toGMTString();
}


function displayOrHide(obj){
	if(obj != ""){
		var displayObj = document.getElementById(obj);
		if(displayObj){
			
			if(displayObj.style.display==""){
				displayObj.style.display = "none";
			}else{
				displayObj.style.display = "";
			}
		}
		
	}
}


function confirmAction(formObject,msg,action){
	if (! window.confirm(msg)){
		return;
	}
	
	formObject.action = action;
	formObject.submit();
}

function doAction(formObject,action){
	formObject.action = action;
	formObject.submit();
}


function buttonAction(formObject,action){
	formObject.action = action;
	formObject.submit();
}

//数据维护界面中，通过标题栏控制内容区显示与隐藏
function titleBarShowOrHide(objID){
	displayOrHide(objID);
	
	var showOrHideObj = document.getElementById(objID);
	var obj = document.getElementById(objID + "_btn");
	
	if(obj){
		if(showOrHideObj.style.display==""){
			obj.className = "title-bar2-btn-col";
		}else{
			obj.className = "title-bar2-btn-exp";
		}
	}
}

//检测某checkbox是否被选择
function checkCheckboxIsSelected(checkboxObject,errmsg){
	if(getCheckboxSelectedValue(checkboxObject) == ""){
		alert(errmsg);
		return false;
	}
	
	return true;
}

//获取单选按钮选择的值
function getRadioSelectedValue(radioName){
	var radioEmts = document.getElementsByName(radioName);
	for(var m=0;m<radioEmts.length;m++){
		if(radioEmts[m].checked==true){
			return radioEmts[m].value;
		}
	}
}


//获取checkbox所有被选择的值
function getCheckboxSelectedValue(checkboxObject){
	var str = "";
	var s = 0;
	if(!isNaN(checkboxObject.length)){			 
			for(i=0;i<checkboxObject.length;i++){   
				if(checkboxObject[i].checked == true){   
					s = s + 1;
					if(s > 1){
						str = str + "," + checkboxObject[i].value;
					}else{
						str = checkboxObject[i].value;
					}
				}   
			}
	}else{
		str = checkboxObject.value;
	}
	return str;
}


//取得多选下拉框中的值
function getMultiSelectSelectedValue(multiSelectObject){
	var str = "";
	var s = 0;
	for(i=0;i<multiSelectObject.options.length;i++){   
		if(multiSelectObject.options[i].selected == true){   
			s = s + 1;
			if(s > 1){
				str = str + "," + multiSelectObject.options[i].value;
			}else{
				str = multiSelectObject.options[i].value;
			}
		}   
	}
	
	return str;
}


String.prototype.replaceAll = stringReplaceAll;
function stringReplaceAll(AFindText,ARepText){
	var raRegExp = new RegExp(AFindText,"g")
	return this.replace(raRegExp,ARepText)
}



function next_page_message_detail(pageCount,currPage){
	var pn = getSearchValue("URL",article_page_num_param_name);
	obj=eval("top.messageFrame");
	
	 	if (pn == "" || pn == null){
			var nextPage = currPage+1;
			obj.location.search = addSearch("URL",article_page_num_param_name,nextPage);
		}else{
			var nextPage = parseInt(pn) + 1;
			obj.location.search = addSearch("URL",article_page_num_param_name,nextPage);
		}
		
		if (nextPage>pageCount){
	    	var nextPage = pageCount;
			obj.location.search = addSearch("URL",article_page_num_param_name,nextPage);	
     	}



	this.location.search = addSearch("URL",article_page_num_param_name,nextPage);
	
}



function prev_page_message_detail(currPage){
	var pn = getSearchValue("URL",article_page_num_param_name);
	obj=eval("top.messageFrame");
	if (pn == "" || pn == null){
		var nextPage=currPage-1;
		obj.location.search = addSearch("URL",article_page_num_param_name,nextPage);
	}else{
	    nextPage = parseInt(pn) - 1;
	    obj.location.search = addSearch("URL",article_page_num_param_name,nextPage);
	}
	
	if (nextPage < 1){
		nextPage = 1;
		obj.location.search = addSearch("URL",article_page_num_param_name,nextPage);
	}
	
	this.location.search = addSearch("URL",article_page_num_param_name,nextPage);
}

function $(emtID){
	return document.getElementById(emtID);
}


/**
* AJAX方法，调用后，返回一个字符串
*/
function ajax(url){
	var http=new ActiveXObject("Microsoft.XMLHTTP");
	http.open("post",url,false);
	http.send();
	return unescape(http.responseText);	
}