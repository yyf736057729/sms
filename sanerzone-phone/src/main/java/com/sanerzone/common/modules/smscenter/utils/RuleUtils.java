package com.sanerzone.common.modules.smscenter.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sanerzone.common.modules.account.utils.AccountCacheUtils;
import com.sanerzone.common.modules.smscenter.entity.JmsgRuleInfo;
import com.sanerzone.common.modules.smscenter.entity.JmsgRuleRelation;
import com.sanerzone.common.support.utils.Encodes;
import com.sanerzone.common.support.utils.StringUtils;

/**
 * 规则工具类
 * @author zhangjie
 */
public class RuleUtils {
	public static Map<String, JmsgRuleInfo> ruleMap = new HashMap<String, JmsgRuleInfo>();
	
	public static Map<String, List<JmsgRuleRelation>> ruleGroupMap = new HashMap<String, List<JmsgRuleRelation>>();
	
	public static Set<String> urlSet = new HashSet<String>();
	public static Set<String> phoneSet = new HashSet<String>();
	public static Set<String> keySet = new HashSet<String>();
	
	//电话（7位及以上连续数字）
	private static String numRegex = "\\d{7,}";
	private static String urlRegex = "(http://|ftp://|https://|www){0,1}[^\u4e00-\u9fa5\\s]*?\\.(com|net|cn|me|cc|gov|tw|fr)[^\u4e00-\u9fa5\\s]*";
	public static void main(String[] args) {
		
		String s = "1024133055001271703149";
		System.out.println(Encodes.encodeBase62(s.getBytes()));
		
		
		
//		String content ="【交通银行】您有1张额度最高80000元信用卡（金卡），可取现！点击免费领取（http://w3z.cn/tb4IGVe）退订回T";
		String content = "【光大银行】鉴于您信用优良，特邀办理光大白金卡，凭此有机会获得10万元额度！戳 http://t.cn/RWaLBuc 申请，退订回T。";
//		String content = "【光大银行】您信用优良，邀您办理光大信用卡,自动审核,额度最高5万。戳 https://0x7.me/y6Tf 申请,退订回T";
		
		Pattern p;
		Matcher matcher;
		List<String> contentList = new ArrayList<String>();
		p = Pattern.compile(urlRegex);   
        matcher = p.matcher(content);  
        while (matcher.find()) {  
        	System.out.println("|"+matcher.group()+"|");
        }
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
	public static Map<String, String> filtrate(String userId, String content)
	{
		Map<String, String> resultMap = new HashMap<String, String>();
		resultMap.put("filCode", "");
		
		String userRuleGroup = AccountCacheUtils.getStringValue(userId, "ruleGroupId", "");
		if(StringUtils.isBlank(userRuleGroup)) {
			resultMap.put("result", "T");
			return resultMap;
		}
		
		
		JmsgRuleInfo jmsgRuleInfo;
		//根据规则组获取规则列表
		String key = getRuleGroupKey(userRuleGroup);
		List<JmsgRuleRelation> list = ruleGroupMap.get(key);
		if (null == list || list.isEmpty())
		{
			resultMap.put("result", "T");
			return resultMap;
		}
		
		
		// 过滤网址和号码白名单，短信内容中包含的网址和号码任意一个不在白名单中，则不充许发送
		Pattern p;
		Matcher matcher;
		for (JmsgRuleRelation ruleRelation : list)
		{
			//规则分类 1：网址 2：电话 3：关键字 4：正则式
			if (StringUtils.equals(ruleRelation.getRuleType(), "1"))
			{
				List<String> contentList = new ArrayList<String>();
				p = Pattern.compile(urlRegex);   
		        matcher = p.matcher(content);  
		        while (matcher.find()) {  
		            contentList.add(matcher.group());
		        }
		        
		        for (String str : contentList)
		        {
		        	if (!urlSet.contains(str))
		        	{
						resultMap.put("result", "短信内容：" + content + " 未包含网址白名单，匹配字符：" + str);
						jmsgRuleInfo = ruleMap.get(getRuleKey(ruleRelation.getRuleId()));
						resultMap.put("filCode", "F0091");
						return resultMap;
		        	}
		        }
			}
			else if (StringUtils.equals(ruleRelation.getRuleType(), "2"))
			{
				List<String> contentList = new ArrayList<String>();
				p = Pattern.compile(numRegex);   
		        matcher = p.matcher(content);  
		        while (matcher.find()) {  
		            contentList.add(matcher.group());
		        }
		        
		        for (String str : contentList)
		        {
		        	if (!phoneSet.contains(str))
		        	{
						resultMap.put("result", "短信内容：" + content + " 未包含电话白名单，匹配字符：" + str);
						jmsgRuleInfo = ruleMap.get(getRuleKey(ruleRelation.getRuleId()));
						resultMap.put("filCode", "F0092");
						return resultMap;
		        	}
		        }
			}
		}
		
		// 过滤通过规则,只要满足任一个，则充许通过
		for (JmsgRuleRelation ruleRelation : list)
		{
			if (StringUtils.equals(ruleRelation.getRuleType(), "4"))
			{
				jmsgRuleInfo = ruleMap.get(getRuleKey(ruleRelation.getRuleId()));
				
				if (StringUtils.equals(jmsgRuleInfo.getRuleType(), "4") && StringUtils.equals(jmsgRuleInfo.getIspass(), "0"))
				{
					if (content.matches(jmsgRuleInfo.getRuleContent()))
					{
						resultMap.put("result", "T");
						return resultMap;
					}
				}
			}
		}
		
		//遍历匹配规则
		for (JmsgRuleRelation ruleRelation : list)
		{
			if (StringUtils.equals(ruleRelation.getRuleType(), "4"))
			{
				jmsgRuleInfo = ruleMap.get(getRuleKey(ruleRelation.getRuleId()));
				
				if (StringUtils.equals(jmsgRuleInfo.getRuleType(), "4") && StringUtils.equals(jmsgRuleInfo.getIspass(), "1"))
				{
					if ( content.matches(jmsgRuleInfo.getRuleContent()) )
					{
						resultMap.put("result", "短信内容：" + content + " 未通过内容排除正则规则：" + jmsgRuleInfo.getDescription() + " 规则编号：" + jmsgRuleInfo.getRuleCode());
						resultMap.put("filCode", "F009"+jmsgRuleInfo.getRuleCode());
						return resultMap;
					}
				}
			}
		}
		
		resultMap.put("result", "T");
		return resultMap;
	}
	
	/**
	 * 获取存储的key
	 * @param ruleId
	 * @param ruleType
	 * @param status
	 * @return
	 */
	public static String getRuleGroupKey(String groupId)
	{
		return "ruleGroup_" + groupId;
	}
	
	/**
	 * 存放规则
	 * @param jmsgRuleInfo
	 */
	public static void put(JmsgRuleRelation ruleRelation)
	{
		String key = getRuleGroupKey(ruleRelation.getGroupId());
		List<JmsgRuleRelation> list = ruleGroupMap.get(key);
		if (null == list)
		{
			list = new ArrayList<JmsgRuleRelation>();
		}
		list.add(ruleRelation);
		ruleGroupMap.put(key, list);
	}
	
	/**
	 * 存放规则
	 * @param jmsgRuleInfo
	 */
	public static void put(String groupId, List<JmsgRuleRelation> list)
	{
		String key = getRuleGroupKey(groupId);
		
		ruleGroupMap.put(key, list);
	}
	
	/**
	 * 删除规则
	 * @param jmsgRuleInfo
	 */
	public static void del(String groupId)
	{
		String key = getRuleGroupKey(groupId);
		del(key);
	}
	
	/**
	 * 删除规则
	 * @param jmsgRuleInfo
	 */
	public static void del(JmsgRuleRelation ruleRelation)
	{
		String key = getRuleGroupKey(ruleRelation.getGroupId());
		List<JmsgRuleRelation> list = ruleGroupMap.get(key);
		
		for (Iterator<JmsgRuleRelation> it = list.iterator(); it.hasNext();) 
		{
			JmsgRuleRelation relation = it.next();
			if (StringUtils.equals(relation.getGroupId(), ruleRelation.getGroupId())
					&& StringUtils.equals(relation.getRuleId(), ruleRelation.getRuleId()))
			{
		          it.remove();
		    }
		}
		
		ruleGroupMap.put(key, list);
	}
}
