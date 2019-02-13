<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>通道队列管理</title>
    <meta name="decorator" content="default"/>
    <script type="text/javascript">
        $(document).ready(function () {

        });

        function page(n, s) {
            $("#pageNo").val(n);
            $("#pageSize").val(s);
            $("#searchForm").submit();
            return false;
        }

    </script>
    <script type="text/javascript">

        $(document).ready(function () {
            //$("#name").focus();
            $("#inputForm").validate({
                submitHandler: function (form) {
                    loading('正在提交，请稍等...');
                    form.submit();
                    closeLoading();
                    },
                errorContainer: "#messageBox",
                errorPlacement: function (error, element) {
                    $("#messageBox").text("输入有误，请先更正。");
                    if (element.is(":checkbox") || element.is(":radio") || element.parent().is(".input-append")) {
                        error.appendTo(element.parent().parent());
                    } else {
                        error.insertAfter(element);
                    }
                }
            });
        });
    </script>
</head>
<body>
<ul class="nav nav-tabs">
    <li><a href="${ctx}/sms/jmsgGatewayQueue/">信息列表</a></li>
    <li class="active"><a href="${ctx}/sms/jmsgGatewayQueue/form?id=${jmsgGatewayQueue.id}">信息<shiro:hasPermission
            name="sms:jmsgGatewayQueue:edit">${not empty jmsgGatewayQueue.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission
            name="sms:jmsgGatewayQueue:edit">查看</shiro:lacksPermission></a></li>
</ul>
<br/>
<form:form id="inputForm" modelAttribute="jmsgGatewayQueue" action="${ctx}/sms/jmsgGatewayQueue/save" method="post"
           class="form-horizontal">
    <form:hidden path="id"/>
    <sys:message content="${message}"/>

    <div class="control-group">
        <label class="control-label">通道：</label>
        <div class="controls">
            <form:select path="gatewayId" class="input-xlarge required" name="queueName">
                <form:option value="" label="请选择"/>
                <form:options items="${fns:getGatewayList()}" itemLabel="label" itemValue="value" htmlEscape="false"/>
            </form:select>
            <span class="help-inline"><font color="red">*</font></span>
        </div>
    </div>

    <div class="control-group">
        <label class="control-label">业务类型：</label>
        <div class="controls">
            <form:select path="" class="input-xlarge required" name="businessType">
                <form:option value="" label="请选择"/>
                <form:option value="1">验证码</form:option>
                <form:option value="2">单发</form:option>
                <form:option value="3">群发</form:option>
            </form:select>
            <span class="help-inline"><font color="red">*</font></span>
        </div>
    </div>

    <div class="control-group">
        <label class="control-label">队列名称：</label>
        <div class="controls">
            <input id="queueName" name="queueName" class="input-xlarge required" type="text" value="" maxlength="45">
            <span class="help-inline"><font color="red">*</font> </span>
        </div>
    </div>

    <div class="control-group">
        <label class="control-label">权重：</label>
        <div class="controls">
            <input id="weight" name="weight" value="1" class="input-xlarge digits" type="text" maxlength="11">
            <span class="help-inline"><font color="red">每种队列所占的速度配比 ,验证码+单发+群发=通道总速度100%权重,输入小于10的数字，默认1</font> </span>
        </div>
    </div>

    <div class="control-group">
        <label class="control-label">备注：</label>
        <div class="controls">
            <textarea id="remark" name="remark" maxlength="255" class="input-xlarge " rows="3" cols="3"></textarea>
        </div>
    </div>

    <div class="form-actions">
        <shiro:hasPermission name="sms:jmsgGatewayQueue:edit"><input id="btnSubmit"class="btn btn-primary"type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
        <input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
    </div>
</form:form>
</body>
</html>