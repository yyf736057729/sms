package com.sanerzone.common.modules.phone.utils;

import java.io.Serializable;
import java.util.Map;

import com.google.common.collect.Maps;
import com.sanerzone.common.support.storedMap.BDBStoredMapFactoryImpl;
import com.sanerzone.common.support.utils.StringUtils;
import com.sanerzone.common.support.utils.ValidatorUtils;

//手机工具类
public class PhoneUtils {

	private static Map<String, Serializable> map = BDBStoredMapFactoryImpl.INS.buildMap("sms_phone_info", "phonesegment");//号段信息
	
	
	public void clear () {
		map.clear();
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String,String> get(String key){
		if(StringUtils.isNotBlank(key) && key.length() > 6) {
			key = key.substring(0, 7);
			return (Map<String,String>)map.get(key);
		}
		return Maps.newHashMap();
	}
	
	//获取运营商
	public static String getPhoneType(String key){
		Map<String,String> map = get(key);
		if(map == null || map.size() == 0)return "";
		return map.get("pt");
	}
	
	//获取省市代码
	public static String getCityCode(String key){
		Map<String,String> map = get(key);
		if(map == null || map.size() == 0)return "";
		return map.get("cc");
	}
	
	//获取运营商
	public static String getPhoneType(Map<String,String> map){
		if(map == null || map.size() == 0)return "";
		return map.get("pt");
	}
	
	//获取省市代码
	public static String getCityCode(Map<String,String> map){
		if(map == null || map.size() == 0)return "";
		return map.get("cc");
	}
	
	public static void put(String phone,String phoneType,String cityCode){
		Map<String,String> value = Maps.newHashMap();
		value.put("pt", phoneType);
		value.put("cc", cityCode);
		put(phone, value);
	}
	
	public static void put(String phone,Map<String,String> value){
		map.put(phone, (Serializable)value);
	}
    
}
