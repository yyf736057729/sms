package com;

import com.siloyou.jmsg.common.gateway.SmsGateWay;
import com.siloyou.jmsg.common.message.SmsMtMessage;
import com.siloyou.jmsg.common.util.SpringContextHolder;
import com.siloyou.jmsg.gateway.Result;
import com.siloyou.jmsg.gateway.api.GatewayFactory;
import com.siloyou.jmsg.gateway.cmpp.CmppGatewayFactory;
import com.siloyou.jmsg.gateway.web.GatewayFacadeImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.core.env.Environment;


import com.siloyou.jmsg.modules.sms.listen.MQStartup;
import com.siloyou.jmsg.modules.sms.listen.MQStorageBatch;

@Configuration
@ComponentScan
@EnableAutoConfiguration
@SpringBootApplication
//@EnableScheduling
public class Application {
	public static ApplicationContext applicationContext;
	public static String appCode;
	public static Environment evn;

	public static boolean isRun = false;

	public static void main(String[] args) throws InterruptedException {
		ApplicationContext mainApplicationContext = SpringApplication.run(Application.class, args);

		evn = mainApplicationContext.getEnvironment();
		System.out.println(evn.getProperty("app.smsnamesvr"));
		appCode = evn.getProperty("server.port");
		String appConfig = "classpath:"+evn.getProperty("app.config");

		System.out.println("开始启动应用："+ appCode +", 配置文件："+appConfig);

		//网关应用
		if(StringUtils.startsWith(appCode, "89")) {
			applicationContext = new FileSystemXmlApplicationContext(new String[]{appConfig}, mainApplicationContext);

			if(!StringUtils.startsWith(appCode, "890")) {
				MQStartup storageStartup = applicationContext.getBean(MQStartup.class);
				storageStartup.initStorage();
			}
			GatewayFactory gatewayFactory = applicationContext.getBean(GatewayFactory.class);
			gatewayFactory.initGateway(appCode);



			CmppGatewayFactory cmppGatewayFactory = applicationContext.getBean(CmppGatewayFactory.class);
			cmppGatewayFactory.initGateway(appCode);

			Thread.sleep(3000);
			cmppGatewayFactory.updateGatewayStatus();


		} else if(StringUtils.startsWith(appCode, "100")) {
			MQStorageBatch mqStorageBatch = mainApplicationContext.getBean(MQStorageBatch.class);
			String[] appCodes = evn.getProperty("app.appcodes").split("-");
			System.out.println(appCodes.toString());
			for(String appCode : appCodes)
				mqStorageBatch.initStorage(appCode);

		} else {
			System.out.println("暂无");
		}

		isRun = true;
	}

}
