package com.siloyou.jmsg.common.utils;

import com.siloyou.core.common.utils.SpringContextHolder;
import com.siloyou.jmsg.modules.sms.dao.JmsgSmsGatewayTmplDao;
import com.siloyou.jmsg.modules.sms.entity.JmsgSmsGatewayTmpl;

import java.util.List;

/**
 * @author yuyunfeng
 * @create_time 2019/1/14
 * @describe ${用户模板工具类}
 */
public class UserTmplUtils {
    private static JmsgSmsGatewayTmplDao jmsgSmsGatewayTmplDao = SpringContextHolder.getBean(JmsgSmsGatewayTmplDao.class);

    /**
     * 获取内容模板列表
     * @return
     */
    public static List<JmsgSmsGatewayTmpl> getUserTmpl()
    {
        JmsgSmsGatewayTmpl jmsgSmsGatewayTmpl = new JmsgSmsGatewayTmpl();
        return jmsgSmsGatewayTmplDao.findList(jmsgSmsGatewayTmpl);
    }
}
