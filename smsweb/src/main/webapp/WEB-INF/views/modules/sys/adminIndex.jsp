<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<!DOCTYPE html>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title></title>
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<link rel="shortcut icon" href="${ctxStatic}/style/sms/konsone.ico" />
    <link href="${ctxStatic}/style/sms/css/bootstrap.min.css" rel="stylesheet">
    <link href="${ctxStatic}/style/sms/font-awesome/css/font-awesome.css" rel="stylesheet">
    <link href="${ctxStatic}/style/sms/css/animate.css" rel="stylesheet">
    <link href="${ctxStatic}/style/sms/css/style.css" rel="stylesheet">
    <script src="${ctxStatic}/style/sms/js/jquery-1.10.2.min.js"></script>
	<script src="${ctxStatic}/style/sms/js/bootstrap.min.js"></script>
    <script src="${ctxStatic}/echart/echarts.min.js"></script>
    
    <script type="text/javascript">
    
		//近30天发送量数据趋势
		function onInitDay(){
			$.ajax({
				type: 'POST',
				url: '${ctx}/sys/user/queryCountByDay',
				dataType: 'json',
				success: function(data){
					initDay(data.day.split(","),data.countArray.split(","),data.successArray.split(","));
				},
				error:function(){
					alertx("加载近30天发送量数据趋势失败");
				}
			});
		}
		
		//运营商网关成功量数据
		function onInitDayPhoneType(queryType){
			$('#btnDayPhoneType1').attr('class','btn  btn-default');
			$('#btnDayPhoneType2').attr('class','btn  btn-default');
			
			if(queryType == 1){
				$('#btnDayPhoneType1').attr('class','btn  btn-blue');
			}else{
				$('#btnDayPhoneType2').attr('class','btn  btn-blue');
			}
			$.ajax({
				type: 'POST',
				url: '${ctx}/sys/user/queryCountByDayPhoneType?queryType='+queryType,
				dataType: 'json',
				success: function(data){
					initDayPhoneType(data.day.split(","),data.ydCountArray.split(","),data.ltCountArray.split(","),data.dxCountArray.split(","));
				},
				error:function(){
					alertx("加载运营商网关成功量数据失败");
				}
			});
		}
		
		//运营商网关成功量、状态成功量、状态失败量、状态未知量
		function onInitPhoneType(id,queryType){
			
			$.ajax({
				type: 'POST',
				url: '${ctx}/sys/user/queryCountByPhoneType?queryType='+queryType,
				dataType: 'json',
				success: function(data){
					if(id == 'all'){
						onInitSuccess("submitSuccessId",queryType,data);
						onInitSuccess("reportSuccessId",queryType,data);
						onInitSuccess("reportFailId",queryType,data);
						onInitSuccess("reportNullId",queryType,data);
					}else{
						onInitSuccess(id,queryType,data);
					}
				},
				error:function(){
					alertx("加载数据失败");
				}
			});
		}
		
		//网关成功
		function onInitSuccess(id,queryType,data){
			 
			$('#btn'+id+'1').attr('class','btn  btn-default');
			$('#btn'+id+'2').attr('class','btn  btn-default');
			
			if(queryType == 1){
				$('#btn'+id+'1').attr('class','btn  btn-blue');
			}else{
				$('#btn'+id+'2').attr('class','btn  btn-blue');
			}
			
			
			 var ydcount = 0;
			 var ltcount = 0;
			 var dxcount = 0;
			 $.each(data, function(i,item){
				if(item.phoneType == 'YD'){
					if(id=='submitSuccessId'){
					 	ydcount = item.submitSuccessCount;
					}else if(id=='reportSuccessId'){
						ydcount = item.reportSuccessCount;
					}else if(id=='reportFailId'){
						ydcount = item.reportFailCount-item.submitFailCount;
					}else if(id=='reportNullId'){
						ydcount = item.reportNullCount;
					}
				 }
				 if(item.phoneType == 'LT'){
					 if(id=='submitSuccessId'){
					 	ltcount = item.submitSuccessCount;
					 }else if(id=='reportSuccessId'){
						ltcount = item.reportSuccessCount;
					 }else if(id=='reportFailId'){
						ltcount = item.reportFailCount-item.submitFailCount;
					 }else if(id=='reportNullId'){
						ltcount = item.reportNullCount;
					 }
				 }
				 if(item.phoneType == 'DX'){
					if(id=='submitSuccessId'){
						dxcount = item.submitSuccessCount;
					}else if(id=='reportSuccessId'){
						dxcount = item.reportSuccessCount;
					}else if(id=='reportFailId'){
						dxcount = item.reportFailCount-item.submitFailCount;
					}else if(id=='reportNullId'){
						dxcount = item.reportNullCount;
					}
				 }
			 });
			 
			 initPhoneType(id,ydcount,ltcount,dxcount);
		}
    
		function initDay(day,count,success){
			var myCharts1 = echarts.init(document.getElementById('dayId'));
			option1 = {
					backgroundColor: 'white',
				    tooltip : {
				        trigger: 'axis'
				    },
				    legend: {
				    	selectedMode:false,
				        data:['网关成功量','状态报告成功量']
				    },
				    calculable : true,
				    grid: {
				        left: '5%',
				        right: '3%',
				        bottom: '3%',
				        containLabel: true
				    },
				    xAxis : [
				        {
				            type : 'category',
				            data : day,
				            axisLabel :{    
			                    interval:0,  
			                    clickable:true     
			                },  
			                splitLine:{   
			                          show:false  
			                },    
			                axisTick: {  
			                    alignWithLabel: true  
			                }
				        }
				    ],
				    yAxis : [
				        {
				            type : 'value'
				        }
				    ],
				    series : [
				        {
				            name:'网关成功量',
				            type:'line',
				            data:count
				            
				        },
				        {
				            name:'状态报告成功量',
				            type:'bar',
				            data:success
				        }
				    ],
				    color: ['#10a1e9','#ffbb8a']
				};
				myCharts1.setOption(option1);
		}
		
		function initDayPhoneType(day,ydArray,ltArray,dxArray){
			var myCharts2 = echarts.init(document.getElementById('dayPhoneTypeId'));
			option2 = {
					backgroundColor: 'white',
				    tooltip : {
				        trigger: 'axis'
				    },
				    legend: {
				    	selectedMode:false,
				        data:['中国移动','中国联通','中国电信']
				    },
				    calculable : true,
				    grid: {
				        left: '5%',
				        right: '3%',
				        bottom: '3%',
				        containLabel: true
				    },
				    xAxis : [
				        {
				            type : 'category',
				            data : day,
				            axisLabel :{    
			                    interval:0,  
			                    clickable:true     
			                },  
			                splitLine:{   
			                          show:false  
			                },    
			                axisTick: {  
			                    alignWithLabel: true  
			                }
				        }
				    ],
				    yAxis : [
				        {
				            type : 'value'
				        }
				    ],
				    series : [
				        {
				            name:'中国移动',
				            type:'line',
				            data:ydArray
				            
				        },
				        {
				            name:'中国联通',
				            type:'line',
				            data:ltArray
				        },
				        {
				            name:'中国电信',
				            type:'line',
				            data:dxArray
				        }
				    ],
				    color: ['#20c95f','#ff9960','#239bfc']
				};
				myCharts2.setOption(option2);
		}
		
		
		function initPhoneType(id,ydCount,ltCount,dxCount){
			
			var myCharts3 = echarts.init(document.getElementById(id));
			option3 = {
					backgroundColor: 'white',
				    title : {
				        x:'center'
				    },
				    tooltip : {
				        trigger: 'item',
				        formatter: "{a} <br/>{b} : {c} ({d}%)"
				    },
				    legend: {
				        orient : 'vertical',
				        x : 'left',
				        data:['中国移动','中国联通','中国电信']
				    },
				    calculable : true,

				    series : [
				        {
				            type:'pie',
				            radius: ['50%', '70%'],
				            avoidLabelOverlap: false,
				            label: {
				                normal: {
				                    show: false,
				                    position: 'center'
				                },
				                emphasis: {
				                    show: true,
				                    textStyle: {
				                        fontSize: '20',
				                        fontWeight: 'bold'
				                    }
				                }
				            },
				            labelLine: {
				                normal: {
				                    show: false
				                }
				            },
				            data:[
				                {value:ydCount, name:'中国移动'},
				                {value:ltCount, name:'中国联通'},
				                {value:dxCount, name:'中国电信'}
				            ]
				        }
				    ],
				    color: ['#20c95f','#ff9960','#239bfc']
				};
			    myCharts3.setOption(option3);
		}
		
		function initRule1(){
			
			var myCharts4 = echarts.init(document.getElementById("fitleRuleId1"));
			option4 = {
				    color: ['#b6a1dc'],
				    tooltip : {
				        trigger: 'axis',
				        axisPointer : {            // 坐标轴指示器，坐标轴触发有效
				            type : 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
				        }
				    },
				    grid: {
				        left: '3%',
				        right: '2%',
				        bottom: '3%',
				        containLabel: true
				    },
				    xAxis : [
				        {
				            type : 'category',
				            data : ['系统黑名单', '营销黑名单', '通道匹配', '内容过滤', '敏感词', '号段匹配', '超日限额', '超月限额' , '营销频控'],
				            axisTick: {
				                alignWithLabel: true
				            },
						    axisLabel :{  
						        interval:0   
						    }
				        }
				    ],
				    yAxis : [
				        {
				            type : 'value'
				        }
				    ],
				    series : [
				        {
				            type:'bar',
				            barWidth: '60%',
				            data:[335, 310, 234, 135, 212, 330, 220,700,100]
				        }
				    ]
				};
			    myCharts4.setOption(option4);
		}

		function initRule2(){
			
			var myCharts5 = echarts.init(document.getElementById("fitleRuleId2"));
			option5 = {
				    tooltip : {
				        trigger: 'item',
				        formatter: "{a} <br/>{b} : {c} ({d}%)"
				    },
				    series : [
				        {
				            type: 'pie',
				            radius : '55%',
				            center: ['50%', '60%'],
				            data:[
				                {value:335, name:'系统黑名单'},
				                {value:310, name:'营销黑名单'},
				                {value:234, name:'通道匹配'},
				                {value:135, name:'内容过滤'},
				                {value:212, name:'敏感词'},
				                {value:330, name:'号段匹配'},
				                {value:220, name:'超日限额'},
				                {value:700, name:'超月限额'},
				                {value:1000, name:'营销频控'}
				                
				            ],
				            itemStyle: {
				                emphasis: {
				                    shadowBlur: 10,
				                    shadowOffsetX: 0,
				                    shadowColor: 'rgba(0, 0, 0, 0.5)'
				                }
				            }
				        }
				    ]
				};

			    myCharts5.setOption(option5);
		}


    </script>
</head>
<body>
<div class="wrapper-content cadmin_con">
    <form id="" class="form-horizontal" action="" method="" novalidate="">
        <div class="row">
            <div class="col-sm-6">
                <div class="ibox float-e-margins">
                    <div class="ibox-title">
                        <h5>用户基本信息</h5>
                    </div>
                    <div class="ibox-content">
                        <div class="row user_info">
                            <div class="col-xs-3 text-center userpic">
                                <img src="${ctxStatic}/style/sms/images/userpic.png">
                                <h6>${user.loginName}</h6>
                            </div>
                            <div class="col-xs-9 username">
                                <div class="row form-inline">
                                    <div class="row">
                                        <div class="col-xs-6">
                                            <blockquote>${user.name}&nbsp;&nbsp; <p class="badge badge-info">正常</p> <p class="badge" style="display: none;">停用</p></blockquote>
                                        </div>
                                        <div class="col-xs-6 user_role">
                                            <div class="form-group"><label><span class="fa fa-flag-o"></span> 用户角色：</label>
                                            <p><c:forEach items="${user.roleList }" var="item">${item.name}&nbsp;&nbsp;</c:forEach></p></div>
                                        </div>
                                    </div>
                                    <div class="row">
                                        <div class="col-xs-12 userdir user_cog clearfix">
                                            <div class="form-group"><label><span class="fa fa-history"></span>上次登录：</label>
                                            <b>时间：<fmt:formatDate value="${user.loginDate}" type="date" pattern="yyyy年MM月dd日 E HH:mm:ss"/></b></div>
                                        </div>
                                        <div class="col-xs-6">
                                            <div class="form-group"><label><span class="fa fa-tablet"></span> 手机：</label><p>${user.mobile}</p></div>
                                        </div>
                                        <div class="col-xs-6">
                                            <div class="form-group"><label><span class="fa fa-envelope-o"></span> 邮箱：</label><p>${user.email}</p></div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="col-xs-6 usercount">
                <div class="ibox float-e-margins">
                    <div class="ibox-title">
                        <h5>今日数据汇总</h5>
                    </div>
                    <div class="ibox-content">
                        <div class="row">
                            <div class="col-xs-4">
                                <p class="count_all"><span>队列总量</span><em>${smsDayReport.sendCount}<i>条</i></em></p>
                            </div>
                            <div class="col-xs-4">
                                <p class="count_all"><span>网关总量</span><em>${smsDayReport.successCount}<i>条</i></em></p>
                            </div>
                            <div class="col-xs-4">
                                <p class="count_all"><span>状态成功量</span><em>${smsDayReport.reportSuccessCount}<i>条</i></em></p>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-xs-4">
                                <p class="count_sucess"><span>状态成功率</span>
                                <em>
                                <c:if test="${smsDayReport.successCount eq 0 }">0</c:if>
								<c:if test="${smsDayReport.successCount gt 0 }">
								<fmt:formatNumber type="number" value="${smsDayReport.reportSuccessCount*100/smsDayReport.successCount}" maxFractionDigits="2" pattern="0.00"></fmt:formatNumber>
								</c:if>	
                                <i>%</i></em></p>
                            </div>
                            <div class="col-xs-4">
                                <p class="count_fail"><span>状态失败率</span>
                                <em>
                                <c:if test="${smsDayReport.successCount eq 0 }">0</c:if>
								<c:if test="${smsDayReport.successCount gt 0 }">
								<fmt:formatNumber type="number" value="${(smsDayReport.reportFailCount-smsDayReport.submitFailCount)*100/smsDayReport.successCount}" maxFractionDigits="2" pattern="0.00"></fmt:formatNumber>
								</c:if>
                                <i>%</i></em></p>
                            </div>
                            <div class="col-xs-4">
                                <p class="count_unknown"><span>状态未知率</span>
                                <em>
								<c:if test="${smsDayReport.successCount eq 0 }">0</c:if>
								<c:if test="${smsDayReport.successCount gt 0 }">
								<fmt:formatNumber type="number" value="${smsDayReport.reportNullCount*100/smsDayReport.successCount}" maxFractionDigits="2" pattern="0.00"></fmt:formatNumber>
								</c:if>
								<i>%</i></em></p>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <div class="row">
            <div class="col-sm-12">
                <div class="ibox float-e-margins">
                    <div class="ibox-title">
                        <h5>快速导航</h5>
                    </div>
                    <div class="ibox-content navlink">
                        <div class="row">
                            <div class="col-xs-2 text-center">
                                <a href="javascript:parent.addTabChild(this, '短信审核管理','${ctx}/sms/jmsgSmsTask/reviewList');" class="clearfix navlink1">
                                    <p><span class="fa fa-hourglass-2"> 短信审核管理</span></p>
                                </a>
                            </div>
                            <div class="col-xs-2 text-center">
                                <a href="javascript:parent.addTabChild(this, '下发短信查询','${ctx}/sms/jmsgSmsSend/detailInit');" class="clearfix navlink2">
                                    <p><span class="fa fa-file-text-o"> 下发短信查询</span></p>
                                </a>
                            </div>
                            <div class="col-xs-2 text-center">
                                <a href="javascript:parent.addTabChild(this, '用户配置管理','${ctx}/account/jmsgAccount/index');" class="clearfix navlink3">
                                    <p><span class="fa fa-diamond"> 用户配置管理</span></p>
                                </a>
                            </div>
                            <div class="col-xs-2 text-center">
                                <a href="javascript:parent.addTabChild(this, '模拟通道开户','${ctx}/sms/jmsgUserGateway/form');" class="clearfix navlink4">
                                    <p><span class="fa fa-key"> 模拟通道开户</span></p>
                                </a>
                            </div>
                            <div class="col-xs-2 text-center">
                                <a href="javascript:parent.addTabChild(this, '用户上行配置','${ctx}/sms/jmsgDeliverNumber/config');" class="clearfix navlink5">
                                    <p><span class="fa fa-yen"> 用户上行配置</span></p>
                                </a>
                            </div>
                            <div class="col-xs-2 text-center">
                                <a href="javascript:parent.addTabChild(this, '用户签名配置','${ctx}/sms/jmsgGatewaySign/config');" class="clearfix navlink6">
                                    <p><span class="fa fa-yen"> 用户签名配置</span></p>
                                </a>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <div class="row">
            <div class="col-xs-12">
                <div class="ibox float-e-margins">
                    <div class="ibox-title">
                        <h5>近30天发送量数据趋势（单位：条）</h5>
                    </div>
                    <div class="ibox-content text-center charts" style="height:320px;">
                    	<div id="dayId" style="width: 100%;height: 100%;"></div>
                    </div>
                </div>
            </div>
        </div>
        <div class="row">
            <div class="col-xs-12">
                <div class="ibox float-e-margins">
                    <div class="ibox-title">
                        <h5>运营商网关成功量数据（单位：条）</h5>
                        <div class="ibox-tools">
                            <div class="btn-toolbar" role="toolbar">
                                <div class="btn-group btn-group-sm">
                                    <button id="btnDayPhoneType1" type="button" class="btn btn-blue" onclick="javascript:onInitDayPhoneType(1)">近7天</button>
                                    <button id="btnDayPhoneType2" type="button" class="btn btn-default" onclick="javascript:onInitDayPhoneType(2)">近30天</button>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="ibox-content text-center charts" style="height: 320px;">
                     	<div id="dayPhoneTypeId" style="width: 100%;height: 100%;"></div>
                    </div>
                </div>
            </div>
        </div>
        <div class="row pie4">
            <div class="col-xs-3">
                <div class="ibox float-e-margins">
                    <div class="ibox-title">
                        <h5>网关成功量占比</h5>
                        <div class="ibox-tools">
                            <div class="btn-toolbar" role="toolbar">
                                <div class="btn-group btn-group-sm">
                                    <button id="btnsubmitSuccessId1" type="button" class="btn btn-blue" onclick="javascript:onInitPhoneType('submitSuccessId',1)">近7天</button>
                                    <button id="btnsubmitSuccessId2" type="button" class="btn btn-default" onclick="javascript:onInitPhoneType('submitSuccessId',2)">近30天</button>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="ibox-content text-center charts" style="height: 320px;">
                        <div id="submitSuccessId" style="width: 100%;height: 100%;"></div>
                    </div>
                </div>
            </div>
            <div class="col-xs-3">
                <div class="ibox float-e-margins">
                    <div class="ibox-title">
                        <h5>状态成功量占比</h5>
                        <div class="ibox-tools">
                            <div class="btn-toolbar" role="toolbar">
                                <div class="btn-group btn-group-sm">
                                    <button id="btnreportSuccessId1" type="button" class="btn btn-blue" onclick="javascript:onInitPhoneType('reportSuccessId',1)">近7天</button>
                                    <button id="btnreportSuccessId2" type="button" class="btn btn-default" onclick="javascript:onInitPhoneType('reportSuccessId',2)">近30天</button>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="ibox-content text-center charts" style="height: 320px;">
                        <div id="reportSuccessId" style="width: 100%;height: 100%;"></div>
                    </div>
                </div>
            </div>
            <div class="col-xs-3">
                <div class="ibox float-e-margins">
                    <div class="ibox-title">
                        <h5>状态失败量占比</h5>
                        <div class="ibox-tools">
                            <div class="btn-toolbar" role="toolbar">
                                <div class="btn-group btn-group-sm">
                                    <button id="btnreportFailId1" type="button" class="btn btn-blue" onclick="javascript:onInitPhoneType('reportFailId',1)">近7天</button>
                                    <button id="btnreportFailId2" type="button" class="btn btn-default" onclick="javascript:onInitPhoneType('reportFailId',2)">近30天</button>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="ibox-content text-center charts" style="height: 320px;">
                        <div id="reportFailId" style="width: 100%;height: 100%;"></div>
                    </div>
                </div>
            </div>
            <div class="col-xs-3">
                <div class="ibox float-e-margins">
                    <div class="ibox-title">
                        <h5>状态未知量占比</h5>
                        <div class="ibox-tools">
                            <div class="btn-toolbar" role="toolbar">
                                <div class="btn-group btn-group-sm">
                                    <button id="btnreportNullId1" type="button" class="btn btn-blue" onclick="javascript:onInitPhoneType('reportNullId',1)">近7天</button>
                                    <button id="btnreportNullId2" type="button" class="btn btn-default" onclick="javascript:onInitPhoneType('reportNullId',2)">近30天</button>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="ibox-content text-center charts" style="height: 320px;">
                        <div id="reportNullId" style="width: 100%;height: 100%;"></div>
                    </div>
                </div>
            </div>
        </div>
        <div class="row">
            <div class="col-xs-12">
                <div class="ibox float-e-margins">
                    <div class="ibox-title">
                        <h5>系统拦截数据分析（单位：条）</h5>
                        <div class="ibox-tools">
                            <div class="btn-toolbar" role="toolbar">
                                <div class="btn-group btn-group-sm">
                                    <button type="button" class="btn btn-blue" onclick="javascript:onQuery(1)">今天</button>
                                    <button type="button" class="btn btn-default" onclick="javascript:onQuery(2)">近7天</button>
                                    <button type="button" class="btn btn-default" onclick="javascript:onQuery(3)">近30天</button>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="ibox-content text-center charts row">
                        <div class="row">
                            <div class="col-xs-8" style="height: 320px;">
                                <div id="fitleRuleId1" style="width: 100%;height: 100%;"></div>
                            </div>
                            <div class="col-xs-4" style="height: 320px;">
                                <div id="fitleRuleId2" style="width: 100%;height: 100%;"></div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <!-- 
        <div class="row">
            <div class="col-xs-12">
                <div class="ibox float-e-margins">
                    <div class="ibox-title">
                        <h5>用户发送量前10名的数据排名</h5>
                        <div class="ibox-tools">
                            <div class="btn-toolbar" role="toolbar">
                                <div class="btn-group btn-group-sm">
                                    <button type="button" class="btn btn-blue" onclick="javascript:onQuery(1)">近7天</button>
                                    <button type="button" class="btn btn-default" onclick="javascript:onQuery(2)">近30天</button>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="ibox-content text-center charts">
                        <img src="images/pic7.jpg" alt="">
                    </div>
                </div>
            </div>
        </div>
        <div class="row">
            <div class="col-xs-12">
                <div class="ibox float-e-margins">
                    <div class="ibox-title">
                        <h5>签名发送量前30名的数据分析</h5>
                        <div class="ibox-tools">
                            <div class="btn-toolbar" role="toolbar">
                                <div class="btn-group btn-group-sm">
                                    <button type="button" class="btn btn-blue" onclick="javascript:onQuery(1)">近7天</button>
                                    <button type="button" class="btn btn-default" onclick="javascript:onQuery(2)">近30天</button>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="ibox-content text-center charts">
                        <img src="images/pic8.jpg" alt="">
                    </div>
                </div>
            </div>
        </div>
         -->
    </form>
</div>
<script type="text/javascript">
onInitDay();//加载近30天发送量数据趋势
onInitDayPhoneType(1);//加载运营商网关成功量数据
onInitPhoneType("all",1);//加载网关成功量、状态成功量、状态失败量、状态未知量
initRule1();
initRule2();
</script>
</body>
</html>