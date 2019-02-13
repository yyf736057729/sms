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


        function addRow(list, idx, tpl, row){
            $(list).append(Mustache.render(tpl, {
                idx: idx, delBtn: true, row: row
            }));
            $(list+idx).find("select").each(function(){
                $(this).val($(this).attr("data-value"));
                $(this).select2();
            });
            $(list+idx).find("input[type='checkbox'], input[type='radio']").each(function(){
                var ss = $(this).attr("data-value").split(',');
                for (var i=0; i<ss.length; i++){
                    if($(this).val() == ss[i]){
                        $(this).attr("checked","checked");
                    }
                }
            });

            refreshSelect();
        }

        function delRow(obj, prefix){
            var id = $(prefix+"_id");
            var delFlag = $(prefix+"_delFlag");
            if (id.val() == ""){
                $(obj).parent().parent().remove();
            }else if(delFlag.val() == "0"){
                delFlag.val("1");
                $(obj).html("撤销删除").attr("title", "撤销删除");
                $(obj).parent().parent().addClass("error");
            }else if(delFlag.val() == "1"){
                delFlag.val("0");
                $(obj).html("删除").attr("title", "删除");
                $(obj).parent().parent().removeClass("error");
            }
        }

        function refreshSelect()
        {
            $("[id$='gatewayId']").change(function()
            {
                var curTr = $(this).parent().parent();
                $.ajax({
                    url : "${ctx}/sms/jmsgGatewayInfo/getGatewayInfo?gatewayId="+$(this).children('option:selected').val(),
                    dataType : "json",
                    success : function(data) {
                        curTr.find("input[id*='spNumber']").val(data.spNumber);
                    }
                });
            })

            $("[id='type']").change(function()
            {
                var html = '';
                var curTr = $(this).parent().parent();
                var gatewaySelect = curTr.find("select[id$='gatewayId']");
                $.ajax({
                    url : "${ctx}/sms/jmsgGatewayInfo/getGatewayInfoList?type="+$(this).children('option:selected').val(),
                    dataType : "json",
                    success : function(data) {
                        gatewaySelect.empty();
                        html += '<option value="">请选择</option>';
                        $.each(data,function(i,data){
                            html += '<option value="'+data.id+'">'+data.gatewayName+'</option>';
                        });
                        gatewaySelect.append(html);
                        gatewaySelect.select2();
                    }
                });
            })
        }
    </script>
</head>
<body>
<ul class="nav nav-tabs">
    <li><a href="${ctx}/sms/jmsgSmsUserTmpl/">信息列表</a></li>
    <li class="active"><a href="${ctx}/sms/jmsgSmsUserTmpl/form?id=${jmsgSmsUserTmpl.id}">信息<shiro:hasPermission name="sms:jmsgSmsUserTmpl:edit">${not empty jmsgSmsUserTmpl.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="sms:jmsgSmsUserTmpl:edit">查看</shiro:lacksPermission></a></li>
</ul><br/>

<form:form id="inputForm" modelAttribute="jmsgSmsUserTmpl" action="${ctx}/sms/jmsgSmsUserTmpl/batchSave" method="post" class="form-horizontal">
    <form:hidden path="id"/>
    <sys:message content="${message}"/>
    <div class="control-group">
        <label class="control-label">用户：</label>
        <div class="controls">
            <sys:treeselect id="user" name="userId" value="${jmsgSmsUserTmpl.userId}" labelName="userName" labelValue="${jmsgSmsUserTmpl.userName}"
                            title="用户" url="/sys/office/treeData?type=3" cssClass="required" allowClear="true" notAllowSelectParent="true" disabled="${not empty jmsgSmsUserTmpl.id?'disabled':''}"/>
            <span class="help-inline"><font color="red">*</font> </span>
        </div>
    </div>

    <table id="contentTable" class="table table-striped table-bordered table-condensed">
        <thead>
        <tr>
            <th>通道模板</th>
            <th>接入号</th>
            <th>备注</th>
            <shiro:hasPermission name="sms:jmsgSmsUserTmpl:edit"><th>操作</th></shiro:hasPermission>
        </tr>
        </thead>
        <tbody id="jmsgSmsUserTmpls">
        <tr>
            <input id="jmsgSmsUserTmplList0_id" name="jmsgSmsUserTmplList[0].id" type="hidden" value=""/>
            <input id="jmsgSmsUserTmplList0_delFlag" name="jmsgSmsUserTmplList[0].delFlag" type="hidden" value="0"/>
                <%--<select id="jmsgSmsUserTmplList0_gatewayId" name="jmsgSmsUserTmplList[0].gatewayId" class="input-xlarge ">--%>
                <%--<option value="">请选择</option>--%>
                <%--</select>--%>
            <td>
                <form:select path="" class="input-xlarge" name="jmsgSmsUserTmplList[0].id" id ="jmsgSmsUserTmplList0_templateName">
                    <form:option value="" label="请选择"/>
                    <form:options items="${fns:getUserTmpl()}" itemLabel="templateName" itemValue="id" htmlEscape="false"/>
                </form:select>
            </td>

            <td>
                <input id="jmsgSmsUserTmplList0_joinNumber" name="jmsgSmsUserTmplList[0].joinNumber" htmlEscape="false" maxlength="20" class="input-small "/>
            </td>
            <td>
                <input id="jmsgSmsUserTmplList0_remarks" name="jmsgSmsUserTmplList[0].remarks"  htmlEscape="false" maxlength="100" class="input-small "/>
            </td>

            <shiro:hasPermission name="sms:jmsgSmsUserTmpl:edit"><td>
                <a href="#" onclick="delRow(this, '#jmsgSmsUserTmplList0')">删除</a>
            </td></shiro:hasPermission>
        </tr>
        <tr>
            <input id="jmsgSmsUserTmplList1_id" name="jmsgSmsUserTmplList[1].id" type="hidden" value=""/>
            <input id="jmsgSmsUserTmplList1_delFlag" name="jmsgSmsUserTmplList[1].delFlag" type="hidden" value="0"/>

            <td>
                <form:select path="" class="input-xlarge" name="jmsgSmsUserTmplList[1].id" id ="jmsgSmsUserTmplList1_templateName">
                    <form:option value="" label="请选择"/>
                    <form:options items="${fns:getUserTmpl()}" itemLabel="templateName" itemValue="id" htmlEscape="false"/>
                </form:select>
            </td>
            <td>
                <input id="jmsgSmsUserTmplList1_joinNumber" name="jmsgSmsUserTmplList[1].joinNumber" htmlEscape="false" maxlength="20" class="input-small "/>
            </td>
            <td>
                <input id="jmsgSmsUserTmplList1_remarks" name="jmsgSmsUserTmplList[1].remarks"  htmlEscape="false" maxlength="100" class="input-small"/>
            </td>
            <shiro:hasPermission name="sms:jmsgSmsUserTmpl:edit"><td>
                <a href="#" onclick="delRow(this, '#jmsgSmsUserTmplList1')">删除</a>
            </td></shiro:hasPermission>
        </tr>
        <tr>
            <input id="jmsgSmsUserTmplList2_id" name="jmsgSmsUserTmplList[2].id" type="hidden" value=""/>
            <input id="jmsgSmsUserTmplList2_delFlag" name="jmsgSmsUserTmplList[2].delFlag" type="hidden" value="0"/>

            <td>
                <form:select path="" class="input-xlarge" name="jmsgSmsUserTmplList[2].id" id ="jmsgSmsUserTmplList2_templateName">
                    <form:option value="" label="请选择"/>
                    <form:options items="${fns:getUserTmpl()}" itemLabel="templateName" itemValue="id" htmlEscape="false"/>
                </form:select>
            </td>
            <td>
                <input id="jmsgSmsUserTmplList2_joinNumber" name="jmsgSmsUserTmplList[2].joinNumber" htmlEscape="false" maxlength="20" class="input-small "/>
            </td>
            <td>
                <input id="jmsgSmsUserTmplList2_remarks" name="jmsgSmsUserTmplList[2].remarks"  htmlEscape="false" maxlength="100" class="input-small "/>
            </td>
            <shiro:hasPermission name="sms:jmsgSmsUserTmpl:edit"><td>
                <a href="#" onclick="delRow(this, '#jmsgSmsUserTmplList2')">删除</a>
            </td></shiro:hasPermission>
        </tr>
        <tr>
            <input id="jmsgSmsUserTmplList3_id" name="jmsgSmsUserTmplList[3].id" type="hidden" value=""/>
            <input id="jmsgSmsUserTmplList3_delFlag" name="jmsgSmsUserTmplList[3].delFlag" type="hidden" value="0"/>

            <td>
                <form:select path="" class="input-xlarge" name="jmsgSmsUserTmplList[3].id" id ="jmsgSmsUserTmplList3_templateName">
                    <form:option value="" label="请选择"/>
                    <form:options items="${fns:getUserTmpl()}" itemLabel="templateName" itemValue="id" htmlEscape="false"/>
                </form:select>
            </td>
            <td>
                <input id="jmsgSmsUserTmplList3_joinNumber" name="jmsgSmsUserTmplList[3].joinNumber" htmlEscape="false" maxlength="20" class="input-small "/>
            </td>
            <td>
                <input id="jmsgSmsUserTmplList3_remarks" name="jmsgSmsUserTmplList[3].remarks"  htmlEscape="false" maxlength="100" class="input-small "/>
            </td>
            <shiro:hasPermission name="sms:jmsgSmsUserTmpl:edit"><td>
                <a href="#" onclick="delRow(this, '#jmsgSmsUserTmplList3')">删除</a>
            </td></shiro:hasPermission>
        </tr>
        <tr>
            <input id="jmsgSmsUserTmplList4_id" name="jmsgSmsUserTmplList[4].id" type="hidden" value=""/>
            <input id="jmsgSmsUserTmplList4_delFlag" name="jmsgSmsUserTmplList[4].delFlag" type="hidden" value="0"/>

            <td>
                <form:select path="" class="input-xlarge" name="jmsgSmsUserTmplList[4].id" id ="jmsgSmsUserTmplList4_templateName">
                    <form:option value="" label="请选择"/>
                    <form:options items="${fns:getUserTmpl()}" itemLabel="templateName" itemValue="id" htmlEscape="false"/>
                </form:select>
            </td>
            <td>
                <input id="jmsgSmsUserTmplList4_joinNumber" name="jmsgSmsUserTmplList[4].joinNumber" htmlEscape="false" maxlength="20" class="input-small "/>
            </td>
            <td>
                <input id="jmsgSmsUserTmplList4_remarks" name="jmsgSmsUserTmplList[4].remarks"  htmlEscape="false" maxlength="100" class="input-small "/>
            </td>
            <shiro:hasPermission name="sms:jmsgSmsUserTmpl:edit"><td>
                <a href="#" onclick="delRow(this, '#jmsgSmsUserTmplList4')">删除</a>
            </td></shiro:hasPermission>
        </tr>
        <tr>
            <input id="jmsgSmsUserTmplList5_id" name="jmsgSmsUserTmplList[5].id" type="hidden" value=""/>
            <input id="jmsgSmsUserTmplList5_delFlag" name="jmsgSmsUserTmplList[5].delFlag" type="hidden" value="0"/>

            <td>
                <form:select path="" class="input-xlarge" name="jmsgSmsUserTmplList[5].id" id ="jmsgSmsUserTmplList5_templateName">
                    <form:option value="" label="请选择"/>
                    <form:options items="${fns:getUserTmpl()}" itemLabel="templateName" itemValue="id" htmlEscape="false"/>
                </form:select>
            </td>
            <td>
                <input id="jmsgSmsUserTmplList5_joinNumber" name="jmsgSmsUserTmplList[5].joinNumber" htmlEscape="false" maxlength="20" class="input-small "/>
            </td>
            <td>
                <input id="jmsgSmsUserTmplList5_remarks" name="jmsgSmsUserTmplList[5].remarks"  htmlEscape="false" maxlength="100" class="input-small "/>
            </td>
            <shiro:hasPermission name="sms:jmsgSmsUserTmpl:edit"><td>
                <a href="#" onclick="delRow(this, '#jmsgSmsUserTmplList5')">删除</a>
            </td></shiro:hasPermission>
        </tr>
        <tr>
            <input id="jmsgSmsUserTmplList6_id" name="jmsgSmsUserTmplList[6].id" type="hidden" value=""/>
            <input id="jmsgSmsUserTmplList6_delFlag" name="jmsgSmsUserTmplList[6].delFlag" type="hidden" value="0"/>

            <td>
                <form:select path="" class="input-xlarge" name="jmsgSmsUserTmplList[6].id" id ="jmsgSmsUserTmplList6_templateName">
                    <form:option value="" label="请选择"/>
                    <form:options items="${fns:getUserTmpl()}" itemLabel="templateName" itemValue="id" htmlEscape="false"/>
                </form:select>
            </td>
            <td>
                <input id="jmsgSmsUserTmplList6_joinNumber" name="jmsgSmsUserTmplList[6].joinNumber" htmlEscape="false" maxlength="20" class="input-small "/>
            </td>
            <td>
                <input id="jmsgSmsUserTmplList6_remarks" name="jmsgSmsUserTmplList[6].remarks"  htmlEscape="false" maxlength="100" class="input-small "/>
            </td>
            <shiro:hasPermission name="sms:jmsgSmsUserTmpl:edit"><td>
                <a href="#" onclick="delRow(this, '#jmsgSmsUserTmplList6')">删除</a>
            </td></shiro:hasPermission>
        </tr>
        <tr>
            <input id="jmsgSmsUserTmplList7_id" name="jmsgSmsUserTmplList[7].id" type="hidden" value=""/>
            <input id="jmsgSmsUserTmplList7_delFlag" name="jmsgSmsUserTmplList[7].delFlag" type="hidden" value="0"/>

            <td>
                <form:select path="" class="input-xlarge" name="jmsgSmsUserTmplList[7].id" id ="jmsgSmsUserTmplList7_templateName">
                    <form:option value="" label="请选择"/>
                    <form:options items="${fns:getUserTmpl()}" itemLabel="templateName" itemValue="id" htmlEscape="false"/>
                </form:select>
            </td>
            <td>
                <input id="jmsgSmsUserTmplList7_joinNumber" name="jmsgSmsUserTmplList[7].joinNumber" htmlEscape="false" maxlength="20" class="input-small "/>
            </td>
            <td>
                <input id="jmsgSmsUserTmplList7_remarks" name="jmsgSmsUserTmplList[7].remarks"  htmlEscape="false" maxlength="100" class="input-small "/>
            </td>
            <shiro:hasPermission name="sms:jmsgSmsUserTmpl:edit"><td>
                <a href="#" onclick="delRow(this, '#jmsgSmsUserTmplList7')">删除</a>
            </td></shiro:hasPermission>
        </tr>
        <tr>
            <input id="jmsgSmsUserTmplList8_id" name="jmsgSmsUserTmplList[8].id" type="hidden" value=""/>
            <input id="jmsgSmsUserTmplList8_delFlag" name="jmsgSmsUserTmplList[8].delFlag" type="hidden" value="0"/>

            <td>
                <form:select path="" class="input-xlarge" name="jmsgSmsUserTmplList[8].id" id ="jmsgSmsUserTmplList8_templateName">
                    <form:option value="" label="请选择"/>
                    <form:options items="${fns:getUserTmpl()}" itemLabel="templateName" itemValue="id" htmlEscape="false"/>
                </form:select>
            </td>
            <td>
                <input id="jmsgSmsUserTmplList8_joinNumber" name="jmsgSmsUserTmplList[8].joinNumber" htmlEscape="false" maxlength="20" class="input-small "/>
            </td>
            <td>
                <input id="jmsgSmsUserTmplList8_remarks" name="jmsgSmsUserTmplList[8].remarks"  htmlEscape="false" maxlength="100" class="input-small "/>
            </td>
            <shiro:hasPermission name="sms:jmsgSmsUserTmpl:edit"><td>
                <a href="#" onclick="delRow(this, '#jmsgSmsUserTmplList8')">删除</a>
            </td></shiro:hasPermission>
        </tr>
        <tr>
            <input id="jmsgSmsUserTmplList9_id" name="jmsgSmsUserTmplList[9].id" type="hidden" value=""/>
            <input id="jmsgSmsUserTmplList9_delFlag" name="jmsgSmsUserTmplList[9].delFlag" type="hidden" value="0"/>

            <td>
                <form:select path="" class="input-xlarge" name="jmsgSmsUserTmplList[9].id" id ="jmsgSmsUserTmplList9_templateName">
                    <form:option value="" label="请选择"/>
                    <form:options items="${fns:getUserTmpl()}" itemLabel="templateName" itemValue="id" htmlEscape="false"/>
                </form:select>
            </td>
            <td>
                <input id="jmsgSmsUserTmplList9_joinNumber" name="jmsgSmsUserTmplList[9].joinNumber" htmlEscape="false" maxlength="20" class="input-small "/>
            </td>
            <td>
                <input id="jmsgSmsUserTmplList9_remarks" name="jmsgSmsUserTmplList[9].remarks"  htmlEscape="false" maxlength="100" class="input-small "/>
            </td>
            <shiro:hasPermission name="sms:jmsgSmsUserTmpl:edit"><td>
                <a href="#" onclick="delRow(this, '#jmsgSmsUserTmplList9')">删除</a>
            </td></shiro:hasPermission>
        </tr>
        </tbody>
        <tfoot>
        <tr><td colspan="7">
            <a href="javascript:" onclick="addRow('#jmsgSmsUserTmpls', rowIdx, jmsgSmsUserTmplsTpl);rowIdx = rowIdx + 1;" class="btn">新增</a>
        </td></tr>
        </tfoot>
    </table>

    <script type="text/template" id="jmsgSmsUserTmplsTpl">
        <tr id="jmsgSmsUserTmpls{{idx}}">
            <input id="jmsgSmsUserTmplList{{idx}}_id" name="jmsgSmsUserTmplList[{{idx}}].id" type="hidden" value=""/>
            <input id="jmsgSmsUserTmplList{{idx}}_delFlag" name="jmsgSmsUserTmplList[{{idx}}].delFlag" type="hidden" value="0"/>

            <td>
                <form:select path="" class="input-xlarge" name="jmsgSmsUserTmplList[{{idx}}].id" id ="jmsgSmsUserTmplList[{{idx}}]_templateName">
                    <form:option value="" label="请选择"/>
                    <form:options items="${fns:getUserTmpl()}" itemLabel="templateName" itemValue="id" htmlEscape="false"/>
                </form:select>
            </td>
            <td>
                <input id="jmsgSmsUserTmplList{{idx}}_joinNumber" name="jmsgSmsUserTmplList[{{idx}}].joinNumber" htmlEscape="false" maxlength="20" class="input-small "/>
            </td>
            <td>
                <input id="jmsgSmsUserTmplList{{idx}}_remarks" name="jmsgSmsUserTmplList[{{idx}}].remarks"  htmlEscape="false" maxlength="100" class="input-small "/>
            </td>
            <shiro:hasPermission name="sms:jmsgSmsUserTmpl:edit"><td>
                <a href="#" onclick="delRow(this, '#jmsgSmsUserTmplList{{idx}}')">删除</a>
            </td></shiro:hasPermission>
        </tr>
    </script>

    <script type="text/javascript">
        var rowIdx = 10, jmsgSmsUserTmplsTpl = $("#jmsgSmsUserTmplsTpl").html().replace(/(\/\/\<!\-\-)|(\/\/\-\->)/g,"");
    </script>

    <div class="form-actions">
        <shiro:hasPermission name="sms:jmsgSmsUserTmpl:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
        <input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
    </div>
</form:form>
</body>
</html>