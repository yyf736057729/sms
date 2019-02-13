<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
	<head>
		<title>错误码对照表</title>
		<meta name="decorator" content="default"/>
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
					<th>错误码</th>
					<th>错误描述</th>
					<th>解决方法</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${chinaMobileErrorCodeList}" var="dict">
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
				</c:forEach>
				<tr>
					<td>
						代码分类
					</td>
					<td>
						MK：短信中心返回的状态报告值，短信状态为UNDELIV。<br>
						CB：SCP鉴权失败。<br>
						MB：短信中心返回错误响应。<br>
						ID：等待应答超时。<br>
						IB：前转网关返回的错误响应消息。
					</td>
					<td>
						无
					</td>
				</tr>
				<tr>
					<td>
						代码详细分类
					</td>
					<td>
						MA:XXXX SMSC不返回响应消息时的状态报告。<br>
						MB:XXXX SMSC返回错误响应消息时的状态报告。<br>
						MC:XXXX 没有从SMSC接收到状态报告时的状态报告。<br>
						CA:XXXX SCP不返回响应消息时的状态报告。<br>
						CB:XXXX SCP返回错误响应消息时的状态报告。<br>
						DA:XXXX DSMP不返回响应消息时的状态报告。<br>
						DB:XXXX DSMP返回错误响应消息时的状态报告。<br>
						SA:XXXX SP不返回响应消息时的状态报告。<br>
						SB:XXXX SP返回错误响应消息时的状态报告。<br>
						IA:XXXX 下一级ISMG不返回响应消息时的状态报告。<br>
						IB:XXXX 下一级ISMG返回错误响应消息时的状态报告。<br>
						IC:XXXX 没有从下一级ISMG收到状态报告时的状态报告。<br>
						ID:XXXX infoX-SMS GW内部检测错误码。<br>
						ID:0020 SPACE用户鉴权模块：鉴权用户停机或欠费错误。<br>
						ID:0021 SPACE用户鉴权模块：用户销户错误。<br>
						MI::zzzz SMSC返回状态报告的状态值为EXPIRED。<br>
						MJ:zzzz SMSC返回状态报告的状态值为DELETED。<br>
						MK:zzzz SMSC返回状态报告的状态值为UNDELIV。<br>
						ML:zzzz SMSC返回状态报告的状态值为ACCEPTD。<br>
						MM:zzzz SMSC返回状态报告的状态值为UNKNOWN。<br>
						MN:zzzz SMSC返回状态报告的状态值为REJECTD。<br>
						MH:zzzz 其它值。
					</td>
					<td>
						无
					</td>
				</tr>
			</tbody>
		</table>
	</body>
</html>