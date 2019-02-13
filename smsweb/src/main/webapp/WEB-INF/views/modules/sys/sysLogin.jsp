<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="org.apache.shiro.web.filter.authc.FormAuthenticationFilter"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
	<meta charset="UTF-8">
	<title>登录-泛圣|短信运营开放平台</title>
	<link rel="shortcut icon" href="${ctxStatic}/style/sms/konsone.ico" />
	<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <link rel="stylesheet" href="${ctxStatic}/style/sms/kon_css/bootstrap.css" type="text/css">
    <link rel="stylesheet" href="${ctxStatic}/style/sms/kon_css/login.css">
    <link rel="stylesheet" href="${ctxStatic}/style/sms/kon_fa/css/font-awesome.min.css">
	<script src="${ctxStatic}/jquery/jquery-1.9.1.min.js"></script>
	<script src="${ctxStatic}/jquery-validation/1.11.1/jquery.validate.js"></script>
	<script type="text/javascript">
		$(document).ready(function() {
		    /**/
			$("#loginForm").validate({
				rules: {
					validateCode: {remote: "${pageContext.request.contextPath}/servlet/validateCodeServlet"}
				},
				messages: {
					username: {required: "请输入账号！"},password: {required: "请输入密码！"},
					validateCode: {remote: "验证码不正确！", required: "请输入验证码！"}
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
        
        function onSub()
        {
            $('#loginForm').submit();
        }
	</script>
</head>
<body>
<div class="login_top">
    <div class="wd1008">
        <dl class="clearfix">
            <dt class="fl"><img src="${ctxStatic}/style/sms/kon_images/logo.png" /></dt>
            <dd class="fr"><p><a href="http://www.51konyun.com/" target="_blank"><span class="fa fa-home"></span> 返回首页</a>　　<b><span class="fa fa-phone"></span> 客服热线：400-1010-322</b></p></dd>
        </dl>
    </div>
</div>
<div class="login_banner">
    <div class="wd1008 clearfix">
        <div class="fl text-center login_banner_ad"><img src="${ctxStatic}/style/sms/kon_images/ad.png" /></div>
        <div class="fr login_box">
            <div class="login_box_title">
                <div class="clearfix">
                    <div class="pull-left text-left">平台登录</div>
                    <div class="pull-right text-right login_pp" id="messageBox">
                        <label id="loginError" class="error">${message}</label>
                    </div>
                </div>
            </div>
            <div class="login_box_con_user">
                <form class="bs-example bs-example-form" id="loginForm" name="loginForm" action="${ctx}/login" method="post">
                    <div class="input-group login_id">
                        <span class="input-group-addon"><b class="fa fa-user"></b></span>
                        <input id="username" name="username" type="text" value="${username}" placeholder="请输入账号" class="form-control required" maxlength="30"/>
                    </div>
                    <div class="input-group login_pwd">
                        <span class="input-group-addon"><b class="fa fa-key"></b></span>
                        <input id="password" name="password" type="password" autocomplete="off" placeholder="请输入密码" class="form-control required" maxlength="30"/>
                    </div>
                    <div class="input-group login_yzm clearfix">
                        <div class="input-group pull-left">
                            <span class="input-group-addon"><b class="fa fa-adjust"></b></span>
                            <input type="text" id="validateCode" name="validateCode" class="form-control required" placeholder="请输入验证码" />
                        </div>
                        <div class="input-group pull-right text-right">
                            <a title="点击重新获取图片" onclick="$('.validateCode').attr('src','/servlet/validateCodeServlet?'+new Date().getTime());" href="javascript:">
                                <img src="${pageContext.request.contextPath}/servlet/validateCodeServlet" onclick="$('.validateCodeRefresh').click();" class="validateCode">
                            </a>
                        </div>
                    </div>
                    <div class="input-group login_yzm login_reme clearfix">
                        <div class="input-group pull-left">
                            <input type="checkbox" id="rememberMe" name="rememberMe" ${rememberMe ? 'checked' : ''} /> 记住我
                        </div>
                        <!--<div class="input-group pull-right text-right">
                            <a href="pwd.html">忘记密码</a>
                        </div>-->
                    </div>
                </form>
            </div>
            <div class="login_btn"><a href="#" onclick="onSub();" class="btn btn-login">登 录</a></div>    
        </div>
    </div>
</div>
<div class="login_dir">
    <div class="wd1008">
        <ul class="clearfix">
            <li class="fl clearfix">
                <div class="fl"><img src="${ctxStatic}/style/sms/kon_images/icon1.png"></div>
                <div class="fr text-left"><h4>三网发送</h4><p>全面支持移动、电信、联通全国三网的用户统一发送。</p></div>
            </li>
            <li class="fl clearfix">
                <div class="fl"><img src="${ctxStatic}/style/sms/kon_images/icon2.png"></div>
                <div class="fr text-left"><h4>操作便捷</h4><p>流程操作人性化，统计查询、数据导出、图形报表。</p></div>
            </li>
            <li class="fl clearfix">
                <div class="fl"><img src="${ctxStatic}/style/sms/kon_images/icon3.png"></div>
                <div class="fr text-left"><h4>二次接口</h4><p>提供标准API接口协议，轻松对接各类客户的业务系统。</p></div>
            </li>
            <li class="fl clearfix">
                <div class="fl"><img src="${ctxStatic}/style/sms/kon_images/icon4.png"></div>
                <div class="fr text-left"><h4>优质服务</h4><p>自动对账，到达计费，提供7x24小时的优质售后服务。</p></div>
            </li>
        </ul>
    </div>
</div>
<div class="login_copy">
    <div class="text-center">
        <p>Copyright © 2015-<script>document.write((new Date()).getFullYear()+"&nbsp;&nbsp;");</script><b><a href="http://www.xxx.com/" target="_blank">杭州泛圣科技有限公司</a></b> All Rights Reserved.</p>
    </div>
</div>
<!--<body class="login_bg">		
    <div class="login_top">
        <div class="wd1008 cf">
            <dl>
                <dt class="l"></dt>
                <dd class="r"><p><a href="http://www.51konyun.com/" target="_blank"><span class="fa fa-home"></span> 返回首页</a>　　<b><span class="fa fa-phone"></span> 客服热线：400-1010-322</b></p></dd>
            </dl>
        </div>
    </div>
    <div class="login_box">
        <div class="wd1008 cf login_ad">
            <div class="login_boxcon ${isValidateCodeLogin?'':'login_boxcon_dis'} r">
            <form id="loginForm" name="loginForm" action="${ctx}/login" method="post">
                <h1></h1>
                <dl>
                    <dt class="cf">
                        <p class="l">账　号：</p>
                        <h2 class="l">
                        	<input id="username" name="username" value="${username}" type="text" class="logininput required" maxlength="30"/>
                        </h2>
                    </dt>
                    <dd class="cf">
                        <p class="l">密　码：</p>
                        <h2 class="l cf">
							<input id="password" name="password" type="password" autocomplete="off" class="l pwdinput required" maxlength="30"/>
						</h2>
                    </dd>
                </dl>
                <c:if test="${isValidateCodeLogin}">
                <ul class="cf">
                    <li class="cf">
						<input type="hidden" name="isgetpwd" id="isgetpwd" value=""/>
                    	<p class="l">验证码：</p>
                        <h3 class="l cf">
							<i class="l">
								<input type="text" id="validateCode" name="validateCode" maxlength="4" class="l yzminput required"/>
							</i>
							<b class="l">
								<a title="点击重新获取图片" onclick="$('.validateCode').attr('src','${pageContext.request.contextPath}/servlet/validateCodeServlet?'+new Date().getTime());" href="javascript:">
									<img src="${pageContext.request.contextPath}/servlet/validateCodeServlet" class="validateCode" onclick="$('.validateCodeRefresh').click();" width="75" height="25" id="randImage"/>
								</a>
							</b>
							<span class="l"><a onclick="$('.validateCode').attr('src','${pageContext.request.contextPath}/servlet/validateCodeServlet?'+new Date().getTime());" href="javascript:">换一张</a></span>
						</h3>
                    </li>
                </ul>
                </c:if>
                <div class="cf">
                    <p class="l"><input type="checkbox" id="rememberMe" name="rememberMe" ${rememberMe ? 'checked' : ''} onclick="checkbox();" style="margin: 5px 0;"> 记住密码</p>
                    <p class="r" style="display: none;"><a href="">忘记密码？</a></p>
                </div>
                <c:if test="${not empty message}"><div class="pp">${message}</div></c:if>
                <ol>
                    <li><input type="submit" class="login_but" onmouseout="this.className='login_but'" onmouseover="this.className='login_but_over'"  value="登 录" /></li>
                </ol>
                </form>
                
                <%-- <div class="header">
					<div id="messageBox" class="alert alert-error ${empty message ? 'hide' : ''}">
						<label id="loginError" class="error">${message}</label>
					</div>
				</div> --%>
            </div>
        </div>
    </div>
    <div class="login_dir">
        <ul class="wd1008 cf">
        	<li class="l dirmenu dirmenu1 nolinel">
                <h2>三网发送</h2>
                <p>全面支持移动、电信、联通全国三网的用户统一发送。</p>
            </li>
            <li class="l dirmenu dirmenu2">
                <h2>操作便捷</h2>
                <p>流程操作人性化，统计查询、数据导出、图形报表。</p>
            </li>
            <li class="l dirmenu dirmenu3">
                <h2>二次接口</h2>
                <p>提供标准API接口协议，轻松对接各类客户的业务系统。</p>
            </li>
            <li class="l dirmenu dirmenu4 noliner">
                <h2>优质服务</h2>
                <p>自动对账，到达计费，提供7x24小时的优质售后服务。</p>
            </li>
        </ul>
    </div>
    <div class="login_copy">
        <div class="wd1008">
            <p>Copyright &copy; 2015-<script>document.write((new Date()).getFullYear());</script> <b>杭州泛圣科技有限公司</b> All Rights Reserved.</p>
        </div>
    </div>
</div>
-->
</body>
</html>