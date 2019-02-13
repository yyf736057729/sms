package com.sanerzone.common.modules.phone.service;

import com.google.common.collect.Lists;
import com.sanerzone.common.modules.phone.dao.GatewayQueueDao;
import com.sanerzone.common.modules.phone.dao.JmsgSmsUserTmplDao;
import com.sanerzone.common.modules.phone.entity.JmsgSmsUserTmpl;
import com.sanerzone.common.modules.phone.utils.SmsUtils;
import com.sanerzone.common.support.utils.EhCacheUtils;
import com.sanerzone.common.support.utils.SpringContextHolder;

import java.util.*;

/**
 * @program: smscenter
 * @description: 初始化用户模板
 * @author: Cral
 * @create: 2019-02-11 16:26
 */
public class InitJmsgSmsUserTmpl {
    private static JmsgSmsUserTmplDao jmsgSmsUserTmplDao = SpringContextHolder.getBean(JmsgSmsUserTmplDao.class);

    public void initJmsgSmsUserTmpl(){
        List<JmsgSmsUserTmpl> jmsgSmsUserTmpls = jmsgSmsUserTmplDao.selectByUseridTemps(null);
        Map<String,List<JmsgSmsUserTmpl>> stringListMap = new HashMap<String, List<JmsgSmsUserTmpl>>();
        List<JmsgSmsUserTmpl> list = new ArrayList<JmsgSmsUserTmpl>();
        if(null != jmsgSmsUserTmpls && jmsgSmsUserTmpls.size() > 0){
            for(JmsgSmsUserTmpl jmsgSmsUserTmpl:jmsgSmsUserTmpls){
                if(stringListMap.containsKey(jmsgSmsUserTmpl.getUserId())){
                    stringListMap.get(jmsgSmsUserTmpl.getUserId()).add(jmsgSmsUserTmpl);
                }else{
                    stringListMap.put(jmsgSmsUserTmpl.getUserId(), Lists.newArrayList(jmsgSmsUserTmpl));
                }
            }
        }
        if(null != stringListMap && stringListMap.size() > 0){
            Iterator<Map.Entry<String, List<JmsgSmsUserTmpl>>> iterator = stringListMap.entrySet().iterator();
            while (iterator.hasNext()){
                Map.Entry<String, List<JmsgSmsUserTmpl>> next = iterator.next();
                EhCacheUtils.put(SmsUtils.JMSGSMSUSERTMPL,next.getKey(),next.getValue());
            }
        }
    }
}