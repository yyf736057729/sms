<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>加载黑白名单</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			
		});
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").submit();
        	return false;
        }
		
		function loadPhone(type){
			$.ajax({
				url : "${ctx}/sms/jmsgPhonelist/loadPhone?type="+type,
				dataType: "text",
				success : function(data) {
					if(data == null || data == ""){
						alertx("操作失败");
					}else{
						alertx(data);
					}
				},
				error : function(){
					alertx("操作失败");
				}
			});
		}
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a>加载名单</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="jmsgPhonelist" action="${ctx}/sms/jmsgPhonelist/loadPhone/init" method="post" class="breadcrumb form-search">
	<div class="form-actions">
		<ul class="ul-form">
			<li class="btns"><input id="btnSubmit1" class="btn btn-primary" type="button" value="加载黑名单" onclick="javascript:loadPhone(1)"/></li>
			<li class="btns"><input id="btnSubmit2" class="btn btn-primary" type="button" value="加载白名单" onclick="javascript:loadPhone(2)"/></li>
			<li class="clearfix"></li>
		</ul>
	</div>	
	</form:form>
</body>
</html>