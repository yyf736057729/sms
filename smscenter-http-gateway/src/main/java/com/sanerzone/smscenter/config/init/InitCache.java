package com.sanerzone.smscenter.config.init;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import com.sanerzone.smscenter.account.service.impl.AccountServiceImpl;
import com.sanerzone.smscenter.common.tools.AccountCacheHelper;

@Service
@DependsOn("springContextHelper")
public class InitCache {
		
	private Logger logger = LoggerFactory.getLogger(InitCache.class);
	
	@Autowired
	private AccountServiceImpl accountService;
	
	@PostConstruct
	public void init(){
		int size = 0;
		List<Map<String,String>> list = accountService.findAccountList();
		if(list != null && list.size() >0){
			size = list.size();
			for (Map<String, String> result : list) {
				String accId = String.valueOf(result.get("id"));
				if(StringUtils.isNotBlank(accId)){
					AccountCacheHelper.put(accId, result);
				}
			}
		}
		logger.info("用户缓存加载完成,用户个数:"+size);
	}
	
}
