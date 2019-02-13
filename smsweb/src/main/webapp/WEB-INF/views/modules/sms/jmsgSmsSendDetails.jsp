<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <meta name="decorator" content="default"/>
</head>
<style>
    tr {
        display: table-row;
        vertical-align: inherit;
        border-color: inherit;
    }
</style>
<body>
<h4 style="padding-bottom: 8px;padding-top: 8px;padding-left: 20px;color: #62A8EA;">内容发送信息</h4>
<table id="contentTable1" class="table table-striped table-bordered table-condensed">
    <tbody>
    <tr>
        <td width="100"><h5>　手机号码</h5></td>
        <td><h5>${jmsgSmsSend.phone} (中国${fns:getDictLabel(jmsgSmsSend.phoneType,'phone_type',jmsgSmsSend.phoneType)},
            ${fns:getCity(jmsgSmsSend.areaCode).phoneCity}${fns:getCity(jmsgSmsSend.areaCode).phoneProv})</h5></td>
    </tr>

    <tr>
        <td width="100"><h5>　短信内容</h5><br/>　(共<span id="smsContentSize"></span>字,1条)</td>
        <td><span id="smsContent">${jmsgSmsSend.smsContent}</span></td>
    </tr>


    </tbody>
</table>

<h4 style="padding-bottom: 8px;padding-top: 8px;padding-left: 8px;color: #62A8EA;">　队列发送信息</h4>
<table id="contentTable2" class="table table-striped table-bordered table-condensed">
    <thead>
    <tr>
        <th>　发送时间</th>
        <th>发送状态</th>
    </tr>
    </thead>
    <tbody>
    <tr>
        <td>
            　 <fmt:formatDate value="${jmsgSmsSend.createDatetime}" pattern="yyyy-MM-dd HH:mm:ss"/><br>
        </td>
        <td>


            <c:if test="${fns:startsWith(jmsgSmsSend.sendStatus,'T')}">
                <b style="color:#68952f">发送成功</b>
            </c:if>
            <c:if test="${fns:startsWith(jmsgSmsSend.sendStatus,'F')}">
                <b style="color:#cc0400">发送失败</b>
            </c:if>


        </td>
    </tr>
    </tbody>
</table>

<h4 style="padding-bottom: 8px;padding-top: 8px;padding-left: 8px;color: #62A8EA;">　网关提交信息</h4>
<table id="contentTable3" class="table table-striped table-bordered table-condensed">
    <thead>
    <tr>
        <th>　提交时间</th>
        <th>提交状态（<font color="red">0:成功</font>）</th>
        <th>状态描述</th>
        <th>提交编号</th>
        <th>通道</th>
    </tr>
    </thead>
    <tbody>

    <tr>
        <td>
            <fmt:formatDate value="${jmsgSmsReport.submitTimeDate}" pattern="yyyy-MM-dd HH:mm:ss"/>
        </td>
        <td>
            <c:if test="${fns:startsWith(jmsgSmsSend.sendStatus,'T')}">
                <b>成功</b>
            </c:if>
            <c:if test="${fns:startsWith(jmsgSmsSend.sendStatus,'F')}">
                <b style="color:#cc0400">失败</b>
            </c:if>
        </td>
        <td>

        </td>
        <td>
            ${jmsgSmsReport.bizid}
        </td>
        <td>
            ${jmsgSmsSend.channelCode}
        </td>
    </tr>

    </tbody>
</table>

<h4 style="padding-bottom: 8px;padding-top: 8px;padding-left: 8px;color: #62A8EA;">　状态回执信息</h4>
<table id="contentTable4" class="table table-striped table-bordered table-condensed">
    <thead>
    <tr>
        <th>回执时间</th>
        <th>回执状态（<font color="red">DELIVRD:成功</font>）</th>
        <th>回执编号</th>
        <th>通道</th>
    </tr>
    </thead>

    <tbody>
    <thead>
    <tr>
        <td><fmt:formatDate value="${jmsgSmsReport.createtime}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
        <td>${jmsgSmsReport.result}</td>
        <td>${jmsgSmsReport.bizid}</td>
        <td>${jmsgSmsSend.channelCode}</td>
    </tr>
    </thead>
    </tbody>
</table>

<h4 style="padding-bottom: 8px;padding-top: 8px;padding-left: 8px;color: #62A8EA;">　推送结果信息</h4>
<table id="contentTable5" class="table table-striped table-bordered table-condensed">
    <thead>
    <tr>
        <th>推送ID</th>
        <th>推送时间</th>
        <th>推送结果（<font color="red">0:成功</font>）</th>
    </tr>
    </thead>
    <tbody>
    <thead>
    <tr>
        <td></td>
        <td><fmt:formatDate value="${jmsgSmsReport.createtime}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
        </td>
        <td></td>
    </tr>
    </thead>
    </tbody>
</table>


<script type="text/javascript">

    window.onload = function () {
        var tdobj = document.getElementById('smsContent').textContent;
        $('#smsContentSize').text(tdobj.length);

    }
    if (!(self.frameElement && self.frameElement.tagName == "IFRAME")) {
        $("body").prepend("<i id=\"btnMenu\" class=\"icon-th-list\" style=\"cursor:pointer;float:right;margin:10px;\"></i><div id=\"menuContent\"></div>");
        $("#btnMenu").click(function () {
            top.$.jBox('get:/admin/sys/menu/treeselect;JSESSIONID=8927e735a59a4f96b1a76749ecb483af', {
                title: '选择菜单',
                buttons: {'关闭': true},
                width: 300,
                height: 350,
                top: 10
            });
            //if ($("#menuContent").html()==""){$.get("/admin/sys/menu/treeselect", function(data){$("#menuContent").html(data);});}else{$("#menuContent").toggle(100);}
        });
    }
</script>
</body>
</html>