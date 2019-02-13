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

        function onUpdate(){
            var id = $("#queueId").val();
            var queueName = $("#queueName").val();
            if(queueName == null || queueName ==''){
                alertx('请输入队列名');
                return;
            }
            var weight = $("#weight").val();
            if(weight == null || weight ==''){
                alertx('请输入权重值');
                return;
            }
            var remarks = $("#remarks").val();

            <%--$("#searchForm").attr("action","${ctx}/sms/jmsgGatewayQueue/edit?remarks="+remarks+"&weight="+weight+"&queueid="+id+"&queueName="+queueName);--%>
            <%--$("#searchForm").submit();--%>

            $.ajax({
                async: false,
                cache: false,
                traditional: true,
                url: "${ctx}/sms/jmsgGatewayQueue/edit",
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
                    document.location.href = "${ctx}/sms/jmsgGatewayQueue";
                }

            })
        }

        function initUpdate(queueId,queueName,weight,remarks){
            $("#queueId").val(queueId);
            $("#queueName").val(queueName);
            $("#weight").val(weight);
            $("#remarks").val(remarks);
            $('#updateModal').modal('show');
        }

    </script>
</head>
<body>
<ul class="nav nav-tabs">
    <li class="active"><a href="${ctx}/sms/jmsgGatewayQueue/">信息列表</a></li>
    <shiro:hasPermission name="sms:jmsgGatewayQueue:edit">
        <li><a href="${ctx}/sms/jmsgGatewayQueue/form">信息添加</a></li>
    </shiro:hasPermission>
</ul>
<form:form id="searchForm" modelAttribute="jmsgGatewayQueue" action="${ctx}/sms/jmsgGatewayQueue/" method="post"
           class="breadcrumb form-search">
    <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
    <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
    <ul class="ul-form">

        <li><label>通道名称：</label>
            <form:select path="" class="input-xlarge" name="gatewayName">
                <form:option value="" label="全部"/>
                <form:options items="${fns:getGatewayList()}"  itemLabel="label" itemValue="label" htmlEscape="false" />
            </form:select>
        </li>

        <li><label>通道编号：</label>
            <input id="gatewayId" name="gatewayId" class="input-medium" type="text" value="" maxlength="20" >
        </li>

        <li class="btns"><input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/></li>
        <li class="clearfix"></li>
    </ul>
</form:form>
<sys:message content="${message}"/>
<table id="contentTable" class="table table-striped table-bordered table-condensed">
    <thead>
    <tr>
        <shiro:hasPermission name="sms:jmsgGatewayQueue:edit">
            <th>操作</th>
        </shiro:hasPermission>

        <th>通道编号</th>
        <th>通道名称</th>
        <th>业务类型</th>
        <th>队列名称</th>
        <th>队列状态</th>
        <th>权重</th>
        <th>创建时间</th>

    </tr>
    </thead>
    <tbody>
    <c:forEach items="${page.list}" var="jmsgGatewayQueue">


        <tr>

            <shiro:hasPermission name="sms:jmsgGatewayQueue:edit">

                <td>
                    <c:if test="${jmsgGatewayQueue.status eq 0}">
                        <a href="${ctx}/sms/jmsgGatewayQueue/updateStatus?id=${jmsgGatewayQueue.id}&status=1"
                           onclick="return confirmx('确认要修改该通道队列状态吗？', this.href)">
                            停用
                        </a>
                    </c:if>
                    <c:if test="${jmsgGatewayQueue.status eq 1}">
                    <a href="${ctx}/sms/jmsgGatewayQueue/updateStatus?id=${jmsgGatewayQueue.id}&status=0"
                       onclick="return confirmx('确认要修改该通道队列状态吗？', this.href)">
                        启用
                    </a>
                    </c:if>
                    <%--<a href="javascript:showModal('288','单发-移动行业报备（备用）','1','')" class="but but_xs but_success">修改</a>--%>

                    <a href="javascript:initUpdate('${jmsgGatewayQueue.id}','${jmsgGatewayQueue.queueName}','${jmsgGatewayQueue.weight}','${jmsgGatewayQueue.remarks}');">修改</a>

                    <a href="/admin/sms/jmsgGatewayQueue/delete?id=${jmsgGatewayQueue.id}"
                       onclick="return confirmx('确认要删除该通道队列吗？', this.href)">删除</a>

                </td>

            </shiro:hasPermission>

            <td>
                    ${jmsgGatewayQueue.gatewayId}
            </td>
            <td>
                    ${jmsgGatewayQueue.gatewayName}
            </td>
            <td>
                <c:if test="${jmsgGatewayQueue.businessType eq 1}">
                    验证码
                </c:if>
                <c:if test="${jmsgGatewayQueue.businessType eq 2}">
                    单发
                </c:if>
                <c:if test="${jmsgGatewayQueue.businessType eq 3}">
                    群发
                </c:if>
            </td>
            <td>
                    ${jmsgGatewayQueue.queueName}
            </td>
            <td>
                    ${jmsgGatewayQueue.status eq 0 ?'<font size="6" id="connectStatus" style="color:#73FA17"启用>●</font>':
                            '<font size="6" id="connectStatus" style="color: red">●</font>未启用'}

            </td>
            <td>
                    ${jmsgGatewayQueue.weight}
            </td>
                <%--<td>--%>
                <%--${jmsgGatewayQueue.createTime}--%>
                <%--</td>--%>
            <td>
                <fmt:formatDate value="${jmsgGatewayQueue.createTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
            </td>

        </tr>
    </c:forEach>
    </tbody>
</table>

<div class="modal fade" style="display:none;" id="updateModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel1" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close"
                        data-dismiss="modal" aria-hidden="true">
                    &times;
                </button>
                <h4 class="modal-title" id="myModalLabel">
                    配置队列数
                </h4>
            </div>
            <div class="modal-body">
                <form id="gateWaySendForm" class="form-horizontal">
                    <input type="hidden" id="queueId" value="">

                    <div class="control-group">
                        <div class="controls1">
                            队列名称：<input type="text" id="queueName" maxlength="40" class="input-xlarge"><span><font color="red">*</font></span>
                        </div>
                    </div>

                    <div class="control-group">
                        <div class="controls1">
                            　　权重：<input type="text" id="weight" maxlength="2" class="input-xlarge"><span><font color="red">*</font></span>
                        </div>
                    </div>

                    <div class="control-group">
                        <div class="controls1">
                            　　备注：<textarea id="remarks" rows="5" cols="3" class="input-xlarge" maxlength="200"></textarea>
                        </div>
                    </div>
                </form>
            </div>
        </div>
        <div class="modal-footer">
            <input id="btnSubmit1" class="btn btn-primary" type="button" value="确 定" class="btn btn-primary" onclick="javascript:onUpdate();"/>
            <input id="btnSubmit2" class="btn btn-primary" type="button" value="关 闭" data-dismiss="modal" aria-hidden="true"/>
        </div>
    </div>
</div>

<div class="pagination">${page}</div>
</body>
</html>