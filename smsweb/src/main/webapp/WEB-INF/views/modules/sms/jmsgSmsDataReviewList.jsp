<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>短信内容审核</title>
	<meta name="decorator" content="default"/>
	<script src="${ctxStatic}/docheck/docheck.js" type="text/javascript"></script>
	<script type="text/javascript">
		$(document).ready(function() {
            $("#userId").val("${userId}");
            $("#userName").val("${userName}");
		});
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").submit();
        	return false;
        }
		function onCheck(status){
			var ids = getCheckboxValue("id");
			if(!ids){
				alertx("请选择要审核的数据");
			}else{
				if("-2" == status){
					//$("#searchForm").attr("action","${ctx}/sms/jmsgSmsTask/reviewSmsForm?ids="+ids);
					$('#reviewRemarksModal').modal('show');
				}else{
					$("#searchForm").attr("action","${ctx}/sms/jmsgSmsTask/reviewSms?status="+status+"&ids="+ids);
					$("#searchForm").submit();
				}
				return false;
			}
		}
		
		function onView(id){
			var url = "${ctx}/sms/jmsgSmsTask/view?id="+id+"&viewFlag=1";
			windowOpen(url,id,800,600);
		}
		
		function onReviewSms(){
			var ids = getCheckboxValue("id");
			var reviewRemarks = $("#reviewRemarks").val();
			reviewRemarks = encodeURI(encodeURI(reviewRemarks));
			$("#searchForm").attr("action","${ctx}/sms/jmsgSmsTask/reviewSms?status=-2"+"&ids="+ids+"&reviewRemarks="+reviewRemarks);
			$("#searchForm").submit();
		}

		function onekeyReview(status){
			var userIdText = $("#userIdText").val();		
			var userId = $("#userId").val();
            var userName = $("#userName").val();
			var sign = $("#sign").val();
			var smsContent = $("#smsContent").val();
			var createDatetimeQ = $("#createDatetimeQ").val();
			var createDatetimeZ = $("#createDatetimeZ").val();
			var onekeyReviewRemarks;
			if(status==1){
                onekeyReviewRemarks = "";
            }else if(status==-2){
                onekeyReviewRemarks = $("#onekeyReviewRemarks").val();
            }
			var url = '${ctx}/sms/jmsgSmsTask/onekeyReview?status=' + status
                + '&userIdText=' + userIdText
                + '&userId=' + userId
                + '&userName=' + userName
                + '&sign=' + sign
                + '&smsContent=' + smsContent
                + '&createDatetimeQ=' + createDatetimeQ
                + '&createDatetimeZ=' + createDatetimeZ
				+ '&onekeyReviewRemarks=' + onekeyReviewRemarks;
			$("#searchForm").attr("action", url);
			$("#searchForm").submit();
		}

		function initUpdate(taskId,content,sendDatetime){
			$("#taskId").val(taskId);
			$("#sendDatetime").val(sendDatetime);
			$("#content").val(content);
			countNum();
			$('#updateModal').modal('show');
		}
		
		function onUpdate(){
			var id = $("#taskId").val();
			var content = $("#content").val();
			if(content == null || content ==''){
				alertx('请输入短信内容');
				return;
			}
			content = encodeURI(encodeURI(content));
			var sendDatetime = $("#sendDatetime").val();
			$("#searchForm").attr("action","${ctx}/sms/jmsgSmsTask/updateSmsContent?content="+content+"&sendDatetime="+sendDatetime+"&id="+id);
			$("#searchForm").submit();
		}
		
		function countNum()
		{
			var content = $("#content").val();
			if (content)
			{
				if (content.length > 0 && content.length <= 70)
				{
					$("#contentLabel").text("共 "+content.length+" 字，1 条短信");
				}
				else
				{
					$("#contentLabel").text("共 "+content.length+" 字，共 "+ (Number(Math.ceil((content.length - 70)/67)) + Number(1)) +" 条短信");
				}
			}
			else
			{
				$("#contentLabel").text("共 0 字，0 条短信");
			}
		}

        function showOneKeyReview(){
            //验证用户ID不能为空
            var userIdText = $("#userIdText").val();
            if(userIdText == null || userIdText == ''){
                alertx("请输入用户ID");
                return;
            }

            //验证发送时间起不能为空
            var createDatetimeQ = $("input[name='createDatetimeQ']").val();
            if(createDatetimeQ == null || createDatetimeQ == ''){
                alertx("请选择发送时间起");
                return;
            }

            //验证发送时间止不能为空
            var createDatetimeZ = $("input[name='createDatetimeZ']").val();
            if(createDatetimeZ == null || createDatetimeZ == ''){
                alertx("请选择发送时间止");
                return;
            }
            top.$.jBox.confirm(
                "确认要审核吗？",
                "系统提示",
                function(v,h,f){
                    if(v=="ok"){
                        onekeyReview(1);
                    }else if(v == "no"){
                        $("#onekeyReviewRemarks").val("");
                        $("#onekeyReviewRemarksModal").modal("show");
                    }
                },
                {
                    buttons:{"审核通过":"ok","审核不通过":"no","取消":"closed"}
                },
                {
                    buttonsFocus:1
                }
            );
            top.$('.jbox-body .jbox-icon').css('top','55px');
        }

	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<shiro:hasPermission name="sms:jmsgSmsTask:view"><li class="active">
			<a href="${ctx}/sms/jmsgSmsTask/reviewList">信息列表</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="jmsgSmsTask" action="${ctx}/sms/jmsgSmsTask/reviewList" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<ul class="ul-form">
			<li>
				<label>用户ID：</label>
				<form:input path="userIdText" htmlEscape="false" maxlength="50" class="input-small"/>
			</li>
			<li><label>创建人：</label>
				<sys:treeselect id="user" name="user.id" value="${jmsgSmsTask.user.id}" labelName="user.name" labelValue="${jmsgSmsTask.user.name}"
					title="用户" url="/sys/office/treeData?type=3" cssClass="input-small" allowClear="true" notAllowSelectParent="true"/>
			</li>
			<%--<li><label>用户ID：</label>--%>
				<%--<form:input id="user" name="user.id" path="user" value="${jmsgSmsTask.user.id}" htmlEscape="false" maxlength="20" class="input-medium"/>--%>
			<%--</li>--%>

			<%--<li><label>手机号：</label>--%>
				<%--<form:input id="phones" name="phones" path="phones" value="${jmsgSmsTask.phones}" htmlEscape="false" maxlength="20" class="input-medium"/>--%>
			<%--</li>--%>
			<li><label>签名：</label>
					<form:input id="sign" name="sign" path="sign" value="${signs}" htmlEscape="false" maxlength="20" class="input-medium"/>
				</li>
			<li><label>短信内容：</label>
				<form:input path="smsContent" htmlEscape="false" maxlength="20" class="input-medium"/>
			</li>
			<li><label>时间：</label>
				<input id="createDatetimeQ" name="createDatetimeQ" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					value="<fmt:formatDate value="${jmsgSmsTask.createDatetimeQ}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:true});"/> - 
				<input id="createDatetimeZ" name="createDatetimeZ" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
					value="<fmt:formatDate value="${jmsgSmsTask.createDatetimeZ}" pattern="yyyy-MM-dd HH:mm:ss"/>"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:true});"/>		
			</li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
			<shiro:hasPermission name="sms:jmsgSmsTask:view">
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="button" value="审核通过" onclick="javascript:onCheck(1)"/></li>
			<li class="btns"><input id="btnSubmit" class="btn btn-primary" type="button" value="审核不通过" onclick="javascript:onCheck(-2)"/></li>
			<li class="btns">
				<input id="btnReview" class="btn btn-primary" type="button" value="一键审核" onclick="javascript:showOneKeyReview();"/>
			</li>
			</shiro:hasPermission>
			<li class="clearfix"></li>
		</ul>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th width="30px;"><input type="checkbox" onclick="doCheckAll(this,'id', this.checked)"></th>
				<th width="50px;">操作</th>
				<th width="70px;">创建人</th>
				<th width="70px;">登录名</th>
				<th width="70px;">组织机构</th>
				<!-- <th>短信ID</th> -->
				<th>发送条数</th>
				<th>短信内容</th>
				<th width="80">创建时间</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="jmsgSmsTask">
			<tr onclick="selectTr(this, '${jmsgSmsTask.id}' ,'id')">
				<td><input type="checkbox" name="id" id="${jmsgSmsTask.id}" value="${jmsgSmsTask.id}" onclick="if(this.checked){this.checked=false;}else{this.checked=true;}"></td>
				<td>
					<a href="javascript:initUpdate('${jmsgSmsTask.id}','${jmsgSmsTask.smsContent}','<fmt:formatDate value="${jmsgSmsTask.sendDatetime}" pattern="yyyy-MM-dd HH:mm:ss"/>');">修改</a>
				</td>
				<td>
					${jmsgSmsTask.user.name}
				</td>
				<td>${jmsgSmsTask.user.loginName}</td>
				<td>${jmsgSmsTask.user.company.name}</td>
				<!-- <td>
					${jmsgSmsTask.id}
				</td> -->
				<td>${jmsgSmsTask.sendCount}</td>
				<td>
					${jmsgSmsTask.smsContent}
					<c:if test="${jmsgSmsTask.taskType eq 0 }">
						<a href="${ctx}/sms/jmsgSmsTask/download?taskId=${jmsgSmsTask.id}">下载清单</a>
					</c:if>
					<c:if test="${jmsgSmsTask.taskType > 0 }">
						<a href="${ctx}/sms/jmsgSmsTask/download?taskId=${jmsgSmsTask.id}">下载内容</a>
					</c:if>
				</td>
				<td>
					<fmt:formatDate value="${jmsgSmsTask.createDatetime}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<!-- <td>
    				<a href="javascript:onView(${jmsgSmsTask.id})">查看</a>
				</td> -->
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
	
	<div class="modal fade" style="display:none;" id="reviewRemarksModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
   		<div class="modal-dialog">
      		<div class="modal-content">
        		<div class="modal-header">
            		<button type="button" class="close" 
               			data-dismiss="modal" aria-hidden="true">
                  		&times;
            		</button>
		            <h4 class="modal-title" id="myModalLabel">
		              	不通过原因
		            </h4>
         		</div>
         		<div class="modal-body">
         			<form id="gateWaySendForm" class="form-horizontal">
	                	<div class="control-group">
	                     	<div class="controls1">
	                     		<textarea style="width:498px;" name="reviewRemarks" rows="3" cols="8" maxlength="200" id="reviewRemarks"></textarea>
	                     	</div>
	                 	</div>
                 	</form>
         		</div>
      		</div>
      		<div class="modal-footer">
      			<input id="btnSubmit1" class="btn btn-primary" type="button" value="确 定" onclick="javascript:onReviewSms();"/>
      			<input id="btnSubmit2" class="btn btn-primary" type="button" value="关 闭" data-dismiss="modal" aria-hidden="true"/>
      		</div>
		</div>
	</div>

	<div class="modal fade" style="display:none;" id="onekeyReviewRemarksModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close"
							data-dismiss="modal" aria-hidden="true">
						&times;
					</button>
					<h4 class="modal-title" id="myModalLabel2">
						不通过原因
					</h4>
				</div>
				<div class="modal-body">
					<form id="gateWaySendForm2" class="form-horizontal">
						<div class="control-group">
							<div class="controls1">
								<textarea style="width:498px;" name="onekeyReviewRemarks" rows="3" cols="8" maxlength="200" id="onekeyReviewRemarks"></textarea>
							</div>
						</div>
					</form>
				</div>
			</div>
			<div class="modal-footer">
				<input id="btnSubmit12" class="btn btn-primary" type="button" value="确 定" onclick="javascript:onekeyReview(-2);"/>
				<input id="btnSubmit22" class="btn btn-primary" type="button" value="关 闭" data-dismiss="modal" aria-hidden="true"/>
			</div>
		</div>
	</div>
	
	<div class="modal fade" style="display:none;" id="updateModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel1" aria-hidden="true">
   		<div class="modal-dialog">
      		<div class="modal-content">
        		<div class="modal-header">
            		<button type="button" class="close" 
               			data-dismiss="modal" aria-hidden="true">
                  		&times;
            		</button>
		            <h4 class="modal-title" id="myModalLabel">
		              	短信详情
		            </h4>
         		</div>
         		<div class="modal-body">
         			<form id="gateWaySendForm" class="form-horizontal">
         				<input type="hidden" id="taskId">
	       				<div class="control-group">
							<div class="controls1">
								短信内容：<textarea name="content" id="content" htmlEscape="false" style="width:300px;" cols="10" rows="5" oninput="countNum();" onpropertychange="countNum();"></textarea>
								<span class="help-inline"><font color="red">*</font> </span>
							</div>
							<div class="controls">
								<span class="help-inline" id="contentLabel">共 0 字，0 条短信</span>
							</div>
						</div>
						<div class="control-group">
							<div class="controls1">
								发送时间：<input name="sendDatetime" id="sendDatetime" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
									onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:true});"/>
							</div>
						</div>
                 	</form>
         		</div>
      		</div>
      		<div class="modal-footer">
      			<input id="btnSubmit1" class="btn btn-primary" type="button" value="确 定" onclick="javascript:onUpdate();"/>
      			<input id="btnSubmit2" class="btn btn-primary" type="button" value="关 闭" data-dismiss="modal" aria-hidden="true"/>
      		</div>	
		</div>
	</div>
</body>
</html>