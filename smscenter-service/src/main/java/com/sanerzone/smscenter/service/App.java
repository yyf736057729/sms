package com.sanerzone.smscenter.service;

import org.springframework.context.support.GenericXmlApplicationContext;

import com.sanerzone.common.modules.InitUtils;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
    	GenericXmlApplicationContext context = new GenericXmlApplicationContext();  
        context.setValidating(false);  
        context.load("classpath*:spring-*.xml");  
        context.refresh();
        
        InitUtils init = new InitUtils();
        init.init();
        System.out.println("启动完成...");
        
        context.start();
//        while(true) {
//        	try {
//        		//System.out.println(AccountCacheUtils.getStringValue("3844", "cmppUserType", "-1"));
//				//System.out.println(KeywordsUtils.keywords("去"));
//        		Thread.sleep(1000L);
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
