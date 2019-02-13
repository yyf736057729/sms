package com.sanerzone;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.sanerzone.common.modules.InitUtils;
import com.sanerzone.common.modules.account.service.InitAccount;
import com.sanerzone.jmsg.listener.MQStartup;

/**
 * Hello world!
 *
 */
public class App 
{
    private static ClassPathXmlApplicationContext context;

	public static void main( String[] args )
    {
		Logger logger = LoggerFactory.getLogger(App.class);
		
		String startType = (args == null || args.length == 0)?"1": args[0];
		System.out.println(startType);
    	context = new ClassPathXmlApplicationContext("classpath*:spring-*.xml");
		if(StringUtils.equals(startType, "1")) {
    		InitAccount account = new InitAccount();
    		account.initAccount();
    		logger.info("用户缓存加载完成!");
        }
		startType = "3";
        if(StringUtils.equals(startType, "3")) {
	        InitUtils init = new InitUtils();
	        init.init();
	        
	        logger.info("业务缓存全部加载完成!");
        }
		startType = "4";
        if(StringUtils.equals(startType, "4")) {
        	new ClassPathXmlApplicationContext(new String[]{"task-context.xml"}, context);
	        
	        logger.info("同步定时任务加载完成!");
        }
		startType = "3";
        MQStartup startup = context.getBean(MQStartup.class);
        startup.init(Integer.parseInt(startType));
        
        logger.info("服务启动完成...");
    }
}
