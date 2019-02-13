<%@ taglib prefix="from" uri="http://www.springframework.org/tags/form" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>通道信息管理</title>
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
</head>
<body>
<ul class="nav nav-tabs">
    <li><a href="${ctx}/sms/jmsgGatewayInfo/">信息列表</a></li>
    <li class="active"><a href="${ctx}/sms/jmsgGatewayInfo/form?id=${jmsgGatewayInfo.id}">信息<shiro:hasPermission
            name="sms:jmsgGatewayInfo:edit">${not empty jmsgGatewayInfo.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission
            name="sms:jmsgGatewayInfo:edit">查看</shiro:lacksPermission></a></li>
</ul>
<br/>
<form:form id="inputForm" modelAttribute="jmsgGatewayInfo" action="${ctx}/sms/jmsgGatewayInfo/save" method="post"
           class="form-horizontal">
    <sys:message content="${message}"/>

    <div class="control-group">
        <label class="control-label">通道ID：</label>
        <div class="controls">
            <form:input path="id" htmlEscape="false" maxlength="36" class="input-xlarge required"
                        readonly="${not empty jmsgGatewayInfo.id && jmsgGatewayInfo.id !='' ?'true':'false'}"/>
            <span class="help-inline"><font color="red">*</font> </span>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">通道名字：</label>
        <div class="controls">
            <form:input path="gatewayName" htmlEscape="false" maxlength="36" class="input-xlarge required"/>
            <span class="help-inline"><font color="red">*</font> </span>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">通道备注：</label>
        <div class="controls">
            <form:input path="remark" htmlEscape="false" maxlength="50" class="input-xlarge"/>
        </div>
    </div>

    <div class="control-group">
        <label class="control-label">网关接入IP：</label>
        <div class="controls">
            <form:input path="host" htmlEscape="false" maxlength="20" class="input-xlarge required"/>
            <span class="help-inline"><font color="red">*</font> </span>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">网关接入端口：</label>
        <div class="controls">
            <form:input path="port" htmlEscape="false" maxlength="11" class="input-xlarge required number"/>
            <span class="help-inline"><font color="red">*</font> </span>
        </div>
    </div>

    <div class="control-group">
        <label class="control-label">网关账号：</label>
        <div class="controls">
            <form:input path="sourceAddr" htmlEscape="false" maxlength="20" class="input-xlarge required"/>
            <span class="help-inline"><font color="red">*</font> </span>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">网关密码：</label>
        <div class="controls">
            <form:input path="sharedSecret" htmlEscape="false" maxlength="20" class="input-xlarge required"/>
            <span class="help-inline"><font color="red">*</font> </span>
        </div>
    </div>

    <div class="control-group">
        <label class="control-label">协议版本号：</label>
        <div class="controls">
            <form:input path="version" htmlEscape="false" maxlength="5" class="input-xlarge required"/>
            <span class="help-inline"><font color="red">*</font> </span>
        </div>
    </div>

    <div class="control-group">
        <label class="control-label">通道类型：</label>
        <div class="controls">
            <form:input path="type" htmlEscape="false" maxlength="10" class="input-xlarge required"/>
            <span class="help-inline"><font color="red">*</font> </span>
        </div>
    </div>

    <%--<div id="protoDiv">--%>
        <%--<div class="control-group"><label class="control-label">通道模板：</label>--%>
            <%--<div class="controls"><select name="httProto" id="httProto" value="1" class="input-xlarge valid">--%>
                <%--<option value="">请选择</option>--%>
                <%--<option value="1" selected="">CMPP</option>--%>
            <%--</select></div>--%>
        <%--</div>--%>
    <%--</div>--%>

    <div class="control-group">
        <label class="control-label">模板参数：</label>
        <div class="controls">
            <div id="paramDiv" style="display: block;">
                <div class="control-group"><label class="control-label">计费用户类型：</label>
                    <div class="controls">
                        <from:input type="text" path="feeUserType"  maxlength="100"
                               class="input-xlarge "/>
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label">资费代码：</label>
                    <div class="controls">
                        <from:input type="text" path="feeCode" maxlength="100"
                               class="input-xlarge "/>
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label">资费类别：</label>
                    <div class="controls">
                        <from:input type="text" path="feeType"  maxlength="100"
                               class="input-xlarge "/>
                    </div>
                </div>
                <div class="control-group"><label class="control-label">被计费用户的号码：</label>
                    <div class="controls">
                        <from:input type="text" path="feeTerminalId"  maxlength="100"
                               class="input-xlarge "/></div>
                </div>
                <div class="control-group"><label class="control-label">被计费用户的号码类型：</label>
                    <div class="controls">
                        <from:input type="text" path="feeTerminalType"
                               maxlength="100" class="input-xlarge "/></div>
                </div>
            </div>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">网关下行接入号：</label>
        <div class="controls">
            <form:input path="spNumber" htmlEscape="false" maxlength="20" class="input-xlarge "/>
            <!-- <span class="help-inline"><font color="red">*</font> </span> -->
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">企业代码：</label>
        <div class="controls">
            <from:input path="gwCorpId"  class="input-xlarge " type="text" value="" maxlength="10"/>
            <span class="help-inline"><font color="red">SP_Id,一般为6位数（HTTP协议为空）</font> </span>
        </div>
    </div>

    <div class="control-group">
        <label class="control-label">业务代码：</label>
        <div class="controls">
            <from:input path="gwServiceId"  class="input-xlarge " type="text" value="" maxlength="45"/>
            <span class="help-inline"><font color="red">Service_Id,一般由数字和字母组合（HTTP协议为空）</font> </span>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">应用IP：</label>
        <div class="controls">
            <form:input path="appHost" htmlEscape="false" maxlength="20" class="input-xlarge required"/>
            <span class="help-inline"><font color="red">*</font> </span>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">应用代码：</label>
        <div class="controls">
            <form:input path="appCode" htmlEscape="false" maxlength="10" class="input-xlarge required"/>
            <span class="help-inline"><font color="red">*</font> </span>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">状态获取方式：</label>
        <div class="controls">
            <form:select path="reportGetFlag" htmlEscape="false" class="input-xlarge required">
                <form:option value="0">主动查询</form:option>
                <form:option value="1">异步通知</form:option>
            </form:select>
            <span class="help-inline"><font color="red">*</font> </span>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">消息来源：</label>
        <div class="controls">
            <form:input path="corpId" htmlEscape="false" maxlength="20" class="input-xlarge "/>
            <span class="help-inline">如:msg_src</span>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">支持自定义签名：</label>
        <div class="controls">
            <form:select path="customFlag" htmlEscape="false" class="input-xlarge ">
                <form:option value="0">否</form:option>
                <form:option value="1">是</form:option>
            </form:select>
        </div>
    </div>

    <div class="control-group">
        <label class="control-label">通道主状态：</label>
        <div class="controls">
            <form:select path="status" htmlEscape="false" class="input-xlarge ">
                <form:option value="1">启用</form:option>
                <form:option value="0">禁用</form:option>
            </form:select>
        </div>
    </div>

    <div class="control-group">
        <label class="control-label">通道端签名：</label>
        <div class="controls">
            <form:select path="gatewaySign" htmlEscape="false" class="input-xlarge ">
                <form:option value="1">是</form:option>
                <form:option value="0">否</form:option>
            </form:select>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">支持长短信：</label>
        <div class="controls">
            <form:select path="supportLongMsg" htmlEscape="false" class="input-xlarge ">
                <form:option value="1">是</form:option>
                <form:option value="0">否</form:option>
            </form:select>
        </div>
    </div>

    <div class="control-group">
        <label class="control-label">协议实现：</label>
        <div class="controls">
            <form:input path="extClass" htmlEscape="false" maxlength="100" class="input-xxlarge "/>
            <span class="help-inline">完整类名,如：com.tingfv.http.SmsApiJYCSMS</span>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">扩展参数：</label>
        <div class="controls">
            <form:textarea path="extParam" htmlEscape="false" rows="4" class="input-xxlarge" id="extParam"/>
            <span class="help-inline">json格式,如：{"local-user":"123", "local-passwd":"passwd"}</span>
        </div>
    </div>

    <div class="control-group">
        <label class="control-label">本地主机：</label>
        <div class="controls">
            <form:input path="localHost" htmlEscape="false" maxlength="20" class="input-xlarge "/>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">本地端口：</label>
        <div class="controls">
            <form:input path="localPort" htmlEscape="false" maxlength="11" class="input-xlarge number"/>
        </div>
    </div>

    <div class="control-group">
        <label class="control-label">通道状态：</label>
        <div class="controls">
            <form:input path="gatewayState" htmlEscape="false" maxlength="1" class="input-xlarge " readonly="true"/>
        </div>
    </div>
    <form:hidden path="readTimeout" value="${jmsgGatewayInfo.readTimeout}"/>
    <form:hidden path="reconnectInterval" value="${jmsgGatewayInfo.reconnectInterval}"/>
    <form:hidden path="transactionTimeout" value="${jmsgGatewayInfo.transactionTimeout}"/>
    <form:hidden path="heartbeatInterval" value="${jmsgGatewayInfo.heartbeatInterval}"/>
    <form:hidden path="heartbeatNoresponseout" value="${jmsgGatewayInfo.heartbeatNoresponseout}"/>
    <form:hidden path="debug" value="${jmsgGatewayInfo.debug}"/>
    <div class="control-group">
        <label class="control-label">支持外省：</label>
        <div class="controls">
            <form:select path="isOutProv" htmlEscape="false" class="input-xlarge ">
                <form:option value="">请选择</form:option>
                <form:option value="1">是</form:option>
                <form:option value="0">否</form:option>
            </form:select>
        </div>
    </div>

    <div class="control-group">
        <label class="control-label">接收速率：</label>
        <div class="controls">
            <form:input path="readLimit" htmlEscape="false" maxlength="11" class="input-xlarge number"/>
        </div>
    </div>
    <div class="control-group">
        <label class="control-label">请求速率：</label>
        <div class="controls">
            <form:input path="writeLimit" htmlEscape="false" maxlength="11" class="input-xlarge number"/>
        </div>
    </div>

    <div class="control-group">
        <label class="control-label">单次处理数量：</label>
        <div class="controls">
            <form:input path="batchSize" htmlEscape="false" maxlength="11" class="input-xlarge number"/>
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
    <div class="form-actions">
        <shiro:hasPermission name="sms:jmsgGatewayInfo:edit"><input id="btnSubmit" class="btn btn-primary" type="submit"
                                                                    value="保 存"/>&nbsp;</shiro:hasPermission>
        <input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
    </div>
</form:form>
</body>
</html>