<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="org.apache.shiro.web.filter.authc.FormAuthenticationFilter"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<meta charset="UTF-8">
	<title>登录-全网短信网关能力开放平台</title>
	<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
	<link rel="stylesheet" href="${ctxStatic}/style/sms/css/bootstrap.min.css">  
	<link rel="stylesheet" href="${ctxStatic}/style/sms/css/login.css">
	
	<script src="${ctxStatic}/jquery/jquery-1.9.1.min.js"></script>
	<script src="${ctxStatic}/jquery-validation/1.11.1/jquery.validate.js"></script>
	
	<%-- <script src="${ctxStatic}/style/sms/js/bootstrap.min.js"></script>
	<script src="${ctxStatic}/style/sms/js/js.js"></script> --%>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#loginForm").validate({
				rules: {
					validateCode: {remote: "${pageContext.request.contextPath}/servlet/validateCodeServlet"}
				},
				messages: {
					username: {required: "请填写用户名."},password: {required: "请填写密码."},
					validateCode: {remote: "验证码不正确.", required: "请填写验证码."}
				},
				errorLabelContainer: "#messageBox",
				errorPlacement: function(error, element) {
					error.appendTo($("#loginError").parent());
				} 
			});
		});
		// 如果在框架或在对话框中，则弹出提示并跳转到首页
		if(self.frameElement && self.frameElement.tagName == "IFRAME" || $('#left').length > 0 || $('.jbox').length > 0){
			alert('未登录或登录超时。请重新登录，谢谢！');
			top.location = "${ctx}";
		}
	</script>
</head>
<body>
<div class="loginbox over">
	<div class="loginpp">
		<h2>全网短信网关能力开放平台</h2>
		<p>
			基于云计算SaaS平台架构，运用队列、缓存、分布式存储和JStorm大数据计算等技术应对高并发发送需求，采用分布式、集群技术并行扩展系统处理能力，同时确保系统的稳定和安全。满足日均千万级数据的吞吐量，构建大容量、高效便捷的信息能力开放平台！
		</p>
	</div>
	<div class="loginform">
		<img src="${ctxStatic}/style/sms/images/kouhao.png">
		<form id="loginForm" class="form-inline" action="${ctx}/login" method="post">
			<div class="row">
				<h2>登录平台</h2>
			</div>
			<div class="form-group">
				<label for="" class="glyphicon glyphicon-user"> </label>
				<input type="text" id="username" name="username" value="${username}" class="form-control required" placeholder="请输入账号">
			</div>
			<div class="form-group">
				<label for="" class="glyphicon glyphicon-eye-close"> </label>
				<input type="password" id="password" name="password" class="form-control required" placeholder="请输入密码">
			</div>
			<c:if test="${isValidateCodeLogin}">
				<div class="form-group">
					<label for="validateCode" class="glyphicon glyphicon-picture"> </label>
					<sys:validateCode name="validateCode" inputCssStyle="margin-bottom:0;"/>
				</div>
			</c:if>
			<div class="form-group" style="display: none;">
				<label for="" class="glyphicon glyphicon-picture"> </label>
				<input type="" class="form-control form-yzm" id="" placeholder="验证码">
				<img src="${ctxStatic}/style/sms/images/yzm.jpg">
				<p><a href="">换一张</a></p>
			</div>
			<div class="form-group form_bottom">
				<input type="checkbox" id="rememberMe" name="rememberMe" ${rememberMe ? 'checked' : ''} onclick="checkbox();">
				<p>记住密码</p>
				<p class="forgetpwd" style="display: none;"><a href="">忘记密码？</a></p>
			</div>
			<div class="row">
				<button type="submit" class="btn btn-primary btn-lg">登　录</button>
			</div>
		</form>
		<div class="header">
			<div id="messageBox" class="alert alert-error ${empty message ? 'hide' : ''}"><button data-dismiss="alert" class="close">×</button>
				<label id="loginError" class="error">${message}</label>
			</div>
		</div>
	</div>
</div>
<div class="copybox">
	<p>Copyright &copy; 2015-<script>document.write((new Date()).getFullYear());</script> <b>杭州泛圣科技有限公司</b> All Rights Reserved.</p>
</div>
</body>
</html>