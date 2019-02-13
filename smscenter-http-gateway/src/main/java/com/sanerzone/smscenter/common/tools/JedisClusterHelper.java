package com.sanerzone.smscenter.common.tools;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sohu.tv.builder.ClientBuilder;
import com.xiaoleilu.hutool.setting.dialect.Props;
import com.xiaoleilu.hutool.util.ObjectUtil;

import redis.clients.jedis.JedisCluster;

public class JedisClusterHelper {

	private static Logger logger = LoggerFactory.getLogger(JedisClusterHelper.class);
	private static JedisCluster redisCluster = getJedisInstance();
	
	public static JedisCluster getJedisInstance(){
		if(redisCluster == null ) {
			Props props = new Props("config.properties");
			GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
			redisCluster = ClientBuilder.redisCluster(props.getLong("redis.appid")).setJedisPoolConfig(poolConfig)
					.setConnectionTimeout(1000).setSoTimeout(1000).setMaxRedirections(5).build();
		}
		return redisCluster;
	}
	
	public static void set(String key,String value,int cacheSeconds){
		getJedisInstance().set(key, value);
		if (cacheSeconds != 0) {
			getJedisInstance().expire(key, cacheSeconds);
		}
	}
	
	public static void hset(String key, String field,String value){
		getJedisInstance().hset(key, field, value);
	}
	
	public static int getInt(String key){
		String value = getJedisInstance().get(key);
		if(StringUtils.isBlank(value)) {
			return 0;
		}
		return Integer.valueOf(value);
	}
	
	public static String get(String key){
		return getJedisInstance().get(key);
	}
	
	public static String hget(String key,String field){
		return getJedisInstance().hget(key, field);
	}
	
	public static long incr(String key,int cacheSeconds){
		long l = getJedisInstance().incr(key);
		if(cacheSeconds !=0) {
			getJedisInstance().expire(key, cacheSeconds);
		}
		return l;
	}
	
	public static long incr(String key){
		long l = (Long) getJedisInstance().incr(key);
		return l;
	}
	
	public static long incrBy(String key, long integer)
	{
		long result = 0;
		
		try
		{
			result = getJedisInstance().incrBy(key, integer);
		}
		catch(Exception e)
		{
			result = 0;
			logger.error("{}", e);
		}
		
		return result;
	}
	
	public static long decrBy(String key, long integer)
	{
		return getJedisInstance().decrBy(key, integer);
	}
	
	public static void decr(String key){
		getJedisInstance().decr(key);
	}
	
	public static void del(String key){
		getJedisInstance().del(key);
	}
	
	
	/**
	 * Object转换byte[]类型
	 * @param key
	 * @return
	 */
	public static byte[] toBytes(Object object){
    	return ObjectUtil.serialize(object);
	}

	/**
	 * byte[]型转换Object
	 * @param key
	 * @return
	 */
	public static Object toObject(byte[] bytes){
		return ObjectUtil.unserialize(bytes);
	}

}
