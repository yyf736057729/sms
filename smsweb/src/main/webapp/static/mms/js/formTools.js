var selectStr="";
/*打开小窗口*/
function MM_openBrWindow(theURL, winName) {
    window.open(theURL, winName, 'left=300,top=50,width =600,height=600,resizable=1,scrollbars=yes,menubar=no,status=yes');
}
function MM_openBrWindow(theURL, winName, style) {
    window.open(theURL, winName, 'resizable=1,scrollbars=yes,menubar=no,status=yes,' + style);
}
function createCode(){
    var code = "";   
    var codeLength = 10;   
    var selectChar = new Array(0,1,2,3,4,5,6,7,8,9,'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z');//所有候选组成验证码的字符，当然也可以用中文的             
    for(var i=0;i<codeLength;i++){    
	    var charIndex = Math.floor(Math.random()*36);   
	    code +=selectChar[charIndex];    
    }   
    return code;
} 
function open_win(url,iWidth,iHeight){
	//url转向网页的地址;
	//name网页名称，可为空;
	//iWidth弹出窗口的宽度;
	//iHeight弹出窗口的高度;
	var iTop = (window.screen.availHeight-30-iHeight)/2; //获得窗口的垂直位置;
	var iLeft = (window.screen.availWidth-10-iWidth)/2; //获得窗口的水平位置;
	window.open(url,createCode(),'height='+iHeight+',innerHeight='+iHeight+',width='+iWidth+',innerWidth='+iWidth+',top='+iTop+',left='+iLeft+',toolbar=no,menubar=no,scrollbars=yes,resizeable=no,location=no,status=no');
}
/*是否单选了按纽*/
function check() {
    var xx = false;
    if (document.form1.BH == null)
    {
        return false;
    }
    if (document.form1.BH.length == null) {
        xx = document.form1.BH.checked;
    } else {
        for (i = 0; i < document.form1.BH.length; i++)
        {
            if (document.form1.BH[i].checked) {
                xx = true;
                break;
            }
        }
    }
    return xx;
}


/*IsInteger: 用于判断一个数字型字符串是否为整形，
  还可判断是否是正整数或负整数，返回值为true或false
string: 需要判断的字符串
sign: 若要判断是正负数是使用，是正用'+'，负'-'，不用则表示不作判断
*/

function isInteger(string, sign)
{
    var integer;
    if ((sign != null) && (sign != '-') && (sign != '+'))
    {
        alert('IsInter(string,sign)的参数出错：nsign为null或"-"或"+"');
        return false;
    }
    integer = parseInt(string);
    if (isNaN(integer))
    {
        return false;
    }
    else if (integer.toString().length == string.length)
    {
        if ((sign == null) || (sign == '-' && integer < 0) || (sign == '+' && integer > 0))
        {
            return true;
        }
        else
            return false;
    }
    else
        return false;
}


//判断数字
/*
function isInteger(str){
  var i;
  var digit;
  for(i=0; i < str.length;i++){
    digit = str.subString(i,i+1);
    if (digit<0 || digit > 9){
      return false;
    }
  }

  return true;
}
*/

/*
IsDate: 用于判断一个字符串是否是日期格式的字符串

返回值：
  true或false

参数：
DateString： 需要判断的字符串
Dilimeter ： 日期的分隔符，缺省值为'-'
*/

function isDate(DateString, Dilimeter)
{
    if (DateString == null) return false;
    if (Dilimeter == '' || Dilimeter == null)
        Dilimeter = '-';
    var tempy = '';
    var tempm = '';
    var tempd = '';
    var tempArray;
    if (DateString.length < 8 && DateString.length > 10)
        return false;
    tempArray = DateString.split(Dilimeter);
    if (tempArray.length != 3)
        return false;
    if (tempArray[0].length == 4)
    {
        tempy = tempArray[0];
        tempd = tempArray[2];
    }
    else
    {
        tempy = tempArray[2];
        tempd = tempArray[1];
    }
    tempm = tempArray[1];
    var tDateString = tempy + '/' + tempm + '/' + tempd + ' 8:0:0';
    //加八小时是因为我们处于东八区
    var tempDate = new Date(tDateString);
    if (isNaN(tempDate))
        return false;
    if (((tempDate.getUTCFullYear()).toString() == tempy) && (tempDate.getMonth() == parseInt(tempm) - 1) && (tempDate.getDate() == parseInt(tempd)))
    {
        return true;
    }
    else
    {
        return false;
    }
}

/*
IsNumber: 用于判断一个数字型字符串是否为数值型，
  还可判断是否是正数或负数，返回值为true或false
string: 需要判断的字符串
sign: 若要判断是正负数是使用，是正用'+'，负'-'，不用则表示不作判断
*/

function isNumber(string, sign)
{
    var number;
    if (string == null) return false;
    if ((sign != null) && (sign != '-') && (sign != '+'))
    {
        alert('IsNumber(string,sign)的参数出错：nsign为null或"-"或"+"');
        return false;
    }
    number = new Number(string);
    if (isNaN(number))
    {
        return false;
    }
    else if ((sign == null) || (sign == '-' && number < 0) || (sign == '+' && number > 0))
    {
        return true;
    }
    else
        return false;
}


/*

==================================================================

LTrim(string):去除左边的空格

==================================================================

*/

function LTrim(str)

{
    var whitespace = new String(" \t\n\r");
    var s = new String(str);

    if (whitespace.indexOf(s.charAt(0)) != -1)

    {
        var j = 0, i = s.length;
        while (j < i && whitespace.indexOf(s.charAt(j)) != -1)
        {
            j++;
        }
        s = s.substring(j, i);
    }
    return s;
}


/*

==================================================================

RTrim(string):去除右边的空格

==================================================================

*/

function RTrim(str)

{

    var whitespace = new String(" \t\n\r");
    var s = new String(str);

    if (whitespace.indexOf(s.charAt(s.length - 1)) != -1)

    {
        var i = s.length - 1;
        while (i >= 0 && whitespace.indexOf(s.charAt(i)) != -1)
        {
            i--;
        }
        s = s.substring(0, i + 1);
    }
    return s;
}


/*

==================================================================

Trim(string):去除前后空格

==================================================================

*/

function Trim(str)

{

    return RTrim(LTrim(str));

}


/*

IsFloat(string,string,int or string):(测试字符串,+ or - or empty,empty or 0)

功能：判断是否为浮点数、正浮点数、负浮点数、正浮点数+0、负浮点数+0

*/

function isFloat(objStr, sign, zero)
{
    var reg;
    var bolzero;

    if (Trim(objStr) == "")

    {
        return false;
    }
    else
    {
        objStr = objStr.toString();
    }

    if ((sign == null) || (Trim(sign) == ""))
    {
        sign = "+-";
    }

    if ((zero == null) || (Trim(zero) == ""))
    {
        bolzero = false;
    }
    else
    {
        zero = zero.toString();
        if (zero == "0")
        {
            bolzero = true;
        }
        else
        {
            alert("检查是否包含0参数，只可为(空、0)");
        }
    }

    switch (sign)
            {
        case "+-":
        //浮点数
            reg = /^((-?|\+?)\d+)(\.\d+)?$/;

            break;
        case "+":
            if (!bolzero)
            {
                //正浮点数
                reg = /^\+?(([0-9]+\.[0-9]*[1-9][0-9]*)|([0-9]*[1-9][0-9]*\.[0-9]+)|([0-9]*[1-9][0-9]*))$/;
            }
            else
            {
                //正浮点数+0
                reg = /^\+?\d+(\.\d+)?$/;
            }
            break;
        case "-":
            if (!bolzero)
            {
                //负浮点数
                reg = /^-(([0-9]+\.[0-9]*[1-9][0-9]*)|([0-9]*[1-9][0-9]*\.[0-9]+)|([0-9]*[1-9][0-9]*))$/;
            }
            else
            {
                //负浮点数+0
                reg = /^((-\d+(\.\d+)?)|(0+(\.0+)?))$/;
            }
            break;
        default:
            alert("检查符号参数，只可为(空、+、-)");
            return false;
            break;
    }

    var r = objStr.match(reg);
    if (r == null)
    {
        return false;
    }
    else
    {
        return true;
    }
}

//限制最大字数
function textCounter(field, maxlimit) {
    if (field.value.length > maxlimit)
        field.value = field.value.substring(0, maxlimit);
}

//返回到上一页
function goBack(url) {
    document.location = url;
}

/*多选框选择全部*/
function checkAll(str)
{
    var a = document.getElementsByName(str);
    var n = a.length;
    for (var i = 0; i < n; i++)
        a[i].checked = window.event.srcElement.checked;
}

//选中所有复选框
//form:复选框所属的form
//objName:复选框的Name
function SelectAll(form, objName) {
    for (var i = 0; i < form.elements.length; i++) {
        if (form.elements[i].type == "checkbox") {
            if ((form.elements[i].name == objName) &&(form.elements[i].disabled ==false)) { //允许选中的
                form.elements[i].checked = true;
            }
        }
    }
}

//用途：Web上选中视图中的所有复选框的反操作；
//form:复选框所属的form
//objName:复选框的Name
function UnSelectAll(form, objName) {

    for (var i = 0; i < form.elements.length; i++) {
        if (form.elements[i].type == "checkbox") {
            if (form.elements[i].name == objName) {
                form.elements[i].checked = false;
            }
        }
    }
}

//删除
function checkDelete(form, objName,cnName) {
    return checkMany(form, "删除",objName,cnName);
}


function checkMany(form, action,objName,cnName) {
    selectStr = "";
    for (var i = 0; i < form.elements.length; i++) {
        if (form.elements[i].type == "checkbox") {
            if (form.elements[i].name == objName && form.elements[i].checked) {
                selectStr = selectStr + form.elements[i].value + ";";
            }
        }
    }
    if (selectStr == ""){
        alert("请选择您要"+action+"的"+cnName+"。");
        return false;
    }
    else {
        if (confirm("确定要"+action+"吗？") == false) {
            return false;
        }
    }
    return true;
}

//修改
function checkUpdate(form, objName,cnName) {
    return checkOne(form,"修改", objName,cnName);
}

//发送配置
function checkSend(form, objName,cnName) {
    return checkOne(form,"配置", objName,cnName);
}

//授权
function checkAuthorize(form, objName,cnName) {
    return checkOne(form,"授权", objName,cnName);
}

//测试
function checkTest(form, objName,cnName) {
    return checkOne(form,"测试", objName,cnName);
}

//查询
function checkQuery(form, objName,cnName) {
    return checkOne(form,"查询", objName,cnName);
}

//查看
function checkView(form, objName,cnName) {
    return checkOne(form,"查看", objName,cnName);
}

//编辑
function checkEdit(form, objName,cnName) {
    return checkOne(form,"编辑", objName,cnName);
}


//审核
function checkReview(form, objName,cnName) {
  return checkOne(form,"审核", objName,cnName);
}

function checkOne(form, action,objName,cnName) {
    selectStr = "";
    var count=0;
    for (var i = 0; i < form.elements.length; i++) {
        if (form.elements[i].type == "checkbox") {
            if (form.elements[i].name == objName && form.elements[i].checked) {
                selectStr = form.elements[i].value;
                count++;
            }
        }
    }
    if (selectStr == ""){
        alert("请选择您要"+action+"的"+cnName+"。");
        return false;
    }
    if(count>1){
        alert(action+"最多只能选择一项。");
        return false;
    }
    return true;
}

//选择一项  lee

	function checkRadio(form, objName,cnName) {
	    selectStr = "";
	    var count=0;
	    for (var i = 0; i < form.elements.length; i++) {
	        if (form.elements[i].type == "checkbox") {
	            if (form.elements[i].name == objName && form.elements[i].checked) {
	                selectStr = form.elements[i].value;
	                count++;
	            }
	        }
	    }
	    if(count>1){
	        alert("最多只能选择一项。");
	        return false;
	    }
	    else if (count==0){
	        alert("请选择一条"+cnName+"记录。");
	        return false;
	    }
	    else if (count==1&&selectStr == ""){
	        alert("您选择的-条\“"+cnName+"\”记录数据为空，请重新选择或联系管理员!");
	        return false;
	    }

	    return true;
	}

 function HTMLEncode2(str)
   {
         var s = "";
         if(str.length == 0) return "";
         s    =    str.replace(/&/g,"&amp;");
         s    =    s.replace(/</g,"&lt;");
         s    =    s.replace(/>/g,"&gt;");
         //s    =    s.replace(/ /g,"&nbsp;");
         s    =    s.replace(/\'/g,"&#39;");
         s    =    s.replace(/\"/g,"&#34;");
         //s    =    s.replace(/\"/g,"&quot;");
         return   s;
   }

 function HTMLDecode2(str)
   {
         var s = "";
         if(str.length == 0)   return "";
         s    =    str.replace(/&amp;/g,"&");
         s    =    s.replace(/&lt;/g,"<");
         s    =    s.replace(/&gt;/g,">");
        // s    =    s.replace(/&nbsp;/g," ");
         s    =    s.replace(/&#39;/g,"\'");
         s    =    s.replace(/&#34;/g,"\"");
         //s    =    s.replace(/&quot;/g,"\"");
         return   s;
   }

function openDiv(TheWidth, TheHeight, url) {
    $("ShowpageContain").style.display = "block";
    var newleft = (document.body.clientWidth - TheWidth) / 2
    var newtop = (document.body.clientHeight - TheHeight) / 2
    $("ShowpageContain").style.left = newleft;
    $("ShowpageContain").style.top = newtop;
    $("ShowpageContain").style.width = TheWidth;
    $("ShowpageContain").style.height = TheHeight;
    $("TheMask").style.display = "block";
    $("TheMask").style.height = document.body.scrollHeight;
    $("TheFramePage").src = url;
}

function hiddenDiv()
{
    parent.$("ShowpageContain").style.display = "none";
    parent.$("TheMask").style.display = "none";
    parent.$("TheFramePage").src = "";
}

//判断输入本文是否越界
function checkTextLength(txtObj,max){
	if(txtObj.value.length>max){
		alert('不能输入超过'+max+'个字！');
		txtObj.value = txtObj.value.substr(0,max);
	}
}

/**
*@desc  根据id获取对象
**/
var $ = function (id) {
	return document.getElementById?document.getElementById(id):null;
};


/**
*@desc 将阿拉伯数字转为中文数字
**/

function getchinese(p)
{
    var input=p;
 if(input=="0")
     return "零";
    else if(input=="1")
     return "壹";
 else if(input=="2")
     return "贰";
 else if(input=="3")
     return "叁";
 else if(input=="4")
     return "肆";
 else if(input=="5")
     return "伍";
 else if(input=="6")
	 return "陆";
 else if(input=="7")
     return "柒";
 else if(input=="8")
     return "捌";
 else if(input=="9")
     return "玖";
}

function transfer2Ch(obj){
	
	var objV = obj.value;
	var minus = "正 ";
	var input= objV;
	if(objV.indexOf('-') == 0){
		input=objV.replace("-", "");
		minus = "负 ";
	}
	
	var danwei=Array("","十","百","千","万","十","百","千","亿","十","百","千");
	var l=input.length;
	var inputvalue=parseInt(input);
	var a=new Array(l);
	var b=new Array(l);
	var result="";
	for(var i=0;i<l;i++){
	  a[i]=input.substr(i,1);
	  b[i]=getchinese(a[i]);
	  result+=b[i]+danwei[l-i-1];
	}
	result = result.replace(/零千|零百|零十/g, "零").replace(/(零)+/g, "零").replace(/零万/g, "万").replace(/零亿/g, "亿").replace(/万亿/,"亿");
	if(result.substr(result.length-1,result.length)=='零'){
		result = result.substr(0,result.length-1);
	}
	return minus+result;
	}


//
///**
//*@desc  弹出框 **********************************************************
function openDialog(title,width,height,url,closetype){
	art.dialog.open(url, {
	    title:title,
	    width:width,
	    height:height,
	    
	    background: '#fff', // 背景色
	    opacity: 0.01,	// 透明度
	    // 在open()方法中，init会等待iframe加载完毕后执行
	    init: function () {
	    	var iframe = this.iframe.contentWindow;
	    },
	    ok: function () {
	    	var iframe = this.iframe.contentWindow;
	    	if (!iframe.document.body) {
	        	alert('iframe还没加载完毕呢');
	        	return false;
	        };
	    	//var form = iframe.document.getElementById(dialogForm); 
	    	return iframe.onCertain();
	    	if(closetype){
	    		return true;
	    	}
	    	
	    },
	    cancel: true
	});
	
}

// jQuery 弹出窗体
function jQueryOpenDialog(title,url,width,height){
	$.box({
		height:height,
		width:width,
		bgvisibel:true,
		title:title,
		iframe:url
	});
}




