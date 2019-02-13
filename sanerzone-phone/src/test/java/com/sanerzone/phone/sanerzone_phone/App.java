package com.sanerzone.phone.sanerzone_phone;

import org.springframework.context.support.GenericXmlApplicationContext;

import com.sanerzone.common.modules.phone.service.InitBlacklist;
import com.sanerzone.common.support.utils.ValidatorUtils;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
    	
    		System.out.println(ValidatorUtils.isMobile("19923124090"));
//    	GenericXmlApplicationContext context = new GenericXmlApplicationContext();  
//        context.setValidating(false);  
//        context.load("classpath*:spring-*.xml");  
//        context.refresh();
//        
//        InitBlacklist init = new InitBlacklist();
//        init.initBlacklist();
//        System.out.println("启动完成...");
//        while(true) {
//        	try {
//				Thread.sleep(1000L);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//        }
//        DemoClient demoClient = context.getBean(DemoClient.class);
//        
//        while (true) {
//        	System.out.println("say:"+demoClient.hello());
//        	
//        	try {
//				Thread.sleep(1000L);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//        }
        
    }
}
