package com.siloyou.jmsg.common.utils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Sets;

/**
 * 白名单工具类
 * 
 * @author zhukc
 *
 */
public class WhitelistUtils {

	public static final String WHITELIST_CACHE = "whitelistCache";
	private static Set<String> set = Sets.newHashSet();
	
	//初始化白名单
	public static void initWhitelist(List<String> list){ 
		set = new HashSet<String>(list);
	}
	
	public static boolean isExist(String phone){
		boolean flag = false;
		try{
			flag = set.contains(phone);
		}catch(Exception e){
			e.printStackTrace();
		}
		return flag;
	}
	
	public static void del(String phone){
		set.remove(phone);
	}
	
	public static void put(String phone){
		set.add(phone);
	}
	
	public static void put(Set<String> phone){
		set.addAll(phone);
	}

}
