package com.zx.sms.session.cmpp;

import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zx.sms.codec.cmpp.msg.CmppDeliverResponseMessage;
import com.zx.sms.codec.cmpp.msg.CmppSubmitResponseMessage;
import com.zx.sms.codec.cmpp.msg.Message;
import com.zx.sms.common.storedMap.VersionObject;
import com.zx.sms.connect.manager.EndpointEntity;
import com.zx.sms.session.AbstractSessionStateManager;

/**
 * @author Lihuanghe(18852780@qq.com) 消息发送窗口拜你控制和消息重发 ，消息持久化
 */
public class SessionStateManager extends AbstractSessionStateManager<Long, Message> {
	private static final Logger logger = LoggerFactory.getLogger(SessionStateManager.class);

	public SessionStateManager(EndpointEntity entity, ConcurrentMap<Long, VersionObject<Message>> storeMap, boolean preSend) {
		super(entity, storeMap, preSend);
	}

	@Override
	protected Long getSequenceId(Message msg) {
		return msg.getHeader().getSequenceId();
	}

	@Override
	protected boolean needSendAgainByResponse(Message req, Message res) {
		if (res instanceof CmppSubmitResponseMessage) {
			CmppSubmitResponseMessage submitResp = (CmppSubmitResponseMessage) res;
			
			if ((submitResp.getResult() != 0L) && (submitResp.getResult() != 8L)) {
				logger.error("Send SubmitMsg ERR . Msg: {} ,Resp:{}", req, submitResp);
			}

			return submitResp.getResult() == 8L;
		} else if (res instanceof CmppDeliverResponseMessage) {
			CmppDeliverResponseMessage deliverResp = (CmppDeliverResponseMessage) res;

			if ((deliverResp.getResult() != 0L) && (deliverResp.getResult() != 8L)) {
				logger.error("Send DeliverMsg ERR . Msg: {} ,Resp:{}", req, deliverResp);
			}

			return deliverResp.getResult() == 8L;
		}
		return false;
	}

}
