package com.sanerzone.common.modules.smscenter.utils;

import com.sanerzone.common.modules.phone.dao.GatewayQueueDao;
import com.sanerzone.common.modules.phone.dao.JmsgSmsGatewayTmplDao;
import com.sanerzone.common.modules.phone.dao.JmsgSmsUserTmplDao;
import com.sanerzone.common.modules.phone.entity.GatewayQueue;
import com.sanerzone.common.modules.phone.entity.JmsgSmsGatewayTmpl;
import com.sanerzone.common.modules.phone.entity.JmsgSmsUserTmpl;
import com.sanerzone.common.modules.phone.utils.SmsUtils;
import com.sanerzone.common.support.utils.EhCacheUtils;
import com.sanerzone.common.support.utils.SpringContextHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: scattereUtils
 * @description: 通道队列
 * @author: Cral
 * @create: 2019-02-11 16:30
 */
public class ScattereUtils {

    public static GatewayQueueDao gatewayQueueDao = SpringContextHolder.getBean(GatewayQueueDao.class);
    public static JmsgSmsUserTmplDao jmsgSmsUserTmplDao = SpringContextHolder.getBean(JmsgSmsUserTmplDao.class);
    public static JmsgSmsGatewayTmplDao jmsgSmsGatewayTmplDao= SpringContextHolder.getBean(JmsgSmsGatewayTmplDao.class);


    //通道队列
    public static void gatewayQueuePut(String gatewayQueueId,Object object){
        GatewayQueue gatewayQueue = (GatewayQueue)object;
        Object o = EhCacheUtils.get(SmsUtils.GATEWAYQUEUE, gatewayQueueId);
        List<GatewayQueue> gatewayQueues = (List<GatewayQueue>)o;
        boolean b = false;
        if(null != gatewayQueues && gatewayQueues.size() > 0){
            for(GatewayQueue gatewayQueue1:gatewayQueues){
                if(gatewayQueue.getId().equals(gatewayQueue1.getId())){
                    gatewayQueues.remove(gatewayQueue1);
                    gatewayQueues.add(gatewayQueue);
                    EhCacheUtils.put(SmsUtils.GATEWAYQUEUE,gatewayQueueId,gatewayQueues);
                    b = true;
                }
            }
            if(b == false){
                gatewayQueues.add(gatewayQueue);
                EhCacheUtils.put(SmsUtils.GATEWAYQUEUE,gatewayQueueId,gatewayQueues);
            }
        }else{
            gatewayQueues = new ArrayList<GatewayQueue>();
            gatewayQueues.add(gatewayQueue);
            EhCacheUtils.put(SmsUtils.GATEWAYQUEUE,gatewayQueueId,gatewayQueues);
        }
    }

    public static void gatewayQueueDel(String gatewayQueueId,String id){
        Object o = EhCacheUtils.get(SmsUtils.GATEWAYQUEUE, gatewayQueueId);
        boolean b = false;
        if(null != o) {
            List<GatewayQueue> gatewayQueues = (List<GatewayQueue>) o;
            if(null !=gatewayQueues && gatewayQueues.size() > 0){
                for(GatewayQueue gatewayQueue:gatewayQueues){
                    if(gatewayQueue.getId().equals(id)) {
                        gatewayQueues.remove(gatewayQueue);
                        EhCacheUtils.put(SmsUtils.GATEWAYQUEUE,gatewayQueueId,gatewayQueues);
                        b = true;
                    }
                }
                if(b == false){
                    EhCacheUtils.remove(SmsUtils.GATEWAYQUEUE,gatewayQueueId);
                }

            }
        }
    }


    //用户通道模板管理
    public static void jmsgSmsUserTmplPut(String userId,Object obj){
        JmsgSmsUserTmpl jmsgSmsUserTmpl = (JmsgSmsUserTmpl) obj;
        List<JmsgSmsUserTmpl> jmsgSmsUserTmpls = (List<JmsgSmsUserTmpl>)EhCacheUtils.get(SmsUtils.JMSGSMSUSERTMPL,userId);
        boolean b = false;
        if(null != jmsgSmsUserTmpls && jmsgSmsUserTmpls.size() > 0){
            for(JmsgSmsUserTmpl j:jmsgSmsUserTmpls){
                if(j.getUserId().equals(userId)){
                    jmsgSmsUserTmpls.remove(j);
                    jmsgSmsUserTmpls.add(jmsgSmsUserTmpl);
                    EhCacheUtils.put(SmsUtils.JMSGSMSUSERTMPL,userId,jmsgSmsUserTmpls);
                    b = true;
                }
            }
            if(b == false){
                jmsgSmsUserTmpls.add(jmsgSmsUserTmpl);
                EhCacheUtils.put(SmsUtils.JMSGSMSUSERTMPL,userId,jmsgSmsUserTmpls);
            }
        }else{
            jmsgSmsUserTmpls = new ArrayList<JmsgSmsUserTmpl>();
            jmsgSmsUserTmpls.add(jmsgSmsUserTmpl);
            EhCacheUtils.put(SmsUtils.JMSGSMSUSERTMPL,userId,jmsgSmsUserTmpls);
        }
    }

    public static void jmsgSmsUserTmplDel(String userId,String id){
        Object o = EhCacheUtils.get(SmsUtils.JMSGSMSUSERTMPL, userId);
        boolean b = false;
        if(null != o){
            List<JmsgSmsUserTmpl> jmsgSmsUserTmpls = (List<JmsgSmsUserTmpl>)o;
            for(JmsgSmsUserTmpl j:jmsgSmsUserTmpls){
                if(j.getUserId().equals(userId)){
                    jmsgSmsUserTmpls.remove(j);
                    EhCacheUtils.put(SmsUtils.JMSGSMSUSERTMPL,userId,jmsgSmsUserTmpls);
                    b = true;
                }
            }
            if(b == false){
                EhCacheUtils.remove(SmsUtils.JMSGSMSUSERTMPL,userId);
            }
        }
    }


    //通道模板管理
    public static void jmsgSmsGatewayTmplPut(String templateId,Object obj){
        Object o = EhCacheUtils.get(SmsUtils.JMSGSMSGATEWAYTMPL, templateId);
        JmsgSmsGatewayTmpl jmsgSmsGatewayTmpl = (JmsgSmsGatewayTmpl) obj;
        EhCacheUtils.put(SmsUtils.JMSGSMSGATEWAYTMPL,templateId,jmsgSmsGatewayTmpl);

    }

    public static void jmsgSmsGatewayTmplDel(String templateId,String id){
        EhCacheUtils.remove(SmsUtils.JMSGSMSGATEWAYTMPL,templateId);
    }



}
