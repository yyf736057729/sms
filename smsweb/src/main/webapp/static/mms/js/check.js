var blnIsCorrect=true; //检查值有效性标志量，初始值为真（有效）
var strError='';       //当发生错误时显示的错误提示信息
var objItem;           //当发生错误时，将焦点定位到哪一个表单元素
var web_requestSubmitted=false;//页面是否已经提交

//返回检查标志量，如果标志量为否，则显示错误警告，并将光标定位在出错的文本框内
function checkAll(){

    if (!blnIsCorrect){
        alert(strError);
        try{
            objItem.focus();
        }
        catch(e){
        }
        blnIsCorrect=true; //返回否之前重置标志量为真
        return false;
    }
    else
        return true;
}

//非空
function isNotEmpty(txtObj,message){
    if (blnIsCorrect){
        //如果txtObj是文本框或下拉列表框(value属性不为空)则判断txtObj的value是否为空,
        if (txtObj.value!=null){
            trimThis(txtObj);
            if (txtObj.value.length==0){
                strError=(message==null?'该值不能为空.':message);
                blnIsCorrect=false;
                if (txtObj.style.display=="none")
                    objItem=txtObj.previousSibling;
                else
                    objItem=txtObj;
            }
        }
        //否则txtObj是复选框或单选框,则判断txtObj每一个元素是否选中
        else{
            blnIsCorrect=false;
            for (i=0;i<txtObj.length && !blnIsCorrect;i++){
                if (txtObj[i].checked)
                    blnIsCorrect=true;
            }
            if (!blnIsCorrect){
                strError=(message==null?'该值不能为空.':message);
                objItem=txtObj[0];
            }
        }
    }
}
//去一个表单元素值的首尾空格
function trimThis(objThis){
    try{
        objThis.value=objThis.value.replace(/(^\s*)|(\s*$)/g,"");
    }
    catch(e){
    }
}
//长度不符合
function checkLength(txtObj1,max,message){
	if (blnIsCorrect && txtObj1.value.length!=0){
		var len = txtObj1.value.length;
		if (len>max){
			if (message!=null)
			  strError=message;
	        else{
		      strError='该项长度不超过' + max + '个字符.';
		    }    
			txtObj1.value = txtObj1.value.substring(0,max);
			blnIsCorrect=false;
	        objItem=txtObj1;
		}
	}
}
//是否是基本ASCII字符(ASCII字符集前127位可见字符)
function isASCII(txtObj,message){
    trimThis(txtObj);
    if (blnIsCorrect && txtObj.value.length!=0){
        var strCheck=/^[!-\x7f]*$/;
        if (!strCheck.exec(txtObj.value)){
            if (message!=null)
                strError=message;
            else
                strError='半角字符格式不正确.';
            blnIsCorrect=false;
            objItem=txtObj;
        }
    }
}

//是否是合法的标识符(以字母开头，字母、数字、下划线的组合)
function isIdentifier(txtObj){
    if (blnIsCorrect && txtObj.value.length!=0){
        var strCheck=/^[a-zA-Z]+(_{0,1}[a-zA-Z0-9])*$/;
        if (!strCheck.exec(txtObj.value)){
            if (message!=null)
                strError=message;
            else
                strError='标识符格式不正确.';
            blnIsCorrect=false;
            objItem=txtObj;
        }
    }
}
//两次输入的密码是否相等
function isEquals(txtObj1,txtObj2){
    if (blnIsCorrect&&(txtObj1.value.length!=0 ||txtObj2.value.length!=0)){
        trimThis(txtObj1);
        trimThis(txtObj2);
        if (txtObj1.value!=txtObj2.value){
            strError='两次输入的密码不一致.';
            blnIsCorrect=false;
            objItem=txtObj1;
        }
    }
}

//两次输入的密码是否相等
function isEqualsOld(txtObj1,txtObj2){
    if (blnIsCorrect&&(txtObj1.value.length!=0 ||txtObj2.value.length!=0)){
        trimThis(txtObj1);
        trimThis(txtObj2);
        if (txtObj1.value!=txtObj2.value){
            strError='输入的旧密码不正确.';
            blnIsCorrect=false;
            objItem=txtObj1;
        }
    }
}
function isEqualsNew(txtObj1,txtObj2){
    if (blnIsCorrect&&(txtObj1.value.length!=0 ||txtObj2.value.length!=0)){
        trimThis(txtObj1);
        trimThis(txtObj2);
        if (txtObj1.value!=txtObj2.value){
            strError='新密码与确认密码不一致.';
            blnIsCorrect=false;
            objItem=txtObj1;
        }
    }
}

function isEqualsRand(txtObj1,txtObj2){
    if (blnIsCorrect&&(txtObj1.value.length!=0 ||txtObj2.value.length!=0)){
        trimThis(txtObj1);
        trimThis(txtObj2);
        if (txtObj1.value!=txtObj2.value){
            strError='校验码不正确.';
            blnIsCorrect=false;
            objItem=txtObj1;
        }
    }
}
//是否是合法的E_mail地址(以字母开头，字母、数字、下划线的组合，中间必须有一个“@”)
function isEmail(txtObj,message){
    trimThis(txtObj);
    if (blnIsCorrect && txtObj.value.length!=0){
        var strCheck=/^\S+@\S+\.\S+$/;
        if (!strCheck.exec(txtObj.value))    {
            strError='电子邮件格式不正确.';
            blnIsCorrect=false;
            objItem=txtObj;
        }
    }
}
//判断是否是合法的身份证号 ADD BY SRH 20060822
function checkID(txtObj){
 if (blnIsCorrect && txtObj.value.length!=0){
    if(!(/^\d{15}$|^\d{18}$|^\d{17}x$/.test(txtObj.value))){
		strError='请输入15位或18位身份证号';
		 blnIsCorrect=false;
            objItem=txtObj;
	}
  }
}
//是否是邮编
function isPostCode(txtObj,message){
    if (blnIsCorrect && txtObj.value.length!=0){
        var strCheck=/^\d{6}$/;
        if (!strCheck.exec(txtObj.value))
        {
			if (message!=null)
                strError=message;
            else
            strError='邮编格式不正确,请重新输入长度为6位的号码.';
            blnIsCorrect=false;
            objItem=txtObj;
        }
    }
}
//是否是正整数
function isInt(txtObj,message){
    trimThis(txtObj);
    if (blnIsCorrect && txtObj.value.length!=0){
        var strCheck=/^\d+$/;
        if (!strCheck.exec(txtObj.value)){
            if (message!=null)
                strError=message;
            else
                strError='数值格式不正确.';
            blnIsCorrect=false;
            objItem=txtObj;
        }
    }
}
//是否为正实数
function isFloat(txtObj,message){
    if (blnIsCorrect && txtObj.value.length!=0){
        var strCheck=/^\d+\.{0,1}\d*$/;
        if (!strCheck.exec(txtObj.value))
        {
            if (message!=null)
                strError=message;
            else
                strError='数值格式不正确！';
            blnIsCorrect=false;
            objItem=txtObj;
        }
    }
}
//区间数值
function rangeLimit(txtObj,min,max,message){
 isNotEmpty(txtObj,message);     
 if (blnIsCorrect){
   if (message!=null)
     strError=message;
   else
     strError='数值不在指定区间里['+min+','+max+']！';
   var number = new Number(txtObj.value); 
   if(isNaN(number)) 
   {
    blnIsCorrect=false;
    objItem=txtObj;
   }else{
	max = max || Number.MAX_VALUE;
	blnIsCorrect = (min <= number && number <= max);
    objItem=txtObj;
  }	
 }   
}
//是否是电话号码
function isTel(txtObj,message){
    if (blnIsCorrect && txtObj.value.length!=0){
        var strCheck=/^((\(\d{2,3}\))|(\d{3}\-))?(\(0\d{2,3}\)|0\d{2,3}-)?[1-9]\d{6,8}(\-\d{1,5})?$/;
        if (!strCheck.exec(txtObj.value)){
            if (message!=null)
                strError=message;
            else
                strError='电话号码格式不正确.';
            blnIsCorrect=false;
            objItem=txtObj;
        }
    }
}
//是否是移动电话号码
function isMobile(txtObj,message){
    if (blnIsCorrect && txtObj.value.length!=0){
        var strCheck=/^((\(\d{2,3}\))|(\d{3}\-))?(13\d{9}|15\d{9}|18\d{9})$/;
        if (!strCheck.exec(txtObj.value)){
            if (message!=null)
                strError=message;
            else
                strError='手机号码格式不正确.';
            blnIsCorrect=false;
            objItem=txtObj;
        }
    }
}

//是否有效URL	
function isURL(txtObj,message){
    if (blnIsCorrect && txtObj.value.length!=0){
        var strCheck= /^http:\/\/[A-Za-z0-9]+\.[A-Za-z0-9]+[\/=\?%\-&_~`@[\]\':+!]*([^<>\"\"])*$/;
        if (!strCheck.exec(txtObj.value)){
            if (message!=null)
                strError=message;
            else
                strError='不是有效的URL,请重新输入.';
            blnIsCorrect=false;
            objItem=txtObj;
        }
    }
}

function textCounter(theField,theCharCounter,theLineCounter,maxChars,maxLines,maxPerLine)

{

	var strTemp = "";

	var strLineCounter = 0;

	var strCharCounter = 0;

	

	for (var i = 0; i < theField.value.length; i++)

	{

		var strChar = theField.value.substring(i, i + 1);

		

		if (strChar == '\n')

		{

			strTemp += strChar;

			strCharCounter = 1;

			strLineCounter += 1;

		}
		else if (strCharCounter == maxPerLine)
		{
			strTemp += '\n' + strChar;

			strCharCounter = 1;

			strLineCounter += 1;

		}

		else

		{

			strTemp += strChar;

			strCharCounter ++;

		}

	}

	

	theCharCounter.value = maxChars - strTemp.length;

	theLineCounter.value = maxLines - strLineCounter;
//alert(theCharCounter.value);
	if (theCharCounter.value<0){
		alert("文本字必须在500汉字或1000英文字以内(包括空格和空行)，超出限定将不能再输入数据！");
		theField.value=theField.value.substring(0,1000);
	}	
}

			
function vdf() {var i,p,q,nm,t,num,min,max,e='',a=vdf.arguments;for (i=0; i<(a.length-2); i+=3) {t=a[i+2];val=fob(a[i]);val1=val.value;if (val) {nm=a[i+1];if ((val=val.value)!="") {if (t.indexOf('pwd') != -1) {val2=fob(t.substring(t.indexOf(':')+1));if (val1!=val2.value) e+='两次密码输入不相同.\n'; else if (val2.value.length<3) e+='密码长度应大于等于3个字符.\n';} if (t.indexOf('len') != -1) {p=t.indexOf(':');l=t.substring(p+1);if (val1.length<l) e+=nm+'-长度应大于等于'+l+'个字符.\n';} if (t.indexOf('sel') != -1) {p=t.indexOf(':');l=t.substring(p+1);if (val1 == l) e+='请输入-'+nm+'\n';} if (t.indexOf('mail')!=-1) { p=val.indexOf('@'); if (p<1 || p==(val.length-1)) e+=nm+'-邮件地址无效.\n';} if (t.indexOf('num')!=-1) { num = parseFloat(val); if (val!=''+num) e+=nm+'-必须是数字.\n';} if (t.indexOf('inRange') != -1) { num = parseFloat(val);p=t.indexOf(':');min=t.substring(8,p);max=t.substring(p+1);if (num<min || max<num || val!=''+num) e+=nm+'-信息不合理 应在'+min+'与'+max+'之间.\n'; }} else {if (t.charAt(0) == 'R') e += nm+' 是必须的.\n'; }}} if (e) alert('请填写正确数据，下列信息输入错误:\n'+e);document.vdfValue=(e=='');return(e=='');}


//opt1 小数 opt2 负数 
//当opt2为1时检查num是否是负数 
//当opt1为1时检查num是否是小数 
//返回1是正确的，0是错误的 
function chknbr(num,opt1,opt2) 
{ 
var i=num.length; 
var staus; 
//staus用于记录.的个数 
status=0; 
if ((opt2!=1) && (num.charAt(0)=='-')) 
{ 
//alert("You have enter a invalid number."); 
return 0; 

} 
//当最后一位为.时出错 
if (num.charAt(i-1)=='.') 
{ 
//alert("You have enter a invalid number."); 
return 0; 
} 

for (j=0;j<i;j++) 
{ 
if (num.charAt(j)=='.') 
{ 
status++; 
} 
if (status>1) 
{ 
//alert("You have enter a invalid number."); 
return 0; 
} 
if (num.charAt(j)<'0' || num.charAt(j)>'9' ) 
{ 
if (((opt1==0) || (num.charAt(j)!='.')) && (j!=0)) 
{ 
//alert("You have enter a invalid number."); 
return 0; 
} 
} 
} 
return 1; 
} 

function checkfloat(num1,num2,num3) {//num1:输入框值；num2：小数点位数；num3：控件名称
if (num1.indexOf('.')!=-1){
	ll=num1.indexOf('.');
	if (num2>=(num1.substring(ll+1).length)){
		blnIsCorrect= true;
	}else{
		//alert("错误,"+num3+"只允许有"+num2+"位小数!");		
		strError="错误,"+num3+"只允许有"+num2+"位小数!";
		blnIsCorrect=false;
	}
}else{
	blnIsCorrect= true;
}
}


/*
IsNumber: 用于判断一个数字型字符串是否为数值型， 
  还可判断是否是正数或负数，返回值为true或false 
string: 需要判断的字符串 
sign: 若要判断是正负数是使用，是正用'+'，负'-'，不用则表示不作判断 
*/

function isNumber(string,sign) 
{ 
  var number; 
  if (string==null) return false; 
  if ((sign!=null) && (sign!='-') && (sign!='+')) 
  { 
   alert('IsNumber(string,sign)的参数出错：nsign为null或"-"或"+"'); 
   return false; 
  } 
  number = new Number(string); 
  if (isNaN(number)) 
  { 
   return false; 
  } 
  else if ((sign==null) || (sign=='-' && number<0) || (sign=='+' && number>0)) 
  { 
   return true; 
  } 
  else 
   return false; 
} 
/*判断页面是否已经提交过


*/
function web_WebInputForm_onSubmit(form){
	if(web_checkSubmit()){
		return true;
	}
	return false;
}
function web_checkSubmit(){
	if(web_requestSubmitted == true){
		alert("系统已经在查询，请耐心等候服务器应答……");
		return false;
	}else{
		web_requestSubmitted=true;
		return true;
	}
}
