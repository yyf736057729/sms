package com.sanerzone.jmsg.util;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import com.sanerzone.common.support.storedMap.BDBStoredMapFactoryImpl;

public class UserSignUtils {
	
	private static Map<String, Serializable> map = BDBStoredMapFactoryImpl.INS.buildMap("sms_sign", "sign");//用户签名
	
	public static void put(String day,String userId,String sign, int sendCount){
		String key = getKey(day,userId,sign);
		if(map.containsKey(key)){
			int count = (int) map.get(key);
			map.put(key, count+sendCount);
		}else{
			map.put(key, sendCount);
		}
	}
	
	private static String getKey(String day,String userId,String sign){
		return day+"||"+userId+"||"+sign;
	}
	
	//移除缓存
	public static void remove(String day){
		Iterator<String> it = map.keySet().iterator();
		while(it.hasNext()) {
			String key = it.next();
			if(key.startsWith(day)){
				map.remove(key);
			}
		}
	}
	
	//移除缓存
	public static void del(String key){
		map.remove(key);
	}
	
	//获取所有的数据
	public static Map<String,Serializable> allMap(){
		return map;
	}
}
