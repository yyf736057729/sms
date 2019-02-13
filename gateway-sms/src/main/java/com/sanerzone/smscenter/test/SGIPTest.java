package com.sanerzone.smscenter.test;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

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
	
//	public static SGIPTest sgipTest ;
	public static String[] UserNumber = {"13666672546"};//接收短信的手机号码，前边要加上86
	public static  String content = "联通短信发送成功";
	
	public AtomicBoolean isOpen = new AtomicBoolean(true);
	
	
	public RateLimiter limiter = RateLimiter.create(10);
	public long lastNum = 0;
	public int rate = 1;
	public AtomicLong cnt = new AtomicLong(0L);
	public String name;
	private String phone = "";
	
	public SGIPTest(Args args){
		super(args);
		name = args.get("id", "Test");
		phone = args.get("phone", "13666672546");
		limiter = RateLimiter.create(args.get("rate", 1000));
	}
	
	public void run(ScheduledExecutorService scheduler) {
		send(new String[]{phone}, content, this);
		scheduler.scheduleAtFixedRate(new Runnable() {  
	        public void run() {  
	        	long nowcnt = cnt.get();
				System.out.println(String.format("ID:%s, rate:%f, Totle Receive Msg Num:%d,   speed : %d/s", name, limiter.getRate(), nowcnt, (nowcnt - lastNum)/rate));
				lastNum = nowcnt;
	        }  
	    }, rate, rate, TimeUnit.SECONDS);
		
		final SGIPTest sgiptest = this;
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				while(true) {
//					if(!isOpen.get()) {
						sgiptest.send(new String[]{phone}, content, sgiptest);
//		        	}
				}
			}
		}).start();
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				while(true) {
					sgiptest.send(new String[]{phone}, content, sgiptest);
				}
			}
		}).start();
	}
	
	public SGIPMessage onDeliver(SGIPDeliverMessage msg)
	{
		System.out.println(msg.toString());
		 return super.onDeliver(msg);
	}
	 
	public SGIPMessage onReport(SGIPReportMessage msg) {
		cnt.incrementAndGet();
		return super.onReport(msg);
	}
	
	public void onTerminate()
	{
		super.onTerminate();
		isOpen.set(false);
		System.out.println("onTerminate" + name + " => "+ (this.getConnState() == null));
	}
	
	
	// 队列
	public static void main(String[] args) {
		
		Args param = new Args();
		param.set("id", "LT8001");
		param.set("host", "127.0.0.1");
		param.set("port", 8001);
		param.set("transaction-timeout", 1);
		param.set("reconnect-interval", 3);
		param.set("read-timeout", 15);
		param.set("source-addr", 8801);
		param.set("version", 1);
		param.set("login-name", "333");
		param.set("login-pass", "0555");
		param.set("debug", "false");
		param.set("phone", "9999999");
		
		param.set("local-host", "127.0.0.1");
		param.set("local-port", "8801");
		
		final SGIPTest sgipTest = new SGIPTest(param);
		ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);  
		sgipTest.run(scheduler);
		sgipTest.startService();
		
		Args param2 = new Args();
		param2.set("id", "LT5555");
		param2.set("host", "127.0.0.1");
		param2.set("port", 8002);
		param2.set("transaction-timeout", 1);
		param2.set("reconnect-interval", 3);
		param2.set("read-timeout", 15);
		param2.set("source-addr", 8801);
		param2.set("version", 1);
		param2.set("login-name", "333");
		param2.set("login-pass", "0555");
		param2.set("debug", "false");
		param2.set("rate", "500");
		final SGIPTest sgipTest2 = new SGIPTest(param2);
//		sgipTest2.run(scheduler);
		
//		sgipTest.startService("127.0.0.1", 8801);
//		try {
//			boolean result = sgipTest.connect("10690843", "123456");
//			if(result) {
//				isOpen.set(true);
//				long sTime = System.currentTimeMillis();
//				sgipTest.send(UserNumber, content, sgipTest);
//				System.out.println("连接成功...." +  (System.currentTimeMillis()- sTime)/1000);
//			} else {
//				System.out.println("连接失败(用户名或密码错误)...........");
//				return ;
//			}
//		} catch (Exception ex) {
//            System.out.println("网络异常...........");
//            ex.printStackTrace();
//            return;
//        }
		
		
//		ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1); 
//		scheduler.scheduleAtFixedRate(new Runnable() {  
//	        public void run() {  
//	        	System.out.println("=====");
//	        }
//	    }, 1, 1, TimeUnit.SECONDS);  
        
        while(true) {
        	
//        	if(!isOpen.get()) {
//        		send(UserNumber, content, sgipTest);
//        	}
        	
        	
        	
//        	
//        	send(UserNumber, content, sgipTest);
        	
        	
        	
        	
        	try {
//        		System.out.println(sgipTest.getConnState());
//        		if( sgipTest.getConnState() == null ) {
//        			send(UserNumber, content, sgipTest);
//        		}
        		
				Thread.sleep(100L);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
		
	}
	
	public void send(String[] UserNumber, String content, SGIPTest sgipTest) {
		limiter.acquire();
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
	
	private int ProcessSubmitRep(SGIPMessage msg) {
        // 收到的响应消息转换成repMsg
        SGIPSubmitRepMessage repMsg = (SGIPSubmitRepMessage) msg;
//        System.out.println(repMsg.getSrcNodeId());
//        System.out.println("status:::::::" + repMsg.getResult() + "-->" + repMsg.getMsgid());
//        if (repMsg != null && repMsg.getResult() == 0) {
//            System.out.println("发送成功：：：");
//        }
        if(repMsg == null) return -1;
        cnt.incrementAndGet();
        return repMsg.getResult();
    }

		
	
}
