package com.siloyou.jmsg.modules.sms.service;

import com.sanerzone.common.support.dubbo.spring.annotation.DubboReference;
import com.sanerzone.smscenter.config.SmsConfigInterface;
import com.siloyou.core.common.persistence.Page;
import com.siloyou.core.common.service.CrudService;
import com.siloyou.jmsg.modules.sms.dao.JmsgSmsGatewayTmplDao;
import com.siloyou.jmsg.modules.sms.dao.JmsgSmsUserTmplDao;
import com.siloyou.jmsg.modules.sms.entity.JmsgSmsGatewayTmpl;
import com.siloyou.jmsg.modules.sms.entity.JmsgSmsUserTmpl;
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
public class JmsgSmsUserTmplService extends CrudService<JmsgSmsUserTmplDao, JmsgSmsUserTmpl> {
    @DubboReference
    private SmsConfigInterface smsConfig;

    @Transactional(readOnly = false)
    public List<JmsgSmsUserTmpl> findList(JmsgSmsUserTmpl jmsgSmsUserTmpl) {
        return super.findList(jmsgSmsUserTmpl);
    }
    @Transactional(readOnly = false)
    public Page<JmsgSmsUserTmpl> findPage(Page<JmsgSmsUserTmpl> page, JmsgSmsUserTmpl jmsgSmsUserTmpl) {
        return super.findPage(page, jmsgSmsUserTmpl);
    }

    @Transactional(readOnly = false)
    public void delete(JmsgSmsUserTmpl jmsgSmsUserTmpl) {
        super.delete(jmsgSmsUserTmpl);
        smsConfig.configUserTmpl(2,jmsgSmsUserTmpl.getUserId(),jmsgSmsUserTmpl);
    }

    @Transactional(readOnly = false)
    public void save(JmsgSmsUserTmpl jmsgSmsUserTmpl) {
        dao.insert(jmsgSmsUserTmpl);
        smsConfig.configUserTmpl(1,jmsgSmsUserTmpl.getUserId(),jmsgSmsUserTmpl);
    }

    @Transactional(readOnly = false)
    public void update(JmsgSmsUserTmpl jmsgSmsUserTmpl) {
        dao.update(jmsgSmsUserTmpl);
        smsConfig.configUserTmpl(1,jmsgSmsUserTmpl.getUserId(),jmsgSmsUserTmpl);
    }

    @Transactional(readOnly = false)
    public  JmsgSmsUserTmpl selectByUseridTemp(JmsgSmsUserTmpl jmsgSmsUserTmpl){
       return dao.selectByUseridTemp(jmsgSmsUserTmpl);
    }

}
