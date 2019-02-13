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
    <li><a href="${ctx}/sms/jmsgSmsGatewayTmpl/">信息列表</a></li>
    <li class="active"><a href="${ctx}/sms/jmsgSmsGatewayTmpl/form?id=${jmsgSmsGatewayTmpl.id}">信息<shiro:hasPermission
            name="sms:jmsgSmsGatewayTmpl:edit">${not empty jmsgSmsGatewayTmpl.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission
            name="sms:jmsgSmsGatewayTmpl:edit">查看</shiro:lacksPermission></a></li>
</ul>
<br/>
<form:form id="inputForm" modelAttribute="jmsgSmsGatewayTmpl" action="${ctx}/sms/jmsgSmsGatewayTmpl/save" method="post"
           class="form-horizontal">
    <form:hidden path="id"/>
    <sys:message content="${message}"/>

    <div class="control-group">
        <label class="control-label">通道：</label>
        <div class="controls">
            <form:select path="gatewayId" class="input-xlarge required" >
                <form:option value="" label="请选择"/>
                <form:options items="${fns:getGatewayList()}" itemLabel="label" itemValue="value" htmlEscape="false"/>
            </form:select>
            <span class="help-inline"><font color="red">*选择模版需要绑定的通道</font> </span>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">接入号：</label>
        <div class="controls">
            <form:input path="joinNumber" id="joinNumber" name="joinNumber" class="input-xlarge" type="text" value="" maxlength="20"/>
            <span class="help-inline"><font color="red">通道接入号或者自行填写(不是必填)</font> </span>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">目标模板ID：</label>
        <div class="controls">
            <form:input path="templateId" id="templateId" name="templateId" class="input-xlarge required" type="text" value="" maxlength="40"/>
            <span class="help-inline"><font color="red">*按上游给的ID填写，不要自行填写</font> </span>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">模板名称：</label>
        <div class="controls">
            <form:input path="templateName" id="templateName" name="templateName" class="input-xlarge required" type="text" value="" maxlength="40"/>
            <span class="help-inline"><font color="red">*根据模版内容编写</font> </span>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">模板内容：</label>
        <div class="controls">
            <form:textarea path="templateContent" id="templateContent" name="templateContent" maxlength="255" class="input-xxlarge required" rows="4"/>
            <span class="help-inline"><font color="red">*发送内容（包含签名和内容）</font></span>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">备注：</label>
        <div class="controls">
            <form:textarea path="remarks" id="remarks" name="remarks" maxlength="255" class="input-xxlarge " rows="4"></form:textarea>
        </div>
    </div>

    <div class="form-actions">
        <shiro:hasPermission name="sms:jmsgSmsGatewayTmpl:edit"><input id="btnSubmit" class="btn btn-primary"
                                                                     type="submit"
                                                                     value="保 存"/>&nbsp;</shiro:hasPermission>
        <input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
    </div>
</form:form>
</body>
</html>