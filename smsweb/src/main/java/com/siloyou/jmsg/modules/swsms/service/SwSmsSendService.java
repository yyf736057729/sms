package com.siloyou.jmsg.modules.swsms.service;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;
import com.siloyou.core.common.config.Global;
import com.siloyou.core.common.utils.FstObjectSerializeUtil;
import com.siloyou.jmsg.common.message.SmsMtMessage;
import com.siloyou.jmsg.common.utils.MQUtils;
import com.siloyou.jmsg.modules.sms.entity.JmsgSmsSend;

@Service
public class SwSmsSendService {
	Logger logger = LoggerFactory.getLogger(SwSmsSendService.class);

	@Autowired
	private MQUtils mQUtils;

	public String sendSms(String group, String gateway, String bizid, JmsgSmsSend msg) throws Exception {
		SmsMtMessage mtMsg = new SmsMtMessage();
		mtMsg.setId(msg.getId());
		mtMsg.setTaskid(msg.getTaskId());
		mtMsg.setUserid(msg.getUser().getId());
		mtMsg.setGateWayID(msg.getChannelCode());
		mtMsg.setPayType(msg.getPayType());
		mtMsg.setCstmOrderID(msg.getCustomerOrderId());
		mtMsg.setUserReportNotify(msg.getPushFlag());
		mtMsg.setUserReportGateWayID(msg.getReportGatewayId());
		mtMsg.setMsgContent(msg.getSmsContent());
		mtMsg.setPhone(msg.getPhone());
		mtMsg.setSpNumber(msg.getSpNumber());
		mtMsg.setSmsType("sms");

		return mQUtils.sendSmsMT(group, gateway, bizid, FstObjectSerializeUtil.write(mtMsg));
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
	 * @throws Exception 
	 */
	public Map<String, String> sendSms(JmsgSmsSend entity) throws Exception {

		Map<String, String> map = Maps.newHashMap();

		// 发送号码
		String msgid = "-1";
		try {
				msgid = sendSms("SMSMT" + Global.getConfig("product.gateway.port") + "B", entity.getChannelCode()+"_LOW", entity.getId(), entity);
		} catch (Exception e) {

		}
		String sendStatus = "T000";
		if ("-1".equals(msgid)) {
			sendStatus = "F0074";
		}

		map.put("sendStatus", sendStatus);
		map.put("msgid", msgid);
		return map;
	}
}
