<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
	<head>
		<title>错误码对照表</title>
		<meta name="decorator" content="default"/>
		<style>
			.boxtitle{background-color:#3B4B6B;}
		</style>
	</head>
	<body>
		<ul class="nav nav-tabs">
			<li class="active">
				<a href="${ctx}/sys/menu/showErrorCodeTable1">平台常用错误码</a>
			</li>
			<li>
				<a href="${ctx}/sys/menu/showErrorCodeTable2">中国移动错误码</a>
			</li>
			<li>
				<a href="${ctx}/sys/menu/showErrorCodeTable3">中国联通错误码</a>
			</li>
			<li>
				<a href="${ctx}/sys/menu/showErrorCodeTable4">中国电信错误码</a>
			</li>
		</ul>
		<table class="table table-striped table-bordered table-condensed">
			<thead>
				<tr>
					<th colspan="3">
						<div class="boxtitle"><font color="white">【HTTP协议】-提交短信过程中-常见错误代码</font></div>
					</th>
				</tr>
				<tr>
					<th>错误码</th>
					<th>错误描述</th>
					<th>解决方法</th>
				</tr>
			</thead>
			<tbody><c:forEach items="${httpProtocolSubmittingShortMessageCommonErrorCodeList}" var="dict">
				<tr>
					<td>
						${dict.value}
					</td>
					<td>
						${dict.label}
					</td>
					<td>
						${dict.remarks}
					</td>
				</tr>
			</c:forEach></tbody>
		</table>
		<table class="table table-striped table-bordered table-condensed">
			<thead>
			<tr>
				<th colspan="3">
					<div class="boxtitle"><font color="white">【CMPP协议】-登录提交过程中-常见错误代码</font></div>
				</th>
			</tr>
			<tr>
				<th>错误码</th>
				<th>错误描述</th>
				<th>解决方法</th>
			</tr>
			</thead>
			<tbody><c:forEach items="${cmppProtocolLoginSubmissionCommonErrorCodeList}" var="dict">
				<tr>
					<td>
						${dict.value}
					</td>
					<td>
						${dict.label}
					</td>
					<td>
						${dict.remarks}
					</td>
				</tr>
			</c:forEach></tbody>
		</table>
		<table class="table table-striped table-bordered table-condensed">
			<thead>
			<tr>
				<th colspan="3">
					<div class="boxtitle"><font color="white">【状态回执报告】-成功提交短信后-常见错误代码</font></div>
				</th>
			</tr>
			<tr>
				<th>错误码</th>
				<th>错误描述</th>
				<th>解决方法</th>
			</tr>
			</thead>
			<tbody><c:forEach items="${statusReceiptReportSuccessfulSubmissionCommonErrorCodeList}" var="dict">
				<tr>
					<td>
						${dict.value}
					</td>
					<td>
						${dict.label}
					</td>
					<td>
						${dict.remarks}
					</td>
				</tr>
			</c:forEach></tbody>
		</table>
		<div class="form-actions pagination-left">
			<font color="red">温馨提醒：其他如MK:XXXX、CBXXXX、MBXXXX、IDXXXX、IBXXXX、DBXXXX、IAXXXX、ICXXXX都表示客户短信接收失败，详细请查看对应运营商错误码。</font>
		</div>
	</body>
</html>