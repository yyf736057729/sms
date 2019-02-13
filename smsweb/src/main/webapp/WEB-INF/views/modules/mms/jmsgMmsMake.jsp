<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
	<link href="${ctxStatic }/mms/css/mmsedit_style.css" rel="stylesheet" type="text/css"/>
	<link href="${ctxStatic }/mms/css/mmsedit_table.css" rel="stylesheet" type="text/css" />
	<link href="${ctxStatic }/mms/css/jquery-webox.css" rel="stylesheet" type="text/css"/>
	<link href="${ctxStatic }/mms/css/oper_area.css" rel="stylesheet" type="text/css" />
	<script type="text/javascript" src="${ctxStatic }/mms/js/common.js"></script>
	<script type="text/javascript" src="${ctxStatic }/mms/js/check.js"></script>
	<script type="text/javascript" src="${ctxStatic }/mms/js/formTools.js"></script>
	<script type="text/javascript" src="${ctxStatic }/mms/js/jquery-1.7.2.min.js"></script>
	<script type="text/javascript" src="${ctxStatic }/mms/js/jquery-webox.js"></script>
	<script type="text/javascript" src="${ctxStatic }/mms/js/mmsedit_checkinput.js"></script>
	<style type="text/css">
 		img {
 		max-width: 200px; 
 		width:expression(this.width > 200 ? "200px" : (this.width+"px")); 
 		height:auto; 
 		} 
	</style>
	<script type="text/javascript"> 
	var imageBaseSrc = "${ctxStatic }/mms";
function verify(procType)
{
	var MMS_TITLE=document.getElementById('MMS_TITLE').value;
	var MMS_REMARK=document.getElementById('MMS_REMARK').value;
	
	/* if(document.getElementById('photoway1').value.length<1){
		alert("请往第一帧中插入图片!");
		return false;
	} */
	
	
	if (MMS_TITLE==undefined || MMS_TITLE=='') {
		alert("彩信主题不能为空！");
	    return false;
	}
	else if(MMS_TITLE.length>20){
		alert("彩信主题的长度不能大于20!");
		return false;
	}
	else if(MMS_REMARK.length>200){
		alert("备注的长度不能大于200!");
		return false;
	}
	else {
		document.forms[0].submit();
	}
}
function gen_base64(idx) {
	var file = document.getElementById('imageupload'+idx).files[0];
	if(!/image\/\w+/.test(file.type)){   
		alert("请确保文件为图像类型"); 
		return false; 
	}
	r = new FileReader();  //本地预览
	r.onload = function(){
		//var photosize = Math.round(r.result.length/1024*1000)/1000 ;
		var photosize = Math.round(file.size/1024*1000)/1000 ;
		var flag=judgeSize(photosize,'photosize'+idx);
		if(flag==false){
			objFile = document.getElementById('imageupload'+idx);
			objFile.outerHTML=objFile.outerHTML.replace(/(value=\").+\"/i,"$1\"");
			return;
		}
		
		document.getElementById('photoname'+idx).value = r.result;
		document.getElementById('photosize'+idx).value = photosize;
		updateImg(r.result, idx);
		
		setParentSize();
		document.getElementById('delphoto'+idx).style.display='';
	}
	r.readAsDataURL(file);    //Base64
}
</script>
</head>
 
<body>	
<script type="text/javascript">top.$.jBox.closeTip();</script>
<c:if test="${not empty message}">
	<c:if test="${not empty type}"><c:set var="ctype" value="${type}"/></c:if><c:if test="${empty type}"><c:set var="ctype" value="${fn:indexOf(message,'失败') eq -1?'success':'error'}"/></c:if>
	<script type="text/javascript">top.$.jBox.tip("${message}","${ctype}",{persistent:true,opacity:0});</script>
</c:if>
<table width="99%" border="0" cellspacing="0" cellpadding="0">
  <tr>
	<td valign="top" id="contentTD">
		<table width="100%" border="0" cellspacing="0" cellpadding="2" >
			 <tr>
				<td width="75%" height="100%" valign="top">
					  <form:form id="inputForm" modelAttribute="jmsgMmsData" action="${ctx}/mms/jmsgMmsData/makesave" method="post" class="form-horizontal">
					  <form:hidden path="id" id="id"/>
					  <input type="hidden" name="mmssize" id="mmssize"/>
						      <table width="100%" border="0" cellpadding="2" cellspacing="0" class="tb-list" id="MM_MMSLIB_CHARGE">
						      	   <!-- 主表信息 -->
							       <tr>
							           <td colspan="2" class="td-bg" id="divContentListMM_MMSLIB_CHARGE">
										    <table width="100%" border="0" cellspacing="1" cellpadding="1">
												<tr>
												   <td width="10%" class="td-left">彩信主题<span class="must">＊</span></td>
												   <td class="td-right" colspan="5">
												   		<!-- 彩信主题 -->		
												   		<form:input path="mmsTitle" htmlEscape="false" maxlength="20" id="MMS_TITLE" size="50"/><span class="must">&nbsp;&nbsp;最大支持20个字（必填)</span>
												    </td>
												</tr>
												<tr>
													<td width="10%" class="td-left">备注</td>
													<td class="td-right" colspan="5">
														<form:input path="remark" htmlEscape="false" maxlength="15" id="MMS_REMARK" size="50"/><span class="must">&nbsp;&nbsp;最大支持15个字（选填）</span>
													</td>
												 </tr> 
										    </table>
									    </td>
								    </tr>
						        </table>
								<table width="100%" border="0" cellpadding="2" cellspacing="0" class="tb-list" id="MM_MMSLIB_CHARGE_FRAME">
									    <tr>
											<th width="100%" class="td-left">彩信内容<span class="must">＊</span></th>
											<th align="right" class="cur-hand"  >
											</th>
										</tr>
										<tr id="divContentListMM_MMSLIB_CHARGE_FRAME">
											  <td class="td-bg" colspan="2" style="padding: 5px">
												    <table width="100%" border="0" cellpadding="1" cellspacing="1" class="dtable" id="MM_MMSLIB_CHARGE_FRAME_DATA">
													   <tr align="center">
													     <td width="16">
															<!--复选框  -->												     	
															<input name="dtRowID" type="checkbox" class="noborder" value="1"/></td>
														 <td width="20" id="rowNo1"><input type="hidden" name="sorts" value="1"/>1</td>
														 <td>
															  <table id="MM_MMSLIB_CHARGE_FRAME_DATA_ROW" width="100%" border="0" cellspacing="1" cellpadding="1" class="tb-list">
																  <tr>
																    <td class="td-left" width="5%"  nowrap="nowrap">图片<span class="must">（jpg或gif）</span></td>
																    <td class="td-right" width="85%" nowrap="nowrap">
																		<!--图片  -->																 
																		<input type="hidden" name="photoname" id="photoname1" />
																		<input type="hidden" name="photosize" id="photosize1" value="0"/>
																	    <input id="imageupload1" type="file" style="width:60;" accept="image/*" onchange="gen_base64(1);"/>&nbsp;&nbsp;<a href="javascript:void(0);" id="delphoto1" style="display: none" onclick="delElement(1,1)">删除</a>
																	</td>
																	<!-- <td width="5%" class="td-left" nowrap="nowrap">音乐</td>
																	<td class="td-right" width="35%" nowrap="nowrap">
																		音乐 																		
																		<input type="hidden" name="musicname" id="musicname1"/>
																		<input type="hidden" name="musicsize" id="musicsize1" value="0" />
																	    <input id="musciupload1" type="file" style="width:60;" accept="image/*" onchange="gen_base64(1);" /><a href="javascript:void(0);" id="delmusic1" style="display: none" onclick="delElement(1,2)">删除</a>
																	</td> -->
																</tr>
																<tr>
																	<td width="5%" class="td-left">文本内容</td>
																	<td class="td-right" width="85%">
																		<!--文本内容  -->																		
																		<textarea name="txtcontent" id="txtcontent1" rows="5" class="wid90" onkeyup="setFrameText(this.value,1);getSizeHtml();"></textarea>
																	</td>
																</tr>
															  </table>
											   		       </td>
													    </tr>
								                     </table>
					
													<table width="100%" border="0" cellspacing="0" cellpadding="2">
														<tr>
															<td height="30">
																<!--全选，全不选  -->													
																<input name="button2" type="button" class="but" onmouseover="this.className='but_mouseover'" onmouseout="this.className='but_mouseout'" onclick="checkAllRow(1)" value="全选"/>
																<input name="button2" type="button" class="but" onmouseover="this.className='but_mouseover'" onmouseout="this.className='but_mouseout'" onclick="checkAllRow(2)" value="反选"/>
																<input name="button2" type="button" class="but" onmouseover="this.className='but_mouseover'" onmouseout="this.className='but_mouseout'" onclick="checkAllRow(0)" value="全不选"/>
																<input name="button2" type="button" class="but" onmouseover="this.className='but_mouseover'" onmouseout="this.className='but_mouseout'" onclick="DeleteFrame(1);deleteRow(1);" value="删除"/>
															</td>
															<td align="right">
																<!--新建帧-->																
																<input name="button" type="button" class="but" onmouseover="this.className='but_mouseover'" onmouseout="this.className='but_mouseout'" onclick="insertNewRow();InsertFrame();" value="新建帧"/>
															</td>
														</tr>
														<tr>
															<td colspan="2" class="line99"></td>
														</tr>
													</table>
															
													<table width="100%" border="0" cellspacing="0" cellpadding="2">
														<tr>
																<td height="40"><span class="must">＊</span>为必须填写的项目</td>
																<td align="right">
																		<!--保存-->															    
																		<input type="button" class="but1" style="margin-left:150px;" onmouseover="this.className='but1_mouseover'" onmouseout="this.className='but1_mouseout'"  value="保存" onclick="verify(&quot;Save&quot;)"/>
																</td>
													   </tr>
													</table>
											</td>
						     			</tr>
						   		</table>
				         </form:form>
					</td>
					<td width="24%" id="MessageObjectTD" valign="top">
                        <!-- asdfasdf -->
						<div id="MessageObject_DIV" style="width:280px; border:0px solid #A4B6D7; background-color:#E5F1FF">
							<table id="MessageObject_HEAD" width="100%" cellspacing="1" cellpadding="0" style="border: 1px solid #A4B6D7; background-color:#FFFFFF">
								<tr>
									<td width="50%" height="14"><img src="${ctxStatic }/mms/images/signal.gif"/></td>
									<td width="50%" align="right"><img src="${ctxStatic }/mms/images/coulom.gif"/></td>
								</tr>
								<tr>
								    <td width="100%" height="24" colspan="2" align="center" valign="middle" style="font-size:9pt;">
										
										<div  style="background-color:#FF8C32; border:2px">
										<font color="#FFFFFF">
										<b style="display:block; background: #FFFFFF">
										<b style="display:block; height: 1px; overflow: hidden; background: #FF8C32; margin: 0 2px"></b>
										<b style="display:block; height: 1px; overflow: hidden; background: #FF8C32; margin: 0 1px"></b>
										</b>
										</font>
										<div id="MessageObject_TITLE"></div>
										<font color="#FFFFFF">
										<b style="display:block; background: #FFFFFF">
										<b style="display:block; height: 1px; overflow: hidden; background: #FF8C32; margin: 0 1px"></b>
										<b style="display:block; height: 1px; overflow: hidden; background: #FF8C32; margin: 0 2px"></b>
										</b>
										</font>
										</div>
										
									</td>
								</tr>
						    </table>
						<div id="MessageObject_FRAMES" align="center" 
							 style="width:100%; border:1px solid #A4B6D7; overflow:auto; height:450px; background-color:#E5F1FF">
								<div id="MessageObject_FRAME_1" style="width:100%;" order="1">
									<div id="SwapContent1" style="width:80%;" align="center">
										<br/>
										<input id="FrameId" type="hidden" value=""/>
										<div id="Image" size="" path="" type="hidden"></div>
										<div id="Text" size="" align="left" type="hidden"></div>
										<div id="Audio" size="" path="" type="hidden"></div>
									</div>
									<div id="Foot" style="width:100%; height:21px; padding-top:4px; background-color:#2C6DBF;font-size:9pt;">
										<font color="#BEF170">第&nbsp;<span id="Order">1</span>&nbsp;帧</font>&nbsp;
										<%-- <a onclick="up(1);" style="cursor:pointer">
											<img src="${ctxStatic }/mms/images/arrow_up.gif"/><font color="#FFFFFF">上移</font>
										</a>
										<a onclick="down(1);" style="cursor:pointer">
											<img src="${ctxStatic }/mms/images/arrow_down.gif"/><font color="#FFFFFF">下移</font>
										</a>&nbsp; --%>
									</div>
								</div>
								<div id="MessageObject_FRAME_NEW" order="-1"></div>
						</div>
						<div style="border:1px solid #A4B6D7;border-width:0px 1px;text-align:right;padding-right:15px;"></div>
						<div id="MessageObject_FOOT" align="center" style="width:100%; height:24px; padding-top:5px; font-size:9pt; border:1px solid #A4B6D7; background-color:#2B6DC3; overflow :hidden; text-overflow:ellipsis">
								<NOBR>
									<font color="#FFFFFF"><div id="MessageObject_SIZE">当前:0.00K 剩余可编辑:100.00K</div></font>
								</NOBR>
						    </div>
						</div>
                    <!-- asdf -->
					</td>
	      		</tr>
	   		</table>
     	</td>
  	</tr>
</table>
      
</body>
</html>