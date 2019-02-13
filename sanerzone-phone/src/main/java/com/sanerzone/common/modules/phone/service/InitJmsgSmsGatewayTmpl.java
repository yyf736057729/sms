package com.sanerzone.common.modules.phone.service;

import com.sanerzone.common.modules.phone.dao.JmsgSmsGatewayTmplDao;
import com.sanerzone.common.modules.phone.entity.ContentManage;
import com.sanerzone.common.modules.phone.entity.JmsgSmsGatewayTmpl;
import com.sanerzone.common.modules.phone.entity.JmsgSmsUserTmpl;
import com.sanerzone.common.modules.phone.utils.SmsUtils;
import com.sanerzone.common.support.utils.EhCacheUtils;
import com.sanerzone.common.support.utils.SpringContextHolder;

import java.util.List;

/**
 * @description: 加载模板
 * @author: Cral
 * @create: 2019-02-11 17:46
 */
public class InitJmsgSmsGatewayTmpl {
    private static JmsgSmsGatewayTmplDao jmsgSmsGatewayTmplDao= SpringContextHolder.getBean(JmsgSmsGatewayTmplDao.class);
    public void InitJmsgSmsGatewayTmpl(){
        List<JmsgSmsGatewayTmpl> list = jmsgSmsGatewayTmplDao.findList(null);
        if(list != null && list.size() >0){
            for(JmsgSmsGatewayTmpl jmsgSmsGatewayTmpl:list){
                EhCacheUtils.put(SmsUtils.JMSGSMSGATEWAYTMPL,jmsgSmsGatewayTmpl.getTemplateId(),jmsgSmsGatewayTmpl);
            }
        }
    }
}