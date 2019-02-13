package com.sanerzone.common.modules.smscenter.utils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aliyun.openservices.shade.com.alibaba.fastjson.JSON;
import com.sanerzone.common.modules.phone.dao.GatewayQueueDao;
import com.sanerzone.common.modules.phone.entity.GatewayQueue;
import com.sanerzone.common.modules.phone.entity.JmsgSmsGatewayTmpl;
import com.sanerzone.common.modules.phone.entity.JmsgSmsUserTmpl;
import com.sanerzone.common.modules.smscenter.entity.*;
import com.sanerzone.common.modules.smscenter.service.TestGatewayService;
import com.sanerzone.common.support.dubbo.spring.annotation.DubboService;
import com.sanerzone.common.support.utils.EhCacheUtils;
import com.sanerzone.common.support.utils.Encodes;
import com.sanerzone.common.support.utils.SpringContextHolder;
import com.sanerzone.common.support.utils.StringUtils;
import com.sanerzone.smscenter.config.SmsConfigInterface;
//import com.sanerzone.common.modules.smscenter.utils.sms.GatewayUtils;

@DubboService(interfaceClass = SmsConfigInterface.class, cluster = "broadcast")
public class SmsConfigUtils implements SmsConfigInterface {


    @Override
    public boolean configGroup(int type, Object object, String groupId) {
        switch (type) {
            case 1:
                GroupUtils.put((JmsgGroup) object);
                break;
            case 2:
                GroupUtils.del(groupId);
                break;
            default:
                break;
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean configGatewayGroup(int type, Object object, String key, String gatewayId) {
        switch (type) {
            case 1:
                GatewayGroupUtils.put((List<JmsgGatewayGroup>) object);
                break;
            case 2:
                GatewayGroupUtils.del(key, gatewayId);
                break;
            default:
                break;
        }
        return false;
    }

    @Override
    public boolean configGateway(int type, String gatewayId, Object object) {
        switch (type) {
            case 1:
			GatewayUtils.put(gatewayId, object);
//                JmsgGatewayInfo jmsgGatewayInfo = (JmsgGatewayInfo) object;
//                try {
//                    if (StringUtils.isNotBlank(jmsgGatewayInfo.getExtParam())) {
//                        String jsonParams = Encodes.unescapeHtml(jmsgGatewayInfo.getExtParam());
//                        Map<String, String> paraMap = JSON.parseObject(jsonParams, Map.class);
//                        jmsgGatewayInfo.setParams(paraMap);
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                String key = CacheKeys.getCacheGatewayInfoKey(gatewayId);
//                EhCacheUtils.put(CacheKeys.GATEWAY_CACHE, key, jmsgGatewayInfo);
                break;
            case 2:
                GatewayUtils.del(gatewayId);
//                String key1 = CacheKeys.getCacheGatewayInfoKey(gatewayId);
//                EhCacheUtils.remove(CacheKeys.GATEWAY_CACHE, key1);
                break;
            default:
                break;
        }
        return false;
    }

    @Override
    public boolean configKeyWords(int type, String value) {
        switch (type) {
            case 1:
                KeywordsUtils.put(value);
                break;
            case 2:
                KeywordsUtils.del(value);
                break;
            default:
                break;
        }
        return false;
    }

    @Override
    public boolean configSign(int type, Object objcet) {
        switch (type) {
            case 1:
                SignUtils.put((JmsgGatewaySign) objcet);
                break;
            case 2:
                SignUtils.del((JmsgGatewaySign) objcet);
                break;
            default:
                break;
        }
        return false;
    }

    @Override
    public boolean testGateway(String userId, String phone, String smsContent) {
        TestGatewayService testGatewayService = new TestGatewayService();
        testGatewayService.testGateway(userId, phone, smsContent);
        return false;
    }

    @Override
    public boolean configRule(int type, Object object) {
        switch (type) {
            case 1:
                RuleUtils.put((JmsgRuleInfo) object);
                break;
            case 2:
                RuleUtils.del((JmsgRuleInfo) object);
                break;
            default:
                break;
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean configRuleGroup(int type, String groupId, Object object) {
        switch (type) {
            case 1:
                RuleUtils.put(groupId, (List<JmsgRuleRelation>) object);
                break;
            case 2:
                RuleUtils.del((JmsgRuleRelation) object);
                break;
            default:
                break;
        }
        return false;
    }

    /**
     * dubbo 内容控制策略库
     * @param type
     * @param gatewayQueueId
     * @param object
     * @return
     */
    public boolean configGatewayQueue(int type, String gatewayQueueId, Object object) {
        GatewayQueue gatewayQueue = conversionTypeGatewayQueue(object);
        switch (type) {
            case 1:
                ScattereUtils.gatewayQueuePut(gatewayQueue.getGateWayId(), gatewayQueue);
                break;
            case 2:
                ScattereUtils.gatewayQueueDel(gatewayQueue.getGateWayId(),gatewayQueue.getId());
                break;
            default:
                break;
        }
        return false;
    }

    /**
     * dubbo 用户模板管理
     * @param type
     * @param userId
     * @param object
     * @return
     */
    public boolean configUserTmpl(int type, String userId, Object object) {
        JmsgSmsUserTmpl jmsgSmsUserTmpl = conversionTypeUserTmpl(object);
        switch (type) {
            case 1:
                ScattereUtils.jmsgSmsUserTmplPut(jmsgSmsUserTmpl.getUserId(), jmsgSmsUserTmpl);
                break;
            case 2:
                ScattereUtils.jmsgSmsUserTmplDel(jmsgSmsUserTmpl.getUserId(),jmsgSmsUserTmpl.getId());
                break;
            default:
                break;
        }
        return false;
    }

    /**
     * dubbo 通道模板管理
     * @param type
     * @param templateId
     * @param object
     * @return
     */
    public boolean configSmsGatewayTmpl(int type, String templateId, Object object) {
        JmsgSmsGatewayTmpl jmsgSmsGatewayTmpl = conversionTypeSmsGatewayTmpl(object);
        switch (type) {
            case 1:
                ScattereUtils.jmsgSmsGatewayTmplPut(jmsgSmsGatewayTmpl.getTemplateId(), jmsgSmsGatewayTmpl);
                break;
            case 2:
                ScattereUtils.jmsgSmsGatewayTmplDel(jmsgSmsGatewayTmpl.getTemplateId(),jmsgSmsGatewayTmpl.getId());
                break;
            default:
                break;
        }
        return false;
    }

    /**
     * 类型转换 JmsgSmsGatewayTmpl
     * @param object
     */
    public JmsgSmsGatewayTmpl conversionTypeSmsGatewayTmpl(Object object){
        Map map = (HashMap)object;
        JmsgSmsGatewayTmpl jmsgSmsGatewayTmpl = new JmsgSmsGatewayTmpl();
        jmsgSmsGatewayTmpl.setUpdateDate((Date) map.get("updateDate"));
        jmsgSmsGatewayTmpl.setGatewayName((String) map.get("gatewayName"));
        jmsgSmsGatewayTmpl.setJoinNumber((String) map.get("joinNumber"));
        jmsgSmsGatewayTmpl.setSqlMap((Map<String, String>) map.get("sqlMap"));
        jmsgSmsGatewayTmpl.setIsNewRecord((Boolean) map.get("isNewRecord"));
        jmsgSmsGatewayTmpl.setTemplateId((String) map.get("templateId"));
        jmsgSmsGatewayTmpl.setDelFlag((String) map.get("delFlag"));
        jmsgSmsGatewayTmpl.setCreateBy((String) map.get("createBy"));
        jmsgSmsGatewayTmpl.setTemplateName((String) map.get("templateName"));
        jmsgSmsGatewayTmpl.setCreateTime((Date) map.get("createTime"));
        jmsgSmsGatewayTmpl.setDateTimeZ((Date) map.get("dateTimeZ"));
        jmsgSmsGatewayTmpl.setUpdateBy((String) map.get("updateBy"));
        jmsgSmsGatewayTmpl.setTemplateContent((String) map.get("templateContent"));
        jmsgSmsGatewayTmpl.setId((String) map.get("id"));
        jmsgSmsGatewayTmpl.setGatewayId((String) map.get("gatewayId"));
        jmsgSmsGatewayTmpl.setRemarks((String) map.get("remarks"));
        return jmsgSmsGatewayTmpl;
    }

    /**
     * 类型转换 GatewayQueue
     * @param object
     * @return
     */
    public GatewayQueue conversionTypeGatewayQueue(Object object){
        Map map = (HashMap)object;
        GatewayQueue gatewayQueue = new GatewayQueue();
        gatewayQueue.setUpdateDate((Date) map.get("updateDate"));
        Object o = map.get("weight");
        int weight = 0;
        if(null != o){
            weight = Integer.parseInt((String)o);
        }
        gatewayQueue.setWeight(weight);
        gatewayQueue.setIsNewRecord((Boolean) map.get("isNewRecord"));
        gatewayQueue.setDelFalg((String) map.get("delFlag"));
        gatewayQueue.setCreateBy((String) map.get("createBy"));
        gatewayQueue.setQueueName((String) map.get("queueName"));
        gatewayQueue.setCreateTime((Date) map.get("createTime"));
        gatewayQueue.setUpdateBy((String) map.get("updateBy"));
        gatewayQueue.setId((String) map.get("id"));
        gatewayQueue.setBusinessType((String) map.get("businessType"));
        gatewayQueue.setGateWayId((String) map.get("gatewayId"));
        gatewayQueue.setRemarks((String) map.get("remarks"));
        gatewayQueue.setStatus((String) map.get("status"));
        gatewayQueue.setCreateDate((Date) map.get("createDate"));
        return gatewayQueue;
    }

    /**
     * 类型转换 JmsgSmsUserTmpl
     * @param object
     * @return
     */
    public JmsgSmsUserTmpl conversionTypeUserTmpl(Object object){
        Map map = (HashMap)object;
        JmsgSmsUserTmpl jmsgSmsUserTmpl = new JmsgSmsUserTmpl();
        jmsgSmsUserTmpl.setJmsgSmsUserTmplList((List<JmsgSmsUserTmpl>) map.get("jmsgSmsUserTmplList"));
        jmsgSmsUserTmpl.setUpdateDate((Date) map.get("updateDate"));
        jmsgSmsUserTmpl.setJoinNumber((String) map.get("joinNumber"));
        jmsgSmsUserTmpl.setSqlMap((Map<String, String>) map.get("sqlMap"));
        jmsgSmsUserTmpl.setIsNewRecord((Boolean) map.get("isNewRecord"));
        jmsgSmsUserTmpl.setTemplateId((String) map.get("templateId"));
        jmsgSmsUserTmpl.setUserName((String) map.get("userName"));
        jmsgSmsUserTmpl.setDelFlag((String) map.get("delFlag"));
        jmsgSmsUserTmpl.setUserId((String) map.get("userId"));
        jmsgSmsUserTmpl.setTemplateName((String) map.get("templateName"));
        jmsgSmsUserTmpl.setCreateTime((Date) map.get("createTime"));
        jmsgSmsUserTmpl.setDateTimeZ((Date) map.get("dateTimeZ"));
        jmsgSmsUserTmpl.setUpdateBy((String) map.get("updateBy"));
        jmsgSmsUserTmpl.setId((String) map.get("id"));
        jmsgSmsUserTmpl.setDateTimeQ((Date) map.get("dateTimeQ"));
        jmsgSmsUserTmpl.setRemarks((String) map.get("remarks"));
        jmsgSmsUserTmpl.setCreateDate((Date) map.get("createDate"));
        return jmsgSmsUserTmpl;
    }

}
