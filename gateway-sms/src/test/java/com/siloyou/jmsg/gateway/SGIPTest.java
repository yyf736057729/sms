package com.siloyou.jmsg.gateway;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import com.google.common.collect.Queues;
import com.google.common.util.concurrent.RateLimiter;
import com.sanerzone.smscenter.gateway.sgip.SGIPSMProxy;
import com.sanerzone.smscenter.gateway.sgip.comm.sgip.message.SGIPDeliverMessage;
import com.sanerzone.smscenter.gateway.sgip.comm.sgip.message.SGIPMessage;
import com.sanerzone.smscenter.gateway.sgip.comm.sgip.message.SGIPReportMessage;
import com.sanerzone.smscenter.gateway.sgip.comm.sgip.message.SGIPSubmitMessage;
import com.sanerzone.smscenter.gateway.sgip.comm.sgip.message.SGIPSubmitRepMessage;
import com.sanerzone.smscenter.gateway.sgip.util.Args;
import com.sanerzone.smscenter.gateway.sgip.util.Debug;

public class SGIPTest extends SGIPSMProxy {
	
	public static SGIPTest sgipTest ;
	public static String[] UserNumber = {"17338147110"};//接收短信的手机号码，前边要加上86
	public static  String content = "联通短信发送成功";
	public static boolean isOpen = false;
	public SGIPTest(Args args){
		super(args);
//		startService("103.239.204.21", 5077);
	}
	
	public SGIPMessage onDeliver(SGIPDeliverMessage msg)
	{
		System.out.println(msg.toString());
		 return super.onDeliver(msg);
	}
	 
	public SGIPMessage onReport(SGIPReportMessage msg) {
		reportCnt.incrementAndGet();
//		System.out.println(msg.toString());
		return super.onReport(msg);
	}
	
	public void onTerminate()
	{
		isOpen = false;
		System.out.println("onTerminate:" + (sgipTest.getConnState() == null));
//		if( sgipTest.getConnState() == null ) {
//			send(UserNumber, content, sgipTest);
//		}
	}
	
	
	// 队列
	public static ArrayBlockingQueue<Integer> queue = Queues.newArrayBlockingQueue(1000);
	public static AtomicLong cnt = new AtomicLong(0L);
	public static AtomicLong reportCnt = new AtomicLong(0L);
	public static long lastNum = 0;
	public static long lastRptNum = 0;
	public static int rate = 1;
	public static void main(String[] args) {
//		<SGIPConnect>
//	       <!-- SMG主机地址 -->
//	       <host>10.71.122.200</host>
//	       <!-- SMG主机端口号 -->
//	       <port>8801</port>
//	       <!-- 操作超时时间(单位：秒) -->
//	       <transaction-timeout>10</transaction-timeout>
//	       <!-- 物理连接读操作超时时间(单位：秒) -->
//	       <read-timeout>15</read-timeout>
//	       <!--SP…ID(最大为六位字符)-->
//	       <source-addr>222222</source-addr>
//	       <!--双方协商的版本号(大于0，小于256)-->
//	       <version>1</version>
//	       <!--登陆用户名(最大为六位字符)-->
//	       <login-name>new3</login-name>
//	       <!--登陆密码-->
//	       <login-pass>new3</login-pass>
//	       <!--是否属于调试状态,true表示属于调试状态，所有的消息被打印输出到屏幕，false表示不属于调试状态，所有的消息不被输出-->
//	       <debug>false</debug>
//	</SGIPConnect>
	
//		sgip_ip=220.199.6.27
//				sgip_port=8801
//				sgip_corpId=98801
//				sgip_spNumber=10690843
//				sgip_userName=10690843
//				sgip_passWord=123456
//
//				sgip_local_ip=103.239.204.21
//				sgip_local_port=5077
//				sgip_signname=
//
//				service_id=9951005601
		
		Args param = new Args();
		param.set("host", "127.0.0.1");
		param.set("port", 8001);
		param.set("transaction-timeout", 10);
		param.set("reconnect-interval", 3);
		param.set("read-timeout", 15);
		param.set("source-addr", 8801);
		param.set("version", 1);
		param.set("login-name", "333");
		param.set("login-pass", "0555");
		param.set("debug", "false");
//		param.set("local-host", "127.0.0.1");
//		param.set("local-host", "220.199.6.27");
		sgipTest = new SGIPTest(param);
		sgipTest.startService("127.0.0.1", 8801);
//		try {
//			boolean result = sgipTest.connect("10690843", "123456");
//			if(result) {
//				isOpen = true;
//				send(UserNumber, content, sgipTest);
//				System.out.println("连接成功....");
//			} else {
//				System.out.println("连接失败(用户名或密码错误)...........");
//				return ;
//			}
//		} catch (Exception ex) {
//            System.out.println("网络异常...........");
//            ex.printStackTrace();
//            return;
//        }
		
		new Thread(new Runnable() {
			RateLimiter limiter = RateLimiter.create(800);
			@Override
			public void run() {
				for (;;) {
					
		        	if(sgipTest.getConn() == null || !sgipTest.getConn().available() || !isOpen) {
		        		isOpen = sgipTest.connect("333", "0555");
		        		try {
							Thread.sleep(100L);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
		        	}
		        	
		        	if(isOpen) {
		        		try {
							Integer item = queue.take();
							if(item != null) {
								limiter.acquire();
				        		send(UserNumber, content, sgipTest);
				        		cnt.incrementAndGet();
							}
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
		        		
		        	}
					
				}
			}
		}).start();
		
		ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);  
        /** 
         * 创建并执行在给定延迟后启用的一次性操作。 
         *  
         * 这里用于在N时间后取消任务 
         */  
        scheduler.scheduleAtFixedRate(new Runnable() {  
            public void run() {  
            	long nowcnt = cnt.get();
            	long nowreportCnt = reportCnt.get();
				System.out.println(String.format("Totle Receive Msg Num:%d,   speed : %d/s, queueSize: %d", nowcnt, (nowcnt - lastNum)/rate, queue.size()));
				System.out.println(String.format("Totle Report Msg Num:%d,   speed : %d/s", nowreportCnt, (nowreportCnt - lastRptNum)/rate));
				lastNum = nowcnt;
				lastRptNum = nowreportCnt;
            }  
        }, rate, rate, TimeUnit.SECONDS);  
        
        
//        RateLimiter limiter = RateLimiter.create(800);
        while(true) {
        	offerQueue(10000);
        	try {
				Thread.sleep(60000L);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
        }
		
	}
	
	public static void offerQueue(int size) {
		for(int i=0;i<size ;i ++)
			try {
				queue.put(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	public static void send(String[] UserNumber, String content, SGIPTest sgipTest) {
		try {
        	byte[] MessageContent = content.getBytes("GB2312");
//            System.out.println("短信内容: "+content);
            // 下发短息
            SGIPSubmitMessage sgipsubmit = new SGIPSubmitMessage(
                    "10690843", // SP的接入号码
                    "", // 付费号码 string
                    UserNumber, // 接收该短消息的手机号，最多100个号码 string[]
                    "98801", // 企业代码，取值范围为0～99999 string
                    "9951005601", // 业务代码，由SP定义 stirng
                    03, // 计费类型 int
                    "0", // 该条短消息的收费值 stirng
                    "0", // 赠送用户的话费 string
                    0, // 代收费标志0：应收1：实收 int
                    0, // 引起MT消息的原因 int
                    06, // 优先级0～9从低 到高，默认为0 int
                    null, // 短消息寿命的终止时间 date
                    null, // 短消息定时发送的时间 date
                    1, // 状态报告标记 int
                    0, // GSM协议类型 int
                    0, // GSM协议类型 int
                    15, // 短消息的编码格式 int
                    0, // 信息类型 int
                    MessageContent.length, // 短消息内容长度 int
                    MessageContent, // 短消息的内容 btye[]
                    "0" // 保留，扩展用 string
            );
            // 收到的响应消息转换成rep
            int status = ProcessSubmitRep(sgipTest.send(sgipsubmit));
//            System.out.println(status);
//            if (status == 0) {
//                System.out.println("消息发送成功..........");
//            } else {
//                System.out.println("消息发送失败..........");
//            }
        } catch (Exception ex) {
            ex.printStackTrace();           
        }
	}
	
	private static int ProcessSubmitRep(SGIPMessage msg) {
        // 收到的响应消息转换成repMsg
        SGIPSubmitRepMessage repMsg = (SGIPSubmitRepMessage) msg;
//        System.out.println(repMsg.getSrcNodeId());
//        System.out.println("status:::::::" + repMsg.getResult() + "-->" + repMsg.getMsgid());
//        if (repMsg != null && repMsg.getResult() == 0) {
//            System.out.println("发送成功：：：");
//        }
        return repMsg.getResult();
    }

		
	
}
