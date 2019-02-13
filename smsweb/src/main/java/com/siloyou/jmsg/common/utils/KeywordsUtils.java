package com.siloyou.jmsg.common.utils;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.siloyou.core.common.utils.SpringContextHolder;
import com.siloyou.core.common.utils.StringUtils;
import com.siloyou.jmsg.modules.sms.dao.JmsgSmsKeywordsDao;
import com.tfc.analysis.KWSeeker;
import com.tfc.analysis.entity.Keyword;

//关键字工具类
public class KeywordsUtils {
	
	//private static Set<String> set = Sets.newHashSet();
	
	private static JmsgSmsKeywordsDao jmsgSmsKeywordsDao = SpringContextHolder.getBean(JmsgSmsKeywordsDao.class);
	
	private static KWSeeker kw1 = KWSeeker.getInstance();
	
	//初始化关键字
	public static void initKeywords(){
		List<String> list = jmsgSmsKeywordsDao.findJmsgSmsKeywords();
		/*if(list != null && list.size() >0){
			set = new HashSet<String>(list);
		}*/
		
		if(list != null && list.size() >0)
		{
            for (String str : list)
            {
                kw1.addWord(new Keyword(str));
            }
        }
	}
	
	public static void put(String value){
		//set.add(value);
		kw1.addWord(new Keyword(value));
	}
	
	public static void del(String value){
		//set.remove(value);
		kw1.remove(value);
	}
	
	/*public static Set<String> keyswords(){
		return set;
	}*/
	
	//是否包换非法内容
	public static String keywords(String content){
		/*if(set.size() >0){
			for (String keys : set) {
				if(content.indexOf(keys)>=0)return keys;
			}
		}*/
		initKeywords();//初始化全局关键字
	    StringBuffer strBuf = new StringBuffer();
	    Set<String> set = kw1.findWords(content);
	    
	    for (Iterator<String> iterator = set.iterator(); iterator.hasNext();)
	    {
	        strBuf.append(iterator.next()).append(",");
	    }
	    
	    if (strBuf.length() > 1)
	    {
	        return strBuf.substring(0, strBuf.length() -1);
	    }
		
		return "";
	}
	
	//匹配用户关键字规则
	public static boolean exits(String keywords,String content){
		if(StringUtils.isBlank(keywords))return true;
		String[] keywordArray = keywords.split(",");
		for (String key : keywordArray) {
			if(content.indexOf(key) >= 0)return true;
		}
		
		return false;
	}
}
