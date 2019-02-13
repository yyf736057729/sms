package com.siloyou.core.modules.sys.listener;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import org.springframework.web.context.WebApplicationContext;

import com.siloyou.core.modules.sys.service.SystemService;
import com.siloyou.jmsg.common.utils.PhoneTypeUtils;

public class WebContextListener extends org.springframework.web.context.ContextLoaderListener {
	
	@Override
	public WebApplicationContext initWebApplicationContext(ServletContext servletContext) {
		if (!SystemService.printKeyLoadMessage()){
			return null;
		}
		return super.initWebApplicationContext(servletContext);
	}

	
	@Override
	public void contextInitialized(ServletContextEvent event) {
		super.contextInitialized(event);
	}
	
}
