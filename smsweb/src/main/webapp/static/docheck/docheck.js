/**
 * 
 */
function E(id) {
	return document.getElementById(id);
}

/**
 * 点击tr的任何位置，选中此行
 */
function selectTr(obj, msgId, name){
	var curCheckBox = document.getElementById(msgId);
    if(curCheckBox.checked){
    	curCheckBox.checked = false;
        curCheckBox.parentNode.className = "";
    }else {
    	curCheckBox.checked = true;
        curCheckBox.parentNode.className = "checked";
    }
    changeTrBackGroud(obj, curCheckBox.checked);
    indeterminateChange(name);
}

/**
 * 修改tr的背景颜色 突出显示选中的行
 * @param obj
 */
function changeTrBackGroud(obj, checked){
	/**var allTds=$(obj).children('td');
	var color = "#f9f9f9";
	if(checked){
		color = "rgb(207, 231, 218)";
	}
	for (var i = 0; i < allTds.length; i++) {
		allTds[i].style.backgroundColor = color;
	}**/
}


/**
 * 全选、全不选方法
 */
function doCheckAll(obj,name,checked){
    var checkBoxs = document.getElementsByName(name);
    if(checked){
	    for(var i=0; i<checkBoxs.length; i++){
	        checkBoxs[i].checked = checked;
	        checkBoxs[i].parentNode.className = "checked";
	        changeTrBackGroud(checkBoxs[i].parentNode.parentNode.parentNode.parentNode, checked);
	    }
    }else{
        for(var i=0; i<checkBoxs.length; i++){
        	checkBoxs[i].checked = checked;
            checkBoxs[i].parentNode.className = "";
            changeTrBackGroud(checkBoxs[i].parentNode.parentNode.parentNode.parentNode, checked);
        }    
    }
}

/**
 * 全选、全不选方法
 */
function doCheckAll2(obj,name,checked){
    var checkBoxs = document.getElementsByName(name);
    if(checked){
        for(var i=0; i<checkBoxs.length; i++){
            checkBoxs[i].checked = checked;
            // checkBoxs[i].parentNode.className = "checked";
            changeTrBackGroud(checkBoxs[i].parentNode.parentNode.parentNode.parentNode, checked);
        }
    }else{
        for(var i=0; i<checkBoxs.length; i++){
            checkBoxs[i].checked = checked;
            // checkBoxs[i].parentNode.className = "";
            changeTrBackGroud(checkBoxs[i].parentNode.parentNode.parentNode.parentNode, checked);
        }
    }
}

//全选按钮的半选属性
function indeterminateChange(name){
    var checkNum = 0;//列表中选中按钮的个数
    var checkidxs = document.getElementsByName(name);
    for (var i = 0; i < checkidxs.length; i++) {
        if (checkidxs[i].checked) {
            checkNum++;
        }
    }
    
    var checkall = document.getElementById("checkall");
    
    if(checkNum == 0){//一个都未选
        checkall.indeterminate = false;
        checkall.checked = false;
    }else if(checkNum == checkidxs.length){//全部选中
        checkall.indeterminate = false;
        checkall.checked = true;
    }else{//选中部分
        checkall.indeterminate = true;
    }
}

/**
 * 获取选中行的值
 */
function getCheckboxValue(name){
    var returnVal = "";
    var checkBoxs = document.getElementsByName(name);
    for(var i=0; i<checkBoxs.length; i++){
        if(checkBoxs[i].type=="checkbox" && checkBoxs[i].checked){
            returnVal = returnVal + checkBoxs[i].value + ";";
        }
    }
    if(returnVal.endWith(";")){
        returnVal = returnVal.substring(0, returnVal.length - 1);
    }
    return returnVal;
}

/**
 * 获取选中行的属性值
 */
function getCheckboxPropValue(name, promp, split){
    var returnVal = "";
    var checkBoxs = document.getElementsByName(name);
    for(var i=0; i<checkBoxs.length; i++){
        if(checkBoxs[i].type=="checkbox" && checkBoxs[i].checked){
        	if(promp == "id"){
        		returnVal = returnVal + checkBoxs[i].id + split;
        	}else if(promp == "value"){
        		returnVal = returnVal + checkBoxs[i].value + split;
        	}else if(promp == "title"){
        		returnVal = returnVal + checkBoxs[i].title + split;
        	}else {
        	
        	}
        }
    }
    if(returnVal.endWith(",")){
        returnVal = returnVal.substring(0, returnVal.length - 1);
    }
    return returnVal;
}

/**
 * 获取选中行的值
 */
function getValueByName(name){
    var returnVal = "";
    var allInputs = document.getElementsByName(name);
    for(var i=0; i<allInputs.length; i++){
        if(allInputs[i].type=="text"){
            returnVal = returnVal + allInputs[i].value + ";";
        }
    }
    if(returnVal.endWith(";")){
        returnVal = returnVal.substring(0, returnVal.length - 1);
    }
    return returnVal;
}

//判断字符串是否以Str结尾
String.prototype.endWith = function(str){
    if(str == null || str == "" || this.length == 0 || str.length > this.length)
        return false;
    if(this.substring(this.length - str.length) == str)
        return true;
    else
        return false;
    return true;
};