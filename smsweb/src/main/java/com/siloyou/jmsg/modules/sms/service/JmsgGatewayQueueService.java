package com.siloyou.jmsg.modules.sms.service;

import com.sanerzone.common.support.dubbo.spring.annotation.DubboReference;
import com.sanerzone.smscenter.config.SmsConfigInterface;
import com.siloyou.core.common.persistence.Page;
import com.siloyou.core.common.service.CrudService;
import com.siloyou.jmsg.common.utils.GatewayUtils;
import com.siloyou.jmsg.modules.sms.dao.JmsgGatewayQueueDao;
import com.siloyou.jmsg.modules.sms.entity.JmsgGatewayInfo;
import com.siloyou.jmsg.modules.sms.entity.JmsgGatewayQueue;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author yuyunfeng
 * @create_time 2019/1/9
 * @describe ${通道队列}
 */
@Service
@Transactional(readOnly = true)
public class JmsgGatewayQueueService extends CrudService<JmsgGatewayQueueDao, JmsgGatewayQueue> {

    @DubboReference
    private SmsConfigInterface smsConfig;
    @Transactional(readOnly = false)
    public List<JmsgGatewayQueue> findList(JmsgGatewayQueue jmsgGatewayQueue) {
        return super.findList(jmsgGatewayQueue);
    }
    @Transactional(readOnly = false)
    public Page<JmsgGatewayQueue> findPage(Page<JmsgGatewayQueue> page, JmsgGatewayQueue jmsgGatewayQueue) {
        return super.findPage(page, jmsgGatewayQueue);
    }

    @Transactional(readOnly = false)
    public void delete(JmsgGatewayQueue jmsgGatewayQueue) {
        super.delete(jmsgGatewayQueue);
        //删除到缓存
        smsConfig.configGatewayQueue(2,jmsgGatewayQueue.getGatewayId(),jmsgGatewayQueue);
    }

    @Transactional(readOnly = false)
    public void save(JmsgGatewayQueue jmsgGatewayQueue) {
        dao.insert(jmsgGatewayQueue);
        //保存到缓存
        smsConfig.configGatewayQueue(1,"",jmsgGatewayQueue);
    }

    @Transactional(readOnly = false)
    public void update(JmsgGatewayQueue jmsgGatewayQueue) {
        dao.update(jmsgGatewayQueue);
        //修改到缓存
        smsConfig.configGatewayQueue(1,"",jmsgGatewayQueue);
    }

    @Transactional(readOnly = false)
    public JmsgGatewayQueue searchByGatewayName(JmsgGatewayQueue jmsgGatewayQueue) {
       return dao.searchByGatewayName(jmsgGatewayQueue);
    }

    @Transactional(readOnly = false)
    public int searchLikeByGateway(JmsgGatewayQueue jmsgGatewayQueue) {
        return dao.searchLikeByGateway(jmsgGatewayQueue);
    }
}
