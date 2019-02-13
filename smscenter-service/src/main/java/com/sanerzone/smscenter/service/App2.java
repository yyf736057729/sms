package com.sanerzone.smscenter.service;

import org.springframework.context.support.GenericXmlApplicationContext;

import com.sanerzone.common.modules.account.utils.AccountCacheUtils;

/**
 * Hello world!
 *
 */
public class App2 
{
    public static void main( String[] args )
    {
    	GenericXmlApplicationContext context = new GenericXmlApplicationContext();  
        context.setValidating(false);  
        context.load("classpath*:spring-*.xml");  
        context.refresh();
        System.out.println("启动完成...");

        while (true) {
        	System.out.println(AccountCacheUtils.getStringValue("3844", "cmppUserType", "-1"));
        	
        	try {
				Thread.sleep(1000L);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        
    }
}
