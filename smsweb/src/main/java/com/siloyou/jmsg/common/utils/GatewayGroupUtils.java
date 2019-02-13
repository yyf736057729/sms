package com.siloyou.jmsg.common.utils;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.siloyou.core.common.utils.EhCacheUtils;
import com.siloyou.core.common.utils.SpringContextHolder;
import com.siloyou.jmsg.modules.sms.dao.JmsgGatewayGroupDao;
import com.siloyou.jmsg.modules.sms.entity.JmsgGatewayGroup;

//通道分组工具类
public class GatewayGroupUtils {

	private static JmsgGatewayGroupDao jmsgGatewayGroupDao = SpringContextHolder.getBean(JmsgGatewayGroupDao.class);
	
	public static void initGatewayGroup(){
		List<JmsgGatewayGroup> list = jmsgGatewayGroupDao.findList(new JmsgGatewayGroup());
		if(list != null && list.size() >0){
			Map<String,List<String>> map = gatewayGroupMap(list);
			for (Map.Entry<String, List<String>> entry : map.entrySet()) {
				put(entry.getKey(),entry.getValue());
			}
		}
	}

	//组装通道组Map
	private static Map<String,List<String>> gatewayGroupMap(List<JmsgGatewayGroup> list) {
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
	
	@SuppressWarnings("unchecked")
	public static void del(String key,String gatewayId){
		Object obj = EhCacheUtils.get(CacheKeys.GATEWAY_CACHE, key);
		if(obj != null){
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
	
	//获取通道列表 缓存中获取不到，从数据库获取并缓存
	@SuppressWarnings("unchecked")
	public static List<String> get(String groupId,String phoneType,String provinceId){
		
		String key = CacheKeys.getCacheGatewayGroupKey(groupId, phoneType, provinceId);
		Object obj = EhCacheUtils.get(CacheKeys.GATEWAY_CACHE, key);
		
		if(obj == null){
			List<String> list = Lists.newArrayList();
			JmsgGatewayGroup entity = new JmsgGatewayGroup();
			entity.setGroupId(groupId);
			entity.setPhoneType(phoneType);
			entity.setProvinceId(provinceId);
			List<JmsgGatewayGroup> jmsgGatewayGroupList = jmsgGatewayGroupDao.findList(entity);
			if(jmsgGatewayGroupList != null && jmsgGatewayGroupList.size()>0){
				put(jmsgGatewayGroupList);
				return (List<String>)EhCacheUtils.get(CacheKeys.GATEWAY_CACHE, key);
			}else{
				put(key,list);
				return list;
			}
		}else{
			if(obj instanceof List){
				return (List<String>)obj;
			}else{
				return null;
			}
		}
		
	} 
}
