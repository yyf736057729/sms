package com.siloyou.jmsg.common.utils;

import com.siloyou.core.common.utils.EhCacheUtils;
import com.siloyou.core.common.utils.SpringContextHolder;
import com.siloyou.jmsg.modules.sms.dao.JmsgSmsReportDao;
import com.siloyou.jmsg.modules.sms.entity.JmsgGatewayInfo;
import com.siloyou.jmsg.modules.sms.entity.JmsgSmsReport;

/**
 * @author yuyunfeng
 * @create_time 2019/1/25
 * @describe ${短信回执报告}
 */
public class SmsReportUtils {
    private static JmsgSmsReportDao jmsgSmsReportDao = SpringContextHolder.getBean(JmsgSmsReportDao.class);

    public static JmsgSmsReport getSmsByBizid(String  bizid){
        Object obj = EhCacheUtils.get("reprot"+bizid);
        if(obj == null){
            JmsgSmsReport jmsgSmsReport = new JmsgSmsReport();
            jmsgSmsReport.setBizid(bizid);
            JmsgSmsReport jmsgSmsReport2 = jmsgSmsReportDao.findByBizid(jmsgSmsReport);
            EhCacheUtils.put("reprot"+bizid,jmsgSmsReport2);
            return jmsgSmsReport2;
        }else {
            if(obj instanceof JmsgSmsReport){
                return (JmsgSmsReport)obj;
            }
        }
        return null;
    }
}
