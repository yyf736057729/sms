package com.sanerzone.common.modules;

import com.sanerzone.common.modules.phone.service.*;
import com.sanerzone.common.modules.smscenter.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sanerzone.common.modules.account.service.InitAccount;
import com.sanerzone.common.modules.phone.utils.PhoneUtils;

public class InitUtils {
		
	private Logger logger = LoggerFactory.getLogger(InitUtils.class);
	
	public void init(){
		InitAccount account = new InitAccount();
		account.initAccount();
		logger.info("用户缓存加载完成");

		InitContentManage initContentManage = new InitContentManage();
		initContentManage.initContentManage();
		logger.info("内容策略加载完成");

		InitGatewayQueue initGatewayQueue = new InitGatewayQueue();
		initGatewayQueue.initGatewayQueue();
		logger.info("通道队列缓存加载完成");

		InitJmsgSmsUserTmpl initJmsgSmsUserTmpl = new InitJmsgSmsUserTmpl();
		initJmsgSmsUserTmpl.initJmsgSmsUserTmpl();
		logger.info("用户通道模板缓存加载完成");

		InitJmsgSmsGatewayTmpl initJmsgSmsGatewayTmpl = new InitJmsgSmsGatewayTmpl();
		initJmsgSmsGatewayTmpl.InitJmsgSmsGatewayTmpl();
		logger.info("模板缓存加载完成");

//		BlacklistUtils.clearAll();
//		logger.info("清理黑名单完成");
		
		InitBlacklist blacklist = new InitBlacklist();
		blacklist.initBlacklist();
		logger.info("黑名单缓存加载完成");
		
		InitPhoneSegment phoneSegment = new InitPhoneSegment();
		phoneSegment.initPhoneType();
		logger.info("号段缓存加载完成" );
		
		InitWhitelist whitelist = new InitWhitelist();
		whitelist.initWhitelist();
		logger.info("白名单缓存加载完成");
		
		InitGatewayGroup gatewayGroup = new InitGatewayGroup();
		gatewayGroup.initGatewayGroup();
		logger.info("网关分组缓存加载完成");
		
		InitGatewayInfo gatewayInfo = new InitGatewayInfo();
		gatewayInfo.initGatewayInfo();
		logger.info("网关缓存加载完成");
		
		InitGatewaySign gatewaySign = new InitGatewaySign();
		gatewaySign.initGatewaySign();
		logger.info("签名缓存加载完成");
		
		InitGroup group = new InitGroup();
		group.initGroup();
		logger.info("分组缓存加载完成");
		
		InitKeywords keyWords = new InitKeywords();
		keyWords.initKeywords();
		logger.info("敏感词加载完成");
		
		InitRuleInfo rule = new InitRuleInfo();
		rule.initRule();
		logger.info("规则缓存加载完成");
		rule.initRuleGroup();
		logger.info("规则分组缓存加载完成");
	}
}
