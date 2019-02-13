<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>用户管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			showReviewCount();
			$("#no").focus();
			$("#inputForm").validate({
				rules: {
					loginName: {remote: "${ctx}/sys/user/checkLoginName?oldLoginName=" + encodeURIComponent('${jmsgAccount.user.loginName}')}
				},
				messages: {
					loginName: {remote: "用户登录名已存在"},
					confirmNewPassword: {equalTo: "输入与上面相同的密码"}
				},
				submitHandler: function(form){
					loading('正在提交，请稍等...');
					form.submit();
				},
				errorContainer: "#messageBox",
				errorPlacement: function(error, element) {
					$("#messageBox").text("输入有误，请先更正。");
					if (element.is(":checkbox")||element.is(":radio")||element.parent().is(".input-append")){
						error.appendTo(element.parent().parent());
					} else {
						error.insertAfter(element);
					}
				}
			});
			
			if ($("#user\\.id").val() != "" && $("#user\\.id").val() != null)
			{
				$("#groupIdDiv").show();
			}
			else
			{
				$("#groupIdDiv").hide();
			}
		});
		
		function onSave(){
			var validate = /^[0-9a-zA-Z]*$/g;
			var mmsfrom = $("#mmsfrom").val();
			if(mmsfrom != null && mmsfrom !=''){
				if(!validate.test(mmsfrom)){
					alertx("请输入正确接入号,字母或数字");
					return;
				}
			}
			
			$("#inputForm").submit();
		}
		
		function onRefresh(){
			$("#apikey").val(guid());
		}
		
		function guid() {
		    return 'xxxxxxxxxxxx4xxxyxxxxxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
		        var r = Math.random()*16|0, v = c == 'x' ? r : (r&0x3|0x8);
		        return v.toString(16);
		    });
		}
		function showReviewCount(){
			var noCheck = $("#noCheck").val();
			if('2' == noCheck){
				$("#reviewCount").show();
			}else{
				$("#reviewCount").hide();
			}
		}
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/account/jmsgAccount/list">信息列表</a></li>
		<!-- <li><a href="${ctx}/account/jmsgAccount/form?id=${jmsgAccount.id}&appType=mms">彩信账户信息${not empty jmsgAccount.id?'修改':'添加'}</a></li> -->
		<li class="active"><a href="${ctx}/account/jmsgAccount/form?id=${jmsgAccount.id}&appType=sms">信息${not empty jmsgAccount.id?'修改':'添加'}</a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="jmsgAccount" action="${ctx}/account/jmsgAccount/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<form:hidden path="user.id"/>
		<input type="hidden" name="appType" value="sms">
		<%-- <form:hidden path="user.groupId"/> --%>
		<sys:message content="${message}"/>
		<fieldset><legend>基本信息</legend></fieldset>
		<div class="control-group">
			<label class="control-label">机构:</label>
			<div class="controls">
                <sys:treeselect id="company" name="user.company.id" value="${jmsgAccount.user.company.id}" labelName="user.company.name" labelValue="${jmsgAccount.user.company.name}"
					title="机构" url="/sys/office/treeData?type=1" cssClass="required"/>
					<span class="help-inline"><font color="red">*请按实际需要选择</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">登录账号:</label>
			<div class="controls">
				<input id="oldLoginName" name="oldLoginName" type="hidden" value="${jmsgAccount.user.loginName}">
				<form:input path="user.loginName" htmlEscape="false" maxlength="50" class="required userName" readonly="${not empty jmsgAccount.user.id && jmsgAccount.user.id !='' ?'true':'false'}"/>
				<span class="help-inline"><font color="red">*建议英文或英文+数字，区分大小写</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">用户名称:</label>
			<div class="controls">
				<form:input path="user.name" htmlEscape="false" maxlength="50" class="required"/>
				<span class="help-inline"><font color="red">*建议中文名称，2-8个字</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">密码:</label>
			<div class="controls">
				<input id="newPassword" name="newPassword" type="password" value="" maxlength="50" minlength="3" class="${empty jmsgAccount.user.id?'required':''}"/>
				<c:if test="${empty jmsgAccount.user.id}"><span class="help-inline"><font color="red">*</font> </span></c:if>
				<c:if test="${not empty jmsgAccount.user.id}"><span class="help-inline">若不修改密码，请留空。</span></c:if>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">确认密码:</label>
			<div class="controls">
				<input id="confirmNewPassword" name="confirmNewPassword" type="password" value="" maxlength="50" minlength="3" equalTo="#newPassword"/>
				<c:if test="${empty jmsgAccount.user.id}"><span class="help-inline"><font color="red">*</font> </span></c:if>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">联系手机:</label>
			<div class="controls">
				<form:input path="user.mobile" htmlEscape="false" maxlength="100"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">电子邮箱:</label>
			<div class="controls">
				<form:input path="user.email" htmlEscape="false" maxlength="100" class="email"/>
			</div>
		</div>
		<!-- <div class="control-group">
			<label class="control-label">电话:</label>
			<div class="controls">
				<form:input path="user.phone" htmlEscape="false" maxlength="100"/>
			</div>
		</div> -->
		<div class="control-group">
			<label class="control-label">QQ:</label>
			<div class="controls">
				<form:input path="user.qq" htmlEscape="false" maxlength="100"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">公司名称:</label>
			<div class="controls">
				<form:input path="user.companyName" htmlEscape="false" maxlength="100"/>
				<span class="help-inline"><font color="red"><span class="help-inline"><font color="red">*填写公司全称，用于后续数据统计</font> </span></font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">联系人:</label>
			<div class="controls">
				<form:input path="user.contactName" htmlEscape="false" maxlength="50"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">短信价格（元/条）:</label>
			<div class="controls">
				<form:input path="user.price" htmlEscape="false" maxlength="100" cssClass="input-xlarge number"/>
				<span class="help-inline"><font color="red"><span class="help-inline"><font color="red">用于客户端首页展示</font> </span></font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">备注:</label>
			<div class="controls">
				<form:textarea path="user.remarks" htmlEscape="false" rows="3" maxlength="200" class="input-xlarge"/>
			</div>
		</div>
		
		<fieldset><legend>权限信息</legend></fieldset>
		<div class="control-group">
			<label class="control-label">用户角色:</label>
			<div class="controls">
				<c:if test="${currentUser.id eq 1}">
					<form:checkboxes path="user.roleIdList" items="${allRoles}" itemLabel="name" itemValue="id" htmlEscape="false" class="required"/>
				</c:if>
				<c:if test="${currentUser.id ne 1}">
					<form:checkboxes path="user.roleIdList" items="${commonUser}" itemLabel="name" itemValue="id" htmlEscape="false" class="required"/>
				</c:if>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">用户分类:</label>
			<div class="controls">
				<form:select path="user.userType" class="input-xlarge required">
					<form:option value="">请选择</form:option>
					<form:options items="${fns:getDictList('user_type')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
					<form:option value="2">其他</form:option>
				</form:select>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group" id="groupIdDiv">
			<label class="control-label">通道分组:</label>
			<div class="controls">
				<form:select path="user.groupId" class="input-xxlarge required">
					<form:option value="">请选择</form:option>
					<form:options items="${fns:getGroupListBz()}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">强制验证码通道:</label>
			<div class="controls">
				<form:select path="user.yzmGatewayFlag" class="input-xlarge required">
					<form:option value="0">否</form:option>
					<form:option value="1">是</form:option>
				</form:select>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>		
		<div class="control-group">
			<label class="control-label">扣费方式:</label>
			<div class="controls">
				<form:radiobuttons path="payMode" items="${fns:getDictList('pay_mode')}" itemLabel="label" itemValue="value" class="required" htmlEscape="false"/>
			</div>
		</div>	
		<div class="control-group">
			<label class="control-label">免审标识:</label>
			<div class="controls">
				<form:select path="user.noCheck" class="input-xlarge" onchange="showReviewCount()" id="noCheck">
					<form:option value="0">手动审核</form:option>
					<form:option value="2">自动审核</form:option>
					<form:option value="1">免审</form:option>
				</form:select>
				<span class="help-inline"><font color="red">*智能审核(设置规则拦截策略可实现直接拦截失败和拦截转人工,不设置拦截规则可实现免审功能)</font> </span>
			</div>
		</div>
		<div class="control-group" id="reviewCount">
			<label class="control-label">审核条数:</label>
			<div class="controls">
				<form:input path="user.reviewCount" htmlEscape="false" maxlength="4" cssClass="required number"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">过滤敏感词:</label>
			<div class="controls">
				<form:select path="user.filterWordFlag" class="input-xlarge" id="filterWordFlag">
					<form:option value="1">是</form:option>
					<form:option value="0">否</form:option>
				</form:select>
			</div>
		</div>			
		<div class="control-group">
		<label class="control-label">签名机制:</label>
			<div class="controls">
				<form:select path="user.usedSign" class="input-xlarge required">
					<form:option value="0">有</form:option>
					<form:option value="1">无</form:option>
				</form:select>
				<span class="help-inline"><font color="red">*发全英文时启用无签机制（全英文支持159个字符）</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">校验签名:</label>
			<div class="controls">
				<form:select path="user.signFlag" class="input-xlarge" id="signFlag">
					<form:option value="1">校验</form:option>
					<form:option value="0">不校验</form:option>
				</form:select>
				<span class="help-inline"><font color="red">*不检验签名即：自定义签名模式</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">签名导流优先:</label>
			<div class="controls">
				<form:select path="user.firstSign" class="input-xlarge" id="firstSign">
					<form:option value="0">否</form:option>
					<form:option value="1">是</form:option>
				</form:select>
				<span class="help-inline"><font color="red">*自定义签名模式下优先通过有报备过签名的通道发送</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">内容导流优先:</label>
			<div class="controls">
				<form:select path="user.userTmplFlag" class="input-xlarge" id="userTmplFlag">
					<form:option value="0">无</form:option>
					<form:option value="1">模板优先</form:option>
				</form:select>
				<span class="help-inline"><font color="red">*启用该功能需提前配置用户模板</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">一级黑名单:</label>
			<div class="controls">
				<form:select path="user.yzmBlacklistFlag" class="input-xlarge" id="yzmBlacklistFlag">
					<form:option value="1">校验</form:option>
					<form:option value="0">不校验</form:option>
				</form:select>
			</div>
		</div>	
		<div class="control-group">
			<label class="control-label">二级黑名单:</label>
			<div class="controls">
				<form:select path="user.sysBlacklistFlag" class="input-xlarge" id="sysBlacklistFlag">
					<form:option value="1">校验</form:option>
					<form:option value="0">不校验</form:option>
				</form:select>
			</div>
		</div>	
		<div class="control-group">
			<label class="control-label">营销黑名单:</label>
			<div class="controls">
				<form:select path="user.marketBlacklistFlag" class="input-xlarge" id="marketBlacklistFlag">
					<form:option value="1">校验</form:option>
					<form:option value="0">不校验</form:option>
				</form:select>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">群发黑名单:</label>
			<div class="controls">
				<form:select path="user.userBlacklistFlag" class="input-xlarge" id="userBlacklistFlag">
					<form:option value="1">校验</form:option>
					<form:option value="0">不校验</form:option>
				</form:select>
			</div>
		</div>
					
		<!-- <div class="control-group">
			<label class="control-label">内容规则配置:</label>
			<div class="controls">
				<form:input path="user.contentRule" htmlEscape="false" maxlength="100" class="input-xlarge"/>
			</div>
		</div> -->
		<div class="control-group">
			<label class="control-label">余额提醒（条数）:</label>
			<div class="controls">
				<form:input path="user.balanceCaution" htmlEscape="false" maxlength="12" cssClass="input-xlarge number"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">余额提醒号码:</label>
			<div class="controls">
				<form:input path="user.cautionMobile" htmlEscape="false" maxlength="50" class="input-xlarge"/>
				<span class="help-inline"><font color="red">仅支持单个手机号码</font> </span>

			</div>
		</div>
		<div class="control-group" id="groupIdDiv">
			<label class="control-label">内容安全过滤库:</label>
			<div class="controls">
				<form:select path="user.ruleGroupId" class="input-xlarge ">
					<form:option value="0">请选择</form:option>
					<form:options items="${fns:getRuleGroup()}" itemLabel="groupName" itemValue="id" htmlEscape="false"/>
				</form:select>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">付费类型:</label>
			<div class="controls">
				<form:select path="user.payType" class="input-xlarge" id="interfaceFlag">
					<form:option value="0">预付费</form:option>
					<form:option value="1">后付费</form:option>
					<span class="help-inline"><font color="red">*用于客户端首页展示</font> </span>
				</form:select>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">用户关键词:</label>
			<div class="controls">
				<form:textarea path="user.keyword" htmlEscape="false" rows="3" class="input-xlarge" id="keyword"/>
				<span class="help-inline"><font color="red">多个以,分开</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">强制签名:</label>
			<div class="controls">
				<form:input path="user.forceSign" htmlEscape="false" maxlength="100" class="input-xlarge"/>
				<span class="help-inline"><font color="red">当强制签名与提交签名不一致时，会在短信内容中强制加上此签名，字段无需加【】</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">限制发送频次:</label>
			<div class="controls">
				<form:select path="user.sendLimit" class="input-xlarge required">
					<form:option value="1">限制</form:option>
					<form:option value="0">不限制</form:option>
				</form:select>
				<span class="help-inline"><font color="red">*默认同一个手机号码当天50条，当月300条限制（平台全局鉴权）</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">验证码发送次数:</label>
			<div class="controls">
				<form:input path="user.yzmSendCount" htmlEscape="false" maxlength="3" cssClass="input-xlarge digits required"/>
				<span class="help-inline"><font color="red">* 0:不限次数（用户级鉴权）</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">营销频控:</label>
			<div class="controls">
				<form:select path="user.marketingControl" htmlEscape="false" maxlength="3" cssClass="input-xlarge required">
					<form:option value="0">不限制</form:option>
					<form:option value="1">限制</form:option>
				</form:select>
				<span class="help-inline"><font color="red">*建议群发短信开启:限制，默认同一个号码1天1条限制，支持代码修改（平台全局鉴权）</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">内容控制策略1:</label>
			<div class="controls">
				<form:select path="user.contentMgIdOne" htmlEscape="false" maxlength="3" cssClass="input-xlarge required">
					<form:option value="0">无</form:option>
					<form:options items="${fns:getContentMgOne()}" itemLabel="contentManage" itemValue="id" htmlEscape="false"/>
				</form:select>
				<span class="help-inline"><font color="red">*建议：无，支持代码修改（平台全局鉴权）</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">内容控制策略2:</label>
			<div class="controls">
				<form:select path="user.contentMgIdTwo" htmlEscape="false" maxlength="3" cssClass="input-xlarge required">
					<form:option value="0">无</form:option>
					<form:options items="${fns:getContentMgTwo()}" itemLabel="contentManage" itemValue="id" htmlEscape="false"/>
				</form:select>
				<span class="help-inline"><font color="red">*建议：无，支持代码修改（平台全局鉴权）</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">发送标签:</label>
			<div class="controls">
				<form:select path="user.sendTag" htmlEscape="false" maxlength="3" cssClass="input-xlarge required">
					<form:option value="S">单发</form:option>
					<form:option value="B">群发</form:option>
				</form:select>
				<span class="help-inline"><font color="red">*该功能可提升同一个通道并发的发送效果，验证码内容走独享验证码队列通道</font> </span>

			</div>
		</div>


		
		<fieldset><legend>HTTP协议接口信息</legend></fieldset>
		<div class="control-group">
			<label class="control-label">是否开通接口:</label>
			<div class="controls">
				<form:select path="user.interfaceFlag" class="input-xlarge" id="interfaceFlag">
					<form:option value="1">开通</form:option>
					<form:option value="0">关闭</form:option>
				</form:select>
			</div>
		</div>	
		<div class="control-group">
			<label class="control-label">apikey:</label>
			<div class="controls">
				<form:input path="user.apikey" htmlEscape="false" maxlength="32" readonly="true" id="apikey" class="input-xlarge"/>&nbsp;<input id="refresh" class="btn" type="button" value="刷新" onclick="onRefresh();"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">绑定ip:</label>
			<div class="controls">
				<form:textarea path="user.whiteIP" rows="3" htmlEscape="false" maxlength="1024" class="input-xxlarge"/>
				
			</div>
		</div>		
		<div class="control-group">
			<label class="control-label">状态报告回调地址:</label>
			<div class="controls">
				<form:input path="user.callbackUrl" htmlEscape="false" maxlength="100" class="input-xlarge"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">短信上行地址:</label>
			<div class="controls">
				<form:input path="user.upUrl" htmlEscape="false" maxlength="100" id="upUrl" class="input-xlarge"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">响应内容类型:</label>
			<div class="controls">
				<form:select path="user.rspContentType" class="input-xlarge" id="rspContentType">
					<form:option value="0">XML</form:option>
					<form:option value="1">JSON</form:option>
				</form:select>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">http速率:</label>
			<div class="controls">
				<form:input path="user.httpSpeed" htmlEscape="false" maxlength="4" cssClass="input-xlarge digits required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>		
		<!-- <div class="control-group">
			<label class="control-label">扩展号:</label>
			<div class="controls">
				<form:input path="user.mmsfrom" htmlEscape="false" maxlength="12" id="mmsfrom"/>
			</div>
		</div> -->
		<%-- <div class="control-group">
			<label class="control-label">用户限额:</label>
			<div class="controls">
				<form:select path="user.userMonthLimit" class="input-xlarge required">
					<form:option value="1">不限额</form:option>
					<form:option value="0">限额</form:option>
				</form:select>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div> --%>
		<fieldset><legend>模拟协议接口信息</legend></fieldset>
		<div class="control-group">
			<label class="control-label">接入号类型:</label>
			<div class="controls">
				<form:select path="user.cmppUserType" class="input-xlarge required">
					<form:option value="1">全号</form:option>
					<form:option value="0">扩展号</form:option>
				</form:select>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">移动扩展号:</label>
			<div class="controls">
				<form:input path="user.extnumYd" htmlEscape="false" maxlength="100" class="input-xlarge"/>
				<span class="help-inline"><font color="red">自定义签名模式下需配置（为匹配客户上行用）</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">联通扩展号:</label>
			<div class="controls">
				<form:input path="user.extnumLt" htmlEscape="false" maxlength="100" class="input-xlarge"/>
				<span class="help-inline"><font color="red">自定义签名模式下需配置（为匹配客户上行用）</font> </span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">电信扩展号:</label>
			<div class="controls">
				<form:input path="user.extnumDx" htmlEscape="false" maxlength="100" class="input-xlarge"/>
				<span class="help-inline"><font color="red">自定义签名模式下需配置（为匹配客户上行用）</font> </span>
			</div>
		</div>
		<%-- <div class="control-group">
			<label class="control-label">上行全号推送:</label>
			<div class="controls">
				<form:select path="user.allnumPush" class="input-xlarge">
					<form:option value="0">否</form:option>
					<form:option value="1">是</form:option>
				</form:select>
			</div>
		</div>			
		<div class="control-group">
			<label class="control-label">截取全号长度:</label>
			<div class="controls">
				<form:input path="user.substringLength" htmlEscape="false" maxlength="2" cssClass="input-xlarge digits required"/>
				<span class="help-inline"><font color="red">*</font> </span>
			</div>
		</div>	 --%>	
		
		<div class="form-actions">
			<shiro:hasPermission name="sys:user:edit"><input id="btnSubmit" class="btn btn-primary" type="button" onclick="javascript:onSave();" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>