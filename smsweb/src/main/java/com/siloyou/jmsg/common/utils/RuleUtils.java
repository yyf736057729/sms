package com.siloyou.jmsg.common.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.siloyou.core.common.utils.SpringContextHolder;
import com.siloyou.core.common.utils.StringUtils;
import com.siloyou.core.modules.sys.utils.UserUtils;
import com.siloyou.jmsg.modules.sms.dao.JmsgRuleGroupDao;
import com.siloyou.jmsg.modules.sms.dao.JmsgRuleInfoDao;
import com.siloyou.jmsg.modules.sms.dao.JmsgRuleRelationDao;
import com.siloyou.jmsg.modules.sms.entity.JmsgRuleGroup;
import com.siloyou.jmsg.modules.sms.entity.JmsgRuleInfo;
import com.siloyou.jmsg.modules.sms.entity.JmsgRuleRelation;

/**
 * 规则工具类
 * @author zhangjie
 */
public class RuleUtils {
	
    private static Logger logger = LoggerFactory.getLogger(RuleUtils.class);
	
	public static Map<String, JmsgRuleInfo> ruleMap = new HashMap<String, JmsgRuleInfo>();
	
	public static Set<String> urlSet = new HashSet<String>();
	public static Set<String> phoneSet = new HashSet<String>();
	public static Set<String> keySet = new HashSet<String>();
	
	private static JmsgRuleGroupDao jmsgRuleGroupDao = SpringContextHolder.getBean(JmsgRuleGroupDao.class);
	
	private static JmsgRuleInfoDao jmsgRuleInfoDao = SpringContextHolder.getBean(JmsgRuleInfoDao.class);
	
	private static JmsgRuleRelationDao jmsgRuleRelationDao = SpringContextHolder.getBean(JmsgRuleRelationDao.class);
	
	//电话（7位及以上连续数字）
	private static String numRegex = "\\d{7,}";
	private static String urlRegex = "(http://|ftp://|https://|www){0,1}[^\u4e00-\u9fa5\\s]*?\\.(com|net|cn|me|tw|fr)[^\u4e00-\u9fa5\\s]*";
	
	/**
	 * 实例化
	 */
	public static void initRule()
	{
		JmsgRuleInfo ruleInfo = new JmsgRuleInfo();
		ruleInfo.setStatus("0");
		List<JmsgRuleInfo> list = jmsgRuleInfoDao.findList(ruleInfo);
		
		for (JmsgRuleInfo jmsgRuleInfo : list)
		{
			put(jmsgRuleInfo);
		}
	}
	
	/**
	 * 获取规则分组列表
	 * @return
	 */
	public static List<JmsgRuleGroup> getRuleGroup()
	{
		JmsgRuleGroup ruleGroup = new JmsgRuleGroup();
		ruleGroup.setStatus("0");
		return jmsgRuleGroupDao.findList(ruleGroup);
	}
	
	/**
	 * 获取存储的key
	 * @param jmsgRuleInfo
	 * @return
	 */
	public static String getRuleKey(JmsgRuleInfo jmsgRuleInfo)
	{
		return "rule_" + jmsgRuleInfo.getId();
	}
	
	/**
	 * 获取存储的key
	 * @param ruleId
	 * @param ruleType
	 * @param status
	 * @return
	 */
	public static String getRuleKey(String ruleId)
	{
		return "rule_" + ruleId;
	}

	/**
	 * 存放规则
	 * @param ruleId
	 * @param ruleType
	 * @param status
	 * @param ruleContent
	 */
	public static void put(String ruleId, JmsgRuleInfo ruleInfo)
	{
		//规则分类 1：网址 2：电话 3：关键字 4：正则式
		if (StringUtils.equals(ruleInfo.getRuleType(), "1"))
		{
			urlSet.add(ruleInfo.getRuleContent());
		}
		else if (StringUtils.equals(ruleInfo.getRuleType(), "2"))
		{
			phoneSet.add(ruleInfo.getRuleContent());
		}
		else if (StringUtils.equals(ruleInfo.getRuleType(), "3"))
		{
			keySet.add(ruleInfo.getRuleContent());
		}
		else
		{
			String key = getRuleKey(ruleId);
			ruleMap.put(key, ruleInfo);
		}
	}
	
	/**
	 * 存放规则
	 * @param jmsgRuleInfo
	 */
	public static void put(JmsgRuleInfo jmsgRuleInfo)
	{
		put(jmsgRuleInfo.getId(), jmsgRuleInfo);
	}
	
	/**
	 * 删除规则
	 * @param jmsgRuleInfo
	 */
	public static void del(JmsgRuleInfo jmsgRuleInfo)
	{
		del(jmsgRuleInfo.getId(), jmsgRuleInfo);
	}
	
	/**
	 * 删除规则
	 * @param ruleId
	 * @param ruleType
	 * @param status
	 */
	public static void del(String ruleId, JmsgRuleInfo ruleInfo)
	{
		//规则分类 1：网址 2：电话 3：关键字 4：正则式
		if (StringUtils.equals(ruleInfo.getRuleType(), "1"))
		{
			urlSet.remove(ruleInfo.getRuleContent());
		}
		else if (StringUtils.equals(ruleInfo.getRuleType(), "2"))
		{
			phoneSet.remove(ruleInfo.getRuleContent());
		}
		else if (StringUtils.equals(ruleInfo.getRuleType(), "3"))
		{
			keySet.remove(ruleInfo.getRuleContent());
		}
		else
		{
			String key = getRuleKey(ruleId);
			ruleMap.remove(key);
		}
	}
	
	/**
	 * 短信内容匹配
	 * @param userId
	 * @param content
	 * @return
	 */
	public static String filtrate(String userId, String content)
	{
		String userRuleGroup = UserUtils.get(userId).getRuleGroupId();
		if(StringUtils.isBlank(userRuleGroup)) {
			return "T0000";
		}
		
//		try {
//			if (!content.equals(new String(content.getBytes("GB2312"), "GB2312"))) {
//				return "0";//短信内容中有繁体字
//			}
//		} catch (Exception e) {
//			logger.error("过滤繁体字错", e);
//		}
 		
		JmsgRuleInfo jmsgRuleInfo;
		//根据规则组获取规则列表
		JmsgRuleRelation param = new JmsgRuleRelation();
		param.setGroupId(userRuleGroup);
		List<JmsgRuleRelation> list = jmsgRuleRelationDao.findList(param);
		
		for (JmsgRuleRelation ralation : list)
		{
			if (StringUtils.isBlank(ralation.getRuleId()))
			{
				//规则分类 1：网址 2：电话 3：关键字 4：正则式
				if (StringUtils.equals(ralation.getRuleType(), "1"))
				{
					List<String> contentList = new ArrayList<String>();
					Pattern p = Pattern.compile(urlRegex);   
					Matcher matcher = p.matcher(content);  
			        while (matcher.find()) {  
			            contentList.add(matcher.group());
			        }
			        
			        for (String str : contentList)
			        {
			        	if (!urlSet.contains(str))
			        	{
			        		logger.debug("短信内容：" + content + " 未包含网址白名单，匹配字符：" + str);
			    			//jmsgRuleInfo = ruleMap.get(getRuleKey(ralation.getRuleId()));
			    			return "F0091";
			        	}
			        }
				}
				else if (StringUtils.equals(ralation.getRuleType(), "2"))
				{
					List<String> contentList = new ArrayList<String>();
					Pattern p = Pattern.compile(numRegex);   
					Matcher matcher = p.matcher(content);  
			        while (matcher.find()) {  
			            contentList.add(matcher.group());
			        }
			        
			        for (String str : contentList)
			        {
			        	if (!phoneSet.contains(str))
			        	{
			        		logger.debug("短信内容：" + content + " 未包含电话白名单，匹配字符：" + str);
			    			//jmsgRuleInfo = ruleMap.get(getRuleKey(ralation.getRuleId()));
			    			//return jmsgRuleInfo.getRuleCode();
			        		return "F0092";
			        	}
			        }
				}
			}
		}
		
		//匹配通过的正则
		for (JmsgRuleRelation ralation : list)
		{
			if (StringUtils.isNotBlank(ralation.getRuleId()))
			{
				jmsgRuleInfo = ruleMap.get(getRuleKey(ralation.getRuleId()));
				
				//是正则匹配规则，并且匹配通过
				if (StringUtils.equals(jmsgRuleInfo.getRuleType(), "4") && StringUtils.equals(jmsgRuleInfo.getIspass(), "0"))
				{
					if (content.matches(jmsgRuleInfo.getRuleContent()))
					{
						return "T0000";
					}
				}
			}
		}
		
		
		//遍历匹配规则
		for (JmsgRuleRelation ralation : list)
		{
			if (StringUtils.isNotBlank(ralation.getRuleId()))
			{
				jmsgRuleInfo = ruleMap.get(getRuleKey(ralation.getRuleId()));
				
				if (StringUtils.equals(jmsgRuleInfo.getRuleType(), "4") && StringUtils.equals(jmsgRuleInfo.getIspass(), "1"))
				{
					if (content.matches(jmsgRuleInfo.getRuleContent()) )
					{
						logger.debug("短信内容:" + content + " ,未通过内容排除正则规则:" + jmsgRuleInfo.getDescription() + ",规则ID:" + jmsgRuleInfo.getId()+"规则编码:"+jmsgRuleInfo.getRuleCode());
						return jmsgRuleInfo.getRuleCode();
					}
				}
			}
		}
		
		return "T0000";
	}
	
	public static String testRule(String smsContent,String ruleContent,String ruleType){
		//规则分类 1：网址 2：电话 3：关键字 4：正则式
		if(StringUtils.equals(ruleType, "1")){
			List<String> contentList = new ArrayList<String>();
			Pattern p = Pattern.compile(urlRegex);   
			Matcher matcher = p.matcher(smsContent);  
	        while (matcher.find()) {  
	            contentList.add(matcher.group());
	        }
	        
	        if(contentList.contains(ruleContent)){
	        	return "true";
	        }
	        return "false";
		}else if(StringUtils.equals(ruleType, "2")){
			List<String> contentList = new ArrayList<String>();
			Pattern p = Pattern.compile(numRegex);   
			Matcher matcher = p.matcher(smsContent);  
	        while (matcher.find()) {  
	            contentList.add(matcher.group());
	        }
	        if(contentList.contains(ruleContent)){
	        	return "true";
	        }
	        return "false";
		}else if(StringUtils.equals(ruleType, "4")){
			if(smsContent.matches(ruleContent)){
				return "true";
			}
			return "false";
		}
		return "false";
	}
	
	public static String testGroupRule(String smsContent,String groupId){
		StringBuffer result = new StringBuffer("短信内容:"+smsContent+"<br/>").append("测试结果：");
		StringBuffer sb = new StringBuffer();
		List<JmsgRuleInfo> list = Lists.newArrayList();
		JmsgRuleInfo param;
		if(StringUtils.isBlank(groupId)){//全部过滤
			param = new JmsgRuleInfo();
			param.setRuleType("4");
			param.setStatus("0");
			list.addAll(jmsgRuleInfoDao.findList(param));
		}else{
			param = new JmsgRuleInfo();
			param.setGroupId(groupId);
			list.addAll(jmsgRuleInfoDao.findRuleInfoByGroup(param));
		}
		
		String code = "1";
		
		if(list != null && list.size() > 0){
			for (JmsgRuleInfo jmsgRuleInfo : list) {
				if (smsContent.matches(jmsgRuleInfo.getRuleContent())){
					if("1".equals(jmsgRuleInfo.getIspass())){//不通过
						sb.append("未通过内容排除正则规则:" + jmsgRuleInfo.getDescription()+"<br/>")
						  .append("规则ID:" + jmsgRuleInfo.getId()+"<br/>规则编码:"+jmsgRuleInfo.getRuleCode());
						code = "0";
						break;
					}
				}
			}
		}else{
			code = "-1";
		}
		if("1".equals(code)){
			result.append("短信内容可以发送");
		}else{
			if("-1".equals(code)){
				result.append("过滤规则错误,分组内不包含规则");
			}else{
				result.append("短信内容不可以发送<br/>")
					  .append(sb);
			}
		}
		
		return result.toString();
	}
}
