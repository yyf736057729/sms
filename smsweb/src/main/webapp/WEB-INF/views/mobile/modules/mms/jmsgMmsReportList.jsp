<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<link rel="stylesheet" href="${ctxStatic}/jingle/css/Jingle.css">
<section id="mms_taskreport_section">
    <header>
        <nav class="left">
            <a href="#" data-icon="previous" data-target="back">返回</a>
        </nav>
        <h1 class="title">彩信任务报表</h1>
    </header>
    <article class="active" id="list_article"  data-scroll="true">
        <ul class="list">
            <c:forEach items="${page.list}" var="jmsgMmsAlldayReport">
            <li data-icon="next"><a href="#" data-target="section">
                <strong>${jmsgMmsAlldayReport.taskId}-${jmsgMmsAlldayReport.mmsTitle}(${jmsgMmsAlldayReport.mmsId})</strong>
                <p>${jmsgMmsAlldayReport.count} &nbsp; ${jmsgMmsAlldayReport.sendCount} &nbsp; ${jmsgMmsAlldayReport.downloadCount} &nbsp; <fmt:formatDate value="${jmsgMmsAlldayReport.createDatetime}" pattern="yyyy-MM-dd HH:mm:ss"/></p></a>
            </li>
            </c:forEach>
         </ul>
    </article>
</section>