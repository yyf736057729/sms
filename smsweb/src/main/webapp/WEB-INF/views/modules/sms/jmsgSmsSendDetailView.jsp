<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>任务发送明细</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#btnMenu").remove();
		});
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").submit();
        	return false;
        }
	</script>
</head>
<body>
	<sys:message content="${message}"/>
	<fieldset><legend>内容发送信息</legend>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<tbody>
			<tr>
				<td width="100"><h4>手机号码</h4></td>
				<td><h4>${entity.phone} (中国${fns:getDictLabel(entity.phoneType,'phone_type','-')},${fns:getCity(entity.areaCode).phoneProv}${fns:getCity(entity.areaCode).phoneCity})</h4></td>
			</tr>
			
			<tr>
				<td width="100">短信内容<br/>(共${fn:length(entity.smsContent) }字,${entity.payCount }条)</td>
				<td>${entity.smsContent}</td>
			</tr>
			
		</tbody>
	</table>
	
	<fieldset><legend>队列发送信息</legend></fieldset>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>发送时间</th>
				<th>发送状态</th>
				<th>发送编号</th>
			</tr>
		</thead>
		<tbody>
			<tr>
				<td>
					<fmt:formatDate value="${entity.createDatetime}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
					${fns:getDictLabel(entity.sendStatus,'mms_send_status',entity.sendStatus)} 
				</td>	
				<td>
					${entity.msgid}
				</td>	
			</tr>
		</tbody>
	</table>
	<fieldset><legend>网关提交信息</legend></fieldset>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>提交时间</th>
				<th>提交状态(<font color="red">0:成功</font>)</th>
				<th>状态描述</th>
				<th>提交编号</th>
				<th>通道</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${submitList}" var="submit">
			<tr>
				<td>
					<fmt:formatDate value="${submit.createtime}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
					${submit.result}
				</td>
				<td>
					${submit.reserve}
				</td>
				<td>
					${submit.msgid}
				</td>
				<td>
					${submit.gatewayid}
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<fieldset><legend>状态回执信息</legend></fieldset>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>回执时间</th>
				<th>回执状态(<font color="red">DELIVRD:成功</font>)</th>
				<th>回执编号</th>
				<th>通道</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${reportList}" var="report">
			<tr>
				<td>
					<fmt:formatDate value="${report.createtime}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
					${report.stat}
				</td>
				<td>
					${report.msgid}
				</td>
				<td>
					${report.gatewayId}
				</td>		
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<fieldset><legend>客户推送信息</legend></fieldset>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>推送ID</th>
				<th>推送时间</th>
				<th>推送结果</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${pushList}" var="push">
			<tr>
				<td>
					${push.id}
				</td>
				<td>
					<fmt:formatDate value="${push.createtime}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
				<td>
					${push.result}
				</td>		
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<c:if test="${smsType eq 3 }">
	<fieldset><legend>短信彩信下载</legend></fieldset>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>下载ID</th>
				<th>用户UA</th>
				<th>推送时间</th>
			</tr>
		</thead>
		<tbody>
			<tr>
				<td>
					${mmsDownList.id}
				</td>
				<td>
					${mmsDownList.userAgent}
				</td>
				<td>
					<fmt:formatDate value="${mmsDownList.downTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
				</td>
			</tr>
		</tbody>
	</table>
	</c:if>
</body>
</html>