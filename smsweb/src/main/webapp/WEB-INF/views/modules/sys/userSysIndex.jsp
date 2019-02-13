<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<!DOCTYPE html>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>个人信息</title>
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<link rel="shortcut icon" href="${ctxStatic}/style/sms/konsone.ico" />
    <link href="${ctxStatic}/style/sms/css/bootstrap.min.css" rel="stylesheet">
    <link href="${ctxStatic}/style/sms/font-awesome/css/font-awesome.css" rel="stylesheet">
    <link href="${ctxStatic}/style/sms/css/animate.css" rel="stylesheet">
    <link href="${ctxStatic}/style/sms/css/style.css" rel="stylesheet">
    <script src="${ctxStatic}/echart/echarts.min.js"></script>
    <style type="text/css">
        .modal.fade.in {top:0;}
        .modal {background-color: transparent;border:none;box-shadow:none;-webkit-box-shadow:none;}
        .modal-header {background-color: #2fa4e7;border-top-left-radius: 6px;border-top-right-radius: 6px;}
        .modal-body {height: 350px;} 
        .modal-title,.modal-header .close {color: #fff;}
        .modal-title {line-height: auto;font-size: 16px;}
    </style>
</head>
<body>
<div class="wrapper-content">
    <div class="row">
        <div class="col-sm-6">
            <div class="ibox float-e-margins">
                <div class="ibox-title">
                    <h5>基本信息</h5>
                    <div class="ibox-tools" style="display: block;">
                    </div>
                </div>
                <div class="ibox-content">
                    <div class="row user_info">
                        <div class="col-xs-3">
                            <img src="${ctxStatic}/style/sms/images/userpic.png" />
                            <h6>${user.loginName}</h6>
                        </div>
                        <div class="col-xs-9">
                            <div class="row form-inline">
                                <blockquote>${user.companyName}　 
                                <c:if test="${user.delFlag eq '0'}">
                                	<p class="badge badge-info">正常</p>
                                </c:if>
                                <c:if test="${user.delFlag eq '1'}">
                                	<p class="badge">停用</p>
                                </c:if>
                                </blockquote>
                                <div class="row">
                                    <div class="col-xs-6">
                                        <div class="form-group"><label>付款方式：</label><p>${user.payType == 0 ? '预付费' : '后付费'}</p></div>
                                        </div>
                                    <div class="col-xs-6">
                                        <div class="form-group"><label>短信价格：</label>
                                        <c:if test="${user.price eq '' || user.price == null}">
                                        <p>-- 元/条</p>
                                        </c:if>
                                        <c:if test="${user.price ne '' && user.price != null}">
                                        <p>${user.price} 元/条</p>
                                        </c:if>
                                        </div>
                                    </div>
                                    <div class="col-xs-12">
                                        <div class="form-group"><label>开通时间：</label><p><fmt:formatDate value="${user.createDate}" pattern="yyyy-MM-dd HH:mm"/></p></div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="userdir">
                        <div class="row">
                            <div class="col-xs-12 clearfix">
                                <label><span class="fa fa-yen"></span> 账户余额：</label>
                                <c:if test="${fns:getAmount(user.id) eq '' || fns:getAmount(user.id) == null}">
                                	<p>0 条</p>
                                </c:if>
                                <c:if test="${fns:getAmount(user.id) ne '' && fns:getAmount(user.id) != null}">
                                	<p>${fns:getAmount(user.id)} 条</p>
                                </c:if>
                                <a href="" class="btn btn-info" style="display: none;">充值</a>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <div class="col-sm-3">
            <div class="ibox float-e-margins data_today">
                <div class="ibox-title">
                    <h5>今日数据</h5>
                </div>
                <div class="ibox-content">
                    <div class="row">
                        <div class="col-xs-7 text"><span class="fa fa-arrow-circle-o-right"></span> 今日发送(条)</div>
                        <div class="col-xs-5 text-right"><span class="badge badge_success">${jmsgSmsDayReport.successCount}</span></div>
                    </div>
                    <c:if test="${jmsgSmsDayReport.successCount > 0}">
                    <div class="row success">
                        <div class="col-xs-12 text"><span class="fa fa-arrow-circle-o-right"></span> 到达率统计</div>
                        <div class="col-xs-6">　• 成功率</div>
                        <div class="col-xs-6 text-right"><span class="text-"><fmt:formatNumber type="number" value="${jmsgSmsDayReport.reportSuccessCount*100/jmsgSmsDayReport.successCount}" maxFractionDigits="2" pattern="0.00"></fmt:formatNumber>%</span></div>
                        <div class="col-xs-6">　• 失败率</div>
                        <div class="col-xs-6 text-right"><span class="text-"><fmt:formatNumber type="number" value="${jmsgSmsDayReport.reportFailCount*100/jmsgSmsDayReport.successCount}" maxFractionDigits="2" pattern="0.00"></fmt:formatNumber>%</span></div>
                        <div class="col-xs-6">　• 未知率</div>
                        <div class="col-xs-6 text-right"><span class="text-"><fmt:formatNumber type="number" value="${jmsgSmsDayReport.reportNullCount*100/jmsgSmsDayReport.successCount}" maxFractionDigits="2" pattern="0.00"></fmt:formatNumber>%</span></div>
                    </div>
                    </c:if>
                    <c:if test="${jmsgSmsDayReport.successCount <= 0}">
                    <div class="row success">
                        <div class="col-xs-12 text"><span class="fa fa-arrow-circle-o-right"></span> 到达率统计</div>
                        <div class="col-xs-6">　• 成功率</div>
                        <div class="col-xs-6 text-right"><span class="text-">--</span></div>
                        <div class="col-xs-6">　• 失败率</div>
                        <div class="col-xs-6 text-right"><span class="text-">--</span></div>
                        <div class="col-xs-6">　• 未知率</div>
                        <div class="col-xs-6 text-right"><span class="text-">--</span></div>
                    </div>
                    </c:if>
                    <div class="row">
                        <div class="col-xs-7 text"><span class="fa fa-arrow-circle-o-right"></span> 请求失败(条)</div>
                        <div class="col-xs-5 text-right"><span class="badge">${jmsgSmsDayReport.submitFailCount}</span></div>
                    </div>
                </div>
            </div>
        </div>
        <div class="col-sm-3">
            <div class="ibox float-e-margins newslist">
                <div class="ibox-title">
                    <h5>最新公告</h5>
                    <div class="ibox-tools" style="display: block;">
                        <!-- <a href="" class="text-info">更多>></a> -->
                    </div>
                </div>
                <div class="ibox-content">
                   <ul>
                   	<c:forEach items="${notifyList}" end="4" var="item" varStatus="status">
                   	<%-- <li><a onclick='showNotify("${item.title}", "<fmt:formatDate value="${item.createDate}" pattern="yyyy-MM-dd"/>", "${item.content}")'  class="clearfix"><span class="fa fa-caret-right pull-left"></span><p class="pull-left">${item.title}</p></a></li> --%>
                   	<li><a onclick='showNotify("${item.id}", "<fmt:formatDate value="${item.createDate}" pattern="yyyy-MM-dd"/>")'  class="clearfix"><span class="fa fa-caret-right pull-left"></span><p class="pull-left">${item.title}</p></a></li>
                   	</c:forEach>
                   </ul>
                </div>
            </div>
        </div>
    </div>
    <div class="row">
        <div class="col-sm-12">
            <div class="ibox float-e-margins">
                <div class="ibox-title">
                    <h5>快速导航</h5>
                    <div class="ibox-tools" style="display: block;">
                    </div>
                </div>
                <div class="ibox-content navlink">
                    <div class="row">
                        <div class="col-xs-2">
                            <a href="javascript:parent.addTabChild(this, '发信箱', '${ctx}/sms/jmsgSmsTask/smsGroupInit');" class="clearfix navlink1">
                                <p><span class="fa fa-pencil"></span>写 短 信 </p>
                            </a>
                        </div>
                        <div class="col-xs-2">
                            <a href="javascript:parent.addTabChild(this, '发信箱','${ctx}/sms/jmsgSmsTask/list');" class="clearfix navlink2">
                                <p><span class="fa fa-envelope"></span>发 信 箱 </p>
                            </a>
                        </div>
                        <div class="col-xs-2">
                            <a href="javascript:parent.addTabChild(this, '收信箱','${ctx}/sms/jmsgSmsDeliverPush/list');" class="clearfix navlink3">
                                <p><span class="fa fa-envelope"></span>收 信 箱 </p>
                            </a>
                        </div>
                        <div class="col-xs-2">
                            <a href="javascript:parent.addTabChild(this, '联系人','${ctx}/sms/jmsgAddresslistInfo/index');" class="clearfix navlink4">
                                <p><span class="fa fa-comments-o"></span>联 系 人</p>
                            </a>
                        </div>
                        <div class="col-xs-2">
                            <a href="javascript:parent.addTabChild(this, '群组管理','${ctx}/sms/jmsgAddresslistGroup/index');" class="clearfix navlink5">
                                <p><span class="fa fa-users"></span>群组管理</p>
                            </a>
                        </div>
                        <div class="col-xs-2">
                            <a href="javascript:parent.addTabChild(this, '批量导入','${ctx}/sms/jmsgAddresslistInfo/batchForm');" class="clearfix navlink6">
                                <p><span class="fa fa-retweet"></span>批量导入</p>
                            </a>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <%-- <c:set var="week2XValue" value=""></c:set>
    <c:set var="week2YValue" value=""></c:set>
    
    <c:set var="week1XValue" value=""></c:set>
    <c:set var="week1YValue" value=""></c:set>
   	<c:forEach items="${weekReportList}" var="item" varStatus="status">
   		<c:set var="week2XValue" value="${week2XValue},'<fmt:formatDate value='${item.day}' pattern='M/dd'/>'"></c:set>
   		<c:if test="${item.successCount > 0}">
   		<c:set var="week2YValue" value="${week2YValue},'<fmt:formatNumber type='number' value='${item.reportSuccessCount*100/item.successCount}' maxFractionDigits='2' pattern='0.00'></fmt:formatNumber>'"></c:set>
   		</c:if>
   		<c:if test="${item.successCount <= 0}">
   		<c:set var="week2YValue" value="${week2YValue},0"></c:set>
   		</c:if>
   		
   		<c:set var="week1XValue" value="${week1XValue},'<fmt:formatDate value='${item.day}' pattern='M/dd'/>'"></c:set>
   		<c:set var="week1YValue" value="${week1YValue},${item.successCount}"></c:set>
   	</c:forEach>
   	
   	
   	<c:set var="month2XValue" value=""></c:set>
    <c:set var="month2YValue" value=""></c:set>
    
    <c:set var="month1XValue" value=""></c:set>
    <c:set var="month1YValue" value=""></c:set>
   	<c:forEach items="${monthReportList}" var="item" varStatus="status">
   		<c:set var="month2XValue" value="${month2XValue},'<fmt:formatDate value='${item.day}' pattern='M/dd'/>'"></c:set>
   		<c:if test="${item.successCount > 0}">
   		<c:set var="month2YValue" value="${month2YValue},'<fmt:formatNumber type='number' value='${item.reportSuccessCount*100/item.successCount}' maxFractionDigits='2' pattern='0.00'></fmt:formatNumber>'"></c:set>
   		</c:if>
   		<c:if test="${item.successCount <= 0}">
   		<c:set var="month2YValue" value="${month2YValue},0"></c:set>
   		</c:if>
   		
   		<c:set var="month1XValue" value="${month1XValue}','<fmt:formatDate value='${item.day}' pattern='M/dd'/>"></c:set>
   		<c:set var="month1YValue" value="${month1YValue},${item.successCount}"></c:set>
   	</c:forEach> --%>
    <div class="row">
        <div class="col-sm-6">
            <div class="ibox float-e-margins">
                <div class="ibox-title">
                    <h5>短信发送量</h5>
                    <div class="ibox-tools" style="display: block;">
                        <div class="btn-group">
                            <button type="button" id="week2" class="btn btn-info">周</button>
                            <button type="button" id="month2" class="btn btn-default">月</button>
                        </div>
                    </div>
                </div>
                <div class="ibox-content">
                    <div class="row">
                        <div class="col-xs-12" id="main2week" style="width: 468px;height:300px;"></div>
                        <div class="col-xs-12" id="main2month" style="width: 468px;height:300px; display: none;"></div>
                    </div>
                </div>
            </div>
        </div>
    	<div class="col-sm-6">
    		<div class="ibox float-e-margins">
                <div class="ibox-title">
                    <h5>到达率统计</h5>
                    <div class="ibox-tools" style="display: block;">
                        <div class="btn-group">
                            <button type="button" id="week1" class="btn btn-info">周</button>
                            <button type="button" id="month1" class="btn btn-default">月</button>
                        </div>
                    </div>
                </div>
                <div class="ibox-content">
                    <div class="row">
                        <div class="col-xs-12" id="main1week" style="width: 468px;height:300px;"></div>
                        <div class="col-xs-12" id="main1month" style="width: 468px;height:300px; display: none;"></div>
                    </div>
                </div>
            </div>
    	</div>
    </div>
</div>
<script src="${ctxStatic}/style/sms/js/jquery-1.10.2.min.js"></script>
<script src="${ctxStatic}/style/sms/js/bootstrap.min.js"></script>
<div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                <h4 class="modal-title" id="myModalLabel">详情</h4>
            </div>
            <div class="modal-body">
                <h2>系统升级功能通知</h2>
                <span>2017-04-17</span>
                <div data-spy="scroll" data-target="#myScrollspy" data-offset="0" style="height:210px;overflow:auto; position: relative;">
                    <div class="newscon">
                        <p>系统升级功能通知系统升级功能通知系统升级功能通知系统升级功能通知系统升级功能通知系统升级功能通知系统升级功能通知系统升级功能通知系统升级功能通知系统升级功能通知系统升级功能通知系统升级功能通知。</p>
                    </div>
                    <div class="newscon">
                    	<label class="control-label">附件：</label>
    					<input type="hidden" id="files" name="files" htmlEscape="false" maxlength="255" class="input-xlarge"/>
    					<sys:ckfinder input="files" type="files" uploadPath="/oa/notify" selectMultiple="true" readonly="true" />
    				</div>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" onclick="hideNotify();" class="btn btn-primary">关闭</button>
            </div>
        </div><!-- /.modal-content -->
    </div><!-- /.modal -->
</div>

<script type="text/javascript">
$("#week2").click( function() 
{ 
	$("#week2").attr("class", "btn btn-info");
	$("#month2").attr("class", "btn btn-default");
	$("#main2week").show();
	$("#main2month").hide();
});
$("#month2").click( function() 
{ 
	$("#week2").attr("class", "btn btn-default");
	$("#month2").attr("class", "btn btn-info");
	$("#main2week").hide();
	$("#main2month").show();
});

$("#week1").click( function() 
{ 
	$("#week1").attr("class", "btn btn-info");
	$("#month1").attr("class", "btn btn-default");
	$("#main1week").show();
	$("#main1month").hide();
});
$("#month1").click( function() 
{ 
	$("#week1").attr("class", "btn btn-default");
	$("#month1").attr("class", "btn btn-info");
	$("#main1week").hide();
	$("#main1month").show();
});

function showNotify1(title, time, content)
{
	$('#myModal').find('.modal-body h2').text(title);
	$('#myModal').find('.modal-body span').text(time);
	$('#myModal').find('.modal-body .newscon p').text(content);
	$('#myModal').modal('show');
}
function showNotify(id, time)
{
	$.ajax({
		url : "${ctx}/oa/oaNotify/getOaNotifyById?id="+id,
		dataType : "json",
		success : function(data) {
			$('#myModal').find('.modal-body h2').text(data.title);
			$('#myModal').find('.modal-body span').text(time);
			$('#myModal').find('.modal-body .newscon p').html(data.content.replace(/\n/g,'<br/>'));
			$('#files').val(data.files);
			filesPreview();
			$('#myModal').modal('show');
		}
	});
}
function hideNotify()
{
	$('#myModal').modal('hide');
}

// 基于准备好的dom，初始化echarts实例
var myChart = echarts.init(document.getElementById('main1week'));
// 指定图表的配置项和数据
var option = {
	 tooltip : {
	        trigger: 'axis'
	    },
    xAxis: {
    	data: [${week1XValue}]
    },
    yAxis: {
    	axisLabel : { 
            formatter : '{value}%' 
		}
    },
    series: [{
        name: '到达率统计',
        type: 'bar',
        data: [${week1YValue}]
    }]
};

// 使用刚指定的配置项和数据显示图表。
myChart.setOption(option);

var myChart1 = echarts.init(document.getElementById('main1month'));
//指定图表的配置项和数据
var option1 = {
	tooltip : {
        trigger: 'axis'
    },
 xAxis: {
	 data: [${month1XValue}]
 },
 yAxis: {
	 axisLabel : { 
         formatter : '{value}%' 
		}
 },
 series: [{
     name: '到达率统计',
     type: 'bar',
     data: [${month1YValue}]
 }]
};

//使用刚指定的配置项和数据显示图表。
myChart1.setOption(option1);


var colors = ['#5793f3', '#d14a61', '#675bba'];
//基于准备好的dom，初始化echarts实例
var myChart2 = echarts.init(document.getElementById('main2week'));
// 指定图表的配置项和数据
var option2 = {
		color: colors,
	    tooltip: {
	        trigger: 'none',
	        axisPointer: {
	            type: 'cross'
	        }
	    },
	    grid: {
	        top: 70,
	        bottom: 50
	    },
	    xAxis: [
	        {
	            type: 'category',
	            axisTick: {
	                alignWithLabel: true
	            },
	            axisLine: {
	                onZero: false,
	                lineStyle: {
	                    color: colors[1]
	                }
	            },
	            axisPointer: {
	                label: {
	                    formatter: function (params) {
	                        return '发送量  ' + params.value + '：' + params.seriesData[0].data;
	                    }
	                }
	            },
	            data: [${week2XValue}]
	        }
	    ],
	    yAxis: [
	        {
	            type: 'value'
	        }
	    ],
	    series: [
	        {
	            name:'2016 降水量',
	            type:'line',
	            smooth: true,
	            data: [${week2YValue}]
	        }
	    ]
	};

// 使用刚指定的配置项和数据显示图表。
myChart2.setOption(option2);

//基于准备好的dom，初始化echarts实例
var myChart3 = echarts.init(document.getElementById('main2month'));
// 指定图表的配置项和数据
var option3 = {
		color: colors,
	    tooltip: {
	        trigger: 'none',
	        axisPointer: {
	            type: 'cross'
	        }
	    },
	    grid: {
	        top: 70,
	        bottom: 50
	    },
	    xAxis: [
	        {
	            type: 'category',
	            axisTick: {
	                alignWithLabel: true
	            },
	            axisLine: {
	                onZero: false,
	                lineStyle: {
	                    color: colors[1]
	                }
	            },
	            axisPointer: {
	                label: {
	                    formatter: function (params) {
	                        return '发送量  ' + params.value + '：' + params.seriesData[0].data;
	                    }
	                }
	            },
	            data: [${month2XValue}]
	        }
	    ],
	    yAxis: [
	        {
	            type: 'value'
	        }
	    ],
	    series: [
	        {
	            name:'2016 降水量',
	            type:'line',
	            smooth: true,
	            data: [${month2YValue}]
	        }
	    ]
	};

// 使用刚指定的配置项和数据显示图表。
myChart3.setOption(option3);
</script>
</body>
</html>