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
					<div class="boxtitle"><font color="white">中国电信SMGP 3.0 协议的结果码</font></div>
				</th>
			</tr>
			<tr>
				<th>错误码</th>
				<th>错误描述</th>
				<th>解决方法</th>
			</tr>
			</thead>
			<tbody><c:forEach items="${chinaTelecomSMGPThreePointZeroProtocolResultCodeList}" var="dict">
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
					<div class="boxtitle"><font color="white">中国电信ISMG返回码</font></div>
				</th>
			</tr>
			<tr>
				<th>错误码</th>
				<th>错误描述</th>
				<th>解决方法</th>
			</tr>
			</thead>
			<tbody><c:forEach items="${chinaTelecomISMGResultCodeList}" var="dict">
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
					<div class="boxtitle"><font color="white">中国电信计费结果返回码</font></div>
				</th>
			</tr>
			<tr>
				<th>错误码</th>
				<th>错误描述</th>
				<th>解决方法</th>
			</tr>
			</thead>
			<tbody><c:forEach items="${chinaTelecomBillingReturnCodeList}" var="dict">
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
	</body>
</html>