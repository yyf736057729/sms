package com.sanerzone.common.modules.smscenter.service;

import java.util.List;

import com.sanerzone.common.modules.smscenter.dao.JmsgSmsKeywordsDao;
import com.sanerzone.common.modules.smscenter.utils.KeywordsUtils;
import com.sanerzone.common.support.utils.SpringContextHolder;

public class InitKeywords {
	
	//初始化关键字
	public void initKeywords(){
		
		List<String> list = SpringContextHolder.getBean(JmsgSmsKeywordsDao.class).findJmsgSmsKeywords();
		
		if(list != null && list.size() >0){
            for (String str : list){
                KeywordsUtils.put(str);
            }
        }
	}
	
}
