package com.siloyou.jmsg.gateway.cmpp.message;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.siloyou.jmsg.common.message.SmsMoMessage;
import com.siloyou.jmsg.common.message.SmsMtMessage;
import com.siloyou.jmsg.common.message.SmsRtMessage;
import com.siloyou.jmsg.common.message.SmsSrMessage;
import com.siloyou.jmsg.common.util.CommonUtils;
import com.siloyou.jmsg.gateway.api.GateWayMessageAbstract;
import com.siloyou.jmsg.gateway.api.JmsgGateWayInfo;
import com.zx.sms.codec.cmpp.msg.CmppDeliverRequestMessage;
import com.zx.sms.codec.cmpp.msg.CmppSubmitRequestMessage;
import com.zx.sms.codec.cmpp.msg.CmppSubmitResponseMessage;
import com.zx.sms.common.util.MsgId;

public class CmppGateWayMessage extends GateWayMessageAbstract {

	public static Map<String, JmsgGateWayInfo> map = new HashMap<String, JmsgGateWayInfo>();

	@Override
	protected SmsSrMessage convertSRMessage(Serializable smsSrMessage) {
		SmsSrMessage message = null;

		if (smsSrMessage instanceof CmppSubmitResponseMessage) {
			SmsMtMessage smsMtMessage = new SmsMtMessage();
			CmppSubmitResponseMessage e = (CmppSubmitResponseMessage) smsSrMessage;

			if (e.getAttachment() != null) {
				smsMtMessage = (SmsMtMessage) e.getAttachment();
			}

			String resultCode = null;
			if (e.getResult() == 0) {
				resultCode = String.valueOf(e.getResult());
			} else {
				resultCode = String.valueOf("F10110" + e.getResult());
			}

			message = new SmsSrMessage(e.getMsgId().toString(), resultCode, smsMtMessage);
		} else if (smsSrMessage instanceof SmsSrMessage) {
			message = (SmsSrMessage) smsSrMessage;
		}

		return message;
	}

	@Override
	protected SmsRtMessage convertRTMessage(Serializable smsRtMessage) {
		SmsRtMessage message = new SmsRtMessage();
		CmppDeliverRequestMessage e = (CmppDeliverRequestMessage) smsRtMessage;
		message.setDestTermID(CommonUtils.resetPhone(e.getReportRequestMessage().getDestterminalId()));
		message.setSrcTermID(e.getDestId());
		message.setDoneTime(e.getReportRequestMessage().getDoneTime());
		message.setSubmitTime(e.getReportRequestMessage().getSubmitTime());
		message.setMsgid(e.getReportRequestMessage().getMsgId().toString());
		message.setSmscSequence(String.valueOf(e.getReportRequestMessage().getSmscSequence()));
		if (e.getAttachment() != null) {
			SmsMtMessage mtMsg = (SmsMtMessage) e.getAttachment();
			message.setSmsMt(mtMsg);
			if (StringUtils.isBlank(message.getSrcTermID())) {
				message.setSrcTermID(CommonUtils.resetPhone(mtMsg.getSpNumber()));
			}

		}
		message.setStat(e.getReportRequestMessage().getStat());

//		JmsgGateWayInfo gateWay = map.get(message.getGateWayID());
//		if (null != gateWay) {
//			if (!gateWay.isWholeSpNumber() && !message.getSrcTermID().startsWith(gateWay.getSpNumber())) {
//				message.setDestTermID(gateWay.getSpNumber() + message.getSrcTermID());
//			}
//		}

		return message;
	}

	@Override
	protected SmsMoMessage convertMOMessage(Serializable smsRoMessage) {
		SmsMoMessage message = new SmsMoMessage();
		CmppDeliverRequestMessage e = (CmppDeliverRequestMessage) smsRoMessage;
		message.setDestTermID(CommonUtils.resetPhone(e.getSrcterminalId()));
		message.setSrcTermID(CommonUtils.resetPhone(e.getDestId()));
		message.setMsgid(e.getMsgId().toString());
		message.setMsgContent(e.getMsgContent());

//		JmsgGateWayInfo gateWay = map.get(message.getGateWayId());
//		if (null != gateWay) {
//			if (!gateWay.isWholeSpNumber() && !message.getSrcTermID().startsWith(gateWay.getSpNumber())) {
//				message.setDestTermID(gateWay.getSpNumber() + message.getSrcTermID());
//			}
//		}
		return message;
	}

	@Override
	public Serializable convertMTMessage(SmsMtMessage message, boolean gatewaySign) {
		CmppSubmitRequestMessage msg = new CmppSubmitRequestMessage();
		msg.setAttachment(message);
		msg.setDestterminalId(message.getPhone());
		msg.setMsgid(new MsgId());

		JmsgGateWayInfo gateWay = map.get(message.getGateWayID());
		if (null != gateWay) {
			System.out.println(gateWay.getServiceId());
			if (StringUtils.isNotBlank(gateWay.getServiceId())&&null!=gateWay.getServiceId()) {
				msg.setServiceId(gateWay.getServiceId());
			}
			
			if(null != gateWay.getParam()&&gateWay.getParam().size()>0) {
				//设置feeUserType
				msg.setFeeUserType((short) CommonUtils.getNumeriValueFromMap(gateWay.getParam(), "feeUserType", 2));
				msg.setFeeCode(CommonUtils.getStringValueFromMap(gateWay.getParam(), "feeCode", "01"));
				msg.setFeeType(CommonUtils.getStringValueFromMap(gateWay.getParam(), "feeType", "000000"));
			}
			
		}

		if(StringUtils.equals(msg.getServiceId(), "cmcczx_sms")) {
			if (StringUtils.isNotBlank(message.getServiceId())) {
				msg.setServiceId(message.getServiceId());
			} else {
				msg.setServiceId(message.getSpNumber());
			}
		}
		msg.setSrcId(message.getSpNumber());
		msg.setRegisteredDelivery((short) 1);
		msg.setMsgContent(message.getMsgContent());
		String msgContent = msg.getMsgContent();
		if (gatewaySign && StringUtils.isNotBlank(msgContent)) {
			int startIdx = 0;
			int endIdx = 0;
			if(msgContent.startsWith("【")) {
				startIdx = 1;
				endIdx = msgContent.indexOf("】");
			}
			
			// 签名要大于等于2且小于等于10 (暂时不考虑签名逻辑 qtang)
//			if ( endIdx - startIdx > 0 ) {
//				String sign = msgContent.substring(startIdx - 1, endIdx + 1);
//				int signLen = sign.getBytes(CMPPCommonUtil.switchCharset(SmsAlphabet.UCS2)).length;
//				msg.setSignLen(signLen);
//			}
		}
		return msg;
	}

}
