package com.siloyou.jmsg.modules.sms.service;

import com.sanerzone.common.support.dubbo.spring.annotation.DubboReference;
import com.sanerzone.smscenter.config.SmsConfigInterface;
import com.siloyou.core.common.persistence.Page;
import com.siloyou.core.common.service.CrudService;
import com.siloyou.jmsg.modules.sms.dao.JmsgGatewayQueueDao;
import com.siloyou.jmsg.modules.sms.dao.JmsgSmsGatewayTmplDao;
import com.siloyou.jmsg.modules.sms.entity.JmsgGatewayQueue;
import com.siloyou.jmsg.modules.sms.entity.JmsgSmsGatewayTmpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author yuyunfeng
 * @create_time 2019/1/9
 * @describe ${通道模板管理}
 */
@Service
@Transactional(readOnly = true)
public class JmsgSmsGatewayTmplService extends CrudService<JmsgSmsGatewayTmplDao, JmsgSmsGatewayTmpl> {
    @DubboReference
    private SmsConfigInterface smsConfig;

    @Transactional(readOnly = false)
    public List<JmsgSmsGatewayTmpl> findList(JmsgSmsGatewayTmpl jmsgSmsGatewayTmpl) {
        return super.findList(jmsgSmsGatewayTmpl);
    }
    @Transactional(readOnly = false)
    public Page<JmsgSmsGatewayTmpl> findPage(Page<JmsgSmsGatewayTmpl> page, JmsgSmsGatewayTmpl jmsgSmsGatewayTmpl) {
        return super.findPage(page, jmsgSmsGatewayTmpl);
    }

    @Transactional(readOnly = false)
    public void delete(JmsgSmsGatewayTmpl jmsgSmsGatewayTmpl) {
        super.delete(jmsgSmsGatewayTmpl);
        smsConfig.configSmsGatewayTmpl(2,jmsgSmsGatewayTmpl.getTemplateId(),jmsgSmsGatewayTmpl);
    }

    @Transactional(readOnly = false)
    public void save(JmsgSmsGatewayTmpl jmsgSmsGatewayTmpl) {
        super.delete(jmsgSmsGatewayTmpl);
        dao.insert(jmsgSmsGatewayTmpl);
        smsConfig.configSmsGatewayTmpl(1,jmsgSmsGatewayTmpl.getTemplateId(),jmsgSmsGatewayTmpl);
    }

    @Transactional(readOnly = false)
    public void update(JmsgSmsGatewayTmpl jmsgSmsGatewayTmpl) {
        dao.update(jmsgSmsGatewayTmpl);
        smsConfig.configSmsGatewayTmpl(1,jmsgSmsGatewayTmpl.getTemplateId(),jmsgSmsGatewayTmpl);
    }

    @Transactional(readOnly = false)
    public JmsgSmsGatewayTmpl searchByGatewayName(JmsgSmsGatewayTmpl jmsgSmsGatewayTmpl) {
       return dao.searchByGatewayName(jmsgSmsGatewayTmpl);
    }



}
