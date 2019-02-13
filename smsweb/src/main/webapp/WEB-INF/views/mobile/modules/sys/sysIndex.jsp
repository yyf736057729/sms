<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<section id="index_section">
	<header>
        <h1 class="title">${fns:getConfig('productName')}</h1>
        <nav class="right">
            <a data-icon="arrow-down-left-2" href="#" id="btnLogout">退出</a>
        </nav>
    </header>
    <article class="active" data-scroll="true">
        <div style="padding: 10px 0 20px;">
        <ul class="list inset demo-list">
        	<shiro:hasPermission name="mms:jmsgMmsData:check">
            <li data-icon="next" data-selected="selected">
                <a href="#mms_checklist_section" data-target="section">
                    <strong>彩信审核</strong>
                </a>
            </li>
            </shiro:hasPermission>
            
            <shiro:hasPermission name="sms:jmsgSmsWarn:view">
            <li data-icon="next" data-selected="selected">
                <a href="#sms_warnlist_section" data-target="section">
                    <strong>短信监控</strong>
                </a>
            </li>
            </shiro:hasPermission>
            
            <%-- <shiro:hasPermission name="mms:jmsgMmsTask:view">
            <li data-icon="next" data-selected="selected">
                <a href="#mms_task_section" data-target="section">
                    <strong>彩信任务列表</strong>
                </a>
            </li>
            </shiro:hasPermission>
            
            <shiro:hasPermission name="mms:jmsgMmsAlldayReport:view">
            <li data-icon="next" data-selected="selected">
                <a href="#mms_taskreport_section" data-target="section">
                    <strong>彩信任务报表</strong>
                </a>
            </li>
            </shiro:hasPermission> --%>
        </ul>
        </div>
    </article>
    <script type="text/javascript">
   		$('#btnLogout').tap(function(){
   			J.confirm('确认提示','确认要退出吗？',function(){
   				$.get("${ctx}/logout", function(){
   					sessionid = '';
   					J.showToast('退出成功！', 'success');
   					J.Router.goTo('#login_section');
   				});
   			});
   		});
    </script>
</section>