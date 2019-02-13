<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<link rel="stylesheet" href="${ctxStatic}/jingle/css/Jingle.css">
<section id="mms_checklist_section">
    <header>
        <nav class="left">
            <a href="#" data-icon="previous" data-target="back">返回</a>
        </nav>
        <h1 class="title">彩信审核</h1>
    </header>
    <article class="active" id="list_article"  data-scroll="true">
    	<!-- <div style="line-height:50px;padding:10px;">
    		待审核功能，点击下面列表，进行单条审核<br/>
    		审核注意事项:
    	</div> -->
        <ul class="list">
            <li class="divider">待审核列表</li>
            <c:forEach items="${page.list}" var="jmsgMmsData">
            <li data-icon="next"><a href="#mms_view_section?id=${jmsgMmsData.id}" data-target="section">
                <strong>${jmsgMmsData.mmsTitle}</strong>
                <p>${jmsgMmsData.user.name} &nbsp; <fmt:formatDate value="${jmsgMmsData.createDatetime}" pattern="yyyy-MM-dd HH:mm:ss"/></p></a>
            </li>
            </c:forEach>
         </ul>
    </article>
</section>