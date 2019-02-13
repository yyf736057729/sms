package com.sanerzone.common.modules.phone.service;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;
import com.sanerzone.common.modules.phone.dao.JmsgPhoneInfoDao;
import com.sanerzone.common.modules.phone.entity.JmsgPhoneInfo;
import com.sanerzone.common.modules.phone.utils.PhoneUtils;
import com.sanerzone.common.support.utils.SpringContextHolder;

public class InitPhoneSegment {
	private static JmsgPhoneInfoDao jmsgPhoneInfoDao = SpringContextHolder.getBean(JmsgPhoneInfoDao.class);
	//初始化号段
	public void initPhoneType() {
		List<JmsgPhoneInfo> list = jmsgPhoneInfoDao.findAllList(new JmsgPhoneInfo());
		for (JmsgPhoneInfo entity : list) {
			Map<String,String> value = Maps.newHashMap();
			value.put("pt", entity.getPhoneType());
			value.put("cc",entity.getPhoneCityCode());
			PhoneUtils.put(entity.getPhone(), value);
		}
	}
}
