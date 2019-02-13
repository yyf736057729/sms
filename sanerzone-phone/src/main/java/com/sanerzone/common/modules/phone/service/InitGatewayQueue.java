package com.sanerzone.common.modules.phone.service;

import com.google.common.collect.Lists;
import com.sanerzone.common.modules.phone.dao.ContentManageDao;
import com.sanerzone.common.modules.phone.dao.GatewayQueueDao;
import com.sanerzone.common.modules.phone.entity.ContentManage;
import com.sanerzone.common.modules.phone.entity.GatewayQueue;
import com.sanerzone.common.modules.phone.utils.SmsUtils;
import com.sanerzone.common.support.utils.EhCacheUtils;
import com.sanerzone.common.support.utils.SpringContextHolder;

import java.util.*;

/**
 * @program: smscenter
 * @description: 初始化通道队列缓存
 * @author: Cral
 * @create: 2019-01-08 16:30
 */
public class InitGatewayQueue {
    private static GatewayQueueDao gatewayQueueDao = SpringContextHolder.getBean(GatewayQueueDao.class);
    //初始化内容策略
    public void initGatewayQueue(){
        List<GatewayQueue> list = gatewayQueueDao.findByGatewayId(null);
        Map<String,List<GatewayQueue>> stringListMap = new HashMap<String, List<GatewayQueue>>();
        if(list != null && list.size() >0){
            for(GatewayQueue gatewayQueue:list){
                List<GatewayQueue> gatewayQueueList = new ArrayList<GatewayQueue>();
                    if(stringListMap.containsKey(gatewayQueue.getGateWayId())){
                        stringListMap.get(gatewayQueue.getGateWayId()).add(gatewayQueue);
                    }else{
                        stringListMap.put(gatewayQueue.getGateWayId(), Lists.newArrayList(gatewayQueue));
                    }
            }
        }
        if(null != stringListMap && stringListMap.size() > 0){
            Iterator<Map.Entry<String,List<GatewayQueue>>> it = stringListMap.entrySet().iterator();
                while (it.hasNext()) {
                   Map.Entry<String,List<GatewayQueue>> entry = it.next();
                    EhCacheUtils.put(SmsUtils.GATEWAYQUEUE,entry.getKey(),entry.getValue());
             }
        }
    }
}