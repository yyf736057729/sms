package com.sanerzone.common.modules.phone.utils;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.sanerzone.common.support.storedMap.BDBStoredMapFactoryImpl;

/**
 * 白名单工具类
 * 
 * @author zhukc
 *
 */
public class WhitelistUtils {
	
    private static Map<String, Serializable> map = BDBStoredMapFactoryImpl.INS.buildMap("sms_phone", "whitelist");//系统白名单
    

    public void clear() {
    	map.clear();
    }
    
	public static boolean isExist(String phone){
		try{
			return map.containsKey(phone);
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	public static void del(String phone){
		map.remove(phone);
	}
	
	public static void put(String phone){
		map.put(phone, 0);
	}
	
    /**
     * 存放黑名单
     * @param phones
     * @param type 0:系统黑名单 1:营销黑名单
     * @param value
     */
    public static void put(String[] phones){
    	if(phones != null && phones.length > 0){
	    	for (String phone : phones) {
				phone = phone.trim();
				put(phone);
			}
    	}
    }
    
    public static void put(List<String> phones){
    	for (String phone : phones) {
			phone = phone.trim();
			put(phone);
		}
    }

}
