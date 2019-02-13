<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>短信发送明细管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#btnMenu").remove();
			
			$("#btnExport").click(function(){
				top.$.jBox.confirm("确认要导出已发短信数据吗？","系统提示",function(v,h,f){
					if(v=="ok"){
						$("#searchForm").attr("action","${ctx}/sms/jmsgSmsSend/userDetailListReport");
						$("#searchForm").submit();
					}
				},{buttonsFocus:1});
				top.$('.jbox-body .jbox-icon').css('top','55px');
			});
		});
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").submit();
        	return false;
        }
		
		function onQuery(){
			var queryType = "${queryType}";
			var url = "";
			if("detail" == queryType){
				url = "${ctx}/sms/jmsgSmsSend/sendResult";
			}else{
				url = "${ctx}/sms/jmsgSmsSend/pendingList";
			}
			$("#searchForm").attr("action",url);
			$("#searchForm").submit();
		}	
		
		function showTestModal(smsContent){
			$('#smsContent').val(smsContent);
			$('#smsContentSize').text(smsContent.length);
			$('#smsContentModal').modal('show');
		}
		
		
		function cDayFunc()
		{
		  	$("#createDatetimeZ").val('');
		  	
		  	var date = new Date();
		  	var iDays = compareDate($("#createDatetimeQ").val(), date);
		  	if (iDays <= 4)
	  		{
		  		var ar=$("#createDatetimeQ").val().split(" ");  
				var arr=ar[0].split("-");  
	  			$("#createDatetimeZ").attr("onclick","WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false,minDate:'"+arr[0]+"-"+arr[1]+"-"+arr[2]+" 00:00:00',maxDate:'"+arr[0]+"-"+arr[1]+"-"+arr[2]+" 23:59:59'});");
	  		}
		  	else
	  		{
	  			//1、获取 createTimeQ 选择的时间 d1
	  			//2、获取当前时间前4天的时间 d2
	  			var d1 = new Date($("#createDatetimeQ").val());
	  			var d2 = new Date(date.getTime() - 24*3600*1000*4);
	  			var lastdate = new Date(d1.getFullYear(),d1.getMonth()+1,0);
	  			//如果两个时间是同一个月，则d2为结束时间
	  			if (d1.getFullYear() == d2.getFullYear() && d1.getMonth() == d2.getMonth())
				{
	  				$("#createDatetimeZ").attr("onclick","WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false,minDate:'#F{$dp.$D(createDatetimeQ);}',maxDate:'"+d2.getFullYear()+"-"+(d2.getMonth() + 1)+"-"+d2.getDate()+" 23:59:59'});");
				}
	  			else
				{
					//不在同一个月，则取d1所在月的最后一天为结束时间
	  				$("#createDatetimeZ").attr("onclick","WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false,minDate:'#F{$dp.$D(createDatetimeQ);}',maxDate:'"+lastdate.getFullYear()+"-"+(lastdate.getMonth() + 1)+"-"+lastdate.getDate()+" 23:59:59'});");
				}
	  		}
		}
		
		//计算两个日期的时间间隔 
	    function compareDate(start,end){ 
			var ar=start.split(" ");  
			var arr=ar[0].split("-");  
	        var starttime=new Date(arr[0],parseInt(arr[1]-1),arr[2]);  
	        var starttimes=starttime.getTime(); 
	        var intervalTime = end-starttimes;
	        //var Inter_Days = ((intervalTime).toFixed(2)/86400000)+1;//加1，是让同一天的两个日期返回一天 
	        
	        var Inter_Days = parseInt(Math.floor(intervalTime) / 1000 / 60 / 60 /24)+1; 
	         
	        return Inter_Days; 
	    } 
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/sms/jmsgSmsSend/resultInit">信息列表</a></li>
		<%-- <li  ${queryType eq 'pending' ? 'class="active"' :''}><a href="${ctx}/sms/jmsgSmsSend/userPendingInit">用户待发送列表</a></li> --%>
	</ul>
	<form:form id="searchForm" modelAttribute="jmsgSmsSend" action="${ctx}/sms/jmsgSmsSend/sendResult" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>批次号：</label>
				<input type="text" name="taskId" maxlength="64" class="input-medium required" value="${jmsgSmsSend.taskId}" id="taskId">
			</li>
			<li><label>手机号码：</label>
				<input type="text" name="phone" maxlength="20" class="input-medium required" value="${jmsgSmsSend.phone}" id="phone">
			</li>
			<li><label>发送结果：</label>
				<form:select path="resultStatus" class="input-medium">
					<form:option value="" label="全部"/>
					<form:option value="1" label="成功"/>
					<form:option value="0" label="失败"/>
					<form:option value="-1" label="未知"/>
				</form:select>
			</li>
			<li><label>提交时间：</label>
				<input name="createDatetimeQ" id="createDatetimeQ" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					value="<fmt:formatDate value="${jmsgSmsSend.createDatetimeQ}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false,onpicked:cDayFunc,maxDate:'%y-%M-%d 23:59:59'});"/>-
				<input name="createDatetimeZ" id="createDatetimeZ" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					value="<fmt:formatDate value="${jmsgSmsSend.createDatetimeZ}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:true});"/>
			</li>			
			<li class="btns">
			<input id="btnSubmit" class="btn btn-primary" type="button" value="查询" onclick="javascript:onQuery();"/>
			<!-- <input id="btnExport" class="btn btn-primary" type="button" value="导出"/> -->
			</li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>手机号码</th>
				<th>短信内容　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　</th>
				<th>计数</th>
				<th>运营商</th>
				<th>省份</th>
				<th>地区</th>
				<th>提交时间　　　　　　</th>
				<th>发送时间　　　　　　</th>
				<th>发送结果</th>
				<th>批次号</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${page.list}" var="jmsgSmsSend">
			<tr>
				<td>
					${jmsgSmsSend.phone}
				</td>	
				<td>
					${fn:substring(jmsgSmsSend.smsContent, 0, 60)}...<!-- <span class="icon-eye-open" data-toggle="tooltip" title="${jmsgSmsSend.smsContent}"></span> -->
					<a href="javascript:showTestModal('${jmsgSmsSend.smsContent}')">[更多查看]</a>
				</td>
				<td>
					${jmsgSmsSend.payCount}
				</td>
				<td>
					${fns:getDictLabel(jmsgSmsSend.phoneType,'phone_type',jmsgSmsSend.phoneType)}
				</td>
				<td>
					${fns:getDictLabel(fn:substring(jmsgSmsSend.areaCode,0,2),'phone_province',jmsgSmsSend.areaCode)}	
				</td>		
				<td>
					<%-- ${jmsgSmsSend.cityName} --%>
					${fns:getCity(jmsgSmsSend.areaCode).phoneCity}
				</td>	
				<td>
					<fmt:formatDate value="${jmsgSmsSend.createDatetime}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
					<fmt:formatDate value="${jmsgSmsSend.sendDatetime}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
				${queryType eq 'pending' ? '待发送':
					fns:getSendResultNew(jmsgSmsSend.payType,jmsgSmsSend.sendStatus,jmsgSmsSend.reportStatus)}
				</td>
				<td>
					${jmsgSmsSend.taskId}
				</td>
			</tr>
			</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
	
	<div class="modal fade" style="display:none;" id="smsContentModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
   		<div class="modal-dialog">
      		<div class="modal-content">
        		<div class="modal-header">
            		<button type="button" class="close" 
               			data-dismiss="modal" aria-hidden="true">
                  		&times;
            		</button>
		            <h4 class="modal-title" id="myModalLabel">
		              	短信内容
		            </h4>
         		</div>
         		<div class="modal-body">
         			<form id="gateWaySendForm" class="form-horizontal">
	                	<div class="control-group">
	                     	<div class="controls1">
	                         	<textarea style="width:498px;" readonly="readonly" name="smsContent" id="smsContent" rows="6" cols="8"></textarea>
	                         	共 <label name="smsContentSize" id="smsContentSize"></label>  个字。
	                     	</div>
	                 	</div>
                 	</form>
         		</div>
      		</div><!-- /.modal-content -->
		</div><!-- /.modal -->
	</div>
</body>
</html>