/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.siloyou.core.common.utils;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.siloyou.core.common.config.Global;
import com.sohu.tv.builder.ClientBuilder;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;

import java.util.HashSet;
import java.util.Set;

public class JedisClusterUtils {

	private static Logger logger = LoggerFactory.getLogger(JedisClusterUtils.class);
	private static JedisCluster redisCluster = getJedisInstance();
	public static final String KEY_PREFIX = Global.getConfig("redis.keyPrefix");
	
	public static JedisCluster getJedisInstance(){
		if(redisCluster == null ) {
//			GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
//			redisCluster = ClientBuilder.redisCluster(Long.parseLong(Global.getConfig("redis.appid"))).setJedisPoolConfig(poolConfig)
//					.setConnectionTimeout(1000).setSoTimeout(1000).setMaxRedirections(5).build();
//			JedisPoolConfig config = new JedisPoolConfig();
//			config.setMaxTotal(10);
//			config.setMaxIdle(2);
//			// 这里添加集群节点。可以添加多个节点，但并不是需要添加Cluster的所有节点
//			HostAndPort node0 = new HostAndPort("101.37.117.13", 7000);
//			HostAndPort node1 = new HostAndPort("101.37.117.13", 7001);
//			HostAndPort node2 = new HostAndPort("101.37.117.13", 7002);
//			HostAndPort node3 = new HostAndPort("101.37.117.13", 7003);
//			HostAndPort node4 = new HostAndPort("101.37.117.13", 7004);
//			HostAndPort node5 = new HostAndPort("101.37.117.13", 7005);
//			Set<HostAndPort> nodes = new HashSet<HostAndPort>();
//			nodes.add(node0);
//			nodes.add(node1);
//			nodes.add(node2);
//			nodes.add(node3);
//			nodes.add(node4);
//			nodes.add(node5);
//			// 创建和连接到集群
//			redisCluster = new JedisCluster(nodes, 5000, 20,config);
            GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
            redisCluster = ClientBuilder.redisCluster(Long.parseLong(com.sanerzone.common.support.config.Global.getConfig("redis.appid"))).setJedisPoolConfig(poolConfig)
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
		if(StringUtils.isBlank(value))return 0;
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
		if(cacheSeconds !=0)getJedisInstance().expire(key, cacheSeconds);
		return l;
	}
	
	public static long incr(String key){
		long l = getJedisInstance().incr(key);
		return l;
	}
	
	public static void decr(String key){
		getJedisInstance().decr(key);
	}
	
	public static void del(String key){
		getJedisInstance().del(key);
	}
	
	public static void hdel(String key, String field){
		getJedisInstance().hdel(key, field);
	}
	
	/**
	 * 获取byte[]类型Key
	 * @param key
	 * @return
	 */
	public static byte[] getBytesKey(Object object){
		if(object instanceof String){
    		return StringUtils.getBytes((String)object);
    	}else{
    		return ObjectUtils.serialize(object);
    	}
	}
	
	/**
	 * Object转换byte[]类型
	 * @param key
	 * @return
	 */
	public static byte[] toBytes(Object object){
    	return ObjectUtils.serialize(object);
	}

	/**
	 * byte[]型转换Object
	 * @param key
	 * @return
	 */
	public static Object toObject(byte[] bytes){
		return ObjectUtils.unserialize(bytes);
	}

}
