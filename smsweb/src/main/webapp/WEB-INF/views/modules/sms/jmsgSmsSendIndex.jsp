<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>短信下行明细查询</title>
    <meta name="decorator" content="default"/>
    <script src="${ctxStatic}/docheck/docheck.js" type="text/javascript"></script>
    <script type="text/javascript">
        $(document).ready(function () {
            $("#btnMenu").remove();

            $("#btnExport").click(function () {
                top.$.jBox.confirm("确认要导出已发短信数据吗？", "系统提示", function (v, h, f) {
                    if (v == "ok") {
                        $("#searchForm").attr("action", "${ctx}/sms/jmsgSmsSend/userDetailListReport");
                        $("#searchForm").submit();
                    }
                }, {buttonsFocus: 1});
                top.$('.jbox-body .jbox-icon').css('top', '55px');
            });
        });

        function page(n, s) {
            $("#pageNo").val(n);
            $("#pageSize").val(s);
            $("#searchForm").submit();
            return false;
        }

        function onQuery() {
            var url = "${ctx}/sms/jmsgSmsSend/search";

            $("#searchForm").attr("action", url);
            $("#searchForm").submit();
        }

        function showTestModal(smsContent) {
            $('#smsContent').val(smsContent);
            $('#smsContentSize').text(smsContent.length);
            $('#smsContentModal').modal('show');
        }


        function cDayFunc() {
            $("#createDatetimeZ").val('');

            var date = new Date();
            var iDays = compareDate($("#createDatetimeQ").val(), date);
            if (iDays <= 4) {
                var ar = $("#createDatetimeQ").val().split(" ");
                var arr = ar[0].split("-");
                $("#createDatetimeZ").attr("onclick", "WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false,minDate:'" + arr[0] + "-" + arr[1] + "-" + arr[2] + " 00:00:00',maxDate:'" + arr[0] + "-" + arr[1] + "-" + arr[2] + " 23:59:59'});");
            }
            else {
                //1、获取 createTimeQ 选择的时间 d1
                //2、获取当前时间前4天的时间 d2
                var d1 = new Date($("#createDatetimeQ").val());
                var d2 = new Date(date.getTime() - 24 * 3600 * 1000 * 4);
                var lastdate = new Date(d1.getFullYear(), d1.getMonth() + 1, 0);
                //如果两个时间是同一个月，则d2为结束时间
                if (d1.getFullYear() == d2.getFullYear() && d1.getMonth() == d2.getMonth()) {
                    $("#createDatetimeZ").attr("onclick", "WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false,minDate:'#F{$dp.$D(createDatetimeQ);}',maxDate:'" + d2.getFullYear() + "-" + (d2.getMonth() + 1) + "-" + d2.getDate() + " 23:59:59'});");
                }
                else {
                    //不在同一个月，则取d1所在月的最后一天为结束时间
                    $("#createDatetimeZ").attr("onclick", "WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false,minDate:'#F{$dp.$D(createDatetimeQ);}',maxDate:'" + lastdate.getFullYear() + "-" + (lastdate.getMonth() + 1) + "-" + lastdate.getDate() + " 23:59:59'});");
                }
            }
        }

        //计算两个日期的时间间隔
        function compareDate(start, end) {
            var ar = start.split(" ");
            var arr = ar[0].split("-");
            var starttime = new Date(arr[0], parseInt(arr[1] - 1), arr[2]);
            var starttimes = starttime.getTime();
            var intervalTime = end - starttimes;
            //var Inter_Days = ((intervalTime).toFixed(2)/86400000)+1;//加1，是让同一天的两个日期返回一天

            var Inter_Days = parseInt(Math.floor(intervalTime) / 1000 / 60 / 60 / 24) + 1;

            return Inter_Days;
        }

        function onView(task_id,id,phone_number) {
            var send_status = document.getElementById("sendStatus_"+id).innerText;
            var status="";
            if(send_status.startsWith("失败")){
                status="1"
            }
            var url = "searchDetail?bizid=" + id+"&task_id="+task_id+"&send_status="+status+"&phone_number="+phone_number;
            windowOpen(url, '短信明细', 800, 600);
        }
        $(document).ready(function() {
            if($("#createDatetimeQ").val()==""){
                document.getElementById("createDatetimeQ").value = todayS();
                console.log(createDatetimeQ);
            }
            if($("#createDatetimeZ").val()==""){
                document.getElementById("createDatetimeZ").value = todayQ();
                console.log(createDatetimeZ);
            }
        });
        function todayS(){
            var today=new Date();
            var h=today.getFullYear();
            var m=today.getMonth()+1;
            var d=today.getDate();
            var hh=today.getHours();
            var mm=today.getMinutes();
            var ss=today.getSeconds();
            m= m<10?"0"+m:m;
            d= d<10?"0"+d:d;
            return h+"-"+m+"-"+d+" "+00+":"+00+":"+00;
        }
        function todayQ(){
            var today=new Date();
            var h=today.getFullYear();
            var m=today.getMonth()+1;
            var d=today.getDate();
            var hh=today.getHours();
            var mm=today.getMinutes();
            var ss=today.getSeconds();
            m= m<10?"0"+m:m;
            d= d<10?"0"+d:d;
            hh = hh < 10 ? "0" + hh:hh;
            mm = mm < 10 ? "0" +  mm:mm;
            ss = ss < 10 ? "0" + ss:ss;
            return h+"-"+m+"-"+d+" "+hh+":"+mm+":"+ss;
        }

        <!-- 触发弹窗功能 -->
        function initUpdate(){
            $('#taskModal').modal('show');
        }

        <!-- 发送任务--短信明细下载任务 -->
        function onSendTask() {
            var exportTaskName = $("#exportTaskName").val();
            $("#task_name").val(exportTaskName);
            var url = "${ctx}/sms/jmsgSmsSend/sendTask";
            $("#searchForm").attr("action", url);
            $("#searchForm").submit();
        }
    </script>
</head>
<body>
<ul class="nav nav-tabs">
    <li class="active"><a href="${ctx}/sms/jmsgSmsSend/resultInit">信息列表</a></li>
    <%-- <li  ${queryType eq 'pending' ? 'class="active"' :''}><a href="${ctx}/sms/jmsgSmsSend/userPendingInit">用户待发送列表</a></li> --%>
</ul>
<form:form id="searchForm" modelAttribute="jmsgSmsSend" action="${ctx}/sms/jmsgSmsSend/search" method="post"
           class="breadcrumb form-search">
    <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
    <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
    <ul class="ul-form">
        <li><label>发送时间：</label>
            <input  path="createDatetimeQ" name="createDatetimeQ" id="createDatetimeQ" type="text" readonly="readonly" maxlength="20"
                   class="input-medium Wdate"
                   value="<fmt:formatDate value="${jmsgSmsSend.createDatetimeQ}" pattern="yyyy-MM-dd HH:mm:ss"/>"
                   onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:false,onpicked:cDayFunc,maxDate:'%y-%M-%d 23:59:59'});"/>-
            <input  path="createDatetimeZ" name="createDatetimeZ" id="createDatetimeZ" type="text" readonly="readonly" maxlength="20"
                   class="input-medium Wdate"
                   value="<fmt:formatDate value="${jmsgSmsSend.createDatetimeZ}" pattern="yyyy-MM-dd HH:mm:ss"/>"
                   onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:true});"/>
        </li>
        <li><label>用户名称：</label>
            <sys:treeselect id="user" name="user.id" value="${jmsgSmsSend.user.id}" labelName="user.name" labelValue="${jmsgSmsSend.user.name}"
                            title="用户" url="/sys/office/treeData?type=3" cssClass="input-small" allowClear="true" notAllowSelectParent="true"/>
        </li>
        <li><label>用户id：</label>
            <input path="userId" id="userId" name="userId" value="${jmsgSmsSend.userId}" class="input-medium" type="text"  maxlength="20">
        </li>
        <li><label>通道名称：</label>
            <form:select path="channelCode" class="input-xlarge">
                <form:option value="" label="全部"/>
                <form:options items="${fns:getGatewayList()}" itemLabel="label" itemValue="value" htmlEscape="false"/>
            </form:select>
        </li>

        <li><label>手机号码：</label>
            <input  path="phone" type="text" name="phone" maxlength="20" class="input-medium required" value="${jmsgSmsSend.phone}"
                   id="phone">
        </li>

        <li><label>短信内容：</label>
            <input path="smsContent" id="smsContent" name="smsContent" value="${jmsgSmsSend.smsContent}" class="input-medium" type="text" value="" maxlength="20">
        </li>
        <li><label>运营商：</label>
            <form:select path="phoneType" name="phoneType" class="input-medium">
                <form:option value="" label="全部"/>
                <form:options items="${fns:getDictList('phone_type')}" itemLabel="label" itemValue="value"
                              htmlEscape="false"/>
            </form:select>
        </li>

        <li><label>省份：</label>
            <!-- 2 -->
            <form:select path="areaCode" class="input-medium " name="areaCode">
                <form:option value="" label="全部"/>
                <form:options items="${fns:getDictList('phone_province')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
            </form:select>
        </li>
        <li><label>发送状态：</label>
            <form:select path="sendStatus" class="input-medium">
                <form:option value="" label="全部"/>
                <form:option value="T" label="成功"/>
                <form:option value="F" label="失败"/>
                <form:option value="R" label="审核中"/>
            </form:select>
        </li>
        <li><label>状态报告码：</label>
            <input path="hzzt" id="hzzt" name="hzzt" class="input-medium" type="text" value="" maxlength="64">
        </li>
        <%--<li><label>推送状态：</label>--%>
            <%--<form:select path="" class="input-medium">--%>
                <%--<form:option value="" label="全部"/>--%>
                <%--<form:option value="1" label="待推"/>--%>
                <%--<form:option value="0" label="成功"/>--%>
                <%--<form:option value="-1" label="失败"/>--%>
                <%--<form:option value="-2" label="不推"/>--%>
            <%--</form:select>--%>
        <%--</li>--%>

        <li><label>短信ID：</label>
            <input  path="taskId" id="taskId" name="taskId" class="input-medium" type="text" value="${jmsgSmsSend.taskId}" maxlength="64">
        </li>

        <li><label>批次号：</label>
            <input path="batchNumber" name="batchNumber" id="batchNumber" type="text" value="${jmsgSmsSend.batchNumber}"  maxlength="64" class="input-medium required" >
        </li>


        <li class="btns">
            <input id="btnSubmit" class="btn btn-primary" type="button" value="查询" onclick="javascript:onQuery();"/>
            <!-- <input id="btnExport" class="btn btn-primary" type="button" value="导出"/> -->
            <input id="btnTaskExport" class="btn btn-primary" type="button" value="数据导出" onclick="javascript:initUpdate();">
        </li>
        <li class="clear   ix"></li>
    </ul>
    <input type="text" style="display: none" id="task_name" name="task_name">
</form:form>
<sys:message content="${message}"/>
<table id="contentTable" class="table table-striped table-bordered table-condensed">
    <thead>
    <tr>
        <th><input type="checkbox" onclick="doCheckAll(this,'id', this.checked)"></th>
        <shiro:hasPermission name="sms:jmsgGatewaySign:edit">
            <th>操作</th>
        </shiro:hasPermission>
        <th>用户ID/用户名称</th>
        <th>机构名称　　　　</th>
        <!-- <th>短信内容　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　</th> -->
        <th>计数/短信类型</th>
        <th>手机号码/运营商</th>
        <th>省份/地区</th>
        <th>发送状态/状态报告　　</th>
        <th>提交时间/回执时间　　</th>
        <th>归属通道分组/通道ID/名称　　　　　　</th>
        <th>接入号/业务类型</th>
        <!-- <th>耗时（秒）　　　　</th> -->
        <th>推送状态</th>
        <th>短信ID/批次号</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${page.list}" var="jmsgSmsSend">
        <tr onclick="selectTr(this, 'jmsgSmsSend.id' ,'id')">
            <td rowspan="2" class="checked">
                <input type="checkbox" name="id" id="${jmsgSmsSend.id}" value="${jmsgSmsSend.id}"
                       onclick="if(this.checked){this.checked=false;}else{this.checked=true;}">
            </td>
            <td rowspan="2">
                <a href='javascript:onView("${jmsgSmsSend.taskId}","${jmsgSmsSend.id}","${jmsgSmsSend.phone}")'
                   class="but but_xs but_success">查看</a>
            </td>
            <td>
                    ${jmsgSmsSend.user.id}<br>
                    ${fns:getUserById(jmsgSmsSend.user.id).name}

            </td>
            <td>
                    ${fns:getUserById(jmsgSmsSend.user.id).company.name}
            </td>
            <td>
                <b>1</b><br>
                    <%--普通短信--%>
            </td>
            <td>
                <b style="color: #62A8EA;">${jmsgSmsSend.phone}</b><br>
                    ${fns:getDictLabel(jmsgSmsSend.phoneType,'phone_type',jmsgSmsSend.phoneType)}
            </td>
            <td>
                <b style="background-color:  #F2A654;color: white;">
                        ${fns:getDictLabel(fn:substring(jmsgSmsSend.areaCode,0,2),'phone_province',jmsgSmsSend.areaCode)}
                </b>
                <br>

                    ${fns:getCity(jmsgSmsSend.areaCode).phoneCity}
            </td>
            <td id="sendStatus_${jmsgSmsSend.id}">
                    <%--${jmsgSmsSend.sendStatus eq "T000" ? "成功":"失败"}--%>
                        <c:if test="${fns:startsWith(jmsgSmsSend.sendStatus,'T')}">
                            <b>成功</b>
                        </c:if>
                        <c:if test="${fns:startsWith(jmsgSmsSend.sendStatus,'F')}">
                            <b style="color:#cc0400">失败</b>
                        </c:if>
                <br>

                        <c:if test="${fns:getSmsByBizid(jmsgSmsSend.id).stat eq 'DELIVRD'}">
                            <b style="color:#70A532">成功（DELIVRD）</b>
                        </c:if>

                        <c:if test="${fns:startsWith(jmsgSmsSend.sendStatus,'F')}">
                            <b>状态未知（P100）</b>
                        </c:if>

                        <%--<c:if test="${fns:startsWith(jmsgSmsSend.sendStatus,'F')}">--%>
                            <%--失败</br>--%>
                        <%--</c:if>--%>

                        <%--<c:if test="${!fns:startsWith(jmsgSmsSend.sendStatus,'F')}">--%>
                            <%--<c:if test="${fns:startsWith(jmsgSmsSend.reportStatus,'P')}">--%>
                                <%--待返回--%>
                            <%--</c:if>--%>
                            <%--<c:if test="${fns:startsWith(jmsgSmsSend.reportStatus,'T')}">--%>
                                <%--成功--%>
                            <%--</c:if>--%>
                            <%--<c:if test="${fns:startsWith(jmsgSmsSend.reportStatus,'F')}">--%>
                                <%--失败--%>
                            <%--</c:if>--%>
                        <%--</c:if>--%>

            </td>
            <td>
                <fmt:formatDate value="${jmsgSmsSend.createDatetime}" pattern="yyyy-MM-dd HH:mm:ss"/><br>
                <b style="color: #3AA99E;">
                    <fmt:formatDate value="${fns:getSmsByBizid(jmsgSmsSend.id).createtime}" pattern="yyyy-MM-dd HH:mm:ss"/>
                </b>
            </td>
            <td>
                <!-- 用户id查询通道分组 -->
                    ${fns:getJmsgGroupByUserId(jmsgSmsSend.user.id).name}<br>


                <b style="color:#F2A654;">${jmsgSmsSend.channelCode}</b><br>
                <!-- 通道id查询通道名 -->
                    ${fns:getGatewayInfo(jmsgSmsSend.channelCode).gatewayName}

            </td>
            <td>
                    ${jmsgSmsSend.spNumber}<br>
                <b style="background-color:  #3AA99E;color: white;">
                        ${fns:getSmsType(jmsgSmsSend.smsType)}
                </b>
            </td>

            <td>
                    <%--${queryType eq 'pending' ? '待发送':--%>
                            <%--fns:getSendResultNew(jmsgSmsSend.payType,jmsgSmsSend.sendStatus,jmsgSmsSend.reportStatus)}--%>
                            ${fns:getSmsByBizid(jmsgSmsSend.id).result eq "0" ? "成功":"待推"}
            </td>
            <td>
                    ${jmsgSmsSend.taskId}
                   <br>
                            ${jmsgSmsSend.id}
            </td>
        </tr>
        <tr onclick="selectTr(this, 'jmsgSmsSend.id' ,'id')">

            <td colspan="11">
                <b style="color: #526069">


                        ${jmsgSmsSend.smsContent}

                </b>
            </td>
        </tr>
    </c:forEach>
    </tbody>
</table>
<div class="pagination">${page}</div>

<%--<div class="modal fade" style="display:none;" id="smsContentModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">--%>
<%--<div class="modal-dialog">--%>
<%--<div class="modal-content">--%>
<%--<div class="modal-header">--%>
<%--<button type="button" class="close" --%>
<%--data-dismiss="modal" aria-hidden="true">--%>
<%--&times;--%>
<%--</button>--%>
<%--<h4 class="modal-title" id="myModalLabel">--%>
<%--短信内容--%>
<%--</h4>--%>
<%--</div>--%>
<%--<div class="modal-body">--%>
<%--<form id="gateWaySendForm" class="form-horizontal">--%>
<%--<div class="control-group">--%>
<%--<div class="controls1">--%>
<%--<textarea style="width:498px;" readonly="readonly" name="smsContent" id="smsContent" rows="6" cols="8"></textarea>--%>
<%--共 <label name="smsContentSize" id="smsContentSize"></label>  个字。--%>
<%--</div>--%>
<%--</div>--%>
<%--</form>--%>
<%--</div>--%>
<%--</div><!-- /.modal-content -->--%>
<%--</div><!-- /.modal -->--%>
<%--</div>--%>

<!-- 数据导出弹窗 -->
<div class="modal fade" style="display:none;" id="taskModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel1" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close"
                        data-dismiss="modal" aria-hidden="true">
                    &times;
                </button>
                <h4 class="modal-title" id="myModalLabel">
                    数据导出任务名称
                </h4>
            </div>
            <div class="modal-body">
                <div class="control-group">
                    <input type="text" name="exportTaskName" id="exportTaskName" style="width: 95%;" maxlength="40">
                </div>
                <div class="control-group">
                    <label style="color:red;">说明：1、按照查询条件获取的数据以任务的方式导出；</label>
                    <label style="color:red;margin-left: 38px;">2、导出结果可在【高级应用管理】下【任务处理结果查询】查看；</label>
                    <label style="color:red;margin-left: 38px;">3、为了提升导出效率，现将转译字段去掉；</label>
                    <label style="color:red;margin-left: 38px;">4、导出字段包括:用户ID、用户名称、手机号码、短信内容、发送状态、状态报告、提交时间、通道ID、短信ID、批次号。</label>
                </div>
            </div>
        </div>
        <div class="modal-footer">
            <input id="btnSubmit1" class="btn btn-primary" type="button" value="创建任务" class="btn btn-primary" onclick="javascript:onSendTask();"/>
            <input id="btnSubmit2" class="btn btn-primary" type="button" value="关 闭" data-dismiss="modal" aria-hidden="true"/>
        </div>
    </div>
</div>
</body>
</html>