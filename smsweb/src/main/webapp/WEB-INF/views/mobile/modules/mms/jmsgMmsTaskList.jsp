<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<link rel="stylesheet" href="${ctxStatic}/jingle/css/Jingle.css">
<section id="mms_task_section">
    <header>
        <nav class="left">
            <a href="#" data-icon="previous" data-target="back">返回</a>
        </nav>
        <h1 class="title">彩信任务报表</h1>
    </header>
    <article class="active" id="list_article"  data-scroll="true">
        <ul class="list">
		<c:forEach items="${page.list}" var="jmsgMmsTask">
		<li data-icon="next"><a href="#" data-target="section">
            <strong>${jmsgMmsTask.id}-${jmsgMmsTask.mmsTitle}(${jmsgMmsTask.mmsId})</strong>
            <p>${jmsgMmsTask.sendCount} &nbsp; ${fns:getDictLabel(jmsgMmsTask.status,'task_send_status',jmsgMmsTask.status)} &nbsp; <fmt:formatDate value="${jmsgMmsTask.sendDatetime}" pattern="MM-dd HH:mm"/></p></a>
        </li>
        </c:forEach>
       </ul>
    </article>
</section>
