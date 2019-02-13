<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<link rel="stylesheet" href="${ctxStatic}/jingle/css/Jingle.css">
<section id="sms_warnlist_section">
    <header>
        <nav class="left">
            <a href="#" data-icon="previous" data-target="back">返回</a>
        </nav>
        <h1 class="title">短信监控</h1>
    </header>
    <article class="active" id="list_article"  data-scroll="true">
    	<div style="line-height:50px;padding:10px;">
    		<c:forEach items="${list}" var="jmsgSmsWarn">
					<p>${jmsgSmsWarn.warnContent}【<fmt:formatDate value="${jmsgSmsWarn.createTime}" pattern="yyyy-MM-dd HH:mm:ss"/>】</p>
			</c:forEach>
		
    	</div>
        <ul class="list">
            <li class="divider">监控日志列表</li>
            <c:forEach items="${page.list}" var="jmsgGatewayMonitor">
            <li data-icon="next">
                <strong>${jmsgGatewayMonitor.gatewayId}=>[${jmsgGatewayMonitor.timeFailCount}][${jmsgGatewayMonitor.continuousFailCount}]</strong>
                <p><fmt:formatDate value="${jmsgGatewayMonitor.createTime}" pattern="yyyy-MM-dd HH:mm:ss"/></p>
            </li>
            </c:forEach>
         </ul>
    </article>
</section>
