package com.siloyou.jmsg.common.utils;

import java.util.List;

import com.siloyou.core.common.utils.EhCacheUtils;
import com.siloyou.core.common.utils.SpringContextHolder;
import com.siloyou.core.common.utils.StringUtils;
import com.siloyou.core.common.utils.ValidatorUtils;
import com.siloyou.jmsg.modules.sms.dao.JmsgPhoneTypeDao;
import com.siloyou.jmsg.modules.sms.entity.JmsgPhoneType;

/**
 * 号码运营商工具类
 * 
 * @author zhukc
 *
 */
public class PhoneTypeUtils {
	private static final String PHONE_TYPE_CACHE = "phoneTypeCache";

	private static JmsgPhoneTypeDao jmsgPhoneTypeDao = SpringContextHolder.getBean(JmsgPhoneTypeDao.class);

	/**
	 * 获取PHONE_TYPE_CACHE缓存
	 * 
	 * @param key
	 * @return
	 */
	public static String get(String key) {
		if(StringUtils.isNotBlank(key) && ValidatorUtils.isMobile(key)) {
			String value = getValue(key, 4);
			if (StringUtils.isBlank(value)) {
				value = getValue(key, 3);
			}
			return value;
		}
		return "";
	}

	/**
	 * 初始化号码运营商
	 */
	public static void initPhoneType() {
		List<JmsgPhoneType> list = jmsgPhoneTypeDao.findAllList(new JmsgPhoneType());
		for (JmsgPhoneType entity : list) {
			put(entity.getNum(), entity.getPhoneType());
		}
	}

	/**
	 * 写入PHONE_TYPE_CACHE缓存
	 * 
	 * @param key
	 * @return
	 */
	public static void put(String key, String value) {
		EhCacheUtils.put(PHONE_TYPE_CACHE, key, value);
	}

	/**
	 * 从PHONE_TYPE_CACHE缓存中移除
	 * 
	 * @param key
	 * @return
	 */
	public static void remove(String key) {
		EhCacheUtils.remove(PHONE_TYPE_CACHE, key);
	}

	public static String getValue(String key, int index) {
		return StringUtils.valueof(EhCacheUtils.get(PHONE_TYPE_CACHE, key.substring(0, index)));
	}

}
