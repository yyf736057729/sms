package com.siloyou.jmsg.modules.monitor.task;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;
import com.sanerzone.common.support.utils.DateUtils;
import com.siloyou.core.common.utils.EmailUtil;
import com.siloyou.core.common.utils.SpringContextHolder;
import com.siloyou.core.common.utils.StringUtils;
import com.siloyou.core.modules.sys.entity.Dict;
import com.siloyou.core.modules.sys.utils.DictUtils;
import com.siloyou.core.modules.sys.utils.UserUtils;
import com.siloyou.jmsg.common.utils.GatewayUtils;
import com.siloyou.jmsg.modules.sms.dao.JmsgGatewayMonitorDao;
import com.siloyou.jmsg.modules.sms.dao.JmsgSmsSendDao;
import com.siloyou.jmsg.modules.sms.dao.JmsgSmsWarnDao;
import com.siloyou.jmsg.modules.sms.entity.JmsgGatewayMonitor;
import com.siloyou.jmsg.modules.sms.entity.JmsgSmsWarn;

//定时任务
@Service
@Lazy(false)
public class SmsSubmitMonitorTask
{
    private static Logger logger = LoggerFactory.getLogger(SmsSubmitMonitorTask.class);
    
    private JmsgGatewayMonitorDao jmsgGatewayMonitorDao = SpringContextHolder.getBean(JmsgGatewayMonitorDao.class);
    
    private JmsgSmsWarnDao jmsgSmsWarnDao = SpringContextHolder.getBean(JmsgSmsWarnDao.class);
    
    private JmsgSmsSendDao jmsgSmsSendDao = SpringContextHolder.getBean(JmsgSmsSendDao.class);
    
    
    //每2分钟执行一次
    @Scheduled(cron = "0 0/10 * * * ?")
    public void execSendMonitor()
    {
    	String tableName = "jmsg_sms_send_"+DateUtils.getDayOfMonth(0);	
    	Map<String,String> map = Maps.newHashMap();
    	map.put("tableName", tableName);
    	List<Map> list = jmsgGatewayMonitorDao.countGateWaySend(map);
    	for(Map item : list) {
    		int reportNullCount = ((BigDecimal)item.get("reportNullCount")).intValue() ; 
    		int sendCount = ((Long)item.get("sendCount")).intValue() ; 
    		
    		logger.info("网关监测：网关ID：{} 10分钟前发送条数：{}, 状态为空条数：{}", item.get("gatewayId"), sendCount, reportNullCount);
			if(reportNullCount > 0 && sendCount/2 <= reportNullCount) {
				String gatewayId = String.valueOf(item.get("gatewayId"));
				EmailUtil.sendEmail("网关告警通知","通道Id:" + gatewayId + "通道名称:"+GatewayUtils.getGatewayInfo(gatewayId).getGatewayName()+ ", 发生告警。10分钟前状态为空率(" + reportNullCount +"/"+ sendCount + "),大于50%.请处理！");
			}
			
    	}
    }
    
    //每2分钟执行一次
    @Scheduled(cron = "0 0/2 * * * ?")
    public void exec()
    {
    	String tableName = "jmsg_sms_submit_"+DateUtils.getDayOfMonth(0);	
    	Map<String,String> map = Maps.newHashMap();
    	map.put("tableName", tableName);
        List<JmsgGatewayMonitor> list = jmsgGatewayMonitorDao.countGateWay(map);
        StringBuilder monitorInfo = new StringBuilder();
        for (JmsgGatewayMonitor gatewayMonitor : list)
        {
            logger.info("网关监测：网关ID：{} 5分钟失败次数：{} 连续失败次数：{}",
                gatewayMonitor.getGatewayId(),
                gatewayMonitor.getTimeFailCount(),
                gatewayMonitor.getContinuousFailCount());
            
            if (StringUtils.isNotBlank(gatewayMonitor.getGatewayId()))
            {
                jmsgGatewayMonitorDao.insert(gatewayMonitor);
                
                //告警：5分钟内失败次数   >  5次         连续失败次数   >  2次
                if (gatewayMonitor.getTimeFailCount() > 5 || gatewayMonitor.getContinuousFailCount() > 2)
                {
                	monitorInfo.append("网关监测：网关ID：").append(gatewayMonitor.getGatewayId())
                	.append("网关名称：").append(GatewayUtils.getGatewayInfo(gatewayMonitor.getGatewayId()).getGatewayName())
                	.append(", 5分钟失败次数：").append(gatewayMonitor.getTimeFailCount()).append(", 连续失败次数：")
                	.append(gatewayMonitor.getContinuousFailCount()).append("<br/>");
                	
                }
            }
        }
        
        if (monitorInfo.length() > 0)
        {
            JmsgSmsWarn jmsgSmsWarn = new JmsgSmsWarn();
            jmsgSmsWarn.setWarnType("1");
            jmsgSmsWarn.setWarnStatus("0");
            List<JmsgSmsWarn> warnList = jmsgSmsWarnDao.findList(jmsgSmsWarn);
            
            // 判断是否已经存在网关类型、未处理的告警。如果存在，则不做处理；如果不存在，则新增网关告警，并发邮件通知。
//            if (null != warnList && warnList.size() > 0)
//            {
//                logger.info("存在待处理的网关类型告警，请及时处理！");
//            }
//            else
//            {
                String content = monitorInfo.toString();
                jmsgSmsWarn.setWarnContent(content);
                jmsgSmsWarnDao.insert(jmsgSmsWarn);
                
                EmailUtil.sendEmail("网关告警通知", content);
//            }
        }
        
    }
    
    /**
     * 每9分钟执行一次
     * 分发失败状态告警如: F0071/2/3/4 通道匹配错误，需要告警  
     */
    @Scheduled(cron = "0 0/10 * * * ?")
    public void execGatewayError(){
    	String tableName = "jmsg_sms_send_"+DateUtils.getDayOfMonth(0);
    	Map<String,String> map = Maps.newHashMap();
    	map.put("tableName", tableName);
    	List<Map<String,Object>> list = jmsgSmsSendDao.queryGatewayErrorCount(map);
    	if(list != null && !list.isEmpty()){
    		StringBuilder detail = new StringBuilder();
    		int size = 0;
    		for (Map<String, Object> resultMap : list) {
    			String userId = String.valueOf(resultMap.get("userId"));
    			long count = (long) resultMap.get("count");
    			if(count > 10) {
    				detail.append("【用户ID：").append(userId).append("用户名称：").append(UserUtils.get(userId).getName()).append("通道匹配错误总量：").append(resultMap.get("count")).append("】<br/>");
    				size ++;
    			}
			}
    		if(size > 0)
    			EmailUtil.sendEmail("网关告警通知","分发失败状态告警,通道匹配错误("+DateUtils.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss")+"),详情情况如下:"+detail.toString());
    	}
    }
    
    
}
