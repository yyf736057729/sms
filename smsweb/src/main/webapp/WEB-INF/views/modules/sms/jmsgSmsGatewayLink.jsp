<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>详情</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
        //队列详情
        function refurbish(){
            var url ="/admin/sms/jmsgGatewayInfo/link_post?id="+$("#gatewayid").val();
            $.ajax({
                type:'POST',
                url:url,
                success:function(data){
                    var htmlContent = "";
                    var conectionInfo = "";
                    if(data != null && data !=''){
                        var e = eval("("+data+")");
                        conectionInfo = e.connect_record;
                        if(e.status == 1) {
                            $("#connectStatus").attr("color","#73FA17");
                        } else {
                            $("#connectStatus").attr("color","red");
                        }
                        $.each(e.spend_list_record,function(index,item){
                            htmlContent+="<tr><td>"+item+"</td></tr>";
                        });

                    }else{
                        htmlContent = "<tr><td>暂无数据</td></tr>";
                    }

                    $("#").html(conectionInfo);
                    $("#tbody").html(htmlContent);
                },
                error:function(){
                    alertx("系统错误");
                }
            });
        }
	</script>
</head>

<body>
<div class="breadcrumb form-search">
	<<input style="display: none" id = "gatewayid"  value=${id}>
	<ul class="ul-form">
		<%--<li class="btns"><input class="btn btn-primary" type="button" onclick="gwControl(1)" value="重启链路"/></li>--%>
		<%--<li class="btns"><input class="btn btn-primary" type="button" onclick="gwControl(0)" value="关闭链路"/></li>--%>
		<li class="btns"><input class="btn btn-primary" type="button" onclick="refurbish()" value="刷新状态"/></li>
	</ul>
</div>
<table id="connection_info" class="table table-striped table-bordered table-condensed">
	<thead>
	<tr>
		<c:if test="${status eq 1 }">
			<th>连接情况<font size="6" id="connectStatus" style="color:#73FA17">●</font></th>
		</c:if>
		<c:if test="${status eq -1 }">
			<th>连接情况<font size="6" id="connectStatus" style="color: red">●</font></th>
		</c:if>

	</tr>
	</thead>
	<tbody>
	<tr>

		<td>
			<%--<pre id="conectionInfo">暂无数据</pre>--%>
				<pre id="conectionInfo" value="${connect_record}">${connect_record}</pre>
		</td>
	</tr>
	</tbody>
</table>
<table id="run_info" class="table table-striped table-bordered table-condensed">
	<thead>
	<tr>
		<th>运行日志</th>
	</tr>
	</thead>
	<tbody id="tbody">
	<tr>
		<%--<td>暂无数据</td>--%>
		<c:forEach items="${spend_list_record}" var="spendlist">
			<tr><td>${spendlist}</td></tr>
		</c:forEach>
	</tr>
	</tbody>
</table>
</body>

</html>