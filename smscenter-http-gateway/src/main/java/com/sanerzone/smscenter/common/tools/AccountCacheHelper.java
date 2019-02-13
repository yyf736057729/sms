package com.sanerzone.smscenter.common.tools;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.RateLimiter;

//账户工具类
public class AccountCacheHelper {
	
	private static Logger logger = LoggerFactory.getLogger(AccountCacheHelper.class);
	
	/**
	 * 获取账户的某个信息或属性值
	 * @param accId
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public static String getStringValue(String accId,String key, String defaultValue){
		if(getMap(accId) == null) return defaultValue;
		Object value = getMap(accId).get(key);
		if(value== null) return defaultValue;
		return String.valueOf(value);
	}
	
	public static int getIntegerValue(String accId,String key, int defaultValue){
		if(getMap(accId) == null) return defaultValue;
		Object value = getMap(accId).get(key);
		if(value== null) return defaultValue;
		if(value instanceof String) 
		{
			return Integer.parseInt((String) value);
		}
		return (Integer) value;
	}
	
	/**
	 * 获取缓存里账户信息Map
	 * @param accId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map<String,String> getMap(String accId){
		Object obj = EhCacheHelper.get(accId);
		if(obj != null){
			return (Map<String,String>)obj;
		}
		return null;
	}
	
	/**
	 * 缓存账号信息
	 * @param accId
	 * @param map
	 */
	public static void put(String accId,Map<String,String> map){
		logger.info("{}, update cache: {}", accId, map);
		int speed = StringHelper.getIntegerValue(map.get("httpSpeed"),0);
		if(speed > 0){
			RateLimiter limiter = RateLimiter.create(speed);
			AccountCacheHelper.putHttpSpeed(accId, limiter);
		}
		EhCacheHelper.put(accId, map);
	}
	
	/**
	 * http速率
	 * @param accId
	 * @param obj
	 */
	public static void putHttpSpeed(String accId,Object obj){
		EhCacheHelper.put("http_speed_"+accId, obj);
	}
	
	/**
	 * 获取速率
	 * @param accId
	 * @return
	 */
	public static RateLimiter getHttpSpeed(String accId){
		return (RateLimiter)EhCacheHelper.get("http_speed_"+accId);
	}
	
	/**
	 * 删除账户信息
	 * @param accId
	 */
	public static void del(String accId){
		EhCacheHelper.remove(accId);
		EhCacheHelper.remove("http_speed_"+accId);
	}
	
	/**
	 * 获取key
	 * accId V1版本使用的是userId
	 * 
	 * @param keyType amount、use
	 * @param servType sms、mms
	 * @param accId V1版本使用的是userId
	 * @return
	 */
	public static String getAmountKey(String keyType, String servType, String accId)
	{
		return keyType + "_" + servType + "_" + accId;
	}
	
	/**
	 * 获取key
	 * accId V1版本使用的是userId
	 * 
	 * @param keyType amount、use
	 * @param servType sms、mms
	 * @param accId
	 * @return
	 */
	public static String getAmountKey(String servType, String accId)
	{
		return getAmountKey("amount", servType, accId);
	}
}
