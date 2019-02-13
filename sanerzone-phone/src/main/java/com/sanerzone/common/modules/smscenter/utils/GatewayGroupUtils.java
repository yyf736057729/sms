package com.sanerzone.common.modules.smscenter.utils;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sanerzone.common.modules.smscenter.entity.JmsgGatewayGroup;
import com.sanerzone.common.support.utils.EhCacheUtils;

//通道分组工具类
public class GatewayGroupUtils {

//	private static JmsgGatewayGroupDao jmsgGatewayGroupDao = SpringContextHolder.getBean(JmsgGatewayGroupDao.class);
//	
//	public static void initGatewayGroup(){
//		List<JmsgGatewayGroup> list = jmsgGatewayGroupDao.findList(new JmsgGatewayGroup());
//		if(list != null && list.size() >0){
//			Map<String,List<String>> map = gatewayGroupMap(list);
//			for (Map.Entry<String, List<String>> entry : map.entrySet()) {
//				put(entry.getKey(),entry.getValue());
//			}
//		}
//	}

	//组装通道组Map
	public static Map<String,List<String>> gatewayGroupMap(List<JmsgGatewayGroup> list) {
		Map<String,List<String>> map = Maps.newHashMap();
		for (JmsgGatewayGroup entity : list) {
			String gatewayId = entity.getGatewayId();
			String key = CacheKeys.getCacheGatewayGroupKey(entity.getGroupId(), entity.getPhoneType(), entity.getProvinceId());
			if(map.containsKey(key)){
				List<String> gatewayIds = map.get(key);
				gatewayIds.add(gatewayId);
			}else{
				List<String> gatewayIds = Lists.newArrayList();
				gatewayIds.add(gatewayId);
				map.put(key, gatewayIds);
			}
		}
		return map;
	}
	
	public static void put(String key,List<String> gatewayIds){
		EhCacheUtils.put(CacheKeys.GATEWAY_CACHE, key, gatewayIds);
	}
	
	public static void put(List<JmsgGatewayGroup> list){
		if(list != null && list.size() >0){
			Map<String,List<String>> map = gatewayGroupMap(list);
			for (Map.Entry<String, List<String>> entry : map.entrySet()) {
				put(entry.getKey(),entry.getValue());
			}
		}
	}
	
	public static void put(String groupId,String phoneType,String provinceId,Object obj){
		String key = CacheKeys.getCacheGatewayGroupKey(groupId, phoneType, provinceId);
		EhCacheUtils.put(CacheKeys.GATEWAY_CACHE, key, obj);
	}
	
	public static void del(String groupId,String phoneType,String provinceId,String gatewayId){
		String key = CacheKeys.getCacheGatewayGroupKey(groupId, phoneType, provinceId);
		del(key, gatewayId);
	}
	
//	public static void del(List<JmsgGatewayGroup> list){
//		if(list != null && list.size() >0){
//			Map<String,List<String>> map = gatewayGroupMap(list);
//			for(String key:map.keySet()){
//				del(key);
//			}
//		}
//	}
	
	public static void del(String key,String gatewayId){
		Object obj = EhCacheUtils.get(CacheKeys.GATEWAY_CACHE, key);
		if(obj != null){
			@SuppressWarnings("unchecked")
			List<String> gatewayList = (List<String>)obj;
			if(gatewayList != null && gatewayList.size() >0){
				int index = 0;
				for (String result : gatewayList) {
					if(gatewayId.equals(result)){
						gatewayList.remove(index);
						return;
					}
					index++;
				}
			}
		}
	}
	
	//获取通道列表 
	@SuppressWarnings("unchecked")
	public static List<String> get(String groupId,String phoneType,String provinceId){
		String key = CacheKeys.getCacheGatewayGroupKey(groupId, phoneType, provinceId);
		Object obj = EhCacheUtils.get(CacheKeys.GATEWAY_CACHE, key);
		if(obj == null)return null;
		return (List<String>)EhCacheUtils.get(CacheKeys.GATEWAY_CACHE, key);
	} 
}
