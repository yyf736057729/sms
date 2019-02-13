package com.sanerzone.common.modules.phone.utils;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.sanerzone.common.support.storedMap.BDBStoredMapFactoryImpl;

/**
 * 黑名单工具类  
 * type 0: 系统黑名单 
 *      1：群发黑名单
 *      2: 营销黑名单
 *      3：验证码黑名单
 * 
 * @author zhukc
 *
 */
public class BlacklistUtils{
    
    
    private static Map<String, Serializable> sysMap = BDBStoredMapFactoryImpl.INS.buildMap("sms_phone_blacklist", "sysblacklist");//系统黑名单
    private static Map<String, Serializable> map = BDBStoredMapFactoryImpl.INS.buildMap("sms_phone", "blacklist");//群发黑名单
    private static Map<String, Serializable> marketmap = BDBStoredMapFactoryImpl.INS.buildMap("sms_phone_market", "marketblacklist");//营销黑名单
    private static Map<String, Serializable> yzmmap = BDBStoredMapFactoryImpl.INS.buildMap("sms_phone_yzm", "yzmblacklist");//验证码黑名单
    
    public static void clearAll() {
    	BDBStoredMapFactoryImpl.INS.buildMap("sms_phone", "sysblacklist").clear();
    	BDBStoredMapFactoryImpl.INS.buildMap("sms_phone", "marketblacklist").clear();
    	BDBStoredMapFactoryImpl.INS.buildMap("sms_phone", "yzmblacklist").clear();
    }
    
    public static int[] size() {
    	int[] size = new int[]{sysMap.size(), marketmap.size(), yzmmap.size()};
    	return size;
    }
    
    /**
     * @param phone 手机号
     * @param type 类型 0:系统 1:群发 2:营销 3：验证码
     * @return 判断号码是否存在 true:存在
     */
    public static boolean isExist(String phone, int type){
    	try{
	    	if(0 == type){
	    		return sysMap.containsKey(phone);
	    	}else if(1 == type){
	    		return map.containsKey(phone);
	    	}else if(2 == type){
	    		return marketmap.containsKey(phone);
	    	}else if(3 == type){
	    		return yzmmap.containsKey(phone);
	    	}
    	}catch(Exception e){
    		e.printStackTrace();
    		return false;
    	}
        return false;
    }
    
    /**
     * 是否系统黑名单
     * @param phone
     * @return
     */
    public static boolean isExistSysBlackList(String phone){
    	return isExist(phone, 0);
    }
    
    /**
     * 是否群发黑名单
     * @param phone
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static boolean isExistBlackList(String phone){
    	return isExist(phone, 1);
    }
    
    /**
     * 是否营销黑名单
     * @param phone
     * @return
     */
    public static boolean isExistMarketBlackList(String phone){
    	return isExist(phone, 2);
    }
    
    /**
     * 是否验证码黑名单
     * @param phone
     * @return
     */
    public static boolean isExistYzmBlackList(String phone){
    	return isExist(phone, 3);
    }
    
    /**
     * 存放系统黑名单
     * @param phone
     */
    public static void putSysBlackList(String phone){
    	put(phone, 0, 0);
    }
    
    /**
     * 存放黑名单
     * @param phone
     * @param value
     */
    public static void putBlackList(String phone,int value){
    	put(phone, 1, value);
    }
    
    public static void put(String phone,int type,int value){
    	if(0 == type){
    		sysMap.put(phone, value);
    	}else if(1 == type){
    		map.put(phone, value);
    	}else if(2 == type){
    		marketmap.put(phone, value);
    	}else if(3 == type){
    		yzmmap.put(phone, value);
    	}
    }
    
    /**
     * 删除黑名单
     * @param key 手机号码
     * @param type 0:系统黑名单 1:群发黑名单 2:营销黑名单  3:验证码黑名单
     */
    public static void del(String key,int type){
    	if(0 == type){
    		sysMap.remove(key);
    	}else if(1 == type){
    		map.remove(key);
    	}else if(2 == type){
    		marketmap.remove(key);
    	}else if(3 == type){
    		yzmmap.remove(key);
    	}
    }
    
    /**
     * 存放黑名单
     * @param phones
     * @param type 0:系统黑名单 1:群发黑名单 2:营销黑名单 3:验证码黑名单
     * @param value
     */
    public static void put(String[] phones,int type,int value){
    	if(phones != null && phones.length > 0){
	    	for (String phone : phones) {
				phone = phone.trim();
				put(phone, type, value);
			}
    	}
    }
    
    /**
     * 存放黑名单
     * @param phones
     * @param type 0:系统黑名单  1:群发黑名单  2:营销黑名单 3:验证码黑名单
     * @param value
     */
    public static void put(List<String> phones,int type,int value){
    	if(phones != null && phones.size() > 0){
	    	for (String phone : phones) {
				phone = phone.trim();
				put(phone, type, value);
			}
    	}
    }
    
}
