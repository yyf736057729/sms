package com.sanerzone;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * spring工具类，通过ApplicationContext.getBean获取注册的bean
 * 
 */
@Component
public class SpringConfigHolder implements ApplicationContextAware {
    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringConfigHolder.applicationContext = applicationContext;
    }

    /**
     * 获取spring的bean
     * */
    public static <T> T getBean(Class<T> clazz) {
        return applicationContext.getBean(clazz);
    }

}