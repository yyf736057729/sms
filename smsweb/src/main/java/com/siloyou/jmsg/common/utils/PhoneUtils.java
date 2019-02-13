package com.siloyou.jmsg.common.utils;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;
import com.siloyou.core.common.utils.EhCacheUtils;
import com.siloyou.core.common.utils.SpringContextHolder;
import com.siloyou.core.common.utils.StringUtils;
import com.siloyou.core.common.utils.ValidatorUtils;
import com.siloyou.jmsg.modules.sms.dao.JmsgPhoneInfoDao;
import com.siloyou.jmsg.modules.sms.entity.JmsgPhoneInfo;

//手机工具类
public class PhoneUtils {
	
	private static JmsgPhoneInfoDao jmsgPhoneInfoDao = SpringContextHolder.getBean(JmsgPhoneInfoDao.class);

	private static Map<String,Map<String,String>> map = Maps.newHashMap();

	//初始化号段
	public static void initPhoneType() {
		List<JmsgPhoneInfo> list = jmsgPhoneInfoDao.findAllList(new JmsgPhoneInfo());
		for (JmsgPhoneInfo entity : list) {
			Map<String,String> value = Maps.newHashMap();
			value.put("pt", entity.getPhoneType());
			value.put("cc",entity.getPhoneCityCode());
			map.put(entity.getPhone(), value);
		}
	}
	
	public static Map<String,String> get(String key){
		if(StringUtils.isNotBlank(key) && ValidatorUtils.isMobile(key)) {
			key = key.substring(0, 7);
			return map.get(key);
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
		map.put(phone, value);
	}
	
	
	//初始化省市列表
    public static void initCityList() {
        List<JmsgPhoneInfo> list = jmsgPhoneInfoDao.findCityList();
        for (JmsgPhoneInfo entity : list) {
            EhCacheUtils.put(CacheKeys.GATEWAY_CACHE, entity.getPhoneCityCode(), entity);
        }
    }
    
    //获取省市
    public static JmsgPhoneInfo getCity(String phoneCityCode){
    	
    	if(StringUtils.isBlank(phoneCityCode))return new JmsgPhoneInfo();
    	
        Object obj = EhCacheUtils.get(CacheKeys.GATEWAY_CACHE, phoneCityCode);
        
        if(obj == null){
            JmsgPhoneInfo jmsgPhoneInfo = new JmsgPhoneInfo();
            jmsgPhoneInfo.setPhoneCityCode(phoneCityCode);
            
            List<JmsgPhoneInfo> list = jmsgPhoneInfoDao.findList(jmsgPhoneInfo);
            if (null != list && list.size() >0)
            {
                jmsgPhoneInfo = list.get(0);
                EhCacheUtils.put(CacheKeys.GATEWAY_CACHE, jmsgPhoneInfo.getPhoneCityCode(), jmsgPhoneInfo);
                return jmsgPhoneInfo;
            }else
            {
            	obj = new JmsgPhoneInfo();
            }
        }
        
        return (JmsgPhoneInfo)obj;
    }
}
