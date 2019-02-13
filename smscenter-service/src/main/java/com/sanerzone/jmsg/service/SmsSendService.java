package com.sanerzone.jmsg.service;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.druid.util.StringUtils;
import com.google.common.collect.Maps;
import com.sanerzone.common.modules.account.utils.AccountCacheUtils;
import com.sanerzone.common.modules.phone.utils.WhitelistUtils;
import com.sanerzone.common.modules.smscenter.utils.GatewayUtils;
import com.sanerzone.common.modules.smscenter.utils.JmsgCacheKeys;
import com.sanerzone.common.support.utils.DateUtils;
import com.sanerzone.common.support.utils.FstObjectSerializeUtil;
import com.sanerzone.common.support.utils.JedisClusterUtils;
import com.sanerzone.jmsg.entity.JmsgSmsSend;
import com.sanerzone.smscenter.utils.MQUtils;
import com.siloyou.jmsg.common.message.SmsMtMessage;

@Service
public class SmsSendService {
	Logger logger = LoggerFactory.getLogger(SmsSendService.class);

	@Autowired
	private MQUtils mQUtils;

	public String sendSms(String group, String gateway, String bizid, JmsgSmsSend msg) throws Exception {
		SmsMtMessage mtMsg = new SmsMtMessage();
		mtMsg.setId(msg.getId());
		mtMsg.setTaskid(msg.getTaskId());
		mtMsg.setUserid(msg.getUserId());
		mtMsg.setGateWayID(msg.getChannelCode());
		mtMsg.setPayType(msg.getPayType());
		mtMsg.setCstmOrderID(msg.getCustomerOrderId());
		mtMsg.setUserReportNotify(msg.getPushFlag());
		mtMsg.setUserReportGateWayID(msg.getReportGatewayId());
		mtMsg.setMsgContent(msg.getSmsContent());
		mtMsg.setPhone(msg.getPhone());
		mtMsg.setSpNumber(msg.getSpNumber());
		mtMsg.setSmsType("sms");
		
		String appCode = GatewayUtils.getAppCode(gateway);// 应用代码

		return mQUtils.sendSmsMT(group, appCode, bizid, FstObjectSerializeUtil.write(mtMsg));
	}
	
	public String sendSms(String gateway, String bizid, SmsMtMessage msg) throws Exception {
        
        String appCode = GatewayUtils.getAppCode(gateway);// 应用代码

        return mQUtils.sendSmsMT(JmsgCacheKeys.getSmsSingleTopic(), appCode, bizid, FstObjectSerializeUtil.write(msg));
    }

	public Map<String, String> sendHandler(JmsgSmsSend entity) {

		String id = entity.getId();
		String phone = entity.getPhone();

		Map<String, String> map = Maps.newHashMap();
		String sendStatus = "F000";
		String msgid = "";
		map.put("id", id);
		
		//String limit = DictUtils.getDictValue(entity.getUserId(), "user_month_limit",  "-1");
		String limit = AccountCacheUtils.getStringValue(entity.getUserId(), "userMonthLimit", "-1");
		try {
			if (WhitelistUtils.isExist(phone) || StringUtils.equals(limit, "0")) {// 判断是否是白名单号码
				Map<String, String> sendMap = sendSms(entity);
				sendStatus = sendMap.get("sendStatus");
				msgid = sendMap.get("msgid");
			} else {
				String dayKey = JmsgCacheKeys.getCacheDaySmsSendKey(phone);
				String monthKey = JmsgCacheKeys.getCacheMonthSmsSendKey(phone);
				long dayKeyValue = JedisClusterUtils.incr(dayKey, DateUtils.getEndDayTime());// 一天内发送次数+1
				long monthKeyValue = JedisClusterUtils.incr(monthKey, DateUtils.getEndMonthTime());// 一月内发送次数+1
				if (dayKeyValue > 50) {// 判断号码是否一天内发送50次以上 F3
					sendStatus = "F003";
				} else {
					if (monthKeyValue > 300) {// 判断号码是否一个月内发送300次以上 F4
						sendStatus = "F004";
					} else {
						Map<String, String> sendMap = sendSms(entity);
						sendStatus = sendMap.get("sendStatus");
						msgid = sendMap.get("msgid");
					}
				}
			}
			
		} catch (Exception e) {
			logger.error("{}", e);
		}
		map.put("sendStatus", sendStatus);
		map.put("msgid", msgid);
		return map;
	}
	
	public Map<String, String> sendHandler(SmsMtMessage entity) {

        String id = entity.getId();
        String phone = entity.getPhone();

        Map<String, String> map = Maps.newHashMap();
        String sendStatus = "F000";
        String msgid = "";
        map.put("id", id);
        
        try {
            if (WhitelistUtils.isExist(phone)) {// 判断是否是白名单号码
                Map<String, String> sendMap = sendSms(entity);
                sendStatus = sendMap.get("sendStatus");
                msgid = sendMap.get("msgid");
            } else {
                String dayKey = JmsgCacheKeys.getCacheDaySmsSendKey(phone);
                String monthKey = JmsgCacheKeys.getCacheMonthSmsSendKey(phone);
                long dayKeyValue = JedisClusterUtils.incr(dayKey, DateUtils.getEndDayTime());// 一天内发送次数+1
                long monthKeyValue = JedisClusterUtils.incr(monthKey, DateUtils.getEndMonthTime());// 一月内发送次数+1
                if (dayKeyValue > 50) {// 判断号码是否一天内发送50次以上 F3
                    sendStatus = "F003";
                } else {
                    if (monthKeyValue > 300) {// 判断号码是否一个月内发送300次以上 F4
                        sendStatus = "F004";
                    } else {
                        Map<String, String> sendMap = sendSms(entity);
                        sendStatus = sendMap.get("sendStatus");
                        msgid = sendMap.get("msgid");
                    }
                }
            }
            
        } catch (Exception e) {
            logger.error("{}", e);
        }
        map.put("sendStatus", sendStatus);
        map.put("msgid", msgid);
        return map;
    }

	/**
	 * 提交队列
	 * 
	 * @param bizid
	 * @param phone
	 * @param smsContent
	 * @param spNumber
	 * @param group
	 * @param gateWayId
	 * @param taskid
	 * @param userId
	 * @return
	 */
	public Map<String, String> sendSms(JmsgSmsSend entity) {

		Map<String, String> map = Maps.newHashMap();

		// 发送号码
		String msgid = null;
		try {
			msgid = sendSms(entity.getTopic(), entity.getChannelCode(), entity.getId(), entity);
		} catch (Exception e) {

		}
		String sendStatus = "T000";
		if ("-1".equals(msgid)) {
			sendStatus = "P001";// 重新发送
			String againSendKey = JmsgCacheKeys.getCacheAgainSmsSendKey(entity.getPhone());
			long agaginCount = JedisClusterUtils.incr(againSendKey);
			if (agaginCount > 3) {// 重新发送3次以上 F5
				sendStatus = "F005";
				String daySendKey = JmsgCacheKeys.getCacheDaySmsSendKey(entity.getPhone());
				int dayValue = JedisClusterUtils.getInt(daySendKey);

				if (dayValue >= 2) {
					JedisClusterUtils.set(daySendKey, "1", 0);
				} else {
					JedisClusterUtils.set(daySendKey, String.valueOf((0)), 0);
				}
				JedisClusterUtils.decr(JmsgCacheKeys.getCacheMonthSmsSendKey(entity.getPhone()));

				// 销毁key
				JedisClusterUtils.del(againSendKey);
			}
		}

		map.put("sendStatus", sendStatus);
		map.put("msgid", msgid);
		return map;
	}
	
	public Map<String, String> sendSms(SmsMtMessage entity) {

        Map<String, String> map = Maps.newHashMap();

        // 发送号码
        String msgid = null;
        try {
            msgid = sendSms(entity.getGateWayID(), entity.getId(), entity);
        } catch (Exception e) {

        }
        String sendStatus = "T000";
        if ("-1".equals(msgid)) {
            sendStatus = "P001";// 重新发送
            String againSendKey = JmsgCacheKeys.getCacheAgainSmsSendKey(entity.getPhone());
            long agaginCount = JedisClusterUtils.incr(againSendKey);
            if (agaginCount > 3) {// 重新发送3次以上 F5
                sendStatus = "F005";
                String daySendKey = JmsgCacheKeys.getCacheDaySmsSendKey(entity.getPhone());
                int dayValue = JedisClusterUtils.getInt(daySendKey);

                if (dayValue >= 2) {
                    JedisClusterUtils.set(daySendKey, "1", 0);
                } else {
                    JedisClusterUtils.set(daySendKey, String.valueOf((0)), 0);
                }
                JedisClusterUtils.decr(JmsgCacheKeys.getCacheMonthSmsSendKey(entity.getPhone()));

                // 销毁key
                JedisClusterUtils.del(againSendKey);
            }
        }

        map.put("sendStatus", sendStatus);
        map.put("msgid", msgid);
        return map;
    }

}
