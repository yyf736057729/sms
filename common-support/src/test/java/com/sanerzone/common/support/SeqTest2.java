//package com.sanerzone.common.support;
//
//import org.databene.contiperf.PerfTest;
//import org.databene.contiperf.junit.ContiPerfRule;
//import org.junit.Rule;
//import org.junit.Test;
//
//import com.alibaba.rocketmq.client.exception.MQClientException;
//import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
//import com.alibaba.rocketmq.common.message.Message;
//import com.sanerzone.common.support.rocketmq.MQProducerFactory;
//import com.sanerzone.common.support.sequence.Sequence;
//import com.sanerzone.common.support.utils.FstObjectSerializeUtil;
//
//public class SeqTest2 {
//
//	@Rule
//	public ContiPerfRule i = new ContiPerfRule();
//
//	Sequence sequence = new Sequence();
//
//	public static DefaultMQProducer smsSRProducer = MQProducerFactory.getMQProducer("TingfvTestSMSSRGroup",
//			"TingfvTestSMSSRInstance");
//	static {
//		try {
//			smsSRProducer.start();
//		} catch (MQClientException e) {
//			e.printStackTrace();
//		}
//	}
//
//	@Test
//	@PerfTest(invocations = 2000000, threads = 16)
//	public void writeRocketMq() {
//		SMSMTMessage smsmt = new SMSMTMessage();
////		smsmt.setId(new MsgId().toString());
//		smsmt.setAccId("1");
//		smsmt.setUserid("45");
//		smsmt.setFeeType("2");
//		smsmt.setFeePayment("2");
//		smsmt.setTaskid(String.valueOf(sequence.nextId()));
//		smsmt.setCustomTaskid("");
//		smsmt.setCustomServiceid("");
//		smsmt.setPhone(String.valueOf(sequence.nextId()));
//		smsmt.setPhoneType("YD");
//		smsmt.setPhoneArea("320100");
//		smsmt.setSpnumber("106575111");
//		smsmt.setSmsContent("短信内容");
//		smsmt.setSmsType("1");
//		smsmt.setSmsSize(1);
//		smsmt.setSendStatus("T000");
//		smsmt.setGatewayId("3");
//		smsmt.setGatewayGroup("1");
//		smsmt.setSourceGateWayId("");
//		Message msg;
//		try {
//			msg = new Message("SMS_SINGLE_TOPIC", "8988", "0", FstObjectSerializeUtil.write(smsmt));
//			smsSRProducer.send(msg);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//
//
////	@Test
////	public void test () {
////		 	System.out.println("2.持久化Queue插入和排空数据所耗时间");
////	        long start=System.nanoTime();
////
////	        int size = 1000000;
////	        for(int i=0;i < size ; i++)
////	        	queue.offer(sequence.nextId());
////
////	        long time=System.nanoTime()-start;
////	        System.out.println("\t填充 "+size+" 条数据耗时: "+(double)time/1000000+" 毫秒,单条耗时: "+((double)time/size/1000000)+" 豪秒");
////	        start=System.nanoTime();
////	        while(!queue.isEmpty()) {
////	        	queue.poll();
////	        }
////	        time=System.nanoTime()-start;
////	        System.out.println("\t排空 "+size+" 条数据耗时: "+(double)time/1000000+" 毫秒,单条耗时: "+((double)time/size/1000)+" 豪秒");
////	}
//
//}
