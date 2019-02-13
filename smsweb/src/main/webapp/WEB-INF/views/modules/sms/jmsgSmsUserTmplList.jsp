<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<html>
<head>
    <title>用户模板管理</title>
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

            <%--$("#searchForm").attr("action","${ctx}/sms/jmsgSmsUserTmpl/edit?remarks="+remarks+"&weight="+weight+"&queueid="+id+"&queueName="+queueName);--%>
            <%--$("#searchForm").submit();--%>

            $.ajax({
                async: false,
                cache: false,
                traditional: true,
                url: "${ctx}/sms/jmsgSmsUserTmpl/edit",
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
                    document.location.href = "${ctx}/sms/jmsgSmsUserTmpl";
                }

            })
        }

        function sigleDelete(id)
        {
            top.$.jBox.confirm("确认要删除该通道签名吗？","系统提示",function(v,h,f){
                if(v=="ok"){
                    $("#searchForm").attr("action","${ctx}/sms/jmsgSmsUserTmpl/delete?id="+id);
                    $("#searchForm").submit();
                }
            },{buttonsFocus:1});
            top.$('.jbox-body .jbox-icon').css('top','55px');
        }

        function onDelete(){
            var ids = getCheckboxValue("id");
            if(!ids){
                alertx("请选择要删除的数据");
            }else{

                top.$.jBox.confirm("确认要删除该数据吗？","系统提示",function(v,h,f){
                    if(v=="ok"){
                        $("#searchForm").attr("action","${ctx}/sms/jmsgSmsUserTmpl/batchDelete?ids="+ids);
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
    <li class="active"><a href="${ctx}/sms/jmsgSmsUserTmpl/">信息列表</a></li>
    <shiro:hasPermission name="sms:jmsgSmsUserTmpl:edit">
        <li><a href="${ctx}/sms/jmsgSmsUserTmpl/config">信息添加</a></li>
    </shiro:hasPermission>
</ul>
<form:form id="searchForm" modelAttribute="jmsgSmsUserTmpl" action="${ctx}/sms/jmsgSmsUserTmpl/" method="post"
           class="breadcrumb form-search">
    <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
    <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
    <ul class="ul-form">

        <li><label>用户名称：</label>
            <sys:treeselect id="user" name="userId" value="${jmsgMmsTask.user.id}" labelName="user.name" labelValue="${jmsgMmsTask.user.name}"
                            title="用户" url="/sys/office/treeData?type=3" cssClass="input-small" allowClear="true" notAllowSelectParent="true"/>
        </li>

        <li><label>用户id：</label>
            <input id="userId_2" name="userId_2" class="input-medium" type="text" value="" maxlength="20">
        </li>

        <%--<li><label>通道模板：</label>--%>
            <%--<input id="templateId1" name="templateId" class="input-medium" type="text" value="" maxlength="20">--%>
        <%--</li>--%>

        <li><label>通道模板：</label>
            <form:select path="" class="input-xlarge" name="templateId">
                <form:option value="" label="全部"/>
                <form:options items="${fns:getUserTmpl()}" itemLabel="templateName" itemValue="id" htmlEscape="false"/>
            </form:select>
        </li>

        <%--<li><label>来源id：</label>--%>
            <%--<input id="templateId2" name="templateId" class="input-medium" type="text" value="" maxlength="20">--%>
        <%--</li>--%>

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
        <shiro:hasPermission name="sms:jmsgGatewaySign:edit"><th>操作　　　　　　</th></shiro:hasPermission>
        <%--<shiro:hasPermission name="sms:jmsgSmsUserTmpl:edit">--%>
            <%--<th>操作</th>--%>
        <%--</shiro:hasPermission>--%>

        <th>用户ID</th>
        <th>通道模板名称</th>
        <th>创建时间</th>

    </tr>
    </thead>
    <tbody>
    <c:forEach items="${page.list}" var="jmsgSmsUserTmpl">
        <tr onclick="selectTr(this, '${jmsgSmsUserTmpl.id}' ,'id')">
            <td>
                <input type="checkbox" name="id" id="${jmsgSmsUserTmpl.id}" value="${jmsgSmsUserTmpl.id}"
                       onclick="if(this.checked){this.checked=false;}else{this.checked=true;}">
            </td>
            <shiro:hasPermission name="sms:jmsgSmsUserTmpl:edit"><td>
                <a href="${ctx}/sms/jmsgSmsUserTmpl/form?id=${jmsgSmsUserTmpl.id}">修改</a> |
                <a href="#"
                   onclick='sigleDelete("${jmsgSmsUserTmpl.id}")'>删除</a>
            </td></shiro:hasPermission>
            <td>
                    ${jmsgSmsUserTmpl.userId}
            </td>
            <td>
                    ${jmsgSmsUserTmpl.templateName}
            </td>

            <td>
                <fmt:formatDate value="${jmsgSmsUserTmpl.createTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
            </td>

        </tr>
    </c:forEach>
    </tbody>
</table>


<div class="pagination">${page}</div>
</body>
</html>