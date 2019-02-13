<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>短信发送明细</title>
	<meta name="decorator" content="default"/>
	<script src="${ctxStatic}/docheck/docheck.js" type="text/javascript"></script>
	<script type="text/javascript">
		var curDate = new Date();
		$(document).ready(function() {
			$("#btnExport").click(function(){
				top.$.jBox.confirm("确认要导出下行短信明细数据吗？","系统提示",function(v,h,f){
					if(v=="ok"){
						$("#searchForm").attr("action","${ctx}/sms/jmsgSmsSend/detailListReport");
						$("#searchForm").submit();
					}
				},{buttonsFocus:1});
				top.$('.jbox-body .jbox-icon').css('top','55px');
			});
			
			$("#btnPush").click(function(){
				var ids = getCheckboxValue("id");
				if(!ids){
					alertx("请选择需要推送的短信！");
				}else{
					$.ajax({  
				        type: "post",  
				        url: "${ctx}/sms/jmsgSmsSend/batchPush?ids="+ids,
				        dataType: 'json',  
				        contentType: "application/x-www-form-urlencoded; charset=utf-8",  
						success : function(result) {
							alertx(result);
							//取消全选   
							$(":checkbox").removeAttr("checked"); 
						}
				    });
				}
			});
			
			$("#btnTaskPush").click(function(){
				$("#tableNameModal").modal('show');
			});
		});
		
		function onCreateTask(){
			
			var taskName = $("#taskName").val();
			if(taskName == null || taskName ==''){
				alertx("请输入任务名称");
				return;
			}
			
			taskName = encodeURI(encodeURI(taskName));
			
			$.ajax({  
		        type: "post",  
		        url: "${ctx}/sms/jmsgSmsSend/taskPush?taskName="+taskName,
		        data:$('#searchForm').serialize(),
		        dataType: 'json',  
		        contentType: "application/x-www-form-urlencoded; charset=utf-8",  
				success : function(result) {
					alertx(result);
					//取消全选   
					$(":checkbox").removeAttr("checked"); 
				}
		    });
			
			$("#tableNameModal").modal('hide');
		}
		
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").submit();
        	return false;
        }
		function onQuery(){
			/* if ($("#createDatetimeQ").val())
			{
				var start=$("#createDatetimeQ").val().substring(0,10);
				start=start.replace(/-/g,"/");
				var startdate=new Date(start);
				var time=curDate.getTime()-startdate.getTime();
				var days=parseInt(time/(1000 * 60 * 60 * 24));
				if (days > 3)
				{
					alert("只能查看前 3 天的数据！");
					return false;
				}
			} */
			$("#searchForm").submit();
		}
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").attr("action","${ctx}/sms/jmsgSmsSend/detailList");
			$("#searchForm").submit();
        	return false;
        }
		function onView(taskid,id,createDatetime){
			var url = "${ctx}/sms/jmsgSmsSend/detailView?taskid="+taskid+"&bizid="+id+"&createDatetime="+createDatetime;
			windowOpen(url,taskid,900,550);
		}
		function showAlert(content){
			alert(content);
		}
		
		function showTestModal(smsContent){
			$('#smsContent').val(smsContent);
			$('#smsContentSize').text(smsContent.length);
			$('#smsContentModal').modal('show');
		}
		
		function testGateway(userId,phone,smsContent){
			
			$.ajax({  
		        type: "post",  
		        url: "${ctx}/sms/jmsgSmsSend/testGateway?userId="+userId+"&phone="+phone+"&smsContent="+smsContent,
		        dataType: 'json',  
		        contentType: "application/x-www-form-urlencoded; charset=utf-8",  
				success : function(result) {
					alertx("正在匹配通道，请稍后点击【获取结果】查看匹配详情");
					$('#testModal').modal('show');
					$('#result').empty();
					$('#result').html("正在匹配通道,请稍后...");
				},
				error : function(){
					alertx("系统错误,测试匹配通道失败");
				}
		    });
		}
		
		function gatewayResult(){
			$.ajax({  
		        type: "post",  
		        url: "${ctx}/sms/jmsgSmsSend/gatewayResult",
		        dataType: 'json',  
		        contentType: "application/x-www-form-urlencoded; charset=utf-8",  
				success : function(result) {
					$('#result').empty();
					$('#result').html(result);
				},
				error : function(){
					alertx("系统错误,获取匹配通道结果失败");
				}
		    });
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
		<li class="active"><a href="${ctx}/sms/jmsgSmsSend/detailInit">信息列表</a></li>
		<shiro:hasPermission name="jmsg:admin">
		<li><a href="${ctx}/sms/jmsgSmsSend/errorDetailInit">发送失败短信记录</a></li>
		</shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="jmsgSmsSend" action="${ctx}/sms/jmsgSmsSend/detailList" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li><label>手机号码：</label>
				<form:input path="phone" maxlength="20" class="input-medium" id="phone"/>
			</li>
			<li><label>批次号：</label>
				<form:input path="taskId" maxlength="64" class="input-medium" id="taskId"/>
			</li>
			<li><label>短信ID：</label>
				<form:input path="id" maxlength="64" class="input-medium" id="id"/>
			</li>
			<shiro:hasPermission name="jmsg:admin">
			<li><label>用户名称：</label>
				<sys:treeselect id="user" name="user.id" value="${jmsgSmsSend.user.id}" labelName="user.name" labelValue="${jmsgSmsSend.user.name}"
					title="用户" url="/sys/office/treeData?type=3" cssClass="input-small" allowClear="true" notAllowSelectParent="true"/>
			</li>
			</shiro:hasPermission>
			<li><label>运营商：</label>
				<form:select path="phoneType" class="input-medium">
					<form:option value="" label="全部"/>
					<form:options items="${fns:getDictList('phone_type')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
			</li>
			<li><label>发送状态：</label>
				<form:select path="sendStatus" class="input-small">
					<form:option value="" label="全部"/>
					<form:option value="P" label="待发送"/>
					<form:option value="T0" label="发送成功"/>
					<form:option value="F" label="发送失败"/>
				</form:select>
			</li>
			<li><label>状态报告：</label>
				<form:select path="reportStatus" class="input-small">
					<form:option value="" label="全部"/>
					<form:option value="P" label="状态空"/>
					<form:option value="T" label="成功"/>
					<form:option value="F" label="失败"/>
				</form:select>
			</li>
			<li><label>回执状态：</label>
				<form:input path="hzdm" maxlength="64" class="input-medium"/>
			</li>
			<shiro:hasPermission name="jmsg:admin">
			<li><label>通道：</label>
				<form:select path="channelCode" class="input-xlarge">
					<form:option value="" label="请选择"/>
					<form:options items="${fns:getGatewayList()}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
			</li>
			<li><label>推送状态：</label>
				<form:select path="pushFlag" class="input-medium">
					<form:option value="">全部</form:option>
					<form:option value="9">待推</form:option>
				</form:select>
			</li>
			</shiro:hasPermission>		
			<shiro:lacksPermission name="jmsg:admin">
			<li><label>通道：</label>
				<form:select path="channelCode" class="input-xlarge">
					<form:options items="${fns:getDictList('gateway_list')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
			</li>
			</shiro:lacksPermission>
			<li><label>日期：</label>
				<input name="createDatetimeQ" id="createDatetimeQ" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					value="<fmt:formatDate value="${jmsgSmsSend.createDatetimeQ}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false,onpicked:cDayFunc,maxDate:'%y-%M-%d 23:59:59'});"/>
					--
				<input name="createDatetimeZ" id="createDatetimeZ" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					value="<fmt:formatDate value="${jmsgSmsSend.createDatetimeZ}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false});"/>
			</li>
			<li class="btns">
				<input id="btnSubmit" class="btn btn-primary" type="button" value="查询" onclick="return page();"/>
				<!-- <input id="btnExport" class="btn btn-primary" type="button" value="导出"/> -->
				<shiro:hasPermission name="jmsg:admin">
				 <input id="btnPush" class="btn btn-primary" type="button" value="批量推送"/>
				<input id="btnTaskPush" class="btn btn-primary" type="button" value="任务推送"/>
				</shiro:hasPermission>
			</li>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th><input type="checkbox" onclick="doCheckAll(this,'id', this.checked)"></th>
				<th>操作　</th>
				<th>用户ID</th>
				<shiro:hasPermission name="jmsg:admin">
				<th>用户名称　　</th>
				<th>机构　　　</th>
				</shiro:hasPermission>
				<th>提交时间　</th>
				<th>短信内容　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　</th>
				<th>计数</th>
				<th>手机号码</th>
				<th>运营商</th>
				<th>省份</th>
				<th>地区</th>
				<th>发送状态</th>
				<th>状态报告</th>
				<th>回执状态</th>
				<shiro:hasPermission name="jmsg:admin">
				<th>通道ID</th>
				<th>通道分组名称　　　　　　</th>
				<th>运营商通道名称　　　　　　</th>
				</shiro:hasPermission>
				<th>推送状态</th>
				<th>优先级</th>
				<th>接入号　　</th>
				<th>短信ID</th>
				<th>批次号</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="jmsgSmsSend">
			<tr onclick="selectTr(this, '${jmsgSmsSend.id}' ,'id')">
				<td><input type="checkbox" name="id" id="${jmsgSmsSend.id}" value="${jmsgSmsSend.id}" onclick="if(this.checked){this.checked=false;}else{this.checked=true;}"></td>
				<td>
					<a href='javascript:onView("${jmsgSmsSend.taskId}","${jmsgSmsSend.id}","<fmt:formatDate value="${jmsgSmsSend.createDatetime}" pattern="yyyy-MM-dd HH:mm:ss"/>")'>
						查看</a>
					<%--<shiro:hasPermission name="jmsg:admin">--%>
					<a href="javascript:testGateway('${jmsgSmsSend.user.id}','${jmsgSmsSend.phone}','${jmsgSmsSend.smsContent}')">核验</a>
					<%--</shiro:hasPermission>--%>
				</td>
				<td>
					${jmsgSmsSend.user.id}
				</td>
				<shiro:hasPermission name="jmsg:admin">
				<td>
					${fns:getUserById(jmsgSmsSend.user.id).name}
				</td>
				<td>
					${fns:getUserById(jmsgSmsSend.user.id).company}
				</td>
				</shiro:hasPermission>		
				<td>
					<fmt:formatDate value="${jmsgSmsSend.sendDatetime}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>		
				<td>
					${fn:substring(jmsgSmsSend.smsContent, 0, 60)}...
					<a href="javascript:showTestModal('${fns:escapeHtml(jmsgSmsSend.smsContent)}')">[更多查看]</a>
				</td>
				<td>
					${jmsgSmsSend.payCount}
				</td>
				<td>
					${jmsgSmsSend.phone}
				</td>
				<td>
					${fns:getDictLabel(jmsgSmsSend.phoneType,'phone_type',jmsgSmsSend.phoneType)}
				</td>
				<td>
					${fns:getDictLabel(fn:substring(jmsgSmsSend.areaCode,0,2),'phone_province',jmsgSmsSend.areaCode)}	
				</td>
				<td>
					<!-- ${jmsgSmsSend.cityName} -->
					${fns:getCity(jmsgSmsSend.areaCode).phoneCity}
				</td>
				<td>
					${fns:getDictLabel(jmsgSmsSend.sendStatus,'mms_send_status',jmsgSmsSend.sendStatus)} 
				</td>
				<td>
					<c:if test="${fns:startsWith(jmsgSmsSend.sendStatus,'F')}">
						失败
					</c:if>
					<c:if test="${!fns:startsWith(jmsgSmsSend.sendStatus,'F')}">
						<c:if test="${fns:startsWith(jmsgSmsSend.reportStatus,'P')}">
							待返回
						</c:if>
						<c:if test="${fns:startsWith(jmsgSmsSend.reportStatus,'T')}">
							成功
						</c:if>
						<c:if test="${fns:startsWith(jmsgSmsSend.reportStatus,'F')}">
							失败
						</c:if>	
					</c:if>
				</td>
				<td>
					${jmsgSmsSend.reportStatus}
				</td>
				<shiro:hasPermission name="jmsg:admin">				
				<td>
					${jmsgSmsSend.channelCode}
				</td>
				<td>
					<!-- ${jmsgSmsSend.user.groupId} -->
					<!-- ${fns:getUserById(jmsgSmsSend.user.id).groupId} -->
					${fns:getJmsgGroup(fns:getUserById(jmsgSmsSend.user.id).groupId).name}
				</td>
				<td>
					${fns:getGatewayInfo(jmsgSmsSend.channelCode).gatewayName}
				</td>
				</shiro:hasPermission>
				<td>
					${jmsgSmsSend.pushFlag eq 1 ? '成功' : jmsgSmsSend.pushFlag eq 0 ? '无' : jmsgSmsSend.pushFlag eq 2  ? '失败' : '待推'}
				</td>
				<td>
					${fns:endsWith(jmsgSmsSend.topic,'HIGH') ?  '高': fns:endsWith(jmsgSmsSend.topic,'NORMAL') ? '中' : '低'}
				</td>
				<td>
					${jmsgSmsSend.spNumber}
				</td>
				<td><a href="${ctx}/sms/jmsgSmsSend/detailView?taskid=${jmsgSmsSend.taskId}&bizid=${jmsgSmsSend.id}&createDatetime=<fmt:formatDate value="${jmsgSmsSend.createDatetime}" pattern="yyyy-MM-dd HH:mm:ss"/>">${jmsgSmsSend.id}</a></td>
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
	
	<div class="modal fade" style="display:none;" id="testModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel1" aria-hidden="true">
   		<div class="modal-dialog">
      		<div class="modal-content">
        		<div class="modal-header">
            		<button type="button" class="close" 
               			data-dismiss="modal" aria-hidden="true">
                  		&times;
            		</button>
		            <h4 class="modal-title" id="myModalLabel">
		              	测试结果
		            </h4>
         		</div>
         		<div class="modal-body">
         			<form id="gateWaySendForm" class="form-horizontal">
	                	<div class="control-group">
	                     	<div class="controls1" id="result">
	                     	</div>
	                 	</div>
                 	</form>
         		</div>
         		<div class="modal-footer">
         			<input id="btnSubmit1" class="btn btn-primary" type="button" value="获取结果" onclick="javascript:gatewayResult();"/>
      				<input id="btnSubmit2" class="btn btn-primary" type="button" value="关 闭" data-dismiss="modal" aria-hidden="true"/>
         		</div>
      		</div><!-- /.modal-content -->
		</div><!-- /.modal -->
	</div>
	
	
	<div class="modal fade" style="display:none;" id="tableNameModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel2" aria-hidden="true">
   		<div class="modal-dialog">
      		<div class="modal-content">
        		<div class="modal-header">
            		<button type="button" class="close" 
               			data-dismiss="modal" aria-hidden="true">
                  		&times;
            		</button>
		            <h4 class="modal-title" id="myModalLabel">
		              	任务名称
		            </h4>
         		</div>
         		<div class="modal-body">
         			<form id="gateWaySendForm" class="form-horizontal">
	                	<div class="control-group">
	                     	<input name="taskName" id="taskName" style="width: 498px;" maxlength="40">
	                 	</div>
                 	</form>
         		</div>
         		<div class="modal-footer">
         			<input id="btnSubmit3" class="btn btn-primary" type="button" value="创建任务" onclick="javascript:onCreateTask();"/>
      				<input id="btnSubmit4" class="btn btn-primary" type="button" value="关 闭" data-dismiss="modal" aria-hidden="true"/>
         		</div>
      		</div><!-- /.modal-content -->
		</div><!-- /.modal -->
	</div>
	
</body>
</html>