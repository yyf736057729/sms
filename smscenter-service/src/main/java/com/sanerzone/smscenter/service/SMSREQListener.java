package com.sanerzone.smscenter.service;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.sanerzone.common.modules.account.utils.AccountCacheUtils;
import com.sanerzone.common.support.utils.FstObjectSerializeUtil;
import com.sanerzone.smscenter.entity.SMSMTMessage;
import com.sanerzone.smscenter.utils.CacheKeys;
import com.sanerzone.smscenter.utils.MQUtils;

@Service
public class SMSREQListener implements MessageListenerConcurrently {

	public static Logger logger = LoggerFactory.getLogger(SMSREQListener.class);
	
	@Autowired
	public MQUtils mQUtils;

	@Override
	public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
		for (MessageExt msg : msgs) {
			logger.info("umt listener recv message: topic:{}, tags:{}, msgid:{}, key:{}", msg.getTopic(), msg.getTags(),
					msg.getMsgId(), msg.getKeys());
			
			long startTime = System.currentTimeMillis();
			
			try {
				SMSMTMessage smsMTMessage = (SMSMTMessage)FstObjectSerializeUtil.read(msg.getBody());
				
				logger.info("CMPP模拟网关,开始处理放库发送: 任务ID:{}, 用户:{}, 手机号码：{}, 接收时间:{} ", smsMTMessage.getTaskid(), smsMTMessage.getUserid(), smsMTMessage.getPhone(), startTime);
				
				String dataId = "99";
				String sendStatus = "P000";
				String phoneType = "";
                String cityCode = "";
				String taskId = smsMTMessage.getTaskid();
				String accId = smsMTMessage.getAccId();
				//String userId = smsMTMessage.getUserid();
				String phones = smsMTMessage.getPhone();
				String smsContent = smsMTMessage.getSmsContent();
				String reportGaetewayId = smsMTMessage.getGatewayId();
				String spNumber = smsMTMessage.getSpnumber();
				String topic = CacheKeys.getSmsBatchTopic();
				
				Map<String,String> map = AccountCacheUtils.getMap(smsMTMessage.getAccId());

				String sign = "";//SignUtils.get(smsContent);
				int payCount = 1;//findPayCount(smsContent);
				String smsType = payCount > 1 ? "2" : "1";
				String payStatus = "1";
				int money = 0;
				int index = 0;
				String[] phoneList = phones.split(",");
				
				SMSMTMessage mtMsg = null;
				for (String phone : phoneList) 
				{
				    mtMsg = new SMSMTMessage();
				    
				    sendStatus = "T000";
				    
				    /*Map<String, String> phoneMap = new HashMap<String, String>();//PhoneUtils.get(phone);
				    phoneType = "";
                    cityCode = "";
                    if ((phoneMap == null) || (phoneMap.size() < 2)) {
                        sendStatus = "F0170";
                        mtMsg.setSendTime(System.currentTimeMillis());
                    } else {
                        phoneType = "YD";//PhoneUtils.getPhoneType(phoneMap);
                        cityCode = "14";//PhoneUtils.getCityCode(phoneMap);
                        
                        if ((StringUtils.isBlank(cityCode)) || (StringUtils.isBlank(phoneType))) {
                            sendStatus = "F0170";
                            mtMsg.setSendTime(System.currentTimeMillis());
                        } else {
                            GatewayResult gatewayResult = gatewayMap(map.get("signFlag"), map.get("groupId"),
                                    phoneType, cityCode.substring(0, 2), sign, map.get("userid"));
                            if (gatewayResult.isExists()) 
                            {
                                mtMsg.setGatewayId(gatewayResult.getGatewayId());
                                
                                String tdSpNumber = gatewayResult.getSpNumber();
                                if ((StringUtils.isNotBlank(spNumber)) && (spNumber.startsWith(tdSpNumber))) {
                                    tdSpNumber = spNumber;
                                }
                                
                                mtMsg.setSpnumber(tdSpNumber);
                            } else {
                                sendStatus = gatewayResult.getErrorCode();
                                mtMsg.setSendTime(System.currentTimeMillis());
                            }
                        }
                    }*/
				    
                    mtMsg.setGatewayId("CMPP");
                    mtMsg.setSpnumber("106123123");
                    
				    mtMsg.setAccId(accId);
				    mtMsg.setUserid(map.get("userid"));
				    mtMsg.setFeeType(map.get("feeType"));
				    mtMsg.setFeePayment(map.get("feePayment"));
				    mtMsg.setTaskid(taskId);
				    mtMsg.setCustomTaskid(smsMTMessage.getCustomTaskid());
				    mtMsg.setCustomServiceid(smsMTMessage.getCustomServiceid());
				    mtMsg.setPhone(phone);
				    mtMsg.setPhoneType(phoneType);
				    mtMsg.setPhoneArea(cityCode);
				    mtMsg.setSmsContent(smsMTMessage.getSmsContent());
				    mtMsg.setSmsType(smsType);
				    mtMsg.setSmsSize(1);
				    mtMsg.setSendStatus(sendStatus);
				    mtMsg.setGatewayAppPort("8787");
				    mtMsg.setGatewayGroup("CMPP");
				    mtMsg.setBizTime(System.currentTimeMillis());
				    mtMsg.setRegisteredDelivery(smsMTMessage.getRegisteredDelivery());
				    mtMsg.setDeliveryGateWayId(smsMTMessage.getDeliveryGateWayId());
				    mtMsg.setSourceGateWayId(smsMTMessage.getSourceGateWayId());
				    mtMsg.setSourceGateWayProto(smsMTMessage.getSourceGateWayProto());
			        
				    mQUtils.sendSmsMT(CacheKeys.getSmsSingleTopic(), mtMsg.getGatewayAppPort(), mtMsg.getId(), FstObjectSerializeUtil.write(mtMsg));
				}
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
	}
}
