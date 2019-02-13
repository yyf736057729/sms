<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>

<html>
<head>
    <title>通道分组管理</title>
    <meta name="decorator" content="default"/>
    <script type="text/javascript">

        $(document).ready(function () {
            //$("#name").focus();
            $("#inputForm").validate({
                submitHandler: function (form) {
                    loading('正在提交，请稍等...');
                    form.submit();
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
    <script src="${ctxStatic}/docheck/docheck.js" type="text/javascript"></script>
</head>
<body>
<ul class="nav nav-tabs">
    <li><a href="${ctx}/sms/jmsgGatewayGroup/">信息列表</a></li>
    <li class="active"><a href="${ctx}/sms/jmsgGatewayGroup/form?id=${jmsgGatewayGroup.id}">信息<shiro:hasPermission
            name="sms:jmsgGatewayGroup:edit">${not empty jmsgGatewayGroup.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission
            name="sms:jmsgGatewayGroup:edit">查看</shiro:lacksPermission></a></li>
</ul>
<br/>
<form:form id="inputForm" modelAttribute="jmsgGatewayGroup" action="${ctx}/sms/jmsgGatewayGroup/save" method="post"
           class="form-horizontal">
    <form:hidden path="id"/>
    <sys:message content="${message}"/>
    <div class="control-group">
        <label class="control-label">分组ID：</label>
        <div class="controls">
            <form:select path="groupId" class="input-xlarge required">
                <form:option value="" label="请选择"/>
                <form:options items="${fns:getGroupList()}" itemLabel="label" itemValue="value" htmlEscape="false"/>
            </form:select>
            <span class="help-inline"><font color="red">*</font> </span>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">运营商：</label>
        <div class="controls">
            <form:select path="phoneType" class="input-xlarge required">
                <form:option value="" label="请选择"/>
                <form:options items="${fns:getDictList('phone_type')}" itemLabel="label" itemValue="value"
                              htmlEscape="false"/>
            </form:select>
            <span class="help-inline"><font color="red">*</font> </span>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">通道：</label>
        <div class="controls">
            <form:select path="gatewayId" class="input-xlarge required">
                <form:option value="" label="请选择"/>
                <form:options items="${fns:getGatewayList()}" itemLabel="label" itemValue="value" htmlEscape="false"/>
            </form:select>
            <span class="help-inline"><font color="red">*</font></span>
        </div>
    </div>
    <%--<div class="control-group">--%>
        <%--<label class="control-label">省份：</label>--%>
        <%--<div class="controls">--%>
            <%--<form:select path="provinceId" class="input-xlarge required">--%>
                <%--<form:option value="" label="请选择"/>--%>
                <%--<form:option value="10" label="全国"/>--%>
                <%--<form:options items="${fns:getDictList_2('phone_province')}" itemLabel="label" itemValue="value"--%>
                              <%--htmlEscape="false"/>--%>
            <%--</form:select>--%>
            <%--<span class="help-inline"><font color="red">*</font> </span>--%>
        <%--</div>--%>
    <%--</div>--%>

    <div class="controls">
            <input type="checkbox"  style="zoom:130%" onclick="doCheckAll2(this,'id', this.checked)">

            <c:forEach items="${fns:getDictList_2('phone_province')}" var="provincelist">
                <span><input id="${provincelist}" name="id" style="zoom:130%" class="required" type="checkbox"
                         value=${provincelist.value}><label for=${provincelist}>${provincelist}</label></span>
            </c:forEach>
            <span class="help-inline"><font color="red">*需要屏蔽下发的省份不用勾选，依据实际业务需求选择</font> </span>
    </div>

    <div class="control-group">
        <label class="control-label">等级：</label>
        <div class="controls">
            <form:input path="level" htmlEscape="false" maxlength="2" class="input-xlarge required number"/>
            <span class="help-inline"><font color="red">*请输入0-99,数字越大优先级越高</font> </span>
        </div>
    </div>
    <div class="form-actions">
        <shiro:hasPermission name="sms:jmsgGatewayGroup:edit"><input id="btnSubmit" class="btn btn-primary"
                                                                     type="submit"
                                                                     value="保 存"/>&nbsp;</shiro:hasPermission>
        <input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
    </div>
</form:form>
</body>
</html>