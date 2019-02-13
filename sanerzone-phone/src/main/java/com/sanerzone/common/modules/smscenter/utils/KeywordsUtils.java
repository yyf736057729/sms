package com.sanerzone.common.modules.smscenter.utils;

import java.util.Iterator;
import java.util.Set;

import com.sanerzone.common.support.utils.StringUtils;
import com.tfc.analysis.KWSeeker;
import com.tfc.analysis.entity.Keyword;

//关键字工具类
public class KeywordsUtils {
	
	private static KWSeeker kw1 = KWSeeker.getInstance();
	
	public static void put(String value){
		kw1.addWord(new Keyword(value));
	}
	
	public static void del(String value){
		kw1.remove(value);
	}
	
	
	//是否包含非法内容
	public static String keywords(String content){
	    StringBuffer strBuf = new StringBuffer();
	    Set<String> set = kw1.findWords(content);
	    
	    for (Iterator<String> iterator = set.iterator(); iterator.hasNext();){
	        strBuf.append(iterator.next()).append(",");
	    }
	    
	    if (strBuf.length() > 1){
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
