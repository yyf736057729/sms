package com.sanerzone.common.modules.smscenter.service;

import java.util.List;

import com.sanerzone.common.modules.smscenter.dao.JmsgGroupDao;
import com.sanerzone.common.modules.smscenter.entity.JmsgGroup;
import com.sanerzone.common.modules.smscenter.utils.GroupUtils;
import com.sanerzone.common.support.utils.SpringContextHolder;

public class InitGroup {
	private static JmsgGroupDao jmsgGroupDao = SpringContextHolder.getBean(JmsgGroupDao.class);
	
	//初始化分组信息
	public void initGroup(){
		JmsgGroup entity = new JmsgGroup();
		List<JmsgGroup> list = jmsgGroupDao.findList(entity);
		for (JmsgGroup jmsgGroup : list) {
			GroupUtils.put(jmsgGroup);
		}
	}
}
