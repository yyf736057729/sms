//package com.siloyou.jmsg.gateway;
//
//import java.util.List;
//
//import com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
//import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
//import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
//import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
//import com.alibaba.rocketmq.client.exception.MQClientException;
//import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
//import com.alibaba.rocketmq.client.producer.SendResult;
//import com.alibaba.rocketmq.common.message.Message;
//import com.alibaba.rocketmq.common.message.MessageExt;
//import com.siloyou.jmsg.common.message.SmsMoMessage;
//import com.siloyou.jmsg.common.message.SmsMtMessage;
//import com.siloyou.jmsg.common.message.SmsRtMessage;
//import com.siloyou.jmsg.common.mq.MQCustomerFactory;
//import com.siloyou.jmsg.common.util.FstObjectSerializeUtil;
//
//public class SingleGateWayTest {
//
//	private String mqserver = "101.37.117.13:9876";
//	private DefaultMQProducer producer;
//
//	public void sendReport(SmsMtMessage smsMtmessage) throws Exception {
//		if (producer == null) {
//			producer = new DefaultMQProducer("SmsTestGroup");
//			producer.setNamesrvAddr(mqserver);
//			producer.setInstanceName("producer");
//			producer.start();
//		}
//		SmsRtMessage rtMessage = new SmsRtMessage();
//		rtMessage.setSmsMt(smsMtmessage);
//		rtMessage.setMsgid(new com.zx.sms.common.util.MsgId().toString());
//		rtMessage.setDestTermID(smsMtmessage.getPhone());
//		rtMessage.setSrcTermID(smsMtmessage.getSpNumber());
//		rtMessage.setStat("DELIVRD");
//		rtMessage.setSubmitTime("1608101602");
//		rtMessage.setDoneTime("1608101602");
//		rtMessage.setSmscSequence("123");
//
//		Message message = new Message("SMSRT_TPC", "RT", "", FstObjectSerializeUtil.write(rtMessage));
//		SendResult result = producer.send(message);
//		System.out.println(result.getMsgId());
//	}
//
//	public void sendMt() throws Exception {
//		if (producer == null) {
//			producer = new DefaultMQProducer("SmsTestGroup");
//			producer.setNamesrvAddr(mqserver);
//			producer.setInstanceName("producer");
//			producer.start();
//		}
//
//		SmsMtMessage smsMtmessage = new SmsMtMessage();
//		smsMtmessage.setId("123");
//		smsMtmessage.setCstmOrderID("");
//		smsMtmessage.setGateWayID("3");
//		smsMtmessage.setMsgContent("测试短信内容");
//		smsMtmessage.setPayType("");
//		smsMtmessage.setPhone("17338147110");
//		smsMtmessage.setSmsType("sms");
//		smsMtmessage.setSpNumber("10678789");
//		smsMtmessage.setTaskid("123");
//		smsMtmessage.setUserid("123");
//		smsMtmessage.setUserReportGateWayID("");
//		smsMtmessage.setUserReportNotify("");
//
//		Message message = new Message("SMS_BATCH_TOPIC", "cmppgroup", "3", FstObjectSerializeUtil.write(smsMtmessage));
//		SendResult result = producer.send(message);
//		System.out.println(result.getMsgId());
//	}
//
//	public void sendMo() throws Exception {
//		if (producer == null) {
//			producer = new DefaultMQProducer("SmsMoTestGroup");
//			producer.setNamesrvAddr(mqserver);
//			producer.setInstanceName("producer");
//			producer.start();
//		}
//		SmsMoMessage smsMomessage = new SmsMoMessage();
//		smsMomessage.setDestTermID("10678765");
//		smsMomessage.setSrcTermID("13666672546");
//		smsMomessage.setMsgid("123123123123");
//		smsMomessage.setMsgContent("测试上行");
//		smsMomessage.setGateWayID("3");
//		Message message = new Message("SMSMO_TPC", "MO", "", FstObjectSerializeUtil.write(smsMomessage));
//		SendResult result = producer.send(message);
//		System.out.println(result.getMsgId());
//	}
//
//	public static void main(String[] args) throws MQClientException, InterruptedException {
//
//		SingleGateWayTest singleGateWay = new SingleGateWayTest();
//		try {
//			singleGateWay.sendMo();
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//	}
//
//	public void test() throws MQClientException {
//
//		Consumer singleSmsMtConsumer = MQCustomerFactory.getPushConsumer("SMSMTCustomerGroup-single");
//		singleSmsMtConsumer.setInstanceName("singleSmsMtConsumer");
//		singleSmsMtConsumer.subscribe("SMS_SINGLE_TOPIC", "cmppgroup");
//		singleSmsMtConsumer.registerMessageListener(new MessageListenerConcurrently() {
//			@Override
//			public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
//				for (MessageExt msg : msgs) {
//					System.out.println("sign:" + msg.getTags() + "-" + msg.getKeys());
//
//					try {
//						SmsMtMessage smsMtMessage = (SmsMtMessage) FstObjectSerializeUtil.read(msg.getBody());
//						sendReport(smsMtMessage);
//					} catch (Exception e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				}
//				return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
//			}
//		});
//
//		DefaultMQPushConsumer batchSmsMtConsumer = MQCustomerFactory.getPushConsumer("SMSMTCustomerGroup-batch");
//		batchSmsMtConsumer.setInstanceName("batchSmsMtConsumer");
//		batchSmsMtConsumer.subscribe("SMS_BATCH_TOPIC", "cmppgroup");
//		batchSmsMtConsumer.registerMessageListener(new MessageListenerConcurrently() {
//			@Override
//			public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
//				for (MessageExt msg : msgs) {
//					System.out.println("batch:" + msg.getTags() + "-" + msg.getKeys());
//					try {
//						SmsMtMessage smsMtMessage = (SmsMtMessage) FstObjectSerializeUtil.read(msg.getBody());
//						sendReport(smsMtMessage);
//					} catch (Exception e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				}
//				return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
//			}
//		});
//
//		singleSmsMtConsumer.start();
//		batchSmsMtConsumer.start();
//		System.out.println("start");
//	}
//
//}
