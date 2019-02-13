package com.siloyou.jmsg.modules.mms.task;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.siloyou.core.common.utils.JedisClusterUtils;
import com.siloyou.core.common.utils.StringUtils;
import com.siloyou.jmsg.common.utils.CacheKeys;
import com.siloyou.jmsg.common.utils.GatewayGroupUtils;
import com.siloyou.jmsg.common.utils.GatewayUtils;
import com.siloyou.jmsg.common.utils.GroupUtils;
import com.siloyou.jmsg.common.utils.KeywordsUtils;
import com.siloyou.jmsg.common.utils.PhoneUtils;
import com.siloyou.jmsg.common.utils.RuleUtils;
import com.siloyou.jmsg.common.utils.SignUtils;
import com.siloyou.jmsg.modules.mms.task.impl.MmsSendExecutor;

//全局公共定时任务
@Service
@Lazy(false)
public class GlobalTask {
	
	private static Logger logger = LoggerFactory.getLogger(GlobalTask.class);
	
	//每100分钟执行一次 速率
	@Scheduled(cron = "0 0/100 * * * ?")
	public void execSpeed(){
		String key= CacheKeys.getCacheMmsSpeed;
		String speed = JedisClusterUtils.get(key);
		if(StringUtils.isNotBlank(speed)){
			MmsSendExecutor.setSpeed(1000/Long.valueOf(speed));
		}
	}
	
	//初始化网关
	@PostConstruct
	public void init(){
//		long s = System.currentTimeMillis();
//		logger.info("=====初始化通道缓存块开始=====");
//		GroupUtils.initGroup();
//		GatewayGroupUtils.initGatewayGroup();
//		
//		logger.info("=====初始化通道缓存块结束,耗时："+(e-s)/1000);
		
		long e = System.currentTimeMillis();
		logger.info("=====初始化号段开始=====");
		PhoneUtils.initPhoneType();
		PhoneUtils.initCityList();
		long e1 = System.currentTimeMillis();
		logger.info("=====初始化号段结束,耗时："+(e1-e)/1000);
		
		
//		logger.info("=====初始化关键字开始=====");
//		KeywordsUtils.initKeywords();
//		long e2 = System.currentTimeMillis();
//		logger.info("=====初始化关键字结束,耗时："+(e2-e1)/1000);
//		
//		
//		logger.info("=====初始化通道信息开始=====");
//		GatewayUtils.initGatewayInfo();
//		long e3 = System.currentTimeMillis();
//		logger.info("=====初始化通道信息结束,耗时:"+(e3-e2)/1000);
//
//		
//		logger.info("=====初始化用户通道签名开始=====");
//        SignUtils.initGatewaySign();
//        long e4 = System.currentTimeMillis();
//        logger.info("=====初始化用户通道签名结束,耗时："+(e4-e3)/1000);
//        
//        logger.info("=====初始化短信内容排除规则开始=====");
//        RuleUtils.initRule();
//        long e5 = System.currentTimeMillis();
//        logger.info("=====初始化短信内容排除规则结束,耗时："+(e5-e4)/1000);
	}
}
