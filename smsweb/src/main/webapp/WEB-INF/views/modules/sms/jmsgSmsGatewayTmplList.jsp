<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>通道模板管理</title>
    <meta name="decorator" content="default"/>
    <script src="${ctxStatic}/docheck/docheck.js" type="text/javascript"></script>
    <script type="text/javascript">
        $(document).ready(function () {

        });

        function page(n, s) {
            $("#pageNo").val(n);
            $("#pageSize").val(s);
            $("#searchForm").submit();
            return false;
        }

        function onUpdate() {
            var id = $("#queueId").val();
            var queueName = $("#queueName").val();
            if (queueName == null || queueName == '') {
                alertx('请输入队列名');
                return;
            }
            var weight = $("#weight").val();
            if (weight == null || weight == '') {
                alertx('请输入权重值');
                return;
            }
            var remarks = $("#remarks").val();

            <%--$("#searchForm").attr("action","${ctx}/sms/jmsgSmsGatewayTmpl/edit?remarks="+remarks+"&weight="+weight+"&queueid="+id+"&queueName="+queueName);--%>
            <%--$("#searchForm").submit();--%>

            $.ajax({
                async: false,
                cache: false,
                traditional: true,
                url: "${ctx}/sms/jmsgSmsGatewayTmpl/edit",
                type: "POST",
                data: {
                    "remarks": remarks,
                    "weight": weight,
                    "queueid": id,
                    "queueName": queueName
                },
                dataType: "json",
                error: function (data) {                //后台处理出错
                    console.log("操作错误");
                },
                success: function (data) {
                    document.location.href = "${ctx}/sms/jmsgSmsGatewayTmpl";
                }

            })
        }

        function onDelete(){
            var ids = getCheckboxValue("id");
            if(!ids){
                alertx("请选择要删除的数据");
            }else{

                top.$.jBox.confirm("确认要删除该数据吗？","系统提示",function(v,h,f){
                    if(v=="ok"){
                        $("#searchForm").attr("action","${ctx}/sms/jmsgSmsGatewayTmpl/batchDelete?ids="+ids);
                        $("#searchForm").submit();
                    }
                },{buttonsFocus:1});
                top.$('.jbox-body .jbox-icon').css('top','55px');
            }
        }
    </script>
</head>
<body>
<ul class="nav nav-tabs">
    <li class="active"><a href="${ctx}/sms/jmsgSmsGatewayTmpl/">信息列表</a></li>
    <shiro:hasPermission name="sms:jmsgSmsGatewayTmpl:edit">
        <li><a href="${ctx}/sms/jmsgSmsGatewayTmpl/form">信息添加</a></li>
    </shiro:hasPermission>
</ul>
<form:form id="searchForm" modelAttribute="jmsgSmsGatewayTmpl" action="${ctx}/sms/jmsgSmsGatewayTmpl/" method="post"
           class="breadcrumb form-search">
    <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
    <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
    <ul class="ul-form">

        <li><label>通道名称：</label>
            <form:select path="" class="input-xlarge" name="gatewayName">
                <form:option value="" label="全部"/>
                <form:options items="${fns:getGatewayList()}" itemLabel="label" itemValue="label" htmlEscape="false"/>
            </form:select>
        </li>

        <li><label>目标模板id：</label>
            <input id="templateId" name="templateId" class="input-medium" type="text" value="" maxlength="20">
        </li>

        <li><label>创建时间：</label>
            <input name="dateTimeQ" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
                   value="<fmt:formatDate value="${jmsgSmsTask.sendDatetimeQ}" pattern="yyyy-MM-dd HH:mm:ss"/>"
                   onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:true});"/> -
            <input name="dateTimeZ" type="text" readonly="readonly" maxlength="20" class="input-medium Wdate"
                   value="<fmt:formatDate value="${jmsgSmsTask.sendDatetimeZ}" pattern="yyyy-MM-dd HH:mm:ss"/>"
                   onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss',isShowClear:true});"/>
        </li>

        <li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
            <input id="btnDelete" class="btn btn-primary" type="button" value="批量删除" onclick="javascript:onDelete()"/>
        </li>
        <li class="clearfix"></li>
    </ul>
</form:form>
<sys:message content="${message}"/>
<table id="contentTable" class="table table-striped table-bordered table-condensed">
    <thead>
    <tr>
        <th><input type="checkbox" onclick="doCheckAll(this,'id', this.checked)"></th>
        <shiro:hasPermission name="sms:jmsgSmsGatewayTmpl:edit"><th>操作　　　　　　</th></shiro:hasPermission>

        <th>通道</th>
        <th>目标模板ID</th>
        <th>模板名称</th>
        <th>模板内容</th>
        <th>创建时间</th>

    </tr>
    </thead>
    <tbody>
    <c:forEach items="${page.list}" var="jmsgSmsGatewayTmpl">


        <tr onclick="selectTr(this, '${jmsgSmsGatewayTmpl.id}' ,'id')">

            <shiro:hasPermission name="sms:jmsgSmsGatewayTmpl:edit">
                <td>
                    <input type="checkbox" name="id" id="${jmsgSmsGatewayTmpl.id}" value="${jmsgSmsGatewayTmpl.id}"
                           onclick="if(this.checked){this.checked=false;}else{this.checked=true;}">
                </td>

                <td>

                    <a href="${ctx}/sms/jmsgSmsGatewayTmpl/form?id=${jmsgSmsGatewayTmpl.id}">修改</a>

                    <a href="/admin/sms/jmsgSmsGatewayTmpl/delete?id=${jmsgSmsGatewayTmpl.id}"
                       onclick="return confirmx('确认要删除该通道模板吗？', this.href)">删除</a>

                </td>

            </shiro:hasPermission>

            <td>
                    ${jmsgSmsGatewayTmpl.gatewayName}
            </td>
            <td>
                    ${jmsgSmsGatewayTmpl.templateId}
            </td>
            <td>
                    ${jmsgSmsGatewayTmpl.templateName}
            </td>
            <td>
                    ${jmsgSmsGatewayTmpl.templateContent}

            </td>
            <td>
                <fmt:formatDate value="${jmsgSmsGatewayTmpl.createTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
            </td>

        </tr>
    </c:forEach>
    </tbody>
</table>


<div class="pagination">${page}</div>
</body>
</html>