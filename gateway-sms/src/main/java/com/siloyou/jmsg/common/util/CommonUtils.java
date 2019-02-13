package com.siloyou.jmsg.common.util;

import java.util.Map;

import org.apache.commons.lang.StringUtils;

/**
 * 
 * <一句话功能简述>
 * <功能详细描述>
 * 
 * @author  zhangjie
 * @version  [版本号, 2016年9月1日]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public class CommonUtils
{
    /**
     * 手机号码去处86前缀
     * @param phone
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static String resetPhone(String phone)
    {
        if (null != phone && phone.length() > 0)
        {
            if (phone.startsWith("86"))
            {
                return phone.substring(2);
            }
            else
            {
                return phone;
            }
        }
        else
        {
            return phone;
        }
    }
    
    public static int getNumeriValueFromMap(Map<String, String> src, String key, int defaultValue) {
    	
    	if(null == src) {
    		return defaultValue;
    	}
    	
    	if(null == src.get(key)) {
    		return defaultValue;
    	}
    	
    	if(!StringUtils.isNumeric(src.get(key))) {
    		return defaultValue;
    	}
    	
    	return Integer.parseInt(src.get(key));
    }
    
    public static String getStringValueFromMap(Map<String, String> src, String key, String defaultValue) {
    	
    	if(null == src) {
    		return defaultValue;
    	}
    	
    	String value = StringUtils.stripToNull(src.get(key));
    	if(value == null) {
    		return defaultValue;
    	}
    	
    	return value;
    }
}
