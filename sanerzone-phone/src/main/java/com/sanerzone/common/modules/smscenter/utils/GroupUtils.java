package com.sanerzone.common.modules.smscenter.utils;

import com.sanerzone.common.modules.smscenter.entity.JmsgGroup;
import com.sanerzone.common.support.utils.EhCacheUtils;

//分组工具类
public class GroupUtils {
	
	//缓存分组
    public static void put(JmsgGroup jmsgGroup){
        String key = CacheKeys.getCacheGroupKey(jmsgGroup.getId());
        EhCacheUtils.put(CacheKeys.GATEWAY_CACHE, key, jmsgGroup);
    }
	
	//删除分组
	public static void del(String groupId){
		String key = CacheKeys.getCacheGroupKey(groupId);
		EhCacheUtils.remove(CacheKeys.GATEWAY_CACHE, key);
	}
	
	//判断分组是否存在
	public static boolean isExists(String groupId){
	    JmsgGroup jmsgGroup = getJmsgGroup(groupId);
	    if(jmsgGroup==null)return false;
	    return "1".equals(jmsgGroup.getStatus()) ? true : false;
	}
	
	
	//获取分组信息 缓存中不存在，则获取数据库并缓存
	public static String get(String groupId){
	    JmsgGroup jmsgGroup = getJmsgGroup(groupId);
	    if(jmsgGroup == null ) return "0";
	    return jmsgGroup.getStatus() == null ? "0" : jmsgGroup.getStatus();
	    
	}
	
	//获取分组信息
    public static JmsgGroup getJmsgGroup(String groupId){
        Object obj = EhCacheUtils.get(CacheKeys.GATEWAY_CACHE, CacheKeys.getCacheGroupKey(groupId));
        if(obj == null) return null;
        return (JmsgGroup)obj;
    }
}
