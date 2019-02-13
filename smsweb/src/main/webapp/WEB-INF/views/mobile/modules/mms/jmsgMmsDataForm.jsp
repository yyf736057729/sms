<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<style type="text/css">
		body {font-family: Helvetica, STHeiti STXihei, Microsoft JhengHei,	Microsoft YaHei, Tohoma, Arial;margin: 0;color: #222222;
		/* 	background: none repeat scroll 0 0 #F8F7F4; */height: 100%;position: relative;}
		#wz_title {color: #000000;font-size: 20px;font-weight: bold;margin: 0;}
		#wz_date {color: #8C8C8C;font-size: 11px;}
		.wz_fengmian {margin: 10px 0;}
		.wz_content {color: #3E3E3E;line-height: 1.5;overflow: hidden;width: 100%;font-size: 15px;}
		.wz_content img,.wz_content embed{max-width: 100%;}
</style> 
<section id="mms_view_section">
    <header>
        <nav class="left">
            <a href="#" data-icon="previous" data-target="back">返回</a>
        </nav>
        <h1 class="title">${jmsgMmsData.mmsTitle }-彩信审核</h1>
    </header>
    <article class="active" data-scroll="true">
    	<div class="container" style="padding: 15px 15px 0;">
			<div class="row-fluid">
				<div class="span12">
				<span id="wz_date">${jmsgMmsData.user.name} &nbsp;<fmt:formatDate value="${jmsgMmsData.createDatetime}" pattern="yyyy-MM-dd HH:mm:ss"/></span>
				<form id="mmsReviewForm" class="input-group" action="${ctx}/mms/jmsgMmsData/checkMms" method="post">
					 <input type="hidden" value="${jmsgMmsData.id}" name="ids"/>
					 <div class="input-row">
	                    <label>审核状态</label>
	                    <div class="toggle active" data-on="通过" data-off="拒绝" name="status"/>
	                 </div>
	                <div class="input-row">
	                    <label>拒绝理由</label>
	                    <input type="text" placeholder="内容不符" name="checkContent" />
	                </div>
	               	<button id="btn" class="submit block" data-icon="key">确定</button>
           		</form>
				</div>
				<div style="height: 50px;">以下是彩信内容</div>
				<div class="span12">
					<div>
						<h4 id="wz_title">主题: ${jmsgMmsData.mmsTitle }</h4>
					</div>
					<div class="wz_content">${fns:unescapeHtml(jmsgMmsData.content)}</div>
				</div>
			</div>
		</div>
		<script type="text/javascript">
		$('body').delegate('#mms_view_section','pageinit',function(){
			
			$("#mmsReviewForm").submit(function(){
					var mmsReviewForm = $("#mmsReviewForm");
					$.post(mmsReviewForm.attr('action'), mmsReviewForm.serializeArray(), function(data){
						J.showToast('审核完成！', 'success');
						J.Router.goTo('#mms_checklist_section');
				 	});
				 return false; 
			});
		});
	</script> 
	</article>
</section>